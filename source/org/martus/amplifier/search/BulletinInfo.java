/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.amplifier.search;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.martus.common.packet.UniversalId;

public class BulletinInfo implements Serializable
{
	public BulletinInfo(UniversalId bulletinIdToUse)
	{
		fields = new HashMap();
		attachments = new ArrayList();
		bulletinId = bulletinIdToUse;
		contactInfo = null;
	}
	
	public void set(String field, String value)
	{
		fields.put(field, value);
	}
	
	public String get(String field)
	{
		String value = (String) fields.get(field);
		if (value == null)
			value = "";
		return value;
	}
	
	public void addAttachment(AttachmentInfo attachment)
	{
		attachments.add(attachment);
	}
	
	public Map getFields()
	{
		return fields;
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

	public String getLocalId()
	{
		return bulletinId.getLocalId();
	}
	
	public void putContactInfo(File infoFile)
	{
		contactInfo = infoFile;
	}
	
	public File getContactInfo()
	{
		return contactInfo;
	}
	
	public boolean hasContactInfo()
	{
		return(contactInfo != null && contactInfo.exists());
	}

	private Map fields;
	private List attachments;
	private UniversalId bulletinId;
	private File contactInfo;

}