package org.martus.amplifier.test.datasynch;

import java.util.List;

import org.martus.amplifier.service.datasynch.BackupServerInfo;
import org.martus.amplifier.service.datasynch.BackupServerManager;

public class BackupServerManagerTest extends AbstractAmplifierDataSynchTest
{

	public BackupServerManagerTest()
	{
		super();
	}
	
	public void testGetBackupServersList()
	{
		List serverList = BackupServerManager.getInstance().getBackupServersList();
		BackupServerInfo testInfo = (BackupServerInfo) serverList.get(0);
		assertEquals(testInfo.getAddress(), "127.0.0.1");
		assertEquals(testInfo.getName(), "backupserver.martus.org");
		assertEquals(testInfo.getPort(), 0);		
	}
}
