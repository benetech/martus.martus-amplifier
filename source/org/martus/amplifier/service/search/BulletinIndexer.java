package org.martus.amplifier.service.search;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.martus.amplifier.LoggerConstants;

/**
 * @author Daniel Chu
 *
 * The BulletinIndexer is responsible for creating, maintaining, and 
 * deleting the index of Bulletins.
 *  
 */
public class BulletinIndexer implements BulletinConstants, LoggerConstants
{
	
	private static BulletinIndexer instance = new BulletinIndexer();

	protected BulletinIndexer()
	{}
	
	public static BulletinIndexer getInstance() 
	{
		return instance;
	}

	public void indexBulletins()
	{
		IndexWriter writer = null;
		try
		{
			writer = new IndexWriter(DEFAULT_INDEX_LOCATION, new StandardAnalyzer(), false);
			indexDocs(writer, new File(DEFAULT_FILES_LOCATION));
			writer.optimize();
			writer.close();
		}
		catch(java.io.IOException ioe)
		{
			logger.severe("Unable to index bulletins: " + ioe.getMessage());
		}
	}
	
	public void indexDocs(IndexWriter writer, File file)
    throws IOException 
    {
    	if(file.getName().equals("CVS"))
    		return;
    		
    	if (file.isDirectory()) 
    	{
      		String[] files = file.list();
      		for (int i = 0; i < files.length; i++)
			indexDocs(writer, new File(file, files[i]));
    	} 
    	else 
    	{
      		logger.info("adding " + file);
      		writer.addDocument(BulletinDocument.convertToDocument(file));
    	}
  	}
  	private Logger logger = Logger.getLogger(SEARCH_LOGGER);
}




