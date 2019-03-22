package game.content.skilling.crafting;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Created by Owain on 11/08/2017.
 */
public class OrbCharging {

	public enum Orbs {
		AIR(573, 66, 76, 1381, 150),
		WATER(571, 56, 66, 1383, 149),
		EARTH(575, 60, 70, 1385, 151),
		FIRE(569, 63, 73, 1387, 152);

		private int orb;

		private int level;

		private int xp;

		private int staffRequired;

		private int gfx;

		Orbs(int orb, int level, int xp, int staffRequired, int gfx) {
			this.orb = orb;
			this.level = level;
			this.xp = xp;
			this.staffRequired = staffRequired;
			this.gfx = gfx;
		}

		public int getOrb() {
			return orb;
		}

		public int getLevel() {
			return level;
		}

		public double getXp() {
			return xp;
		}

		public int getstaffRequired() {
			return staffRequired;
		}

		public int getGfx() {
			return gfx;
		}
	}

	public static void chargeOrb(Player player, Orbs orbs) {
		if (player.baseSkillLevel[ServerConstants.CRAFTING] < orbs.getLevel()) {
			player.getPA().closeInterfaces(true);
			player.getDH().sendStatement("You need a crafting level of " + orbs.getLevel() + " to charge " + ItemAssistant.getItemName(orbs.getOrb()).toLowerCase() + "s.");
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
				if (!ItemAssistant.hasItemInInventory(player, 567)) {
					container.stop();
					player.playerAssistant.sendMessage("You have run out of unpowered orbs.");
					return;
				}
				player.getPA().closeInterfaces(true);
				player.startAnimation(726);
				player.gfx100(orbs.getGfx());
				ItemAssistant.deleteItemFromInventory(player, 567, 1);
				ItemAssistant.addItem(player, orbs.getOrb(), 1);
				Skilling.addSkillExperience(player, (int) orbs.getXp(), ServerConstants.CRAFTING, false);
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);
		return;
	}
}
