package org.martus.amplifier.test.datasynch;

import java.util.List;

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
		assertTrue(serverList.contains("backupserver.martus.org"));
		assertTrue(serverList.contains("backupserver2.martus.org"));
	}
}
