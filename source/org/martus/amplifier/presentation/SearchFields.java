
package org.martus.amplifier.presentation;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

public class SearchFields
{
	public SearchFields()
	{
		results = new Hashtable();		
	}
	
	public void add(Object field, Object value)
	{
		results.put(field, value);
	}
	
	public void remove(Object key)
	{
		results.remove(key);
	}
	
	public Object getValue(String fieldName)
	{
		return results.get(fieldName);
	}	
	
	public Collection getResults()
	{
		return results.values();
	}
	
	Hashtable results;
}
