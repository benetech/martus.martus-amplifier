package org.martus.amplifier.service.attachment.api;

import java.io.File;

import org.martus.common.UniversalId;

public interface IAttachmentManager
{
	public String getAttachmentName(UniversalId UniversalBulletinId);
	public void putAttachmentName(UniversalId universalId, String attachmentName);
	
	public File getAttachmentFile(UniversalId UniversalBulletinId, String filePath);
	public void putAttachmentFile(UniversalId universalId, File attachment);

}
