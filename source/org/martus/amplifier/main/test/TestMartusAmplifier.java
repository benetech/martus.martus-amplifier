package org.martus.amplifier.main.test;

import java.io.File;
import java.util.List;

import org.martus.amplifier.datasynch.BackupServerInfo;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.amplifier.main.StubServer;
import org.martus.common.LoggerForTesting;
import org.martus.common.MartusUtilities;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.test.TestCaseEnhanced;
import org.martus.util.UnicodeWriter;

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
		
		StubServer server = new StubServer(dir, new LoggerForTesting());
		MartusAmplifier amp = server.amp;
		
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
	
	public void testLoadAccountsWeWillNotAmplify() throws Exception
	{
		File unamplified = createTempFile();
		StubServer server = new StubServer(unamplified, new LoggerForTesting());
		MartusAmplifier amp = server.amp;
		assertNull("List should be null", amp.getListOfAccountsWeWillNotAmplify());
		
		amp.loadAccountsWeWillNotAmplify(null);
		List noAccounts = amp.getListOfAccountsWeWillNotAmplify();
		assertEquals("should be 0",0, noAccounts.size());

		UnicodeWriter writer = new UnicodeWriter(unamplified);
		String account1 = "account 1";
		String account2 = "account 2";
		writer.writeln(account1);	
		writer.writeln(account2);	
		writer.close();
		amp.loadAccountsWeWillNotAmplify(unamplified);
		
		List twoAccounts = amp.getListOfAccountsWeWillNotAmplify(); 
		assertEquals("List should have 2 entries", 2, twoAccounts.size());
		assertEquals("No account 1", account1, twoAccounts.get(0));
		assertEquals("No account 2", account2, twoAccounts.get(1));
	}
	BackupServerInfo testInfo;
}
