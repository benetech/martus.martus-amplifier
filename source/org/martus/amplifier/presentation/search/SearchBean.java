package org.martus.amplifier.presentation.search;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.search.BulletinField;
import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinSearcher;
import org.martus.amplifier.service.search.lucene.LuceneBulletinSearcher;

public class SearchBean implements Serializable
{
	public SearchBean()
	{
		AmplifierConfiguration config = AmplifierConfiguration.getInstance();
		indexPath = config.getBasePath();
		maxCacheSize = 50;
		startIndex = 0;
	}
	
	public void setField(String field)
	{
		this.field = field;
		invalidateCache();
	}
	
	public void setQuery(String query)
	{
		this.query = query;
		startDate = endDate = null;
		invalidateCache();
	}
	
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
		query = null;
		invalidateCache();
	}
	
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
		query = null;
		invalidateCache();
	}
	
	public void setResultsPerPage(int resultsPerPage)
		throws BulletinIndexException
	{
		this.maxCacheSize = resultsPerPage;
		invalidateCache();
	}
	
	public void setIndexPath(String indexPath)
	{
		this.indexPath = indexPath;
		invalidateCache();
	}
	
	public int getResultsPerPage()
	{
		return maxCacheSize;
	}
	
	public int getLastIndexInCurrentPage()
		throws BulletinIndexException
	{
		SearchResultsBean results = getResults();
		return Math.min(startIndex + maxCacheSize, results.size()) - 1;
	}
	
	public boolean isAtEnd() throws BulletinIndexException
	{
		SearchResultsBean results = getResults();
		return (getLastIndexInCurrentPage() == (results.size() - 1));
	}
	
	public int getStartIndex()
	{
		return startIndex;
	}
	
	public void setStartIndex(int startIndex)
	{
		this.startIndex = startIndex;
	}
	
	public Collection getTextSearchFields()
	{
		return BulletinField.getSearchableTextFields();
	}
	
	public Collection getDateSearchFields()
	{
		return BulletinField.getSearchableDateFields();
	}
	
	public List getMonthNames()
	{
		return BulletinField.getMonthNames();
	}
	
	public SearchResultsBean getResults()
		throws BulletinIndexException
	{
		if (results == null) {
			results = new SearchResultsBean();
		}
		return results;	
	}
	
	private void invalidateCache()
	{
		results = null;
		startIndex = 0;
	}
	
	private BulletinSearcher openBulletinSearcher() 
		throws BulletinIndexException
	{
		return new LuceneBulletinSearcher(indexPath);
	}
	
	private String query;
	private Date startDate;
	private Date endDate;
	private String field;
	private int maxCacheSize;
	private int startIndex;
	private String indexPath;
	private SearchResultsBean results;
	
	private class SearchResultsBean extends AbstractList
		implements Serializable
	{
		public Object get(int index) 
		{
			if ((index < startIndex) || 
				(index >= (startIndex + cache.size()))) 
			{
				startIndex = index / maxCacheSize * maxCacheSize;
				try {
					resetCache();
				} catch (BulletinIndexException e) {
					throw new SearchResultsRuntimeException(
						"Unable to read results", e);
				}
			}
			return cache.get(index - startIndex);
		}
	
		public int size() 
		{
			return numResults;
		}
		
		private SearchResultsBean()
			throws BulletinIndexException
		{
			cache = new ArrayList(maxCacheSize);
			resetCache();
		}
		
		private void resetCache()
			throws BulletinIndexException
		{
			BulletinSearcher searcher = openBulletinSearcher();
			try {
				BulletinSearcher.Results results;
				if (query != null) {
					results = searcher.search(field, query);
				} else {
					results = searcher.searchDateRange(field, startDate, endDate);
				}
				cache.clear();
				numResults = results.getCount();
				for (int i = 0; 
					(i < maxCacheSize) && ((i + startIndex) < numResults); 
					i++)
				{
					cache.add(results.getBulletinInfo(i + startIndex));
				}
			} finally {
				searcher.close();
			}
		}
		
		private List cache;
		private int numResults;
	}
}

