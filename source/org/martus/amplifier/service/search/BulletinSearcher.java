package org.martus.amplifier.service.search;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

/**
 * @author Daniel Chu
 *
 * The BulletinSearcher class holds the keys to all of the searching
 * functionality needed by the Amplifier application. If this class
 * gets too unwieldy we may need to break it up.
 */
public class BulletinSearcher implements BulletinConstants
{
	protected BulletinSearcher()
	{}
	
	public static BulletinSearcher getInstance() 
	{
		return instance;
	}
   
	public Hits fieldSearch(String field, String queryString)
	{
		Hits hits = null;
		Analyzer analyzer = null;
		Query query = null;
		IndexSearcher searcher = null;
		
		Assert.assertTrue(queryString != null);
		analyzer = new StopAnalyzer();
		
		try
		{
			query = QueryParser.parse(queryString, field, analyzer);
		}
		catch(ParseException pe)
		{
			
		}
		
		try
		{
			searcher = new IndexSearcher(IndexReader.open(DEFAULT_INDEX_LOCATION));
			hits = searcher.search(query);
		}
		catch(IOException ioe)
		{}
		
		return hits;
	}
	
	public Hits textSearch(String queryString)
	{
		return fieldSearch("contents", queryString);
	}
	
	private static BulletinSearcher instance = new BulletinSearcher();
}
