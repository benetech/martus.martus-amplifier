package org.martus.amplifier.service.datasynch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.AttachmentStorageException;
import org.martus.amplifier.service.search.BulletinField;
import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinIndexer;
import org.martus.common.AttachmentPacket;
import org.martus.common.AttachmentProxy;
import org.martus.common.BulletinHeaderPacket;
import org.martus.common.FieldDataPacket;
import org.martus.common.MartusUtilities;
import org.martus.common.UniversalId;
import org.martus.common.ZipEntryInputStream;
import org.martus.common.Base64.InvalidBase64Exception;
import org.martus.common.MartusCrypto.DecryptionException;
import org.martus.common.MartusCrypto.NoKeyPairException;
import org.martus.common.Packet.InvalidPacketException;
import org.martus.common.Packet.SignatureVerificationException;
import org.martus.common.Packet.WrongPacketTypeException;


/**
 * An instance of this class is responsible for taking a zip file
 * representing a bulletin, indexing its public field data, and
 * storing its attachments.
 * 
 * @author PDAlbora
 */
public class BulletinProcessor
{
	public BulletinProcessor(
		AttachmentManager attachmentManager, 
		BulletinIndexer bulletinIndexer)
	{
		this.attachmentManager = attachmentManager;
		this.bulletinIndexer = bulletinIndexer;
	}
	
	public void processBulletin(File bulletinFile) 
		throws IOException, SignatureVerificationException, 
			BulletinIndexException, NoKeyPairException, 
			InvalidPacketException, DecryptionException, 
			WrongPacketTypeException, AttachmentStorageException, 
			InvalidBase64Exception
	{
		ZipFile bulletinZipFile = new ZipFile(bulletinFile);
		try {
			BulletinHeaderPacket bhp = 
				BulletinHeaderPacket.loadFromZipFile(bulletinZipFile, null);
			
			FieldDataPacket fdp = indexFieldData(bhp, bulletinZipFile);
			
			storeAttachments(fdp.getAttachments(), bulletinZipFile);
		} finally {
			bulletinZipFile.close();
		}
	}
	
	private FieldDataPacket indexFieldData(
		BulletinHeaderPacket bhp, ZipFile bulletinZipFile) 
		throws SignatureVerificationException, NoKeyPairException, 
			WrongPacketTypeException, DecryptionException, 
			InvalidPacketException, IOException, BulletinIndexException
	{
		String fieldDataPacketId = bhp.getFieldDataPacketId();
		UniversalId fieldUid = UniversalId.createFromAccountAndLocalId(
			bhp.getAccountId(), fieldDataPacketId);
			
		String[] fieldTags = BulletinField.getSearchableXmlIds();
		FieldDataPacket fdp = new FieldDataPacket(fieldUid, fieldTags);
		
		ZipEntry fieldDataEntry = bulletinZipFile.getEntry(fieldDataPacketId);
		if (fieldDataEntry == null) {
			throw new IOException(
				"No entry " + fieldDataPacketId + " found for account " + 
				bhp.getAccountId());
		}
		
		fdp.loadFromXml(
			new ZipEntryInputStream(bulletinZipFile, fieldDataEntry),
			null);
			
		bulletinIndexer.indexFieldData(bhp.getUniversalId(), fdp);
		
		return fdp;
	}
	
	private void storeAttachments(
		AttachmentProxy[] proxies, ZipFile bulletinZipFile) 
		throws IOException, WrongPacketTypeException, 
			SignatureVerificationException, InvalidPacketException, 
			InvalidBase64Exception, AttachmentStorageException
	{
		for (int i = 0; i < proxies.length; i++) {
			AttachmentProxy proxy = proxies[i];
			UniversalId attachmentId = proxy.getUniversalId();
			ZipEntry attachmentEntry = bulletinZipFile.getEntry(attachmentId.getLocalId());
			if (attachmentEntry == null) {
				throw new IOException(
					"No entry " + attachmentId.getLocalId() + 
					" found for account " + attachmentId.getAccountId());
			}
			proxy = MartusUtilities.createFileProxyFromAttachmentPacket(
				new ZipEntryInputStream(bulletinZipFile, attachmentEntry), 
				proxy, null);
			InputStream attachmentData = new FileInputStream(proxy.getFile());
			try {
				attachmentManager.putAttachment(attachmentId, attachmentData);
			} finally {
				attachmentData.close();
			}
		}
	}
	
	private AttachmentManager attachmentManager;
	private BulletinIndexer bulletinIndexer;
}