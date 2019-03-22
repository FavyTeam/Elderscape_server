package game.object.custom;

import core.Server;
import game.player.Player;

public class Object {

	public int objectId;

	public int objectX;

	public int objectY;

	public int height;

	public int face;

	public int type;

	public int newId;

	public int tick;

	// Only used for doors.
	public String doorUniqueId = "";

	private boolean transformationUpdateRequired;

	/**
	 * @param id Object identity to spawn.
	 * @param x
	 * @param y
	 * @param height
	 * @param face
	 * @param type
	 * @param newId Object of identity to spawn after ticks are finished.
	 * @param ticks Amount of game ticks the object will last for.
	 */
	public Object(int id, int x, int y, int height, int face, int type, int newId, int ticks) {
		this.objectId = id;
		this.objectX = x;
		this.objectY = y;
		this.height = height;
		this.face = face;
		this.type = type;
		this.newId = newId;
		this.tick = ticks;
		Server.objectManager.addObject(this);
	}

	public Object(int id, int x, int y, int height, int face, int type, int newId, int ticks, String uniqueId) {
		this.objectId = id;
		this.objectX = x;
		this.objectY = y;
		this.height = height;
		this.face = face;
		this.type = type;
		this.newId = newId;
		this.tick = ticks;
		this.doorUniqueId = uniqueId;
		Server.objectManager.addObject(this);
	}

	public Object(Player player, int id, int x, int y, int height, int face, int type, int newId, int ticks) {
		this.objectId = id;
		this.objectX = x;
		this.objectY = y;
		this.height = height;
		this.face = face;
		this.type = type;
		this.newId = newId;
		this.tick = ticks;
		Server.objectManager.addObject1(player, this);
	}

	public Object(int id, int x, int y, int height, int face, int type, int newId, int ticks, boolean add) {
		this.objectId = id;
		this.objectX = x;
		this.objectY = y;
		this.height = height;
		this.face = face;
		this.type = type;
		this.newId = newId;
		this.tick = ticks;
		if (add) {
			Server.objectManager.addObject(this);
		}
	}

	/**
	 * Referenced when the object is called by the manager. It should be noted that {@link ObjectManagerServer#objects} not
	 * be modified from inside this function as it would result in a {@link java.util.ConcurrentModificationException}.
	 */
	public void onTick() {

	}

	/**
	 * Referenced when the object is removed from the manager.
	 */
	public void onRemove() {

	}

	public void transform(int objectId) {
		this.objectId = objectId;
		this.transformationUpdateRequired = true;
	}

	public boolean isTransformationUpdateRequired() {
		return transformationUpdateRequired;
	}

	public void clearTransformationUpdateRequired() {
		transformationUpdateRequired = false;
	}
}
