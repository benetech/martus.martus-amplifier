package org.martus.amplifier.common.bulletin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.api.AttachmentInfoListFactory;
import org.martus.amplifier.service.search.IBulletinConstants;
import org.martus.amplifier.service.search.ISearchConstants;
import org.martus.common.UniversalId;
import org.martus.common.UniversalId.NotUniversalIdException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;





/**
 * BulletinDocument
 * 
 * Creates a Lucene Document object from a Martus Bulletin
 * Uses SAX to parse the XML data into meaningful fields
 * Does not respect the XML hierarchy at this time
 * 
 * @author Daniel Chu
 */

public class BulletinDocument implements IBulletinConstants, ISearchConstants
{
  /** Makes a document for a File.
    <p>
    The document has three fields:
    <ul>
    <li><code>path</code>--containing the pathname of the file, as a stored,
    tokenized field;
    <li><code>modified</code>--containing the last modified date of the file as
    a keyword field as encoded by <a
    href="lucene.document.DateField.html">DateField</a>; and
    <li><code>contents</code>--containing the full contents of the file, as a
    Reader field;
    */
	public static Document convertToDocument(File file)
  	throws java.io.FileNotFoundException 
  	{	 
	    Document doc = new Document();
	
	    // Add the path of the file as a field named "path".  Use a Text field, so
	    // that the index stores the path, and so that the path is searchable
	    doc.add(Field.Text("path", file.getPath()));
	
	    // Add the last modified date of the file a field named "modified".  Use a
	    // Keyword field, so that it's searchable, but so that no attempt is made
	    // to tokenize the field into words.
	    doc.add(Field.Keyword("modified",
				  DateField.timeToString(file.lastModified())));
	
		XMLReader xmlReader = null;
		
		try
		{
			xmlReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		}
		catch(SAXException se)
		{
			logger.severe("Unable to create XML reader: " + se.getMessage());
		}
		
		BulletinContentHandler handler = new BulletinContentHandler();
		xmlReader.setContentHandler(handler);
		//xr.setErrorHandler(handler);		
		
    	FileInputStream is = new FileInputStream(file);
    	Reader reader = new BufferedReader(new InputStreamReader(is));
    	
		
		try
		{
	    	xmlReader.parse(new InputSource(reader));
		}
		catch(SAXException se)
		{
			logger.severe("Unable to parse XML file: " + se.getMessage());
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to parse XML file: " + ioe.getMessage());
		}
		
		String currentFieldname, currentField;
		for(int i = 0; i < BULLETIN_FIELDS.length; i++)
		{
			currentFieldname = BULLETIN_FIELDS[i];
			currentField = handler.getBulletinField(currentFieldname);
			if(currentField != null)
			{
				if(currentFieldname == UNIVERSAL_ID_FIELD)
				{
					doc.add(Field.Keyword(currentFieldname, currentField));
				}
				else
				{
					doc.add(Field.Text(currentFieldname, currentField));
				}
			}
		}
	   
	   	// store the attachments as well
	   	// dan: not sure this is the best place for it
	   	List attachmentList = 
	   		AttachmentInfoListFactory.createList(handler.getBulletinAttachmentIds(), 
	   			handler.getBulletinAttachmentSessionKeys(),
	   			handler.getBulletinAttachmentLabels());
	   	try
	   	{
	   		UniversalId bulletinId = UniversalId.createFromString(handler.getBulletinUniversalId());
	   		AttachmentManager.getInstance().putAttachmentInfoList(bulletinId,
	   			attachmentList);
	   	}
	   	catch(NotUniversalIdException nuie)
	   	{}
	   	logger.info("Document is " + doc.toString() );
	    
	    // return the document
	    return doc;
  }

  private BulletinDocument() {}
  static private Logger logger = Logger.getLogger(SEARCH_LOGGER);
}
    
