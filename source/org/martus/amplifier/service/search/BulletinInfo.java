package org.martus.amplifier.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.martus.common.packet.UniversalId;

public class BulletinInfo implements Serializable
{
	public BulletinInfo(UniversalId bulletinId)
	{
		data = new HashMap();
		attachments = new ArrayList();
		this.bulletinId = bulletinId;
	}
	
	public void set(String field, Object value)
	{
		data.put(field, value);
	}
	
	public String get(String field)
	{
		return (String) data.get(field);
	}
	
	public void addAttachment(AttachmentInfo attachment)
	{
		attachments.add(attachment);
	}
	
	public Map getFields()
	{
		return data;
	}
	
	public List getAttachments()
	{
		return attachments;
	}
	
	public UniversalId getBulletinId()
	{
		return bulletinId;
	}
	
	public String getAccountId()
	{
		return bulletinId.getAccountId();
	}

	private Map data;
	private List attachments;
	private UniversalId bulletinId;

}