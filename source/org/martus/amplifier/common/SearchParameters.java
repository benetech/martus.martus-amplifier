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

package org.martus.amplifier.common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.martus.amplifier.search.SearchConstants;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletSession;
import org.martus.util.MartusFlexidate;

public class SearchParameters implements SearchResultConstants, SearchConstants
{
	public SearchParameters(AmplifierServletRequest request)
	{
		searchRequest = request;
		inputParameters = new HashMap();
		searchFields = new HashMap();

		loadFromRequest();		
	}

	public void saveSearchInSession(AmplifierServletSession session)
	{
		AdvancedSearchInfo info = new AdvancedSearchInfo(inputParameters);
		session.setAttribute("defaultAdvancedSearch", info);	
	}
	
	public Map getSearchFields()
	{
		return searchFields;
	}
	
	private void loadFromRequest()
	{
		for(int i=0; i< ADVANCED_KEYS.length; i++)
		{
			String value = getParameterValue(ADVANCED_KEYS[i]);
			if (value != null)
			{				
				inputParameters.put(ADVANCED_KEYS[i], value);				
			}
		}
		setEventDate();
		setAllWordsSearch();
		setExactPhraseSearch();
		setAnyWordSearch();
		setNormalFields();																
	}	

	private void addField(String key, Object value)
	{
		searchFields.put(key, value);
	}
	
	private void setAnyWordSearch()
	{
		FormatterForAnyWordSearch decorator = new FormatterForAnyWordSearch();
		decorator.addFormattedString(searchFields, inputParameters);
	}

	private void setExactPhraseSearch()
	{
		FormatterForExactPhraseSearch decorator = new FormatterForExactPhraseSearch();
		decorator.addFormattedString(searchFields, inputParameters);
	}

	private void setAllWordsSearch()
	{
		FormatterForAllWordsSearch decorator = new FormatterForAllWordsSearch();
		decorator.addFormattedString(searchFields, inputParameters);
	}
	
	static String insertBeforeEachWord(String sign, String queryString)
	{
		String[] words = queryString.split(" ");
		String query = "(";		

		for (int i=0;i<words.length;i++)		
			query += sign + words[i]+ " ";

		return query + ")";
	}

	
	private void setEventDate()
	{
		String startDate	= getStartDate();
		String endDate	= getEndDate();

		if (startDate != null && endDate != null)
		{			
			addField(SEARCH_EVENT_START_DATE_INDEX_FIELD, startDate);
			addField(SEARCH_EVENT_END_DATE_INDEX_FIELD, endDate);
		}	
	}

	private void setNormalFields()
	{
		addField(RESULT_FIELDS_KEY, getValue(RESULT_FIELDS_KEY));
		
		String languageString = getValue(RESULT_LANGUAGE_KEY);
		if (languageString != null && !languageString.equals(LANGUAGE_ANYLANGUAGE_LABEL))
			addField(RESULT_LANGUAGE_KEY, languageString);
			
		String entryDate = getEntryDate(getValue(RESULT_ENTRY_DATE_KEY));
		addField(SEARCH_ENTRY_DATE_INDEX_FIELD, entryDate);
		
		String sortByString = getValue(RESULT_SORTBY_KEY);
		if (sortByString != null)
			addField(RESULT_SORTBY_KEY, sortByString);	
	}

	public static String getEntryDate(String dayString)
	{
		if (dayString.equals(ENTRY_ANYTIME_TAG))
			return "1900-01-01";
			
		int days = Integer.parseInt(dayString);	
		return daysAgo(days);
	}

	public static String daysAgo(int days)
	{
		GregorianCalendar today = new GregorianCalendar();
		today.add(Calendar.DATE, -days);
		return MartusFlexidate.toStoredDateFormat(today.getTime());
	}
	
	private String getParameterValue(String param)
	{
		return searchRequest.getParameter(param);
	}
	
	public String getValue(String key)
	{
		return (String) inputParameters.get(key);
	}	
	
	public String getStartDate()
	{			
		String yearTag = RESULT_START_YEAR_KEY;
		String monthTag = RESULT_START_MONTH_KEY;
		String dayTag = RESULT_START_DAY_KEY;
		return getDateFromRequest(yearTag, monthTag, dayTag);
	}
	
