package org.martus.amplifier.service.attachment;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.martus.amplifier.test.AbstractAmplifierTestCase;
import org.martus.common.packet.UniversalId;
import org.martus.util.StringInputStream;

public abstract class TestAbstractAttachmentManager 
	extends AbstractAmplifierTestCase
{
	public void testClearAllAttachments() 
		throws AttachmentStorageException, IOException
	{
		AttachmentManager attachmentManager = getAttachmentManager();
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
		AttachmentManager attachmentManager = getAttachmentManager();
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
		AttachmentManager attachmentManager = getAttachmentManager();
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
		AttachmentManager attachmentManager = getAttachmentManager();
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
	
	public void testOverwriteExistingAttachment() 
		throws AttachmentStorageException, IOException
	{
		AttachmentManager attachmentManager = getAttachmentManager();
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
	
	protected TestAbstractAttachmentManager(String name)
	{
		super(name);
	}
	
	protected void tearDown() throws Exception 
	{
		try {
			getAttachmentManager().close();
		} finally {
			super.tearDown();
		}
	}
	
	protected abstract AttachmentManager getAttachmentManager();
	

}