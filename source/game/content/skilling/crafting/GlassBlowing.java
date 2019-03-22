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
public class GlassBlowing {

	@SuppressWarnings("unused")
	private static int amount;

	public static void makeGlass(final Player player, final int buttonId) {
		for (final glassData g : glassData.values()) {
			if (buttonId == g.getButtonId(buttonId)) {
				if (player.baseSkillLevel[ServerConstants.CRAFTING] < g.getLevelReq()) {
					player.playerAssistant
							.sendMessage("You need a crafting level of " + g.getLevelReq() + " to make " + ItemAssistant.getItemName(g.getNewId()).toLowerCase() + "s.");
					player.getPA().closeInterfaces(true);
					return;
				}
				player.getPA().closeInterfaces(true);
				amount = g.getAmount(buttonId);


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
						if (!ItemAssistant.hasItemInInventory(player, 1775)) {
							container.stop();
							return;
						}
						player.startAnimation(884);
						ItemAssistant.deleteItemFromInventory(player, 1775, 1);
						ItemAssistant.addItem(player, g.getNewId(), 1);
						Skilling.addSkillExperience(player, (int) g.getXP(), ServerConstants.CRAFTING, false);
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

	public static void viewInterface(final Player c) {
		c.getPA().displayInterface(11462);
	}

	public enum glassData {

		BEER_GLASS(new int[][]
				           {
						           {48112, 1},
						           {48111, 5},
						           {48110, 28}
				           }, 1919, 1, 1, 19.5),
		CANDLE_LANTERN(new int[][]
				               {
						               {48116, 1},
						               {48115, 5},
						               {48114, 28}
				               }, 4529, 1, 4, 19.0),
		OIL_LAMP(new int[][]
				         {
						         {48120, 1},
						         {48119, 5},
						         {48118, 28}
				         }, 4522, 1, 12, 25.0),
		VIAL(new int[][]
				     {
						     {44210, 1},
						     {44209, 5},
						     {44208, 28}
				     }, 229, 1, 33, 35),
		FISHBOWL(new int[][]
				         {
						         {24059, 1},
						         {24058, 5},
						         {24057, 28}
				         }, 6667, 1, 42, 42.5),
		GLASS_ORB(new int[][]
				          {
						          {48108, 1},
						          {48107, 5},
						          {48106, 28}
				          }, 567, 1, 46, 52.5),
		BULLSEYE_LANTERN_LENS(new int[][]
				                      {
						                      {48124, 1},
						                      {48123, 5},
						                      {48122, 28}
				                      }, 4542, 1, 49, 55.0);

		private int[][] buttonId;

		private int newId, needed, levelReq;

		private double xp;

		glassData(final int[][] buttonId, final int newId, final int needed, final int levelReq, final double xp) {
			this.buttonId = buttonId;
			this.newId = newId;
			this.needed = needed;
			this.levelReq = levelReq;
			this.xp = xp;
		}

		public int getButtonId(final int button) {
			for (int i = 0; i < buttonId.length; i++) {
				if (button == buttonId[i][0]) {
					return buttonId[i][0];
				}
			}
			return -1;
		}

		public int getAmount(final int button) {
			for (int i = 0; i < buttonId.length; i++) {
				if (button == buttonId[i][0]) {
					return buttonId[i][1];
				}
			}
			return -1;
		}

		public int getNewId() {
			return newId;
		}

		public int getNeeded() {
			return needed;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public double getXP() {
			return xp;
		}

	}

	public static boolean isGlassBlowingButton(Player player, int buttonId) {
		for (GlassBlowing.glassData t : GlassBlowing.glassData.values()) {
			if (buttonId == t.getButtonId(buttonId)) {
				GlassBlowing.makeGlass(player, buttonId);
				return true;
			}
		}
		return false;
	}
}
