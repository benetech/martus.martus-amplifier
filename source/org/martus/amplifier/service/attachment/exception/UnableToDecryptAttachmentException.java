package org.martus.amplifier.service.attachment.exception;

import org.martus.amplifier.exception.MartusAmplifierRuntimeException;

public class UnableToDecryptAttachmentException extends MartusAmplifierRuntimeException 
{

	public UnableToDecryptAttachmentException()
	{
		super();
	}

	public UnableToDecryptAttachmentException(String arg0)
	{
		super(arg0);
	}

	public UnableToDecryptAttachmentException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public UnableToDecryptAttachmentException(Throwable arg0)
	{
		super(arg0);
	}

}
