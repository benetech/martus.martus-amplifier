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
import org.martus.common.FieldSpec;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusCrypto.DecryptionException;
import org.martus.common.crypto.MartusCrypto.NoKeyPairException;
import org.martus.common.packet.BulletinHeaderPacket;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;
import org.martus.common.packet.Packet.InvalidPacketException;
import org.martus.common.packet.Packet.SignatureVerificationException;
import org.martus.common.packet.Packet.WrongPacketTypeException;
import org.martus.util.ZipEntryInputStream;
import org.martus.util.Base64.InvalidBase64Exception;


/**
 * An instance of this class is responsible for taking a zip file
 * representing a bulletin, indexing its public field data, and
 * storing its attachments.
 * 
 * @author PDAlbora
 */
public class BulletinExtractor
{
	public BulletinExtractor(
		AttachmentManager attachmentManager, 
		BulletinIndexer bulletinIndexer,
		MartusCrypto verifier)
	{
		this.attachmentManager = attachmentManager;
		this.bulletinIndexer = bulletinIndexer;
		this.verifier = verifier;
	}
	
	public void extractAndStoreBulletin(File bulletinFile) 
		throws IOException, SignatureVerificationException, 
			BulletinIndexException, NoKeyPairException, 
			InvalidPacketException, DecryptionException, 
			WrongPacketTypeException, AttachmentStorageException, 
			InvalidBase64Exception
	{
		ZipFile bulletinZipFile = new ZipFile(bulletinFile);
		try {
			BulletinHeaderPacket bhp = 
				BulletinHeaderPacket.loadFromZipFile(bulletinZipFile, verifier);
			
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
			
		FieldSpec[] fieldSpec = BulletinField.getDefaultSearchFieldSpecs();
		FieldDataPacket fdp = new FieldDataPacket(fieldUid, fieldSpec);
		
		ZipEntry fieldDataEntry = bulletinZipFile.getEntry(fieldDataPacketId);
		if (fieldDataEntry == null) {
			throw new IOException(
				"No entry " + fieldDataPacketId + " found for account " + 
				bhp.getAccountId());
		}
		
		fdp.loadFromXml(
			new ZipEntryInputStream(bulletinZipFile, fieldDataEntry),
			verifier);
			
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
			proxy = AttachmentProxy.createFileProxyFromAttachmentPacket(
				new ZipEntryInputStream(bulletinZipFile, attachmentEntry), 
				proxy, verifier);
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
	private MartusCrypto verifier;
}