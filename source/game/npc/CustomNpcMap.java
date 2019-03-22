package game.npc;

import game.type.GameTypeIdentity;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import core.GameType;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jason MacKeigan on 2018-06-06 at 2:27 PM
 */
public class CustomNpcMap {

	/**
	 * The single instance of this class.
	 */
	private static final CustomNpcMap singleton = new CustomNpcMap();

	/**
	 * A mapping of npc types to their respective npc instance. This instance should never be
	 * returned, however, {@link Npc#copy(int)} should be called on the reference.
	 */
	private final Map<Integer, Npc> npcs = new HashMap<>();

	/**
	 * A private constructor to satisfy the singleton pattern.
	 */
	private CustomNpcMap() {

	}

	/**
	 * Populates the underlying {@link #npcs} mapping by searching reflectively for classes that are
	 * annotated with {@link CustomNpcComponent}.
	 */
	public void load() {
		Configuration configuration = new ConfigurationBuilder().useParallelExecutor()
				.addScanners(new TypeAnnotationsScanner(), new SubTypesScanner()).forPackages("game");

		Set<Class<?>> custom =
				new Reflections(configuration).getTypesAnnotatedWith(CustomNpcComponent.class);

		if (!custom.isEmpty()) {
			for (Class<?> npcClass : custom) {
				CustomNpcComponent component = npcClass.getDeclaredAnnotation(CustomNpcComponent.class);

				if (component == null) {
					continue;
				}
				GameTypeIdentity[] identities = component.identities();

				if (identities.length == 0) {
					throw new RuntimeException(
							String.format("Custom npc must contain an id: %s", npcClass));
				}
				for (GameTypeIdentity identity : identities) {
					int[] ids = identity.identity();

					GameType type = identity.type();

					if (!GameType.getGameType(type)) {
						continue;
					}
					for (int id : ids) {
						if (npcs.containsKey(id)) {
							throw new RuntimeException(
									String.format("Duplicate Npc already exists: %s", npcClass));
						}
						try {
							Object object =
									npcClass.getConstructor(int.class, int.class).newInstance(-1, id);

							if (object instanceof Npc) {
								Npc npc = (Npc) object;

								try {
									npc.copy(-1);
								} catch (UnsupportedOperationException unsupported) {
									throw new RuntimeException(
											"Custom npc does not override copy which is required: "
													+ component,
											unsupported);
								}
								npcs.put(id, npc);
							} else {
								throw new RuntimeException("Object is not an instance of npc: [component="
										+ component + ", npc=" + npcClass + "]");
							}
						} catch (InstantiationException | IllegalAccessException
								| InvocationTargetException | NoSuchMethodException e) {
							throw new RuntimeException("Unable to parse custom npc component: [component="
									+ component + ", npc=" + npcClass + "]", e);
						}
					}
				}
			}
		}
	}

	/**
	 * Creates a new instance of the npc for the given type, with the given index, if it exists. If
	 * it does not exist, null is returned.
	 *
	 * @param type the type of npc.
	 * @param index the index of the npc.
	 * @return creates the npc using the copy function using the given index, or returns null.
	 */
	public Npc createCopyOrNull(int type, int index) {
		Npc npc = npcs.get(type);

		if (npc == null) {
			return null;
		}

		return npc.copy(index);
	}

	/**
	 * The single instance to this class for global access.
	 *
	 * @return the single instance.
	 */
	public static CustomNpcMap getSingleton() {
		return singleton;
	}

}
