package org.martus.amplifier.service.search.api;

import org.martus.amplifier.common.bulletin.UniversalBulletinId;
import org.martus.amplifier.service.search.api.IBulletinSearcher;

/**
 * @author Bskinner
 *
 */
public interface IBulletinCatalog {
	public boolean bulletinHasBeenIndexed(UniversalBulletinId UniversalBulletinId);
}
