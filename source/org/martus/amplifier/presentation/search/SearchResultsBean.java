package org.martus.amplifier.presentation.search;

import java.util.Iterator;

import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinSearcher;

public class SearchResultsBean
{
	/* package */
	SearchResultsBean(BulletinSearcher.Results results)
	{
		this.results = results;
	}
	
	public Iterator getIterator()
	{
		return new SearchResultsIterator();
	}
	
	public SearchResultBean getResult(int i) throws BulletinIndexException
	{
		return new SearchResultBean(results.getFieldDataPacket(i));
	}
	
	public int getCount() throws BulletinIndexException
	{
		return results.getCount();
	}
	
	private class SearchResultsIterator implements Iterator
	{
		public boolean hasNext() 
		{
			try {
				return (index < results.getCount());
			} catch (BulletinIndexException e) {
				throw new SearchResultsRuntimeException(
					"Exception occurred during iteration", e);
			}
		}

		public Object next() 
		{
			try {
				return getResult(index++);
			} catch (BulletinIndexException e) {
				throw new SearchResultsRuntimeException(
					"Exception occurred during iteration", e);
			}
		}

		public void remove()
		{
			throw new UnsupportedOperationException(
				"Remove not supported");
		}
		
		private SearchResultsIterator()
		{
			index = 0;
		}
		
		private int index;
	}
	
	private BulletinSearcher.Results results;
}
