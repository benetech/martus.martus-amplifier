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
package org.martus.amplifier.lucene;

import java.io.IOException;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.martus.amplifier.attachment.AttachmentManager;
import org.martus.amplifier.attachment.AttachmentStorageException;
import org.martus.amplifier.attachment.FileSystemAttachmentManager;
import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.amplifier.search.AttachmentInfo;
import org.martus.amplifier.search.BulletinField;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.search.Results;
import org.martus.amplifier.search.SearchConstants;
import org.martus.common.packet.UniversalId;
import org.martus.common.packet.UniversalId.NotUniversalIdException;


public class LuceneResults implements Results, LuceneSearchConstants, SearchConstants
{
	public LuceneResults(Hits hits)
	{
		this.hits = hits;
	}
		
	public int getCount() throws BulletinIndexException
	{
		return hits.length();
	}

	public BulletinInfo getBulletinInfo(int n)
		throws BulletinIndexException 
	{
		Document doc;
		try
		{
			doc = hits.doc(n);
		}
		catch (IOException ioe)
		{
			throw new BulletinIndexException(
				"Unable to retrieve FieldDataPacket " + n,
				ioe);
		}
		BulletinInfo info = new BulletinInfo(getBulletinId(doc));
		
		addAllEmptyFields(info);
		addFields(info, doc);
		addAttachments(info, doc);
		return info;
	}
	
	private static BulletinField getField(String fieldId) throws BulletinIndexException
	{
		BulletinField field = BulletinField.getFieldByXmlId(fieldId);
		if (field == null) 
		{
			throw new BulletinIndexException(
				"Unknown field " + fieldId);
		}
		return field;
	}
	
	private static void addAllEmptyFields(BulletinInfo info)
		throws BulletinIndexException
	{
		String[] fieldIds = BulletinField.getSearchableXmlIds();
		for (int i = 0; i < fieldIds.length; i++) 
		{
			BulletinField field = getField(fieldIds[i]);
			info.set(field.getIndexId(), "");
		}
	}			
	
	private static void addFields(BulletinInfo info, Document doc) 
		throws BulletinIndexException
	{
		String[] fieldIds = BulletinField.getSearchableXmlIds();
		for (int i = 0; i < fieldIds.length; i++) 
		{
			BulletinField field = getField(fieldIds[i]);
			
			String value = doc.get(field.getIndexId());
			if (value != null) 
			{
				if (field.isDateField()) 														
					value = SEARCH_DATE_FORMAT.format(DateField.stringToDate(value));
			 	
				if (field.isDateRangeField())
				{
					String startDate = LuceneBulletinSearcher.getStartDateRange(value);
					info.set(field.getIndexId()+"-start", startDate);
					String endDate = LuceneBulletinSearcher.getEndDateRange(value);
					if(endDate != null)
						info.set(field.getIndexId()+"-end", endDate);
					continue;
				}
					
				info.set(field.getIndexId(), value);
			}
		}
	}
	

	private static void addAttachments(BulletinInfo bulletinInfo, Document doc) 
		throws BulletinIndexException
	{
		String attachmentsString = doc.get(ATTACHMENT_LIST_INDEX_FIELD);
		if (attachmentsString != null) 
		{
			String[] attachmentsAssocList = 
				attachmentsString.split(ATTACHMENT_LIST_SEPARATOR);
			if ((attachmentsAssocList.length % 2) != 0) 
			{
				throw new BulletinIndexException(
					"Invalid attachments string found: " + 
					attachmentsString);
			}
			for (int i = 0; i < attachmentsAssocList.length; i += 2) 
			{
				String accountId = bulletinInfo.getAccountId();
				String localId = attachmentsAssocList[i];
				UniversalId uId = UniversalId.createFromAccountAndLocalId(accountId, localId);
				long size = getAttachmentSizeInKb(uId);
				
				String attachmentLabel = attachmentsAssocList[i + 1];
				AttachmentInfo attachmentInfo = new AttachmentInfo(uId, attachmentLabel, size);
				bulletinInfo.addAttachment(attachmentInfo);
			}
		}
	}
	
	private static long getAttachmentSizeInKb(UniversalId uId)
	{
		AttachmentManager manager = MartusAmplifier.attachmentManager;
		
		//MartusAmplifier.attachmentManager is set in tests but live code since
		//Two different classloaders construct the MartusAmplifier && DownloadAttachment
		//requesting the static member results in null 
		if(manager == null) 
		{
			String basePath = AmplifierConfiguration.getInstance().getBasePath();
			try
			{
				manager = new FileSystemAttachmentManager(basePath);
			}
			catch (AttachmentStorageException e)
			{
				e.printStackTrace();
			}
		}
		long size = -1;
		try
		{
			size = manager.getAttachmentSizeInKb(uId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return size;
	}

	private static UniversalId getBulletinId(Document doc) 
		throws BulletinIndexException
	{
		String bulletinIdString = doc.get(BULLETIN_UNIVERSAL_ID_INDEX_FIELD);
		if (bulletinIdString == null)
		{
			throw new BulletinIndexException("Did not find bulletin universal id");
		}

		try
		{
			return UniversalId.createFromString(bulletinIdString);
		}
		catch (NotUniversalIdException e)
		{
			throw new BulletinIndexException(
				"Invalid bulletin universal id found",
				e);
		}
	}
	private Hits hits;		
}
	