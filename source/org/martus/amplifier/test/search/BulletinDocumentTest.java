package org.martus.amplifier.test.search;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;

import junit.framework.Assert;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.martus.amplifier.common.bulletin.BulletinDocument;
import org.martus.amplifier.service.search.ISearchConstants;
import org.martus.common.Bulletin;

public class BulletinDocumentTest extends AbstractAmplifierSearchTest
implements ISearchConstants
{
	public BulletinDocumentTest(String name)
	{
		super(name);
	}
	
	public void testConvertXmlToDocument()
	{
		File file = new File(DEFAULT_FILES_LOCATION + "\\OxfamBulletin");
		Document doc = null;
		try
		{
			doc = BulletinDocument.convertToDocument(file);
		}
		catch(IOException ioe)
		{
			Assert.fail("Conversion failed: " + ioe.getMessage());
		}
		
		
		Assert.assertNotNull(doc);
		Assert.assertEquals(file.getPath(), doc.get(PATH_INDEX_FIELD));
		Assert.assertEquals(file.lastModified(), DateField.stringToTime(doc.get(MODIFIED_INDEX_FIELD)));
		Assert.assertEquals("Sri Lanka Peace Institute", doc.get(AUTHOR_INDEX_FIELD));
		Assert.assertEquals("firebomb, explosion, NGO", doc.get(KEYWORDS_INDEX_FIELD));
		Assert.assertEquals("Firebombing of NGO Office", doc.get(TITLE_INDEX_FIELD));
		Assert.assertEquals("Colombo", doc.get(LOCATION_INDEX_FIELD));
		
		// Long fields
		Assert.assertTrue(doc.get(PUBLIC_INFO_INDEX_FIELD).startsWith("The Oxfam NGO office in downtown Colombo "));
		Assert.assertTrue(doc.get(SUMMARY_INDEX_FIELD).endsWith("by men who threw a firebomb in the front windows."));
		
		
		// dates
		DateFormat df = Bulletin.getStoredDateFormat();
		Assert.assertEquals("2001-02-03", df.format(DateField.stringToDate(doc.get(EVENT_DATE_INDEX_FIELD))));
		Assert.assertEquals("2001-02-05", df.format(DateField.stringToDate(doc.get(ENTRY_DATE_INDEX_FIELD))));
		
	}
	
	public void testAttachmentIds()
	{
		File file = new File(DEFAULT_FILES_LOCATION + "\\F-40984b-eeeb80ef32--7fab");
		Document doc = null;
		try
		{
			doc = BulletinDocument.convertToDocument(file);
		}
		catch(IOException ioe)
		{
			Assert.fail("Conversion failed: " + ioe.getMessage());
		}
		
		Assert.assertNotNull(doc);
		
		// TODO pdalbora 17-Apr-2003 -- The following code should be placed
		// in a function somewhere, since it we'll need it eventually. I've
		// placed it here for now since I'm not sure where it should go at
		// the moment. We want a function which takes a Document and returns
		// a List of attachmentId strings.
		String attachmentIdsString = doc.get(ATTACHMENT_ID_LIST_INDEX_FIELD);
		Assert.assertNotNull(attachmentIdsString);
		String[] attachmentIds = attachmentIdsString.split(ATTACHMENT_ID_LIST_SEPARATOR);
		
		Assert.assertEquals(1, attachmentIds.length);
		Assert.assertEquals("A-40984b-eeeb80ef32--7fa6", attachmentIds[0]);
	}
	
}
