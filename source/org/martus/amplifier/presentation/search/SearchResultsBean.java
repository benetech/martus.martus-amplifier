package org.martus.amplifier.presentation.search;

import org.apache.lucene.search.Hits;
import org.martus.amplifier.service.search.BulletinSearcher;

public class SearchResultsBean
{

	public SearchResultsBean()
	{
		super();
	}

	public Hits getSearchResults(String field, String query)
	{
		return BulletinSearcher.getInstance().searchField(field, query);
	}
	
	public Hits getSearchResults(String query)
	{
		return BulletinSearcher.getInstance().searchText(query);
	}

}
