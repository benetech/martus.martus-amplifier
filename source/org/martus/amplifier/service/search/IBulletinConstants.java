package org.martus.amplifier.service.search;

/**
 * @author Daniel Chu
 *
 * This interface holds all the shared constants and imports
 * for the Martus Amplifier searching code
 * 
 */
public interface IBulletinConstants
{

	public static final String DEFAULT_FILES_LOCATION = 
		"amplifierdata";
	public static final String DEFAULT_INDEX_LOCATION = 
		"C:\\amplifierindex";
		
	// Martus Amplifier Field Constants
	public static final String AUTHOR_FIELD = "author";
	public static final String KEYWORDS_FIELD = "keywords";
	public static final String TITLE_FIELD = "title";
	public static final String EVENT_DATE_FIELD = "event_date";
	public static final String PUBLIC_INFO_FIELD = "public_info";
	public static final String SUMMARY_FIELD = "summary";
	public static final String LOCATION_FIELD = "location";
	public static final String ENTRY_DATE_FIELD = "entry_date";

	public static final String[] BULLETIN_FIELDS = 
		{AUTHOR_FIELD, KEYWORDS_FIELD, TITLE_FIELD, EVENT_DATE_FIELD, 
			PUBLIC_INFO_FIELD, SUMMARY_FIELD, LOCATION_FIELD, ENTRY_DATE_FIELD};
}
