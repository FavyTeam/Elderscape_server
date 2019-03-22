package game.content.minigame.height_manager;

/**
 * Created by Jason MacKeigan on 2018-04-05 at 12:19 PM
 * <p>
 * Thrown when a manage cannot consume a height because one is not available.
 */
public class NoAvailableHeightException extends RuntimeException {

	/**
	 * Constructs a new runtime exception with {@code null} as its
	 * detail message.  The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause}.
	 */
	public NoAvailableHeightException() {
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
	public NoAvailableHeightException(String message) {
		super(message);
	}

	/**
	 * Constructs a new runtime exception with the specified detail message and
	 * cause.  <p>Note that the detail message associated with
	 * {@code cause} is <i>not</i> automatically incorporated in
	 * this runtime exception's detail message.
	 *
	 * @param message the detail message (which is saved for later retrieval
	 * by the {@link #getMessage()} method).
	 * @param cause the cause (which is saved for later retrieval by the
	 * {@link #getCause()} method).  (A <tt>null</tt> value is
	 * permitted, and indicates that the cause is nonexistent or
	 * unknown.)
	 * @since 1.4
	 */
	public NoAvailableHeightException(String message, Throwable cause) {
		super(message, cause);
	}
}
