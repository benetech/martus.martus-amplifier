package org.martus.amplifier.service.attachment;

import com.sleepycat.db.Db;
import com.sleepycat.db.Dbt;

public class StringDbt extends Dbt
{
    StringDbt()
    {
        set_flags(Db.DB_DBT_MALLOC); // tell Db to allocate on retrieval
    }

    StringDbt(String value)
    {
        setString(value);
        set_flags(Db.DB_DBT_MALLOC); // tell Db to allocate on retrieval
    }

    void setString(String value)
    {
        byte[] data = value.getBytes();
        set_data(data);
        set_size(data.length);
    }

    String getString()
    {
        return new String(get_data(), 0, get_size());
    }
}
