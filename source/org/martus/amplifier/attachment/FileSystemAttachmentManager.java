/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.amplifier.attachment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.martus.amplifier.main.MartusAmplifier;
import org.martus.common.MartusUtilities.FileVerificationException;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ServerFileDatabase;
import org.martus.common.database.FileDatabase.MissingAccountMapException;
import org.martus.common.database.FileDatabase.MissingAccountMapSignatureException;
import org.martus.common.packet.UniversalId;


public class FileSystemAttachmentManager implements AttachmentManager
{
	public FileSystemAttachmentManager(String baseDir) throws FileVerificationException, MissingAccountMapException, MissingAccountMapSignatureException
	{
		this(baseDir, MartusAmplifier.security);
	}
	
	public FileSystemAttachmentManager(String baseDir, MartusCrypto crypto) throws FileVerificationException, MissingAccountMapException, MissingAccountMapSignatureException
	{
		db = new ServerFileDatabase(new File(baseDir), crypto);
		db.initialize();
	}
	public InputStream getAttachment(UniversalId attachmentId) throws AttachmentStorageException
	{
		try
		{
			return db.openInputStream(new DatabaseKey(attachmentId), db.security);
		}
		catch (Exception e)
		{
			throw new AttachmentStorageException(e);
		}
	}

	public long getAttachmentSizeInKb(UniversalId attachmentId) throws AttachmentStorageException
	{
		try
		{
			int sizeInBytes = db.getRecordSize(new DatabaseKey(attachmentId));
			int sizeInKb = sizeInBytes / Kbytes;
			if(sizeInKb == 0)
				sizeInKb = 1;
			return sizeInKb;
		}
		catch (Exception e)
		{
			throw new AttachmentStorageException(e);
		}
	}

	public long getAttachmentSizeInBytes(UniversalId attachmentId) throws AttachmentStorageException
	{
		try
		{
			return db.getRecordSize(new DatabaseKey(attachmentId));
		}
		catch (Exception e)
		{
			throw new AttachmentStorageException(e);
		}
	}

	public void putAttachment(UniversalId attachmentId, InputStream data) throws AttachmentStorageException
	{
		try
		{
			db.writeRecord(new DatabaseKey(attachmentId), data);
		}
		catch (Exception e)
		{
			throw new AttachmentStorageException(e);
		}
	}

	public void clearAllAttachments() throws AttachmentStorageException
	{
		try
		{
			db.deleteAllData();
		}
		catch (Exception e)
		{
			throw new AttachmentStorageException(e);
		}		
	}
	
	public File getContactInfoFile(String accountId) throws IOException
	{
		return db.getContactInfoFile(accountId);	
	}
	
	private ServerFileDatabase db;
	public static final int Kbytes = 1024;
	
}