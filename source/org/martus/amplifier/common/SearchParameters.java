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
import org.martus.common.utilities.MartusFlexidate;

public class SearchParameters implements SearchResultConstants, SearchConstants
{
	public SearchParameters(RawSearchParameters rawParameters)
	{
		inputParameters = rawParameters;

		searchFields = new HashMap();

		copyFormattedQueryString(new FormatterForAllWordsSearch());
		copyFormattedQueryString(new FormatterForExactPhraseSearch());
		copyFormattedQueryString(new FormatterForAnyWordSearch());
		copyLanguageChoice();
		copyFieldsChoice();
		copyEntryDateChoice();
		copyEventDateChoice();
		copySortByChoice();		
	}

	public Map getSearchFields()
	{
		return searchFields;
	}
	
	private void copyFormattedQueryString(LuceneQueryFormatter formatter)
	{
		String decoratedString = inputParameters.getFormattedString(formatter);
		searchFields.put(formatter.getTag(), decoratedString);
	}
	
	private void copyLanguageChoice()
	{
		String languageString = inputParameters.getLanguage();
		if(languageString == null)
			return;
		if(languageString.equals(LANGUAGE_ANYLANGUAGE_LABEL))
			return;

		String key = RESULT_LANGUAGE_KEY;
		searchFields.put(key, languageString);
	}

	private void copyFieldsChoice()
	{
		searchFields.put(RESULT_FIELDS_KEY, inputParameters.getFieldToSearchIn());
	}

	private void copyEntryDateChoice()
	{
		String entryDateDayCount = inputParameters.getEntryDate();
		String entryDate = getEntryDate(entryDateDayCount);
		searchFields.put(SEARCH_ENTRY_DATE_INDEX_FIELD, entryDate);
	}

	private void copyEventDateChoice()
	{
		String startDate = inputParameters.getStartDate();
		String endDate = inputParameters.getEndDate();

		if (startDate != null && endDate != null)
		{
			String keyStart = SEARCH_EVENT_START_DATE_INDEX_FIELD;			
			searchFields.put(keyStart, startDate);
			String keyEnd = SEARCH_EVENT_END_DATE_INDEX_FIELD;
			searchFields.put(keyEnd, endDate);
		}	
	}

	private void copySortByChoice()
	{
		String key = RESULT_SORTBY_KEY;
		String sortByString = inputParameters.getSortBy();
		if (sortByString != null)
			searchFields.put(key, sortByString);
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
		GregorianCalendar today = null;
		if(todaysDateUsedForTesting == null)
			today = new GregorianCalendar();
		else
			today = (GregorianCalendar) todaysDateUsedForTesting.clone();
		
		today.add(Calendar.DATE, -days);
		return MartusFlexidate.toStoredDateFormat(today.getTime());
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
			return "\"" + rawString.replace('\"',' ').trim() + "\"";
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
	public static GregorianCalendar todaysDateUsedForTesting = null;
}
