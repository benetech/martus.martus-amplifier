package org.martus.amplifier.service.attachment;

import org.martus.common.UniversalId;

import com.sleepycat.db.Db;
import com.sleepycat.db.Dbt;

public class UniversalIdDbt extends Dbt
{
	private static final String FILE_KEY_SUFFIX = "_file";
	
	public UniversalIdDbt(UniversalId id)
	{
		super();
		set_flags(Db.DB_DBT_MALLOC);
		set_data((id.toString() + FILE_KEY_SUFFIX).getBytes());
	}

}
