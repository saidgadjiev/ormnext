package ru.saidgadjiev.ormnext.core.exception;

/**
 * Exception will be thrown when object instantiate.
 *
 * @author said gadjiev
 */
public class InstantiationException extends RuntimeException {

	/**
	 * Target instantiated class when thrown exception.
	 */
	private final Class clazz;

	/**
	 * Constructs a new instance exception with the specified detail message,
	 * cause and instantiated class.
	 *
	 * @param message target message
	 * @param clazz target class
	 * @param cause target clause
	 */
	public InstantiationException(String message, Class clazz, Throwable cause) {
		super(message, cause);
		this.clazz = clazz;
	}

	/**
	 * Constructs a new instantiate exception with the specified detail message and instantiated class.
	 *
	 * @param message target message
	 * @param clazz target class
	 */
	public InstantiationException(String message, Class clazz) {
		this(message, clazz, null);
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " : " + clazz.getName();
	}

}
