/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.amplifier.main.test;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.main.LanguagesIndexedList;
import org.martus.common.MartusUtilities;
import org.martus.util.DirectoryUtils;
import org.martus.util.TestCaseEnhanced;


public class TestLanguagesIndexList extends TestCaseEnhanced
{
	public TestLanguagesIndexList(String name)
	{
		super(name);
	}
	
	public void testLanguagesIndexedBasics() throws Exception
	{
		File baseDir = createTempDirectory();
		File languageListFile = new File(baseDir, "langList.txt");
		languageListFile.deleteOnExit();
		LanguagesIndexedList list = new LanguagesIndexedList(languageListFile);
		assertFalse("file should not exist", languageListFile.exists());
		assertNull("language list should be null", list.getListOfLanguagesIndexed());
		
		try
		{
			list.loadLanguagesAlreadyIndexed();
			fail("Should have thrown since file didn't exist");
		}
		catch (IOException expected)
		{
		}
		
		assertTrue("file should now exist", languageListFile.exists());
		assertEquals("Only 1 Language should be in the list", 1, list.getListOfLanguagesIndexed().size());		
		assertEquals("Any Language should be in the list", SearchResultConstants.LANGUAGE_ANYLANGUAGE_LABEL, list.getListOfLanguagesIndexed().get(0));		
		
		Vector listFromFile = MartusUtilities.loadListFromFile(languageListFile);
		
		assertEquals("Saved copy should have 1 entry", 1, listFromFile.size());
		assertEquals("Any Language should be in the list", SearchResultConstants.LANGUAGE_ANYLANGUAGE_LABEL, listFromFile.get(0));		
		DirectoryUtils.deleteEntireDirectoryTree(baseDir);
	}

	public void testLanguagesIndexedAdd() throws Exception
	{
		File baseDir = createTempDirectory();
		File languageListFile = new File(baseDir, "langList.txt");
		languageListFile.deleteOnExit();
		LanguagesIndexedList list = new LanguagesIndexedList(languageListFile);
		try
		{
			list.loadLanguagesAlreadyIndexed();
			fail("Should have thrown since file didn't exist");
		}
		catch (IOException expected)
		{
		}

		Vector updatedList = list.getListOfLanguagesIndexed();
		assertFalse("List should not contain en", updatedList.contains("en"));		

		list.updateLanguagesIndexed("en");
		updatedList = list.getListOfLanguagesIndexed();
		assertEquals("List should now 2 entries", 2, updatedList.size());		
		assertTrue("List should now contain en", updatedList.contains("en"));		

		list.updateLanguagesIndexed("en");
		updatedList = list.getListOfLanguagesIndexed();
		assertEquals("List should still 2 entries", 2, updatedList.size());		

		list.updateLanguagesIndexed("fr");
		updatedList = list.getListOfLanguagesIndexed();
		assertEquals("List should have 3 entries", 3, updatedList.size());
		
		LanguagesIndexedList list2 = new LanguagesIndexedList(languageListFile);
		try
		{
			list2.loadLanguagesAlreadyIndexed();
		}
		catch (IOException unexpected)
		{
			fail("Should not have thrown since file should exist");
		}

		updatedList = list2.getListOfLanguagesIndexed();
		assertEquals("List2 should have 3 entries", 3, updatedList.size());
		assertTrue("Any Language should be in the list", updatedList.contains(SearchResultConstants.LANGUAGE_ANYLANGUAGE_LABEL));		
		assertTrue("en should be in the list", updatedList.contains("en"));		
		assertTrue("fr should be in the list", updatedList.contains("fr"));		

		languageListFile.delete();
		try
		{
			list2.updateLanguagesIndexed("gr");
		}
		catch (IOException unexpected)
		{
			fail("File deleted a new file should be created");
		}

		LanguagesIndexedList list3 = new LanguagesIndexedList(languageListFile);
		try
		{
			list3.loadLanguagesAlreadyIndexed();
		}
		catch (IOException unexpected)
		{
			fail("Should not have thrown since a new file should have been created");
		}

		updatedList = list3.getListOfLanguagesIndexed();
		assertEquals("This list should be 4 in size", 4, updatedList.size());
		
		LanguagesIndexedList addDirectlyToNewEmptyList = new LanguagesIndexedList(languageListFile);

		addDirectlyToNewEmptyList.updateLanguagesIndexed("it");
		updatedList = addDirectlyToNewEmptyList.getListOfLanguagesIndexed();
		assertEquals("should contain all & it", 2, updatedList.size());
		assertTrue("Any Language should be in the list", updatedList.contains(SearchResultConstants.LANGUAGE_ANYLANGUAGE_LABEL));		
		assertTrue("en should be in the list", updatedList.contains("it"));		

		LanguagesIndexedList nullList = new LanguagesIndexedList(null);
		try
		{
			nullList.updateLanguagesIndexed("it");
			fail("Null File no file could be created");
		}
		catch (Exception expected)
		{
		}

		DirectoryUtils.deleteEntireDirectoryTree(baseDir);
	}

	
}
