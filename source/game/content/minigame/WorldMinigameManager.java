package game.content.minigame;

import game.log.GameTickLog;
import game.player.Player;
import game.player.event.CycleEventHandler;

import java.util.Map;

/**
 * Created by Jason MacKeigan on 2018-01-24 at 11:09 AM
 * <p>
 * A means of managing minigames that only require a single instance globally.
 */
public final class WorldMinigameManager implements MinigameManager {

	/**
	 * A mapping of minigames to their respective keys.
	 */
	private final Map<MinigameKey, Minigame> minigames;

	/**
	 * Creates a new manager using the map of minigames from the loader.
	 *
	 * @param minigames the mapping of minigames and keys.
	 */
	public WorldMinigameManager(Map<MinigameKey, Minigame> minigames) {
		this.minigames = minigames;
	}

	/**
	 * Referenced when a player logs into the game that should trigger the {@link Minigame#onLogin(Player)} function
	 * if inside the minigame.
	 *
	 * @param player the player logging in.
	 */
	@Override
	public void login(Player player) {
		Minigame minigame = minigames.values().stream().filter(m -> m.inside(player)).findAny().orElse(null);

		if (minigame != null) {
			minigame.onLogin(player);
		}
	}

	/**
	 * Attempts to retrieves a minigame for the given key.
	 *
	 * @param key the key to the possible minigame.
	 * @return the minigame.
	 */
	public Minigame getMinigameOrNull(MinigameKey key) {
		return minigames.get(key);
	}

	/**
	 * Referenced every game cycle from the game thread. Subsequently
	 * calls the {@link CycleEventHandler#cycle()} function from {@link Minigame#getEventHandler()}.s
	 */
	@Override
	public void cycle() {
		GameTickLog.minigameTickDuration = System.currentTimeMillis();
		for (Minigame minigame : minigames.values()) {
			if (minigame == null) {
				continue;
			}

			minigame.getEventHandler().cycle();
		}
		GameTickLog.minigameTickDuration = System.currentTimeMillis() - GameTickLog.minigameTickDuration;
	}

	@Override
	public String toString() {
		return "WorldMinigameManager{" +
		       "minigames=" + minigames +
		       '}';
	}
}