	public String getEndDate()
	{	
		String yearTag = RESULT_END_YEAR_KEY;
		String monthTag = RESULT_END_MONTH_KEY;
		String dayTag = RESULT_END_DAY_KEY;
		return getDateFromRequest(yearTag, monthTag, dayTag);
	}
	
	String getDateFromRequest(String yearTag, String monthTag, String dayTag)
	{
		int year = Integer.parseInt(getValue(yearTag));
		int month = Integer.parseInt(getValue(monthTag));
		int day = Integer.parseInt(getValue(dayTag));
		Date startDate = getDate(year, month, day);
		return MartusFlexidate.toStoredDateFormat(startDate);
	}
		
	public static Date getDate(int year, int month, int day)
	{
		return new GregorianCalendar(year, month, day).getTime();
	}	
	
	public static void clearAdvancedSearch(AmplifierServletSession session)
	{
		AdvancedSearchInfo info = new AdvancedSearchInfo(getDefaultAdvancedFields());
		session.setAttribute("defaultAdvancedSearch", info);	
	}
	
	public static void clearSimpleSearch(AmplifierServletRequest request)
	{
		request.getSession().setAttribute("simpleQuery", "");
		request.getSession().setAttribute("defaultSimpleSearch", "");
	}
	
	public static HashMap getDefaultAdvancedFields()
	{
		HashMap defaultMap = new HashMap();
		defaultMap.put(SearchResultConstants.EXACTPHRASE_TAG, "");
		defaultMap.put(SearchResultConstants.ANYWORD_TAG, "");
		defaultMap.put(SearchResultConstants.THESE_WORD_TAG, "");	
		defaultMap.put(SearchResultConstants.WITHOUTWORDS_TAG, "");
		defaultMap.put(SearchResultConstants.RESULT_FIELDS_KEY, SearchResultConstants.IN_ALL_FIELDS);
		defaultMap.put(SearchResultConstants.RESULT_ENTRY_DATE_KEY, SearchResultConstants.ENTRY_ANYTIME_TAG);
		defaultMap.put(SearchResultConstants.RESULT_LANGUAGE_KEY, SearchResultConstants.LANGUAGE_ANYLANGUAGE_LABEL);
		defaultMap.put(SearchResultConstants.RESULT_SORTBY_KEY, SearchResultConstants.SORT_BY_TITLE_TAG);
		
		defaultMap.put(SearchResultConstants.RESULT_END_DAY_KEY, Today.getDayString());
		defaultMap.put(SearchResultConstants.RESULT_END_MONTH_KEY, Today.getMonth());
		defaultMap.put(SearchResultConstants.RESULT_END_YEAR_KEY, Today.getYearString());
				
		return defaultMap;	
	}

	abstract static class LuceneQueryFormatter
	{
		public LuceneQueryFormatter(String tagToUse)
		{
			tag = tagToUse;
		}
		
		public void addFormattedString(Map destination, Map source)
		{
			String rawString = (String)source.get(tag);
			rawString = CharacterUtil.removeRestrictCharacters(rawString);
			String decoratedString = "";
			if(rawString.length() > 0)
				decoratedString = getFormattedString(rawString);
			destination.put(tag, decoratedString);
		}
		
		abstract String getFormattedString(String rawString);
		
		String tag;
	}
	
	public static class FormatterForAnyWordSearch extends LuceneQueryFormatter
	{
		public FormatterForAnyWordSearch()
		{
			super(SearchResultConstants.ANYWORD_TAG);
		}
		
		public String getFormattedString(String rawString)
		{
			return "(" + rawString + ")";
		}
	}
	
	public static class FormatterForExactPhraseSearch extends LuceneQueryFormatter
	{
		public FormatterForExactPhraseSearch()
		{
			super(SearchResultConstants.EXACTPHRASE_TAG);
		}
		
		public String getFormattedString(String rawString)
		{
			return "\"" + rawString + "\"";
		}
	}
	
	public static class FormatterForAllWordsSearch extends LuceneQueryFormatter
	{
		public FormatterForAllWordsSearch()
		{
			super(SearchResultConstants.THESE_WORD_TAG);
		}
		
		public String getFormattedString(String rawString)
		{
			return insertBeforeEachWord(PLUS, rawString);
		}
	}
	
	AmplifierServletRequest searchRequest;
	HashMap inputParameters;
	HashMap	searchFields;
	final static String PLUS 	= "+";
	final static String NOT 	= "-";	
}
