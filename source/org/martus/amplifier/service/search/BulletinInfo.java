package org.martus.amplifier.service.search;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.martus.common.UniversalId;

public class BulletinInfo extends AbstractMap implements Serializable
{
	public BulletinInfo(UniversalId bulletinId)
	{
		data = new HashMap();
		attachments = new ArrayList();
		this.bulletinId = bulletinId;
	}
	
	public Set entrySet() 
	{
		return data.entrySet();
	}
	
	public Object put(Object key, Object value)
	{
		return data.put(key, value);
	}
	
	public Object get(Object key)
	{
		return data.get(key);
	}
	
	public boolean containsKey(Object key) 
	{
		return data.containsKey(key);
	}
	
	public void addAttachment(AttachmentInfo attachment)
	{
		attachments.add(attachment);
	}
	
	public List getAttachments()
	{
		return Collections.unmodifiableList(attachments);
	}
	
	public UniversalId getBulletinId()
	{
		return bulletinId;
	}

	private Map data;
	private List attachments;
	private UniversalId bulletinId;

}