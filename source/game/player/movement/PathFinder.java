package game.player.movement;

import game.entity.EntityType;
import game.object.clip.Region;
import game.player.Player;
import utility.Misc;

import java.util.ArrayList;
import java.util.Arrays;


public class PathFinder {

	private static final PathFinder pathFinder = new PathFinder();

	public static PathFinder getPathFinder() {
		return pathFinder;
	}

	public PathFinder() {
	}

	public static int loops1;

	public static int loops2;

	public static int loops3;

	public static int loops4;

	public static int loops5;

	public static int loops6;

	public static int destX;

	public static int destY;

	public void findRoute(Player player, int destX, int destY, boolean moveNear, int xLength, int yLength, int maximumPath) {
		PathFinder.destX = destX;
		PathFinder.destY = destY;
		if (player.dragonSpearEvent) {
			return;
		}
		if (destX == player.getLocalX() && destY == player.getLocalY() && !moveNear) {
			return;
		}
		int maximum = maximumPath;
		if (player.getX() - destX <= -maximum || player.getX() - destX >= maximum || player.getY() - destY <= -maximum || player.getY() - destY >= maximum) {
			return;
		}
		destX = destX - 8 * player.getMapRegionX();
		destY = destY - 8 * player.getMapRegionY();
		int[][] via = new int[104][104];
		int[][] cost = new int[104][104];

		ArrayList<Integer> tileQueueX = new ArrayList<Integer>(9000);
		ArrayList<Integer> tileQueueY = new ArrayList<Integer>(9000);

		for (int xx = 0; xx < 104; xx++) {
			for (int yy = 0; yy < 104; yy++) {
				cost[xx][yy] = 99999999;
			}
		}
		int curX = player.getLocalX();
		int curY = player.getLocalY();
		via[curX][curY] = 99;
		cost[curX][curY] = 0;
		int tail = 0;
		tileQueueX.add(curX);
		tileQueueY.add(curY);
		boolean foundPath = false;
		int pathLength = 4000;
		while (tail != tileQueueX.size() && tileQueueX.size() < pathLength) {
			loops1++;
			curX = tileQueueX.get(tail);
			curY = tileQueueY.get(tail);
			int curAbsX = player.getMapRegionX() * 8 + curX;
			int curAbsY = player.getMapRegionY() * 8 + curY;
			if (curX == destX && curY == destY) {
				foundPath = true;
				break;
			}
			tail = (tail + 1) % pathLength;
			int thisCost = cost[curX][curY] + 1;
			if (curY > 0 && via[curX][curY - 1] == 0 && !Region.blockedSouth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX);
				tileQueueY.add(curY - 1);
				via[curX][curY - 1] = 1;
				cost[curX][curY - 1] = thisCost;
			}
			if (curX > 0 && via[curX - 1][curY] == 0 && !Region.blockedWest(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY);
				via[curX - 1][curY] = 2;
				cost[curX - 1][curY] = thisCost;
			}
			if (curY < 104 - 1 && via[curX][curY + 1] == 0 && !Region.blockedNorth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX);
				tileQueueY.add(curY + 1);
				via[curX][curY + 1] = 4;
				cost[curX][curY + 1] = thisCost;
			}
			if (curX < 104 - 1 && via[curX + 1][curY] == 0 && !Region.blockedEast(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY);
				via[curX + 1][curY] = 8;
				cost[curX + 1][curY] = thisCost;
			}
			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0 && !Region.blockedSouthWest(curAbsX, curAbsY, player.getHeight()) && !Region.blockedWest(curAbsX, curAbsY,
			                                                                                                                                                  player.getHeight())
			    && !Region.blockedSouth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY - 1);
				via[curX - 1][curY - 1] = 3;
				cost[curX - 1][curY - 1] = thisCost;
			}
			if (curX > 0 && curY < 104 - 1 && via[curX - 1][curY + 1] == 0 && !Region.blockedNorthWest(curAbsX, curAbsY, player.getHeight()) && !Region.blockedWest(curAbsX,
			                                                                                                                                                        curAbsY,
			                                                                                                                                                        player.getHeight())
			    && !Region.blockedNorth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY + 1);
				via[curX - 1][curY + 1] = 6;
				cost[curX - 1][curY + 1] = thisCost;
			}
			if (curX < 104 - 1 && curY > 0 && via[curX + 1][curY - 1] == 0 && !Region.blockedSouthEast(curAbsX, curAbsY, player.getHeight()) && !Region.blockedEast(curAbsX,
			                                                                                                                                                        curAbsY,
			                                                                                                                                                        player.getHeight())
			    && !Region.blockedSouth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY - 1);
				via[curX + 1][curY - 1] = 9;
				cost[curX + 1][curY - 1] = thisCost;
			}
			if (curX < 104 - 1 && curY < 104 - 1 && via[curX + 1][curY + 1] == 0 && !Region.blockedNorthEast(curAbsX, curAbsY, player.getHeight()) && !Region.blockedEast(curAbsX,
			                                                                                                                                                              curAbsY,
			                                                                                                                                                              player.getHeight())
			    && !Region.blockedNorth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY + 1);
				via[curX + 1][curY + 1] = 12;
				cost[curX + 1][curY + 1] = thisCost;
			}
		}
		if (!foundPath) {
			if (moveNear) {
				int i_223_ = 1000;
				int thisCost = 100;
				int i_225_ = 10;
				for (int x = destX - i_225_; x <= destX + i_225_; x++) {
					loops2++;
					for (int y = destY - i_225_; y <= destY + i_225_; y++) {
						loops3++;
						if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < 100) {
							loops4++;
							int i_228_ = 0;
							if (x < destX)
								i_228_ = destX - x;
							else if (x > destX + xLength - 1)
								i_228_ = x - (destX + xLength - 1);
							int i_229_ = 0;
							if (y < destY)
								i_229_ = destY - y;
							else if (y > destY + yLength - 1)
								i_229_ = y - (destY + yLength - 1);
							int i_230_ = i_228_ * i_228_ + i_229_ * i_229_;
							if (i_230_ < i_223_ || (i_230_ == i_223_ && (cost[x][y] < thisCost))) {
								i_223_ = i_230_;
								thisCost = cost[x][y];
								curX = x;
								curY = y;
							}
						}
					}
				}
				if (i_223_ == 1000)
					return;
			} else {
				return;
			}
		}
		tail = 0;
		tileQueueX.set(tail, curX);
		tileQueueY.set(tail++, curY);
		int l5;
		for (int j5 = l5 = via[curX][curY]; curX != player.getLocalX() || curY != player.getLocalY(); j5 = via[curX][curY]) {
			loops5++;
			if (j5 != l5) {
				l5 = j5;
				tileQueueX.set(tail, curX);
				tileQueueY.set(tail++, curY);
			}
			if ((j5 & 2) != 0) {
				curX++;
			} else if ((j5 & 8) != 0) {
				curX--;
			}
			if ((j5 & 1) != 0) {
				curY++;
			} else if ((j5 & 4) != 0) {
				curY--;
			}
		}
		Movement.resetWalkingQueue(player);
		int size = tail--;
		player.walkingQueueCurrentSize = size;
		int pathX = player.getMapRegionX() * 8 + tileQueueX.get(tail);
		int pathY = player.getMapRegionY() * 8 + tileQueueY.get(tail);
		Movement.addToWalkingQueue(player, localize(pathX, player.getMapRegionX()), localize(pathY, player.getMapRegionY()));
		for (int i = 1; i < size; i++) {
			loops6++;
			tail--;
			pathX = player.getMapRegionX() * 8 + tileQueueX.get(tail);
			pathY = player.getMapRegionY() * 8 + tileQueueY.get(tail);
			Movement.addToWalkingQueue(player, localize(pathX, player.getMapRegionX()), localize(pathY, player.getMapRegionY()));
		}
	}

	public void findRouteNew(Player player, int destX, int destY, boolean moveNear, int xLength, int yLength) {
		PathFinder.destX = destX;
		PathFinder.destY = destY;
		if (player.dragonSpearEvent) {
			return;
		}
		if (destX == player.getLocalX() && destY == player.getLocalY() && !moveNear) {
			Misc.print("Path finding new rejected.");
			return;
		}

		destX = destX - (player.getMapRegionX() << 3);
		destY = destY - (player.getMapRegionY() << 3);

		int[][] via = new int[104][104];
		int[][] cost = new int[104][104];

		ArrayList<Integer> tileQueueX = new ArrayList<Integer>(9000);
		ArrayList<Integer> tileQueueY = new ArrayList<Integer>(9000);

		int curX = player.getLocalX();
		int curY = player.getLocalY();
		via[curX][curY] = 99;
		cost[curX][curY] = 1;
		int tail = 0;
		tileQueueX.add(curX);
		tileQueueY.add(curY);

		final int regionX = player.getMapRegionX() << 3;
		final int regionY = player.getMapRegionY() << 3;

		boolean foundPath = false;
		int pathLength = 4000;

		while (tail != tileQueueX.size() && tileQueueX.size() < pathLength) {

			curX = tileQueueX.get(tail);
			curY = tileQueueY.get(tail);

			int curAbsX = (regionX) + curX;
			int curAbsY = (regionY) + curY;

			if (curX == destX && curY == destY) {
				foundPath = true;
				break;
			}

			tail = (tail + 1) % pathLength;

			int thisCost = cost[curX][curY] + 1 + 1;


			if (curY > 0 && via[curX][curY - 1] == 0 && !Region.blockedSouth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX);
				tileQueueY.add(curY - 1);
				via[curX][curY - 1] = 1;
				cost[curX][curY - 1] = thisCost;
			}
			if (curX > 0 && via[curX - 1][curY] == 0 && !Region.blockedWest(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY);
				via[curX - 1][curY] = 2;
				cost[curX - 1][curY] = thisCost;
			}
			if (curY < 104 - 1 && via[curX][curY + 1] == 0 && !Region.blockedNorth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX);
				tileQueueY.add(curY + 1);
				via[curX][curY + 1] = 4;
				cost[curX][curY + 1] = thisCost;
			}
			if (curX < 104 - 1 && via[curX + 1][curY] == 0 && !Region.blockedEast(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY);
				via[curX + 1][curY] = 8;
				cost[curX + 1][curY] = thisCost;
			}
			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0 && !Region.blockedSouthWest(curAbsX, curAbsY, player.getHeight()) && !Region.blockedWest(curAbsX, curAbsY,
			                                                                                                                                                  player.getHeight())
			    && !Region.blockedSouth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY - 1);
				via[curX - 1][curY - 1] = 3;
				cost[curX - 1][curY - 1] = thisCost;
			}
			if (curX > 0 && curY < 104 - 1 && via[curX - 1][curY + 1] == 0 && !Region.blockedNorthWest(curAbsX, curAbsY, player.getHeight()) && !Region.blockedWest(curAbsX,
			                                                                                                                                                        curAbsY,
			                                                                                                                                                        player.getHeight())
			    && !Region.blockedNorth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY + 1);
				via[curX - 1][curY + 1] = 6;
				cost[curX - 1][curY + 1] = thisCost;
			}
			if (curX < 104 - 1 && curY > 0 && via[curX + 1][curY - 1] == 0 && !Region.blockedSouthEast(curAbsX, curAbsY, player.getHeight()) && !Region.blockedEast(curAbsX,
			                                                                                                                                                        curAbsY,
			                                                                                                                                                        player.getHeight())
			    && !Region.blockedSouth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY - 1);
				via[curX + 1][curY - 1] = 9;
				cost[curX + 1][curY - 1] = thisCost;
			}
			if (curX < 104 - 1 && curY < 104 - 1 && via[curX + 1][curY + 1] == 0 && !Region.blockedNorthEast(curAbsX, curAbsY, player.getHeight()) && !Region.blockedEast(curAbsX,
			                                                                                                                                                              curAbsY,
			                                                                                                                                                              player.getHeight())
			    && !Region.blockedNorth(curAbsX, curAbsY, player.getHeight())) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY + 1);
				via[curX + 1][curY + 1] = 12;
				cost[curX + 1][curY + 1] = thisCost;
			}
		}

		if (!foundPath) {
			if (moveNear) {

				int i_223_ = 1000;
				int thisCost = 100 + 1;
				int i_225_ = 10;

				for (int x = destX - i_225_; x <= destX + i_225_; x++) {
					for (int y = destY - i_225_; y <= destY + i_225_; y++) {
						if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < 100 && cost[x][y] != 0) {
							int i_228_ = 0;
							if (x < destX) {
								i_228_ = destX - x;
							} else if (x > destX + xLength - 1) {
								i_228_ = x - (destX + xLength - 1);
							}
							int i_229_ = 0;
							if (y < destY) {
								i_229_ = destY - y;
							} else if (y > destY + yLength - 1) {
								i_229_ = y - (destY + yLength - 1);
							}
							int i_230_ = i_228_ * i_228_ + i_229_ * i_229_;
							if (i_230_ < i_223_ || i_230_ == i_223_ && cost[x][y] < thisCost && cost[x][y] != 0) {
								i_223_ = i_230_;
								thisCost = cost[x][y];
								curX = x;
								curY = y;
							}
						}
					}
				}

				if (i_223_ == 1000) {
					return;
				}

			} else {
				return;
			}
		}

		tail = 0;
		tileQueueX.set(tail, curX);
		tileQueueY.set(tail++, curY);
		int l5;

		for (int j5 = l5 = via[curX][curY]; curX != player.getLocalX() || curY != player.getLocalY(); j5 = via[curX][curY]) {
			if (j5 != l5) {
				l5 = j5;
				tileQueueX.set(tail, curX);
				tileQueueY.set(tail++, curY);
			}
			if ((j5 & 2) != 0) {
				curX++;
			} else if ((j5 & 8) != 0) {
				curX--;
			}
			if ((j5 & 1) != 0) {
				curY++;
			} else if ((j5 & 4) != 0) {
				curY--;
			}
		}
		Movement.resetWalkingQueue(player);
		int size = tail--;
		int pathX = (regionX) + tileQueueX.get(tail);
		int pathY = (regionY) + tileQueueY.get(tail);

		Movement.addToWalkingQueue(player, localize(pathX, player.getMapRegionX()), localize(pathY, player.getMapRegionY()));

		for (int i = 1; i < size; i++) {
			tail--;
			pathX = (regionX) + tileQueueX.get(tail);
			pathY = (regionY) + tileQueueY.get(tail);
			Movement.addToWalkingQueue(player, localize(pathX, player.getMapRegionX()), localize(pathY, player.getMapRegionY()));
		}
	}

	public static int localize(int value, int mapRegion) {
		return value - 8 * mapRegion;
	}

}
