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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.amplifier.common.AmplifierLocalization;
import org.martus.amplifier.common.CharacterUtil;
import org.martus.amplifier.common.SearchParameters;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.lucene.LuceneBulletinSearcher;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.search.BulletinSearcher;
import org.martus.amplifier.search.Results;
import org.martus.amplifier.search.SearchConstants;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletSession;


public class DoSearch extends AbstractSearchResultsServlet
{
	public void configureSessionFromRequest(AmplifierServletRequest request)
	{
		AmplifierServletSession session = request.getSession();
		
		String basicQueryString = request.getParameter(SearchResultConstants.RESULT_BASIC_QUERY_KEY);
		String searchedForString = (String)session.getAttribute("searchedFor");
		String searchType = request.getParameter("typeOfSearch");
		
		if(basicQueryString != null)
		{
			searchedForString = basicQueryString;
		}
		else
		{
			searchedForString = "Advanced Search";
			basicQueryString = "";
		}
		
		session.setAttribute("searchedFor", searchedForString);
		session.setAttribute("defaultSimpleSearch", basicQueryString);
		session.setAttribute("simpleQuery", basicQueryString);
		session.setAttribute("typeOfSearch", searchType);
	}

	List getBulletinsToDisplay(AmplifierServletRequest request)
		throws Exception
	{
		return  getSearchResults(request);
	}

	public List getSearchResults(AmplifierServletRequest request)
		throws Exception
	{
		String simpleQueryString = request.getParameter(SearchResultConstants.RESULT_BASIC_QUERY_KEY);			
	 
		if (simpleQueryString != null)
		{
			simpleQueryString = CharacterUtil.removeRestrictCharacters(simpleQueryString);

			if (simpleQueryString.equals(""))
				return new ArrayList();
							
			return getSimpleSearchResults(request.getSession(), simpleQueryString);
		}

		return getComplexSearchResults(request);
	}

	private List getComplexSearchResults(AmplifierServletRequest request)
		throws Exception
	{
		SearchParameters sp = new SearchParameters(request);
		sp.saveSearchInSession(request.getSession());
		SearchParameters.clearSimpleSearch(request);								
		Map fields = sp.getSearchFields();
		return getResults(fields);
	}

	private List getSimpleSearchResults(
		AmplifierServletSession session,
		String simpleQueryString)
		throws Exception
	{
		if (simpleQueryString.equals(""))
			return new ArrayList();
		Map fields = new HashMap();				
		fields.put(SearchResultConstants.RESULT_BASIC_QUERY_KEY, simpleQueryString);
		SearchParameters.clearAdvancedSearch(session);			
		return getResults(fields);
	}	
		
	BulletinSearcher openBulletinSearcher() throws Exception
	{
		AmplifierConfiguration config = AmplifierConfiguration.getInstance();
		String indexPath = config.getBasePath();

		return new LuceneBulletinSearcher(indexPath);
	}
	
	public List getResults(Map fields) throws Exception
	{
		BulletinSearcher searcher = openBulletinSearcher();
		ArrayList list = new ArrayList();
		
		try
		{
			Results results = searcher.search(fields);

			int numResults = results.getCount();
			for (int i = 0; i < numResults; i++)
			{
				BulletinInfo bulletinInfo = results.getBulletinInfo(i);
				convertLanguageCode(bulletinInfo);
				formatDataForHtmlDisplay(bulletinInfo.getFields());
				list.add(bulletinInfo);
			}
		}
		finally
		{
			searcher.close();
		}
		return list;
	}
	
	public void convertLanguageCode(BulletinInfo bulletinInfo)
	{
		String code = bulletinInfo.get(SearchConstants.SEARCH_LANGUAGE_INDEX_FIELD);
		if(code == null)
			return;
		String languageString = AmplifierLocalization.getLanguageString(code);
		if(languageString == null)
			return;				
		bulletinInfo.set(SearchConstants.SEARCH_LANGUAGE_INDEX_FIELD, languageString);
	}
}