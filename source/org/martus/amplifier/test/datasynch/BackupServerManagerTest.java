package org.martus.amplifier.test.datasynch;

import java.util.List;

import org.martus.amplifier.service.datasynch.BackupServerInfo;
import org.martus.amplifier.service.datasynch.BackupServerManager;
import org.martus.common.TestCaseEnhanced;

public class BackupServerManagerTest extends TestCaseEnhanced
{
	public BackupServerManagerTest(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		List serverList = BackupServerManager.getInstance().getBackupServersList();
		testInfo = (BackupServerInfo) serverList.get(0);
	}
	
	public void testGetBackupServersList()
	{		
		String result = testInfo.getAddress();
		assertEquals(result, "127.0.0.1");
		
		result = testInfo.getName();
		assertEquals(result, "127.0.0.1");
		
		int intResult = testInfo.getPort();
		assertEquals(intResult, 985);		
	}
	
	BackupServerInfo testInfo;
}
