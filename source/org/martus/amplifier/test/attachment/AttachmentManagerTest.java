package org.martus.amplifier.test.attachment;

import java.io.File;

import org.martus.amplifier.common.bulletin.UniversalBulletinId;
import org.martus.amplifier.service.attachment.AttachmentManager;

public class AttachmentManagerTest extends AbstractAttachmentTest
{

	public void testCreateDatabase()
	{		
		AttachmentManager manager = AttachmentManager.getInstance();
		manager.createDatabase();
	}

	public void testPutAndGetAttachment()
	{		
		AttachmentManager manager = AttachmentManager.getInstance();
		manager.createDatabase();
		UniversalBulletinId id = new UniversalBulletinId("test");
		File testDoc = new File("D:\\test.doc");
		manager.putAttachmentFile(id, testDoc);
		File returnDoc = manager.getAttachmentFile(id);
		assertNotNull(returnDoc);
	}
	
	public void testPutAndGetString()
	{		
		AttachmentManager manager = AttachmentManager.getInstance();
		manager.createDatabase();
		UniversalBulletinId id = new UniversalBulletinId("testString");
		manager.putAttachmentName(id, "yeah baby");
		String result = manager.getAttachmentName(id);
		assertNotNull(result);
	}
}
