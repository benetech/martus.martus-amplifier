package org.martus.amplifier.service.attachment;

/**
 * An exception class intended to encapsulate lower-level exceptions
 * that may be thrown from different implementations of AttachmentManager.
 * 
 * @author PDAlbora
 */
public class AttachmentStorageException extends Exception 
{

	/**
	 * Constructor for AttachmentStorageException.
	 */
	public AttachmentStorageException() {
		super();
	}

	/**
	 * Constructor for AttachmentStorageException.
	 * @param message
	 */
	public AttachmentStorageException(String message) {
		super(message);
	}

	/**
	 * Constructor for AttachmentStorageException.
	 * @param message
	 * @param cause
	 */
	public AttachmentStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for AttachmentStorageException.
	 * @param cause
	 */
	public AttachmentStorageException(Throwable cause) {
		super(cause);
	}

}
