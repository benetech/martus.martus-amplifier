package org.martus.amplifier.search;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.martus.common.bulletin.BulletinConstants;

public interface SearchConstants
{
	// Index field ids
	final String SEARCH_AUTHOR_INDEX_FIELD = BulletinConstants.TAGAUTHOR;
	final String SEARCH_KEYWORDS_INDEX_FIELD = BulletinConstants.TAGKEYWORDS;
	final String SEARCH_TITLE_INDEX_FIELD = BulletinConstants.TAGTITLE;
	final String SEARCH_EVENT_DATE_INDEX_FIELD = BulletinConstants.TAGEVENTDATE;
	final String SEARCH_DETAILS_INDEX_FIELD = BulletinConstants.TAGPUBLICINFO;
	final String SEARCH_SUMMARY_INDEX_FIELD = BulletinConstants.TAGSUMMARY;
	final String SEARCH_LOCATION_INDEX_FIELD = BulletinConstants.TAGLOCATION;
	final String SEARCH_ENTRY_DATE_INDEX_FIELD = BulletinConstants.TAGENTRYDATE;
	final String SEARCH_LANGUAGE_INDEX_FIELD = BulletinConstants.TAGLANGUAGE;
	final String SEARCH_ORGANIZATION_INDEX_FIELD = BulletinConstants.TAGORGANIZATION;
	final String SEARCH_EVENT_START_DATE_INDEX_FIELD = "eventStartDate";
	final String SEARCH_EVENT_END_DATE_INDEX_FIELD = "eventEndDate";
	
	final String SEARCH_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
	final DateFormat SEARCH_DATE_FORMAT = new SimpleDateFormat(SEARCH_DATE_FORMAT_PATTERN);

	final String[] SEARCH_ALL_TEXT_FIELDS ={SEARCH_AUTHOR_INDEX_FIELD, SEARCH_KEYWORDS_INDEX_FIELD,
		SEARCH_TITLE_INDEX_FIELD, SEARCH_DETAILS_INDEX_FIELD, SEARCH_SUMMARY_INDEX_FIELD,
		SEARCH_LOCATION_INDEX_FIELD};
	
}
