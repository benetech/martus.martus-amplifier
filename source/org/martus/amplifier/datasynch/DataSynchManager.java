package org.martus.amplifier.datasynch;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.martus.amplifier.attachment.AttachmentManager;
import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.amplifier.search.BulletinCatalog;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.common.packet.UniversalId;
import org.martus.common.packet.UniversalId.NotUniversalIdException;

public class DataSynchManager
{

	private AmplifierNetworkGateway amplifierGateway = null;
	private static Logger logger = Logger.getLogger(AmplifierConfiguration.DATASYNC_LOGGER);
	boolean isIndexingNeeded;

	public DataSynchManager(List backupServersList)
	{
		super();
		amplifierGateway = new AmplifierNetworkGateway(backupServersList);
	}
	
	
	/**
	 * This methods retrieves account and UniversalIds.
	 * This calls the method checkAndRetrieveBulletinsForUIDs() for new bulletins and attachments 
	 * Saving the attachments and bulletin files is done in AmplifierNetworkGateway.retrieveAndManageBulletin()
	 */
	public void getAllNewBulletins(
		AttachmentManager attachmentManager, BulletinIndexer indexer)
	{
		//logger.info("in DataSynchManager.getAllNewBulletins(): ");	
	
		String accountId = "";
		List accountList = null;
		Vector response;
		
		
		
		//Step1: Retrieve all the accountids from Martus Server
		
		accountList = new ArrayList(amplifierGateway.getAllAccountIds());
		
		BulletinCatalog catalog = BulletinCatalog.getInstance();
		
		BulletinExtractor bulletinExtractor = 
			amplifierGateway.createBulletinExtractor(
				attachmentManager, indexer);
		
		//Step2: Retrieve the universal ids and bulletins for each account
		for(int index=0; index <accountList.size();index++)
		{
			accountId = (String) accountList.get(index);
			response = amplifierGateway.getAccountUniversalIds(accountId);
			for(int i = 0; i < response.size(); i++)
			{
				UniversalId uid = null;
				try
				{
					uid = UniversalId.createFromString((String) response.get(i));
	
					if( !catalog.bulletinHasBeenIndexed(uid) )
					{
						logger.info("DataSynchManager.checkAndRetrieveBulletinsForUIDs():before calling  amplifierGateway.retrieveAndManageBulletin on UID = "+ uid.toString());
						amplifierGateway.retrieveAndManageBulletin(uid, bulletinExtractor);
					}
				}
				catch (NotUniversalIdException e)
				{
					logger.severe("DataSynchManager.getAllNewBulletins(): " + e);
				}
				catch (Exception e)
				{
					logger.severe("Unable to process " + uid + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
				
	}
}