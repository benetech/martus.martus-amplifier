package org.martus.amplifier.service.attachment.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xalan.transformer.KeyIterator;

public class AttachmentInfoListFactory
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
			currentInfo = new AttachmentInfo((String) idIterator.next(), (String) keyIterator.next(), (String) labelIterator.next());	
			attachmentInfoList.add(currentInfo);
		}
		return attachmentInfoList;
	}
}
