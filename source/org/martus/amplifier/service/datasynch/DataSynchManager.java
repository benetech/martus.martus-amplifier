package org.martus.amplifier.service.datasynch;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.Vector;


import org.martus.common.UniversalId;
import org.martus.amplifier.service.datasynch.AmplifierNetworkGateway;
import org.martus.amplifier.service.search.BulletinIndexer;
import org.martus.amplifier.service.search.BulletinCatalog;

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
	
	/*
	 * This method retrieves existing Server UID list
	 * 
	 */
	public List getAllServerUniversalIds()
	{
		logger.info("in DataSynchManager.getNewUniversalIds(): ");	
		List accountList = new ArrayList();
		List accountUIDs = new ArrayList(); //all UniversalIds in each account
		List totalMartusUIDList = new ArrayList();//all universalIds from Martus
		
		int i=0;
		String accountId = "";
		//compare that with the local UID list.
		//Step1: Retrieve all the accountids from Martus Server
		accountList = (List) amplifierGateway.getAllAccountIds();
		if(accountList == null)
		{
			logger.severe("DataSynchManager.getNewUniversalIds(): accountList is null");
			System.exit(0);
		}
		//Step2: Retrieve the universal ids for each account and create a list
		for(i=0; i<accountList.size();i++)
		{
			accountId = (String) accountList.get(i);
			accountUIDs = (List) amplifierGateway.getAccountUniversalIds(accountId) ;
			totalMartusUIDList.addAll(accountUIDs);
		}
		
				
		return totalMartusUIDList;
	}
	
	
	/*
	 * This methods retrieves new bulletins and attachments 
	 * Saving the attachments and bulletin files is done in retrieveAndManageBulletin()
	 */
	public void getALLNewBulletinObjects(List uidList)
	{ 
		logger.info("in DataSynchManager.getALLNewBulletinObjects()");	
		int index=0;
		int size = uidList.size();
		UniversalId tempUID = null;
		
		BulletinCatalog catalog = BulletinCatalog.getInstance();	
		for(index=0; index<size; index++)
		{
			tempUID = (UniversalId)uidList.get(index);	
			if( !catalog.bulletinHasBeenIndexed(tempUID) )
			{
			  logger.info("before calling  amplifierGateway.retrieveAndManageBulletin on UID = "+ tempUID.toString());
			  amplifierGateway.retrieveAndManageBulletin(tempUID );
			}
		}
	}
	
	
	/*
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
