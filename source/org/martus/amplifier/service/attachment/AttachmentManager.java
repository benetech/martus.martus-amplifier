package org.martus.amplifier.service.attachment;

import java.io.File;

import org.martus.amplifier.common.bulletin.UniversalBulletinId;

public class AttachmentManager
{

	protected AttachmentManager()
	{
		super();
	}
	
	public void putAttachment(UniversalBulletinId universalId, File attachment)
	{}
	
	public File getAttachment(String UniversalBulletinId)
	{
		return null;
	}
	
	
	private AttachmentManager instance = new AttachmentManager();
}
