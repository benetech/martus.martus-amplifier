package org.martus.amplifier.presentation.search;

import org.martus.amplifier.service.search.BulletinField;
import org.martus.common.FieldDataPacket;


public class SearchResultBean 
{
	/* package */
	SearchResultBean(FieldDataPacket fdp)
	{
		data = fdp;
	}
	
	public String getAuthor()
	{
		return data.get(BulletinField.TAGAUTHOR);
	}
	
	public String getEventDate()
	{
		return data.get(BulletinField.TAGEVENTDATE);
	}
	
	public String getTitle()
	{
		return data.get(BulletinField.TAGTITLE);
	}
	
	private FieldDataPacket data;
}
