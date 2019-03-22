package game.content.minigame.height_manager;

/**
 * Created by Jason MacKeigan on 2018-04-05 at 12:12 PM
 */
public interface HeightManager<T> extends HeightManagerCleanupEvent {

	int consume(T key);

	boolean isConsumable(T key);

	void release(T key, int height);

}
