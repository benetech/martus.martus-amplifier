package org.martus.amplifier.test.presentation;

import java.util.HashMap;
import java.util.Map;

import org.martus.amplifier.presentation.AmplifierServletRequest;

public class MockAmplifierRequest implements AmplifierServletRequest
{
	public void putParameter(String key, String value)
	{
		parameters.put(key, value);
	}
	
	public String getParameter(String key)
	{
		return (String)parameters.get(key);
	}


	Map parameters = new HashMap();
}
