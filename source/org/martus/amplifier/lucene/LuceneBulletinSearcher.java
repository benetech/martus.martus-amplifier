/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/

package org.martus.amplifier.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.martus.amplifier.common.CharacterUtil;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.search.BulletinSearcher;
import org.martus.amplifier.search.Results;
import org.martus.common.packet.UniversalId;

public class LuceneBulletinSearcher implements BulletinSearcher
{
	public LuceneBulletinSearcher(String baseDirName) throws Exception
	{
		File indexDir = LuceneBulletinIndexer.getIndexDir(baseDirName);
		LuceneBulletinIndexer.createIndexIfNecessary(indexDir);
		searcher = new IndexSearcher(indexDir.getPath());
	}	
	
	public Results search(Map fields) throws Exception 
	{	
		if (isComplexSearch(fields))
			return getComplexSearchResults(fields);

		return getSimpleSearchResults(fields);
	}

	public BulletinInfo lookup(UniversalId bulletinId) throws Exception 
	{
		Term term = new Term(LuceneSearchConstants.BULLETIN_UNIVERSAL_ID_INDEX_FIELD, 
								bulletinId.toString());
		Results results = new LuceneResults(searcher, new TermQuery(term));

		int numResults = results.getCount();
		if (numResults == 0)
			return null;

		if (numResults == 1)
			return results.getBulletinInfo(0);

		throw new BulletinIndexException(
			"Found more than one field data set for the same bulletin id: " +
				bulletinId + "; found " + numResults + " results");
	}

	public void close() throws Exception
	{
		searcher.close();
	}

	// This method is ONLY used by tests. 
	// We should find a way to get rid of it! 
	public Results search(String field, String queryString) throws Exception 
	{
		QueryBuilder query = new QueryBuilder(queryString, field);
		return new LuceneResults(searcher, query.getQuery());
	}

	private boolean isComplexSearch(Map fields)
	{
		String queryString = (String) fields.get(SearchResultConstants.RESULT_BASIC_QUERY_KEY);
		return (queryString == null);
	}

	private Results getComplexSearchResults(Map fields)
		throws Exception, IOException
	{
		QueryBuilder query = new QueryBuilder(fields);
		return new LuceneResults(searcher, query.getQuery());
	}

	private Results getSimpleSearchResults(Map fields)
		throws Exception, IOException
	{
		String queryString = (String) fields.get(SearchResultConstants.RESULT_BASIC_QUERY_KEY);
		
		String validString = CharacterUtil.removeRestrictCharacters(queryString);
				
		QueryBuilder query = new QueryBuilder(validString, SEARCH_ALL_TEXT_FIELDS);				
		return new LuceneResults(searcher, query.getQuery());
	}
	
	private IndexSearcher searcher;	
}
