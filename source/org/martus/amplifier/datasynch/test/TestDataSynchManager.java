package org.martus.amplifier.datasynch.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.martus.amplifier.datasynch.DataSynchManager;

public class TestDataSynchManager extends TestAbstractAmplifierDataSynch
{
	public TestDataSynchManager(String name)
	{
		super(name);
	}

	public void testRemoveAccountsFromList() throws Exception
	{
		ArrayList allAccounts = new ArrayList();
		String account1 = "account1"; 
		String account2 = "account2"; 
		String account3 = "account3";
		String account4 = "account4";
		
		allAccounts.add(account1);
		allAccounts.add(account2);
		allAccounts.add(account3);
		allAccounts.add(account4);
		
		Vector removeAccounts = new Vector();
		removeAccounts.add(account4);
		removeAccounts.add(account2);

		List noAccountsToRemove = DataSynchManager.removeAccountsFromList(allAccounts, null);
		assertEquals("should contain all 4 accounts", 4, noAccountsToRemove.size());
		

		
		List remainingAccounts = DataSynchManager.removeAccountsFromList(allAccounts, removeAccounts);
		assertEquals("New size should be 2", 2, remainingAccounts.size());
		assertTrue("Should contain account 1", remainingAccounts.contains(account1));
		assertTrue("Should contain account 3", remainingAccounts.contains(account3));
	}
	
}
