package game.entity.attributes;

/**
 * Created by Jason MacKeigan on 2018-03-13 at 2:49 PM
 */
public interface AttributeKey<T> {

	T defaultValue();

	AttributeSerializationType serializationType();

	String serializationKeyOrNull();

}
