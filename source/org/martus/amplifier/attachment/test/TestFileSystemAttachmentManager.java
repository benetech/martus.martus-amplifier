package org.martus.amplifier.attachment.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.martus.amplifier.attachment.AttachmentManager;
import org.martus.amplifier.attachment.AttachmentStorageException;
import org.martus.amplifier.attachment.FileSystemAttachmentManager;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.database.FileDatabase.MissingAccountMapException;
import org.martus.common.database.FileDatabase.MissingAccountMapSignatureException;
import org.martus.common.packet.UniversalId;
import org.martus.util.DirectoryTreeRemover;
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
		MockMartusSecurity security = new MockMartusSecurity();
		security.createKeyPair();
		attachmentManager = 
			new FileSystemAttachmentManager(getTestBasePath(), security);
	}
	
	public void testMissingAccountMap() throws Exception
	{
		File missingAccountMap = null;
		File emptyAccount = null;
		try
		{
			missingAccountMap = createTempDirectory();
			emptyAccount = new File(missingAccountMap.getAbsolutePath() + "\\ab00");
			emptyAccount.deleteOnExit();
			emptyAccount.mkdir();
			new FileSystemAttachmentManager(missingAccountMap.getAbsolutePath());
			fail("Should have thrown");
		}
		catch (MissingAccountMapException expectedException)
		{
		}		
		finally
		{
			DirectoryTreeRemover.deleteEntireDirectoryTree(missingAccountMap);
		}
	}

	public void testInvalidAccountMap() throws Exception
	{
		File baseDir = null;
		File accountDir = null;
		try
		{
			baseDir = createTempDirectory();
			accountDir = new File(baseDir.getAbsolutePath() + "\\ab00");
			accountDir.deleteOnExit();
			accountDir.mkdir();
			File accountMap = new File(baseDir.getAbsolutePath() + "\\acctmap.txt");
			accountMap.deleteOnExit();
			accountMap.createNewFile();
			new FileSystemAttachmentManager(baseDir.getAbsolutePath());
			fail("Should have thrown");
		}
		catch (MissingAccountMapSignatureException expectedException)
		{
		}		
		finally
		{
			DirectoryTreeRemover.deleteEntireDirectoryTree(baseDir);
		}
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
			getTestBasePath());
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