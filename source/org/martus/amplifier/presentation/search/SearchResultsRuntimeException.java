package org.martus.amplifier.presentation.search;

/**
 * @author pdalbora
 */
public class SearchResultsRuntimeException extends RuntimeException
{

	/**
	 * Constructor for SearchResultsRuntimeException.
	 */
	public SearchResultsRuntimeException() {
		super();
	}

	/**
	 * Constructor for SearchResultsRuntimeException.
	 * @param message
	 */
	public SearchResultsRuntimeException(String message) {
		super(message);
	}

	/**
	 * Constructor for SearchResultsRuntimeException.
	 * @param message
	 * @param cause
	 */
	public SearchResultsRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for SearchResultsRuntimeException.
	 * @param cause
	 */
	public SearchResultsRuntimeException(Throwable cause) {
		super(cause);
	}

}
