package org.martus.amplifier.presentation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

	public HttpSession getSession()
	{
		return request.getSession();
	}
	
	HttpServletRequest request;

}
