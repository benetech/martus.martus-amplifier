/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/

package org.martus.amplifier.presentation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.apache.velocity.context.Context;
import org.martus.amplifier.common.FindBulletinsFields;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.search.SearchConstants;
import org.martus.amplifier.velocity.AmplifierServlet;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletSession;

public abstract class AbstractSearchResultsServlet extends AmplifierServlet
{

	public static void setSortByInSession(String sortField, AmplifierServletRequest request)
	{
		AmplifierServletSession session = request.getSession();
		session.setAttribute(SearchResultConstants.RESULT_SORTBY_KEY, sortField);
	}

	public static void setSearchResultsContext(Vector bulletins, AmplifierServletRequest request, Context context)
	{
		request.getSession().setAttribute("foundBulletins", bulletins);
		context.put("foundBulletins", bulletins);
		context.put("totalBulletins", new Integer(bulletins.size()));
		Vector sortByFields = FindBulletinsFields.getSortByFieldDisplayNames();
		context.put("sortByFields", sortByFields);
		String sortBy = request.getParameter(SearchResultConstants.RESULT_SORTBY_KEY);
		context.put("currentlySortingBy", sortBy);
	}

	public static void setSearchedForInContextAndSession(AmplifierServletRequest request, Context context)
	{
		String basicQueryString = request.getParameter(SearchResultConstants.RESULT_BASIC_QUERY_KEY);
		String searchedForString = (String)request.getSession().getAttribute("searchedFor");
		
		if(basicQueryString != null)
		{
			searchedForString = basicQueryString;
		}
		else
		{
			searchedForString = "Advanced Search";
			basicQueryString = "";
		}

		request.getSession().setAttribute("searchedFor", searchedForString);
		request.getSession().setAttribute("defaultSimpleSearch", basicQueryString);

		context.put("searchedFor", searchedForString);
		context.put("defaultSimpleSearch", basicQueryString);
	}

	public static void sortBulletins(List bulletinList, final String sortByFieldTag)
	{
		Collections.sort(bulletinList, new BulletinSorter(sortByFieldTag));
	}

	static class BulletinSorter implements Comparator
	{
		private final String field;
		BulletinSorter(String field)
		{
			super();
			if(field.equals(SearchConstants.SEARCH_EVENT_DATE_INDEX_FIELD))
				this.field = SearchConstants.SEARCH_EVENT_DATE_INDEX_FIELD +"-start";
			else
				this.field = field;
		}
		public int compare(Object o1, Object o2)
		{
			String string1 = ((BulletinInfo)o1).get(field);
			String string2 = ((BulletinInfo)o2).get(field);	  			  		
			return ((Comparable)string1.toLowerCase()).compareTo(string2.toLowerCase());
		}
	}

	protected static String getFieldToSortBy(AmplifierServletRequest request)
	{
		AmplifierServletSession session = request.getSession();
		String sortField = request.getParameter(SearchResultConstants.RESULT_SORTBY_KEY);
		if (sortField == null)
			sortField = (String)session.getAttribute(SearchResultConstants.RESULT_SORTBY_KEY);
		if(sortField == null)
			sortField = SearchResultConstants.SORT_BY_TITLE_TAG;
		return sortField;
	}

}
