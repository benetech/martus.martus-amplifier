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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.lucene.document.DateField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
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
	public LuceneBulletinSearcher(String baseDirName) 
		throws BulletinIndexException
	{
		File indexDir = LuceneBulletinIndexer.getIndexDir(baseDirName);
		try {
			LuceneBulletinIndexer.createIndexIfNecessary(indexDir);
			searcher = new IndexSearcher(indexDir.getPath());
		} catch (IOException e) {
			throw new BulletinIndexException(
				"Could not create LuceneBulletinSearcher", e);
		}
	}	
	
	private Results getLuceneResults(Query query)
		throws BulletinIndexException 
	{
		try {
			return new LuceneResults(searcher.search(query));
		} catch (IOException e) {
			throw new BulletinIndexException(
				"An error occurred while executing query " + query.toString(), e);
		}		
	}
	
	public Results search(String field, String queryString)
		throws BulletinIndexException 
	{
		Query query = queryParser(queryString, field, "Improperly formed query: ");
		return getLuceneResults(query);
	}

	public Results search(HashMap fields)
		throws BulletinIndexException 
	{	
		String queryString = (String) fields.get(SearchResultConstants.RESULT_BASIC_QUERY_KEY);
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);

		if (queryString != null)
		{
			Query query = multiFieldQueryParser(queryString, SEARCH_ALL_TEXT_FIELDS, "Improperly formed multiquery: ");				
			return getLuceneResults(query);	
		}	
			
		queryString = (String) fields.get(SearchResultConstants.RESULT_ADVANCED_QUERY_KEY);												
		return getLuceneResults(complexSearch(queryString, fields));
	}

		private Query queryParser(String query, String field, String msg)
			throws BulletinIndexException 
	{
		try {
			return QueryParser.parse(query, field, 		
				LuceneBulletinIndexer.getAnalyzer());
		} catch(ParseException pe) {
			throw new BulletinIndexException( msg + query, pe);
		}
	}

	private Query multiFieldQueryParser(String query, String[] fields, String msg)
			throws BulletinIndexException 
	{
		try {
			return MultiFieldQueryParser.parse(query, fields, 		
				LuceneBulletinIndexer.getAnalyzer());
		} catch(ParseException pe) {
			throw new BulletinIndexException( msg + query, pe);
		}
	}				
	
	private Query queryEventDate(HashMap fields)
			throws BulletinIndexException 
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
			throws BulletinIndexException 
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
			throws BulletinIndexException
	{
		Query query = null;
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_LANGUAGE_KEY);

		if (fieldString != null)				
			query = queryParser(fieldString,SEARCH_LANGUAGE_INDEX_FIELD, "Improperly formed advanced find language type in bulletin query: ");
		
		return query;
	} 
	
	private Query queryMultiBulletinFields(String query, HashMap fields)
			throws BulletinIndexException 
	{		
		Query fieldQuery = null;
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);
		String queryString = (String) fields.get(SearchResultConstants.RESULT_ADVANCED_QUERY_KEY);

		if (query != null && query.length()>0)
		{
			if (fieldString.equals(SearchResultConstants.IN_ALL_FIELDS))
				return multiFieldQueryParser(query, SEARCH_ALL_TEXT_FIELDS, "Improperly formed advanced find bulletin multiquery: ");
						
			fieldQuery = queryParser(queryString, fieldString, "Improperly formed advanced find bulletin query: ");
		}
		
		return fieldQuery;
	}				
	
	private Query complexSearch(String queryString, HashMap fields)
			throws BulletinIndexException 
	{
		BooleanQuery query = new BooleanQuery();
		Query foundEventDateQuery = queryEventDate(fields);					
		query.add(foundEventDateQuery, true, false);
		
		Query foudBulletinsQuery = queryMultiBulletinFields(queryString, fields);
		if (foudBulletinsQuery != null)
			query.add(foudBulletinsQuery, true, false);
			
		Query foudLanguageQuery = queryLanguage(fields);
		if (foudLanguageQuery != null)
			query.add(foudLanguageQuery, true, false);
			
		Query foudEntryDateQuery = queryEntryDate(fields);

		if (foudEntryDateQuery != null)
			query.add(foudEntryDateQuery, true, false);			
			
		return query;	
	}			

	public BulletinInfo lookup(UniversalId bulletinId)
		throws BulletinIndexException 
	{
		Term term = new Term(
			BULLETIN_UNIVERSAL_ID_INDEX_FIELD, bulletinId.toString());
		Query query = new TermQuery(term);
		
		Results results;
		try {
			results = new LuceneResults(searcher.search(query));
		} catch (IOException e) {
			throw new BulletinIndexException(
				"An error occurred while searching query " + 
					query.toString(BULLETIN_UNIVERSAL_ID_INDEX_FIELD), 
				e);
		}
		
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

	public void close() throws BulletinIndexException
	{
		try {
			searcher.close();
		} catch (IOException e) {
			throw new BulletinIndexException(
				"Unable to close the searcher: ", e);
		}
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

	private String setRangeQuery(String from, String to)
	{
		return "[ " + from + " TO " + to + " ]";
	}
	
	private String getFieldQuery(String name, String query)
	{
		return name+":"+query;
	}
		
	private IndexSearcher searcher;	
	final String AND= " AND ";
}