package org.martus.amplifier.service.attachment;

public class AttachmentNotFoundException extends AttachmentStorageException
{
	/**
	 * Constructor for AttachmentNotFoundException.
	 */
	public AttachmentNotFoundException() {
		super();
	}

	/**
	 * Constructor for AttachmentNotFoundException.
	 * @param message
	 */
	public AttachmentNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructor for AttachmentNotFoundException.
	 * @param message
	 * @param cause
	 */
	public AttachmentNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for AttachmentNotFoundException.
	 * @param cause
	 */
	public AttachmentNotFoundException(Throwable cause) {
		super(cause);
	}

}