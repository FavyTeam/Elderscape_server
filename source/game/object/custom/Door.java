package game.object.custom;

import core.Server;
import core.ServerConstants;
import game.object.clip.DynamicClipping;
import game.object.clip.Region;
import game.player.Player;
import utility.FileUtility;

import java.util.ArrayList;

/**
 * Global doors system.
 *
 * @author MGT Madness, created on 09-12-2016.
 */
public class Door {

	public static ArrayList<String> doorsList = new ArrayList<String>();

	/**
	 * Contains doors that have been modified, only used for object verification.
	 */
	public static ArrayList<String> doorsListObjectVerification = new ArrayList<String>();

	/**
	 * Load the door data on start-up.
	 */
	public static void loadDoorData() {
		doorsList = FileUtility.readFile(ServerConstants.getOsrsGlobalDataLocation() + "world osrs/object/door_data.txt");
	}

	public static boolean isDoor(Player player, int objectId) {
		if (objectId == 9565 || objectId == 9563) {
			player.getPA().sendMessage("You need a thieving level of 100 to break out.");
			return true;
		}
		for (int index = 0; index < doorIdList.length; index++) {
			if (doorIdList[index] == objectId) {
				doorActionStage1(player, objectId);
				return true;
			}
		}
		return false;
	}

	private static void doorActionStage1(Player player, int originalObjectId) {
		for (int index = 0; index < doorsList.size(); index++) {
			String[] parse = doorsList.get(index).split(" ");
			int objectId = Integer.parseInt(parse[0]);
			int x = Integer.parseInt(parse[1]);
			int y = Integer.parseInt(parse[2]);
			int height = Integer.parseInt(parse[3]);

			if (objectId == originalObjectId) {
				if (x == player.getObjectX() && y == player.getObjectY() && height == player.getHeight()) {
					int face = Integer.parseInt(parse[4]);
					int type = Integer.parseInt(parse[5]);
					String doorStatus = parse[6];
					String objectStatus = parse[7];
					doorActionStage2(player, objectId, x, y, height, face, type, doorStatus, index, objectStatus);
					return;
				}
			}
		}
	}

	/**
	 * Doors that do not change objectId when used.
	 */
	private final static int[] nonChangingDoors =
			{
					// Large door at Castlewars
					1517,
					// Door at Warriors guild entrance,
					24318,
					// Door at wilderness castle
					1474,
					// Door at Slayer tower first floor
					2100,
					// Door at Wilderness castle.
					14749,
					// Door at black knights fortress, west of Edgeville.
					2337,
					// Door at Entrana to Ellis
					1540,
					25819,

			};

