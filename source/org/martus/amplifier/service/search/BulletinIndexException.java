package org.martus.amplifier.service.search;

/**
 * An exception class intended to encapsulate lower-level exceptions
 * that may be thrown from different implementations of BulletinIndexer
 * and BulletinSearcher.
 *  
 * @author PDAlbora
 */
public class BulletinIndexException extends Exception {
	
	

	/**
	 * Constructor for BulletinIndexException.
	 */
	public BulletinIndexException() {
		super();
	}

	/**
	 * Constructor for BulletinIndexException.
	 * @param message
	 */
	public BulletinIndexException(String message) {
		super(message);
	}

	/**
	 * Constructor for BulletinIndexException.
	 * @param message
	 * @param cause
	 */
	public BulletinIndexException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for BulletinIndexException.
	 * @param cause
	 */
	public BulletinIndexException(Throwable cause) {
		super(cause);
	}

}
