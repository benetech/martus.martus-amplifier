package org.martus.amplifier.test.attachment;

import java.io.File;

import org.martus.common.UniversalId;
import org.martus.amplifier.common.configuration.AmplifierConfiguration;
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
		UniversalId id = UniversalId.createDummyUniversalId();
		File testDoc = 
			new File(AmplifierConfiguration.getInstance().getTestDataPath() + File.separator + "test.doc");
		manager.putAttachmentFile(id, testDoc);
		File returnDoc = manager.getAttachmentFile(id, AmplifierConfiguration.getInstance().getTestOutputPath() + File.separator + "testoutput.doc");
		assertNotNull(returnDoc);
	}
	
	public void testPutAndGetString()
	{		
		AttachmentManager manager = AttachmentManager.getInstance();
		manager.createDatabase();
		UniversalId id = UniversalId.createDummyUniversalId();
		manager.putAttachmentName(id, "yeah baby");
		String result = manager.getAttachmentName(id);
		assertNotNull(result);
	}
}
