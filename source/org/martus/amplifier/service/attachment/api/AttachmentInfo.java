package org.martus.amplifier.service.attachment.api;

public class AttachmentInfo
{

	public AttachmentInfo(String newId, String newKey, String newLabel)
	{
		super();
		id = newId;
		key = newKey;
		label = newLabel;
	}
	
	private String id = null;
	private String key = null;
	private String label = null;

	public String getId()
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
