package org.martus.amplifier.test.datasynch;

import java.util.List;

import org.martus.amplifier.service.datasynch.BackupServerInfo;
import org.martus.amplifier.service.datasynch.BackupServerManager;
import org.martus.common.test.TestCaseEnhanced;

public class TestBackupServerManager extends TestCaseEnhanced
{
	public TestBackupServerManager(String name)
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
		assertEquals("127.0.0.1", result);
		
		result = testInfo.getName();
		assertEquals("127.0.0.1", result);
		
		int intResult = testInfo.getPort();
		assertEquals(985, intResult);		
	}
	
	BackupServerInfo testInfo;
}
