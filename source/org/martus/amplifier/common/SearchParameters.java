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
import org.martus.util.MartusFlexidate;

public class SearchParameters implements SearchResultConstants, SearchConstants
{
	public SearchParameters(RawSearchParameters rawParameters)
	{
		inputParameters = rawParameters;

		searchFields = new HashMap();
		setEventDate();
		setNormalFields();		
		copyFormattedQueryString(new FormatterForAllWordsSearch());
		copyFormattedQueryString(new FormatterForExactPhraseSearch());
		copyFormattedQueryString(new FormatterForAnyWordSearch());
	}

	public Map getSearchFields()
	{
		return searchFields;
	}
	
	private void addField(String key, Object value)
	{
		searchFields.put(key, value);
	}
	
	private void copyFormattedQueryString(LuceneQueryFormatter formatter)
	{
		String decoratedString = inputParameters.getFormattedString(formatter);
		searchFields.put(formatter.getTag(), decoratedString);
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
	
	abstract static class LuceneQueryFormatter
	{
		public LuceneQueryFormatter(String tagToUse)
		{
			tag = tagToUse;
		}
		
		public String getTag()
		{
			return tag;
		}
		
		public void addFormattedString(Map destination, Map source)
		{
			String decoratedString = getFormattedString(source);
			destination.put(tag, decoratedString);
		}

		public String getFormattedString(Map source)
		{
			String rawString = (String)source.get(tag);
			rawString = CharacterUtil.removeRestrictCharacters(rawString);
			String decoratedString = "";
			if(rawString.length() > 0)
				decoratedString = getFormattedString(rawString);
			return decoratedString;
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
			String[] words = rawString.split(" ");
			String query = "(";		
			
			for (int i=0;i<words.length;i++)		
				query += "+" + words[i]+ " ";
			
			return query + ")";
		}
	}
	
	RawSearchParameters inputParameters;
	HashMap	searchFields;
}
