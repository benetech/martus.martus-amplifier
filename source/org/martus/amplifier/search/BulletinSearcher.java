package org.martus.amplifier.search;

import java.util.HashMap;

import org.martus.common.packet.UniversalId;

public interface BulletinSearcher extends SearchConstants
{
	void close() throws Exception;
	
	Results search(String field, String queryString) throws Exception;		
		
	Results search(HashMap fields) throws Exception; 				
		
	BulletinInfo lookup(UniversalId bulletinId) throws Exception;	
}