/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
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
package org.martus.amplifier.lucene.test;

import java.util.Vector;

import org.martus.amplifier.main.LanguagesIndexedList;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.amplifier.search.BulletinSearcher;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;


public class TestLuceneBulletinIndexer  extends CommonSearchTest
{
	public TestLuceneBulletinIndexer(String name)
	{
		super(name);
	}

	public void testClearIndex() throws Exception
	{
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateFieldDataPacket(bulletinId);
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.indexFieldData(bulletinId, fdp);
		} 
		finally 
		{
			indexer.close();
		}
		
		BulletinSearcher searcher = openBulletinSearcher();
		try 
		{
			assertNotNull("Didn't find indexed bulletin", 
				searcher.lookup(bulletinId));
		} 
		finally 
		{
			searcher.close();
		}
		
		indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
		} 
		finally 
		{
			indexer.close();
		}
		
		searcher = openBulletinSearcher();
		try 
		{
			assertNull("Found an indexed bulletin after clearing!", 
				searcher.lookup(bulletinId));
		} 
		finally 
		{
			searcher.close();
		} 		
	}

	public void testNewIndexerWithNoIndexDirectory()
		throws BulletinIndexException
	{
		deleteIndexDir();
		BulletinIndexer indexer = openBulletinIndexer();
		indexer.close();
	}
	
	public void testIndexingLanguages() throws Exception
	{
		LanguagesIndexedList.languagesIndexedSingleton = new LanguagesIndexedList(createTempFile());
		LanguagesIndexedList.languagesIndexedSingleton.loadLanguagesAlreadyIndexed();
		
		Vector languages = LanguagesIndexedList.languagesIndexedSingleton.getListOfLanguagesIndexed();
		assertEquals("Should not have any yet file exists but is empty", 0, languages.size());
		
		UniversalId bulletinId = UniversalId.createDummyUniversalId();
		FieldDataPacket fdp = generateSampleData(bulletinId);		
		BulletinIndexer indexer = openBulletinIndexer();
		try 
		{
			indexer.clearIndex();
			indexer.indexFieldData(bulletinId, fdp);
		} 
		finally 
		{
			indexer.close();
		}
		languages = LanguagesIndexedList.languagesIndexedSingleton.getListOfLanguagesIndexed();
		assertEquals("Should now have english", 1, languages.size());
		assertTrue("Should contain english", languages.contains("en"));
		
	}


}
