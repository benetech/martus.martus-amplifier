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
		assertEquals(list.size(), 3);			
	}
	
	
	

	
	final String sampleAccountId = "MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEA0yZzR54EIRdasI8OegAR5K5X5KCzFbgXTF+jQ97xv41puyTS14H0DnukR52+kZaSW5lIJZFo4Tw7jyVZLoEeavAy76QH6InFx9hH1bxzU+AzGfjvTQTRWEstv8MOiDOclvj94hecwe60ikyWSASRblUqWGJNxFqhQkeWyBaQW7WYUe5WwQ43B5laCvZ4W719dQDAZEA1qCZloqBJ4RHptl1cgUZAzwN6ELQ0PiQqvdyRfAC1MeufSpq58bdTZc2qlqxbyApIIx42s+cmbRNI+kl0xm8VWdRDX2egQeMfSFtKMWif12+C0xLHAIPCiGwrYhKe5JEjBarVdh/1sWjS6QIBEQ==";
	final String sampleLocalId = "F-97617-edb26b21c8--7ffe";

}
