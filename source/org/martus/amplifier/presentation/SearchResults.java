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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.velocity.context.Context;
import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.amplifier.common.SearchParameters;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.lucene.LuceneBulletinSearcher;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.search.BulletinSearcher;
import org.martus.amplifier.velocity.AmplifierServlet;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletResponse;


public class SearchResults extends AmplifierServlet implements SearchResultConstants
{	
	public String selectTemplate(AmplifierServletRequest request,
			AmplifierServletResponse response, Context context) 
					throws Exception
	{
		String templateName = "NoSearchResults.vm";

		List results = null;
		int resultCount = 0;
		try
		{
			results = getSearchResults(request);
			resultCount = results.size();	
		}
		catch (Exception e)
		{
//			e.printStackTrace();
		}

		if(resultCount > 0)
		{
			templateName ="SearchResults.vm";
			Vector bulletins = new Vector();
			for (Iterator iter = results.iterator(); iter.hasNext();)
			{
				BulletinInfo element = (BulletinInfo) iter.next();
				bulletins.add(element);
			}
			context.put("foundBulletins", bulletins);
			request.getSession().setAttribute("foundBulletins", bulletins);
		}
		return templateName;
	}

	public List getSearchResults(AmplifierServletRequest request)
		throws Exception, BulletinIndexException
	{
		HashMap searcher = new HashMap();				
		String queryString = request.getParameter(RESULT_BASIC_QUERY_KEY);
	 
		if (queryString != null)
		{
			searcher.put(RESULT_BASIC_QUERY_KEY, queryString);
			return getResults(searcher);
		}

		queryString = request.getParameter(RESULT_ADVANCED_QUERY_KEY);
		if (queryString != null)		
			searcher.put(RESULT_ADVANCED_QUERY_KEY, queryString);
				
		new SearchParameters(request, searcher);									
										
		return getResults(searcher);
	}
	
	BulletinSearcher openBulletinSearcher() throws BulletinIndexException
	{
		AmplifierConfiguration config = AmplifierConfiguration.getInstance();
		String indexPath = config.getBasePath();

		return new LuceneBulletinSearcher(indexPath);
	}
	
	public List getResults(HashMap fields) throws BulletinIndexException
	{
		BulletinSearcher searcher = openBulletinSearcher();
		ArrayList list = new ArrayList();
		
		try {
			BulletinSearcher.Results results;			
		
			String field = (String)fields.get(RESULT_BASIC_FIELD_KEY);			
			results = searcher.search(field, fields);
						
			int numResults = results.getCount();
			for (int i = 0; i < numResults; i++)				
				list.add(results.getBulletinInfo(i));
			
			String sortField = (String) fields.get(RESULT_SORTBY_KEY);
			if (sortField != null)
				sortBulletins(list, sortField);					
	
		} finally {
			searcher.close();
		}
		return list;	
	}
	
	public static void sortBulletins(List list, final String field)
	{
		Collections.sort(list, new Comparator()
		{
		  public int compare(Object o1, Object o2)
		  {
			String string1 = ((BulletinInfo)o1).get(field);
			String string2 = ((BulletinInfo)o2).get(field);	  			  		
			return ((Comparable)string1.toLowerCase()).compareTo(string2.toLowerCase());
		   }
		});
	
	}	
}
