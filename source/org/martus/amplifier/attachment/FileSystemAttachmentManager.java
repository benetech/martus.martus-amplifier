package org.martus.amplifier.attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;

import org.martus.common.packet.UniversalId;
import org.martus.util.StreamCopier;

public class FileSystemAttachmentManager implements AttachmentManager
{
	public FileSystemAttachmentManager(String baseDirName) 
		throws AttachmentStorageException
	{
		baseDir = new File(baseDirName, ATTACHMENTS_DIR_NAME);
		if (!baseDir.exists() && !baseDir.mkdirs()) {
			throw new AttachmentStorageException(
				"Unable to create path: " + baseDir);
		}
		accountMapFile = new File(baseDir, ACCOUNT_MAP_FILE_NAME);
		loadAccountMap();
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
					save();					
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
		for (Iterator iter = accountMap.values().iterator(); 
			iter.hasNext();) 
		{
			String dir = (String) iter.next();
			deleteAccountDir(dir);
		}
		accountMap.clear();
		accountMapFile.delete();
		File[] children = baseDir.listFiles();
		if ((children == null) || (children.length > 0)) {
			throw new AttachmentStorageException(
				"Failed to clear attachments directory " + baseDir);
		}
	}
	
	private void save() throws AttachmentStorageException
	{
		saveAccountMap();
	}
	
	/* package */
	public static final String ATTACHMENTS_DIR_NAME = "attachments";
	
	private void deleteAccountDir(String dirName) 
		throws AttachmentStorageException
	{
		File accountDir = new File(baseDir, dirName);
		File[] attachments = accountDir.listFiles();
		if (attachments == null) {
			throw new AttachmentStorageException(
				"Unable to list files in directory " + accountDir);
		}
		for (int i = 0; i < attachments.length; i++) {
			if (!attachments[i].isFile()) {
				throw new AttachmentStorageException(
					"Unexpected non-file object found: " + attachments[i]);
			}
			attachments[i].delete();
		}
		accountDir.delete();
	}
	
	public long getAttachmentSizeInBytes(UniversalId attachmentId)
		throws AttachmentStorageException
	{
		return getAttachmentFile(attachmentId).length();
	}

	public long getAttachmentSizeInKb(UniversalId attachmentId) 
	throws AttachmentStorageException
	{
		long sizeInBytes = getAttachmentSizeInBytes(attachmentId);
		long sizeInKb = (sizeInBytes + 500) / 1000;
		if(sizeInKb == 0)
			sizeInKb = 1;
		return sizeInKb;
	}
	
	private File getAttachmentFile(UniversalId attachmentId) 
		throws AttachmentStorageException
	{
		File accountDir = getAccountDir(attachmentId.getAccountId());
		return new File(accountDir, attachmentId.getLocalId());
	}
	
	private File getAccountDir(String accountId)
		throws AttachmentStorageException
	{
		String accountDirName = accountMap.getProperty(accountId);
		File accountDir;
		if (accountDirName == null) {
			accountDirName = ACCOUNT_DIR_PREFIX + accountMap.size();
			accountDir = new File(baseDir, accountDirName);
			if (accountDir.exists()) {
				throw new AttachmentStorageException(
					"Corrupted attachment directory: Unexpected " +
					"directory found: " + accountDir);
			}
			if (!accountDir.mkdir()) {
				throw new AttachmentStorageException(
					"Unable to create new path " + accountDir);
			}
			accountMap.setProperty(accountId, accountDirName);
		} else {
			accountDir = new File(baseDir, accountDirName);
			if (!accountDir.exists()) {
				throw new AttachmentStorageException(
					"Corrupted attachment directory; " + accountDir +
						" does not exist but is referenced in " +
						" account map.");
			}
		}
		return accountDir;
	}
	
	private void loadAccountMap() throws AttachmentStorageException
	{
		accountMap = new Properties();
		if (accountMapFile.exists()) {
			InputStream in = null;
			try {
				in = new FileInputStream(accountMapFile);
				accountMap.load(in);
			} catch (IOException e) {
				throw new AttachmentStorageException(
					"Unable to load account map", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						throw new AttachmentStorageException(
							"Unable to close account map file", e);
					}
				}
			}
		}
	}
	
	private void saveAccountMap() throws AttachmentStorageException
	{
		OutputStream out = null;
		try {
			out = new FileOutputStream(accountMapFile);
			accountMap.store(out, "Account Map");
		} catch (IOException e) {
			throw new AttachmentStorageException(
				"Unable to save account map", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					throw new AttachmentStorageException(
						"Unable to close account map file", e);
				}
			}
		}
	}
	
	private File baseDir;
	private Properties accountMap;
	private File accountMapFile;
	
	public static final String ACCOUNT_MAP_FILE_NAME = "acctmap.txt";
	private static final String ACCOUNT_DIR_PREFIX = "acct";
}