package org.martus.amplifier.test.search;

import junit.framework.Assert;

import org.apache.lucene.search.Hits;
import org.martus.amplifier.service.search.BulletinSearcher;

/**
 * @author Daniel Chu
 *
 * Unit Test for basic bulletin text searching
 *  
 */
public class BulletinSearcherTest extends AbstractAmplifierSearchTest {
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
		
		Hits hits = bulletinSearch.fieldSearch(AUTHOR_FIELD, "Sri Lanka Peace Institute");
		System.out.println(hits.length());
		Assert.assertEquals("Found author?", 1, hits.length());

		hits = bulletinSearch.fieldSearch(KEYWORDS_FIELD, "explosion");
		System.out.println(hits.length());
		Assert.assertEquals("Found keyword?", 1, hits.length());

		hits = bulletinSearch.fieldSearch(TITLE_FIELD, "NGO Office");
		System.out.println(hits.length());
		Assert.assertEquals("Found title?", 1, hits.length());
		
//		hits = bulletinSearch.fieldSearch(EVENT_DATE_FIELD, "2001-02-03");
//		System.out.println(hits.length());
//		Assert.assertEquals("Found event date?", 1, hits.length());

		hits = bulletinSearch.fieldSearch(PUBLIC_INFO_FIELD, "staff members were");
		System.out.println(hits.length());
		Assert.assertEquals("Found public info?", 1, hits.length());

		hits = bulletinSearch.fieldSearch(SUMMARY_FIELD, "attacked by men");
		System.out.println(hits.length());
		Assert.assertEquals("Found summary?", 1, hits.length());

		hits = bulletinSearch.fieldSearch(LOCATION_FIELD, "Colombo");
		System.out.println(hits.length());
		Assert.assertEquals("Found location?", 1, hits.length());

//		hits = bulletinSearch.fieldSearch(ENTRY_DATE_FIELD, "2001-02-05");
//		System.out.println(hits.length());
//		Assert.assertEquals("Found entry date?", 1, hits.length());
	}
}
