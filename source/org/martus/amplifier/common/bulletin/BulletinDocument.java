package org.martus.amplifier.common.bulletin;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.api.*;
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
		
	    doc.add(Field.Text(AUTHOR_FIELD,handler.getBulletinAuthor()));
	    doc.add(Field.Text(KEYWORDS_FIELD,handler.getBulletinKeywords()));
	    doc.add(Field.Text(TITLE_FIELD,handler.getBulletinTitle()));
	    //doc.add(Field.Keyword(EVENT_DATE_FIELD,handler.getBulletinEventDate()));
	    doc.add(Field.Text(PUBLIC_INFO_FIELD,handler.getBulletinPublicInfo()));
	    doc.add(Field.Text(SUMMARY_FIELD,handler.getBulletinSummary()));
	    doc.add(Field.Text(LOCATION_FIELD,handler.getBulletinLocation()));
	    //doc.add(Field.Keyword(ENTRY_DATE_FIELD,handler.getBulletinEventDate()));
	    doc.add(Field.Keyword(UNIVERSAL_ID_FIELD,handler.getBulletinUniversalId()));
	
	    String eventDateString = convertToDateString(handler.getBulletinEventDate());
	    doc.add(Field.Text(EVENT_DATE_FIELD, eventDateString));
	    String entryDateString = convertToDateString(handler.getBulletinEntryDate());
	    doc.add(Field.Text(ENTRY_DATE_FIELD, entryDateString));
	   
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
  
  
  public static String convertToDateString(String inString)
  {
  	//String inString is of the format YYYY-MM-DD indexed as 0123-56-89
  	Date date = new Date();
  	int iYear, iMonth, iDate;
  	String dateString="";
  	
  	String yearStr  = inString.substring(0,4);
  	String monthStr = inString.substring(5,7);
  	String dateStr  = inString.substring(8);
	try
	{	
		iYear = Integer.parseInt(yearStr);
		iMonth= Integer.parseInt(monthStr);
		iDate = Integer.parseInt(dateStr);
		//note: Month value is 0-based. e.g., 0 for January.	
	   	Calendar calendar = new GregorianCalendar(iYear, iMonth-1, iDate);
	    date = calendar.getTime();
	    dateString = DateField.dateToString(date);	    	
	}
	catch (NumberFormatException e)
	{
		logger.severe("BulletinDocument.convertToDocument(): " + e.getMessage());
	}
	return dateString;
  	
  }

  private BulletinDocument() {}
  static private Logger logger = Logger.getLogger(SEARCH_LOGGER);
}
    
