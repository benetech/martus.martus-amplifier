package org.martus.amplifier.test.datasynch;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.martus.common.UniversalId;

import org.martus.amplifier.service.datasynch.AmplifierNetworkGateway;

public class AmplifierNetworkGatewayTest extends AbstractAmplifierDataSynchTest
{

	public AmplifierNetworkGatewayTest()
	{
		super();
	}
	/*
	public void testGetAllBulletinIds()
	{
		System.out.println("in testGetAllBulletinIds");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();
		List testBulletinsList = amplifierGateway.getAllBulletinIds();
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
	*/
	public void testGetBulletin()
	{
		System.out.println("in testGetBulletin");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();		
		UniversalId uid = UniversalId.createFromAccountAndLocalId(sampleAccountId, sampleLocalId);
		Vector list = amplifierGateway.getBulletin(uid);				
		assertEquals(list.size(), 3);			
	}	
	
	public void testRetrieveBulletin()
	{
		System.out.println("in testRetrieveBulletin");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();
		UniversalId uid = UniversalId.createFromAccountAndLocalId(sampleAccountId, sampleLocalId);
		//File file = amplifierGateway.retrieveOneBulletin(uid);
		//assertTrue(file.length() >0);		
	}
	

	
	final String sampleAccountId = "an account id";
	final String sampleLocalId = "a local id";

}
