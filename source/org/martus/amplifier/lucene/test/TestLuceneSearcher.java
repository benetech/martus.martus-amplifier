package org.martus.amplifier.lucene.test;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.martus.amplifier.lucene.LuceneBulletinIndexer;
import org.martus.amplifier.lucene.LuceneBulletinSearcher;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.amplifier.search.BulletinSearcher;
import org.martus.util.DirectoryTreeRemover;

public class TestLuceneSearcher extends AbstractSearchTestCase
{
	public TestLuceneSearcher(String name) 
	{
		super(name);
	}
	
	public void testNewSearcherWithNoIndexDirectory() throws Exception
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
	
	public void testRawLuceneDateRangeSearch() throws Exception
	{
		File indexDir = createTempDirectory();
		
		String sampleId1 = "blister";
		String sampleDate1 = "1996-05-11";
		String sampleDetails1 = "This is a test with keywords";

		String sampleId2 = "pucker";
		String sampleDate2 = "2003-09-29";
		String sampleDetails2 = "More keywords that can be found";

		boolean createIfNotThere = true;
		Analyzer analyzer = LuceneBulletinIndexer.getAnalyzer();
		IndexWriter writer = new IndexWriter(indexDir, analyzer, createIfNotThere);
		writeDocument(writer, sampleId1, sampleDate1, sampleDetails1);
		writeDocument(writer, sampleId2, sampleDate2, sampleDetails2);
		writer.close();
		
		IndexSearcher searcher = new IndexSearcher(indexDir.getPath());
		verifyLookupById(searcher, sampleId1);
		verifyLookupById(searcher, sampleId2);
		
		verifyTextSearch(searcher, "none", new String[] {});
		verifyTextSearch(searcher, "test", new String[] {sampleId1});
		verifyTextSearch(searcher, "keywords", new String[] {sampleId1, sampleId2});

		searcher.close();
		
		DirectoryTreeRemover.deleteEntireDirectoryTree(indexDir);
		assertFalse("didn't delete?", indexDir.exists());
	}

	private void verifyTextSearch(IndexSearcher searcher, String searchFor, String[] expectedIds)
		throws ParseException, IOException
	{
		String[] fields = {TAG_DETAILS};
		Analyzer analyzer = LuceneBulletinIndexer.getAnalyzer();
		Query query = MultiFieldQueryParser.parse(searchFor, fields, analyzer);
		Hits hits = searcher.search(query);
		assertEquals(expectedIds.length, hits.length());
		Vector foundIds = new Vector();
		for(int i=0; i < hits.length(); ++i)
		{
			Document foundDoc = hits.doc(i);
			foundIds.add(foundDoc.get(TAG_ID));
		}

		for(int i=0; i < expectedIds.length; ++i)
			assertContains("missing id?", expectedIds[i], foundIds);
	}

	private void verifyLookupById(IndexSearcher searcher, String sampleId1)
		throws IOException
	{
		Term term = new Term(TAG_ID, sampleId1);
		Query idQuery = new TermQuery(term);
		Hits hitsId = searcher.search(idQuery);
		assertEquals(1, hitsId.length());
	}

	private void writeDocument(
		IndexWriter writer,
		String sampleId,
		String sampleDate,
		String sampleDetails)
		throws IOException
	{
		Document docToWrite = new Document();
		docToWrite.add(Field.Keyword(TAG_ID, sampleId));	
		docToWrite.add(Field.Keyword(TAG_DATE, sampleDate));
		docToWrite.add(Field.Text(TAG_DETAILS, sampleDetails));
		writer.addDocument(docToWrite);
	}
	
	protected BulletinIndexer openBulletinIndexer()
		throws BulletinIndexException 
	{
		return new LuceneBulletinIndexer(getTestBasePath());
	}

	protected BulletinSearcher openBulletinSearcher() throws Exception 
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

	static String TAG_ID = "id";
	static String TAG_DATE = "date";
	static String TAG_DETAILS = "details";
		

}