package org.martus.amplifier.service.datasynch;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.martus.amplifier.service.search.BulletinCatalog;
import org.martus.amplifier.service.search.BulletinIndexer;
import org.martus.common.UniversalId;

public class DataSynchManager implements IDataSynchConstants
{

	private AmplifierNetworkGateway amplifierGateway = null;
	private static Logger logger = Logger.getLogger(DATASYNC_LOGGER);
	private static DataSynchManager instance = new DataSynchManager();

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
		logger.info("in DataSynchManager.getAllNewBulletins(): ");	
	
		String accountId = "";
		List accountList = null;
		List uidList = null;
		
		//Step1: Retrieve all the accountids from Martus Server
		accountList = new ArrayList(amplifierGateway.getAllAccountIds());
		if(accountList == null)
		{
			logger.severe("DataSynchManager.getAllNewBulletins(): accountList is null");
			System.exit(0);
		}
		
		//Step2: Retrieve the universal ids and bulletins for each account
		for(int index=0; index <accountList.size();index++)
		{
			accountId = (String) accountList.get(index);
			uidList  = new ArrayList(amplifierGateway.getAccountUniversalIds(accountId));
			checkAndRetrieveBulletinsForUIDs(uidList, accountId);		
		}
				
	}
	
	
	private void  checkAndRetrieveBulletinsForUIDs(List uidList, String accountId)
	{
		logger.info("DataSynchManager:checkAndRetrieveBulletinsForUIDs()");
		int fromIndex = 0;
		int toIndex = 0;
		String localId = "";
		String uidStr = "";
		UniversalId uid = null;
		BulletinCatalog catalog = BulletinCatalog.getInstance();
		
		for(int index=0; index<uidList.size(); index++)
			{
				Object obj = (Object) uidList.get(index);
			    uidStr = obj.toString();
			    
			    fromIndex = uidStr.indexOf('[');
			    toIndex = uidStr.indexOf(',');
			    localId = uidStr.substring(fromIndex+1, toIndex);
			    logger.info("DataSynchManager.checkAndRetrieveBulletinsForUIDs()--localID = "+ localId);
				uid = UniversalId.createFromAccountAndLocalId(accountId, localId);	
				if( !catalog.bulletinHasBeenIndexed(uid) )
				{
			  		logger.info("DataSynchManager.checkAndRetrieveBulletinsForUIDs():before calling  amplifierGateway.retrieveAndManageBulletin on UID = "+ uid.toString());
			  		amplifierGateway.retrieveAndManageBulletin(uid);
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
		logger.info("in DataSynchManager.indexBulletins() ");	
		BulletinIndexer indexer = BulletinIndexer.getInstance();
		indexer.indexBulletins();
	}
	

}
