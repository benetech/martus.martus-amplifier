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

package org.martus.amplifier.common.test;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.martus.amplifier.common.CharacterUtil;
import org.martus.util.UnicodeWriter;

public class TestCharacterUtil extends TestCase
{
	public TestCharacterUtil(String name)
	{
		super(name);
	}
	
	public void testRemoveSpecialCharacters()
	{
		String test1 = "[test]";
		String test2 = "(test)";
		String test3 = "test:test";
		String test4 = "\"test\" 'test'";
		String test5 = "http:\\test@testagain.com";
		String test6 = "<html> test* test?";
		String test7 = "*";		
		
		String outStr = CharacterUtil.removeRestrictCharacters(test1);
		assertEquals("removed []", "test", outStr);
		outStr = CharacterUtil.removeRestrictCharacters(test2);
		assertEquals("removed ()", "test", outStr);
		outStr = CharacterUtil.removeRestrictCharacters(test3);
		assertEquals("removed :", "test test", outStr);
		outStr = CharacterUtil.removeRestrictCharacters(test4);
		assertEquals("removed \"", "\"test\" 'test'", outStr);
		outStr = CharacterUtil.removeRestrictCharacters(test5);
		assertEquals("removed :\"@.", "http  test testagain com", outStr);
		outStr = CharacterUtil.removeRestrictCharacters(test6);
		assertEquals("removed <>)", "html  test  test", outStr);
		outStr = CharacterUtil.removeRestrictCharacters(test7);		
		assertEquals("removed *)", "", outStr);
				
	}		
	
	public void testWritingUnicodeCharacters() throws Exception
	{
		String text2 = "í";
		int len2 = 0;
		try
		{
			len2 = text2.getBytes("UTF8").length;
		}
		catch(Exception e)
		{
			assertTrue("UTF8 not supported", false);
		}
		assertTrue("Unicode length should be different", len2 != text2.length());

		File file = createTempFileFromName("$$$MartusAmplifiertestamplifierunicodechars");
		UnicodeWriter writer = new UnicodeWriter(file);
		writer.write(text2);
		writer.close();
		assertEquals(len2, file.length());
		file.delete();
	}
	
	public File createTempFileFromName(String name) throws IOException
	{
		File file = File.createTempFile(name, null);
		file.deleteOnExit();
		return file;
	}
}
