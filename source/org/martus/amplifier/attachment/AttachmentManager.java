package org.martus.amplifier.attachment;

import java.io.InputStream;

import org.martus.common.packet.UniversalId;

/**
 * This class defines the interface through which attachments are saved
 * on the local system and retrieved.
 * 
 * @author PDAlbora
 */
public interface AttachmentManager 
{
	InputStream getAttachment(UniversalId attachmentId) 
		throws AttachmentStorageException;
	
	void putAttachment(UniversalId attachmentId, InputStream data)
		throws AttachmentStorageException;
		
	void clearAllAttachments() throws AttachmentStorageException;
	
	void close() throws AttachmentStorageException;
}
