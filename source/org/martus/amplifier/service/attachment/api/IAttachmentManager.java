package org.martus.amplifier.service.attachment.api;

import java.io.File;

import org.martus.amplifier.common.bulletin.UniversalBulletinId;

public interface IAttachmentManager
{
	public String getAttachmentName(UniversalBulletinId UniversalBulletinId);
	public void putAttachmentName(UniversalBulletinId universalId, String attachmentName);
	
	public File getAttachmentFile(UniversalBulletinId UniversalBulletinId, String filePath);
	public void putAttachmentFile(UniversalBulletinId universalId, File attachment);

}
