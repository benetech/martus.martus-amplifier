
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
		
		public int getCount() throws BulletinIndexException
		{
			return hits.length();
		}

		public BulletinInfo getBulletinInfo(int n)
			throws BulletinIndexException 
		{
			Document doc;
			try {
				doc = hits.doc(n);
			} catch (IOException ioe) {
				throw new BulletinIndexException(
					"Unable to retrieve FieldDataPacket " + n, ioe);
			}
			BulletinInfo info = new BulletinInfo(getBulletinId(doc));
			
			addAllEmptyFields(info);
			addFields(info, doc);
			addAttachments(info, doc);
			return info;
		}
		
		private static void addAllEmptyFields(BulletinInfo info)
			throws BulletinIndexException
		{
			String[] fieldIds = BulletinField.getSearchableXmlIds();
			for (int i = 0; i < fieldIds.length; i++) {
				BulletinField field = BulletinField.getFieldByXmlId(fieldIds[i]);
				if (field == null) {
					throw new BulletinIndexException(
						"Unknown field " + fieldIds[i]);
				}
				info.set(field.getIndexId(), "");
			}
		}
		
		private static void addFields(BulletinInfo info, Document doc) 
			throws BulletinIndexException
		{
			String[] fieldIds = BulletinField.getSearchableXmlIds();
			for (int i = 0; i < fieldIds.length; i++) {
				BulletinField field = BulletinField.getFieldByXmlId(fieldIds[i]);
				if (field == null) {
					throw new BulletinIndexException(
						"Unknown field " + fieldIds[i]);
				}
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
			if (attachmentsString != null) {
				String[] attachmentsAssocList = 
					attachmentsString.split(ATTACHMENT_LIST_SEPARATOR);
				if ((attachmentsAssocList.length % 2) != 0) {
					throw new BulletinIndexException(
						"Invalid attachments string found: " + 
						attachmentsString);
				}
				for (int i = 0; i < attachmentsAssocList.length; i += 2) {
					AttachmentInfo attachmentInfo = new AttachmentInfo(
						bulletinInfo.getAccountId(),
						attachmentsAssocList[i],
						attachmentsAssocList[i + 1]);
					
					setAttachmentSize(attachmentInfo);
					bulletinInfo.addAttachment(attachmentInfo);
				}
			}
		}
		
		private static void setAttachmentSize(AttachmentInfo attachmentInfo)
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
			UniversalId uId = UniversalId.createFromAccountAndLocalId(attachmentInfo.getAccountId(), attachmentInfo.getLocalId());
			try
			{
				attachmentInfo.setSize(manager.getAttachmentSizeInKb(uId));
			}
			catch (Exception e)
			{
				attachmentInfo.setSize(-1);
				e.printStackTrace();
			}
		}

		private static UniversalId getBulletinId(Document doc) 
			throws BulletinIndexException
		{
			String bulletinIdString = doc.get(
				BULLETIN_UNIVERSAL_ID_INDEX_FIELD);
			if (bulletinIdString == null) {
				throw new BulletinIndexException(
					"Did not find bulletin universal id");
			}
			
			try {
				return UniversalId.createFromString(bulletinIdString);
			} catch (NotUniversalIdException e) {
				throw new BulletinIndexException(
					"Invalid bulletin universal id found", e);
			}
		}
		
		public LuceneResults(Hits hits)
		{
			this.hits = hits;
		}
		
		private Hits hits;		
	}
	