	private static void doorActionStage2(Player player, int objectId, int x, int y, int height, int face, int type, String status, int index, String objectStatus) {
		// Face 9 means diagonal door, if i spawn a door at 9, it puts it diagonally automatically.

		int newObjectId = objectId;
		int newFace = face;
		int newX = x;
		int newY = y;
		String newStatus = "";
		String newObjectStatus = "ORIGINAL";
		if (objectStatus.equals("ORIGINAL")) {
			newObjectStatus = "MODIFIED";
		}
		// UniqueId is the id of the new door that is being spawned, so when i click the door again, it will remove the clipping etc using this uniqueId
		String uniqueId = ""; // Must be 0, this has to be updated when the dynamicTileClipping is added.
		if (status.equals("Open")) {
			newStatus = "Close";
			if (objectId == 1535 || objectId == 24050) {
				newObjectId++;
			} else {
				boolean match = false;
				for (int index1 = 0; index1 < nonChangingDoors.length; index1++) {
					if (objectId == nonChangingDoors[index1]) {
						match = true;
						break;
					}
				}
				if (!match) {
					newObjectId--;
				}
			}
			if (face == 0) {
				newX--;
				newFace = 1;
				if (objectStatus.equals("ORIGINAL")) {
					uniqueId = newObjectId + "" + newX + "" + newY + "" + height;
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.WEST, true, uniqueId));
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x - 1, y, height, Region.EAST, true, uniqueId));
				} else {
					removePreviouslyAddedDoorClipping(objectId, x, y, height);
				}

			} else if (face == 3) {
				newY--;
				newFace = 0;
				if (objectStatus.equals("MODIFIED")) {
					removePreviouslyAddedDoorClipping(objectId, x, y, height);
				} else {
					uniqueId = newObjectId + "" + newX + "" + newY + "" + height;
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y - 1, height, Region.NORTH, true, uniqueId));
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.SOUTH, true, uniqueId));
				}
			} else if (face == 2) {
				newX++;
				newFace = 3;
				if (objectStatus.equals("MODIFIED")) {
					removePreviouslyAddedDoorClipping(objectId, x, y, height);
				} else {
					uniqueId = newObjectId + "" + newX + "" + newY + "" + height;
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.EAST, true, uniqueId));
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x + 1, y, height, Region.WEST, true, uniqueId));
				}
			} else if (face == 1) {
				newY++;
				newFace = 2;
				if (objectStatus.equals("ORIGINAL")) {
					uniqueId = newObjectId + "" + newX + "" + newY + "" + height;
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.NORTH, true, uniqueId));
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y + 1, height, Region.SOUTH, true, uniqueId));
				} else {
					removePreviouslyAddedDoorClipping(objectId, x, y, height);
				}
			}
		} else if (status.equals("Close")) {
			newStatus = "Open";

			// Door located at Edgeville general store and door at Falador.
			if (objectId == 1536 || objectId == 24051) {
				newObjectId--;

			} else {
				boolean match = false;
				for (int index1 = 0; index1 < nonChangingDoors.length; index1++) {
					if (objectId == nonChangingDoors[index1]) {
						match = true;
						break;
					}
				}
				if (!match) {
					newObjectId++;
				}
			}
			if (face == 1) {
				newX++;
				newFace = 0;

				if (objectStatus.equals("MODIFIED")) {
					removePreviouslyAddedDoorClipping(objectId, x, y, height);
				} else {
					uniqueId = newObjectId + "" + newX + "" + newY + "" + height;
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.EAST, false, uniqueId));
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x + 1, y, height, Region.WEST, false, uniqueId));
				}
			} else if (face == 0) {
				newY++;
				newFace = 3;
				if (objectStatus.equals("ORIGINAL")) {
					uniqueId = newObjectId + "" + newX + "" + newY + "" + height;
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.NORTH, false, uniqueId));
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y + 1, height, Region.SOUTH, false, uniqueId));
				} else {
					removePreviouslyAddedDoorClipping(objectId, x, y, height);
				}
			} else if (face == 3) {
				newX--;
				newFace = 2;
				if (objectStatus.equals("ORIGINAL")) {
					uniqueId = newObjectId + "" + newX + "" + newY + "" + height;
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.WEST, false, uniqueId));
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x - 1, y, height, Region.EAST, false, uniqueId));
				} else {
					removePreviouslyAddedDoorClipping(objectId, x, y, height);
				}
			} else if (face == 2) {
				newY--;
				newFace = 1;
				if (objectStatus.equals("MODIFIED")) {
					removePreviouslyAddedDoorClipping(objectId, x, y, height);
				} else {
					uniqueId = newObjectId + "" + newX + "" + newY + "" + height;
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y - 1, height, Region.NORTH, false, uniqueId));
					DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.SOUTH, false, uniqueId));
				}
			}
		}

		// If door modified, remove the modified door data and update visually.
		// if door original, add new door data


		// Add object to objects list and spawn visually.
		if (objectStatus.equals("ORIGINAL")) {
			// Delete door.
			new Object(-1, x, y, player.getHeight(), face, 0, -1, -1, uniqueId);

			// Spawn new door.
			new Object(newObjectId, newX, newY, player.getHeight(), newFace, 0, -1, -1, uniqueId);
		}

		// Remove the ORIGINAL objects from objects list and update visually.
		else {
			Server.objectManager.removeGlobalObject(objectId + "" + x + "" + y + "" + height);
			// Delete door.
			Server.objectManager.spawnGlobalObject(-1, x, y, height, face, 0);

			// Spawn new door.
			Server.objectManager.spawnGlobalObject(newObjectId, newX, newY, height, newFace, 0);
		}
		String old = doorsList.get(index);
		doorsList.remove(index);
		String newObjectData = newObjectId + " " + newX + " " + newY + " " + height + " " + newFace + " 0 " + newStatus + " " + newObjectStatus;
		doorsList.add(0, newObjectData);

		for (int i = 0; i < doorsListObjectVerification.size(); i++) {
			if (old.equals(doorsListObjectVerification.get(i))) {
				doorsListObjectVerification.remove(i);
			}
		}
		if (objectStatus.equals("ORIGINAL")) {
			doorsListObjectVerification.add(0, newObjectData);
		}
	}

	private static void removePreviouslyAddedDoorClipping(int objectId, int x, int y, int height) {
		for (int i = 0; i < DynamicClipping.dynamicClipping.size(); i++) {
			String currentId = objectId + "" + x + "" + y + "" + height;
			if (DynamicClipping.dynamicClipping.get(i).uid.equals(currentId)) {
				DynamicClipping.dynamicClipping.remove(i);
				i--; // Index is minused because when i remove an index, all the orders
				// shift upwards, so i also have to shift the index up by 1.
			}
		}

	}

	/**
	 * @return True if it is an existing modified door.
	 */
	public static boolean isModifiedDoor(int objectId, int x, int y, int height) {
		for (int i = 0; i < doorsListObjectVerification.size(); i++) {

			String[] parse = doorsListObjectVerification.get(i).split(" ");
			int id = Integer.parseInt(parse[0]);
			int x1 = Integer.parseInt(parse[1]);
			int y1 = Integer.parseInt(parse[2]);
			int height1 = Integer.parseInt(parse[3]);

			if (id == objectId && x1 == x && y1 == y && height1 == height) {
				return true;
			}
		}
		return false;
	}

	public final static int[] doorIdList =
			{
					1560,
					1540,
					1535,
					1561,
					25417,
					1517,
					1518,
					25418,
					25419,
					1522,
					24318,
					1537,
					1538,
					10261,
					10262,
					10263,
					10264,
					10265,
					17091,
					17092,
					17093,
					17094,
					1536,
					1551,
					1552,
					6108,
					6109,
					2337,
					25642,
					25643,
					25827,
					25828,
					25718,
					25719,
					779,
					780,
					25640,
					25641,
					5888,
					5889,
					5890,
					24686,
					24687,
					24051,
					24052,
					24055,
					24056,
					24058,
					24060,
					24062,
					24063,
					24065,
					12446,
					12447,
					12448,
					12449,
					9564,
					9565,
					9566,
					1512,
					14749,
					1513,
					1516,
					2623,
					11773,
					11774,
					11772,
					11775,
					2100,
					11776,
					11783,
					11782,
					11784,
					15512,
					15513,
					11777,
					11778,
					15514,
					15515,
					15516,
					15517,
					1514,
					2103,
					2105,
					24363,
					24364,
					24057,
					24368,
					24050,
					24369,
					24370,
					25819,
					21600,
			};
}
