package org.martus.amplifier.test.search;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.logging.Logger;

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
		
		DateFormat df = Bulletin.getStoredDateFormat();
		
		Assert.assertNotNull(doc);
		Assert.assertEquals(doc.get(PATH_INDEX_FIELD), file.getPath());
		Assert.assertEquals(DateField.stringToTime(doc.get(MODIFIED_INDEX_FIELD)), file.lastModified());
		Assert.assertEquals(doc.get(AUTHOR_INDEX_FIELD), "Sri Lanka Peace Institute");
		Assert.assertEquals(doc.get(KEYWORDS_INDEX_FIELD), "firebomb, explosion, NGO");
		Assert.assertEquals(doc.get(TITLE_INDEX_FIELD), "Firebombing of NGO Office");
		Assert.assertEquals(doc.get(LOCATION_INDEX_FIELD), "Colombo");
		
		// Long fields
		Assert.assertTrue(doc.get(PUBLIC_INFO_INDEX_FIELD).startsWith("The Oxfam NGO office in downtown Colombo "));
		Assert.assertTrue(doc.get(SUMMARY_INDEX_FIELD).endsWith("by men who threw a firebomb in the front windows."));
		
		
		// dates
		try {
			Assert.assertEquals(DateField.stringToDate(doc.get(EVENT_DATE_INDEX_FIELD)), df.parse("2001-02-03"));
			Assert.assertEquals(DateField.stringToDate(doc.get(ENTRY_DATE_INDEX_FIELD)), df.parse("2001-02-05"));
		} catch (ParseException pe) {
			Assert.fail("Date parsing failed: " + pe.getMessage());
		}
		
	}
}
