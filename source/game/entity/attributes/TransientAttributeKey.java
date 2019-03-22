package game.entity.attributes;

/**
 * Created by Jason MacKeigan on 2018-06-07 at 3:08 PM
 */
public class TransientAttributeKey<T> extends AttributeKeyAdapter<T> {

	public TransientAttributeKey(T defaultValue) {
		super(defaultValue, AttributeSerializationType.TRANSIENT, null);
	}

}
