package org.martus.amplifier.service.datasynch;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.martus.amplifier.service.search.BulletinCatalog;
import org.martus.amplifier.service.search.BulletinIndexer;
import org.martus.common.UniversalId;
import org.martus.common.UniversalId.NotUniversalIdException;

public class DataSynchManager implements IDataSynchConstants
{

	private AmplifierNetworkGateway amplifierGateway = null;
	private static Logger logger = Logger.getLogger(DATASYNC_LOGGER);
	private static DataSynchManager instance = new DataSynchManager();
	boolean isIndexingNeeded;

	private DataSynchManager()
	{
		super();
		amplifierGateway = AmplifierNetworkGateway.getInstance();
	}
	
	public static DataSynchManager getInstance()
	{
		if(instance == null)
		instance = new DataSynchManager();
		return instance;
	}
	
	
	
	/**
	 * This methods retrieves account and UniversalIds.
	 * This calls the method checkAndRetrieveBulletinsForUIDs() for new bulletins and attachments 
	 * Saving the attachments and bulletin files is done in AmplifierNetworkGateway.retrieveAndManageBulletin()
	 */
	public void getAllNewBulletins()
	{
		//logger.info("in DataSynchManager.getAllNewBulletins(): ");	
	
		String accountId = "";
		List accountList = null;
		Vector response;
		isIndexingNeeded = false;
		
		//Step1: Retrieve all the accountids from Martus Server
		
		accountList = new ArrayList(amplifierGateway.getAllAccountIds());
		if(accountList == null)
		{
			logger.severe("DataSynchManager.getAllNewBulletins(): accountList is null");
			System.exit(0);
		}
		
		BulletinCatalog catalog = BulletinCatalog.getInstance();
		
		//Step2: Retrieve the universal ids and bulletins for each account
		for(int index=0; index <accountList.size();index++)
		{
			accountId = (String) accountList.get(index);
			response = amplifierGateway.getAccountUniversalIds(accountId);
			for(int i = 0; i < response.size(); i++)
			{
				try
				{
					UniversalId uid = UniversalId.createFromString((String) response.get(i));
	
					if( !catalog.bulletinHasBeenIndexed(uid) )
					{
						logger.info("DataSynchManager.checkAndRetrieveBulletinsForUIDs():before calling  amplifierGateway.retrieveAndManageBulletin on UID = "+ uid.toString());
						amplifierGateway.retrieveAndManageBulletin(uid);
						isIndexingNeeded = true;
					}
				}
				catch (NotUniversalIdException e)
				{
					logger.severe("DataSynchManager.getAllNewBulletins(): " + e);
				}
			}
		}
				
	}
	
   /**
	 * This will take care of indexing bulletins and 
	 * calling AttachmentManager
	 * we initiate the indexing on the field data packet folder
	 * for each packet that has an associated attachment
	 * it will know that from the xml file and find it and index it
	 * -Initiating the indexing is done through BulletinIndexer
	 */
	public void indexBulletins()
	{
		//logger.info("in DataSynchManager.indexBulletins() ");	
		if(isIndexingNeeded)
		{
			BulletinIndexer indexer = BulletinIndexer.getInstance();
			indexer.indexBulletins();
			isIndexingNeeded = false;
		}
		else
		{
			logger.info("No new bulletins retrieved. No indexing necessary");
		}
	}
}
