package org.martus.amplifier.service.search;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.martus.amplifier.test.TestAbstractAmplifier;
import org.martus.common.FieldSpec;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;

public abstract class TestAbstractSearch 
	extends TestAbstractAmplifier implements SearchConstants
{
	
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
			Assert.assertNotNull(
				"Didn't find indexed bulletin", 
				found);
						
			Assert.assertEquals(
				1, 
				searcher.search(
					AUTHOR_INDEX_FIELD, 
					fdp.get(BulletinField.TAGAUTHOR)).getCount());
			Assert.assertEquals(
				1,
				searcher.search(
					KEYWORDS_INDEX_FIELD, 
					fdp.get(BulletinField.TAGKEYWORDS)).getCount());
			Date startDate = SearchConstants.DATE_FORMAT.parse("2003-05-01");
			Date endDate = SearchConstants.DATE_FORMAT.parse("2003-05-20");
			Assert.assertEquals(
				1,
				searcher.searchDateRange(
					ENTRY_DATE_INDEX_FIELD, startDate, endDate).getCount());
		
			Assert.assertEquals(
				0,
				searcher.search(
					DETAILS_INDEX_FIELD, 
					fdp.get(BulletinField.TAGAUTHOR)).getCount());
			Assert.assertEquals(
				0,
				searcher.searchDateRange(
					EVENT_DATE_INDEX_FIELD, startDate, endDate).getCount());
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
		BulletinSearcher.Results results = null;
		try {
			results = searcher.search(
				AUTHOR_INDEX_FIELD, fdp.get(BulletinField.TAGAUTHOR));
		} finally {
			searcher.close();
		}
		
		try {
			BulletinInfo info = results.getBulletinInfo(0);
			Assert.fail(
				"Accessing results after closing searcher should have failed.");
		} catch (BulletinIndexException expected) {
		}
	}
	
	public void testDateBoundary() 
		throws BulletinIndexException, ParseException
	{
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateSampleData(bulletinId);		
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
			Date startDate = SearchConstants.DATE_FORMAT.parse("2003-05-11");
			Date endDate = SearchConstants.DATE_FORMAT.parse("2003-05-11");
			
			results = searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, startDate, endDate);
			assertEquals("Entry Date not found?",1, results.getCount());

			startDate = SearchConstants.DATE_FORMAT.parse("2003-04-10");
			endDate = SearchConstants.DATE_FORMAT.parse("2003-04-11");
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, endDate);
			assertEquals("Flexidate single Event Date not found?",1, results.getCount());
			
		} 
		finally 
		{
			searcher.close();
		}
	}
	
	public void testOpenStartedDateSearch()
		throws BulletinIndexException, ParseException
	{
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateSampleData(bulletinId);		
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
			// entryDate = 2003-05-11			
			Date endDate = SearchConstants.DATE_FORMAT.parse("2003-03-20");
			results = searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, null, endDate);
			assertEquals("Entrydate with an invalid before an entry date (open started)?", 0, results.getCount());
			
			endDate = SearchConstants.DATE_FORMAT.parse("2003-05-11");
			results = searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, null, endDate);
			assertEquals("Entrydate with an exact on start date (open started)", 1, results.getCount());
			
			endDate = SearchConstants.DATE_FORMAT.parse("2003-06-30");
			results = searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, null, endDate);
			assertEquals("Entrydate with a valid after an entry date (open started)?", 1, results.getCount());			
				
			//eventDate = 2003-04-10 			
			endDate = SearchConstants.DATE_FORMAT.parse("2002-04-01");
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, null, endDate);
			assertEquals("Eventdate with an invalid before start date (open started)?", 0, results.getCount());
			
			endDate = SearchConstants.DATE_FORMAT.parse("2003-04-10");
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, null, endDate);
			assertEquals("Eventdate with an exact on start date (open started)", 1, results.getCount());
			
			endDate = SearchConstants.DATE_FORMAT.parse("2003-07-10");
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, null, endDate);
			assertEquals("Eventdate with a valid after start date (open started)?", 1, results.getCount());			
			
			try 
			{
				results = searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, null, null);
				fail("Should not have been able to specify null for both ends of range query");
			} 
			catch (IllegalArgumentException expected) 
			{
			}
		} 
		finally 
		{
			searcher.close();
		}
	}
	
	public void testOpenEndedDateSearch()
		throws BulletinIndexException, ParseException
	{
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateSampleData(bulletinId);		
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
			// entryDate = 2003-05-11			
			Date startDate = SearchConstants.DATE_FORMAT.parse("2003-03-20");
			results = searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, startDate, null);
			assertEquals("Entrydate with a valid before an entry date (open ended)?", 1, results.getCount());
			
			startDate = SearchConstants.DATE_FORMAT.parse("2003-05-11");
			results = searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, startDate, null);
			assertEquals("Entrydate with an exact on start date (open ended)", 1, results.getCount());
			
			startDate = SearchConstants.DATE_FORMAT.parse("2003-06-30");
			results = searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, startDate, null);
			assertEquals("Entrydate with an invalid after an entry date (open ended)?", 0, results.getCount());			
			
			// eventDate = 2003-04-10 			
			startDate = SearchConstants.DATE_FORMAT.parse("2002-04-01");
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, null);
			assertEquals("Eventdate with a valid before start date(open ended)?", 1, results.getCount());
			
			startDate = SearchConstants.DATE_FORMAT.parse("2003-04-10");
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, null);
			assertEquals("Eventdate with an exact on start date", 1, results.getCount());
			
			startDate = SearchConstants.DATE_FORMAT.parse("2003-07-10");
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, null);
			assertEquals("Eventdate with an invalid after start date(open ended)?", 0, results.getCount());			
			
			try 
			{
				results = searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, null, null);
				fail("Should not have been able to specify null for both ends of range query");
			} 
			catch (IllegalArgumentException expected) 
			{
			}
		} 
		finally 
		{
			searcher.close();
		}
	}
	
	public void testFlexiDateBoundary() 
		throws BulletinIndexException, ParseException
	{
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateSampleFlexiData(bulletinId);		
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
			// The flexidate is 2003-08-20, 20030820+3				
			Date startDate = SearchConstants.DATE_FORMAT.parse("2003-08-10");
			Date endDate = SearchConstants.DATE_FORMAT.parse("2003-08-22");			
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, endDate);
			assertEquals("FlexiDate ([startEventDate] ) range Event Date found?",1, results.getCount());
			
			startDate = SearchConstants.DATE_FORMAT.parse("2003-08-21");
			endDate = SearchConstants.DATE_FORMAT.parse("2003-08-25");			
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, endDate);
			assertEquals("FlexiDate ([endEventDate]) range Event Date found?",1, results.getCount());

			startDate = SearchConstants.DATE_FORMAT.parse("2003-01-21");
			endDate = SearchConstants.DATE_FORMAT.parse("2003-12-25");	
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, endDate);
			assertEquals("FlexiDate ([startEventDate|endEvetnDate]) range Event Date found?",1, results.getCount());
			
			startDate = SearchConstants.DATE_FORMAT.parse("2002-01-21");
			endDate = SearchConstants.DATE_FORMAT.parse("2002-01-25");			
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, endDate);
			assertEquals("FlexiDate with an invalid ([]startDate) range Event Date found?",0, results.getCount());

			startDate = SearchConstants.DATE_FORMAT.parse("2003-08-28");
			endDate = SearchConstants.DATE_FORMAT.parse("2003-08-31");			
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, endDate);
			assertEquals("FlexiDate with an invalid (endDate[]) range Event Date found?",0, results.getCount());
		
			startDate = SearchConstants.DATE_FORMAT.parse("2003-08-21");
			endDate = SearchConstants.DATE_FORMAT.parse("2003-08-22");		
			results = searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, endDate);
			assertEquals("FlexiDate (startEventDate[]endEvetnDate) range Event Date found?",1, results.getCount());
			
		} 
		finally 
		{
			searcher.close();
		}
	}
		
	protected FieldDataPacket generateSampleData(UniversalId bulletinId)
	{
		String author = "Paul";
		String keyword = "2003-04-10";
		String keywords = keyword + " egg salad root beer";
		String title = "What's for Lunch";
		String eventDate = "2003-04-10";
		String entryDate = "2003-05-11";
		String publicInfo = "menu";
		String summary = 
			"Today Paul ate an egg salad sandwich and a root beer " +
			"for lunch.";
		String location = "San Francisco, CA";
		
		String attachment1LocalId = "att1Id";
		String attachment1Label = "Eggs.gif";
		String attachment2LocalId = "att2Id";
		String attachment2Label = "Recipe.txt";
		
		FieldDataPacket fdp = generateFieldDataPacket(
			bulletinId, new String[] { 
				BulletinField.TAGAUTHOR, author, 
				BulletinField.TAGKEYWORDS, keywords, 
				BulletinField.TAGTITLE, title,
				BulletinField.TAGENTRYDATE, entryDate, 
				BulletinField.TAGEVENTDATE, eventDate,
				BulletinField.TAGPUBLICINFO, publicInfo, 
				BulletinField.TAGSUMMARY, summary,
				BulletinField.TAGLOCATION, location
			}, new String[] {
				attachment1LocalId, attachment1Label, 
				attachment2LocalId, attachment2Label
			});
		return fdp;
	}
	
	protected FieldDataPacket generateSampleFlexiData(UniversalId bulletinId)
	{
		String author = "Chuck";	
		String keywords = "2003-08-20" + " 2003-08-23";
		String title = "What's for Lunch??";
		String entryDate= "2003-08-30";
		String eventDate = "2003-08-20,20030820+3";
		String publicInfo = "menu3";
		String summary = 
			"Today Chuck ate an egg2 salad sandwich and a root beer2 " +
			"for lunch.";
		String location = "San Francisco, CA";
		
		String attachment1LocalId = "att1Id";
		String attachment1Label = "Eggs.gif";
		String attachment2LocalId = "att2Id";
		String attachment2Label = "Recipe.txt";
		
		FieldDataPacket fdp = generateFieldDataPacket(
			bulletinId, new String[] { 
				BulletinField.TAGAUTHOR, author, 
				BulletinField.TAGKEYWORDS, keywords, 
				BulletinField.TAGTITLE, title,
				BulletinField.TAGENTRYDATE, entryDate, 
				BulletinField.TAGEVENTDATE, eventDate,
				BulletinField.TAGPUBLICINFO, publicInfo, 
				BulletinField.TAGSUMMARY, summary,
				BulletinField.TAGLOCATION, location
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
	
	
	protected TestAbstractSearch(String name) 
	{
		super(name);
	}
	
	protected abstract BulletinIndexer openBulletinIndexer()
		throws BulletinIndexException;
	protected abstract BulletinSearcher openBulletinSearcher()
		throws BulletinIndexException;

}