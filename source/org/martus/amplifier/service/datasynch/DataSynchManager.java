package org.martus.amplifier.service.datasynch;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.Vector;


import org.martus.common.UniversalId;
import org.martus.amplifier.service.datasynch.AmplifierNetworkGateway;
import org.martus.amplifier.service.search.BulletinIndexer;

public class DataSynchManager implements IDataSynchConstants
{

	private AmplifierNetworkGateway amplifierGateway = null;
	private static Logger logger = Logger.getLogger(DATASYNC_LOGGER);

	public DataSynchManager()
	{
		super();
		amplifierGateway = AmplifierNetworkGateway.getInstance();
	}
	
	/*
	 * This method retrieves existing Server UID list
	 * and compares with the Amplifier List 
	 * to give the new list of UniversalIDs.
	 * 
	 */
	public List getNewUniversalIds()
	{
		logger.info("in DataSynchManager.getNewUniversalIds(): ");	
		List newUIDList = new ArrayList();	
		List accountList = new ArrayList();
		List totalMartusUIDList = new ArrayList();//all universalIds from Martus
		List totalLocalUIDList = new ArrayList(); //existing UIDs on Amplifier
		List accountUIDs = new ArrayList();
		
		int i=0;
		String accountId = "";
		//compare that with the local UID list.
		//Step1: Retrieve all the accountids from Martus Server
		accountList = (List) amplifierGateway.getAllAccountIds();
		if(accountList == null)
		{
			logger.severe("DataSynchManager.getNewUniversalIds(): accountLIst is null");
			System.exit(0);
		}
		//Step2: Retrieve the universal ids for each account and create a list
		for(i=0; i<accountList.size();i++)
		{
			accountId = (String) accountList.get(i);
			accountUIDs = (List) amplifierGateway.getAccountUniversalIds(accountId) ;
			totalMartusUIDList.addAll(accountUIDs);
		}
		
		//Step3: Compare that with the LocalUIDList;
		//TODO: get the totalLocalUIDList, compare it with the totalMartusUIDList
		newUIDList = totalMartusUIDList;
				
		return newUIDList;
	}
	
	
	/*
	 * This methods retrieves new bulletins and attachments 
	 * Saving the attachments and bulletin files is done in retrieveAndManageBulletin()
	 */
	public void getALLNewBulletinObjects(List newUIDList)
	{ 
		logger.info("in DataSynchManager.getALLNewBulletinObjects()");	
		int index=0;
		int size = newUIDList.size();
		UniversalId tempUID = null;
		for(index=0; index<size; index++)
		{
			tempUID = (UniversalId)newUIDList.get(index);		
			amplifierGateway.retrieveAndManageBulletin(tempUID );
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
