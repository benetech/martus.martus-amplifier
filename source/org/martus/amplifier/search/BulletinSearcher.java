package org.martus.amplifier.search;

import java.util.HashMap;

import org.martus.common.packet.UniversalId;

public interface BulletinSearcher extends SearchConstants
{
	void close() throws Exception;
	
	Results search(String field, String queryString) 
		throws BulletinIndexException;		
		
	Results search(HashMap fields)
		throws BulletinIndexException; 				
		
	BulletinInfo lookup(UniversalId bulletinId)
		throws BulletinIndexException;	
}