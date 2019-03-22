package game.content.minigame;

import java.util.Map;

/**
 * Created by Jason MacKeigan on 2018-01-24 at 11:18 AM
 * <p>
 * A means of loading minigame data to a mapping where the key is the minigame key.
 */
public interface WorldMinigameLoader {

	Map<MinigameKey, Minigame> load();

}
