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

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.martus.amplifier.presentation.DownloadAttachment;
import org.martus.amplifier.presentation.SearchResults;
import org.martus.amplifier.search.AttachmentInfo;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.common.packet.UniversalId;
import org.martus.common.test.TestCaseEnhanced;


public class TestDownloadAttachment extends TestCaseEnhanced
{
	public TestDownloadAttachment(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		HttpServletResponse response = null;
		Context context = createSampleSearchResults(request, response);

		DownloadAttachment servlet = new DownloadAttachment();
		String templateName = servlet.selectTemplate(request, response, context);
		assertEquals("DownloadAttachment.vm", templateName);
	}
	
	public void testGetAttachment() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		HttpServletResponse response = null;
		Context context = createSampleSearchResults(request, response);
		request.parameters.put("bulletinIndex","1");
		request.parameters.put("attachmentIndex","1");
		DownloadAttachment servlet = new DownloadAttachment();
		servlet.selectTemplate(request, response, context);
		AttachmentInfo attachmentInfo = (AttachmentInfo)context.get("attachment");
		assertEquals("Attachment # 1's local ID didn't match", uid1.getLocalId(), attachmentInfo.getLocalId());
		assertEquals("Attachment # 1's label didn't match", label1, attachmentInfo.getLabel());
		assertEquals("Attachment # 1's account ID didn't match", uid1.getAccountId(), attachmentInfo.getAccountId());

		request.parameters.put("bulletinIndex","1");
		request.parameters.put("attachmentIndex","2");
		servlet.selectTemplate(request, response, context);
		attachmentInfo = (AttachmentInfo)context.get("attachment");
		assertEquals("Attachment # 2's local ID didn't match", uid1.getLocalId(), attachmentInfo.getLocalId());
		assertEquals("Attachment # 2's label didn't match", label2, attachmentInfo.getLabel());
		assertEquals("Attachment # 2's account ID didn't match", uid1.getAccountId(), attachmentInfo.getAccountId());

		request.parameters.put("bulletinIndex","2");
		request.parameters.put("attachmentIndex","1");
		servlet.selectTemplate(request, response, context);
		attachmentInfo = (AttachmentInfo)context.get("attachment");
		assertEquals("Attachment # 3's local ID didn't match", uid2.getLocalId(), attachmentInfo.getLocalId());
		assertEquals("Attachment # 3's label didn't match", label3, attachmentInfo.getLabel());
		assertEquals("Attachment # 3's account ID didn't match", uid2.getAccountId(), attachmentInfo.getAccountId());
	}

	private Context createSampleSearchResults(MockAmplifierRequest request, HttpServletResponse response) throws Exception
	{
		Context context = new MockContext();
		SearchResultsForTesting sr = new SearchResultsForTesting();
		request.putParameter("query", "test");
		request.parameters.put("bulletinIndex","1");
		request.parameters.put("attachmentIndex","1");
		sr.selectTemplate(request, response, context);
		return context;
	}

	final UniversalId uid1 = UniversalId.createDummyUniversalId();
	final String label1 = "attachment 1";
	final AttachmentInfo attachment1 = new AttachmentInfo(uid1.getAccountId(), uid1.getLocalId(), label1);
	final String label2 = "attachment 2";
	final AttachmentInfo attachment2 =  new AttachmentInfo(uid1.getAccountId(), uid1.getLocalId(), label2);

	final UniversalId uid2 = UniversalId.createDummyUniversalId();
	final String label3 = "attachment 3";
	final AttachmentInfo attachment3 =  new AttachmentInfo(uid2.getAccountId(), uid2.getLocalId(), label3);

	class SearchResultsForTesting extends SearchResults
	{
		public List getSearchResults(AmplifierServletRequest request)
			throws Exception, BulletinIndexException
		{
			if(request.getParameter("query")==null)
				throw new Exception("malformed query");
			Vector infos = new Vector();
			BulletinInfo bulletinInfo1 = new BulletinInfo(uid1);
			bulletinInfo1.addAttachment(attachment1);
			bulletinInfo1.addAttachment(attachment2);
			infos.add(bulletinInfo1);
			
			BulletinInfo bulletinInfo2 = new BulletinInfo(uid2);
			bulletinInfo2.addAttachment(attachment3);
			infos.add(bulletinInfo2);
			
			return infos;
		}
	}
	
	
}
