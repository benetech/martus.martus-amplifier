package org.martus.amplifier.test.search;

import junit.framework.Assert;
import org.martus.amplifier.service.search.BulletinCatalog;
import org.martus.amplifier.common.bulletin.UniversalBulletinId;


/**
 * @author Bskinner
 *
 */
public class BulletinCatalogTest extends AbstractAmplifierSearchTest {
	public void testBulletinCatalog()
	{
		String oxfamAccountId = "MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEAgiR0CtYBOPjm4c8VICsYCPOWZWIukA+VWX/lg2F7cGmByUeR5Ujn/05gL/HA0aFhThgU4tqBdPmik7g9S9lrEoTyn5Bavc+J0fvLtQRZMEgCXlbGB4CxhaoQGabYix8dnk+R8xJb9DFEcrH/SeJC5YJVc1noCQcZ4ceY1gBMRW0pFc4EFTkb+4phe1KUM8AMzyh0uIvS2zurgsFiOxvG8xiCzp381w7R5vw4ledO93b7e0AAE+zRxBcmiTa5ffnWY8NxJ5nawfKXUIBIjquqt4OMd9h5upgFy856uYNh8PL35BVNF3tMhVZFhILAIsuu2AoCeQpKLzpFw4e7iiqTbQIBEQ==";
		String oxfamPacketId = "F-3c9c31-ee415a55de--1111";
		String nonExistentPacketId = "F-3c9c31-ee415a55de--qqqq";
		
		UniversalBulletinId oxfamBulletinId = new UniversalBulletinId(oxfamAccountId, oxfamPacketId);
		UniversalBulletinId nonExistentBulletinId = new UniversalBulletinId(oxfamAccountId, nonExistentPacketId);
		
		BulletinCatalog catalog = BulletinCatalog.getInstance();

		Assert.assertTrue(catalog.bulletinHasBeenIndexed(oxfamBulletinId));
		Assert.assertFalse(catalog.bulletinHasBeenIndexed(nonExistentBulletinId));
	}

}

