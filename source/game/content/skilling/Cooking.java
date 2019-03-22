package game.content.skilling;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.content.skilling.fishing.FishingOld;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class Cooking {

	public enum CookableFood {
		// Meat & fish
		BEEF(2132, 2146, 2142, 1, 30, false),
		RAT_MEAT(2134, 2146, 2142, 1, 30, false),
		BEAR_MEAT(2136, 2146, 2142, 1, 30, false),
		CHICKEN(2138, 2144, 2140, 1, 30, false),
		SHRIMPS(317, 323, 315, 1, 30, false),
		SARDINE(327, 369, 325, 1, 40, false),
		ANCHOVIES(321, 323, 319, 1, 30, false),
		HERRING(345, 357, 347, 5, 50, false),
		MACKEREL(353, 357, 355, 10, 60, false),
		BIRD_MEAT(9978, 9982, 9980, 11, 62, false),
		TROUT(335, 343, 333, 15, 70, false),
		COD(341, 343, 339, 18, 75, false),
		PIKE(9978, 343, 351, 20, 80, false),
		SALMON(331, 343, 329, 25, 90, false),
		TUNA(359, 367, 361, 30, 100, false),
		KARAMBWAN(3142, 3148, 3144, 30, 190, false),
		LOBSTER(377, 381, 379, 40, 120, false),
		BASS(363, 367, 365, 43, 130, false),
		SWORDFISH(371, 375, 373, 45, 140, false),
		MONKFISH(7944, 7948, 7946, 62, 150, false),
		SHARK(383, 387, 385, 80, 210, false),
		SEA_TURTLE(395, 399, 397, 82, 211, false),
		ANGLERFISH(13439, 13443, 13441, 84, 260, false),
		DARK_CRAB(11934, 11938, 11936, 90, 250, false),
		MANTA_RAY(389, 393, 391, 91, 216, false),

		// Pies
		REDBERRY_PIE(2321, 2329, 2325, 10, 78, true),
		MEAT_PIE(2319, 2329, 2327, 20, 104, true),
		MUD_PIE(7168, 2329, 7170, 29, 128, true),
		APPLE_PIE(2317, 2329, 2323, 30, 130, true),
		GARDEN_PIE(7176, 2329, 7178, 34, 128, true),
		FISH_PIE(7186, 2329, 7188, 47, 164, true),
		BOTANICAL_PIE(19656, 2329, 19662, 52, 180, true),
		ADMIRAL_PIE(7196, 2329, 7198, 70, 210, true),
		WILD_PIE(7206, 2329, 7208, 85, 240, true),
		SUMMER_PIE(7216, 2329, 7218, 95, 260, true),

		// Misc
		PLAIN_PIZZA(2287, 2305, 2289, 35, 143, true),
		BAKED_POTATO(1942, 6699, 6701, 7, 15, true),
		CAKE(1889, 1903, 1891, 40, 180, true);

		private int rawId, burntId, cookedId, level, xp;

		private boolean rangeOnly;

		private CookableFood(int rawId, int burntId, int cookedId, int level, int xp, boolean rangeOnly) {
			this.rawId = rawId;
			this.burntId = burntId;
			this.cookedId = cookedId;
			this.level = level;
			this.xp = xp;
			this.rangeOnly = rangeOnly;
		}

		public int getRawId() {
			return rawId;
		}

		public int getBurntId() {
			return burntId;
		}

		public int getCookedId() {
			return cookedId;
		}

		public int getLevel() {
			return level;
		}

		public int getXp() {
			return xp;
		}

		public boolean rangeOnly() {
			return rangeOnly;
		}
	}

	public static void cookThisFood(Player p, int itemId, int object) {
		switch (itemId) {
			// Raw beef
			case 2132:
				cookFish(p, itemId, 30, 1, 2146, 2142, object);
				break;

			// Raw rat
			case 2134:
				cookFish(p, itemId, 30, 1, 2146, 2142, object);
				break;

			// Raw bear
			case 2136:
				cookFish(p, itemId, 30, 1, 2146, 2142, object);
				break;

			// Raw chicken.
			case 2138:
				cookFish(p, itemId, 30, 1, 2144, 2140, object);
				break;

			// Raw shrimp
			case 317:
				cookFish(p, itemId, 30, 1, 323, 315, object);
				break;

			// Raw sardine
			case 327:
				cookFish(p, itemId, 40, 1, 369, 325, object);
				break;

			// Raw anchovy
			case 321:
				cookFish(p, itemId, 30, 1, 323, 319, object);
				break;

			// Raw herring
			case 345:
				cookFish(p, itemId, 50, 5, 357, 347, object);
				break;

			// Raw mackerel
			case 353:
				cookFish(p, itemId, 60, 10, 357, 355, object);
				break;

			// Raw bird meat.
			case 9978:
				cookFish(p, itemId, 62, 11, 9982, 9980, object);
				break;

			// Raw trout
			case 335:
				cookFish(p, itemId, 70, 15, 343, 333, object);
				break;

			// Raw cod
			case 341:
				cookFish(p, itemId, 75, 18, 343, 339, object);
				break;

			// Raw pike
			case 349:
				cookFish(p, itemId, 80, 20, 343, 351, object);
				break;

			// Raw Salmon
			case 331:
				cookFish(p, itemId, 90, 25, 343, 329, object);
				break;

			// Raw tuna
			case 359:
				cookFish(p, itemId, 100, 30, 367, 361, object);
				break;

			// Raw karambwan
			case 3142:
				cookFish(p, itemId, 190, 30, 3148, 3144, object);
				break;

			// Raw lobster
			case 377:
				cookFish(p, itemId, 120, 40, itemId + 4, itemId + 2, object);
				break;

			// Raw bass
			case 363:
				cookFish(p, itemId, 130, 43, 367, 365, object);
				break;

			// Raw swordfish
			case 371:
				cookFish(p, itemId, 140, 45, 375, 373, object);
				break;

			// Raw monkfish
			case 7944:
				cookFish(p, itemId, 150, 62, itemId + 4, itemId + 2, object);
				break;

			// Raw shark
			case 383:
				cookFish(p, itemId, 210, 80, itemId + 4, itemId + 2, object);
				break;

			// Raw turtle
			case 395:
				cookFish(p, itemId, 211, 82, 399, 397, object);
				break;

			// Raw anglerfish
			case 13439:
				cookFish(p, itemId, 260, 84, itemId + 4, itemId + 2, object);
				break;

			// Raw dark crab.
			case 11934:
				cookFish(p, itemId, 250, 90, itemId + 4, itemId + 2, object);
				break;

			// Raw manta ray
			case 389:
				cookFish(p, itemId, 216, 91, 393, 391, object);
				break;

			default:
				p.playerAssistant.sendMessage("You cannot cook this.");
				break;
		}
	}

	public static int getCookingAnimation(int objectId) {
		if (Firemaking.isCookableFireObject(objectId)) {
			return 897;
		}
		return 896;
	}

	public static void cookFish(Player player, int itemID, int xpRecieved, int levelRequired, int burntFish, int cookedFish, int object) {
		if (!Skilling.hasRequiredLevel(player, 7, levelRequired, "cooking", "cook this")) {
			return;
		}
		player.playerSkillProp[7][0] = itemID;
		player.playerSkillProp[7][1] = xpRecieved;
		player.playerSkillProp[7][2] = levelRequired;
		player.playerSkillProp[7][3] = burntFish;
		player.playerSkillProp[7][4] = cookedFish;
		player.playerSkillProp[7][5] = object;
		int item = ItemAssistant.getItemAmount(player, player.playerSkillProp[7][0]);
		if (item == 1) {
			player.doAmount = 1;
			cookTheFish(player);
			return;
		}
		viewCookInterface(player, itemID);
	}

	public static void getAmount(Player player, int amount) {
		int item = ItemAssistant.getItemAmount(player, player.playerSkillProp[7][0]);
		if (amount > item) {
			amount = item;
		}
		player.doAmount = amount;
		cookTheFish(player);
	}

	public static void resetCooking(Player c) {
		for (int i = 0; i < 6; i++) {
			c.playerSkillProp[7][i] = -1;
		}
	}

	private static void viewCookInterface(Player player, int item) {
		player.getPA().sendFrame164(1743);
		player.getPA().sendFrame246(13716, 190, item);
		player.getPA().sendFrame126("\\n\\n\\n\\n\\n" + ItemAssistant.getItemName(item) + "", 13717);
	}

	private static void cookTheFish(final Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (player.playerSkillProp[7][0] == 3142) {
			if (!player.isExtremeDonator()) {
				player.getPA().sendMessage("You need to be at least an " + DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.EXTREME_DONATOR) + "Extreme Donator to do this!");
				return;
			}
		}
		player.getPA().closeInterfaces(true);

		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		/*// Will bug out if uncommented, need to find a way.
		if (Cooking.stopCookingCycle(player))
		{
			return;
		}
		*/
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				if (Cooking.stopCookingCycle(player)) {
					container.stop();
					return;
				}
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 4);
	}

	private static boolean stopCookingCycle(final Player player) {
		if (!ItemAssistant.hasItemAmountInInventory(player, player.playerSkillProp[7][0], 1) || player.doAmount <= 0) {
			return true;
		}
		int counter = 0;
		if (Skilling.forceStopSkillingEvent(player)) {
			return true;
		}
		if (player.playerSkillProp[7][4] <= 0) {
			return true;
		}
		if (player.getObjectId() == Firemaking.FIRE_OBJECT_ID) {
			if (!Firemaking.fireExists(player.fireX, player.fireY, player.getHeight())) {
				player.playerAssistant.sendMessage("The fire has run out.");
				return true;
			}
		}
		if (counter == 0) {
			player.startAnimation(getCookingAnimation(player.getObjectId()));
			SoundSystem.sendSound(player, 357, 0);
		}
		counter++;
		if (player.playerSkillProp[7][0] == FishingOld.RAW_SHARK) {
			Achievements.checkCompletionMultiple(player, "1037");
		}
		ItemAssistant.deleteItemFromInventory(player, player.playerSkillProp[7][0], ItemAssistant.getItemSlot(player, player.playerSkillProp[7][0]), 1);
		double burnPercentage = (double) player.baseSkillLevel[ServerConstants.COOKING] - (double) player.playerSkillProp[7][2];
		burnPercentage = 30.0 - burnPercentage;
		if (burnPercentage > 20.0 && player.baseSkillLevel[ServerConstants.COOKING] >= 80) {
			burnPercentage = 20.0;
		}
		// Cooking gauntlets.
		if (ItemAssistant.hasItemEquipped(player, 775)) {
			burnPercentage *= 0.5;
		}
		if (burnPercentage <= 0) {
			burnPercentage = 0;
		}
		if (Skilling.hasMasterCapeWorn(player, 9801)) {
			burnPercentage = 0;
		}
		if (counter == 3) {
			player.startAnimation(getCookingAnimation(player.getObjectId()));
			SoundSystem.sendSound(player, 357, 0);
			counter = 1;
		}
		SoundSystem.sendSound(player, 357, 0);
		if (!Misc.hasPercentageChance((int) burnPercentage)) {
			// Shrimp.
			if (player.playerSkillProp[7][4] == 315) {
				Achievements.checkCompletionSingle(player, 1022);
			}
			player.playerAssistant.sendFilterableMessage("You successfully cook the " + ItemAssistant.getItemName(player.playerSkillProp[7][0]).toLowerCase() + ".");
			Skilling.addSkillExperience(player, (int) (player.playerSkillProp[7][1] * 0.95), 7, false);
			ItemAssistant.addItem(player, player.playerSkillProp[7][4], 1);
			player.startAnimation(getCookingAnimation(player.getObjectId()));
			Skilling.addHarvestedResource(player, player.playerSkillProp[7][4], 1);
			player.skillingStatistics[SkillingStatistics.FISH_COOKED]++;
		} else {
			player.playerAssistant.sendFilterableMessage("You burn the " + ItemAssistant.getItemName(player.playerSkillProp[7][0]).toLowerCase() + ".");
			ItemAssistant.addItem(player, player.playerSkillProp[7][3], 1);
		}
		Skilling.deleteTime(player);
		if (!ItemAssistant.hasItemAmountInInventory(player, player.playerSkillProp[7][0], 1) || player.doAmount <= 0) {
			return true;
		}
		return false;
	}

	public static void rawBeef(Player player) {
		player.getPA().sendMessage(":packet:senditemchat 8870 0 -12");
		player.getPA().sendMessage(":packet:senditemchat 8869 0 -10");
		player.getPA().sendFrame246(8869, 190, 2142);
		player.getPA().sendFrame246(8870, 210, 9436);
		player.getPA().sendFrame126("" + ItemAssistant.getItemName(2142) + "", 8874);
		player.getPA().sendFrame126("" + ItemAssistant.getItemName(9436) + "", 8878);
		player.getPA().sendFrame164(8866);

	}

	public static void cookSinew(final Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		player.getPA().closeInterfaces(true);
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				if (player.getObjectId() == Firemaking.FIRE_OBJECT_ID) {
					if (!Firemaking.fireExists(player.fireX, player.fireY, player.getHeight())) {
						player.playerAssistant.sendMessage("The fire has run out.");
						container.stop();
						return;
					}
				}
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, 2132, 1)) {
					player.getDH().sendStatement("You have run out of " + ItemAssistant.getItemName(2132) + ".");
					container.stop();
					return;
				}
				ItemAssistant.deleteItemFromInventory(player, 2132, 1);
				ItemAssistant.addItem(player, 9436, 1);
				Skilling.addSkillExperience(player, 3, ServerConstants.COOKING, false);
				SoundSystem.sendSound(player, 357, 0);
				player.startAnimation(getCookingAnimation(player.getObjectId()));
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);
	}
}
