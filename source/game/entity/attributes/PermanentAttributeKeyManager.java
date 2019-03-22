package game.entity.attributes;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MacKeigan on 2018-06-08 at 10:18 AM
 */
public class PermanentAttributeKeyManager {

	private static final PermanentAttributeKeyManager singleton = new PermanentAttributeKeyManager();

	private final Map<String, AttributeKey<?>> keys = new HashMap<>();

	public void populate() {
		if (!keys.isEmpty()) {
			throw new IllegalStateException("Cannot populate keys when it is already populated.");
		}
        Configuration configuration = new ConfigurationBuilder().useParallelExecutor().addScanners(new FieldAnnotationsScanner()).forPackages("game");

		Reflections reflections = new Reflections(configuration);

		Set<Field> fields = reflections.getFieldsAnnotatedWith(PermanentAttributeKeyComponent.class);

		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
				try {
					field.setAccessible(true);

					Object value = field.get(null);

					if (value instanceof PermanentAttributeKey) {
						final AttributeKey<?> key = (AttributeKey<?>) value;

						final String serializationKey = key.serializationKeyOrNull();

						if (serializationKey == null) {
							throw new RuntimeException(String.format("AttributeKey#serializationKeyOrNull is null: %s", key));
						}

						if (keys.containsKey(key.serializationKeyOrNull())) {
							throw new RuntimeException(String.format("Key already exists: %s", key));
						}
						keys.put(key.serializationKeyOrNull(), key);
					} else {
						throw new RuntimeException(String.format("Value for Field is not instance of PermanentAttributeKey: %s", value));
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(String.format("Field is not accessible: %s", field));
				}
			} else {
				throw new RuntimeException(String.format("Field is annotated with PermanentAttributeKey but is not public and static: %s", field));
			}
		}
	}

	public AttributeKey<?> getKey(String key) {
		return keys.get(key);
	}

	public Map<String, AttributeKey<?>> getKeys() {
		return keys;
	}

	public static PermanentAttributeKeyManager getSingleton() {
		return singleton;
	}
}
