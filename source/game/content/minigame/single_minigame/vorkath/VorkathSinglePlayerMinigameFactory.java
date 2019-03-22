package game.content.minigame.single_minigame.vorkath;

import game.content.minigame.MinigameKey;
import game.content.minigame.height_manager.MinigameKeyHeightManager;
import game.content.minigame.height_manager.NoAvailableHeightException;
import game.player.Player;

public final class VorkathSinglePlayerMinigameFactory {

	private VorkathSinglePlayerMinigameFactory() {

	}

	public static VorkathSinglePlayerMinigame create(Player player) throws NoAvailableHeightException {
		MinigameKeyHeightManager heightManager = MinigameKeyHeightManager.getSingleton();

		if (!heightManager.isConsumable(MinigameKey.VORKATH)) {
			throw new NoAvailableHeightException("No height was available for vorkath.");
		}
		return new VorkathSinglePlayerMinigame(player, heightManager.consume(MinigameKey.VORKATH));
	}
}
