/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
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

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.martus.amplifier.attachment.FileSystemDataManager;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.lucene.LuceneBulletinIndexer;
import org.martus.amplifier.lucene.LuceneBulletinSearcher;
import org.martus.amplifier.main.LanguagesIndexedList;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.amplifier.search.BulletinField;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.amplifier.search.BulletinSearcher;
import org.martus.amplifier.search.SearchConstants;
import org.martus.amplifier.test.AbstractAmplifierTestCase;
import org.martus.common.FieldSpec;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;
import org.martus.util.DirectoryTreeRemover;
import org.martus.util.MartusFlexidate;

public abstract class CommonSearchTest 
	extends AbstractAmplifierTestCase implements SearchConstants, SearchResultConstants
{
	protected CommonSearchTest(String name) 
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		MartusAmplifier.security = new MockMartusSecurity();
		MartusAmplifier.security.createKeyPair();
		MartusAmplifier.dataManager = new FileSystemDataManager(getTestBasePath());
		MartusAmplifier.languagesIndexed = new LanguagesIndexedList(new File(getTestBasePath(),"langIndex"));
	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
		MartusAmplifier.dataManager.clearAllAttachments();
		DirectoryTreeRemover.deleteEntireDirectoryTree(new File(basePath));
	}
	
	protected FieldDataPacket generateSampleData(UniversalId bulletinId)
	{
		String author = "Paul";
		String keyword = "ate";
		String keywords = keyword + " egg salad root beer";
		String title = "ZZZ for Lunch?";
		String eventdate = "2003-04-10";
		String entrydate = "2003-05-11";
		String publicInfo = "menu";
		String language = "en";
		String organization = "test sample";
		String summary = 
			"Today Paul ate an egg salad sandwich and a root beer " +
			"for lunch.";
		String location = "San Francisco, CA";
		
		String attachment1LocalId = "att1Id";
		String attachment1Label = "Eggs.gif";
		String attachment2LocalId = "att2Id";
		String attachment2Label = "Recipe.txt";
		
		FieldDataPacket fdp = createFieldDataPacket(bulletinId, author, keywords, title, eventdate, entrydate, publicInfo, summary, location, attachment1LocalId, attachment1Label, attachment2LocalId, attachment2Label, language, organization);
		return fdp;
	}

	protected FieldDataPacket generateSampleFlexiData(UniversalId bulletinId)
	{
		String author = "Chuck";	
		String keywords = "2003-08-20";
		String title = "What's for Lunch?";
		long tenDaysOfMillis = 10*24*60*60*1000L;
		Date tenDaysAgo = new Date(System.currentTimeMillis() - tenDaysOfMillis);
		String entrydate= MartusFlexidate.toStoredDateFormat(tenDaysAgo);
		String eventdate = "2003-08-20,20030820+3";
		String publicInfo = "menu3";
		String language = "es";
		String organization = "test complex";
		String summary = 
			"Today Chuck ate an egg2 salad2 sandwich and a root beer2 " +
			"for lunch.";
		//String location = "San Francisco, CA";
		
		String attachment1LocalId = "att1Id";
		String attachment1Label = "Eggs.gif";
		String attachment2LocalId = "att2Id";
		String attachment2Label = "Recipe.txt";
		
		FieldDataPacket fdp = createFieldDataPacket(bulletinId, author, keywords, title, eventdate, entrydate, publicInfo, summary, null, attachment1LocalId, attachment1Label, attachment2LocalId, attachment2Label, language, organization);
		return fdp;
	}
	
	private FieldDataPacket createFieldDataPacket(UniversalId bulletinId, String author, String keywords, String title, String eventdate, String entrydate, String publicInfo, String summary, String location, String attachment1LocalId, String attachment1Label, String attachment2LocalId, String attachment2Label, String language, String organization)
	{
		FieldDataPacket fdp = generateFieldDataPacket(
			bulletinId, new String[] { 
				SEARCH_AUTHOR_INDEX_FIELD, author, 
				SEARCH_KEYWORDS_INDEX_FIELD, keywords, 
				SEARCH_TITLE_INDEX_FIELD, title,
				SEARCH_ENTRY_DATE_INDEX_FIELD, entrydate, 
				SEARCH_EVENT_DATE_INDEX_FIELD, eventdate,
				SEARCH_DETAILS_INDEX_FIELD, publicInfo, 
				SEARCH_SUMMARY_INDEX_FIELD, summary,
				SEARCH_LOCATION_INDEX_FIELD, location,
				SEARCH_LANGUAGE_INDEX_FIELD, language,
				SEARCH_ORGANIZATION_INDEX_FIELD, organization
			}, new String[] {
				attachment1LocalId, attachment1Label, 
				attachment2LocalId, attachment2Label
			});
		return fdp;
	}

	protected FieldDataPacket generateFieldDataPacket(UniversalId bulletinId)
	{
		return generateFieldDataPacket(bulletinId, new String[0]);
	}
	
	protected FieldDataPacket generateFieldDataPacket(
		UniversalId bulletinId, String[] fieldsAssocList)
	{
		return generateFieldDataPacket(
			bulletinId, fieldsAssocList, new String[0]);
	}
	
	
	protected FieldDataPacket generateFieldDataPacket(
		UniversalId bulletinId, String[] fieldsAssocList,
		String[] attachmentsAssocList)
	{
		FieldSpec[] fieldSpecs = BulletinField.getDefaultSearchFieldSpecs();
		UniversalId fieldUid = UniversalId.createFromAccountAndLocalId(
			bulletinId.getAccountId(), "TestField");
		
		FieldDataPacket fdp = new FieldDataPacket(fieldUid, fieldSpecs);
		Assert.assertEquals(
			"Uneven assoc list: " + Arrays.asList(fieldsAssocList), 
			0, fieldsAssocList.length % 2);
		for (int i = 0; i < fieldsAssocList.length; i += 2) {
			fdp.set(fieldsAssocList[i], fieldsAssocList[i + 1]);
		}
		Assert.assertEquals(
			"Uneven assoc list: " + Arrays.asList(attachmentsAssocList), 
			0, attachmentsAssocList.length % 2);
		for (int i = 0; i < attachmentsAssocList.length; i += 2) {
			fdp.addAttachment(new AttachmentProxy(
				UniversalId.createFromAccountAndLocalId(
					bulletinId.getAccountId(), attachmentsAssocList[i]),
				attachmentsAssocList[i + 1],
				null));
		}
					
		return fdp;
	}
	
	protected void deleteIndexDir() throws BulletinIndexException
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
	

	protected BulletinIndexer openBulletinIndexer()
		throws BulletinIndexException 
	{
		return new LuceneBulletinIndexer(getTestBasePath());
	}

	protected BulletinSearcher openBulletinSearcher() throws Exception 
	{
		return new LuceneBulletinSearcher(getTestBasePath());
	}
}