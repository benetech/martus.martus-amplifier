package org.martus.amplifier.service.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.martus.common.Bulletin;
import org.martus.common.BulletinConstants;

/**
 * This class is used to encapsulate information about a field
 * in a bulletin.
 * 
 * @author PDAlbora
 */
public class BulletinField implements BulletinConstants, SearchConstants
{
	public String getDisplayName()
	{
		return displayName;
	}
	
	public String getXmlId()
	{
		return xmlId;
	}
	
	public String getIndexId()
	{
		return indexId;
	}
	
	public boolean isDateField()
	{
		return (Bulletin.getFieldType(xmlId) == DATE);
	}
	
	public static BulletinField getFieldByXmlId(String xmlId)
	{
		return (BulletinField) FIELDS.get(xmlId);
	}
	
	public static Collection getSearchableFields() 
	{
		return FIELDS.values();
	}
	
	public static String[] getSearchableXmlIds()
	{
		return (String[]) FIELDS.keySet().toArray(new String[0]);
	}
	
	private BulletinField(String xmlId, String indexId, String displayName)
	{
		this.xmlId = xmlId;
		this.indexId = indexId;
		this.displayName = displayName;
	}
	
	private String xmlId;
	private String indexId;
	private String displayName;
	
	private static final Map FIELDS = new HashMap();
	static {
		// NOTE paul 8-Apr-2003 -- The display names should at some
		// point be i18n'ed strings.
		addField(TAGAUTHOR, AUTHOR_INDEX_FIELD, "Author");
		addField(TAGKEYWORDS, KEYWORDS_INDEX_FIELD, "Keywords");
		addField(TAGTITLE, TITLE_INDEX_FIELD, "Title");
		addField(TAGEVENTDATE, EVENT_DATE_INDEX_FIELD, "Event Date");
		addField(TAGPUBLICINFO, PUBLIC_INFO_INDEX_FIELD, "Public Info");
		addField(TAGSUMMARY, SUMMARY_INDEX_FIELD, "Summary");
		addField(TAGLOCATION, LOCATION_INDEX_FIELD, "Location");
		addField(TAGENTRYDATE, ENTRY_DATE_INDEX_FIELD, "Entry Date");
	}
	
	private static void addField(String xmlId, String indexId, String displayName)
	{
		FIELDS.put(xmlId, new BulletinField(xmlId, indexId, displayName));
	}
	

}

