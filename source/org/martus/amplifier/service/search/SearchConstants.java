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
	String PUBLIC_INFO_INDEX_FIELD = "publicInfo";
	String SUMMARY_INDEX_FIELD = "summary";
	String LOCATION_INDEX_FIELD = "location";
	String ENTRY_DATE_INDEX_FIELD = "entryDate";
	
	DateFormat DATE_FORMAT = 
		new SimpleDateFormat("yyyy-MM-dd");
	
}
