package org.martus.amplifier.service.search;

import java.util.Date;

import org.martus.common.FieldDataPacket;
import org.martus.common.UniversalId;

public interface BulletinSearcher extends SearchConstants
{
	void close() throws BulletinIndexException;
	
	Results searchField(String field, String queryString) 
		throws BulletinIndexException;
	Results searchDateRange(String field, Date startDate, Date endDate)
		throws BulletinIndexException;
	FieldDataPacket getBulletinData(UniversalId bulletinId)
		throws BulletinIndexException;
	
	interface Results
	{
		int getCount() throws BulletinIndexException;
		FieldDataPacket getFieldDataPacket(int n) 
			throws BulletinIndexException;
	}
	
	
}