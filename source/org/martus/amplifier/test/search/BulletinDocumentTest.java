package org.martus.amplifier.test.search;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.apache.lucene.document.Document;
import org.martus.amplifier.LoggerConstants;
import org.martus.amplifier.service.search.BulletinDocument;

public class BulletinDocumentTest extends AbstractAmplifierSearchTest
implements LoggerConstants
{
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
			Logger.getLogger(SEARCH_LOGGER).severe("Unable to convert document:" + 
				ioe.getMessage());
		}
		Assert.assertNotNull(doc);
	}
}
