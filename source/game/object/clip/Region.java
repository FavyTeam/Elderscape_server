package game.object.clip;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.position.Position;
import game.content.skilling.Farming;
import game.content.skilling.Firemaking;
import game.npc.Npc;
import game.object.custom.CustomClippedTiles;
import game.object.custom.Door;
import game.object.custom.Object;
import game.player.Player;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import utility.Misc;

public class Region {

	public static boolean MULTI_THREAD_ENABLED = false;

	private static final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private static boolean loadingCompleted;

	public final static int NORTH = 0;

	public final static int NORTH_WEST = 14;

	public final static int NORTH_EAST = 2;

	public final static int WEST = 12;

	public final static int EAST = 4;

	public final static int SOUTH = 8;

	public final static int SOUTH_WEST = 10;

	public final static int SOUTH_EAST = 6;

	public List<Object> verifiedObjects = new CopyOnWriteArrayList<>();

	public static ArrayList<String> removedObjectCoordinates = new ArrayList<String>();

	public static Region[] regionIdTable;

	private Set<Npc> npcs = new HashSet<>();

	private Set<Player> players = new HashSet<>();

	public int getX() {
		return (id >> 8) * 64;
	}

	public int getY() {
		return (id & 0xff) * 64;
	}

	public static void buildTable(int count) {
		regionIdTable = new Region[count];
		for (Region r : regions) {
			regionIdTable[r.id()] = r;
		}
	}

	@Deprecated // This is faulty, do not use until further notice.
	public static boolean inDistance(ObjectDefinitionServer definition, Object object, Player player) {
		return inDistance(definition, object.face, object.objectX, object.objectY, player.getX(), player.getY());
	}

	@Deprecated // This is faulty, do not use until further notice.
	public static boolean inDistance(ObjectDefinitionServer definition, int orientation, int x, int y, int playerX, int playerY) {
		int type = definition.type;
		int width;
		int height;
		if (type == 10 || type == 11 || type == 22) {
			if (orientation == 0 || orientation == 2) {
				width = definition.objectSizeX;
				height = definition.objectSizeY;
			} else {
				width = definition.objectSizeY;
				height = definition.objectSizeX;
			}
			int rotation = definition.rotationFlags;

			if (orientation != 0) {
				rotation = (rotation << orientation & 0xf) + (rotation >> 4 - orientation);
			}
			return atObject(y, x, playerX, height, rotation, width, playerY);
		}
		return atObject(y, x, playerX, definition.objectSizeY, 0, definition.objectSizeX, playerY);
	}

	@Deprecated // This is faulty, do not use until further notice.
	private static boolean atObject(int finalY, int finalX, int x, int height, int rotation, int width, int y) {
		int maxX = (finalX + width) - 1;

		int maxY = (finalY + height) - 1;

		if (x >= finalX && x <= maxX && y >= finalY && y <= maxY
				|| x == finalX - 1 && y >= finalY && y <= maxY && (getClipping(x, y, height) & 8) == 0 && (rotation & 8) == 0
				|| x == maxX + 1 && y >= finalY && y <= maxY && (getClipping(x, y, height) & 0x80) == 0 && (rotation & 2) == 0) {
			return true;
		}
		return y == finalY - 1 && x >= finalX && x <= maxX && (getClipping(x, y, height) & 2) == 0
				&& (rotation & 4) == 0 || y == maxY + 1 && x >= finalX && x <= maxX && (getClipping(x, y, height) & 0x20) == 0
				&& (rotation & 1) == 0;
	}

	public static List<Region> getSurrounding(Region region) {
		return getSurrounding(region.getX(), region.getY());
	}

	private final static int GET_SURROUNDING_OFFSET = 16;
	public static List<Region> getSurrounding(int originX, int originY) {
		List<Region> surrounding = new ArrayList<>();

		for (int x = originX - GET_SURROUNDING_OFFSET; x <= originX + GET_SURROUNDING_OFFSET; x += GET_SURROUNDING_OFFSET) {
			for (int y = originY - GET_SURROUNDING_OFFSET; y <= originY + GET_SURROUNDING_OFFSET; y += GET_SURROUNDING_OFFSET) {
				Region region = getRegion(x, y);

				if (region == null) {
					continue;
				}
				surrounding.add(region);
			}
		}

		return surrounding;
	}

