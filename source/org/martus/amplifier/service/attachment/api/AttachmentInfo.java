package org.martus.amplifier.service.attachment.api;

import org.martus.common.UniversalId;
import org.martus.common.UniversalId.NotUniversalIdException;

public class AttachmentInfo
{
	public class InvalidAttachmentInfoException extends Exception
	{
		InvalidAttachmentInfoException(String message)
		{
			super(message);
		}
	}
	
	public AttachmentInfo(String newId, String newKey, String newLabel)
	throws InvalidAttachmentInfoException
	{
		super();
		try
		{
			id = UniversalId.createFromString(newId);
		}
		catch(NotUniversalIdException nuie)
		{
			throw new InvalidAttachmentInfoException(nuie.getMessage());
		}
		key = newKey;
		label = newLabel;
	}
	
	private UniversalId id = null;
	private String key = null;
	private String label = null;

	public UniversalId getId()
	{
		return id;
	}

	public String getKey()
	{
		return key;
	}

	public String getLabel()
	{
		return label;
	}

}
