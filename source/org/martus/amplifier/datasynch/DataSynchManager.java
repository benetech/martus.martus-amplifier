package org.martus.amplifier.datasynch;

import java.io.IOException;
import java.util.ArrayList;
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
	
	public void getAllNewData(DataManager attachmentManager, BulletinIndexer indexer)
	{
		List accountList = new ArrayList(amplifierGateway.getAllAccountIds());
		
		BulletinExtractor bulletinExtractor = 
			amplifierGateway.createBulletinExtractor(
				attachmentManager, indexer);
		
		for(int index=0; index <accountList.size();index++)
		{
			if(MartusAmplifier.doesShutdownFileExist())
				return;
			String accountId = (String) accountList.get(index);
			pullContactInfoForAccount(accountId);
			pullNewBulletinsForAccount(accountId, bulletinExtractor);
		}
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
					logger.info("DataSynchManager.checkAndRetrieveBulletinsForUIDs():before calling  amplifierGateway.retrieveAndManageBulletin on UID = "+ uid.toString());
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
