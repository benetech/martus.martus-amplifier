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
			searcher = new LuceneBulletinSearcher(MartusAmplifier.getBasePath());
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
