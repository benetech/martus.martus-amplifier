package org.martus.amplifier.test.attachment;

import java.io.File;
import java.util.List;

import org.martus.common.UniversalId;
import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.attachment.AttachmentManager;

public class AttachmentManagerTest extends AbstractAttachmentTest
{
	public void testPutAndGetId()
	{		
		AttachmentManager manager = AttachmentManager.getInstance();
		UniversalId id = UniversalId.createDummyUniversalId();
		UniversalId attachmentId = UniversalId.createDummyUniversalId();
		manager.putAttachmentId(id, attachmentId);
		List ids = manager.getAttachmentIds(id);
		assertNotNull(ids);
		assertTrue(ids.size() == 1);
	}

	public void testPutAndGetAttachment()
	{		
		AttachmentManager manager = AttachmentManager.getInstance();
		UniversalId id = UniversalId.createDummyUniversalId();
		File testDoc = 
			new File(AmplifierConfiguration.getInstance().buildAmplifierWorkingPath(ATTACHMENT_TEST_FOLDER, "test.doc"));
		manager.putAttachmentFile(id, testDoc);
		File returnDoc = manager.getAttachmentFile(id, AmplifierConfiguration.getInstance().buildAmplifierWorkingPath(ATTACHMENT_TEST_FOLDER, "testoutput.doc"));
		assertNotNull(returnDoc);
	}
	
	public void testPutAndGetString()
	{		
		AttachmentManager manager = AttachmentManager.getInstance();
		UniversalId id = UniversalId.createDummyUniversalId();
		manager.putAttachmentName(id, "yeah baby");
		String result = manager.getAttachmentName(id);
		assertNotNull(result);
	}
	
	private static final String ATTACHMENT_TEST_FOLDER = "attachment_test_output";
}
