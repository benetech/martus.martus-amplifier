package org.martus.amplifier.service.search;

/**
 * An exception class intended to encapsulate lower-level exceptions
 * that may be thrown from different implementations of BulletinSearcher.
 * 
 * @author pdalbora
 */
public class BulletinSearchException extends Exception 
{

	/**
	 * Constructor for BulletinSearchException.
	 */
	public BulletinSearchException() {
		super();
	}

	/**
	 * Constructor for BulletinSearchException.
	 * @param message
	 */
	public BulletinSearchException(String message) {
		super(message);
	}

	/**
	 * Constructor for BulletinSearchException.
	 * @param message
	 * @param cause
	 */
	public BulletinSearchException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for BulletinSearchException.
	 * @param cause
	 */
	public BulletinSearchException(Throwable cause) {
		super(cause);
	}

}
