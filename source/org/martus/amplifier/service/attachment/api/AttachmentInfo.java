package org.martus.amplifier.service.attachment.api;



public class AttachmentInfo
{
	public AttachmentInfo(String newLocalId, String newKey, String newLabel)
	{
		localId = newLocalId;
		key = newKey;
		label = newLabel;
	}
	
	private String localId = null;
	private String key = null;
	private String label = null;

	public String getLocalId()
	{
		return localId;
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
