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

	public static final String WORKSPACE_ROOT = 
		"C:\\Development\\eclipse\\workspace\\";
	public static final String DEFAULT_FILES_LOCATION = 
		WORKSPACE_ROOT + "martus-amplifier\\amplifierdata";
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
}
