package org.martus.amplifier.presentation;

import javax.servlet.http.HttpServletRequest;

public class WrappedServletRequest implements AmplifierServletRequest
{
	public WrappedServletRequest(HttpServletRequest requestToWrap)
	{
		request = requestToWrap;
	}
	
	public String getParameter(String key)
	{
		return request.getParameter(key);
	}

	public AmplifierServletSession getSession()
	{
		return new WrappedServletSession(request.getSession());
	}
	
	HttpServletRequest request;

}
