package org.martus.amplifier.test.datasynch;

import java.util.Vector;

import org.martus.amplifier.service.datasynch.AmplifierNetworkGateway;
import org.martus.amplifier.service.datasynch.BackupServerManager;

public class TestAmplifierNetworkGateway extends TestAbstractAmplifierDataSynch
{	
	public TestAmplifierNetworkGateway(String name)
	{
		super(name);
	}
	
	public void testGetAllAccountIds()
	{
		System.out.println("AmplifierNetworkGatewayTest:testGetAllAccountIds");
		AmplifierNetworkGateway amplifierGateway = new AmplifierNetworkGateway(new BackupServerManager().getBackupServersList());
		Vector list = amplifierGateway.getAllAccountIds();
		for(int i =0; i<list.size(); i++)
		{
			System.out.println("AccountId1 = "+(list.elementAt(i)).toString() );
		}
		assertTrue(list.size() >0);	
	}

	public void testGetAccountUniversalIds()
	{
		System.out.println("AmplifierNetworkGatewayTest:testGetAccountUniversalIds");
		AmplifierNetworkGateway amplifierGateway = new AmplifierNetworkGateway(new BackupServerManager().getBackupServersList());
		
		Vector list = amplifierGateway.getAccountUniversalIds(sampleAccountId);
		System.out.println("AccountId2 = "+ sampleAccountId +"  Number of Universal IDs = "+ list.size());
		for(int i =0; i < list.size(); i++)
		{
			String uid = (String) list.get(i);
			System.out.println("UniversalIDs = "+ uid );
		}
		//assertTrue(list.size() > 0);	
	}
	
	public void testgetBulletin()
	{
		//System.out.println("AmplifierNetworkGatewayTest: testGetBulletin");
		//AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();
		//UniversalId uid = UniversalId.createFromAccountAndLocalId(sampleAccountId, sampleLocalId);
		//File file = amplifierGateway.getBulletin(uid);
		//assertTrue(file.length() >0);		
	}



	public void testRetrieveAndManageBulletin()
	{
		//System.out.println("AmplifierNetworkGatewayTest:testRetrieveAndManageBulletin");
		//AmplifierNetworkGateway amplifierGateway = AmplifierNetworkGateway.getInstance();		
		//UniversalId uid = UniversalId.createFromAccountAndLocalId(sampleAccountId, sampleLocalId);
		//Vector list = amplifierGateway.retrieveAndManageBulletin(uid);				
		//assertTrue(list.size() > 0);			
	}
		
	final String sampleAccountId = "MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEAt+9X7kLTLx8fTfXIogRK5ySJnVL1s2Wi/L9MYMxHWkddpD5XBibQjOM/RkW2tn7oXM9SdQrU16EvEJtTnIZ+z5D6uXuq37vffHcfV9x5vQ3p5PEtKLinvvbqwbVgka+OXbMsjoV6seeAtXAxop9qme9yk4d1/Pco+RdLOX/Toyt9prSqlr2epu+hpZ6Qv8X9C4IF80eajPJd0x5cKsTZPpAmC5Iy5oh2uE0dy9iP6Esz3Ob1X3dn/QLaHJhQQp49um6UCbuN57wof/m4k703txDzxpZdKYUDaCQvKslpBpfiqjLTZ2FbaUodkkcckky9U9xzMDdrNxSvuG9LpjFr0QIBEQ==";
	final String sampleLocalId = "B-111ded2-f19d90f997--7ffd";

}
