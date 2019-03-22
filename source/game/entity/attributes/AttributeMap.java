package game.entity.attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sun.org.apache.xpath.internal.operations.Bool;
import game.content.item.impl.TrackingHelmet;

/**
 * Created by Jason MacKeigan on 2018-03-13 at 2:56 PM
 * <p>
 * of the default type of the attribute key.
 */
public class AttributeMap {

	private final Map<AttributeKey<?>, Object> values = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getOrDefault(AttributeKey<T> key, T defaultValue) {
		Object value = values.get(key);

		if (value == null) {
			return defaultValue;
		}
		return (T) value;
	}

	public <T> T getOrDefault(AttributeKey<T> key) {
		return getOrDefault(key, key.defaultValue());
	}

	public Boolean is(AttributeKey<Boolean> key) {
		return getOrDefault(key, key.defaultValue());
	}

	public Boolean isNot(AttributeKey<Boolean> key) {
		return !is(key);
	}

	public void put(AttributeKey<?> key, Object value) {
		values.put(key, value);
	}

	public void put(AttributeKey<?> key) {
		values.put(key, key.defaultValue());
	}

	public void increase(AttributeKey<Integer> key) {
		put(key, getOrDefault(key) + 1);
	}

	public Set<Map.Entry<AttributeKey<?>, Object>> entries() {
		return values.entrySet();
	}

	public boolean containsKey(AttributeKey<?> key) {
		return values.containsKey(key);
	}

}
