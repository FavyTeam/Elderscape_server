package game.content.skilling.crafting;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Created by Owain on 10/08/2017.
 */
public class BattlestaffMaking {

	public enum Battlestaffs {
		AIR(1397, 573, 66, 137, 306),
		WATER(1395, 571, 54, 100, 1370),
		EARTH(1399, 575, 58, 112, 1371),
		FIRE(1393, 569, 62, 125, 1372);

		private int finishedStaff;

		private int orb;

		private int level;

		private int xp;

		private int gfx;

		Battlestaffs(int finishedStaff, int orb, int level, int xp, int gfx) {
			this.finishedStaff = finishedStaff;
			this.orb = orb;
			this.level = level;
			this.xp = xp;
			this.gfx = gfx;
		}

		public int getfinishedStaff() {
			return finishedStaff;
		}

		public int getOrb() {
			return orb;
		}

		public int getLevel() {
			return level;
		}

		public int getXp() {
			return xp;
		}

		public int getGfx() {
			return gfx;
		}

	}

	public static void makeBattlestaff(final Player player, final int itemUsed, final int usedWith) {
		if (!ItemAssistant.hasItemInInventory(player, 1391)) {
			return;
		}
		for (final Battlestaffs g : Battlestaffs.values()) {
			final int itemId = (itemUsed == 1391 ? usedWith : itemUsed);
			if (itemId == g.getOrb()) {
				if (player.baseSkillLevel[ServerConstants.CRAFTING] < g.getLevel()) {
					player.playerAssistant.sendMessage("You need a Crafting level of " + g.getLevel() + " to make " + ItemAssistant.getItemName(g.getfinishedStaff()) + "s.");
					return;
				}
				if (Skilling.cannotActivateNewSkillingEvent(player)) {
					return;
				}
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (Skilling.forceStopSkillingEvent(player)) {
							container.stop();
							return;
						}
						if (ItemAssistant.hasItemInInventory(player, g.getOrb()) && ItemAssistant.hasItemInInventory(player, 1391)) {
							ItemAssistant.deleteItemFromInventory(player, 1391, 1);
							ItemAssistant.deleteItemFromInventory(player, g.getOrb(), 1);
							ItemAssistant.addItem(player, g.getfinishedStaff(), 1);
							player.gfx0(g.getGfx());
							player.startAnimation(7531);
							Skilling.addSkillExperience(player, g.getXp(), ServerConstants.CRAFTING, false);
						} else {
							container.stop();
						}
					}

					@Override
					public void stop() {
						Skilling.endSkillingEvent(player);
					}
				}, 2);
				return;
			}
		}
	}

	public static boolean useOrbonStaff(Player player, int itemUsedId, int itemUsedWith) {
		for (final Battlestaffs g : Battlestaffs.values()) {
			if (itemUsedId != 1391 && itemUsedWith != g.getOrb() && itemUsedId != g.getOrb() && itemUsedWith != 1391) {
				return false;
			}
			InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 150, g.getfinishedStaff(), 20, 0);
			player.skillingInterface = "BATTLESTAFF";
			player.skillingData[0] = g.getOrb();
			return true;
		}
		return false;
	}

	public static boolean isUsingItemOnBattlestaff(Player player, int itemUsedId, int itemUsedWithId) {
		if (itemUsedId == 1391 || itemUsedWithId == 1391) {
			if (RandomEvent.isBannedFromSkilling(player)) {
				return true;
			}
			makeBattlestaff(player, itemUsedId, itemUsedWithId);
			return true;
		}
		return false;
	}
}
