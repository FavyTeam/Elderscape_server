import game.entity.EntityType;
import game.player.Boundary;
import game.player.Player;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MacKeigan on 2018-01-26 at 11:46 AM
 * <p>
 * After the test it was concluded that 2 ^ 16 references to the function {@link Boundary#isIn(Player, Boundary)} and similar would
 * take a total of anywhere between 3 milliseconds and 5 milliseconds.
 */
public class BoundaryTest {

	public static void main(String[] args) {
		Player player = new Player(null, -1, true, EntityType.PLAYER);

		player.setX(3400);
		player.setY(6600);

		long time = System.nanoTime();

		double iterations = Math.pow(2, 16);

		Boundary boundary = new Boundary(3200, 6400, 3600, 6800);

		while (iterations-- > 0.0) {
			boolean result = Boundary.isIn(player, boundary);
		}

		System.out.println(String.format("time elapsed=%s", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - time)));
	}

}
