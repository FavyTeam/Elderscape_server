package game.content.skilling;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.object.clip.Region;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import java.util.ArrayList;
import utility.Misc;

public class Firemaking {
	public static final int FIRE_OBJECT_ID = 26185;

	public static boolean isCookableFireObject(int objectId) {
		switch (objectId) {
			case 5249:
			case 26185:
			case 26576: // Blue fire
			case 26575: // Green fire
			case 20001: // Purple fire
			case 20000: // White fire
			case 26186: // Red fire
				return true;
		}
		return false;
	}

	public static ArrayList<String> fireMakingSpots = new ArrayList<String>();


	public static enum LogData {
		REGULAR(1511, 1, 40, 125, 26185),

		BLUE(7406, 1, 40, 125, 26576),

		GREEN(7405, 1, 40, 125, 26575),

		PURPLE(10329, 1, 40, 125, 20001),

		WHITE(10328, 1, 40, 125, 20000),

		RED(7404, 1, 40, 125, 26186),

		ACHEY(2862, 1, 40, 125, 26185),

		OAK(1521, 15, 60, 135, 26185),

		WILLOW(1519, 30, 90, 145, 26185),

		TEAK(6333, 35, 105, 150, 26185),

		ARCTIC_PINE(10810, 42, 125, 150, 26185),

		MAPLE(1517, 45, 135, 155, 26185),

		MAHOGANY(6332, 50, 157, 160, 26185),

		YEW(1515, 60, 203, 165, 26185),

		MAGIC(1513, 75, 304, 185, 26185),

		REDWOOD(19669, 90, 350, 195, 26185);

		private int id, req, experience, timer, objectId;

		private LogData(int id, int level, int experience, int timer, int objectId) {
			this.id = id;
			this.req = level;
			this.experience = experience;
			this.timer = timer;
			this.objectId = objectId;
		}

		public int getItemId() {
			return id;
		}

		public int getRequiredLevel() {
			return req;
		}

		public int getExperience() {
			return experience;
		}

		public int getTimer() {
			return timer;
		}

		public int getObjectId() {
			return objectId;
		}
	}


	public static enum Firelighters {
		BLUE(7406, 7331), GREEN(7405, 7330), PURPLE(10329, 10326), WHITE(10328, 10327), RED(7404,
				7329);

		private int logId, firelighterId;

		private Firelighters(int logId, int firelighterId) {
			this.logId = logId;
			this.firelighterId = firelighterId;
		}

		public int getLogId() {
			return logId;
		}

		public int getFirelighterId() {
			return firelighterId;
		}
	}

	public static boolean colourLogs(Player player, int itemUsed, int usedWith) {
		boolean hasLog = false;
		boolean hasFirelighter = false;
		for (Firelighters data : Firelighters.values()) {
			if (itemUsed == 1511 || usedWith == 1511) {
				hasLog = true;
			}
			if (usedWith == data.getFirelighterId() || itemUsed == data.getFirelighterId()) {
				hasFirelighter = true;
			}
			if (hasLog && hasFirelighter) {
				ItemAssistant.deleteItemFromInventory(player, 1511, 1);
				ItemAssistant.deleteItemFromInventory(player, data.getFirelighterId(), 1);
				ItemAssistant.addItem(player, data.getLogId(), 1);
				return true;
			}
		}
		return false;
	}

	/**
	 * @return True, if the fire exists at the given coordinates.
	 */
	public static boolean fireExists(int fireX, int fireY, int fireHeight) {
		if (fireX == 2200 && fireY == 3249) {
			return true;
		}
		for (int i = 0; i < Firemaking.fireMakingSpots.size(); i++) {
			if (Firemaking.fireMakingSpots.get(i).equals(fireX + " " + fireY + " " + fireHeight)) {
				return true;
			}
		}

		return false;
	}

	public static boolean playerLogs(Player player, int i, int l) {
		boolean flag = false;
		for (LogData data : LogData.values()) {
			if ((i == data.getItemId() && requiredItem(player, l))
					|| (requiredItem(player, i) && l == data.getItemId())) {
				flag = true;
			}
		}
		return flag;
	}

	private static boolean requiredItem(Player player, int i) {
		if (i == 590) {
			return true;
		}
		return false;
	}

	/**
	 * Initiate default firemaking spots that are spawned by the map.
	 */
	public static void defaultFiremakingSpots() {
		fireMakingSpots.add("3188 3930 0");
	}

