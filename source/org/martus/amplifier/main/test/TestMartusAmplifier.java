package org.martus.amplifier.main.test;

import java.io.File;
import java.util.List;

import org.martus.amplifier.datasynch.BackupServerInfo;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.common.LoggerForTesting;
import org.martus.common.MartusUtilities;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.test.TestCaseEnhanced;

public class TestMartusAmplifier extends TestCaseEnhanced
{
	public TestMartusAmplifier(String name)
	{
		super(name);
	}
	
	public void testGetBackupServersList() throws Exception
	{
		MockMartusSecurity security = MockMartusSecurity.createServer();
		File dir = createTempFile();
		dir.delete();
		dir.mkdirs();
		
		MartusAmplifier amp = new MartusAmplifier(dir, new LoggerForTesting());
		
		List noServers = amp.loadServersWeWillCall(dir, security);
		assertEquals(0, noServers.size());
		
		String ip = "2.4.6.8";
		File keyFile = new File(dir, "ip=" + ip);
		MartusUtilities.exportServerPublicKey(security, keyFile);
		
		List oneServer = amp.loadServersWeWillCall(dir, security);
		assertEquals(1, oneServer.size());

		BackupServerInfo testInfo = (BackupServerInfo)oneServer.get(0);
		String result = testInfo.getAddress();
		assertEquals("ip", ip, result);
		
		result = testInfo.getName();
		assertEquals("name", ip, result);
		
		int intResult = testInfo.getPort();
		assertEquals(985, intResult);
		
		keyFile.delete();
		dir.delete();
		assertFalse(dir.getPath() + " still exists?", dir.exists());	
	}
	
	BackupServerInfo testInfo;
}
