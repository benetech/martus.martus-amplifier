package org.martus.amplifier.service.search;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.Token;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.martus.amplifier.service.search.api.IBulletinSearcher;

/**
 * @author Daniel Chu
 *
 * The BulletinSearcher class holds the keys to all of the searching
 * functionality needed by the Amplifier application. If this class
 * gets too unwieldy we may need to break it up.
 */
public class BulletinSearcher 
implements IBulletinSearcher, IBulletinConstants, ISearchConstants
{
	protected BulletinSearcher()
	{}
	
	public static BulletinSearcher getInstance() 
	{
		return instance;
	}
   
	
	public Hits searchDateRange(String field, Date startDate, Date endDate)
	{
		Hits hits = null;
		RangeQuery query = null;
		IndexSearcher searcher = null;
		Term startTerm, endTerm = null;
		
		startTerm = new Term(field, 
								DateField.dateToString(startDate));
		endTerm = new Term(field,						
								DateField.dateToString(endDate)); 
		query = new RangeQuery(startTerm, endTerm, true);
		
		try
		{
			searcher = new IndexSearcher(IndexReader.open(DEFAULT_INDEX_LOCATION));
			hits = searcher.search(query);
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to search index:" + ioe.getMessage());
		}		
		return hits;
	}

	public Hits searchWholeWordField(String field, String queryString)
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
			logger.severe("Unable to parse query:" + pe.getMessage());
		}
		
		try
		{
			searcher = new IndexSearcher(IndexReader.open(DEFAULT_INDEX_LOCATION));
			hits = searcher.search(query);
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to search index:" + ioe.getMessage());
		}
		
		return hits;
	}

	public Hits searchPartialWordField(String field, String queryString)
	{
		Assert.assertTrue(queryString != null);

		Hits hits = null;
		Analyzer analyzer = null;
		IndexSearcher searcher = null;
		analyzer = new StopAnalyzer();
		QueryParser parser = null;
		Term currentTerm = null;
		BooleanQuery completeQuery = null;
		String currentTermString = null;
		PrefixQuery currentPrefixQuery = null;
		Token nextToken = null;
		parser = new QueryParser(queryString, analyzer);
		
		while((nextToken = parser.getNextToken()) != null)
		{
			currentTermString = nextToken.toString();
			currentTerm = new Term(field, currentTermString);
			currentPrefixQuery = new PrefixQuery(currentTerm);
			completeQuery.add(currentPrefixQuery, true, false);	
		}
				
		try
		{
			searcher = new IndexSearcher(IndexReader.open(DEFAULT_INDEX_LOCATION));
			hits = searcher.search(completeQuery);
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to search index:" + ioe.getMessage());
		}
		
		return hits;
	}
	
	public Hits searchField(String field, String queryString)
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
			logger.severe("Unable to parse query:" + pe.getMessage());
		}
		
		try
		{
			searcher = new IndexSearcher(IndexReader.open(DEFAULT_INDEX_LOCATION));
			hits = searcher.search(query);
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to search index:" + ioe.getMessage());
		}
		
		return hits;
	}
	
	/**
     * Searches a keyword field.  Unlike the searchField method, 
     * this method does not parse the queryString.  Use this 
     * method when you want to search fields that have not been
     * tokenized, such as the "universal_id" field.
     * 
     * @return a hits list that includes all the matching documents
	 *
	 */
	public Hits searchKeywordField(String field, String queryString)
	{
		Hits hits = null;
		IndexSearcher searcher = null;
		
		Assert.assertTrue(queryString != null);
		
		TermQuery query = null;
		Term term = null;
		
		term = new Term(field, queryString);
		query = new TermQuery(term);
				
		try
		{
			searcher = new IndexSearcher(IndexReader.open(DEFAULT_INDEX_LOCATION));
			hits = searcher.search(query);
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to search index:" + ioe.getMessage());
		}
		
		return hits;
	}
	
	public Hits searchText(String queryString)
	{
		return searchField(null, queryString);
	}
	
	private static BulletinSearcher instance = new BulletinSearcher();
	private static Logger logger = Logger.getLogger(SEARCH_LOGGER);
}
