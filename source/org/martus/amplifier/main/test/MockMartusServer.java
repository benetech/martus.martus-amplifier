/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.amplifier.main.test;

import java.io.File;

import org.martus.amplifier.ServerCallbackInterface;
import org.martus.common.LoggerInterface;
import org.martus.common.crypto.MartusCrypto;


public class MockMartusServer implements ServerCallbackInterface
{
	public MockMartusServer(File dir, LoggerInterface loggerToUse, MartusCrypto securityToUse)
	{
		dataDirectory = dir;
		logger = loggerToUse;
		security = securityToUse;
	}
	public MartusCrypto getSecurity()
	{
		return security;
	}
	public boolean isShutdownRequested()
	{
		return false;
	}
	
	public File getStartupConfigDirectory()
	{
		return null;
	}
	public File getDataDirectory()
	{
		return dataDirectory;
	}
	public String getAmpIpAddress()
	{
		return null;
	}
	public LoggerInterface getLogger()
	{
		return logger;
	}
	

	public void logError(String message)
	{
		logger.logError(message);
	}
	
	public void logInfo(String message)
	{
		logger.logInfo(message);
	}

	public void logNotice(String message)
	{
		logger.logNotice(message);
	}

	public void logWarning(String message)
	{
		logger.logWarning(message);
	}

	public void logDebug(String message)
	{
		logger.logDebug(message);
	}

	public boolean wantsDevelopmentMode()
	{
		return false;
	}

	MartusCrypto security;
	LoggerInterface logger;
	File dataDirectory;
	
}