	public static Region getRegion(int x, int y) {
		x = (x / 64) * 64;
		y = (y / 64) * 64;

		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = (regionX / 8 << 8) + regionY / 8;

		if (regionId < 0) {
			return null;
		}
		if (regionId > regionIdTable.length - 1) {
			return null;
		}
		if (regionIdTable[regionId] != null) {
			return regionIdTable[regionId];
		}
		return null;
	}

	public static Object getObjectForRegion(int x, int y, int height) {
		Region region = getRegion(x, y);

		if (region == null) {
			return null;
		}

		int realHeight = height;

		// Instanced heights adjustment.
		if (height >= 4) {
			realHeight = (height / 4);
			realHeight = height - (realHeight * 4);
		}
		return region.getObject(x, y, realHeight);
	}

	public Object getObject(int x, int y, int height) {
		return verifiedObjects.stream().filter(o -> o.objectX == x && o.objectY == y && o.height == height).findAny().orElse(null);
	}

	public boolean objectExists(int x, int y, int height) {
		return verifiedObjects.stream().anyMatch(object -> object != null && object.objectX == x && object.objectY == y && object.height == height);
	}

	public static boolean objectExists(Player player, int objectId, int x, int y, int height) {
		Region region;

		if (Server.objectManager.isServerGeneratedObject(x, y, player.getHeight()) != null) {
			return true;
		}
		if (Region.getObjectForRegion(x, y, player.getHeight()) != null) {
			return true;
		}

		if (GameType.isOsrs()) {
			// Ice chunk (vorkath object)
			if (objectId == 31990 && x == 2272 && y == 4053) {
				return true;
			}
			if (objectId == 451) // Empty ore id.
			{
				return true;
			}

			// Clan wars portal.
			if (objectId == 26646 && x == 3326 && y == 4749 && height == 0) {
				return true;
			}

			// Herb patch at Entrana.
			if ((objectId == Farming.PATCH_HERBS || objectId == Farming.patchWeedObject) && x >= 2809 && x <= 2813 && y >= 3334 && y <= 3338) {
				return true;
			}
			// Jad cave exit.
			if (objectId == 11834) {
				if (x != 2412 || y != 5118) {
					return false;
				}
				return true;
			}
			if (height == 20) {
				// Box of health at tournament.
				if (objectId == 23709 && x == 3328 && y == 4753) {
					return true;
				}
			}
			// Fire made from logs.
			if (objectId == Firemaking.FIRE_OBJECT_ID) {
				if (Firemaking.fireExists(x, y, height)) {
					return true;
				}
			}

			if (Door.isModifiedDoor(objectId, x, y, height)) {
				return true;
			}

			// Region instance related below.
			region = getRegion(x, y);
			if (region != null) {
				if (region.id == 14231) {
					// Sarcophagus
					if (objectId == 20772 && x == 3573 && y == 9705) {
						return true;
					}

					// Staircase, at barrows.
					if (objectId == 20672 && x == 3578 && y == 9703) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void readRemovedObjectCoordinates() {
		try {
			BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getOsrsGlobalDataLocation() + "world osrs/object/removed_objects.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty() && !line.contains("//")) {
					removedObjectCoordinates.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param startX For example playerX
	 * @param startY For example playerY
	 * @param endX For example npcX
	 * @param endY For example npcY
	 * @param height For example playerHeight
	 * @param xLength Size of Npc
	 * @param yLength Size of Npc
	 * @param finalEntry TODO
	 * @return True if the player can walk directly to the npc.
	 */
	public static boolean isStraightPathUnblocked(int startX, int startY, int endX, int endY, int height, int xLength, int yLength, boolean finalEntry) {
		int diffX = endX - startX;
		int diffY = endY - startY;
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int ii = 0; ii < max; ii++) {
			int currentX = endX - diffX;
			int currentY = endY - diffY;

			for (int i = 0; i < xLength; i++) {
				for (int i2 = 0; i2 < yLength; i2++) {
					if (diffX < 0 && diffY < 0) {
						if (blockedSouthWest((currentX + i), (currentY + i2), height) || blockedWest((currentX + i), currentY + i2, height) || blockedSouth(currentX + i,
						                                                                                                                                    (currentY + i2),
						                                                                                                                                    height)) {
							return false;
						}
					} else if (diffX > 0 && diffY > 0) {
						if (blockedNorthEast(currentX + i, currentY + i2, height) || blockedEast(currentX + i, currentY + i2, height) || blockedNorth(currentX + i, currentY + i2,
						                                                                                                                              height)) {
							return false;
						}
					} else if (diffX < 0 && diffY > 0) {
						if (blockedNorthWest((currentX + i), currentY + i2, height) || blockedWest((currentX + i), currentY + i2, height) || blockedNorth(currentX + i,
						                                                                                                                                  currentY + i2, height)) {
							return false;
						}
					} else if (diffX > 0 && diffY < 0) {
						if (blockedSouthEast(currentX + i, (currentY + i2), height) || blockedEast(currentX + i, currentY + i2, height) || blockedSouth(currentX + i,
						                                                                                                                                (currentY + i2), height)) {
							return false;
						}
					} else if (diffX > 0 && diffY == 0) {
						if (blockedEast(currentX + i, currentY + i2, height)) {
							return false;
						}
					} else if (diffX < 0 && diffY == 0) {
						if (blockedWest((currentX + i), currentY + i2, height)) {
							return false;
						}
					} else if (diffX == 0 && diffY > 0) {
						if (blockedNorth(currentX + i, currentY + i2, height)) {
							return false;
						}
					} else if (diffX == 0 && diffY < 0 && blockedSouth(currentX + i, (currentY + i2), height)) {
						return false;
					}
				}

			}

			if (diffX < 0)
				diffX++;
			else if (diffX > 0)
				diffX--;
			if (diffY < 0)
				diffY++;
			else if (diffY > 0)
				diffY--;
		}
		if (!finalEntry) {
			return isStraightPathUnblocked(endX, endY, startX, startY, height, xLength, yLength, true);
		}
		return true;
	}

	/**
	 * This has specific exceptions to certain object coordinates that can be shot through with an arrow etc. Like tree stumps, fences etc.
	 *
	 * @param startX For example playerX
	 * @param startY For example playerY
	 * @param endX For example npcX
	 * @param endY For example npcY
	 * @param height For example playerHeight
	 * @param xLength Size of Npc
	 * @param yLength Size of Npc
	 * @return True if the player can walk directly to the npc.
	 */
	public static boolean isStraightPathUnblockedProjectiles(int startX, int startY, int endX, int endY, int height, int xLength, int yLength, boolean mainScan) {
		if (mainScan) {
			return isStraightPathUnblockedProjectiles(endX, endY, startX, startY, height, xLength, yLength, false);
		}
		int diffX = endX - startX;
		int diffY = endY - startY;
		int distance = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int index = 0; index < distance + 1; index++) {
			int currentX = endX - diffX;
			int currentY = endY - diffY;
			for (int xIndex = 0; xIndex < xLength; xIndex++) {
				for (int yIndex = 0; yIndex < yLength; yIndex++) {
					int x = currentX + xIndex;
					int y = currentY + yIndex;

					if (diffX < 0 && diffY < 0) {
						if (!Region.projectileCanPassThrough(x - 1, y - 1, height)) {
							if (blockedSouthWest(x, y, height)) {
								return false;
							}
						}

						if (!Region.projectileCanPassThrough(x - 1, y, height)) {
							if (blockedWest(x, y, height)) {
								return false;
							}
						}

						if (!Region.projectileCanPassThrough(x, y - 1, height)) {
							if (blockedSouth(x, y, height)) {
								return false;
							}
						}
					} else if (diffX > 0 && diffY > 0) {
						if (!Region.projectileCanPassThrough(x + 1, y + 1, height)) {
							if (blockedNorthEast(x, y, height)) {
								return false;
							}
						}


						if (!Region.projectileCanPassThrough(x + 1, y, height)) {
							if (blockedEast(x, y, height)) {
								return false;
							}
						}

						if (!Region.projectileCanPassThrough(x, y + 1, height)) {
							if (blockedNorth(x, y, height)) {
								return false;
							}
						}
					} else if (diffX < 0 && diffY > 0) {
						if (!Region.projectileCanPassThrough(x - 1, y + 1, height)) {
							if (blockedNorthWest(x, y, height)) {
								return false;
							}
						}

						if (!Region.projectileCanPassThrough(x, y + 1, height)) {
							if (blockedNorth(x, y, height)) {
								return false;
							}
						}

						if (!Region.projectileCanPassThrough(x - 1, y, height)) {
							if (blockedWest(x, y, height)) {
								return false;
							}
						}
					} else if (diffX > 0 && diffY < 0) {
						if (!Region.projectileCanPassThrough(x + 1, y - 1, height)) {
							if (blockedSouthEast(x, y, height)) {
								return false;
							}
						}


						if (!Region.projectileCanPassThrough(x + 1, y, height)) {
							if (blockedEast(x, y, height)) {
								return false;
							}
						}


						if (!Region.projectileCanPassThrough(x, y - 1, height)) {
							if (blockedSouth(x, y, height)) {
								return false;
							}
						}
					} else if (diffX > 0 && diffY == 0) {
						if (!Region.projectileCanPassThrough(x + 1, y, height)) {
							if (blockedEast(x, y, height)) {
								return false;
							}
						}
					} else if (diffX < 0 && diffY == 0) {
						if (!Region.projectileCanPassThrough(x - 1, y, height)) {
							if (blockedWest(x, y, height)) {
								return false;
							}
						}
					} else if (diffX == 0 && diffY > 0) {
						if (!Region.projectileCanPassThrough(x, y + 1, height)) {
							if (blockedNorth(x, y, height)) {
								return false;
							}
						}
					} else if (diffX == 0 && diffY < 0) {

						if (!Region.projectileCanPassThrough(x, y - 1, height)) {
							if (blockedSouth(x, y, height)) {
								return false;
							}
						}
					}


				}


			}

			if (diffX < 0)
				diffX++;
			else if (diffX > 0)
				diffX--;
			if (diffY < 0)
				diffY++;
			else if (diffY > 0)
				diffY--;
		}

		return true;
	}

	public void addNpcIfAbsent(Npc npc) {
		if (!npcs.contains(npc)) {
			npcs.add(npc);
		}
	}

	public void addPlayerIfAbsent(Player player) {
		if (!players.contains(player)) {
			players.add(player);
		}
	}

	public void removeNpcIfPresent(Npc npc) {
		npcs.remove(npc);
	}

	public void removePlayerIfPresent(Player player) {
		players.remove(player);
	}

	public boolean contains(Npc npc) {
		return npcs.contains(npc);
	}

	public boolean contains(Player player) {
		return players.contains(player);
	}

	public boolean anyMatch(Predicate<Npc> predicate) {
		return npcs.stream().anyMatch(predicate);
	}

	public boolean anyPlayersMatch(Predicate<Player> predicate) {
		return players.stream().anyMatch(predicate);
	}

	public boolean npcsIsEmpty() {
		return npcs.isEmpty();
	}

	public boolean playersIsEmpty() {
		return players.isEmpty();
	}

	public void forEachPlayer(Consumer<Player> player) {
		for (Player player1 : players) {
			player.accept(player1);
		}
	}

	public void forEachNpc(Consumer<Npc> npc) {
		for (Npc npc1 : npcs) {
			npc.accept(npc1);
		}
	}

	public int amountOfPlayers() {
		return players.size();
	}

	public int amountOfNpcs() {
		return npcs.size();
	}

	private void removeClip(int x, int y, int height, int shift) {
		height++; // Added because height -1 is barrows.
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		if (clips[height] == null) {
			clips[height] = new int[64][64];
		}
		clips[height][x - regionAbsX][y - regionAbsY] = 0;
	}

	private void addClip(int x, int y, int height, int shift, int type, int direction, String debug) {
		height++; // Added because height -1 is barrows.
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		if (clips[height] == null) {
			clips[height] = new int[64][64];
		}

		clips[height][x - regionAbsX][y - regionAbsY] |= shift;
	}

	private int getClip(int x, int y, int height) {
		height++; // Added because height -1 is barrows.
		if (height > 4) {
			height = 1;
		}
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		if (clips[height] == null) {
			return 0;
		}
		return clips[height][x - regionAbsX][y - regionAbsY];
	}

	public static void addClipping(int x, int y, int height, int shift, String debug, int type, int direction) {
		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = ((regionX / 8) << 8) + (regionY / 8);
		if (regionId < 0) {
			return;
		}
		if (regionIdTable[regionId] != null) {
			regionIdTable[regionId].addClip(x, y, height, shift, type, direction, debug);
		}
	}

	public static void removeClipping(int x, int y, int height) {
		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = ((regionX / 8) << 8) + (regionY / 8);
		if (regionId < 0) {
			return;
		}
		regionIdTable[regionId].removeClip(x, y, height, 0);
	}

	private static Region[] regions;

	private int id;

	private int[][][] clips = new int[5][][];

	public Region(int id, boolean members) {
		this.id = id;
	}

	public int id() {
		return id;
	}

	public static void addClippingForVariableObject(int x, int y, int height, int type, int direction, boolean flag) {
		if (type == 0) {
			if (direction == 0) {
				addClipping(x, y, height, 128, "Here2", type, direction);
				addClipping(x - 1, y, height, 8, "Here3", type, direction);
			} else if (direction == 1) {
				addClipping(x, y, height, 2, "Here4", type, direction);
				addClipping(x, y + 1, height, 32, "Here5", type, direction);
			} else if (direction == 2) {
				addClipping(x, y, height, 8, "Here6", type, direction);
				addClipping(x + 1, y, height, 128, "Here7", type, direction);
			} else if (direction == 3) {
				addClipping(x, y, height, 32, "Here8", type, direction);
				addClipping(x, y - 1, height, 2, "Here9", type, direction);
			}
		} else if (type == 1 || type == 3) {
			if (direction == 0) {
				addClipping(x, y, height, 1, "Here10", type, direction);
				addClipping(x - 1, y, height, 16, "Here11", type, direction);
			} else if (direction == 1) {
				addClipping(x, y, height, 4, "Here12", type, direction);
				addClipping(x + 1, y + 1, height, 64, "Here13", type, direction);
			} else if (direction == 2) {
				addClipping(x, y, height, 16, "Here14", type, direction);
				addClipping(x + 1, y - 1, height, 1, "Here15", type, direction);
			} else if (direction == 3) {
				addClipping(x, y, height, 64, "Here16", type, direction);
				addClipping(x - 1, y - 1, height, 4, "Here17", type, direction);
			}
		} else if (type == 2) {
			if (direction == 0) {
				addClipping(x, y, height, 130, "Here18", type, direction);
				addClipping(x - 1, y, height, 8, "Here19", type, direction);
				addClipping(x, y + 1, height, 32, "Here20", type, direction);
			} else if (direction == 1) {
				addClipping(x, y, height, 10, "Here21", type, direction);
				addClipping(x, y + 1, height, 32, "Here22", type, direction);
				addClipping(x + 1, y, height, 128, "Here23", type, direction);
			} else if (direction == 2) {
				addClipping(x, y, height, 40, "Here24", type, direction);
				addClipping(x + 1, y, height, 128, "Here25", type, direction);
				addClipping(x, y - 1, height, 2, "Here26", type, direction);
			} else if (direction == 3) {
				addClipping(x, y, height, 160, "Here27", type, direction);
				addClipping(x, y - 1, height, 2, "Here28", type, direction);
				addClipping(x - 1, y, height, 8, "Here29", type, direction);
			}
		}
		if (flag) {
			if (type == 0) {
				if (direction == 0) {
					addClipping(x, y, height, 65536, "Here30", type, direction);
					addClipping(x - 1, y, height, 4096, "Here31", type, direction);
				} else if (direction == 1) {
					addClipping(x, y, height, 1024, "Here32", type, direction);
					addClipping(x, y + 1, height, 16384, "Here33", type, direction);
				} else if (direction == 2) {
					addClipping(x, y, height, 4096, "Here34", type, direction);
					addClipping(x + 1, y, height, 65536, "Here35", type, direction);
				} else if (direction == 3) {
					addClipping(x, y, height, 16384, "Here36", type, direction);
					addClipping(x, y - 1, height, 1024, "Here37", type, direction);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					addClipping(x, y, height, 512, "Here38", type, direction);
					addClipping(x - 1, y + 1, height, 8192, "Here39", type, direction);
				} else if (direction == 1) {
					addClipping(x, y, height, 2048, "Here40", type, direction);
					addClipping(x + 1, y + 1, height, 32768, "Here41", type, direction);
				} else if (direction == 2) {
					addClipping(x, y, height, 8192, "Here42", type, direction);
					addClipping(x + 1, y + 1, height, 512, "Here43", type, direction);
				} else if (direction == 3) {
					addClipping(x, y, height, 32768, "Here44", type, direction);
					addClipping(x - 1, y - 1, height, 2048, "Here45", type, direction);
				}
			} else if (type == 2) {
				if (direction == 0) {
					addClipping(x, y, height, 66560, "Here46", type, direction);
					addClipping(x - 1, y, height, 4096, "Here47", type, direction);
					addClipping(x, y + 1, height, 16384, "Here48", type, direction);
				} else if (direction == 1) {
					addClipping(x, y, height, 5120, "Here49", type, direction);
					addClipping(x, y + 1, height, 16384, "Here50", type, direction);
					addClipping(x + 1, y, height, 65536, "Here51", type, direction);
				} else if (direction == 2) {
					addClipping(x, y, height, 20480, "Here52", type, direction);
					addClipping(x + 1, y, height, 65536, "Here53", type, direction);
					addClipping(x, y - 1, height, 1024, "Here54", type, direction);
				} else if (direction == 3) {
					addClipping(x, y, height, 81920, "Here55", type, direction);
					addClipping(x, y - 1, height, 1024, "Here56", type, direction);
					addClipping(x - 1, y, height, 4096, "Here57", type, direction);
				}
			}
		}
	}

	private static void addClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean flag, int type, int direction) {
		int clipping = 256;
		for (int i = x; i < x + xLength; i++) {
			for (int i2 = y; i2 < y + yLength; i2++) {
				addClipping(i, i2, height, clipping, "Here1", type, direction);
			}
		}
	}

	public static void addObject(int objectId, int x, int y, int height, int type, int direction, boolean ignoreGate) {
		ObjectDefinitionServer definition = ObjectDefinitionServer.getObjectDef(objectId);
		if (definition == null) {
			return;
		}
		int xLength;
		int yLength;
		if (direction != 1 && direction != 3) {
			xLength = definition.xLength();
			yLength = definition.yLength();
		} else {
			xLength = definition.yLength();
			yLength = definition.xLength();
		}
		if (objectId == -1) {
			return;
		}

		// Objects that projectiles can pass through.
		if (!definition.blocksProjectiles) {
			int clipping = 0x20000;
			for (int i = x; i < x + xLength; i++) {
				for (int i2 = y; i2 < y + yLength; i2++) {
					addClipping(i, i2, height, clipping, "Special1", type, direction);
				}
			}
		}
		if (type == 22) {
			if (definition.hasActions() && definition.blocksWalk()) {
				addClipping(x, y, height, 0x200000, "Here58", type, direction);
			}
		} else if (type >= 9) {
			if (definition.blocksWalk()) {
				addClippingForSolidObject(x, y, height, xLength, yLength, definition.solid(), type, direction);
			}
		} else if (type >= 0 && type <= 3) {
			if (definition.blocksWalk()) {
				addClippingForVariableObject(x, y, height, type, direction, definition.solid());
			}
		}
		if (definition.hasActions) {
			Region r = getRegion(x, y);
			if (r != null) {
				r.verifiedObjects.add(new Object(objectId, x, y, height, direction, type, 0, 0, false));
			}
		}
	}

	public static int getClipping(int x, int y, int height) {
		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = ((regionX / 8) << 8) + (regionY / 8);
		if (regionId < 0) {
			return 0;
		}
		if (regionIdTable[regionId] != null) {
			return regionIdTable[regionId].getClip(x, y, height);
		}
		return 0;
	}

	public static boolean pathUnblocked(int x, int y, int height, String moveTo) {
		switch (moveTo) {
			case "WEST":
				return !blockedWest(x, y, height);
			case "EAST":
				return !blockedEast(x, y, height);
			case "SOUTH":
				return !blockedSouth(x, y, height);
			case "NORTH":
				return !blockedNorth(x, y, height);
			case "SOUTH WEST":
				return !blockedSouthWest(x, y, height);
			case "SOUTH EAST":
				return !blockedSouthEast(x, y, height);
			case "NORTH WEST":
				return !blockedNorthWest(x, y, height);
			case "NORTH EAST":
				return !blockedNorthEast(x, y, height);
		}
		return false;
	}

	public static void load() {
		Collection<Future<?>> futures = new ArrayList<>();

		try {
			long start = System.nanoTime();

			loadingCompleted = false;
			readRemovedObjectCoordinates();
			File f = new File(ServerConstants.getOsrsGlobalDataLocation() + "world osrs/map_index");
			byte[] buffer = new byte[(int) f.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(f));
			dis.readFully(buffer);
			dis.close();
			ByteStream in = new ByteStream(buffer);
			int size = in.length() / 6;
			in.readUnsignedWord();
			regions = new Region[size];
			int[] regionIds = new int[size];
			int[] mapGroundFileIds = new int[size];
			int[] mapObjectsFileIds = new int[size];
			for (int i = 0; i < size; i++) {
				regionIds[i] = in.getUShort();
				mapGroundFileIds[i] = in.getUShort();
				mapObjectsFileIds[i] = in.getUShort();
			}

			int highest = 0;
			for (int i = 0; i < size; i++) {
				regions[i] = new Region(regionIds[i], false);
				if (regionIds[i] > highest) {
					highest = regionIds[i];
				}
			}
			buildTable(highest + 1);

			for (int i = 0; i < size; i++) {
				byte[] file1 = getBuffer(new File(ServerConstants.getOsrsGlobalDataLocation() + "world osrs/map/" + mapObjectsFileIds[i] + ".gz"));
				byte[] file2 = getBuffer(new File(ServerConstants.getOsrsGlobalDataLocation() + "world osrs/map/" + mapGroundFileIds[i] + ".gz"));
				if (file1 == null || file2 == null) {
					continue;
				}
				try {
					final int index = i;

					if (MULTI_THREAD_ENABLED) {
						futures.add(service.submit(() -> loadMaps(regionIds[index], new ByteStream(file1), new ByteStream(file2))));
					} else {
						loadMaps(regionIds[index], new ByteStream(file1), new ByteStream(file2));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (MULTI_THREAD_ENABLED) {
				while (!futures.stream().allMatch(Future::isDone)) {
					return;
				}
			}
			loadingCompleted = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		CustomClippedTiles.removeClippedTileOsrs();
		CustomClippedTiles.removeClippedTilePreEoc();
	}

	public static boolean hasLoadingCompleted() {
		return loadingCompleted;
	}

	private static void loadMaps(int regionId, ByteStream str1, ByteStream str2) {
		int absX = (regionId >> 8) * 64;
		int absY = (regionId & 0xff) * 64;
		int[][][] someArray = new int[4][64][64];
		for (int i = 0; i < 4; i++) {
			for (int i2 = 0; i2 < 64; i2++) {
				for (int i3 = 0; i3 < 64; i3++) {
					while (true) {
						int v = str2.getUByte();
						if (v == 0) {
							break;
						} else if (v == 1) {
							str2.skip(1);
							break;
						} else if (v <= 49) {
							str2.skip(1);
						} else if (v <= 81) {
							someArray[i][i2][i3] = v - 49;
						}
					}
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int i2 = 0; i2 < 64; i2++) {
				for (int i3 = 0; i3 < 64; i3++) {
					if ((someArray[i][i2][i3] & 1) == 1) {
						int height = i;
						if ((someArray[1][i2][i3] & 2) == 2) {
							height--;
						}
						if (height >= 0 && height <= 3) {
							addClipping(absX + i2, absY + i3, height, 0x200000, "Here59", -1, -1);
						}
					}
				}
			}
		}
		int objectId = -1;
		int incr;
		while ((incr = str1.getUSmart()) != 0) {
			objectId += incr;
			int location = 0;
			int incr2;
			while ((incr2 = str1.getUSmart()) != 0) {
				location += incr2 - 1;
				int localX = (location >> 6 & 0x3f);
				int localY = (location & 0x3f);
				int height = location >> 12;
				int objectData = str1.getUByte();
				int type = objectData >> 2;
				int direction = objectData & 0x3;
				if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) {
					continue;
				}
				if ((someArray[1][localX][localY] & 2) == 2) {
					height--;
				}
				if (height >= -1 && height <= 3) {
					addObject(objectId, absX + localX, absY + localY, height, type, direction, false);
				}
			}
		}

	}

	/**
	 * Attempts to retrieve the next open tile around this position as long as its not blocked.
	 *
	 * @return the next open tile.
	 */
	public static Position nextOpenTileOrNull(int x, int y, int z) {
		if (!Region.blockedNorth(x, y, z)) {
			return new Position(x, y + 1, z);
		} else if (!Region.blockedNorthEast(x, y, z)) {
			return new Position(x + 1, y + 1, z);
		} else if (!Region.blockedEast(x, y, z)) {
			return new Position(x + 1, y, z);
		} else if (!Region.blockedSouthEast(x, y, z)) {
			return new Position(x + 1, y - 1, z);
		} else if (!Region.blockedSouth(x, y, z)) {
			return new Position(x, y - 1, z);
		} else if (!Region.blockedSouthWest(x, y, z)) {
			return new Position(x - 1, y - 1, z);
		} else if (!Region.blockedWest(x, y, z)) {
			return new Position(x - 1, y, z);
		} else if (!Region.blockedNorthWest(x, y, z)) {
			return new Position(x - 1, y + 1, z);
		}
		return null;
	}

	public static void addObjectActionTile(int objectId, int x, int y, int height, int xLength, int yLength) {
		Region r = getRegion(x, y);

		if (r != null) {
			r.verifiedObjects.add(new Object(objectId, x, y, height, 0, 10, 0, 0, false));
			addClippingForSolidObject(x, y, height, xLength, yLength, true, 0, 0);
		}
	}

	public static byte[] getBuffer(File f) throws Exception {
		if (!f.exists())
			return null;
		byte[] buffer = new byte[(int) f.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		dis.readFully(buffer);
		dis.close();
		byte[] gzipInputBuffer = new byte[999999];
		int bufferlength = 0;
		GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer));
		do {
			if (bufferlength == gzipInputBuffer.length) {
				Misc.print("Error inflating data.\nGZIP buffer overflow.");
				break;
			}
			int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
			if (readByte == -1)
				break;
			bufferlength += readByte;
		}
		while (true);
		byte[] inflated = new byte[bufferlength];
		System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
		buffer = inflated;
		if (buffer.length < 10)
			return null;
		return buffer;
	}

	public static boolean projectileCanPassThrough(int x, int y, int z) {
		return (getClipping(x, y, z) & 0x20000) != 0;
	}

	public static boolean blockedNorth(int x, int y, int z) {

		int dynamic = DynamicClipping.dynamicPathResult(x, y, z, Region.NORTH);
		if (dynamic == 2) {
			return true;
		} else if (dynamic == 1) {
			return false;
		}
		return (getClipping(x, y + 1, z) & 0x1280120) != 0;
	}

	public static boolean blockedEast(int x, int y, int z) {

		int dynamic = DynamicClipping.dynamicPathResult(x, y, z, Region.EAST);
		if (dynamic == 2) {
			return true;
		} else if (dynamic == 1) {
			return false;
		}
		return (getClipping(x + 1, y, z) & 0x1280180) != 0;
	}

	public static boolean blockedSouth(int x, int y, int z) {

		int dynamic = DynamicClipping.dynamicPathResult(x, y, z, Region.SOUTH);
		if (dynamic == 2) {
			return true;
		} else if (dynamic == 1) {
			return false;
		}
		return (getClipping(x, y - 1, z) & 0x1280102) != 0;
	}
	// != 0 means blocked
	// == 0 means not blocked

	public static boolean blockedWest(int x, int y, int z) {

		int dynamic = DynamicClipping.dynamicPathResult(x, y, z, Region.WEST);
		if (dynamic == 2) {
			return true;
		} else if (dynamic == 1) {
			return false;
		}
		return (getClipping(x - 1, y, z) & 0x1280108) != 0;
	}

	public static boolean blockedNorthEast(int x, int y, int z) {
		int dynamic = DynamicClipping.dynamicPathResult(x, y, z, Region.NORTH_EAST);
		if (dynamic == 2) {
			return true;
		} else if (dynamic == 1) {
			return false;
		}
		return (getClipping(x + 1, y + 1, z) & 0x12801e0) != 0;
	}

	public static boolean blockedNorthWest(int x, int y, int z) {

		int dynamic = DynamicClipping.dynamicPathResult(x, y, z, Region.NORTH_WEST);
		if (dynamic == 2) {
			return true;
		} else if (dynamic == 1) {
			return false;
		}
		return (getClipping(x - 1, y + 1, z) & 0x1280138) != 0;
	}

	public static boolean blockedSouthEast(int x, int y, int z) {
		int dynamic = DynamicClipping.dynamicPathResult(x, y, z, Region.SOUTH_EAST);
		if (dynamic == 2) {
			return true;
		} else if (dynamic == 1) {
			return false;
		}
		return (getClipping(x + 1, y - 1, z) & 0x1280183) != 0;
	}

	public static boolean blockedSouthWest(int x, int y, int z) {

		int dynamic = DynamicClipping.dynamicPathResult(x, y, z, Region.SOUTH_WEST);
		if (dynamic == 2) {
			return true;
		} else if (dynamic == 1) {
			return false;
		}
		return (getClipping(x - 1, y - 1, z) & 0x128010e) != 0;
	}

	public static boolean objectExistsHere(Player player) {
		Region region = player.getRegionOrNull();

		if (region == null || Server.objectManager.exists(player.getX(), player.getY(), player.getHeight())
				|| region.objectExists(player.getX(), player.getY(), player.getHeight())) {
			return true;
		}
		return false;
	}
}
