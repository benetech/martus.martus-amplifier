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
	public void getAllNewBulletins(BulletinProcessor bulletinProcessor)
	{
		//logger.info("in DataSynchManager.getAllNewBulletins(): ");	
	
		String accountId = "";
		List accountList = null;
		Vector response;
		
		
		
		//Step1: Retrieve all the accountids from Martus Server
		
		accountList = new ArrayList(amplifierGateway.getAllAccountIds());
		
		BulletinCatalog catalog = BulletinCatalog.getInstance();
		
		// TODO pdalbora 17-Apr-2003 -- This code attempts to get all new
		// bulletins from the server and put them into a temporary Bulletin
		// Folder, whence they will be subsequently indexed. What happens
		// if two different accounts have bulletins with the same local id?
		// Currently it looks like the second bulletin will overwrite the
		// first.
		
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
						amplifierGateway.retrieveAndManageBulletin(uid, bulletinProcessor);
					}
				}
				catch (NotUniversalIdException e)
				{
					logger.severe("DataSynchManager.getAllNewBulletins(): " + e);
				}
				catch (Exception e)
				{
					logger.severe("Unable to process " + uid + ": " + e.getMessage());
				}
			}
		}
				
	}
}
