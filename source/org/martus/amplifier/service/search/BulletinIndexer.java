package org.martus.amplifier.service.search;

import org.martus.common.FieldDataPacket;
import org.martus.common.UniversalId;

public interface BulletinIndexer extends SearchConstants
{
	void indexFieldData(UniversalId bulletinId, FieldDataPacket fdp) 
		throws BulletinIndexException;
		
	void clearIndex() throws BulletinIndexException;
		
	void close() throws BulletinIndexException;
}




