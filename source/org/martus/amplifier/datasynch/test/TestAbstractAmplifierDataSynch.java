package org.martus.amplifier.datasynch.test;

import java.io.File;

import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.common.test.TestCaseEnhanced;
import org.martus.util.DirectoryTreeRemover;

public abstract class TestAbstractAmplifierDataSynch extends TestCaseEnhanced
{	
	public TestAbstractAmplifierDataSynch(String name)
	{
		super(name);
	}
	
	protected void tearDown() throws Exception 
	{
		String basePath = AmplifierConfiguration.getInstance().getBasePath() + "/test";
		DirectoryTreeRemover.deleteEntireDirectoryTree(new File(basePath));
	}
}
