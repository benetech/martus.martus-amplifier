package org.martus.amplifier.lucene.test;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.martus.amplifier.attachment.FileSystemAttachmentManager;
import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.amplifier.common.SearchParameters;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.amplifier.search.AttachmentInfo;
import org.martus.amplifier.search.BulletinField;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.search.BulletinSearcher;
import org.martus.amplifier.search.SearchConstants;
import org.martus.amplifier.test.AbstractAmplifierTestCase;
import org.martus.common.FieldSpec;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;
import org.martus.util.DirectoryTreeRemover;

public abstract class AbstractSearchTestCase 
	extends AbstractAmplifierTestCase implements SearchConstants, SearchResultConstants
{
	public void setUp() throws Exception
	{
		String basePath = AmplifierConfiguration.getInstance().getBasePath() + "/testing";
		MartusAmplifier.attachmentManager = new FileSystemAttachmentManager(basePath);
	}
	
	public void tearDown() throws Exception
	{
		MartusAmplifier.attachmentManager.clearAllAttachments();
		String basePath = AmplifierConfiguration.getInstance().getBasePath() + "/testing";
		DirectoryTreeRemover.deleteEntireDirectoryTree(new File(basePath));
	}
	
	public void testClearIndex() 
		throws BulletinIndexException
	{
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateFieldDataPacket(bulletinId);
		BulletinIndexer indexer = openBulletinIndexer();
		try {
			indexer.indexFieldData(bulletinId, fdp);
		} finally {
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		try {
			Assert.assertNotNull(
				"Didn't find indexed bulletin", 
				searcher.lookup(bulletinId));
		} finally {
			searcher.close();
		}
		
		indexer = openBulletinIndexer();
		try {
			indexer.clearIndex();
		} finally {
			indexer.close();
		}
		
		searcher = openBulletinSearcher();
		try {
			Assert.assertNull(
				"Found an indexed bulletin after clearing!", 
				searcher.lookup(bulletinId));
		} finally {
			searcher.close();
		} 		
	}
	
	public void testFindBulletin() 
		throws BulletinIndexException
	{
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateFieldDataPacket(bulletinId);
		BulletinIndexer indexer = openBulletinIndexer();
		try {
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId, fdp);
		} finally {
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		try {
			Assert.assertNotNull(
				"Didn't find indexed bulletin", 
				searcher.lookup(bulletinId));
		} finally {
			searcher.close();
		}
	}
	
	public void testIndexAndSearch() 
		throws BulletinIndexException, ParseException
	{
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateSampleData(bulletinId);		
		BulletinIndexer indexer = openBulletinIndexer();
		try {
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId, fdp);
		} finally {
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		try {
			BulletinInfo found = searcher.lookup(bulletinId);
			Assert.assertNotNull("Didn't find indexed bulletin", found);
				
			HashMap fields = new HashMap();			
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(SEARCH_AUTHOR_INDEX_FIELD) );
			Assert.assertEquals(1, searcher.search(SEARCH_AUTHOR_INDEX_FIELD, fields).getCount());
			
			fields = new HashMap();	
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(SEARCH_KEYWORDS_INDEX_FIELD));
			Assert.assertEquals(1,searcher.search(SEARCH_KEYWORDS_INDEX_FIELD, fields).getCount());
			
			fields = new HashMap();
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(SEARCH_DETAILS_INDEX_FIELD));											
			Assert.assertEquals(1, searcher.search(SEARCH_DETAILS_INDEX_FIELD , fields).getCount());
		} finally {
			searcher.close();
		}
		
	}
	
	public void testReconstructFieldDataPacket()
		throws BulletinIndexException
	{
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateSampleData(bulletinId);		
		BulletinIndexer indexer = openBulletinIndexer();
		try {
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId, fdp);
		} finally {
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		try {
			BulletinInfo found = searcher.lookup(bulletinId);
			Assert.assertNotNull(
				"Didn't find indexed bulletin", 
				found);
			
			AttachmentProxy[] origProxies = fdp.getAttachments();
			List foundAttachments = found.getAttachments();
			Assert.assertEquals(
				origProxies.length, foundAttachments.size());
			for (int i = 0; i < origProxies.length; i++) {
				Assert.assertEquals(
					origProxies[i].getUniversalId().getLocalId(), 
					((AttachmentInfo) foundAttachments.get(i)).getLocalId());	
				Assert.assertEquals(
					origProxies[i].getLabel(), 
					((AttachmentInfo) foundAttachments.get(i)).getLabel());
			}
			
			Assert.assertEquals(
				bulletinId, found.getBulletinId());
			Collection fields = BulletinField.getSearchableFields();
			for (Iterator iter = fields.iterator(); iter.hasNext();) {
				BulletinField field = (BulletinField) iter.next();
				Assert.assertEquals(
					fdp.get(field.getXmlId()), 
					found.get(field.getIndexId()));
			}
		} finally {
			searcher.close();
		}
	}
	
	public void testInterleavedAccess() throws BulletinIndexException
	{
		BulletinIndexer indexer = null;
		BulletinSearcher searcher = null;
		BulletinIndexException closeException = null;
		
		try {
			indexer = openBulletinIndexer();
			searcher = openBulletinSearcher();
		} finally {
			if (indexer != null) {
				try {
					indexer.close();
				} catch (BulletinIndexException e) {
					closeException = e;
				}
			}
			if (searcher != null) {
				try {
					searcher.close();
				} catch (BulletinIndexException e) {
					closeException = e;
				}
			}
		}
		if (closeException != null) {
			throw closeException;
		}
			
		
	}
	
	public void testSearchResultsAfterClose() throws BulletinIndexException
	{
		UniversalId bulletinId 	= UniversalId.createDummyUniversalId();
		FieldDataPacket fdp 	= generateSampleData(bulletinId);		
		BulletinIndexer indexer = openBulletinIndexer();
		try {
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId, fdp);
		} finally {
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;
		try {
			HashMap fields = new HashMap();			
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(SEARCH_AUTHOR_INDEX_FIELD) );
			results = searcher.search(SEARCH_AUTHOR_INDEX_FIELD, fields);
		} finally {
			searcher.close();
		}
		
		try {
			results.getBulletinInfo(0);
			Assert.fail(
				"Accessing results after closing searcher should have failed.");
		} catch (BulletinIndexException expected) {
		}
	}
	
	public void testSearchAllFields() throws BulletinIndexException
	{
		UniversalId bulletinId 	= UniversalId.createDummyUniversalId();
		FieldDataPacket fdp 	= generateSampleData(bulletinId);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId, fdp);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;
		try 
		{
			HashMap fields = new HashMap();
			fields.put(SEARCH_AUTHOR_INDEX_FIELD, fdp.get(BulletinField.SEARCH_AUTHOR_INDEX_FIELD));				
			fields.put(SEARCH_DETAILS_INDEX_FIELD, fdp.get(BulletinField.SEARCH_DETAILS_INDEX_FIELD));
			fields.put(SEARCH_KEYWORDS_INDEX_FIELD, fdp.get(BulletinField.SEARCH_KEYWORDS_INDEX_FIELD));
			fields.put(SEARCH_LOCATION_INDEX_FIELD, fdp.get(BulletinField.SEARCH_LOCATION_INDEX_FIELD));
			fields.put(SEARCH_SUMMARY_INDEX_FIELD, fdp.get(BulletinField.SEARCH_SUMMARY_INDEX_FIELD));
			fields.put(SEARCH_TITLE_INDEX_FIELD, fdp.get(BulletinField.SEARCH_TITLE_INDEX_FIELD));
			
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(BulletinField.SEARCH_AUTHOR_INDEX_FIELD));								
			results = searcher.search(null, fields);							
			assertEquals("Should have found a result for author", 1, results.getCount());
			
			fields.remove(RESULT_BASIC_QUERY_KEY);
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(SEARCH_DETAILS_INDEX_FIELD));								
			results = searcher.search(null, fields);							
			assertEquals("Should have found a result for details", 1, results.getCount());
						
			fields.remove(RESULT_BASIC_QUERY_KEY);
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(SEARCH_KEYWORDS_INDEX_FIELD));								
			results = searcher.search(null, fields);	
			assertEquals("Should have found a result for keyword", 1, results.getCount());
			
			fields.remove(RESULT_BASIC_QUERY_KEY);
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(SEARCH_LOCATION_INDEX_FIELD));								
			results = searcher.search(null, fields);				
			assertEquals("Should have found a result for location", 1, results.getCount());
			
			
			fields.remove(RESULT_BASIC_QUERY_KEY);
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(SEARCH_SUMMARY_INDEX_FIELD));								
			results = searcher.search(null, fields);				
			assertEquals("Should have found a result for summary", 1, results.getCount());
			
			fields.remove(RESULT_BASIC_QUERY_KEY);
			fields.put(RESULT_BASIC_QUERY_KEY, fdp.get(SEARCH_TITLE_INDEX_FIELD));								
			results = searcher.search(null, fields);		
			assertEquals("Should have found a result for title", 1, results.getCount());
			
			
			fields.remove(RESULT_BASIC_QUERY_KEY);
			fields.put(RESULT_BASIC_QUERY_KEY, "Lunch");								
			results = searcher.search(null, fields);			
			assertEquals("Should have found a result for the word Lunch", 1, results.getCount());
			
			fields.remove(RESULT_BASIC_QUERY_KEY);
			fields.put(RESULT_BASIC_QUERY_KEY, "Luch");								
			results = searcher.search(null, fields);	
			assertEquals("Should not have found a result for a word 'Luch' not in the bulletin", 0, results.getCount());
			
		} 
		finally 
		{
			searcher.close();
		}
	}

	public void testSearchForStopWords() throws BulletinIndexException
	{
		UniversalId bulletinId 	= UniversalId.createDummyUniversalId();
		FieldDataPacket fdp 	= generateSampleData(bulletinId);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId, fdp);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;
		try 
		{			
			HashMap fields = new HashMap();
			fields.put(SEARCH_AUTHOR_INDEX_FIELD, fdp.get(BulletinField.SEARCH_AUTHOR_INDEX_FIELD));				
			fields.put(SEARCH_DETAILS_INDEX_FIELD, fdp.get(BulletinField.SEARCH_DETAILS_INDEX_FIELD));
			fields.put(SEARCH_KEYWORDS_INDEX_FIELD, fdp.get(BulletinField.SEARCH_KEYWORDS_INDEX_FIELD));
			fields.put(SEARCH_LOCATION_INDEX_FIELD, fdp.get(BulletinField.SEARCH_LOCATION_INDEX_FIELD));
			fields.put(SEARCH_SUMMARY_INDEX_FIELD, fdp.get(BulletinField.SEARCH_SUMMARY_INDEX_FIELD));
			fields.put(SEARCH_TITLE_INDEX_FIELD, fdp.get(BulletinField.SEARCH_TITLE_INDEX_FIELD));
		
			fields.put(RESULT_BASIC_QUERY_KEY, "for");								
					
			results = searcher.search(SEARCH_TITLE_INDEX_FIELD, fields);
			assertEquals("Should have found 1 result for stopword 'for'", 1, results.getCount());
		} 
		finally 
		{
			searcher.close();
		}
	}

	public void testSearchForWildCards() throws BulletinIndexException
	{
		UniversalId bulletinId1 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp1 	= generateSampleData(bulletinId1);		
		UniversalId bulletinId2 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp2 	= generateSampleFlexiData(bulletinId2);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId1, fdp1);
			indexer.indexFieldData(bulletinId2, fdp2);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;
		try 
		{
			HashMap fields = new HashMap();
			fields.put(SEARCH_AUTHOR_INDEX_FIELD, fdp1.get(BulletinField.SEARCH_AUTHOR_INDEX_FIELD));				
			fields.put(SEARCH_DETAILS_INDEX_FIELD, fdp1.get(BulletinField.SEARCH_DETAILS_INDEX_FIELD));
			fields.put(SEARCH_KEYWORDS_INDEX_FIELD, fdp1.get(BulletinField.SEARCH_KEYWORDS_INDEX_FIELD));
			fields.put(SEARCH_LOCATION_INDEX_FIELD, fdp1.get(BulletinField.SEARCH_LOCATION_INDEX_FIELD));
			fields.put(SEARCH_SUMMARY_INDEX_FIELD, fdp1.get(BulletinField.SEARCH_SUMMARY_INDEX_FIELD));
			fields.put(SEARCH_TITLE_INDEX_FIELD, fdp1.get(BulletinField.SEARCH_TITLE_INDEX_FIELD));
		
			fields.put(RESULT_BASIC_QUERY_KEY, "lun??");						
			results = searcher.search(null, fields);
			assertEquals("Should have found 2 result lun??", 2, results.getCount());
			
			
			fields.remove(RESULT_BASIC_QUERY_KEY);
			fields.put(RESULT_BASIC_QUERY_KEY, "sal*");	
			results = searcher.search(null, fields);
			assertEquals("Should have found 2 result sal* salad and salad2", 2, results.getCount());
			
			
			fields.remove(RESULT_BASIC_QUERY_KEY);
			fields.put(RESULT_BASIC_QUERY_KEY, "sa?ad");	
			results = searcher.search(null, fields);		
			assertEquals("Should have found 1 result sa?ad just salad", 1 , results.getCount());
			
			
/*			results = searcher.search(null, "");
			assertEquals("Should have found 2 result for nothing entered", 2, results.getCount());
			results = searcher.search(null, null);
			assertEquals("Should have found 2 result for null entered", 2, results.getCount());
			results = searcher.search(null, "*");
			assertEquals("Should have found 2 result for * entered", 2, results.getCount());
			results = searcher.search(null, "?");
			assertEquals("Should have found 2 result for ? entered", 2, results.getCount());
*/		}
		finally 
		{
			searcher.close();
		}
	}

	public void testSearchEmptyField() throws BulletinIndexException
	{
		UniversalId bulletinId 	= UniversalId.createDummyUniversalId();
		FieldDataPacket fdp 	= generateSampleFlexiData(bulletinId);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId, fdp);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;
		try 
		{
			HashMap fields = new HashMap();
			fields.put(SEARCH_AUTHOR_INDEX_FIELD, fdp.get(BulletinField.SEARCH_AUTHOR_INDEX_FIELD));				
			fields.put(SEARCH_DETAILS_INDEX_FIELD, fdp.get(BulletinField.SEARCH_DETAILS_INDEX_FIELD));
			fields.put(SEARCH_KEYWORDS_INDEX_FIELD, fdp.get(BulletinField.SEARCH_KEYWORDS_INDEX_FIELD));
			fields.put(SEARCH_LOCATION_INDEX_FIELD, fdp.get(BulletinField.SEARCH_LOCATION_INDEX_FIELD));
			fields.put(SEARCH_SUMMARY_INDEX_FIELD, fdp.get(BulletinField.SEARCH_SUMMARY_INDEX_FIELD));
			fields.put(SEARCH_TITLE_INDEX_FIELD, fdp.get(BulletinField.SEARCH_TITLE_INDEX_FIELD));
		
			fields.put(RESULT_BASIC_QUERY_KEY, "Chuck");		
			
			results = searcher.search(SEARCH_SUMMARY_INDEX_FIELD, fields);
			assertEquals("Should have found 1 result Chuck", 1, results.getCount());
			
			
			BulletinInfo info =results.getBulletinInfo(0);
			assertNotNull("Bulletin Info null?", info);
			assertNotNull("Sumary should not be null",info.get(SEARCH_SUMMARY_INDEX_FIELD));
			assertNotNull("Location should not be  null",info.get(SEARCH_LOCATION_INDEX_FIELD));
			assertEquals("Location should be ''", "", info.get(SEARCH_LOCATION_INDEX_FIELD));
		}
		finally 
		{
			searcher.close();
		}
	}

	
	public void testAdvancedSearchEventDateOnly() throws BulletinIndexException,ParseException
	{
		UniversalId bulletinId1 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp1 	= generateSampleData(bulletinId1);		
		UniversalId bulletinId2 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp2 	= generateSampleFlexiData(bulletinId2);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId1, fdp1);
			indexer.indexFieldData(bulletinId2, fdp2);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;				
		
		try 
		{
			Date startDate 	= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-01");
			Date endDate 	= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-25");
		
			HashMap fields = new HashMap();
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, startDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, endDate);
			
			results = searcher.search(null, fields);
			assertEquals("Should have found 1 match? ", 1, results.getCount());			
		}
		finally 
		{
			searcher.close();
		}
	}	
	
	public void testAdvancedSearchCombineEventDateAndBulletineField() throws BulletinIndexException,ParseException
	{
		UniversalId bulletinId1 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp1 = generateSampleData(bulletinId1);		
		UniversalId bulletinId2 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp2 = generateSampleFlexiData(bulletinId2);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId1, fdp1);
			indexer.indexFieldData(bulletinId2, fdp2);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;				
		
		try 
		{
			Date startDate 	= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-01");
			Date endDate 	= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-22");
		
			HashMap fields = new HashMap();
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, startDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, endDate);
			fields.put(SearchResultConstants.RESULT_FIELDS_KEY, BulletinField.SEARCH_TITLE_INDEX_FIELD);
			fields.put(SearchResultConstants.RESULT_ADVANCED_QUERY_KEY, "lunch");
			
			results = searcher.search(null, fields);
			assertEquals("Combine search for eventdate and field? ", 1, results.getCount());
		}
		finally 
		{
			searcher.close();
		}
	}
	
	public void testAdvancedSearchCombineEventDateAndEntryDate() throws BulletinIndexException,ParseException
	{
		UniversalId bulletinId1 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp1 	= generateSampleData(bulletinId1);		
		UniversalId bulletinId2 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp2 	= generateSampleFlexiData(bulletinId2);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId1, fdp1);
			indexer.indexFieldData(bulletinId2, fdp2);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;				
		
		try 
		{
			Date startDate		= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-01");
			Date endDate 		= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-22");
			Date defaultDate 	= SearchConstants.SEARCH_DATE_FORMAT.parse("1970-01-01");			
			Date entryStartDate = SearchConstants.SEARCH_DATE_FORMAT.parse("2003-05-22");		
		
			HashMap fields = new HashMap();
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, defaultDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, new GregorianCalendar().getTime());
			fields.put(BulletinField.SEARCH_ENTRY_DATE_INDEX_FIELD, entryStartDate);
			
			results = searcher.search(null, fields);
			assertEquals("search for entry date only? ", 1, results.getCount());
			
			fields.remove(SEARCH_EVENT_START_DATE_INDEX_FIELD);
			fields.remove(SEARCH_EVENT_END_DATE_INDEX_FIELD);
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, startDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, endDate);			
			
			results = searcher.search(null, fields);
			assertEquals("Combine search for eventdate and entry date? ", 1, results.getCount());
		}
		finally 
		{
			searcher.close();
		}
	}
	
	public void testAdvancedSearchCombineEventDateAndBulletineFieldAndLanguage() throws BulletinIndexException,ParseException
	{
		UniversalId bulletinId1 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp1 	= generateSampleData(bulletinId1);		
		UniversalId bulletinId2 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp2 	= generateSampleFlexiData(bulletinId2);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId1, fdp1);
			indexer.indexFieldData(bulletinId2, fdp2);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;				
		
		try 
		{
			Date startDate 			= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-01");
			Date endDate 			= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-22");			
			Date defaultStartDate	= SearchConstants.SEARCH_DATE_FORMAT.parse("1970-01-01");
			Date defaultEndDate		= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-09-24");
				
			HashMap fields = new HashMap();
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, defaultStartDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, defaultEndDate);		
		
			fields.put(BulletinField.SEARCH_LANGUAGE_INDEX_FIELD, "es");		
			results = searcher.search(null, fields);			
			assertEquals("search laguage with default event date? ", 1, results.getCount());
				
			fields = new HashMap();			
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, startDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, endDate);
			fields.put(SearchResultConstants.RESULT_FIELDS_KEY, BulletinField.SEARCH_TITLE_INDEX_FIELD);
			fields.put(BulletinField.SEARCH_LANGUAGE_INDEX_FIELD, "en");
			fields.put(SearchResultConstants.RESULT_ADVANCED_QUERY_KEY, "lunch");
			
			results = searcher.search(null, fields);
			assertEquals("Combine search for eventdate, field, and laguage? ", 0, results.getCount());
			
			fields.remove(SEARCH_LANGUAGE_INDEX_FIELD);
			fields.put(BulletinField.SEARCH_LANGUAGE_INDEX_FIELD, "fr");
			results = searcher.search(null, fields);
			assertEquals("Combine search for eventdate, bulletin field, and language (not match)? ", 0, results.getCount());			
						
		}
		finally 
		{
			searcher.close();
		}
	}
	
	public void testAdvancedSearchCombineEventDateAndBulletineFieldAndLanguageAndEntryDate() throws BulletinIndexException,ParseException
	{
		UniversalId bulletinId1 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp1 = generateSampleData(bulletinId1);		
		UniversalId bulletinId2 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp2 = generateSampleFlexiData(bulletinId2);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId1, fdp1);
			indexer.indexFieldData(bulletinId2, fdp2);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;				
		
		try 
		{
			Date startDate 			= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-01");
			Date endDate 			= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-08-22");			
			Date defaultStartDate 	= SearchConstants.SEARCH_DATE_FORMAT.parse("1970-01-01");		
			Date todayDate 			= SearchConstants.SEARCH_DATE_FORMAT.parse("2003-09-24");
			
			Date pastWeek 	= SearchParameters.getEntryDate(ENTRY_PAST_WEEK_DAYS_LABEL);
			Date pastMonth 	= SearchParameters.getEntryDate(ENTRY_PAST_MONTH_DAYS_LABEL);
			Date past3Month = SearchParameters.getEntryDate(ENTRY_PAST_3_MONTH_DAYS_LABEL);
			Date past6Month = SearchParameters.getEntryDate(ENTRY_PAST_6_MONTH_DAYS_LABEL);
			Date pastYear 	= SearchParameters.getEntryDate(ENTRY_PAST_YEAR_DAYS_LABEL);
		
			HashMap fields = new HashMap();
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, defaultStartDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, todayDate);		
			
			//2003-05-11 and 2003-08-30
			fields.put(BulletinField.SEARCH_ENTRY_DATE_INDEX_FIELD, pastWeek);		
			results = searcher.search(null, fields);			
			assertEquals("search for entry date submitted in past 1 week? ", 0, results.getCount());
						
			fields.remove(SEARCH_ENTRY_DATE_INDEX_FIELD);
			fields.put(SEARCH_ENTRY_DATE_INDEX_FIELD, pastMonth);
			results = searcher.search(null, fields);			
			assertEquals("search for entry date submitted in past 1 month? ", 1, results.getCount());
			
			fields.remove(SEARCH_ENTRY_DATE_INDEX_FIELD);
			fields.put(SEARCH_ENTRY_DATE_INDEX_FIELD, past3Month);
			results = searcher.search(null, fields);			
			assertEquals("search for entry date submitted in past 3 month? ", 1, results.getCount());
			
			fields.remove(SEARCH_ENTRY_DATE_INDEX_FIELD);
			fields.put(SEARCH_ENTRY_DATE_INDEX_FIELD, past6Month);
			results = searcher.search(null, fields);			
			assertEquals("search for entry date submitted in past 6 month? ", 2, results.getCount());
			
			fields.remove(SEARCH_ENTRY_DATE_INDEX_FIELD);
			fields.put(SEARCH_ENTRY_DATE_INDEX_FIELD, pastYear);
			results = searcher.search(null, fields);			
			assertEquals("search for entry date submitted in past 1 year? ", 2, results.getCount());
			
									
			fields = new HashMap();			
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, startDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, endDate);
			fields.put(SearchResultConstants.RESULT_FIELDS_KEY, BulletinField.SEARCH_TITLE_INDEX_FIELD);			
			fields.put(BulletinField.SEARCH_LANGUAGE_INDEX_FIELD, "es");
			fields.put(SEARCH_ENTRY_DATE_INDEX_FIELD, past3Month);
			fields.put(SearchResultConstants.RESULT_ADVANCED_QUERY_KEY, "lunch");
			
			results = searcher.search(null, fields);
			assertEquals("Combine search for eventdate, field, laguage, and event date? ", 1, results.getCount());
								
		}
		finally 
		{
			searcher.close();
		}
	}
	
	public void testAdvancedSearchCombineEventDateAndFilterWords() throws BulletinIndexException,ParseException
	{
		UniversalId bulletinId1 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp1 	= generateSampleData(bulletinId1);		
		UniversalId bulletinId2 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp2 	= generateSampleFlexiData(bulletinId2);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId1, fdp1);
			indexer.indexFieldData(bulletinId2, fdp2);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;				
		
		try 
		{
			Date defaultDate 	= SearchConstants.SEARCH_DATE_FORMAT.parse("1970-01-01");
			Date defaultEndDate = SearchConstants.SEARCH_DATE_FORMAT.parse("2004-01-01");			
		
			HashMap fields = new HashMap();
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, defaultDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, defaultEndDate);
			fields.put(RESULT_FIELDS_KEY, IN_ALL_FIELDS);
			fields.put(RESULT_FILTER_BY_KEY, THESE_WORD_LABEL);
			String query = SearchParameters.convertToQueryString("root sandwich", THESE_WORD_LABEL);			
			fields.put(RESULT_ADVANCED_QUERY_KEY, query);
			
			results = searcher.search(null, fields);
			assertEquals("search for all of these words? ", 2, results.getCount());
			
			fields.remove(RESULT_FILTER_BY_KEY);
			fields.put(RESULT_FILTER_BY_KEY,THESE_WORD_LABEL );
			fields.remove(RESULT_ADVANCED_QUERY_KEY);	
			query = SearchParameters.convertToQueryString("Today Paul", THESE_WORD_LABEL);		
			fields.put(RESULT_ADVANCED_QUERY_KEY, query);
			results = searcher.search(null, fields);
			assertEquals("search for all of these words? ", 1, results.getCount());
			
			fields.remove(RESULT_FILTER_BY_KEY);
			fields.put(RESULT_FILTER_BY_KEY, EXACTPHRASE_LABEL);
			fields.remove(RESULT_ADVANCED_QUERY_KEY);	
			query = SearchParameters.convertToQueryString("egg2 salad2 sandwich", EXACTPHRASE_LABEL);		
			fields.put(RESULT_ADVANCED_QUERY_KEY, query);
			
			results = searcher.search(null, fields);
			assertEquals("search for exact phrase? ", 1, results.getCount());
			
			fields.remove(RESULT_FILTER_BY_KEY);
			fields.put(RESULT_FILTER_BY_KEY, EXACTPHRASE_LABEL);
			fields.remove(RESULT_ADVANCED_QUERY_KEY);	
			query = SearchParameters.convertToQueryString("sandwich", EXACTPHRASE_LABEL);		
			fields.put(RESULT_ADVANCED_QUERY_KEY, query);
			
			results = searcher.search(null, fields);
			assertEquals("search for exact phrase? ", 2, results.getCount());
			
			fields.remove(RESULT_FILTER_BY_KEY);
			fields.put(RESULT_FILTER_BY_KEY, WITHOUTWORDS_LABEL);
			fields.remove(RESULT_ADVANCED_QUERY_KEY);
			query = SearchParameters.convertToQueryString("egg2 salad2", WITHOUTWORDS_LABEL);			
			fields.put(RESULT_ADVANCED_QUERY_KEY, query);
			
			results = searcher.search(null, fields);
