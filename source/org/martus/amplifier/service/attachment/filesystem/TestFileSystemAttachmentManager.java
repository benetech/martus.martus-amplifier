package org.martus.amplifier.service.attachment.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
		File attachmentDir = new File(getTestAttachmentPath());
		Assert.assertEquals(
			"attachments directory not empty", 
			0, attachmentDir.listFiles().length);
	}
	
	protected void setUp() throws Exception
	{
		super.setUp();
		attachmentManager = new FileSystemAttachmentManager(getTestAttachmentPath());
	}
	
	protected AttachmentManager getAttachmentManager()
	{
		return attachmentManager;
	}
	
	private FileSystemAttachmentManager attachmentManager;
	
}