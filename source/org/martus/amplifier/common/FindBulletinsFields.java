
package org.martus.amplifier.common;

import java.util.Vector;

import org.martus.amplifier.search.SearchConstants;


public class FindBulletinsFields implements SearchConstants, SearchResultConstants
{
	public static Vector getFindWordFilterDisplayNames()
	{
		Vector filters = new Vector();
		
		filters.add(new ChoiceEntry(ANYWORD_LABEL, ANYWORD_KEY));
		filters.add(new ChoiceEntry(THESE_WORD_LABEL, THESE_WORD_KEY));
		filters.add(new ChoiceEntry(EXACTPHRASE_LABEL, EXACTPHRASE_KEY));		
//		filters.add(new ChoiceEntry(WITHOUTWORDS_LABEL, WITHOUTWORDS_KEY));

		return filters;	
	}
	
	public static Vector getFindEntryDatesDisplayNames()
	{
		Vector dates = new Vector();
		dates.add(new ChoiceEntry(ENTRY_ANYTIME_LABEL, ENTRY_ANYTIME_KEY));
		dates.add(new ChoiceEntry(ENTRY_PAST_WEEK_DAYS_LABEL, ENTRY_PAST_WEEK_KEY));
		dates.add(new ChoiceEntry(ENTRY_PAST_MONTH_DAYS_LABEL, ENTRY_PAST_MONTH_KEY));
		dates.add(new ChoiceEntry(ENTRY_PAST_3_MONTH_DAYS_LABEL,ENTRY_PAST_3_MONTH_KEY ));
		dates.add(new ChoiceEntry(ENTRY_PAST_6_MONTH_DAYS_LABEL, ENTRY_PAST_6_MONTH_KEY));
		dates.add(new ChoiceEntry(ENTRY_PAST_YEAR_DAYS_LABEL, ENTYR_PAST_YEAR_KEY));
		
		return dates;		
	}
	
	public static Vector getBulletinFieldDisplayNames()
	{
		Vector fields = new Vector();
		fields.add(new ChoiceEntry(IN_ALL_FIELDS, ANYWHERE_IN_BULLETIN_KEY));
		fields.add(new ChoiceEntry(SEARCH_TITLE_INDEX_FIELD, IN_TITLE_KEY));
		fields.add(new ChoiceEntry(SEARCH_KEYWORDS_INDEX_FIELD, IN_KEYWORDS_KEY));
		fields.add(new ChoiceEntry(SEARCH_SUMMARY_INDEX_FIELD,IN_SUMMARY_KEY ));
		fields.add(new ChoiceEntry(SEARCH_AUTHOR_INDEX_FIELD, IN_AUTHOR_KEY));
		fields.add(new ChoiceEntry(SEARCH_DETAILS_INDEX_FIELD, IN_DETAIL_KEY ));
		fields.add(new ChoiceEntry(SEARCH_LOCATION_INDEX_FIELD, IN_LOCATION_KEY ));
		fields.add(new ChoiceEntry(SEARCH_ORGANIZATION_INDEX_FIELD, IN_ORGANIZATION_KEY));
		
		return fields;
	}
	
	public static Vector getLanguageFieldDisplayNames()
	{
		Vector fields = new Vector();
		fields.add(new ChoiceEntry(LANGUAGE_ANYLANGUAGE_KEY, LANGUAGE_ANYLANGUAGE_KEY));
		fields.add(new ChoiceEntry("en", LANGUAGE_ENGLISH_KEY));
		fields.add(new ChoiceEntry("fr", LANGUAGE_FRENCH_KEY));
		fields.add(new ChoiceEntry("de", LANGUAGE_GERMAN_KEY));
		fields.add(new ChoiceEntry("id", LANGUAGE_INDONESIAN_KEY ));
		fields.add(new ChoiceEntry("ru", LANGUAGE_RUSSIAN_KEY));
		fields.add(new ChoiceEntry("es", LANGUAGE_SPANISH_KEY ));		
		
		return fields;
	}
	
	public static Vector getSortByFieldDisplayNames()
	{
		Vector fields = new Vector();
		
		fields.add(new ChoiceEntry(SEARCH_TITLE_INDEX_FIELD, SORT_BY_TITLE_LABEL));
		fields.add(new ChoiceEntry(SEARCH_AUTHOR_INDEX_FIELD, SORT_BY_AUTHOR_LABEL));
		fields.add(new ChoiceEntry(SEARCH_EVENT_DATE_INDEX_FIELD, SORT_BY_EVENTDATE_LABEL ));
		fields.add(new ChoiceEntry(SEARCH_LOCATION_INDEX_FIELD, SORT_BY_LOCATION_LABEL ));
		fields.add(new ChoiceEntry(SEARCH_ORGANIZATION_INDEX_FIELD, SORT_BY_ORGANIZATION_LABEL));
		
		return fields;
	}
	
	public static Vector getMonthFieldDisplayNames()
	{
		Vector fields = new Vector();
		for (int i=0;i< MONTH_NAMES.length;i++)	
			fields.add(new ChoiceEntry(new Integer(i).toString(), MONTH_NAMES[i]));
					
		return fields;
	}
	
	public static Object getToday()
	{
		return new Today();
	}
	
	private static final String[] MONTH_NAMES = new String[] {
		"January", "February", "March", "April", "May", "June",
		"July", "August", "September", "October", "November", "December"
	};
	
}
