package game.entity.attributes;

/**
 * Created by Jason MacKeigan on 2018-06-07 at 3:00 PM
 */
public class AttributeKeyAdapter<T> implements AttributeKey<T> {

	private final T defaultValue;

	private final AttributeSerializationType serializationType;

	private final String serializationKey;

	public AttributeKeyAdapter(T defaultValue, AttributeSerializationType serializationType, String serializationKey) {
		this.defaultValue = defaultValue;
		this.serializationType = serializationType;
		this.serializationKey = serializationKey;
	}

	@Override
	public T defaultValue() {
		return defaultValue;
	}

	@Override
	public AttributeSerializationType serializationType() {
		return serializationType;
	}

	@Override
	public String serializationKeyOrNull() {
		return serializationKey;
	}
}
