package org.martus.amplifier.service.search;

import java.io.Serializable;

/**
 * @author pdalbora
 */
public class AttachmentInfo implements Serializable
{
	public AttachmentInfo(String localId, String label)
	{
		this.localId = localId;
		this.label = label;
	}
	
	public String getLocalId()
	{
		return localId;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	private String localId;
	private String label;
}
