package org.martus.amplifier.test.datasynch;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.martus.amplifier.service.datasynch.AmplifierNetworkGateway;

public class AmplifierNetworkGatewayTest extends AbstractAmplifierDataSynchTest
{

	public AmplifierNetworkGatewayTest()
	{
		super();
	}
	
	public void testGetAllBulletinIds()
	{
		List testBulletinsList = AmplifierNetworkGateway.getAllBulletinIds();
		Iterator testBulletinIterator = testBulletinsList.iterator();
		String currentBulletinId = null;
		String[] correctData = {"11", "12", "21", "22"};
		System.out.println("in testGetAllBuletinIds");
		int counter = 0;
		while(testBulletinIterator.hasNext())
		{
			currentBulletinId = (String) testBulletinIterator.next();
			assertEquals(currentBulletinId, correctData[counter]);
			counter++;
		}			
	}	
	
	public void testGetBulletin()
	{
		System.out.println("in testGetBulletin");
		Vector list = AmplifierNetworkGateway.getBulletin("firebomb");				
		assertEquals(list.size(), 3);			

	}	

}
