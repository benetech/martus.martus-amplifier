package org.martus.amplifier.common.bulletin;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.api.AttachmentInfo;
import org.martus.amplifier.service.search.BulletinField;
import org.martus.amplifier.service.search.ISearchConstants;
import org.martus.common.Bulletin;
import org.martus.common.MartusXml;
import org.martus.common.UniversalId;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
		this(null);
	}
	
	public BulletinContentHandler(Document doc)
	{
		setIndexDocument(doc);
	}
	
	public void setIndexDocument(Document doc)
	{
		this.doc = doc;
	}

	/**
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
	 */
	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException
	{
		fieldValues = new HashMap();
		attachments = new ArrayList();
		inAttachment = false;
		curElementName = null;
		curValue = null;
		account = null;
		packetId = null;
		id = null;
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException
	{
		addFieldsToDocument();
		addAttachmentsToDocument();
		//storeAttachments();	
	}

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
		String namespaceURI,
		String localName,
		String qName,
		Attributes attributes)
		throws SAXException
	{
		curElementName = localName;
		curValue = null;
		if (curElementName.equals(MartusXml.AttachmentElementName)) {
			startAttachment();
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(String namespaceURI, String localName, String qName)
		throws SAXException
	{
		processCurElement();
		curElementName = null;
		if (localName.equals(MartusXml.AttachmentElementName)) {
			endAttachment();
		}
	}

	
	
	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		String s = new String(ch, start, length);
		if (curValue == null) {
			curValue = s;
		} else {
			curValue += s;
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
	
	private void startAttachment() 
	{
		inAttachment = true;
		attachmentLocalId = null;
		attachmentKey = null;
		attachmentLabel = null;
	}

	private void endAttachment()
	{
		inAttachment = false;
		attachments.add(new AttachmentInfo(
			attachmentLocalId, attachmentKey, attachmentLabel));
	}
	
	private void processCurElement() throws SAXException
	{
		if (curElementName != null) {
			if (inAttachment) {
				processAttachmentField();
			} else if (curElementName.startsWith(MartusXml.FieldElementPrefix)) {
				processBulletinField();
			} else if (curElementName.equals(MartusXml.AccountElementName)) {
				account = curValue;
			} else if (curElementName.equals(MartusXml.PacketIdElementName)) {
				packetId = curValue;
			}
		}
	}
	
	private void processAttachmentField()
	{
		if (curElementName.equals(
			MartusXml.AttachmentLocalIdElementName))
		{
			attachmentLocalId = curValue;
		}
		else if (curElementName.equals(
			MartusXml.AttachmentKeyElementName))
		{
			attachmentKey = curValue;
		}
		else if (curElementName.equals(
			MartusXml.AttachmentLabelElementName))
		{
			attachmentLabel = curValue;
		}
	}
	
	private void processBulletinField() 
		throws SAXException
	{
		String fieldName = curElementName.substring(
			MartusXml.FieldElementPrefix.length());
		BulletinField field = BulletinField.getFieldByXmlId(fieldName);
		if (field != null) {
			String value = curValue;
			if (field.isDateField()) {
				try {
					value = convertDateToSearchableString(value);
				} catch (ParseException e) {
					throw new SAXParseException(
						"Found invalid " + fieldName, locator, e);
				}
			}
			fieldValues.put(field.getIndexId(), value);
		}
	}
	
	private void addFieldsToDocument()
	{
		for (Iterator iter = fieldValues.entrySet().iterator(); 
			iter.hasNext();) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			doc.add(Field.Text(key, value));
		}
		
		UniversalId id = getUniversalId();
		doc.add(Field.Keyword(ISearchConstants.UNIVERSAL_ID_INDEX_FIELD, id.toString()));
		
	}
	
	private void addAttachmentsToDocument()
	{
		if (!attachments.isEmpty()) {
			StringBuffer attachmentLocalIds = new StringBuffer();
			for (Iterator iter = attachments.iterator(); iter.hasNext();) {
				AttachmentInfo info = (AttachmentInfo) iter.next();
				attachmentLocalIds.append(info.getLocalId());
				attachmentLocalIds.append(ISearchConstants.ATTACHMENT_ID_LIST_SEPARATOR);
			}
			doc.add(Field.UnIndexed(
				ISearchConstants.ATTACHMENT_ID_LIST_INDEX_FIELD, 
				attachmentLocalIds.toString()));
		}	
	}
	
	private void storeAttachments()
	{
		AttachmentManager.getInstance().putAttachmentInfoList(
			getUniversalId(), attachments);
	}
	
	private UniversalId getUniversalId()
	{
		if (id == null) {
			id = UniversalId.createFromAccountAndLocalId(
				account, packetId);
		}
		return id;
	}
	
	private static String convertDateToSearchableString(String dateString) 
		throws ParseException
	{
		// NOTE pdalbora 07-Apr-2003 -- Is this an appropriate
		// use of this method? The current code appears to create
		// a new SimpleDateFormat on each call, so maybe store this
		// in a static variable?
		DateFormat df = Bulletin.getStoredDateFormat();
		
		return DateField.dateToString(df.parse(dateString));
	}
	
	
	private Document doc;
	private Map fieldValues;
	private List attachments;
	private boolean inAttachment;
	private UniversalId id;
	private String curElementName;
	private String curValue;
	private String attachmentLocalId;
	private String attachmentLabel;
	private String attachmentKey;
	private String account;
	private String packetId;
	
	private Locator locator;
		
}
