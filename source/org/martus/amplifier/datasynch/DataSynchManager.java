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

package org.martus.amplifier.datasynch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.martus.amplifier.attachment.DataManager;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.amplifier.search.BulletinCatalog;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.common.LoggerInterface;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.packet.UniversalId;

public class DataSynchManager
{

	private AmplifierNetworkGateway amplifierGateway = null;
	private static Logger logger = Logger.getLogger("DATASYNC_LOGGER");
	boolean isIndexingNeeded;

	public DataSynchManager(BackupServerInfo backupServerToCall, LoggerInterface loggerToUse, MartusCrypto securityToUse)
	{
		super();
		amplifierGateway = new AmplifierNetworkGateway(backupServerToCall, loggerToUse, securityToUse);
	}
	
	public void getAllNewData(DataManager attachmentManager, BulletinIndexer indexer, List accountsNotAmplified)
	{
		List accountsListAll = new ArrayList(amplifierGateway.getAllAccountIds());
		
		List accountsToBeAmplified = removeAccountsFromList(accountsListAll, accountsNotAmplified);
		
		BulletinExtractor bulletinExtractor = 
			amplifierGateway.createBulletinExtractor(
				attachmentManager, indexer);
		
		for(int index=0; index <accountsToBeAmplified.size();index++)
		{
			if(MartusAmplifier.doesShutdownFileExist())
				return;
			String accountId = (String) accountsToBeAmplified.get(index);
			pullContactInfoForAccount(accountId);
			pullNewBulletinsForAccount(accountId, bulletinExtractor);
		}
	}
	
	static public List removeAccountsFromList(List allAccounts, List accountsToRemove)
	{
		if(accountsToRemove==null || accountsToRemove.isEmpty())
			return allAccounts;
		
		ArrayList remainingAccounts = new ArrayList();
		for (Iterator i = allAccounts.iterator(); i.hasNext();)
		{
			String account = (String) i.next();
			if(!accountsToRemove.contains(account))
				remainingAccounts.add(account);
		}
		return remainingAccounts;
	}
	
	private void pullContactInfoForAccount(String accountId)
	{
		Vector response = amplifierGateway.getContactInfo(accountId);
		if(response == null)
			return;
		try
		{
			MartusAmplifier.dataManager.writeContactInfoToFile(accountId, response);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void pullNewBulletinsForAccount(String accountId, BulletinExtractor bulletinExtractor)
	{
		BulletinCatalog catalog = BulletinCatalog.getInstance();
		Vector response = amplifierGateway.getAccountPublicBulletinLocalIds(accountId);
		for(int i = 0; i < response.size(); i++)
		{
			if(MartusAmplifier.doesShutdownFileExist())
				return;
			UniversalId uid = null;
			try
			{
				
				uid = UniversalId.createFromAccountAndLocalId(accountId, (String)response.get(i));

				if( !catalog.bulletinHasBeenIndexed(uid) )
				{
					//logger.info("DataSynchManager.checkAndRetrieveBulletinsForUIDs():before calling  amplifierGateway.retrieveAndManageBulletin on UID = "+ uid.toString());
					amplifierGateway.retrieveAndManageBulletin(uid, bulletinExtractor);
				}
			}
			catch (Exception e)
			{
				logger.severe("Unable to process " + uid + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
