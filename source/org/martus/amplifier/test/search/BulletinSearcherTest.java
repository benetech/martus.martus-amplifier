package org.martus.amplifier.test.search;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.apache.lucene.search.Hits;
import org.martus.amplifier.service.search.BulletinSearcher;


/**
 * @author Daniel Chu
 *
 * Unit Test for basic bulletin text searching
 *  
 */
public class BulletinSearcherTest extends AbstractAmplifierSearchTest
{
	public BulletinSearcherTest(String name)
	{
		super(name);
	}
	
	public void testBasicTextSearch()
	{
//		BulletinSearcher bulletinSearch = BulletinSearcher.getInstance();
//		Hits hits = bulletinSearch.textSearch("Oxfam");
//		System.out.println(hits.length());
//		Assert.assertNotNull(hits);
	}
	public void testNumberOfHits()
	{
//		BulletinSearcher bulletinSearch = BulletinSearcher.getInstance();
//		Hits hits = bulletinSearch.textSearch("Oxfam");
//		System.out.println(hits.length());
//		Assert.assertEquals(hits.length(), 1);
	}
	public void testBasicFieldSearch()
	{
		BulletinSearcher bulletinSearch = BulletinSearcher.getInstance();
		
		Hits hits = bulletinSearch.searchField(AUTHOR_FIELD, "Sri Lanka Peace Institute");
		System.out.println(hits.length());
		Assert.assertEquals("Found author?", 1, hits.length());

		hits = bulletinSearch.searchField(KEYWORDS_FIELD, "explosion");
		System.out.println(hits.length());
		Assert.assertEquals("Found keyword?", 1, hits.length());

		hits = bulletinSearch.searchField(TITLE_FIELD, "NGO Office");
		System.out.println(hits.length());
		Assert.assertEquals("Found title?", 1, hits.length());
		
		//To test Dates
		Calendar sDate = new GregorianCalendar(2001, Calendar.FEBRUARY, 01);
        Date startDate = sDate.getTime();
        Calendar eDate = new GregorianCalendar(2001, Calendar.FEBRUARY, 03);
        Date endDate = eDate.getTime();
        
      	hits = bulletinSearch.searchDateRange(EVENT_DATE_FIELD, startDate, endDate);
		System.out.println(hits.length());
		Assert.assertEquals("Found event date?", 1, hits.length());

		hits = bulletinSearch.searchField(PUBLIC_INFO_FIELD, "staff members were");
		System.out.println(hits.length());
		Assert.assertEquals("Found public info?", 1, hits.length());

		hits = bulletinSearch.searchField(SUMMARY_FIELD, "attacked by men");
		System.out.println(hits.length());
		Assert.assertEquals("Found summary?", 1, hits.length());

		hits = bulletinSearch.searchField(LOCATION_FIELD, "Colombo");
		System.out.println(hits.length());
		Assert.assertEquals("Found location?", 1, hits.length());

//		hits = bulletinSearch.fieldSearch(ENTRY_DATE_FIELD, "2001-02-05");
//		System.out.println(hits.length());
//		Assert.assertEquals("Found entry date?", 1, hits.length());
	}
}
