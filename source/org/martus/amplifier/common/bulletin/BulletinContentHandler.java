package org.martus.amplifier.common.bulletin;

import java.util.ArrayList;
import java.util.List;

import org.martus.common.UniversalId;
import org.martus.amplifier.exception.MartusAmplifierRuntimeException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * BulletinContentHandler
 * 
 * Parses out the XML fields in a Martus Bulletin using
 * a SAX Parser. The BulletinContentHandler is registered
 * with the SAX Parser, and as the Parser parses, it calls 
 * the methods of the BulletinContentHandler.
 * 
 * @author dchu
 *  
 */
public class BulletinContentHandler implements ContentHandler
{
	/**
	 * Constructor for BulletinContentHandler.
	 */
	public BulletinContentHandler()
	{
		super();
	}

	/**
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
	 */
	public void setDocumentLocator(Locator arg0)
	{}

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException
	{}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException
	{}

	/**
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
	 */
	public void startPrefixMapping(String arg0, String arg1)
		throws SAXException
	{}

	/**
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
	 */
	public void endPrefixMapping(String arg0) throws SAXException
	{}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(
		String uri,
		String name,
		String qName,
		Attributes attributes)
		throws SAXException
	{
		if(name.equals("Field-author"))
		{
			currentFieldType = AUTHOR_FIELD_TYPE;
		}
		else if (name.equals("Field-keywords"))
		{
			currentFieldType = KEYWORDS_FIELD_TYPE;
		}
		else if (name.equals("Field-title"))
		{
			currentFieldType = TITLE_FIELD_TYPE;
		}
		else if (name.equals("Field-eventdate"))
		{
			currentFieldType = EVENT_DATE_FIELD_TYPE;
		}
		else if (name.equals("Field-publicinfo"))
		{
			currentFieldType = PUBLIC_INFO_FIELD_TYPE;
		}
		else if (name.equals("Field-summary"))
		{
			currentFieldType = SUMMARY_FIELD_TYPE;
		}
		else if (name.equals("Field-location"))
		{
			currentFieldType = LOCATION_FIELD_TYPE;
		}
		else if (name.equals("Field-entrydate"))
		{
			currentFieldType = ENTRY_DATE_FIELD_TYPE;
		}
		else if (name.equals("Account"))
		{
			currentFieldType = ACCOUNT_ID_FIELD_TYPE;
		}
		else if (name.equals("PacketId"))
		{
			currentFieldType = PACKET_ID_FIELD_TYPE;
		}
		else if (name.equals("AttachmentLocalId"))
		{
			currentFieldType = ATTACHMENT_ID;
		}
		else if (name.equals("AttachmentSessionKey"))
		{
			currentFieldType = ATTACHMENT_KEY;
		}
		else if (name.equals("AttachmentLabel"))
		{
			currentFieldType = ATTACHMENT_LABEL;
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(String arg0, String arg1, String arg2)
		throws SAXException
	{
		currentFieldType = EMPTY_FIELD_TYPE;
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		switch(currentFieldType)
		{
			case AUTHOR_FIELD_TYPE:
				bulletinAuthor = new String(ch, start, length);
				break;
			case EMPTY_FIELD_TYPE:
				break;
			case ENTRY_DATE_FIELD_TYPE: 
				bulletinEntryDate = new String(ch, start, length);
				break;
			case EVENT_DATE_FIELD_TYPE: 
				bulletinEventDate = new String(ch, start, length);
				break;
			case KEYWORDS_FIELD_TYPE: 
				bulletinKeywords = new String(ch, start, length);
				break;
			case LOCATION_FIELD_TYPE: 
				bulletinLocation = new String(ch, start, length);
				break;
			case PUBLIC_INFO_FIELD_TYPE: 
				bulletinPublicInfo = new String(ch, start, length);
				break;
			case SUMMARY_FIELD_TYPE: 
				bulletinSummary = new String(ch, start, length);
				break;
			case TITLE_FIELD_TYPE: 
				bulletinTitle = new String(ch, start, length);
				break;
			case ACCOUNT_ID_FIELD_TYPE: 
				bulletinAccountId = new String(ch, start, length);
				break;
			case PACKET_ID_FIELD_TYPE: 
				bulletinPacketId = new String(ch, start, length);
				break;
			case ATTACHMENT_ID: 
				addBulletinAttachmentId(new String(ch, start, length));
				break;
			case ATTACHMENT_KEY: 
				addBulletinAttachmentSessionKey(new String(ch, start, length));
				break;
			case ATTACHMENT_LABEL: 
				addBulletinAttachmentLabel(new String(ch, start, length));
				break;
			default:
				throw new MartusAmplifierRuntimeException("Invalid bulletin field type.");
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
		throws SAXException
	{}

	/**
	 * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
	 */
	public void processingInstruction(String arg0, String arg1)
		throws SAXException
	{}

	/**
	 * @see org.xml.sax.ContentHandler#skippedEntity(String)
	 */
	public void skippedEntity(String arg0) throws SAXException
	{}

	public String getBulletinAuthor()
	{
		return bulletinAuthor;
	}
	
	public String getBulletinEntryDate()
	{
		return bulletinEntryDate;
	}

	public String getBulletinEventDate()
	{
		return bulletinEventDate;
	}

	public String getBulletinKeywords()
	{
		return bulletinKeywords;
	}

	public String getBulletinLocation()
	{
		return bulletinLocation;
	}

	public String getBulletinPublicInfo()
	{
		return bulletinPublicInfo;
	}

	public String getBulletinSummary()
	{
		return bulletinSummary;
	}

	public String getBulletinTitle()
	{
		return bulletinTitle;
	}
	
	public String getBulletinUniversalId()
	{
		UniversalId universalId = UniversalId.createFromAccountAndLocalId(bulletinAccountId, bulletinPacketId);

		return universalId.toString();
	}
	
	private void addBulletinAttachmentId(String attachmentId)
	{
		addGenericAttachmentAttribute(bulletinAttachmentIds, attachmentId);
	}
	
	private void addBulletinAttachmentSessionKey(String attachmentSessionKey)
	{
		addGenericAttachmentAttribute(bulletinAttachmentSessionKeys, attachmentSessionKey);	
	}

	private void addBulletinAttachmentLabel(String attachmentLabel)
	{
		addGenericAttachmentAttribute(bulletinAttachmentLabels, attachmentLabel);	
	}
	
	private void addGenericAttachmentAttribute(List attributeList, String attributeValue)
	{
		if(attributeList == null)
			attributeList = new ArrayList();
		attributeList.add(attributeValue);
	}

	public List getBulletinAttachmentIds()
	{
		return bulletinAttachmentIds;
	}

	public List getBulletinAttachmentLabels()
	{
		return bulletinAttachmentLabels;
	}

	public List getBulletinAttachmentSessionKeys()
	{
		return bulletinAttachmentSessionKeys;
	}

	private String bulletinAuthor = null;
	private String bulletinEntryDate = null;
	private String bulletinEventDate = null;
	private String bulletinKeywords = null;
	private String bulletinLocation = null;
	private String bulletinPublicInfo = null;
	private String bulletinSummary = null;	
	private String bulletinTitle = null;
	private String bulletinAccountId = null;
	private String bulletinPacketId = null;
	private List bulletinAttachmentIds = null;
	private List bulletinAttachmentSessionKeys = null;
	private List bulletinAttachmentLabels = null;
	
	private int currentFieldType = 0;
	
	private static final int AUTHOR_FIELD_TYPE = 0;
	private static final int EMPTY_FIELD_TYPE = 1;
	private static final int ENTRY_DATE_FIELD_TYPE = 2;
	private static final int EVENT_DATE_FIELD_TYPE = 3;
	private static final int KEYWORDS_FIELD_TYPE = 4;
	private static final int LOCATION_FIELD_TYPE = 5;
	private static final int PUBLIC_INFO_FIELD_TYPE = 6;
	private static final int SUMMARY_FIELD_TYPE = 7;
	private static final int TITLE_FIELD_TYPE = 8;
	private static final int ACCOUNT_ID_FIELD_TYPE = 9;
	private static final int PACKET_ID_FIELD_TYPE = 10;
	private static final int ATTACHMENT_ID = 11;
	private static final int ATTACHMENT_KEY = 12;
	private static final int ATTACHMENT_LABEL = 13;
}
