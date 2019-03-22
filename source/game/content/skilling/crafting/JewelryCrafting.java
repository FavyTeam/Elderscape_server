package game.content.skilling.crafting;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

public class JewelryCrafting {

	public static boolean isJewelryInterfaceButton(Player player, int buttonId) {
		if (buttonId >= 76055 && buttonId <= 76075) {
			mouldJewelry(player, buttonId - 76055);
			return true;
		}
		return false;
	}

	private static void mouldJewelry(final Player player, int jewelryIndex) {

		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		final int required = unStrungAmuletData[jewelryIndex][0];
		final int itemId = unStrungAmuletData[jewelryIndex][1];
		final int level = unStrungAmuletData[jewelryIndex][2];
		final int xp = unStrungAmuletData[jewelryIndex][3];
		if (player.baseSkillLevel[ServerConstants.CRAFTING] < level) {
			player.getPA().sendMessage("You need a crafting level of " + level + " to mould this item.");
			return;
		}
		if (!ItemAssistant.hasItemInInventory(player, 2357) && !player.isInZombiesMinigame()) {
			player.getPA().sendMessage("You need a gold bar to mould this item.");
			return;
		}
		final String itemRequired = ItemAssistant.getItemName(required);
		if (!ItemAssistant.hasItemInInventory(player, required)) {
			player.getPA().sendMessage(
					"You need " + ((itemRequired.startsWith("A") || itemRequired.startsWith("E") || itemRequired.startsWith("O")) ? "an" : "a") + " " + itemRequired.toLowerCase()
					+ " to mould this item.");
			return;
		}
		final String itemName = ItemAssistant.getItemName(itemId);
		int mouldId = 0;
		if (jewelryIndex <= 6) {
			mouldId = 1592;
		} else if (jewelryIndex <= 13) {
			mouldId = 1597;
		} else {
			mouldId = 1595;
		}
		if (!ItemAssistant.hasItemInInventory(player, mouldId) && !player.isInZombiesMinigame()) {
			player.getPA().sendMessage("You need a " + ItemAssistant.getItemName(mouldId) + ".");
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		player.getPA().closeInterfaces(true);
		player.startAnimation(899);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if ((ItemAssistant.hasItemInInventory(player, 2357) || player.isInZombiesMinigame()) && ItemAssistant.hasItemInInventory(player, required)) {
					ItemAssistant.deleteItemFromInventory(player, 2357, 1);
					ItemAssistant.deleteItemFromInventory(player, required, 1);
					ItemAssistant.addItem(player, itemId, 1);
					player.startAnimation(899);
					SoundSystem.sendSound(player, 469, 500);
					Skilling.addSkillExperience(player, xp, ServerConstants.CRAFTING, false);
					player.skillingStatistics[SkillingStatistics.JEWELRY_CREATED]++;
					player.getPA().sendFilterableMessage(
							"You make " + ((itemName.startsWith("A") || itemName.startsWith("E") || itemName.startsWith("O")) ? "an" : "a") + " " + itemName.toLowerCase());
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 3);
	}

	public static boolean stringAmulet(final Player player, final int itemUsed, final int usedWith) {
		final int amuletId = (itemUsed == 1759 ? usedWith : itemUsed);
		for (final StringAmuletData a : StringAmuletData.values()) {
			if (amuletId == a.getUnStrungAmulet()) {
				player.skillingInterface = "STRINGING AMULET";
				player.skillingData[0] = a.getUnStrungAmulet();
				player.skillingData[1] = a.getStrungAmulet();
				InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 160, a.getStrungAmulet(), 0, 5);
				return true;
			}
		}
		return false;
	}

	public static void stringAmuletAmount(Player player, int amount) {

		player.getPA().closeInterfaces(true);
		player.skillingData[3] = amount;
		if (amount == 100) {
			InterfaceAssistant.showAmountInterface(player, "STRINGING AMULET", "Enter amount");
			return;
		}

		stringAmuletAction(player);
	}

