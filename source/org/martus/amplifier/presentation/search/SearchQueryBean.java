package org.martus.amplifier.presentation.search;

import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.Hits;
import org.martus.amplifier.service.search.BulletinSearcher;
import org.martus.amplifier.service.search.IBulletinConstants;

public class SearchQueryBean implements IBulletinConstants
{

	public SearchQueryBean()
	{
		super();
	}

	public List getSearchFields()
	{
		List searchFields = Arrays.asList(BULLETIN_FIELDS);
		return searchFields;
	}
	
}
