package game.content.minigame.single_minigame.zulrah;

import game.content.minigame.MinigameKey;
import game.content.minigame.height_manager.MinigameKeyHeightManager;
import game.content.minigame.height_manager.NoAvailableHeightException;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-04-09 at 10:09 AM
 */
public final class ZulrahSinglePlayerMinigameFactory {

	private ZulrahSinglePlayerMinigameFactory() {

	}

	public static ZulrahSinglePlayerMinigame create(Player player) throws NoAvailableHeightException {
		MinigameKeyHeightManager heightManager = MinigameKeyHeightManager.getSingleton();

		if (!heightManager.isConsumable(MinigameKey.ZULRAH)) {
			// stop the minigame because no height is available and inform player.
			throw new NoAvailableHeightException("No height was available for zulrah.");
		}
		return new ZulrahSinglePlayerMinigame(MinigameKey.ZULRAH, player,
		                                      heightManager.consume(MinigameKey.ZULRAH));
	}
}
