package utility;

import java.util.Objects;

/**
 * Created by Jason MacKeigan on 2018-04-17 at 1:50 PM
 * <p>
 * A utility class that returns the first object if its not null, or the default value of it is. It
 * should be noted that the default value cannot be null.
 */
public final class NonNull {

	/**
	 * Returns the value if its not null or the default if it is. The default cannot be null.
	 *
	 * @param value the first value we're comparing.
	 * @param defaultValue the default value if the first value is null.
	 * @param <T> the type of object were comparing.
	 * @return a non-null object.
	 */
	public static <T> T either(T value, T defaultValue) {
		return value == null ? Objects.requireNonNull(defaultValue) : value;
	}

}
