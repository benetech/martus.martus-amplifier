
package org.martus.amplifier.presentation;

import java.util.Vector;

import org.martus.amplifier.service.search.SearchConstants;


public class FindBulletinsFields implements SearchConstants, SearchResultConstants
{
	public static Vector getFindWordFilterDisplayNames()
	{
		return loadWordFilterList();			
	}

	private static Vector loadWordFilterList()
	{
		Vector filters = new Vector();
		filters.add(new ChoiceEntry(THESE_WORD_KEY, THESE_WORD_LABEL));
		filters.add(new ChoiceEntry(EXACTPHRASE_KEY, EXACTPHRASE_LABEL));
		filters.add(new ChoiceEntry(ANYWORD_KEY, ANYWORD_LABEL));
		filters.add(new ChoiceEntry(WITHOUTWORDS_KEY, WITHOUTWORDS_LABEL));

		return filters;
	}
	
	public static Vector getFindEntryDatesDisplayNames()
	{
		return loadEntryDateList();			
	}
	
	private static Vector loadEntryDateList()
	{
		Vector dates = new Vector();
		dates.add(new ChoiceEntry(ENTRY_ANYTIME_KEY, ENTRY_ANYTIME_LABEL));
		dates.add(new ChoiceEntry(ENTRY_PAST_WEEK_KEY, ENTRY_PAST_WEEK_DAYS_LABEL));
		dates.add(new ChoiceEntry(ENTRY_PAST_MONTH_KEY, ENTRY_PAST_MONTH_DAYS_LABEL));
		dates.add(new ChoiceEntry(ENTRY_PAST_3_MONTH_KEY,ENTRY_PAST_3_MONTH_DAYS_LABEL ));
		dates.add(new ChoiceEntry(ENTRY_PAST_6_MONTH_KEY, ENTRY_PAST_6_MONTH_DAYS_LABEL));
		dates.add(new ChoiceEntry(ENTYR_PAST_YEAR_KEY, ENTRY_PAST_YEAR_DAYS_LABEL));
		
		return dates;
	}
	
	public static Vector getBulletinFieldDisplayNames()
	{
		Vector fields = new Vector();
		fields.add(new ChoiceEntry(ANYWHERE_IN_BULLETIN_KEY, IN_ALL_FIELDS));
		fields.add(new ChoiceEntry(IN_TITLE_KEY, SEARCH_TITLE_INDEX_FIELD));
		fields.add(new ChoiceEntry(IN_KEYWORDS_KEY, SEARCH_KEYWORDS_INDEX_FIELD));
		fields.add(new ChoiceEntry(IN_SUMMARY_KEY,SEARCH_SUMMARY_INDEX_FIELD ));
		fields.add(new ChoiceEntry(IN_AUTHOR_KEY, SEARCH_AUTHOR_INDEX_FIELD));
		fields.add(new ChoiceEntry(IN_DETAIL_KEY, SEARCH_DETAILS_INDEX_FIELD ));
		fields.add(new ChoiceEntry(IN_LOCATION_KEY, SEARCH_LOCATION_INDEX_FIELD ));
		
		return fields;
	}
	
	
	public static String getFilterWordValue(String word)
	{
		return null;
	}
	
	public static String getBulletineFieldValue(String phrase)
	{		
		return null;
	}

	public static class ChoiceEntry
	{
		public String getTag()	{return tagString;}
		public String getLabel() {return labelString;}

		public ChoiceEntry(String label, String tag)
		{
			labelString = label;
			tagString 	= tag;
		}		

		private String tagString;
		private String labelString;
	}
}
