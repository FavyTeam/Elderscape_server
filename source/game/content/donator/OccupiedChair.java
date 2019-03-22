package game.content.donator;

import java.util.ArrayList;
import java.util.List;

/**
 * Occupied chair system.
 *
 * @author MGT Madness, created on 08-08-2017.
 */
public class OccupiedChair {

	/**
	 * Store data of chairs in this list.
	 */
	public static List<OccupiedChair> chairData = new ArrayList<OccupiedChair>();

	/**
	 * The x coord of the chair.
	 */
	private int x;

	/**
	 * The y coord of the chair.
	 */
	private int y;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}


	public OccupiedChair(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
