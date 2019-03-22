package game.object.clip;

import java.util.ArrayList;
import java.util.List;

public class DynamicClipping {

	public static List<DynamicClipping> dynamicClipping = new ArrayList<DynamicClipping>();

	public int x;

	public int y;

	public int height;

	public int direction;

	public boolean pass;

	public String uid;

	public DynamicClipping(int x, int y, int height, int direction, boolean pass, String uid) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.direction = direction;
		this.pass = pass;
		this.uid = uid;
	}

	/**
	 * @return 0 means not blocked or unblocked, 2 means blocked, 1 means unblocked.
	 */
	public static int dynamicPathResult(int x, int y, int height, int direction) {
		for (int index = 0; index < dynamicClipping.size(); index++) {
			if (x == dynamicClipping.get(index).x && y == dynamicClipping.get(index).y && height == dynamicClipping.get(index).height && direction == dynamicClipping
					                                                                                                                                          .get(index).direction) {
				if (dynamicClipping.get(index).pass) {
					return 1;
				} else {
					return 2;
				}
			}
		}
		return 0;
	}

	public static void removeFromDynamicTileClipping(int x, int y, int height, int direction, boolean pass) {
		for (int index = 0; index < dynamicClipping.size(); index++) {
			if (x == dynamicClipping.get(index).x && y == dynamicClipping.get(index).y && height == dynamicClipping.get(index).height && direction == dynamicClipping
					                                                                                                                                          .get(index).direction
			    && pass == dynamicClipping.get(index).pass) {
				dynamicClipping.remove(index);
				break;
			}
		}

	}

}
