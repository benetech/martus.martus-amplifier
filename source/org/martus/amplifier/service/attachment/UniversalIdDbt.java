package org.martus.amplifier.service.attachment;

import java.io.IOException;

import org.martus.amplifier.common.bulletin.UniversalBulletinId;

import com.sleepycat.db.Db;
import com.sleepycat.db.Dbt;

public class UniversalIdDbt extends Dbt
{

	public UniversalIdDbt(UniversalBulletinId id)
	{
		super();
		set_flags(Db.DB_DBT_MALLOC);
	}
}
