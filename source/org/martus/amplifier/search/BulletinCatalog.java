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
package org.martus.amplifier.search;

import java.util.logging.Logger;

import org.martus.amplifier.lucene.LuceneBulletinSearcher;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.common.packet.UniversalId;

/**
 * A class that represents a catalog of all of the 
 * bulletins that have been indexed.  
 * 
 * @author Bskinner
 * @version first created Dec 2002
 *
 */
public class BulletinCatalog 
{
	protected BulletinCatalog()
	{}
	
	public static BulletinCatalog getInstance() 
	{
		return instance;
	}
   
	public boolean bulletinHasBeenIndexed(UniversalId universalId)
	{
		BulletinSearcher searcher = null;
		try
		{
			searcher = new LuceneBulletinSearcher(MartusAmplifier.getStaticAmplifierDataPath());
			return (searcher.lookup(universalId) != null);
		}
		catch (Exception e)
		{
			Logger.getLogger("catalog").severe("Catalog error: " + e.getMessage());
		}
		finally
		{
			if (searcher != null)
			{
				try
				{
					searcher.close();
				}
				catch (Exception e)
				{
					Logger.getLogger("catalog").severe("Catalog error: " + e.getMessage());
				}
			}
		}
		return false;
	}
	
	private static BulletinCatalog instance = new BulletinCatalog();

}
