package org.martus.amplifier.test.presentation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

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

	public HttpSession getSession()
	{
		return httpSession;
	}

	Map parameters = new HashMap();
	MockHttpSession httpSession = new MockHttpSession();
}
