package org.martus.amplifier.test.presentation;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.context.Context;

public class MockContext implements Context
{

	public Object put(String key, Object value)
	{
		return values.put(key, value);
	}

	public Object get(String key)
	{
		return values.get(key);
	}

	public boolean containsKey(Object arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Object[] getKeys()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object remove(Object arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	Map values = new HashMap();
}