	public static boolean grabData(final Player player, final int itemUsed, final int usedWith) {
		if (!Firemaking.playerLogs(player, itemUsed, usedWith)) {
			return false;
		}
		if (RandomEvent.isBannedFromSkilling(player)) {
			return true;
		}

		final int[] coords = new int[2];
		coords[0] = player.getX();
		coords[1] = player.getY();
		player.playerAssistant.stopAllActions();
		for (LogData data : LogData.values()) {
			if (data == LogData.REDWOOD && GameType.isPreEoc()) {
				return false;
			}
			if ((requiredItem(player, itemUsed) && usedWith == data.getItemId()
					|| itemUsed == data.getItemId() && requiredItem(player, usedWith))) {
				if (player.baseSkillLevel[11] < data.getRequiredLevel()) {
					player.getDH().sendStatement(
							"You need the Firemaking level of at least " + data.getRequiredLevel() + ".");
					return false;
				}
				if (fireExists(player.getX(), player.getY(), player.getHeight())
						|| Region.objectExistsHere(player)) {
					player.getPA().sendMessage("You can't light a fire here.");
					return false;
				}
				if (System.currentTimeMillis() - player.lastFire > 1200) {

					if (player.playerIsFiremaking) {
						return false;
					}

					final int[] time = new int[3];
					final int log = data.getItemId();

					// Yew log.
					if (log == 1515) {
						Achievements.checkCompletionMultiple(player, "1041");
					}
					// Magic log.
					else if (log == 1513) {
						Achievements.checkCompletionMultiple(player, "1064");
					}
					if (System.currentTimeMillis() - player.lastFire > 2450) {
						player.startAnimation(733);
						SoundSystem.sendSound(player, 811, 400);
						time[0] = 4;
						time[1] = 3;
					} else {
						time[0] = 1;
						time[1] = 2;
					}
					player.playerIsFiremaking = true;
					player.woodCuttingEventTimer = -1;
					if (Skilling.hasMasterCapeWorn(player, 9804) && Misc.hasPercentageChance(10)) {
						player.getPA().sendMessage(
								"<col=a54704>Your cape allows you to save a log from being burnt.");
					} else {
						ItemAssistant.deleteItemFromInventory(player, data.getItemId(),
								ItemAssistant.getItemSlot(player, data.getItemId()), 1);
					}
					Server.itemHandler.createGroundItem(player, log, coords[0], coords[1],
							player.getHeight(), 1, false, 0, true, "", "", "", "", "grabData " + log);

					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							new Object(data.getObjectId(), coords[0], coords[1], player.getHeight(), 0, 10,
									-1, data.getTimer());
							Server.itemHandler.removeGroundItem(player, log, coords[0], coords[1], 1,
									false);
							player.playerIsFiremaking = false;
							fireMakingSpots.add(coords[0] + " " + coords[1] + " " + player.getHeight());
							container.stop();
						}

						@Override
						public void stop() {

						}
					}, time[0]);

					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}

						@Override
						public void stop() {
							Server.itemHandler.createGroundItem(player, 592, coords[0], coords[1],
									player.getHeight(), 1, true, 40, true, "", "", "", "", "grabData 592");
						}
					}, data.getTimer() + time[0]);

					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							player.startAnimation(65535);
							container.stop();
						}

						@Override
						public void stop() {

						}
					}, time[1] + 1);

					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							Movement.movePlayerFromUnderEntity(player);
							player.getPA()
									.sendFilterableMessage("The fire catches and the logs begin to burn.");
							Skilling.addSkillExperience(player, data.getExperience(), 11, false);
							Skilling.petChance(player, data.getExperience(), 304, 10368,
									ServerConstants.FIREMAKING, null);
							player.skillingStatistics[SkillingStatistics.LOGS_BURNT]++;
							player.turnPlayerTo(player.getX() + 1, player.getY());
							player.lastFire = System.currentTimeMillis();
							container.stop();
						}

						@Override
						public void stop() {

						}
					}, time[0]);
					return true;
				}
			}
		}
		return false;
	}

	public static void deleteFire(Object object) {
		for (LogData data : LogData.values()) {
			if (object.objectId != data.objectId) {
				return;
			}
			for (int i = 0; i < Firemaking.fireMakingSpots.size(); i++) {
				if (Firemaking.fireMakingSpots.get(i)
						.equals(object.objectX + " " + object.objectY + " " + object.height)) {
					Firemaking.fireMakingSpots.remove(Firemaking.fireMakingSpots.get(i));
					break;
				}
			}
		}
	}
}
