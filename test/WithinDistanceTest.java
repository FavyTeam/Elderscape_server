import game.entity.EntityType;
import game.player.Player;
import utility.Misc;

/**
 * Created by Jason MacKeigan on 2018-02-15 at 1:13 PM
 */
public class WithinDistanceTest {

	public static void main(String... args) {
		int x = 3200;

		int y = 3200;

		Player player = new Player(null, -1, true, EntityType.PLAYER);

		player.setX(3200);
		player.setY(3200);

		for (int xOffset = 0; xOffset < 25; xOffset++) {
			System.out.println(String.format("originalWithinDistance=%s, newWithinDistance=%s",
			                                 player.getPA().withinDistance(x + xOffset, y + xOffset),
			                                 Misc.withinLocalViewport(3200, 3200, x + xOffset, y + xOffset)));
		}
	}
}
