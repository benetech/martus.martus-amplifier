/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
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
package org.martus.amplifier.main;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.martus.amplifier.common.SearchResultConstants;
import org.martus.common.MartusUtilities;
import org.martus.util.UnicodeWriter;


public class LanguagesIndexedList
{
	static public void initialize(File languagesIndexedFile) throws IOException
	{
		LanguagesIndexedList.languagesIndexedSingleton = new LanguagesIndexedList(languagesIndexedFile);
		LanguagesIndexedList.languagesIndexedSingleton.loadLanguagesAlreadyIndexed();
	}
		

	public LanguagesIndexedList(File languagesIndexedFileToUse)
	{
		languagesIndexedFile = languagesIndexedFileToUse;
		languagesIndexed = null;
	}

	public void loadLanguagesAlreadyIndexed() throws IOException
	{
		try
		{
			languagesIndexed = MartusUtilities.loadListFromFile(languagesIndexedFile);
		}
		catch(IOException e)
		{
			createInitialList();
			throw e;
		}
	}
	
	private void createInitialList() throws IOException
	{
		languagesIndexed = new Vector();
		updateLanguagesIndexed(SearchResultConstants.LANGUAGE_ANYLANGUAGE_LABEL);
	}

	public void updateLanguagesIndexed(String language) throws IOException
	{
		if(languagesIndexed == null)
			createInitialList();
		if(!languagesIndexed.contains(language))
		{
			languagesIndexed.add(language);
			saveLanguagesAlreadyIndexed();
		}
	}

	public void saveLanguagesAlreadyIndexed() throws IOException
	{
		UnicodeWriter writer = new UnicodeWriter(languagesIndexedFile);
		for (int i = 0; i < languagesIndexed.size(); i++)
		{
			writer.writeln((String)languagesIndexed.get(i));	
		}
		writer.close();
	}

	public Vector getListOfLanguagesIndexed()
	{
		return languagesIndexed;
	}

	File languagesIndexedFile;
	private Vector languagesIndexed;

	public static LanguagesIndexedList languagesIndexedSingleton;
	
}
