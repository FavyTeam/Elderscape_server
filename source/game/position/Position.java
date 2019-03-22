package game.position;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import game.npc.Npc;
import game.object.clip.Region;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-01-09 at 11:28 AM
 * <p>
 * Represents the x, y, and z location of a location in the game. It should be noted that this class
 * is intended to be immutable and should remain that way due to the structure and contract of the class.
 */
public class Position {

	/**
	 * The x coordinate on the map.
	 */
	private final int x;

	/**
	 * The y coordinate on the map.
	 */
	private final int y;

	/**
	 * The z or height coordinate on the map.
	 */
	private final int z;

	/**
	 * The hash of the x, y, and z.
	 */
	private final int hashcode;

	/**
	 * Creates a new position from the given coordinates.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 */
	public Position(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.hashcode = Objects.hash(x, y, z);
	}

	/**
	 * Creates a new position from the given position.
	 *
	 * @param position
	 * 			  the position we're copying.
	 */
	public Position(Position position) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		this.hashcode = position.hashcode;
	}

	/**
	 * Creates a new position from the given coordinates.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 */
	public Position(int x, int y) {
		this(x, y, 0);
	}
	
	/**
	 * Gets the position for player
	 * @param player the player
	 */
	public Position(Player player) {
		this(player.getX(), player.getY(), player.getHeight());
	}
	
	/**
	 * Gets the position for npc
	 * @param npc the npc
	 */
	public Position(Npc npc) {
		this(npc.getX(), npc.getY(), npc.getHeight());
	}
	
	/**
	 * Determines if the coordinates given match this positions current coordinates.
	 *
	 * @param x the x position that needs to match.
	 * @param y the y position that needs to match.
	 * @param z the z position that needs to match.
	 * @return {@code true} if the x, y and z matches.
	 */
	public boolean matches(int x, int y, int z) {
		return this.x == x && this.y == y && this.z == z;
	}

	/**
	 * Determines if the coordinates given match this positions current coordinates.
	 *
	 * @return {@code true} if the x, y and z matches.
	 */
	public boolean matches(Position position) {
		return this.x == position.x && this.y == position.y && this.z == position.z;
	}

	/**
	 * Uses the manhattan algorithm or determine the distance between this x and y, and the ones
	 * provided.
	 *
	 * @param x the x coordinate used to compare distance.
	 * @param y the y coordinate used to compare distance.
	 * @return the total manhattan distance from the two points.
	 */
	public int distanceTo(int x, int y) {
		if (this.x == x && this.y == y) {
			return 0;
		}
		return Math.abs(this.x - x) + Math.abs(this.y - y);
	}

	/**
	 * Uses the manhattan algorithm or determine the distance between this x and y, and the ones
	 * provided.
	 *
	 * @param position
	 * 			  the position of the other point.
	 * @return the distance between this position and the one provided.
	 */
	public int distanceTo(Position position) {
		return distanceTo(position.x, position.y);
	}

	/**
	 * Creates a new position from the given x, z, and y position.
	 *
	 * @param x the x offset of this translation.
	 * @param y the y offset of this translation.
	 * @param z the z offset of this translation.
	 * @return a new position object with the given translation.
	 */
	public Position translate(int x, int y, int z) {
		return new Position(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Creates a new position from the given x, z, and y position.
	 *
	 * @param x the x offset of this translation.
	 * @param y the y offset of this translation.
	 * @return a new position object with the given translation.
	 */
	public Position translate(int x, int y) {
		return new Position(this.x + x, this.y + y, this.z);
	}

	/**
	 * Does a simple addition to offset the height of this position.
	 *
	 * @param z the z position to offset.
	 * @return the new position.
	 */
	public Position translateHeight(int z) {
		return translate(0, 0, z);
	}

	/**
	 * Randomly translates the position by the given range of values on the x and y coordinates.
	 *
	 * @param minimumXInclusive the minimum to offset the x position by.
	 * @param maximumXExclusive the maximum to offset the x position by.
	 * @param minimumYInclusive the minimum to offset the y position by.
	 * @param maximumYExclusive the maximum to offset the y position by.
	 * @return creates a new position with a random translation.
	 */
	public Position randomTranslate(int minimumXInclusive, int maximumXExclusive, int minimumYInclusive, int maximumYExclusive) {
		return new Position(x + ThreadLocalRandom.current().nextInt(minimumXInclusive, maximumXExclusive),
		                    y + ThreadLocalRandom.current().nextInt(minimumYInclusive, maximumYExclusive), z);
	}

	/**
	 * Returns a set of Position objects that surround this position using the given radius excluding the
	 * provided position.
	 *
	 * @param radius
	 * 			  the radius or distance from the initial tile.
	 * @return the set of surrounding positions.
	 */
	public Set<Position> surrounding(int radius) {
		return surrounding(radius, false);
	}

	/**
	 * Returns a set of positions that surround this position that are unblocked from the original location and can be accessed.
	 *
	 * @param radius
	 * 			  the radius from this point.
	 * @return the set, or empty, of unblocked positions.
	 */
	public Set<Position> surroundingUnblocked(int radius) {
		return surrounding(radius, true);
	}

	/**
	 * Returns a set of Position objects that surround this position using the given radius excluding the
	 * provided position.
	 *
	 * @param radius
	 * 			  the radius or distance from the initial tile.
	 * @return the set of surrounding positions.
	 */
	private Set<Position> surrounding(int radius, boolean checkForBlocked) {
		Set<Position> surrounding = new HashSet<>();

		for (int x = this.x - radius; x <= this.x + radius; x++) {
			for (int y = this.y - radius; y <= this.y + radius; y++) {
				if (matches(x, y, this.z)) {
					continue;
				}
				if (checkForBlocked && !Region.isStraightPathUnblocked(this.x, this.y, x, y, z, Math.abs(this.x - x), Math.abs(this.y - y), true)) {
					continue;
				}
				surrounding.add(new Position(x, y, this.z));
			}
		}

		return surrounding;
	}


	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Position) {
			Position position = (Position) obj;

			return position.x == x && position.y == y && position.z == z;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("Position: { x=%s, y=%s, z=%s, regionX=%s, regionY=%s, localX=%s, localY=%s}",
				x, y, z, getRegionX(), getRegionY(), getLocalX(), getLocalY());
	}

	/**
	 * Retrieves the x coordinate of this position.
	 *
	 * @return the x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Retrieves the y coordinate of this position.
	 *
	 * @return the y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Retrieves the z, or height, coordinate of this position.
	 *
	 * @return the z coordinate.
	 */
	public int getZ() {
		return z;
	}

	/**
	 * The region x position.
	 *
	 * @return the position in the region for the x-axis.
	 */
	public int getRegionX() {
		return (x >> 3) - 6;
	}

	/**
	 * The region y position.
	 *
	 * @return the position in the region for the y-axis.
	 */
	public int getRegionY() {
		return (y >> 3) - 6;
	}

	/**
	 * The local x for this position.
	 *
	 * @return the local x position.
	 */
	public int getLocalX() {
		return x - 8 * getRegionX();
	}

	/**
	 * The local y for this position.
	 *
	 * @return the local y position.
	 */
	public int getLocalY() {
		return y - 8 * getRegionY();
	}


}
