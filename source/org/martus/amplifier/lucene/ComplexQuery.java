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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateField;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.search.SearchConstants;

public class ComplexQuery
{
	ComplexQuery(String queryString, String field) throws Exception
	{
		query = queryParser(queryString, field, ERROR_PARSING_QUERY);
	}

	ComplexQuery(String queryString, String[] fields) throws Exception
	{
		query = multiFieldQueryParser(queryString, fields, ERROR_PARSING_MULTIQUERY);
	}

	ComplexQuery(HashMap fields) throws Exception
	{
		query = complexSearch(fields);
	}
	
	public Query getQuery()
	{
		return query;
	}
	

	static Query queryEventDate(HashMap fields)
			throws Exception 
	{
		Date startDate	= (Date) fields.get(SearchConstants.SEARCH_EVENT_START_DATE_INDEX_FIELD);
		Date endDate 	= (Date) fields.get(SearchConstants.SEARCH_EVENT_END_DATE_INDEX_FIELD);

		String startDateString = setRangeQuery("*", DateField.dateToString(endDate));
		String endDateString   = setRangeQuery(DateField.dateToString(startDate), "?");

		String queryString = getFieldQuery(SearchConstants.SEARCH_EVENT_START_DATE_INDEX_FIELD, startDateString);
		queryString += AND+ getFieldQuery(SearchConstants.SEARCH_EVENT_END_DATE_INDEX_FIELD,endDateString);
		
		return queryParser(queryString,SearchConstants.SEARCH_EVENT_DATE_INDEX_FIELD, ERROR_PARSING_QUERY);
	
	}
	
	static Query queryEntryDate(HashMap fields)
			throws Exception 
	{		
		Date startDate	= (Date) fields.get(SearchConstants.SEARCH_ENTRY_DATE_INDEX_FIELD);

		if (startDate == null)				
			return null;
	
		String startDateString = DateField.dateToString(startDate);
		String endDateString = DateField.dateToString(new GregorianCalendar().getTime());
						
		return queryParser(setRangeQuery(startDateString, endDateString), SearchConstants.SEARCH_ENTRY_DATE_INDEX_FIELD,
			"Improperly formed advanced find entry date type in bulletin query: ");		
	}	
	
	static Query queryLanguage(HashMap fields)
			throws Exception
	{
		Query query = null;
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_LANGUAGE_KEY);

		if (fieldString != null)				
			query = queryParser(fieldString,SearchConstants.SEARCH_LANGUAGE_INDEX_FIELD, "Improperly formed advanced find language type in bulletin query: ");
		
		return query;
	} 	

	static Query queryAnyWords(HashMap fields) throws Exception
	{
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);
		String queryString = (String) fields.get(SearchResultConstants.ANYWORD_TAG);

		return queryEachField(queryString, fieldString);
	}

	static Query queryTheseWords(HashMap fields) throws Exception
	{
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);
		String queryString = (String) fields.get(SearchResultConstants.THESE_WORD_TAG);

		return queryEachField(queryString, fieldString);
	}

	static Query queryExactPhrase(HashMap fields) throws Exception
	{
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);
		String queryString = (String) fields.get(SearchResultConstants.EXACTPHRASE_TAG);

		return queryEachField(queryString, fieldString);
	}	

	static Query queryWithoutWords(HashMap fields) throws Exception
	{
		String fieldString = (String) fields.get(SearchResultConstants.RESULT_FIELDS_KEY);
		String queryString = (String) fields.get(SearchResultConstants.WITHOUTWORDS_TAG);

		return queryEachField(queryString, fieldString);
	}	

	static Query queryEachField(String queryString, String fieldString)
		throws Exception
	{
		if (queryString == null || queryString.length() <= 1)
			return null;
			
		if (fieldString.equals(SearchResultConstants.IN_ALL_FIELDS))
			return multiFieldQueryParser(queryString, SearchConstants.SEARCH_ALL_TEXT_FIELDS, "Improperly formed advanced find bulletin multiquery: ");
						
		return queryParser(queryString, fieldString, "Improperly formed advanced find bulletin query: ");
	}				
	
	static Query complexSearch(HashMap fields) throws Exception
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
			
		Query foundLanguageQuery = queryLanguage(fields);
		if (foundLanguageQuery != null)
			query.add(foundLanguageQuery, true, false);
			
		Query foudEntryDateQuery = queryEntryDate(fields);

		if (foudEntryDateQuery != null)
			query.add(foudEntryDateQuery, true, false);			
			
		return query;	
	}			

	static Query multiFieldQueryParser(String query, String[] fields, String msg)
			throws Exception 
	{
		return MultiFieldQueryParser.parse(query, fields, getAnalyzer());
	}
	
	static Query queryParser(String query, String field, String msg)
			throws Exception 
	{
		return QueryParser.parse(query, field, getAnalyzer());
	}

	static String setRangeQuery(String from, String to)
	{
		return "[ " + from + " TO " + to + " ]";
	}
	
	static String getFieldQuery(String fieldTag, String query)
	{
		return fieldTag + ":" + query;
	}
		
	static Analyzer getAnalyzer()
	{
		return LuceneBulletinIndexer.getAnalyzer();
	}

	final static String AND = " AND ";
	private static final String ERROR_PARSING_QUERY = "Improperly formed query: ";
	private static final String ERROR_PARSING_MULTIQUERY = "Improperly formed multiquery: ";

	Query query;
}
