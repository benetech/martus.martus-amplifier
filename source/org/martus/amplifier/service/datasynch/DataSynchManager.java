package org.martus.amplifier.service.datasynch;

import java.util.List;
import java.util.logging.Logger;

import org.martus.common.UniversalId;
import org.martus.amplifier.service.datasynch.AmplifierNetworkGateway;


public class DataSynchManager implements IDataSynchConstants
{

	private List newUIDList = null;
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
		return newUIDList;
	}
	
	
	/*
	 * This methods retrieves new bulletins and attachments 
	 * and saves them into temporary folders
	 */
	public void getALLNewBulletinObjects()
	{ 
		int index=0;
		int size = newUIDList.size();
		UniversalId tempUID = null;
		for(index=0; index<size; index++)
		{
			tempUID = (UniversalId)newUIDList.get(index);		
			amplifierGateway.getBulletin(tempUID );
		}
		
	}
	
	/*
	 * This will take care of indexing bulletins and 
	 * calling AttachmentManager
	 */
	public boolean indexAndSaveNewBulletins()
	{
		return false;
	}
	
	public void updateAmplifierBulletins()
	{}	

}
