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

package org.martus.amplifier.common;


public interface SearchResultConstants
{
	final String RESULT_START_YEAR_KEY 	= "startYear";
	final String RESULT_START_MONTH_KEY = "startMonth";
	final String RESULT_START_DAY_KEY 	= "startDay";
	final String RESULT_END_YEAR_KEY  	= "endYear";
	final String RESULT_END_MONTH_KEY 	= "endMonth";
	final String RESULT_END_DAY_KEY 	= "endDay";
	final String RESULT_FILTER_BY_KEY 	= "filterBy";
	final String RESULT_LANGUAGE_KEY 	= "language";
	final String RESULT_FIELDS_KEY		= "fields";
	final String RESULT_ENTRY_DATE_KEY	= "entryDate";
	final String RESULT_SORTBY_KEY		= "sortBy";
	final String RESULT_BASIC_FIELD_KEY	= "field";
	final String RESULT_BASIC_QUERY_KEY	= "query";
	final String RESULT_ADVANCED_QUERY_KEY = "advancedQuery";
	
	final static String THESE_WORD_KEY 		= "with all of these words";
	final static String EXACTPHRASE_KEY 	= "with this exact phrase";
	final static String ANYWORD_KEY 		= "with any of these words";
	final static String WITHOUTWORDS_KEY  	= "without any of these words";
	
	final static String THESE_WORD_LABEL 		= "these words";
	final static String EXACTPHRASE_LABEL 		= "exact phrase";
	final static String ANYWORD_LABEL 			= "any words";
	final static String WITHOUTWORDS_LABEL  	= "without words";
	
	final static String ENTRY_ANYTIME_KEY		= "any time";
	final static String ENTRY_PAST_WEEK_KEY		= "week";
	final static String ENTRY_PAST_MONTH_KEY	= "month";
	final static String ENTRY_PAST_3_MONTH_KEY  = "3 months";
	final static String ENTRY_PAST_6_MONTH_KEY	= "6 months";
	final static String ENTYR_PAST_YEAR_KEY		= "year";
	
	final static String ENTRY_ANYTIME_LABEL				= "0";
	final static String ENTRY_PAST_WEEK_DAYS_LABEL		= "7";
	final static String ENTRY_PAST_MONTH_DAYS_LABEL		= "30";
	final static String ENTRY_PAST_3_MONTH_DAYS_LABEL	= "90";
	final static String ENTRY_PAST_6_MONTH_DAYS_LABEL	= "180";
	final static String ENTRY_PAST_YEAR_DAYS_LABEL		= "365";	
	
	final static String ANYWHERE_IN_BULLETIN_KEY= "anywhere";
	final static String IN_TITLE_KEY			= "title";
	final static String IN_KEYWORDS_KEY			= "keywords";
	final static String IN_SUMMARY_KEY			= "summary";
	final static String IN_AUTHOR_KEY			= "author";
	final static String IN_DETAIL_KEY			= "detail of event";
	final static String IN_ORGANIZATION_KEY		= "organization";
	final static String IN_LOCATION_KEY			= "location of event";	
	final static String IN_ALL_FIELDS			= "all";	
	
	final static String LANGUAGE_ANYLANGUAGE_KEY= "any language";
	final static String LANGUAGE_ENGLISH_KEY	= "English";
	final static String LANGUAGE_FRENCH_KEY		= "French";
	final static String LANGUAGE_GERMAN_KEY		= "German";
	final static String LANGUAGE_INDONESIAN_KEY	= "Indonesian";
	final static String LANGUAGE_RUSSIAN_KEY	= "Russian";
	final static String LANGUAGE_SPANISH_KEY	= "Spanish";
	
	final static String SORT_BY_TITLE_LABEL		= "title";
	final static String SORT_BY_AUTHOR_LABEL	= "author";
	final static String SORT_BY_LOCATION_LABEL	= "location";
	final static String SORT_BY_EVENTDATE_LABEL	= "event date";
	final static String SORT_BY_ORGANIZATION_LABEL= "organization";
		
	
	final String[] ADVANCED_KEYS = new String[] {
		RESULT_START_YEAR_KEY, RESULT_START_MONTH_KEY, RESULT_START_DAY_KEY,
		RESULT_END_YEAR_KEY, RESULT_END_MONTH_KEY, RESULT_END_DAY_KEY,
		RESULT_FIELDS_KEY, RESULT_FILTER_BY_KEY, RESULT_LANGUAGE_KEY, RESULT_ENTRY_DATE_KEY,
		RESULT_SORTBY_KEY };		
}
