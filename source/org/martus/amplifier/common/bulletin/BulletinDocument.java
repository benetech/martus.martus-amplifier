package org.martus.amplifier.common.bulletin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.martus.amplifier.service.search.IBulletinConstants;
import org.martus.amplifier.service.search.ISearchConstants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
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
	    doc.add(Field.Text(PATH_INDEX_FIELD, file.getPath()));
	
	    // Add the last modified date of the file a field named "modified".  Use a
	    // Keyword field, so that it's searchable, but so that no attempt is made
	    // to tokenize the field into words.
	    doc.add(Field.Keyword(MODIFIED_INDEX_FIELD,
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
		
		BulletinContentHandler handler = new BulletinContentHandler(doc);
		xmlReader.setContentHandler(handler);
		//xr.setErrorHandler(handler);		
		
    	FileInputStream is = new FileInputStream(file);
    	Reader reader = new BufferedReader(new InputStreamReader(is));
    	
		
		try
		{
	    	xmlReader.parse(new InputSource(reader));
		}
		catch (SAXParseException spe)
		{
			logger.severe(
				"Unable to parse XML file: SAXParseException: " +
				spe.getMessage() + "; line = " + spe.getLineNumber() +
				"; col = " + spe.getColumnNumber() + "; cause: " +
				spe.getException());
		}
		catch(SAXException se)
		{
			logger.severe(
				"Unable to parse XML file: SAXException: " + 
				se.getMessage() + "; cause: " + 
				se.getException());
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to parse XML file: IOException: " + ioe.getMessage());
		}
		
	   	logger.info("Document is " + doc.toString() );
	    
	    // return the document
	    return doc;
  }

  private BulletinDocument() {}
  static private Logger logger = Logger.getLogger(SEARCH_LOGGER);
}
    
