package game.content.skilling.thieving;

import core.GameType;
import core.ServerConstants;
import game.content.donator.DonatorContent;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Thieving stalls.
 *
 * @author MGT Madness, created on 29-11-2015.
 */
public class Stalls {


	public static enum StallData {
		BANANA_STALL(1, 20, 1963, 4875, 2),
		GENERAL_STALL(30, 45, 2946, 4876, 3),
		CRAFTING_STALL(50, 60, 1633, 4874, 4),
		RUNES_STALL(65, 100, 9594, 4877, 6),
		SCIMITAR_STALL(85, 160, 6721, 4878, 7);

		private int levelRequirement;

		private int experience;

		private int itemStolen;

		private int objectId;

		private int damage;


		private StallData(int levelRequirement, int experience, int itemStolen, int objectId, int damage) {
			this.levelRequirement = levelRequirement;
			this.experience = experience;
			this.itemStolen = itemStolen;
			this.objectId = objectId;
			this.damage = damage;
		}

		public int getLevelRequirement() {
			return levelRequirement;
		}

		public int getExperience() {
			return experience;
		}

		public int getItemStolen() {
			return itemStolen;
		}

		public int getObjectId() {
			return objectId;
		}

		public int getDamage() {
			return damage;
		}
	}

	public static boolean isStallObject(Player player, int objectId) {
		for (StallData data : StallData.values()) {
			if (objectId == data.getObjectId()) {
				stealFromStall(player, data);
				return true;
			}
		}
		return false;
	}

	public static void bloodMoneyStall(Player player) {
		String averagePerHour = GameType.isOsrsPvp() ? "35k" : "26m";
		if (!DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.LEGENDARY_DONATOR)) {
			player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.LEGENDARY_DONATOR) + " Legendary Donators can use the " + ServerConstants.getMainCurrencyName().toLowerCase() + " stall for " + averagePerHour + "/h!");
			return;
		}
		if (System.currentTimeMillis() - player.stoleFromStallTime < 2100) {
			return;
		}

		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.playerAssistant.sendMessage("You need at least 1 inventory space.");
			return;
		}
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (player.baseSkillLevel[ServerConstants.THIEVING] < 90) {
			player.getDH().sendStatement("You need a thieving level of 90 to steal from this stall.");
			return;
		}
		if (PickPocket.pickPocketFailure(player, null, "You fail to steal.")) {
			return;
		}
		player.stoleFromStallTime = System.currentTimeMillis();
		player.startAnimation(832);
		player.skillingStatistics[SkillingStatistics.STALLS_THIEVED]++;
		int amount = GameType.isOsrsPvp() ? Misc.random(17, 37) : Misc.random(12750, 27750);
		ItemAssistant.addItemToInventoryOrDrop(player, ServerConstants.getMainCurrencyId(), amount);
		player.playerAssistant.sendFilterableMessage("You steal " + amount + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " from the stall.");
		Skilling.addSkillExperience(player, 315, ServerConstants.THIEVING, false);
		Skilling.petChance(player, 315, 315, 11520, ServerConstants.THIEVING, null);
		CoinEconomyTracker.addIncomeList(player, "DONATOR_STALL " + amount);

	}

	/**
	 * Steal from the stall.
	 *
	 * @param player The associated player.
	 * @param data The Stall enum data.
	 */
	private static void stealFromStall(Player player, StallData data) {
		if (!hasStallRequirements(player, data)) {
			return;
		}
		if (GameType.isOsrsPvp() && !DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.SUPER_DONATOR)) {
			return;
		}

		player.stoleFromStallTime = System.currentTimeMillis();
		player.startAnimation(832);
		giveItemFromStallEvent(player, data);
		player.skillingStatistics[SkillingStatistics.STALLS_THIEVED]++;

	}

	public static enum StallItems {

		BANANA(1963, 1000),
		GOLDEN_TINDERBOX(2946, 1500),
		CRUSHED_GEM(1633, 2500),
		GROUND_MUD_RUNE(9594, 3500),
		RUSTY_SCIMITAR(6721, 6500);

		public int itemId;

		public int value;

		StallItems(final int itemId, final int value) {
			this.itemId = itemId;
			this.value = value;
		}

		public int getItemId() {
			return itemId;
		}

		public int getValue() {
			return value;
		}
	}

	/**
	 * True if the player has the requirements to use the stall.
	 *
	 * @param player The associated player.
	 * @param data The Stall enum data.
	 */
	private static boolean hasStallRequirements(Player player, StallData data) {
		if (player.baseSkillLevel[ServerConstants.THIEVING] < data.getLevelRequirement()) {
			player.getDH().sendStatement("You need a thieving level of " + data.getLevelRequirement() + " to steal from this stall.");
			return false;
		}

		if (System.currentTimeMillis() - player.stoleFromStallTime < 2100) {
			return false;
		}

		if (PickPocket.pickPocketFailure(player, null, "You fail to steal.")) {
			return false;
		}

		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.playerAssistant.sendMessage("You need at least 1 inventory space.");
			return false;
		}

		return true;

	}

	/**
	 * Give the item stolen from the stall.
	 *
	 * @param player The associated player.
	 * @param data The Stall enum data.
	 */
	private static void giveItemFromStallEvent(final Player player, final StallData data) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				giveItemFromStallAction(player, data);
			}
		}, 1);
	}

	public static void giveItemFromStallAction(Player player, StallData data) {
		int amount = 1;
		int itemId = data.getItemStolen();
		ItemAssistant.addItemToInventoryOrDrop(player, itemId, amount);
		player.playerAssistant.sendFilterableMessage("You steal a " + ItemAssistant.getItemName(itemId) + " from the stall.");
		Skilling.addSkillExperience(player, data.getExperience(), ServerConstants.THIEVING, false);
	}

}
