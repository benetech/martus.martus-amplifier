package org.martus.amplifier.service.attachment.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.AttachmentNotFoundException;
import org.martus.amplifier.service.attachment.AttachmentStorageException;
import org.martus.common.AttachmentPacket;
import org.martus.common.StreamCopier;
import org.martus.common.UniversalId;

public class FileSystemAttachmentManager implements AttachmentManager
{
	public FileSystemAttachmentManager(String baseDirName)
	{
		baseDir = new File(baseDirName);
		baseDir.mkdirs();
	}
	
	public InputStream getAttachment(UniversalId attachmentId) 
		throws AttachmentStorageException
	{
		try {
			return new FileInputStream(getAttachmentFile(attachmentId));
		} catch (FileNotFoundException e) {
			throw new AttachmentNotFoundException(
				"Unable to get attachment " + attachmentId, e);
		}
	}
	
	public void putAttachment(UniversalId attachmentId, InputStream data)
		throws AttachmentStorageException
	{
		InputStream in = data;
		OutputStream out = null;
		try {
			out = new FileOutputStream(getAttachmentFile(attachmentId));
			new StreamCopier().copyStream(in, out);
		} catch (IOException e) {
			throw new AttachmentStorageException(
				"Unable to store attachment " + attachmentId, e);	
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ioe) {
					throw new AttachmentStorageException(
						"Unable to store attachment " + attachmentId, 
						ioe);
				}
			}
		}		
	}
	
	public void clearAllAttachments() throws AttachmentStorageException
	{
		deleteAllAccounts();
	}
	
	private void deleteAllAccounts() throws AttachmentStorageException
	{
		File[] accountDirs = baseDir.listFiles();
		for (int i = 0; i < accountDirs.length; i++) {
			File accountDir = accountDirs[i];
			if (!accountDir.isDirectory()) {
				throw new AttachmentStorageException(
					"Unexpected file object found in base directory: " 
					+ accountDir.getName());
			}
			deleteAccount(accountDir);
		}
	}
	
	private void deleteAccount(File accountDir) throws AttachmentStorageException
	{
		File[] attachments = accountDir.listFiles();
		for (int i = 0; i < attachments.length; i++) {
			File attachment = attachments[i];
			if (!attachment.isFile()) 
			{
				throw new AttachmentStorageException(
					"Unexpected file object found in account directory. Account: " + 
					accountDir.getName() + "; file: " + attachment.getName());
			}
			attachment.delete();
		}
		accountDir.delete();
	}
	
	private File getAttachmentFile(UniversalId attachmentId)
	{
		File accountDir = new File(baseDir, attachmentId.getAccountId());
		accountDir.mkdir();
		return new File(accountDir, attachmentId.getLocalId());
	}
	
	private File baseDir;
}