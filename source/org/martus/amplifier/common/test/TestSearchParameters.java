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

package org.martus.amplifier.common.test;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.martus.amplifier.common.SearchParameters;
import org.martus.amplifier.common.SearchResultConstants;

public class TestSearchParameters extends TestCase
{

	public TestSearchParameters(String name)
	{
		super(name);
	}
	
	public void testGetDate()
	{
		int year = 2003;
		int month = 11;
		int day = 25;
		Date result = SearchParameters.getDate(year, month, day);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(result);
		assertEquals(year, cal.get(GregorianCalendar.YEAR));
		assertEquals(month, cal.get(GregorianCalendar.MONTH));
		assertEquals(day, cal.get(GregorianCalendar.DAY_OF_MONTH));
	}
	
	public void testAllWordsFormatter() throws Exception
	{
		SearchParameters.FormatterForAllWordsSearch d = 
			new SearchParameters.FormatterForAllWordsSearch();
		assertEquals("(+cat +dog )", d.getFormattedString("cat dog"));		
	}

	public void testAnyWordFormatter() throws Exception
	{
		SearchParameters.FormatterForAnyWordSearch d = 
			new SearchParameters.FormatterForAnyWordSearch();
		assertEquals("(cat dog)", d.getFormattedString("cat dog"));		
	}

	public void testExactPhraseFormatter() throws Exception
	{
		SearchParameters.FormatterForExactPhraseSearch d = 
			new SearchParameters.FormatterForExactPhraseSearch();
		assertEquals("\"cat dog\"", d.getFormattedString("cat dog"));		
	}
	
	public void testLuceneQueryFormatter() throws Exception
	{
		Map destination = new HashMap();
		Map source = new HashMap();
		
		SearchParameters.FormatterForAnyWordSearch d = 
			new SearchParameters.FormatterForAnyWordSearch();

		source.put(SearchResultConstants.ANYWORD_TAG, "");
		d.addFormattedStringIfNotEmpty(destination, source);
		assertNull("Added it when blank?", destination.get(SearchResultConstants.ANYWORD_TAG));		
		
		source.put(SearchResultConstants.ANYWORD_TAG, "cat dog");
		d.addFormattedStringIfNotEmpty(destination, source);
		assertEquals("(cat dog)", destination.get(SearchResultConstants.ANYWORD_TAG));
	}
}
