package org.martus.amplifier.presentation.attachment;

import org.apache.lucene.document.Document;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.search.IBulletinConstants;
import org.martus.common.UniversalId;

public class AttachmentBean implements IBulletinConstants
{

	public AttachmentBean()
	{
		super();
	}
	
	public void setDocument(Document newDoc)
	{
		System.out.println("hi");
		doc = newDoc;
	}
	
	public boolean bulletinHasAttachments()
	{
		boolean bulletinHasAttachments = true;
		try
		{
			String universalIdString = doc.get(UNIVERSAL_ID_FIELD);
			UniversalId universalId = UniversalId.createFromString(universalIdString);
			bulletinHasAttachments = 
				AttachmentManager.getInstance().hasAttachments(universalId);
		}
		catch(Exception e)
		{
			System.out.println("Not universal id");
		}
		return bulletinHasAttachments;
	}
	
	private Document doc = null;
}
