package org.martus.amplifier.test.attachment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.common.Base64;
import org.martus.common.UniversalId;
import org.martus.common.Base64.InvalidBase64Exception;

public class AttachmentManagerTest extends AbstractAttachmentTest
{
	public AttachmentManagerTest(String name)
	{
		super(name);
	}
	
	public void testExtractAttachment()
	{		
		AttachmentManager manager = AttachmentManager.getInstance();
		String attachmentId = "A-40984b-eeeb80ef32--7fa6";
		String testAttachmentXmlFilePath = 
			AmplifierConfiguration.getInstance().getBasePath() + "\\testdata\\" + attachmentId;
		File xmlFile = new File(testAttachmentXmlFilePath);
		String sessionKey = "++WSe6fWUsf3UsQHr6nrr/cxxi0wa3s8LZPqP40ZwhE=";
		byte[] decryptedSessionKey = null;
		try
		{
			decryptedSessionKey = Base64.decode(sessionKey);
		}
		catch(InvalidBase64Exception ibe)
		{}
		String filePath = 
			AmplifierConfiguration.getInstance().buildAmplifierWorkingPath("attachment_testing", attachmentId);
		File destinationFile = new File(filePath);
		manager.extractAttachment(xmlFile, decryptedSessionKey, destinationFile);	
}

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
	
	public void testMultipleAttachments()
	{
		AttachmentManager manager = AttachmentManager.getInstance();
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		UniversalId attachmentId1 = UniversalId.createDummyUniversalId();
		UniversalId attachmentId2 = UniversalId.createDummyUniversalId();
		manager.putAttachmentIds(
			bulletinId, 
			Arrays.asList(new Object[] { attachmentId1, attachmentId2 }));
		List ids = manager.getAttachmentIds(bulletinId);
		assertNotNull(ids);
		assertEquals(2, ids.size());
	}

	public void testPutAndGetAttachment()
	{		
		AttachmentManager manager = AttachmentManager.getInstance();
		UniversalId id = UniversalId.createDummyUniversalId();
		File testDoc = 
			new File(AmplifierConfiguration.getInstance().buildAmplifierBasePath(ATTACHMENT_TEST_INPUT_FOLDER, "test.doc"));
		manager.putAttachmentFile(id, testDoc);
		File returnDoc = manager.getAttachmentFile(id, AmplifierConfiguration.getInstance().buildAmplifierWorkingPath(ATTACHMENT_TEST_OUTPUT_FOLDER), "testoutput.doc");
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
	
	private static final String ATTACHMENT_TEST_OUTPUT_FOLDER = "attachment_test_output";
	private static final String ATTACHMENT_TEST_INPUT_FOLDER = "testdata";
}
