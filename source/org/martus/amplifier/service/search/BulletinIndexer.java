package org.martus.amplifier.service.search;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.martus.amplifier.common.bulletin.BulletinDocument;

/**
 * @author Daniel Chu
 *
 * The BulletinIndexer is responsible for creating, maintaining, and 
 * deleting the index of Bulletins.
 *  
 */
public class BulletinIndexer implements IBulletinConstants, ISearchConstants
{
	
	private static BulletinIndexer instance = new BulletinIndexer();

	protected BulletinIndexer()
	{
		;
	}
	
	public static BulletinIndexer getInstance() 
	{
		return instance;
	}

	synchronized public void indexBulletins()
	{
		IndexWriter writer = null;
		try
		{
			if(IndexReader.isLocked(DEFAULT_INDEX_LOCATION))
			{
				logger.info("Cannot indexing bulletins, database locked...");
			}
			else
			{
				logger.info("Indexing bulletins...");
				
				writer = new IndexWriter(DEFAULT_INDEX_LOCATION, new StandardAnalyzer(), true);
				indexDocs(writer, new File(DEFAULT_FILES_LOCATION));
				writer.optimize();
			}
		}
		catch(java.io.IOException ioe)
		{
			logger.severe("Unable to index bulletins:" + ioe.getMessage());
			ioe.printStackTrace();
		}
		finally
		{
			if(writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
				}
			}
			
			try
			{
				theIndexLastModified = IndexReader.lastModified(DEFAULT_INDEX_LOCATION);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	synchronized  public void indexDocs(IndexWriter writer, File file)
    throws IOException 
    {
    	// this is ugly, maybe we need to remove amplifierdata
    	if(file.getName().equals("CVS"))
    		return;
    		
    	if(file.isDirectory())
    	{
      		String[] files = file.list();
      		for (int i = 0; i < files.length; i++)
      		{
				indexDocs(writer, new File(file, files[i]));
      		}
    	}
    	else 
    	{
      		logger.info("adding bulletin " + file.getName());
      		writer.addDocument(BulletinDocument.convertToDocument(file));
    	}
  	}
  	private Logger logger = Logger.getLogger(SEARCH_LOGGER);
  	static long theIndexLastModified;
}




