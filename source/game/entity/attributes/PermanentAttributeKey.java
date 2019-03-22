package game.entity.attributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Jason MacKeigan on 2018-06-07 at 3:09 PM
 */
public class PermanentAttributeKey<T> extends AttributeKeyAdapter<T> {

	public PermanentAttributeKey(T defaultValue, String serializationKey) {
		super(defaultValue, AttributeSerializationType.PERMANENT, serializationKey);
	}

	@Override
	public String toString() {
		return String.format("default=%s, key=%s", defaultValue(), serializationKeyOrNull());
	}
}
