package org.martus.amplifier.common;

import javax.servlet.http.HttpSession;

public class WrappedServletSession implements AmplifierServletSession
{
	WrappedServletSession(HttpSession sessionToWrap)
	{
		session = sessionToWrap;
	}
	
	public void setAttribute(String key, Object value)
	{
		session.setAttribute(key, value);
	}

	public Object getAttribute(String key)
	{
		return session.getAttribute(key);
	}
	
	HttpSession session;
}
