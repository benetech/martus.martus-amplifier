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
package org.martus.amplifier.presentation.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import org.martus.amplifier.attachment.AttachmentStorageException;
import org.martus.amplifier.attachment.FileSystemAttachmentManager;
import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.amplifier.presentation.DownloadAttachment;
import org.martus.amplifier.search.AttachmentInfo;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.common.packet.UniversalId;
import org.martus.common.test.TestCaseEnhanced;
import org.martus.util.DirectoryTreeRemover;
import org.martus.util.StringInputStream;


public class TestDownloadAttachment extends TestCaseEnhanced
{
	public TestDownloadAttachment(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		String basePath = AmplifierConfiguration.getInstance().getBasePath() + "/testing";
		MartusAmplifier.attachmentManager = new FileSystemAttachmentManager(basePath);
	}
	
	public void tearDown() throws Exception
	{
		MartusAmplifier.attachmentManager.clearAllAttachments();
		String basePath = AmplifierConfiguration.getInstance().getBasePath() + "/testing";
		DirectoryTreeRemover.deleteEntireDirectoryTree(new File(basePath));
	}
	
	public void testGetAttachment() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = new MockAmplifierResponse();
		createSampleSearchResults(request, response);
		request.parameters.put("bulletinIndex","1");
		request.parameters.put("attachmentIndex","1");
		DownloadAttachment servlet = new DownloadAttachment(basePath);
		servlet.internalDoGet(request, response);
		
		String attachment1 = response.getDataString();
		assertEquals("Attachment 1's data not the same?", data1, attachment1);
		 
		request.parameters.put("bulletinIndex","1");
		request.parameters.put("attachmentIndex","2");
		MockAmplifierResponse response2 = new MockAmplifierResponse();
		servlet.internalDoGet(request, response2);
		String attachment2 = response2.getDataString();
		assertEquals("Attachment 2's data not the same?", data2, attachment2);

		request.parameters.put("bulletinIndex","2");
		request.parameters.put("attachmentIndex","1");
		MockAmplifierResponse response3 = new MockAmplifierResponse();
		servlet.internalDoGet(request, response3);
		String attachment3 = response3.getDataString();
		assertEquals("Attachment 3's data not the same?", data3, attachment3);
	}

	private void createSampleSearchResults(MockAmplifierRequest request, MockAmplifierResponse response) throws Exception
	{
		List infos = getFoundBulletins();
		request.getSession().setAttribute("foundBulletins", infos);
		request.putParameter("query", "test");
		request.parameters.put("bulletinIndex","1");
		request.parameters.put("attachmentIndex","1");
	}

	final UniversalId uid1 = UniversalId.createDummyUniversalId();
	final String label1 = "attachment 1";
	final String data1 = "this is attachment 1";
	final AttachmentInfo attachment1 = new AttachmentInfo(uid1.getAccountId(), uid1.getLocalId(), label1);

	final UniversalId uid2 = UniversalId.createDummyUniversalId();
	final String label2 = "attachment 2";
	final String data2 = "this is attachment 2";
	final AttachmentInfo attachment2 =  new AttachmentInfo(uid1.getAccountId(), uid2.getLocalId(), label2);

	final UniversalId uid3 = UniversalId.createDummyUniversalId();
	final String label3 = "attachment 3";
	final String data3 = "this is attachment 3";
	final AttachmentInfo attachment3 =  new AttachmentInfo(uid2.getAccountId(), uid3.getLocalId(), label3);

	public List getFoundBulletins()
		throws Exception, BulletinIndexException
	{
		Vector infos = new Vector();
		BulletinInfo bulletinInfo1 = new BulletinInfo(uid1);
		bulletinInfo1.addAttachment(attachment1);
		bulletinInfo1.addAttachment(attachment2);
		infos.add(bulletinInfo1);
		writeAttachment(bulletinInfo1, 0, data1);
		writeAttachment(bulletinInfo1, 1, data2);
		
		BulletinInfo bulletinInfo2 = new BulletinInfo(uid2);
		bulletinInfo2.addAttachment(attachment3);
		infos.add(bulletinInfo2);
		writeAttachment(bulletinInfo2, 0, data3);
		
		return infos;
	}

	private void writeAttachment(BulletinInfo bulletinInfo1,int index, String data) throws AttachmentStorageException, UnsupportedEncodingException
	{
		AttachmentInfo attachInfo = (AttachmentInfo)bulletinInfo1.getAttachments().get(index);
		UniversalId uid1 = UniversalId.createFromAccountAndLocalId(attachInfo.getAccountId(), attachInfo.getLocalId());
		MartusAmplifier.attachmentManager.putAttachment(uid1, new StringInputStream(data));
	}
	
	final String basePath = AmplifierConfiguration.getInstance().getBasePath() + "/testing";
}
