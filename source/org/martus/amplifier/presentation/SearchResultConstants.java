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

package org.martus.amplifier.presentation;


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
	final String RESULT_FIELD_KEY		= "field";
	final String RESULT_BASIC_QUERY_KEY	= "query";
	final String RESULT_ADVANCED_QUERY_KEY = "advancedQuery";
	
	final String[] ADVANCED_KEYS = new String[] {
		RESULT_START_YEAR_KEY, RESULT_START_MONTH_KEY, RESULT_START_DAY_KEY,
		RESULT_END_YEAR_KEY, RESULT_END_MONTH_KEY, RESULT_END_DAY_KEY,
		RESULT_FILTER_BY_KEY, RESULT_FIELDS_KEY, RESULT_LANGUAGE_KEY, RESULT_ENTRY_DATE_KEY};		
}
