package org.martus.amplifier.test.datasynch;

import java.util.Iterator;
import java.util.List;

import org.martus.amplifier.service.datasynch.AmplifierNetworkGateway;

public class AmplifierNetworkGatewayTest extends AbstractAmplifierDataSynchTest
{

	public AmplifierNetworkGatewayTest()
	{
		super();
	}
	
	public void testGetAllBulletinIds()
	{
		AmplifierNetworkGateway gateway = new AmplifierNetworkGateway();
		List testBulletinsList = gateway.getAllBulletinIds();
		Iterator testBulletinIterator = testBulletinsList.iterator();
		String currentBulletinId = null;
		String[] correctData = {"11", "12", "21", "22"};
		int counter = 0;
		while(testBulletinIterator.hasNext())
		{
			currentBulletinId = (String) testBulletinIterator.next();
			assertEquals(currentBulletinId, correctData[counter]);
			counter++;
		}			
	}	

}
