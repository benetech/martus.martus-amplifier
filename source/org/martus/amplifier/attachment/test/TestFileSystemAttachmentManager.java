package org.martus.amplifier.attachment.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.martus.amplifier.attachment.AttachmentManager;
import org.martus.amplifier.attachment.AttachmentStorageException;
import org.martus.amplifier.attachment.FileSystemAttachmentManager;
import org.martus.common.packet.UniversalId;
import org.martus.util.StringInputStream;

public class TestFileSystemAttachmentManager 
	extends TestAbstractAttachmentManager
{
	
	public TestFileSystemAttachmentManager(String name) 
	{
		super(name);
	}
	
	protected void setUp() throws Exception
	{
		super.setUp();
		attachmentManager = 
			new FileSystemAttachmentManager(getTestBasePath());
	}

	public void testFileSystemClearAllAttachments() 
		throws AttachmentStorageException, IOException
	{
		UniversalId id = UniversalId.createDummyUniversalId();
		String testString = "FileSystemClearAll";
		InputStream sin = new StringInputStream(testString);
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
	
	protected AttachmentManager getAttachmentManager()
	{
		return attachmentManager;
	}
	
	private FileSystemAttachmentManager attachmentManager;
	
}