package org.martus.amplifier.service.search.lucene;

import java.io.File;

import org.martus.amplifier.service.search.TestAbstractSearch;
import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinIndexer;
import org.martus.amplifier.service.search.BulletinSearcher;

public class TestLuceneSearcher extends TestAbstractSearch
{
	public TestLuceneSearcher(String name) 
	{
		super(name);
	}
	
	public void testNewSearcherWithNoIndexDirectory()
		throws BulletinIndexException
	{
		deleteIndexDir();
		BulletinSearcher searcher = openBulletinSearcher();
		searcher.close();
	}
	
	public void testNewIndexerWithNoIndexDirectory()
		throws BulletinIndexException
	{
		deleteIndexDir();
		BulletinIndexer indexer = openBulletinIndexer();
		indexer.close();
	}
	
	protected BulletinIndexer openBulletinIndexer()
		throws BulletinIndexException 
	{
		return new LuceneBulletinIndexer(getTestBasePath());
	}

	protected BulletinSearcher openBulletinSearcher()
		throws BulletinIndexException 
	{
		return new LuceneBulletinSearcher(getTestBasePath());
	}
	
	private void deleteIndexDir() throws BulletinIndexException
	{
		File indexDir = 
			LuceneBulletinIndexer.getIndexDir(getTestBasePath());
		File[] indexFiles = indexDir.listFiles();
		for (int i = 0; i < indexFiles.length; i++) {
			File indexFile = indexFiles[i];
			if (!indexFile.isFile()) {
				throw new BulletinIndexException(
					"Unexpected non-file encountered: " + indexFile);
			}
			indexFile.delete();
		}
		indexDir.delete();
	}

}