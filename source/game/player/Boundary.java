package game.player;

import game.position.Position;
import game.npc.Npc;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Jason http://www.rune-server.org/members/jason
 */
public class Boundary {

	private final int minX;

	private final int minY;

	private final int highX;

	private final int highY;

	private final int height;

	private final Position center;

	/**
	 * @param minX The south-west x coordinate
	 * @param minY The south-west y coordinate
	 * @param highX The north-east x coordinate
	 * @param highY The north-east y coordinate
	 */
	public Boundary(int minX, int minY, int highX, int highY) {
		this(minX, minY, highX, highY, 0);
	}

	/**
	 * @param minX The south-west x coordinate
	 * @param minY The south-west y coordinate
	 * @param highX The north-east x coordinate
	 * @param highY The north-east y coordinate
	 * @param height The height of the boundary
	 */
	public Boundary(int minX, int minY, int highX, int highY, int height) {
		this.minX = minX;
		this.minY = minY;
		this.highX = highX;
		this.highY = highY;
		this.height = height;
		this.center = new Position((minX + highX) / 2, (minY + highY) / 2, height);
	}

	public Boundary withHeight(int height) {
		return new Boundary(minX, minY, highX, highY, height);
	}

	public int getMinimumX() {
		return minX;
	}

	public int getMinimumY() {
		return minY;
	}

	public int getMaximumX() {
		return highX;
	}

	public int getMaximumY() {
		return highY;
	}

	public Position getCenter() {
		return center;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * @param player The player object
	 * @param boundaries The boundary object
	 * @return
	 */
	public static boolean isIn(Player player, Boundary boundaries) {
		if (boundaries.height > 0 && player.getHeight() != boundaries.height) {
			return false;
		}
		return player.getX() >= boundaries.minX && player.getX() <= boundaries.highX
		       && player.getY() >= boundaries.minY && player.getY() <= boundaries.highY;
	}

	/**
	 * Determines if the player is in any of the given boundaries provided.
	 *
	 * @param player the player we're determining is in one of the boundaries are not.
	 * @param boundaries the boundaries we're determining the player is in.
	 * @return {@code true} if the player is in any of the boundaries defined by {@link #isIn(Player, Boundary)}
	 */
	public static boolean isInAny(Player player, Boundary... boundaries) {
		return Stream.of(boundaries).anyMatch(boundary -> Boundary.isIn(player, boundary));
	}

	public static boolean isIn(int x, int y, int z, Boundary boundaries) {
		if (boundaries.height > 0) {
			if (z != boundaries.height) {
				return false;
			}
		}
		return x >= boundaries.minX && x <= boundaries.highX && y >= boundaries.minY && y <= boundaries.highY;
	}

	/**
	 * @param npc The npc object
	 * @param boundaries The boundary object
	 * @return
	 */
	public static boolean isIn(Npc npc, Boundary boundaries) {
		if (boundaries.height > 0) {
			if (npc.getHeight() != boundaries.height) {
				return false;
			}
		}
		return npc.getX() >= boundaries.minX && npc.getX() <= boundaries.highX && npc.getY() >= boundaries.minY && npc.getY() <= boundaries.highY;
	}

	public static boolean isIn(Npc npc, Boundary[] boundaries) {
		for (Boundary boundary : boundaries) {
			if (boundary.height > 0) {
				if (npc.getHeight() != boundary.height) {
					return false;
				}
			}
			if (npc.getX() >= boundary.minX && npc.getX() <= boundary.highX && npc.getY() >= boundary.minY && npc.getY() <= boundary.highY) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInSameBoundary(Player player1, Player player2, Boundary[] boundaries) {
		Optional<Boundary> boundary1 = Arrays.asList(boundaries).stream().filter(b -> isIn(player1, b)).findFirst();
		Optional<Boundary> boundary2 = Arrays.asList(boundaries).stream().filter(b -> isIn(player2, b)).findFirst();
		if (!boundary1.isPresent() || !boundary2.isPresent()) {
			return false;
		}
		return Objects.equals(boundary1.get(), boundary2.get());
	}

	public static int entitiesInArea(Boundary boundary) {
		int i = 0;
		for (Player player : PlayerHandler.players)
			if (player != null)
				if (isIn(player, boundary))
					i++;
		return i;
	}

	public static final Boundary ORB_CHARGING_AREA = new Boundary(3083, 3566, 3090, 3573);

	public static final Boundary BANDOS_GODWARS = new Boundary(2864, 5351, 2876, 5369);

	public static final Boundary ARMADYL_GODWARS = new Boundary(2824, 5296, 2842, 5308);

	public static final Boundary ZAMORAK_GODWARS = new Boundary(2918, 5318, 2936, 5331);

	public static final Boundary SARADOMIN_GODWARS = new Boundary(2889, 5258, 2907, 5276);

	public static final Boundary[] GODWARS_BOSSROOMS =
			{BANDOS_GODWARS, ARMADYL_GODWARS, ZAMORAK_GODWARS, SARADOMIN_GODWARS};

	public static final Boundary RESOURCE_AREA = new Boundary(3174, 3924, 3196, 3944);

	public static final Boundary KBD_AREA = new Boundary(2251, 4675, 2296, 4719);

	public static final Boundary PEST_CONTROL_AREA = new Boundary(2650, 2635, 2675, 2655);

	public static final Boundary FIGHT_CAVE = new Boundary(2365, 5052, 2429, 5122);

	public static final Boundary EDGEVILLE_PERIMETER = new Boundary(3073, 3465, 3108, 3518);

	public static final Boundary[] DUEL_ARENAS = new Boundary[]
			                                             {new Boundary(3332, 3244, 3359, 3259), new Boundary(3364, 3244, 3389, 3259)};

	public static final Boundary HUNTING_AREA = new Boundary(3126, 3759, 3162, 3797);
}
