package org.martus.amplifier.service.attachment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.martus.amplifier.test.AbstractAmplifierTest;
import org.martus.common.StreamCopier;
import org.martus.common.UniversalId;

public abstract class AbstractAttachmentManagerTest 
	extends AbstractAmplifierTest
{
	public void testClearAllAttachments() 
		throws AttachmentStorageException, IOException
	{
		AttachmentManager attachmentManager = getAttachmentManager();
		UniversalId id = UniversalId.createDummyUniversalId();
		String testString = "ClearAll";
		InputStream sin = stringToInputStream(testString);
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
		InputStream sin = stringToInputStream(testString);
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
		InputStream sin = stringToInputStream(testString);
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
		sin = stringToInputStream(testString2);
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
		InputStream sin = stringToInputStream(testString);
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
		sin = stringToInputStream(testString2);
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
		InputStream sin = stringToInputStream(testString);
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
		sin = stringToInputStream(testString2);
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
	
	protected AbstractAttachmentManagerTest(String name)
	{
		super(name);
	}
	
	protected InputStream stringToInputStream(String s) 
		throws UnsupportedEncodingException
	{
		return new ByteArrayInputStream(s.getBytes("UTF-8"));
	}
	
	protected String inputStreamToString(InputStream in) 
		throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new StreamCopier().copyStream(in, out);
		return out.toString("UTF-8");
	}
	
	protected abstract AttachmentManager getAttachmentManager();
}