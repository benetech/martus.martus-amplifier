package org.martus.amplifier.service.attachment.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.xalan.transformer.KeyIterator;
import org.martus.amplifier.service.attachment.IAttachmentConstants;
import org.martus.amplifier.service.attachment.api.AttachmentInfo.InvalidAttachmentInfoException;

public class AttachmentInfoListFactory
implements IAttachmentConstants
{

	private AttachmentInfoListFactory(List initialInfoList)
	{
		super();
	}
	
	public static List createList(List ids, List keys, List labels)
	{
		Iterator idIterator, keyIterator, labelIterator;
		if(ids == null || keys == null || labels == null)
			return null;
		idIterator = ids.iterator();
		keyIterator = ids.iterator();
		labelIterator = labels.iterator();
		List attachmentInfoList = new ArrayList();
		AttachmentInfo currentInfo = null;
		while(idIterator.hasNext() && keyIterator.hasNext() && labelIterator.hasNext())
		{
			try
			{
				currentInfo = new AttachmentInfo((String) idIterator.next(), (String) keyIterator.next(), (String) labelIterator.next());	
				attachmentInfoList.add(currentInfo);
			}
			catch(InvalidAttachmentInfoException iaie)
			{
				logger.severe("Invalid Attachment Info: " + iaie.getMessage());
			}
		}
		return attachmentInfoList;
	}
	
	private static Logger logger = Logger.getLogger(ATTACHMENT_LOGGER);
}
