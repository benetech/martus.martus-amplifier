package org.martus.amplifier.main;


import java.util.logging.Logger;

import org.martus.amplifier.service.datasynch.DataSynchManager;

/**
 * @author SKoneru
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MartusAmplifierDataSynch {

	/**
	 * Constructor for MartusAmplifierDataSynch.
	 */
	public MartusAmplifierDataSynch() {
		super();
	}

	public void execute() 
	{
		
		try
		{
			DataSynchManager dataManager = DataSynchManager.getInstance();
			dataManager.getAllNewBulletins();
			dataManager.indexBulletins();
		}
		catch(Exception e)
		{
			logger.severe("MartusAmplifierDataSynch.execute(): " + e.getMessage());
			e.printStackTrace();
		}
					
	}
	
	
	private static Logger logger = Logger.getLogger("MainTask");
	
}
