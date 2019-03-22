package game.content.minigame;

/**
 * Created by Jason MacKeigan on 2018-01-24 at 11:10 AM
 * <p>
 * Keys and {@link Minigame}s are paired. Only one key can be paired to one unique minigame and
 * vice versa.
 */
public enum MinigameKey {
	FIGHT_PIT(MinigameKeyCleanupPolicy.NEVER),

	ZULRAH(MinigameKeyCleanupPolicy.IF_NO_PLAYERS),

	VORKATH(MinigameKeyCleanupPolicy.IF_NO_PLAYERS);

	private final MinigameKeyCleanupPolicy cleanupPolicy;

	MinigameKey(MinigameKeyCleanupPolicy cleanupPolicy) {
		this.cleanupPolicy = cleanupPolicy;
	}

	public MinigameKeyCleanupPolicy getCleanupPolicy() {
		return cleanupPolicy;
	}
}
