package org.martus.amplifier.service.search.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.martus.amplifier.service.search.BulletinField;
import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinSearcher;
import org.martus.amplifier.service.search.BulletinSearcher.Results;
import org.martus.common.AttachmentProxy;
import org.martus.common.FieldDataPacket;
import org.martus.common.UniversalId;
import org.martus.common.UniversalId.NotUniversalIdException;

public class LuceneBulletinSearcher 
	implements BulletinSearcher, LuceneSearchConstants
{
	public LuceneBulletinSearcher(String baseDirName) 
		throws BulletinIndexException
	{
		File indexDir = new File(baseDirName);
		indexDir.mkdirs();
		try {
			LuceneBulletinIndexer.createIndexIfNecessary(indexDir);
			searcher = new IndexSearcher(baseDirName);
		} catch (IOException e) {
			throw new BulletinIndexException(
				"Could not create LuceneBulletinSearcher", e);
		}
	}
	
	public void close() throws BulletinIndexException
	{
		try {
			searcher.close();
		} catch (IOException e) {
			throw new BulletinIndexException(
				"Unable to close the searcher: ", e);
		}
	}
	
	public Results searchDateRange(String field, Date startDate, Date endDate)
		throws BulletinIndexException 
	{
		Term startTerm = new Term(field, 
			DateField.dateToString(startDate));
		Term endTerm = new Term(field,						
			DateField.dateToString(endDate)); 
		Query query = new RangeQuery(startTerm, endTerm, true);
		
		try {
			return new LuceneResults(searcher.search(query));
		} catch (IOException e) {
			throw new BulletinIndexException(
				"An error occurred while searching query " + 
					query.toString(field), 
				e);
		}
	}

	public Results searchField(String field, String queryString)
		throws BulletinIndexException 
	{
		Query query;
		try
		{
			query = QueryParser.parse(
				queryString, field, 
				LuceneBulletinIndexer.getAnalyzer());
		}
		catch(ParseException pe)
		{
			throw new BulletinIndexException(
				"Improperly formed query: " + queryString, pe);
		}
		
		try {
			return new LuceneResults(searcher.search(query));
		} catch (IOException e) {
			throw new BulletinIndexException(
				"An error occurred while searching query " + 
					query.toString(field), 
				e);
		}
	}
	
	public FieldDataPacket getBulletinData(UniversalId bulletinId)
		throws BulletinIndexException 
	{
		Term term = new Term(
			BULLETIN_UNIVERSAL_ID_INDEX_FIELD, bulletinId.toString());
		Query query = new TermQuery(term);
		
		Results results;
		try {
			results = new LuceneResults(searcher.search(query));
		} catch (IOException e) {
			throw new BulletinIndexException(
				"An error occurred while searching query " + 
					query.toString(BULLETIN_UNIVERSAL_ID_INDEX_FIELD), 
				e);
		}
		
		int numResults = results.getCount();
		if (numResults == 0) {
			return null;
		}
		if (numResults == 1) {
			return results.getFieldDataPacket(0);
		}
		throw new BulletinIndexException(
			"Found more than one field data set for the same bulletin id: " +
				bulletinId + "; found " + numResults + " results");
	}
	
	private static class LuceneResults implements Results
	{
		
		public int getCount() throws BulletinIndexException
		{
			return hits.length();
		}

		public FieldDataPacket getFieldDataPacket(int n)
			throws BulletinIndexException 
		{
			Document doc;
			try {
				doc = hits.doc(n);
			} catch (IOException ioe) {
				throw new BulletinIndexException(
					"Unable to retrieve FieldDataPacket " + n, ioe);
			}
			String[] fields = BulletinField.getSearchableXmlIds();
			FieldDataPacket fdp = 
				new FieldDataPacket(getFieldId(doc), fields);
			addFields(fdp, doc);
			addAttachments(fdp, doc);
			return fdp;
		}
		
		private static void addFields(FieldDataPacket fdp, Document doc) 
			throws BulletinIndexException
		{
			String[] fieldIds = fdp.getFieldTags();
			for (int i = 0; i < fieldIds.length; i++) {
				BulletinField field = BulletinField.getFieldByXmlId(fieldIds[i]);
				if (field == null) {
					throw new BulletinIndexException(
						"Unknown field " + fieldIds[i]);
				}
				String value = doc.get(field.getIndexId());
				if (value != null) {
					if (field.isDateField()) {
						value = DATE_FORMAT.format(DateField.stringToDate(value));
					}
					fdp.set(fieldIds[i], value);
				}
			}
		}
		
		private static void addAttachments(FieldDataPacket fdp, Document doc) 
			throws BulletinIndexException
		{
			String attachmentsString = doc.get(ATTACHMENT_LIST_INDEX_FIELD);
			if (attachmentsString != null) {
				String[] attachmentsAssocList = 
					attachmentsString.split(ATTACHMENT_LIST_SEPARATOR);
				if ((attachmentsAssocList.length % 2) != 0) {
					throw new BulletinIndexException(
						"Invalid attachments string found: " + 
						attachmentsString);
				}
				UniversalId bulletinId = getBulletinId(doc);
				for (int i = 0; i < attachmentsAssocList.length; i += 2) {
					fdp.addAttachment(new AttachmentProxy(
						UniversalId.createFromAccountAndLocalId(
							bulletinId.getAccountId(), 
							attachmentsAssocList[i]),
						attachmentsAssocList[i + 1],
						null));	
				}
			}
		}
		
		private static UniversalId getFieldId(Document doc) 
			throws BulletinIndexException
		{
			
			UniversalId bulletinId = getBulletinId(doc);
			String fieldLocalIdString = doc.get(FIELD_LOCAL_ID_INDEX_FIELD);
			if (fieldLocalIdString == null) {
				throw new BulletinIndexException(
					"Did not find field local id");
			}
			return UniversalId.createFromAccountAndLocalId(
				bulletinId.getAccountId(), fieldLocalIdString);
		}
		
		private static UniversalId getBulletinId(Document doc) 
			throws BulletinIndexException
		{
			String bulletinIdString = doc.get(
				BULLETIN_UNIVERSAL_ID_INDEX_FIELD);
			if (bulletinIdString == null) {
				throw new BulletinIndexException(
					"Did not find bulletin universal id");
			}
			
			try {
				return UniversalId.createFromString(bulletinIdString);
			} catch (NotUniversalIdException e) {
				throw new BulletinIndexException(
					"Invalid bulletin universal id found", e);
			}
		}
		
		private LuceneResults(Hits hits)
		{
			this.hits = hits;
		}
		
		private Hits hits;		
	}
	
	private IndexSearcher searcher;
}