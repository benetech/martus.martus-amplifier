package org.martus.amplifier.test.search;

import junit.framework.Assert;

import org.martus.amplifier.service.search.BulletinIndexer;

public class BulletinIndexerTest extends AbstractAmplifierSearchTest
{
	public void testIndexingBulletins()
	{
		BulletinIndexer indexer = BulletinIndexer.getInstance();
		indexer.indexBulletins();
		Assert.assertNotNull(indexer);
	}

}
