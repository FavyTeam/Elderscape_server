package game.container;

/**
 * Created by Jason MacKeigan on 2018-04-20 at 10:28 AM
 */
public class ItemContainerFullException extends RuntimeException {

	/**
	 * Constructs a new runtime exception with {@code null} as its
	 * detail message.  The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause}.
	 */
	public ItemContainerFullException() {
		super();
	}

	/**
	 * Constructs a new runtime exception with the specified detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for
	 * later retrieval by the {@link #getMessage()} method.
	 */
	public ItemContainerFullException(String message) {
		super(message);
	}
}
