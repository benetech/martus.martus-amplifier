package org.martus.amplifier.service.search;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface SearchConstants
{
	// Index field ids
	String AUTHOR_INDEX_FIELD = "author";
	String KEYWORDS_INDEX_FIELD = "keywords";
	String TITLE_INDEX_FIELD = "title";
	String EVENT_DATE_INDEX_FIELD = "eventDate";
	String DETAILS_INDEX_FIELD = "publicInfo";
	String SUMMARY_INDEX_FIELD = "summary";
	String LOCATION_INDEX_FIELD = "location";
	String ENTRY_DATE_INDEX_FIELD = "entryDate";
	String EVENT_START_DATE_INDEX_FIELD = "eventStartDate";
	String EVENT_END_DATE_INDEX_FIELD = "eventEndDate";
	
	String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
	DateFormat DATE_FORMAT = 
		new SimpleDateFormat(DATE_FORMAT_PATTERN);
	
}
