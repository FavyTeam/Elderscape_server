package game.content.minigame;

import core.ServerConfiguration;
import game.content.minigame.impl.fight_pits.FightPitsMinigame;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jason MacKeigan on 2018-01-24 at 11:19 AM
 * <p>
 * A means of loading miniames into a map via good ol fashion code. Json unfortunately
 * was not possible at this time without using a Deserializer and Serializer for each
 * new entry which didn't seam maintainable.
 */
public class WorldMinigameCodeLoader implements WorldMinigameLoader {

	@Override
	public Map<MinigameKey, Minigame> load() {
		Map<MinigameKey, Minigame> minigames = new HashMap<>();

		if (ServerConfiguration.DEBUG_MODE) {
			minigames.put(MinigameKey.FIGHT_PIT, new FightPitsMinigame(30));
		}

		return minigames;
	}
}
