package game.content.skilling;

import core.GameType;
import core.ServerConstants;
import game.content.miscellaneous.RandomEvent;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

public class Bonfire {

	public enum Logs {
		REGULAR(1511, 1, 40),
		BLUE(7406, 1, 40),
		GREEN(7405, 1, 40),
		PURPLE(10329, 1, 40),
		WHITE(10328, 1, 40),
		RED(7404, 1, 40),
		ACHEY(2862, 1, 40),
		OAK(1521, 15, 60),
		WILLOW(1519, 30, 90),
		TEAK(6333, 35, 105),
		ARCTIC_PINE(10810, 42, 125),
		MAPLE(1517, 45, 135),
		MAHOGANY(6332, 50, 157),
		YEW(1515, 60, 203),
		MAGIC(1513, 75, 304),
		REDWOOD(19669, 90, 350);

		private int logID;

		private int level;

		private int xp;

		Logs(int logID, int level, int xp) {
			this.logID = logID;
			this.level = level;
			this.xp = xp;
		}

		public int getLogID() {
			return logID;
		}

		public int getLevel() {
			return level;
		}

		public int getXp() {
			return xp;
		}

	}

	public static void startBurning(final Player player, final int itemId) {
		for (final Logs g : Logs.values()) {
			if (itemId == g.getLogID()) {

				if (g == Logs.REDWOOD && GameType.isPreEoc()) {
					return;
				}
				if (player.baseSkillLevel[ServerConstants.FIREMAKING] < g.getLevel()) {
					player.getPA().sendMessage("You need a Firemaking level of " + g.getLevel() + " to burn " + ItemAssistant.getItemName(g.getLogID()) + ".");
					return;
				}
				if (RandomEvent.isBannedFromSkilling(player)) {
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, g.getLogID())) {
					return;
				}
				if (Skilling.cannotActivateNewSkillingEvent(player)) {
					return;
				}
				if (ItemAssistant.hasItemAmountInInventory(player, itemId, 1)) {
					bonfireCycle(player, itemId);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (Skilling.forceStopSkillingEvent(player)) {
								container.stop();
								return;
							}
							if (ItemAssistant.hasItemInInventory(player, itemId)) {
								bonfireCycle(player, itemId);
							} else {
								container.stop();
							}
						}

						@Override
						public void stop() {
							Skilling.endSkillingEvent(player);
						}
					}, 4);
					return;
				}
			}
		}
	}

	public static void bonfireCycle(final Player player, int itemId) {
		for (final Logs g : Logs.values()) {
			if (itemId == g.getLogID()) {
				if (ItemAssistant.hasItemInInventory(player, itemId)) {
					ItemAssistant.deleteItemFromInventory(player, itemId, 1);
					player.startAnimation(645);
					player.getPA().sendFilterableMessage("You add some " + ItemAssistant.getItemName(g.getLogID()) + " to the fire.");
					Skilling.addSkillExperience(player, g.getXp(), ServerConstants.FIREMAKING, false);
					player.skillingStatistics[SkillingStatistics.LOGS_BURNT]++;
					Skilling.petChance(player, g.getXp(), 304, 10368, ServerConstants.FIREMAKING, null);
				}
			}
		}
	}
}
