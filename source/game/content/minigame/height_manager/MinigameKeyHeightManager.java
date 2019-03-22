package game.content.minigame.height_manager;

import game.content.minigame.MinigameKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jason MacKeigan on 2018-04-05 at 12:13 PM
 * <p>
 * The purpose of this class is to provide a height that is open and free of players
 * based on the given minigame key.
 */
public class MinigameKeyHeightManager implements HeightManager<MinigameKey> {

	/**
	 * The single instance of this height manager to be used on the game thread.
	 */
	private static final MinigameKeyHeightManager singleton = new MinigameKeyHeightManager(24, 512);

	/**
	 * The interval used to determine the height.
	 */
	private static final int INTERVAL = 4;

	/**
	 * A mapping of heights that are consumed.
	 */
	private final Map<MinigameKey, Set<Integer>> consumed = new HashMap<>();

	/**
	 * The minimum height allowed.
	 */
	private final int minimumHeight;

	/**
	 * The maximum height that can be consumed by this manager.
	 */
	private final int maximumHeight;

	public MinigameKeyHeightManager(int minimumHeight, int maximumHeight) {
		if (maximumHeight > 0 && maximumHeight % INTERVAL > 0) {
			throw new IllegalArgumentException("The maximum height must be a multiple of 4 and greater than or equal to 4.");
		}
		if (minimumHeight > 0 && minimumHeight % INTERVAL > 0) {
			throw new IllegalArgumentException("Minimum height cannot be less than 4, and must be a multiple of 4.");
		}
		if (minimumHeight > maximumHeight || minimumHeight == maximumHeight) {
			throw new IllegalArgumentException("The minimum height cannot be less than the maximum height and they cannot be equal.");
		}
		this.maximumHeight = maximumHeight;
		this.minimumHeight = minimumHeight;
	}

	@Override
	public int consume(MinigameKey key) {
		Set<Integer> heightsConsumed = consumed.getOrDefault(key, new HashSet<>(maximumHeight / INTERVAL));

		for (int height = minimumHeight; height < maximumHeight; height += INTERVAL) {
			if (!heightsConsumed.contains(height)) {
				heightsConsumed.add(height);
				consumed.put(key, heightsConsumed);

				return height;
			}
		}
		throw new NoAvailableHeightException("No available height can be created for this minigame key.");
	}

	@Override
	public boolean isConsumable(MinigameKey key) {
		return consumed.size() < maximumHeight / INTERVAL;
	}

	@Override
	public void release(MinigameKey key, int height) {
		Set<Integer> heightsConsumed = consumed.get(key);

		if (heightsConsumed == null) {
			// no heights are consumed for this key, nothing needs to be released
			return;
		}
		heightsConsumed.removeIf(h -> h == height);
	}

	/**
	 * The single instance of this height manager.
	 *
	 * @return the single instance.
	 */
	public static MinigameKeyHeightManager getSingleton() {
		return singleton;
	}

	@Override
	public void cleanup() {
		//TODO create a means of cleaning up an instance of no players are inside of it.
		//TODO the current issue is that it's not possible to cleanup an area by checking
		//TODO if it has players because keys dont have boundaries, MinigameAreaKey dont either.
	}
}
