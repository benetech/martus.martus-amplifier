package org.martus.amplifier.attachment.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.martus.amplifier.attachment.DataManager;
import org.martus.amplifier.attachment.AttachmentNotFoundException;
import org.martus.amplifier.attachment.AttachmentStorageException;
import org.martus.amplifier.test.AbstractAmplifierTestCase;
import org.martus.common.packet.UniversalId;
import org.martus.util.DirectoryTreeRemover;
import org.martus.util.StringInputStream;

public abstract class TestAbstractDataManager 
	extends AbstractAmplifierTestCase
{
	protected TestAbstractDataManager(String name)
	{
		super(name);
	}

	protected void tearDown() throws Exception 
	{
		DirectoryTreeRemover.deleteEntireDirectoryTree(new File(basePath));
	}

	public void testClearAllAttachments() 
		throws AttachmentStorageException, IOException
	{
		DataManager attachmentManager = getAttachmentManager();
		UniversalId id = UniversalId.createDummyUniversalId();
		String testString = "ClearAll";
		InputStream sin = new StringInputStream(testString);
		try {
			attachmentManager.putAttachment(id, sin);
		} finally {
			sin.close();
		}
		InputStream in = null;
		try {
			in = attachmentManager.getAttachment(id);
		} catch (AttachmentStorageException e) {
			Assert.fail("Expected an attachment for id: " + id);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		attachmentManager.clearAllAttachments();
		in = null;
		try {
			in = attachmentManager.getAttachment(id);
		} catch (AttachmentNotFoundException expected) {
		} finally {
			if (in != null) {
				in.close();
				Assert.fail(
					"Found something after clearing all attachments");
			}
		}
	}
	
	public void testSimplePutAndGetAttachment() 
		throws AttachmentStorageException, IOException
	{
		DataManager attachmentManager = getAttachmentManager();
		attachmentManager.clearAllAttachments();
		UniversalId id = UniversalId.createDummyUniversalId();
		String testString = "SimplePutAndGet";
		InputStream sin = new StringInputStream(testString);
		try {
			attachmentManager.putAttachment(id, sin);
		} finally {
			sin.close();
		}
		InputStream in = null;
		try {
			in = attachmentManager.getAttachment(id);
			Assert.assertEquals(testString, inputStreamToString(in));
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	public void testPutAndGetTwoSameAccount() 
		throws AttachmentStorageException, IOException
	{
		DataManager attachmentManager = getAttachmentManager();
		attachmentManager.clearAllAttachments();
		UniversalId id = UniversalId.createDummyUniversalId();
		String testString = "PutAndGetTwoSameAccount";
		InputStream sin = new StringInputStream(testString);
		try {
			attachmentManager.putAttachment(id, sin);
		} finally {
			sin.close();
		}
		InputStream in = null;
		try {
			in = attachmentManager.getAttachment(id);
			Assert.assertEquals(testString, inputStreamToString(in));
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		UniversalId id2 = UniversalId.createDummyUniversalId();
		String testString2 = "PutAndGetTwoSameAccount2";
		sin = new StringInputStream(testString2);
		try {
			attachmentManager.putAttachment(id2, sin);
		} finally {
			sin.close();
		}
		in = null;
		try {
			in = attachmentManager.getAttachment(id2);
			Assert.assertEquals(testString2, inputStreamToString(in));
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	public void testPutAndGetTwoDifferentAccounts() 
		throws AttachmentStorageException, IOException
	{
		DataManager attachmentManager = getAttachmentManager();
		attachmentManager.clearAllAttachments();
		UniversalId id = 
			UniversalId.createFromAccountAndLocalId("Account1", "Test");
		String testString = "PutAndGetTwoDifferentAccounts";
		InputStream sin = new StringInputStream(testString);
		try {
			attachmentManager.putAttachment(id, sin);
		} finally {
			sin.close();
		}
		InputStream in = null;
		try {
			in = attachmentManager.getAttachment(id);
			Assert.assertEquals(testString, inputStreamToString(in));
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		UniversalId id2 = 
			UniversalId.createFromAccountAndLocalId("Account2", "Test");
		String testString2 = "PutAndGetTwoDifferentAccounts2";
		sin = new StringInputStream(testString2);
		try {
			attachmentManager.putAttachment(id2, sin);
		} finally {
			sin.close();
		}
		in = null;
		try {
			in = attachmentManager.getAttachment(id2);
			Assert.assertEquals(testString2, inputStreamToString(in));
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public void testSizeOfFile() 
		throws AttachmentStorageException, IOException
	{
		DataManager attachmentManager = getAttachmentManager();
		attachmentManager.clearAllAttachments();
		UniversalId id = 
			UniversalId.createFromAccountAndLocalId("Account1", "Test");
		String testString = "PutAndGetTwoDifferentAccounts";
		InputStream sin = new StringInputStream(testString);
		try 
		{
			attachmentManager.putAttachment(id, sin);
		} 
		finally 
		{
			sin.close();
		}
		assertEquals("Size not correct?", testString.length(), (int)attachmentManager.getAttachmentSizeInBytes(id));
		assertEquals("Size of file <1K should be 1Kb?",1,(int)attachmentManager.getAttachmentSizeInKb(id));
	}
	
	public void testOverwriteExistingAttachment() 
		throws AttachmentStorageException, IOException
	{
		DataManager attachmentManager = getAttachmentManager();
		attachmentManager.clearAllAttachments();
		UniversalId id = UniversalId.createDummyUniversalId();
		String testString = "OverwriteExisting";
		InputStream sin = new StringInputStream(testString);
		try {
			attachmentManager.putAttachment(id, sin);
		} finally {
			sin.close();
		}
		InputStream in = null;
		try {
			in = attachmentManager.getAttachment(id);
			Assert.assertEquals(testString, inputStreamToString(in));
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		String testString2 = "OverwriteExisting2";
		sin = new StringInputStream(testString2);
		try {
			attachmentManager.putAttachment(id, sin);
		} finally {
			sin.close();
		}
		in = null;
		try {
			in = attachmentManager.getAttachment(id);
			Assert.assertEquals(testString2, inputStreamToString(in));
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	protected abstract DataManager getAttachmentManager();
}