package org.martus.amplifier.datasynch.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.martus.amplifier.attachment.AttachmentManager;
import org.martus.amplifier.attachment.AttachmentStorageException;
import org.martus.amplifier.attachment.FileSystemAttachmentManager;
import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.amplifier.datasynch.BulletinExtractor;
import org.martus.amplifier.lucene.LuceneBulletinIndexer;
import org.martus.amplifier.lucene.LuceneBulletinSearcher;
import org.martus.amplifier.search.AttachmentInfo;
import org.martus.amplifier.search.BulletinField;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.search.BulletinSearcher;
import org.martus.amplifier.search.SearchConstants;
import org.martus.amplifier.test.AbstractAmplifierTestCase;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinForTesting;
import org.martus.common.bulletin.BulletinSaver;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.crypto.MartusCrypto.CryptoException;
import org.martus.common.crypto.MartusCrypto.EncryptionException;
import org.martus.common.database.Database;
import org.martus.common.database.MockServerDatabase;
import org.martus.common.packet.UniversalId;
import org.martus.util.DirectoryTreeRemover;
import org.martus.util.StreamCopier;
import org.martus.util.StringInputStream;

public class TestBulletinExtractor extends AbstractAmplifierTestCase
	implements SearchConstants
{
	public TestBulletinExtractor(String name) 
	{
		super(name);
	}
	
	public void testSimpleExtraction() 
		throws Exception
	{
		clearTestData();
		BulletinIndexer indexer = null;
		Exception closeException = null;
		Bulletin b = createSampleBulletin(new File[0]);
		File f = createBulletinZipFile(b);
		
		try {
			indexer = getBulletinIndexer();
			BulletinExtractor extractor = 
				new BulletinExtractor(
					attachmentManager, indexer, security);
			extractor.extractAndStoreBulletin(f);
		} finally {
			if (indexer != null) {
				try {
					indexer.close();
				} catch (BulletinIndexException e) {
					closeException = e;
				}
			}
		}
		
		if (closeException != null) {
			throw closeException;
		}
		
		BulletinSearcher searcher = getBulletinSearcher();
		try {
			BulletinSearcher.Results results = 
				searcher.search(
					SEARCH_AUTHOR_INDEX_FIELD, b.get(BulletinField.TAGAUTHOR));
			Assert.assertEquals(1, results.getCount());
			BulletinInfo info = 
				searcher.lookup(b.getUniversalId());
			Assert.assertNotNull(info);
			compareBulletins(b, info);
		} finally {
			searcher.close();
		}
	}
	
	// TODO pdalbora 5-May-2003 -- Expose this method to junit by
	// removing the underscore when it's working.
	public void testExtractionWithAttachments() 
		throws Exception
	{
		clearTestData();
		BulletinIndexer indexer = null;
		Exception closeException = null;
		File[] attachments = new File[2];
		attachments[0] = createAttachment("Attachment 1");
		attachments[1] = createAttachment("Attachment 2");
		Bulletin b = createSampleBulletin(attachments);
		BulletinSaver.saveToClientDatabase(b, db, false, security);
		File f = createBulletinZipFile(b);
		
		try {
			indexer = getBulletinIndexer();
			BulletinExtractor extractor = 
				new BulletinExtractor(
					attachmentManager, indexer, security);
			extractor.extractAndStoreBulletin(f);
		} finally {
			if (indexer != null) {
				try {
					indexer.close();
				} catch (BulletinIndexException e) {
					closeException = e;
				}
			}
		}
		
		if (closeException != null) {
			throw closeException;
		}
		
		BulletinSearcher searcher = getBulletinSearcher();
		try {
			BulletinSearcher.Results results = 
				searcher.search(
					SEARCH_AUTHOR_INDEX_FIELD, b.get(BulletinField.TAGAUTHOR));
			Assert.assertEquals(1, results.getCount());
			BulletinInfo info = 
				searcher.lookup(b.getUniversalId());
			Assert.assertNotNull(info);
			compareBulletins(b, info);
			compareAttachments(b.getAccount(), attachments, info.getAttachments());
		} finally {
			searcher.close();
		}
	}
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		attachmentManager = 
			new FileSystemAttachmentManager(getTestBasePath());
		security = new MockMartusSecurity();
		security.createKeyPair();
		db = new MockServerDatabase();
	}

	protected void tearDown() throws Exception 
	{
		try 
		{
			attachmentManager.clearAllAttachments();
			String basePath = AmplifierConfiguration.getInstance().getBasePath() + "/test";
			DirectoryTreeRemover.deleteEntireDirectoryTree(new File(basePath));
		} 
		finally 
		{
			super.tearDown();
		}
	}
	
	private void compareBulletins(
		Bulletin bulletin, BulletinInfo retrievedData) 
		throws IOException
	{
		Assert.assertEquals(
			bulletin.getUniversalId(), retrievedData.getBulletinId());
		Collection fields = BulletinField.getSearchableFields();
		for (Iterator iter = fields.iterator(); iter.hasNext();) {
			BulletinField field = (BulletinField) iter.next();
			Object retrievedValue = retrievedData.get(field.getIndexId());
			if (retrievedValue == null) {
				retrievedValue = "";
			}
			Assert.assertEquals(
				bulletin.get(field.getXmlId()), 
				retrievedValue);
		}
	}
	
	private void compareAttachments(
		String accountId, File[] attachments, List retrieved) 
		throws IOException, AttachmentStorageException
	{
		Assert.assertEquals(attachments.length, retrieved.size());
		for (int i = 0; i < attachments.length; i++) {
			String s1 = fileToString(attachments[i]);
			AttachmentInfo info = (AttachmentInfo) retrieved.get(i);
			InputStream in = 
				attachmentManager.getAttachment(
					UniversalId.createFromAccountAndLocalId(accountId, info.getLocalId()));
			try {
				Assert.assertEquals(s1, inputStreamToString(in));
			} finally {
				in.close();
			}
		}
	}
	
	private Bulletin createSampleBulletin(File[] attachments) 
		throws EncryptionException, IOException
	{
		Bulletin b = new Bulletin(security);
		b.set(BulletinField.TAGAUTHOR, "paul");
		b.set(BulletinField.TAGKEYWORDS, "testing");
		b.set(BulletinField.TAGENTRYDATE, "2003-04-30");
		for (int i = 0; i < attachments.length; i++) {
			b.addPublicAttachment(new AttachmentProxy(attachments[i]));
		}
		return b;
	}
	
	private File createAttachment(String data) 
		throws IOException
	{
		return stringToFile(data);
	}
	
	private File stringToFile(String s) throws IOException
	{
		File temp = createTempFileFromName("$$$MartusAmpTempAttachment");
		InputStream in = new StringInputStream(s);
		OutputStream out = new FileOutputStream(temp);
		try {
			new StreamCopier().copyStream(in, out);
		} finally {
			out.close();
		}
		return temp;
	}
	
	private String fileToString(File f) throws IOException
	{
		InputStream in = new FileInputStream(f);
		try {
			return inputStreamToString(in);
		} finally {
			in.close();
		}
	}
	
	private File createBulletinZipFile(Bulletin b) 
		throws IOException, CryptoException			
	{
		File tempFile = createTempFileFromName("$$$MartusAmpBulletinExtractorTest");
		BulletinForTesting.saveToFile(db, b, tempFile, security);
		return tempFile;
	}

	
	private void clearTestData() 
		throws AttachmentStorageException, BulletinIndexException
	{
		attachmentManager.clearAllAttachments();
		
		BulletinIndexer indexer = getBulletinIndexer();
		try {
			indexer.clearIndex();
		} finally {
			indexer.close();
		}	
	}
	
	private BulletinIndexer getBulletinIndexer() 
		throws BulletinIndexException
	{
		return new LuceneBulletinIndexer(getTestBasePath());
	}
	
	private BulletinSearcher getBulletinSearcher()
		throws BulletinIndexException
	{
		return new LuceneBulletinSearcher(getTestBasePath());
	}
	
	private AttachmentManager attachmentManager;
	private MartusCrypto security;
	private Database db;
}