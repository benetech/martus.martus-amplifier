package org.martus.amplifier.search;

import org.martus.amplifier.common.SearchFields;
import org.martus.common.packet.UniversalId;

public interface BulletinSearcher extends SearchConstants
{
	void close() throws BulletinIndexException;
	
	Results search(String field, String queryString) 
		throws BulletinIndexException;		
		
	Results search(String field, SearchFields fields)
		throws BulletinIndexException; 				
		
	BulletinInfo lookup(UniversalId bulletinId)
		throws BulletinIndexException;
	
	interface Results
	{
		int getCount() throws BulletinIndexException;
		BulletinInfo getBulletinInfo(int n) 
			throws BulletinIndexException;
	}
	
	
}