	public static void xAmountStringAmulet(Player player, int amount) {
		player.skillingData[3] = amount;
		stringAmuletAction(player);
	}

	private static void stringAmuletAction(final Player player) {

		if (RandomEvent.isBannedFromSkilling(player)) {
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
				if (!ItemAssistant.hasItemAmountInInventory(player, player.skillingData[0], 1)) {
					player.getDH().sendStatement("You have run out of " + ItemAssistant.getItemName(player.skillingData[0]) + ".");
					container.stop();
					return;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, 1759, 1)) {
					player.getPA().sendMessage("You have run out of wool");
					container.stop();
					return;
				}
				if (player.skillingData[3] == 0) {
					container.stop();
					return;
				}

				ItemAssistant.deleteItemFromInventory(player, 1759, 1);
				ItemAssistant.deleteItemFromInventory(player, player.skillingData[0], 1);
				ItemAssistant.addItem(player, player.skillingData[1], 1);
				Skilling.addSkillExperience(player, 4, ServerConstants.CRAFTING, false);
				player.skillingData[3]--;
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);
	}

	public static void jewelryInterface(final Player player) {
		if (player.getActionIdUsed() != 7) {
			return;
		}
		player.getPA().displayInterface(19501);
		// Ring mould.
		if (ItemAssistant.hasItemInInventory(player, 1592) || player.isInZombiesMinigame()) {
			player.getPA().sendFrame126("@gr3@Ready to craft. Right click jewlery to craft.", 19504);
		} else {
			player.getPA().sendFrame126("You need a ring mould to craft rings.", 19504);
		}
		// Necklace mould
		if (ItemAssistant.hasItemInInventory(player, 1597) || player.isInZombiesMinigame()) {
			player.getPA().sendFrame126("@gr3@Ready to craft. Right click jewlery to craft.", 19505);
		} else {
			player.getPA().sendFrame126("You need a necklace mould to craft necklaces.", 19505);
		}

		// Amulet mould
		if (ItemAssistant.hasItemInInventory(player, 1595) || player.isInZombiesMinigame()) {
			player.getPA().sendFrame126("@gr3@Ready to craft. Right click jewlery to craft.", 19506);
		} else {
			player.getPA().sendFrame126("You need an amulet mould to craft amulets.", 19506);
		}
	}

	/**
	 * Ingredient, unstrung amulet, level, experience
	 */
	private final static int[][] unStrungAmuletData =
			{

					// Ring
					{2357, 1635, 5, 15},
					{1607, 1637, 20, 40},
					{1605, 1639, 27, 55},
					{1603, 1641, 34, 70},
					{1601, 1643, 43, 85},
					{1615, 1645, 55, 100},
					{6573, 6575, 67, 115},

					// Necklace.
					{2357, 1654, 6, 20},
					{1607, 1656, 22, 55},
					{1605, 1658, 29, 60},
					{1603, 1660, 40, 75},
					{1601, 1662, 56, 90},
					{1615, 1664, 72, 105},
					{6573, 6577, 82, 120},

					// Amulets.
					{2357, 1673, 8, 30},
					{1607, 1675, 24, 65},
					{1605, 1677, 31, 70},
					{1603, 1679, 50, 85},
					{1601, 1681, 70, 100},
					{1615, 1683, 80, 150},
					{6573, 6579, 90, 165}
			};


	public static enum StringAmuletData {
		GOLD(1673, 1692),
		SAPPHIRE(1675, 1694),
		EMERALD(1677, 1696),
		RUBY(1679, 1698),
		DIAMOND(1681, 1700),
		DRAGONSTONE(1683, 1702),
		ONYX(6579, 6581);

		private int unstrungAmulet, strungAmulet;

		private StringAmuletData(final int unstrungAmulet, final int strungAmulet) {
			this.unstrungAmulet = unstrungAmulet;
			this.strungAmulet = strungAmulet;
		}

		public int getUnStrungAmulet() {
			return unstrungAmulet;
		}

		public int getStrungAmulet() {
			return strungAmulet;
		}
	}
}
