package org.martus.amplifier.presentation;

import javax.servlet.http.HttpSession;

public interface AmplifierServletRequest
{
	public String getParameter(String key);
	public HttpSession getSession();
}
