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
package org.martus.amplifier.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.martus.amplifier.main.LanguagesIndexedList;
import org.martus.amplifier.search.BulletinField;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.amplifier.search.SearchConstants;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;
import org.martus.common.utilities.MartusFlexidate;

public class LuceneBulletinIndexer 
	implements BulletinIndexer, LuceneSearchConstants
{
	
	public LuceneBulletinIndexer(String baseDirName) 
		throws BulletinIndexException
	{
		indexDir = getIndexDir(baseDirName);
		try {
			createIndexIfNecessary(indexDir);
			writer = new IndexWriter(indexDir, getAnalyzer(), false);
		} catch (IOException e) {
			throw new BulletinIndexException(
				"Could not create LuceneBulletinIndexer", e);
		}		
	}
	
	public void close() throws BulletinIndexException
	{
		try {
			// TODO pdalbora 23-Apr-2003 -- Don't call this unnecessarily.
			writer.optimize();
		} catch (IOException e) {
			throw new BulletinIndexException("Unable to close the index", e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				throw new BulletinIndexException(
					"Unable to close the index", e);
			}		
		}		
	}
	
	public void clearIndex() throws BulletinIndexException
	{
		try {
			writer.close();
			writer = new IndexWriter(indexDir, ANALYZER, true);
		} catch (IOException e) {
			throw new BulletinIndexException("Unable to clear the index", e);
		}		
	}
	
	public void indexFieldData(UniversalId bulletinId, FieldDataPacket fdp) 
		throws BulletinIndexException
	{
		Document doc = new Document();
		addBulletinId(doc, bulletinId);
		try
		{
			addFields(doc, fdp);
		}
		catch (IOException e1)
		{
			throw new BulletinIndexException(
				"Unable to index field data for " + bulletinId, e1);
		}
		addAttachmentIds(doc, fdp.getAccountId(), fdp.getAttachments());		
		
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			throw new BulletinIndexException(
				"Unable to index field data for " + bulletinId, e);
		}
	}
	
	/* package */ 
	static void createIndexIfNecessary(File indexDir)
		throws IOException
	{
		if (!IndexReader.indexExists(indexDir)) {
			IndexWriter writer = 
				new IndexWriter(indexDir, getAnalyzer(), true);
			writer.close();
		}
	}
	
	/* package */
	public static Analyzer getAnalyzer()
	{
		return ANALYZER;
	}
	
	/* package */
	public static File getIndexDir(String basePath)
		throws BulletinIndexException
	{
		File f = new File(basePath, INDEX_DIR_NAME);
		if (!f.exists() && !f.mkdirs()) {
			throw new BulletinIndexException(
				"Unable to create path: " + f);
		}
		return f;
	}
	
	private static void addBulletinId(Document doc, UniversalId bulletinId)
	{
		doc.add(Field.Keyword(
			BULLETIN_UNIVERSAL_ID_INDEX_FIELD, bulletinId.toString()));	
	}
	
	private static void addFields(Document doc, FieldDataPacket fdp) 
		throws BulletinIndexException, IOException 
	{
		Collection fields = BulletinField.getSearchableFields();
		for (Iterator iter = fields.iterator(); iter.hasNext();) 
		{
			BulletinField field = (BulletinField) iter.next();
			String value = fdp.get(field.getXmlId());
			if(field.isLanguageField())
				LanguagesIndexedList.languagesIndexedSingleton.updateLanguagesIndexed(value);
			if ((value != null) && (value.length() > 0)) 
			{
				addField(doc, field, value);
			}
		}
	}
	
	private static void addField(Document doc, BulletinField field, String value)
		throws BulletinIndexException
	{
		if (field.isDateField()) 
		{
			doc.add(Field.Keyword(field.getIndexId(), value));
		}
		else if (field.isDateRangeField())
		{
			convertDateRangeToSearchableString(doc, field, value);
		}
		else 
		{
			doc.add(Field.Text(field.getIndexId(), value));
		}
	}
	
	private static void addAttachmentIds(
		Document doc, String accountId, AttachmentProxy[] proxies)
	{
		if (proxies.length > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < proxies.length; i++) {
				AttachmentProxy proxy = proxies[i];
				buf.append(proxy.getUniversalId().getLocalId());
				buf.append(ATTACHMENT_LIST_SEPARATOR);
				buf.append(proxy.getLabel());
				buf.append(ATTACHMENT_LIST_SEPARATOR);
			}
			doc.add(Field.UnIndexed(
				ATTACHMENT_LIST_INDEX_FIELD, buf.toString()));
		}
	}
	
	private static void convertDateRangeToSearchableString(Document doc, BulletinField field, String value) throws BulletinIndexException
	{
		MartusFlexidate mfd = MartusFlexidate.createFromMartusDateString(value);
	
		String beginDate = MartusFlexidate.toStoredDateFormat(mfd.getBeginDate());
		String endDate = MartusFlexidate.toStoredDateFormat(mfd.getEndDate());							
	
		doc.add(Field.Keyword(SearchConstants.SEARCH_EVENT_START_DATE_INDEX_FIELD, beginDate)); 			
		doc.add(Field.Keyword(SearchConstants.SEARCH_EVENT_END_DATE_INDEX_FIELD, endDate));
					
		doc.add(Field.Text(field.getIndexId(), value));				
	}
	
	private File indexDir;
	private IndexWriter writer;
	private final static Analyzer ANALYZER = new StandardAnalyzer(new String[]{""});
	
	private static final String INDEX_DIR_NAME = "ampIndex";
}