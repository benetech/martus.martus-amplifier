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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.martus.amplifier.presentation.search.SearchBean;
import org.martus.amplifier.service.search.BulletinInfo;


public class SearchResults extends AmplifierServlet
{
	public Date getDate(int year, int month, int day)
	{
		return new GregorianCalendar(year, month, day).getTime();
	}	
	
	private void handleAdvancedSearchParams(HttpServletRequest request, SearchBean searcher) 
			throws Exception
	{
		
		String startMonthString = request.getParameter("startMonth");		
		String startDayString   = request.getParameter("startDay");		
		String startYearString  = request.getParameter("startYear");
		
		String endMonthString   = request.getParameter("endMonth");
		String endDayString     = request.getParameter("endDay");
		String endYearString    = request.getParameter("endYear");

		if (startMonthString == null || 
			endMonthString == null ||
			startDayString == null ||
			endDayString == null ||
			startYearString == null ||
			endYearString == null)
			return;
		
		Date startDate = getDate(Integer.parseInt(startYearString),
								 Integer.parseInt(startMonthString), 
								 Integer.parseInt(startDayString));
		Date endDate = getDate(Integer.parseInt(endYearString),
							   Integer.parseInt(endMonthString),
							   Integer.parseInt(endDayString));

		searcher.setStartDate(startDate);
		searcher.setEndDate(endDate);	
	}
	
	public String selectTemplate( HttpServletRequest request,
					HttpServletResponse response, Context ctx ) 
					throws Exception
	{

		SearchBean searcher = new SearchBean();
		String queryString = request.getParameter("query");
		searcher.setQuery(queryString);
//		String fieldString = request.getParameter("field");
searcher.setField("author");

		handleAdvancedSearchParams(request, searcher);	

		SearchBean.SearchResultsBean results = searcher.getResults();
		int resultCount = results.size();

		String templateName = "NoSearchResults.vm";
		if(resultCount > 0)
		{
			templateName ="SearchResults.vm";
			Vector bulletins = new Vector();
			for (Iterator iter = results.iterator(); iter.hasNext();)
			{
				BulletinInfo element = (BulletinInfo) iter.next();
				bulletins.add(element);
			}
			ctx.put("foundBulletins", bulletins);
		}
		return templateName;
	}
}
