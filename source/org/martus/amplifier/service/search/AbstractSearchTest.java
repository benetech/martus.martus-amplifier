package org.martus.amplifier.service.search;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.martus.amplifier.test.AbstractAmplifierTest;
import org.martus.common.AttachmentProxy;
import org.martus.common.FieldDataPacket;
import org.martus.common.UniversalId;

public abstract class AbstractSearchTest 
	extends AbstractAmplifierTest implements SearchConstants
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
				searcher.getBulletinData(bulletinId));
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
				searcher.getBulletinData(bulletinId));
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
				searcher.getBulletinData(bulletinId));
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
			FieldDataPacket found = searcher.getBulletinData(bulletinId);
			Assert.assertNotNull(
				"Didn't find indexed bulletin", 
				found);
						
			Assert.assertEquals(
				1, 
				searcher.searchField(
					AUTHOR_INDEX_FIELD, 
					fdp.get(BulletinField.TAGAUTHOR)).getCount());
			Assert.assertEquals(
				1,
				searcher.searchField(
					KEYWORDS_INDEX_FIELD, 
					fdp.get(BulletinField.TAGKEYWORDS)).getCount());
			Date startDate = searcher.DATE_FORMAT.parse("2003-05-01");
			Date endDate = searcher.DATE_FORMAT.parse("2003-05-20");
			Assert.assertEquals(
				1,
				searcher.searchDateRange(ENTRY_DATE_INDEX_FIELD, startDate, endDate).getCount());
		
			Assert.assertEquals(
				0,
				searcher.searchField(
					PUBLIC_INFO_INDEX_FIELD, 
					fdp.get(BulletinField.TAGAUTHOR)).getCount());
			Assert.assertEquals(
				0,
				searcher.searchDateRange(EVENT_DATE_INDEX_FIELD, startDate, endDate).getCount());
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
			FieldDataPacket found = searcher.getBulletinData(bulletinId);
			Assert.assertNotNull(
				"Didn't find indexed bulletin", 
				found);
			
			AttachmentProxy[] origProxies = fdp.getAttachments();
			AttachmentProxy[] foundProxies = found.getAttachments();
			Assert.assertEquals(origProxies.length, foundProxies.length);
			for (int i = 0; i < origProxies.length; i++) {
				Assert.assertEquals(
					origProxies[i].getUniversalId(), 
					foundProxies[i].getUniversalId());	
				Assert.assertEquals(
					origProxies[i].getLabel(), 
					foundProxies[i].getLabel());
			}
			
			Assert.assertEquals(fdp.getUniversalId(), found.getUniversalId());
			String[] origFieldTags = fdp.getFieldTags();
			String[] foundFieldTags = found.getFieldTags();
			Assert.assertEquals(
				Arrays.asList(origFieldTags), 
				Arrays.asList(foundFieldTags));
			for (int i = 0; i < origFieldTags.length; i++) {
				Assert.assertEquals(
					fdp.get(origFieldTags[i]), 
					found.get(origFieldTags[i]));
			}
		} finally {
			searcher.close();
		}
	}
	
	public void testSimultaneousAccess() throws BulletinIndexException
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
			results = searcher.searchField(
				AUTHOR_INDEX_FIELD, fdp.get(BulletinField.TAGAUTHOR));
		} finally {
			searcher.close();
		}
		
		try {
			fdp = results.getFieldDataPacket(0);
			Assert.fail(
				"Accessing results after closing searcher should have failed.");
		} catch (BulletinIndexException expected) {
		}
	}
	
	protected FieldDataPacket generateSampleData(UniversalId bulletinId)
	{
		String author = "Paul";
		String keyword = "egg";
		String keywords = keyword + " salad root beer";
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
		String[] fieldIds = BulletinField.getSearchableXmlIds();
		UniversalId fieldUid = UniversalId.createFromAccountAndLocalId(
			bulletinId.getAccountId(), "TestField");
		FieldDataPacket fdp = new FieldDataPacket(fieldUid, fieldIds);
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
	
	
	protected AbstractSearchTest(String name) 
	{
		super(name);
	}
	
	protected abstract BulletinIndexer openBulletinIndexer()
		throws BulletinIndexException;
	protected abstract BulletinSearcher openBulletinSearcher()
		throws BulletinIndexException;

}