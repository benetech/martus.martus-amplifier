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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.search.BulletinSearcher;
import org.martus.amplifier.search.Results;
import org.martus.amplifier.search.SearchConstants;
import org.martus.common.packet.UniversalId;
import org.martus.util.MartusFlexidate;

public class LuceneBulletinSearcher 
	implements BulletinSearcher, LuceneSearchConstants
{
	public LuceneBulletinSearcher(String baseDirName) throws Exception
	{
		File indexDir = LuceneBulletinIndexer.getIndexDir(baseDirName);
		LuceneBulletinIndexer.createIndexIfNecessary(indexDir);
		searcher = new IndexSearcher(indexDir.getPath());
	}	
	
	public Results search(String field, String queryString) throws Exception 
	{
		Query query = queryParser(queryString, field, "Improperly formed query: ");
		return new LuceneResults(searcher, query);
	}

	public Results search(HashMap fields) throws Exception 
	{	
		String queryString = (String) fields.get(SearchResultConstants.RESULT_BASIC_QUERY_KEY);
		Query query = null;
		if (queryString != null)
			query = multiFieldQueryParser(queryString, SEARCH_ALL_TEXT_FIELDS, "Improperly formed multiquery: ");				
		else
			query = complexSearch(fields);

		return new LuceneResults(searcher, query);
	}

	public BulletinInfo lookup(UniversalId bulletinId) throws Exception 
	{
		Term term = new Term(
			BULLETIN_UNIVERSAL_ID_INDEX_FIELD, bulletinId.toString());
		Query query = new TermQuery(term);
		
		Results results = new LuceneResults(searcher, query);

		int numResults = results.getCount();
		if (numResults == 0) {
			return null;
		}
		if (numResults == 1) {
			return results.getBulletinInfo(0);
		}
		throw new BulletinIndexException(
			"Found more than one field data set for the same bulletin id: " +
				bulletinId + "; found " + numResults + " results");
	}

	public void close() throws Exception
	{
		searcher.close();
	}

	public static String getStartDateRange(String value)
	{
		MartusFlexidate mfd = MartusFlexidate.createFromMartusDateString(value);
		return MartusFlexidate.toStoredDateFormat(mfd.getBeginDate());
	}

	public static String getEndDateRange(String value)
	{
		MartusFlexidate mfd = MartusFlexidate.createFromMartusDateString(value);
		if (!mfd.hasDateRange())
			return null;
		return MartusFlexidate.toStoredDateFormat(mfd.getEndDate());
	}

	private Query queryParser(String query, String field, String msg)
			throws Exception 
	{
		return QueryParser.parse(query, field, getAnalyzer());
	}

	private Query multiFieldQueryParser(String query, String[] fields, String msg)
			throws Exception 
	{
		return MultiFieldQueryParser.parse(query, fields, getAnalyzer());
	}
	
	private Query queryEventDate(HashMap fields)
			throws Exception 
	{
		Date startDate	= (Date) fields.get(SEARCH_EVENT_START_DATE_INDEX_FIELD);
		Date endDate 	= (Date) fields.get(SEARCH_EVENT_END_DATE_INDEX_FIELD);

		String startDateString = setRangeQuery("*", DateField.dateToString(endDate));
		String endDateString   = setRangeQuery(DateField.dateToString(startDate), "?");

		String queryString = getFieldQuery(SearchConstants.SEARCH_EVENT_START_DATE_INDEX_FIELD, startDateString);
		queryString += AND+ getFieldQuery(SearchConstants.SEARCH_EVENT_END_DATE_INDEX_FIELD,endDateString);
		
		return queryParser(queryString,SEARCH_EVENT_DATE_INDEX_FIELD, "Improperly formed query: ");
	
	}
	
	private Query queryEntryDate(HashMap fields)
			throws Exception 
	{		
		Date startDate	= (Date) fields.get(SEARCH_ENTRY_DATE_INDEX_FIELD);

		if (startDate == null)				
			return null;
	
		String startDateString = DateField.dateToString(startDate);
		String endDateString = DateField.dateToString(new GregorianCalendar().getTime());
						
		return queryParser(setRangeQuery(startDateString, endDateString), SEARCH_ENTRY_DATE_INDEX_FIELD,
			"Improperly formed advanced find entry date type in bulletin query: ");		
	}	
	
	private Query queryLanguage(HashMap fields)
			throws Exception
	{
		Query query = null;
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_LANGUAGE_KEY);

		if (fieldString != null)				
			query = queryParser(fieldString,SEARCH_LANGUAGE_INDEX_FIELD, "Improperly formed advanced find language type in bulletin query: ");
		
		return query;
	} 	

	private Query queryAnyWords(HashMap fields) throws Exception
	{
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);
		String queryString = (String) fields.get(SearchResultConstants.ANYWORD_TAG);

		return queryEachField(queryString, fieldString);
	}

	private Query queryTheseWords(HashMap fields) throws Exception
	{
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);
		String queryString = (String) fields.get(SearchResultConstants.THESE_WORD_TAG);

		return queryEachField(queryString, fieldString);
	}

	private Query queryExactPhrase(HashMap fields) throws Exception
	{
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);
		String queryString = (String) fields.get(SearchResultConstants.EXACTPHRASE_TAG);

		return queryEachField(queryString, fieldString);
	}	

	private Query queryWithoutWords(HashMap fields) throws Exception
	{
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);
		String queryString = (String) fields.get(SearchResultConstants.WITHOUTWORDS_TAG);

		return queryEachField(queryString, fieldString);
	}	

	private Query queryEachField(String queryString, String fieldString)
		throws Exception
	{
		if (queryString == null || queryString.length() <= 1)
			return null;
			
		if (fieldString.equals(SearchResultConstants.IN_ALL_FIELDS))
			return multiFieldQueryParser(queryString, SEARCH_ALL_TEXT_FIELDS, "Improperly formed advanced find bulletin multiquery: ");
						
		return queryParser(queryString, fieldString, "Improperly formed advanced find bulletin query: ");
	}				
	
	private Query complexSearch(HashMap fields)
			throws Exception 
	{
		BooleanQuery query = new BooleanQuery();
		
		Query foundEventDateQuery = queryEventDate(fields);					
		query.add(foundEventDateQuery, true, false);
		
		Query foudAnywordsQuery = queryAnyWords(fields);
		if (foudAnywordsQuery != null)
			query.add(foudAnywordsQuery, true, false);

		Query foudThesewordsQuery = queryTheseWords(fields);
		if (foudThesewordsQuery != null)
			query.add(foudThesewordsQuery, true, false);

		Query foudExactPhraseQuery = queryExactPhrase(fields);
		if (foudExactPhraseQuery != null)
			query.add(foudExactPhraseQuery, true, false);

		Query foudWithoutWordsQuery = queryWithoutWords(fields);
		if (foudWithoutWordsQuery != null)
			query.add(foudWithoutWordsQuery, true, false);
			
		Query foudLanguageQuery = queryLanguage(fields);
		if (foudLanguageQuery != null)
			query.add(foudLanguageQuery, true, false);
			
		Query foudEntryDateQuery = queryEntryDate(fields);

		if (foudEntryDateQuery != null)
			query.add(foudEntryDateQuery, true, false);			
			
		return query;	
	}			

	private String setRangeQuery(String from, String to)
	{
		return "[ " + from + " TO " + to + " ]";
	}
	
	private String getFieldQuery(String name, String query)
	{
		return name+":"+query;
	}
		
	private static Analyzer getAnalyzer()
	{
		return LuceneBulletinIndexer.getAnalyzer();
	}

	private IndexSearcher searcher;	
	final String AND= " AND ";
}