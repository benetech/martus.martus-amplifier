package org.martus.amplifier.search;

import java.util.Map;

import org.martus.common.packet.UniversalId;

public interface BulletinSearcher extends SearchConstants
{
	void close() throws Exception;
	
	Results search(String field, String queryString) throws Exception;		
		
	Results search(Map fields) throws Exception; 				
		
	BulletinInfo lookup(UniversalId bulletinId) throws Exception;	
}