package org.martus.amplifier.search;

import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;

public interface BulletinIndexer extends SearchConstants
{
	void indexFieldData(UniversalId bulletinId, FieldDataPacket fdp) 
		throws BulletinIndexException;
		
	void clearIndex() throws BulletinIndexException;
		
	void close() throws BulletinIndexException;
}




