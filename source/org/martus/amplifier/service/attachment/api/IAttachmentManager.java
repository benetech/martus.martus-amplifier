package org.martus.amplifier.service.attachment.api;

import java.io.File;
import java.util.List;

import org.martus.common.UniversalId;

public interface IAttachmentManager
{
	public String getAttachmentName(UniversalId UniversalBulletinId);
	public void putAttachmentName(UniversalId universalId, String attachmentName);
	
	public File getAttachmentFile(UniversalId UniversalBulletinId, String filePath, String fileName);
	public void putAttachmentFile(UniversalId universalId, File attachment);

	public void putAttachmentIds(UniversalId universalId, List attachmentIdList);
	public void putAttachmentId(UniversalId universalId, UniversalId attachmentId);
	public List getAttachmentIds(UniversalId UniversalBulletinId);

}
