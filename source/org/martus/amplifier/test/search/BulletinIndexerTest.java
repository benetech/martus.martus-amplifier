package org.martus.amplifier.test.search;

import org.martus.amplifier.service.search.BulletinIndexer;
import org.martus.common.TestCaseEnhanced;

public class BulletinIndexerTest extends TestCaseEnhanced
{
	public BulletinIndexerTest(String name)
	{
		super(name);
	}
	
	public void testIndexingBulletins()
	{
		BulletinIndexer indexer = BulletinIndexer.getInstance();
		indexer.indexBulletins();
		assertNotNull(indexer);
	}

}