//			assertEquals("search for without of those words? ", 1, results.getCount());
			
			fields.remove(RESULT_FILTER_BY_KEY);
			fields.put(RESULT_FILTER_BY_KEY, WITHOUTWORDS_LABEL);
			fields.remove(RESULT_ADVANCED_QUERY_KEY);
			query = SearchParameters.convertToQueryString("Paul", WITHOUTWORDS_LABEL);			
			fields.put(RESULT_ADVANCED_QUERY_KEY, query);
			
			results = searcher.search(null, fields);
//			assertEquals("search for without of those words? ", 1, results.getCount());
										
		}
		finally 
		{
			searcher.close();
		}
	}
	
	public void testAdvancedSearchSortBy() throws BulletinIndexException,ParseException
	{
		UniversalId bulletinId1 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp1 	= generateSampleData(bulletinId1);		
		UniversalId bulletinId2 = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp2 	= generateSampleFlexiData(bulletinId2);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId1, fdp1);
			indexer.indexFieldData(bulletinId2, fdp2);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		BulletinSearcher.Results results = null;				
		
		try 
		{
			Date defaultDate 	= SearchConstants.SEARCH_DATE_FORMAT.parse("1970-01-01");
			Date defaultEndDate = SearchConstants.SEARCH_DATE_FORMAT.parse("2004-01-01");
			
			HashMap fields = new HashMap();			
			fields.put(BulletinField.SEARCH_EVENT_START_DATE_INDEX_FIELD, defaultDate);
			fields.put(BulletinField.SEARCH_EVENT_END_DATE_INDEX_FIELD, defaultEndDate);
			fields.put(RESULT_FIELDS_KEY, IN_ALL_FIELDS);
			fields.put(RESULT_SORTBY_KEY, SEARCH_TITLE_INDEX_FIELD);			
			fields.put(RESULT_ADVANCED_QUERY_KEY, "lunch");			
			
			results = searcher.search(null, fields);
			assertEquals("Should have found 2 matches? ", 2, results.getCount());
			
			int count = results.getCount();												
			ArrayList list = new ArrayList();
			for (int i = 0; i < count; i++)
			{
				BulletinInfo bulletin = results.getBulletinInfo(i);					
				list.add(bulletin);
			}
			
			Collections.sort(list, new Comparator()
			{
			  public int compare(Object o1, Object o2)
			  {
				Object string1 = ((BulletinInfo)o1).get(SEARCH_TITLE_INDEX_FIELD);
				Object string2 = ((BulletinInfo)o2).get(SEARCH_TITLE_INDEX_FIELD);	  			  		
				return ((Comparable)string1).compareTo(string2);
			   }
			});
			
			String title1 = ((BulletinInfo)list.get(0)).get(SEARCH_TITLE_INDEX_FIELD);
			String title2 = ((BulletinInfo)list.get(1)).get(SEARCH_TITLE_INDEX_FIELD);
						
			assertEquals(fdp2.get(BulletinField.SEARCH_TITLE_INDEX_FIELD), title1);
			assertEquals(fdp1.get(BulletinField.SEARCH_TITLE_INDEX_FIELD), title2);
												
		}
		finally 
		{
			searcher.close();
		}
	}				
		
	protected FieldDataPacket generateSampleData(UniversalId bulletinId)
	{
		String author = "Paul";
		String keyword = "ate";
		String keywords = keyword + " egg salad root beer";
		String title = "ZZZ for Lunch?";
		String eventdate = "2003-04-10";
		String entrydate = "2003-05-11";
		String publicInfo = "menu";
		String language = "en";
		String organization = "test sample";
		String summary = 
			"Today Paul ate an egg salad sandwich and a root beer " +
			"for lunch.";
		String location = "San Francisco, CA";
		
		String attachment1LocalId = "att1Id";
		String attachment1Label = "Eggs.gif";
		String attachment2LocalId = "att2Id";
		String attachment2Label = "Recipe.txt";
		
		FieldDataPacket fdp = createFieldDataPacket(bulletinId, author, keywords, title, eventdate, entrydate, publicInfo, summary, location, attachment1LocalId, attachment1Label, attachment2LocalId, attachment2Label, language, organization);
		return fdp;
	}

	protected FieldDataPacket generateSampleFlexiData(UniversalId bulletinId)
	{
		String author = "Chuck";	
		String keywords = "2003-08-20";
		String title = "What's for Lunch??";
		String entrydate= "2003-08-30";
		String eventdate = "2003-08-20,20030820+3";
		String publicInfo = "menu3";
		String language = "es";
		String organization = "test complex";
		String summary = 
			"Today Chuck ate an egg2 salad2 sandwich and a root beer2 " +
			"for lunch.";
		//String location = "San Francisco, CA";
		
		String attachment1LocalId = "att1Id";
		String attachment1Label = "Eggs.gif";
		String attachment2LocalId = "att2Id";
		String attachment2Label = "Recipe.txt";
		
		FieldDataPacket fdp = createFieldDataPacket(bulletinId, author, keywords, title, eventdate, entrydate, publicInfo, summary, null, attachment1LocalId, attachment1Label, attachment2LocalId, attachment2Label, language, organization);
		return fdp;
	}
	
	private FieldDataPacket createFieldDataPacket(UniversalId bulletinId, String author, String keywords, String title, String eventdate, String entrydate, String publicInfo, String summary, String location, String attachment1LocalId, String attachment1Label, String attachment2LocalId, String attachment2Label, String language, String organization)
	{
		FieldDataPacket fdp = generateFieldDataPacket(
			bulletinId, new String[] { 
				SEARCH_AUTHOR_INDEX_FIELD, author, 
				SEARCH_KEYWORDS_INDEX_FIELD, keywords, 
				SEARCH_TITLE_INDEX_FIELD, title,
				SEARCH_ENTRY_DATE_INDEX_FIELD, entrydate, 
				SEARCH_EVENT_DATE_INDEX_FIELD, eventdate,
				SEARCH_DETAILS_INDEX_FIELD, publicInfo, 
				SEARCH_SUMMARY_INDEX_FIELD, summary,
				SEARCH_LOCATION_INDEX_FIELD, location,
				SEARCH_LANGUAGE_INDEX_FIELD, language,
				SEARCH_ORGANIZATION_INDEX_FIELD, organization
			}, new String[] {
				attachment1LocalId, attachment1Label, 
				attachment2LocalId, attachment2Label
			});
		return fdp;
	}

	protected FieldDataPacket generateFieldDataPacket(UniversalId bulletinId)
	{
		return generateFieldDataPacket(bulletinId, new String[0]);
	}
	
	protected FieldDataPacket generateFieldDataPacket(
		UniversalId bulletinId, String[] fieldsAssocList)
	{
		return generateFieldDataPacket(
			bulletinId, fieldsAssocList, new String[0]);
	}
	
	
	protected FieldDataPacket generateFieldDataPacket(
		UniversalId bulletinId, String[] fieldsAssocList,
		String[] attachmentsAssocList)
	{
		FieldSpec[] fieldSpecs = BulletinField.getDefaultSearchFieldSpecs();
		UniversalId fieldUid = UniversalId.createFromAccountAndLocalId(
			bulletinId.getAccountId(), "TestField");
		
		FieldDataPacket fdp = new FieldDataPacket(fieldUid, fieldSpecs);
		Assert.assertEquals(
			"Uneven assoc list: " + Arrays.asList(fieldsAssocList), 
			0, fieldsAssocList.length % 2);
		for (int i = 0; i < fieldsAssocList.length; i += 2) {
			fdp.set(fieldsAssocList[i], fieldsAssocList[i + 1]);
		}
		Assert.assertEquals(
			"Uneven assoc list: " + Arrays.asList(attachmentsAssocList), 
			0, attachmentsAssocList.length % 2);
		for (int i = 0; i < attachmentsAssocList.length; i += 2) {
			fdp.addAttachment(new AttachmentProxy(
				UniversalId.createFromAccountAndLocalId(
					bulletinId.getAccountId(), attachmentsAssocList[i]),
				attachmentsAssocList[i + 1],
				null));
		}
					
		return fdp;
	}
	
	
	protected AbstractSearchTestCase(String name) 
	{
		super(name);
	}
	
	protected abstract BulletinIndexer openBulletinIndexer()
		throws BulletinIndexException;
	protected abstract BulletinSearcher openBulletinSearcher()
		throws BulletinIndexException;

}