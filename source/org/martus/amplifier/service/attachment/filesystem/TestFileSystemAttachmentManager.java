package org.martus.amplifier.service.attachment.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.martus.amplifier.service.attachment.AbstractAttachmentManagerTest;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.AttachmentStorageException;
import org.martus.common.UniversalId;

public class TestFileSystemAttachmentManager 
	extends AbstractAttachmentManagerTest
{
	
	public TestFileSystemAttachmentManager(String name) 
	{
		super(name);
	}
	
	public void testFileSystemClearAllAttachments() 
		throws AttachmentStorageException, IOException
	{
		UniversalId id = UniversalId.createDummyUniversalId();
		String testString = "FileSystemClearAll";
		InputStream sin = stringToInputStream(testString);
		try {
			attachmentManager.putAttachment(id, sin);
		} finally {
			sin.close();
		}
		
		attachmentManager.clearAllAttachments();
		File attachmentDir = new File(
			getTestBasePath(), 
			FileSystemAttachmentManager.ATTACHMENTS_DIR_NAME);
		Assert.assertEquals(
			"attachments directory not empty", 
			0, attachmentDir.listFiles().length);
	}
	
	public void testAccountWithFileSeparators() 
		throws IOException, AttachmentStorageException
	{
		UniversalId id = UniversalId.createFromAccountAndPrefix(
			"AnAccount/With/Slashes", "Test");
		String testString = "AccountWithFileSeparators";
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
	
	protected void setUp() throws Exception
	{
		super.setUp();
		attachmentManager = 
			new FileSystemAttachmentManager(getTestBasePath());
	}
	
	protected AttachmentManager getAttachmentManager()
	{
		return attachmentManager;
	}
	
	private FileSystemAttachmentManager attachmentManager;
	
}