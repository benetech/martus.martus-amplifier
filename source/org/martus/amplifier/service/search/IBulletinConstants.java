package org.martus.amplifier.service.search;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;

/**
 * @author Daniel Chu
 *
 * This interface holds all the shared constants and imports
 * for the Martus Amplifier searching code
 * 
 */
public interface IBulletinConstants
{
	public static final String FILES_FOLDER = "Bulletin_Folder";
	public static final String DEFAULT_FILES_LOCATION = 
		AmplifierConfiguration.getInstance().buildAmplifierWorkingPath(FILES_FOLDER);
	public static final String INDEX_FOLDER = "amplifier_index";
	public static final String DEFAULT_INDEX_LOCATION = 
		AmplifierConfiguration.getInstance().buildAmplifierWorkingPath(INDEX_FOLDER);

	// Martus Amplifier Field Constants
	/** [Brian says: In a way, all the "field" names and "visible field"
	/* name are just two attributes of a set of "field" instances.
	 * We might want to think about making these fields into first-class
	 * objects someday.  That would make it easier/cleaner if we wanted
	 * to add new attributes, like an attribute to indicate whether the
	 * field should be included on our JSP search page.]
	 */
	public static final String AUTHOR_FIELD = "author";
	public static final String VISIBLE_AUTHOR_FIELD = "Author";
	public static final String KEYWORDS_FIELD = "keywords";
	public static final String VISIBLE_KEYWORDS_FIELD = "Keywords";
	public static final String TITLE_FIELD = "title";
	public static final String VISIBLE_TITLE_FIELD = "Title";
	public static final String EVENT_DATE_FIELD = "event_date";
	public static final String VISIBLE_EVENT_DATE_FIELD = "Event Date";
	public static final String PUBLIC_INFO_FIELD = "public_info";
	public static final String VISIBLE_PUBLIC_INFO_FIELD = "Public Info";
	public static final String SUMMARY_FIELD = "summary";
	public static final String VISIBLE_SUMMARY_FIELD = "Summary";
	public static final String LOCATION_FIELD = "location";
	public static final String VISIBLE_LOCATION_FIELD = "Location";
	public static final String ENTRY_DATE_FIELD = "entry_date";
	public static final String VISIBLE_ENTRY_DATE_FIELD = "Entry Date";	
	public static final String UNIVERSAL_ID_FIELD = "universal_id";
	
	// The list of fields that the SearchQueryBean knows about
	public static final String[] BULLETIN_FIELDS = 
		{AUTHOR_FIELD, KEYWORDS_FIELD, TITLE_FIELD, EVENT_DATE_FIELD, 
			PUBLIC_INFO_FIELD, SUMMARY_FIELD, LOCATION_FIELD, ENTRY_DATE_FIELD, UNIVERSAL_ID_FIELD};
	public static final String[] VISIBLE_BULLETIN_FIELDS = 
		{VISIBLE_AUTHOR_FIELD, VISIBLE_KEYWORDS_FIELD, VISIBLE_TITLE_FIELD, VISIBLE_EVENT_DATE_FIELD, 
			VISIBLE_PUBLIC_INFO_FIELD, VISIBLE_SUMMARY_FIELD, VISIBLE_LOCATION_FIELD, VISIBLE_ENTRY_DATE_FIELD};

}
