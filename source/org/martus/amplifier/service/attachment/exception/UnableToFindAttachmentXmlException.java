package org.martus.amplifier.service.attachment.exception;

import org.martus.amplifier.exception.MartusAmplifierRuntimeException;

public class UnableToFindAttachmentXmlException extends MartusAmplifierRuntimeException {

	public UnableToFindAttachmentXmlException(String arg0)
	{
		super(arg0);
	}

	public UnableToFindAttachmentXmlException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public UnableToFindAttachmentXmlException(Throwable arg0)
	{
		super(arg0);
	}

	public UnableToFindAttachmentXmlException()
	{
		super();
	}

}
