package game.content.item;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import core.GameType;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-06-06 at 2:27 PM
 * 
 * @author 2012
 */
public class ItemInteractionMap {

	/**
	 * The single instance of this class.
	 */
	private static final ItemInteractionMap singleton = new ItemInteractionMap();

	/**
	 * A mapping of item types to their respective item instance.
	 */
	private final Map<Integer, ItemInteraction> items = new HashMap<>();

	/**
	 * A private constructor to satisfy the singleton pattern.
	 */
	private ItemInteractionMap() {

	}

	/**
	 * Populates the underlying {@link #items} mapping by searching reflectively for classes that are
	 * annotated with {@link ItemInteractionComponent}.
	 */
	public void load() {
		Configuration configuration = new ConfigurationBuilder().useParallelExecutor()
				.addScanners(new TypeAnnotationsScanner(), new SubTypesScanner()).forPackages("game");

		Set<Class<?>> custom =
				new Reflections(configuration).getTypesAnnotatedWith(ItemInteractionComponent.class);

		if (!custom.isEmpty()) {
			for (Class<?> itemClass : custom) {
				ItemInteractionComponent component =
						itemClass.getDeclaredAnnotation(ItemInteractionComponent.class);
				if (component == null) {
					continue;
				}
				GameTypeIdentity[] identities = component.identities();
				if (identities.length == 0) {
					throw new RuntimeException(
							String.format("Custom item must contain an id: %s", itemClass));
				}
				for (GameTypeIdentity identity : identities) {
					int[] ids = identity.identity();
					GameType type = identity.type();
					if (!GameType.getGameType(type)) {
						continue;
					}
					for (int id : ids) {
						try {
							Object object = itemClass.getConstructor().newInstance();

							if (object instanceof ItemInteraction) {
								ItemInteraction item = (ItemInteraction) object;
								if (items.containsKey(id)) {
									throw new RuntimeException(
											String.format("Duplicate item already exists: %s", itemClass));
								}
								items.put(id, item);
							} else {
								throw new RuntimeException("Object is not an instance of item: [component="
										+ component + ", item=" + itemClass + "]");
							}
						} catch (InstantiationException | IllegalAccessException
								| InvocationTargetException | NoSuchMethodException e) {
							throw new RuntimeException("Unable to parse custom item component: [component="
									+ component + ", item=" + itemClass + "]", e);
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the item interaction
	 * 
	 * @param id the id
	 * @return the interaction
	 */
	public ItemInteraction get(int id) {
		return items.get(id);
	}

	/**
	 * The single instance to this class for global access.
	 *
	 * @return the single instance.
	 */
	public static ItemInteractionMap getSingleton() {
		return singleton;
	}
}
