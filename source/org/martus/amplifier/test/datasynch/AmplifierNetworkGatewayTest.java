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
	
	/*
	
	public void testGetAllAccountIds()
	{
		System.out.println("in testGetAllAccountIds");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();
		Vector list = amplifierGateway.getAllAccountIds();
		assertTrue(list.size() >0);	
	}
	
	
	
	public void testGetAccountUniversalIds()
	{
		System.out.println("in testGetAccountUniversalIds");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();
		Vector list = amplifierGateway.getAccountUniversalIds(sampleAccountId);
		assertTrue(list.size() >0);	
	}

	
	public void testgetBulletin()
	{
		System.out.println("in testGetBulletin");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();
		UniversalId uid = UniversalId.createFromAccountAndLocalId(sampleAccountId, sampleLocalId);
		File file = amplifierGateway.getBulletin(uid);
		assertTrue(file.length() >0);		
	}

*/
	public void testRetrieveAndManageBulletin()
	{
		System.out.println("in testRetrieveAndManageBulletin");
		AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();		
		UniversalId uid = UniversalId.createFromAccountAndLocalId(sampleAccountId, sampleLocalId);
		Vector list = amplifierGateway.retrieveAndManageBulletin(uid);				
		assertTrue(list.size() > 0);			
	}
	

	

	
	final String sampleAccountId = "MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEAl9NDWiuXjljLkZ4cscHcpcOoK0BaZ6KwaV8UG23n5gdY6A43aoK+y2jTxAl1Krh57Y1cfxKFbTI2cdQ/NQzNHSPat8xJxu8Cao9N1XJk3njBCqRVIvFKIbzUvkZ64eMDP668Zmrp0fLOj1UQedBWyyYwU+5ixUCLFfx3u/WWSE0XszDc+dbWouKCIQLmGaMtn8UuQCMg5JTv3CMNufVNe2UYRF+x68LvCz3lCmFAQ06akxE5ahgUV7MAvVyTBAHM8YQj5TvuwvwffZPgJuDAS9Rs2j4qUnYNtLJCGax+BUfaBE/N5Urj+s/8fE+pwDE2Xpd2alD76Wt54bQBAX6tFwIBEQ==";
	final String sampleLocalId = "B-1adb7b8-f166248fec--8000";

}
