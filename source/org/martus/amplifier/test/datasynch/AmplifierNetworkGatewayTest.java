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
	
	public AmplifierNetworkGatewayTest(String name)
	{
		super(name);
	}
	
	
	public void testGetAllAccountIds()
	{
		System.out.println("in testGetAllAccountIds");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();
		Vector list = amplifierGateway.getAllAccountIds();
		assertTrue(list.size() >0);	
	}
	
	/*
	public void testGetAccountUniversalIds()
	{
		System.out.println("in testGetAccountUniversalIds");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();
		Vector list = amplifierGateway.getAccountUniversalIds(sampleAccountId);
		assertTrue(list.size() >0);	
	}
	*/
	
	/*
	public void testgetBulletin()
	{
		System.out.println("in testGetBulletin");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();
		UniversalId uid = UniversalId.createFromAccountAndLocalId(sampleAccountId, sampleLocalId);
		File file = amplifierGateway.getBulletin(uid);
		assertTrue(file.length() >0);		
	}
	
	public void testRetrieveAndManageBulletin()
	{
		System.out.println("in testRetrieveAndManageBulletin");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();		
		UniversalId uid = UniversalId.createFromAccountAndLocalId(sampleAccountId, sampleLocalId);
		Vector list = amplifierGateway.retrieveAndManageBulletin(uid);				
		assertEquals(list.size(), 3);			
	}
	
	*/
	

	
	final String sampleAccountId = "an account id";
	final String sampleLocalId = "a local id";

}
