package org.martus.amplifier.test.datasynch;

import org.martus.amplifier.service.datasynch.DataSynchManager;

/**
 * @author skoneru
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DataSynchManagerTest extends AbstractAmplifierDataSynchTest
{
	public DataSynchManagerTest(String name)
	{
		super(name);
	}


	public void testGetAllNewBulletins()
	{
		System.out.println("DataSynchManagerTest:testGetAllNewBulletins");
		DataSynchManager dataManager = DataSynchManager.getInstance();
		//dataManager.getAllNewBulletins();
	    //String bulletinWorkingDirectory = AmplifierConfiguration.getInstance().getBulletinPath();
	    //File file = new File(bulletinWorkingDirectory);
		//System.out.println("DataSynchManagerTest:testGetAllNewBulletins ends");
		//assertTrue(file.list().length>0);
	}	
	
}
