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
import org.martus.amplifier.velocity.AmplifierServletResponse;


public class SearchResults extends AmplifierServlet implements SearchResultConstants
{	
	public String selectTemplate(AmplifierServletRequest request,
			AmplifierServletResponse response, Context context) 
					throws Exception
	{
		setSearchedFor(request, context);

		String sortField = request.getParameter(RESULT_SORTBY_KEY);
		Vector bulletins = (Vector)request.getSession().getAttribute("foundBulletins");
		sortBulletins(bulletins, sortField);

		setReturnContext(request, bulletins, context);
		return "SearchResults.vm";				
	}
	

	static public void setReturnContext(AmplifierServletRequest request, Vector bulletins, Context context)
	{
		request.getSession().setAttribute("foundBulletins", bulletins);
		context.put("foundBulletins", bulletins);
		context.put("totalBulletins", new Integer(bulletins.size()));
		Vector sortByFields = FindBulletinsFields.getSortByFieldDisplayNames();
		context.put("sortByFields", sortByFields);
		context.put("currentlySortingBy", request.getParameter(RESULT_SORTBY_KEY));
	}


	static public void setSearchedFor(AmplifierServletRequest request, Context context)
	{
		String basicQueryString = request.getParameter(RESULT_BASIC_QUERY_KEY);
		String advanceQueryString = request.getParameter(RESULT_ADVANCED_QUERY_KEY);
		if(basicQueryString != null)
		{
			context.put("searchedFor", basicQueryString);
			request.getSession().setAttribute("searchedFor", basicQueryString);
		}
		else if (advanceQueryString != null)
		{
			context.put("searchedFor", advanceQueryString);
			request.getSession().setAttribute("searchedFor", advanceQueryString);
		}
		else		
		{
			String searchedForString = (String)request.getSession().getAttribute("searchedFor");
			context.put("searchedFor", searchedForString);
			request.getSession().setAttribute("searchedFor", searchedForString);
		}
	}


	public static void sortBulletins(List list, final String field)
	{
		Collections.sort(list, new BulletinSorter(field));
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

}
