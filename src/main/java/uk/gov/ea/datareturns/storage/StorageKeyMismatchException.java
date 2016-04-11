/**
 *
 */
package uk.gov.ea.datareturns.storage;

/**
 * Thrown if the {@link StorageProvider} is unable to retrieve a file for a particular key
 *
 * @author Sam Gardner-Dell
 *
 */
public class StorageKeyMismatchException extends StorageException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public StorageKeyMismatchException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StorageKeyMismatchException(final String message, final Throwable cause) {
		super(message, cause);
	}

}