package org.martus.amplifier.service.search;

import org.apache.lucene.search.Hits;

public interface IBulletinSearcher
{
	public Hits searchField(String field, String queryString);
}
