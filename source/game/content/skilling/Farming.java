package game.content.skilling;

import core.GameType;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class Farming {

	private final static int[] VALID_SEEDS =
			{5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304};

	private final static int[] HERBS =
			{199, 201, 203, 205, 207, 3049, 209, 211, 213, 3051, 215, 2485, 217, 219};

	private final static int[] SEED_PLANT_EXP =
			{11, 14, 16, 22, 27, 34, 43, 55, 69, 81, 93, 105, 114, 129};

	private final static int[] HERB_EXPS =
			{11, 14, 16, 22, 27, 34, 43, 55, 69, 81, 93, 105, 114, 129};

	private final static int[] FARMING_REQS =
			{1, 14, 19, 26, 32, 38, 44, 50, 56, 62, 67, 73, 79, 85};

	public final static int PATCH_HERBS = 8143;

	private final static int RAKE = 5341;

	private final static int SEED_DIBBER = 5343;

	/**
	 * Load the farming configuration.
	 */
	public static void loadFarmingSettings() {

		/*
		 // Farming patch at Catherby
		if (Config.ECO) {
			patchX = 2813;
			patchY = 3463;
			patchCleanObject = 8132;
			patchWeedObject = 8151;
			patchLoopAmount = 2;
			herbMinimumWield = 2;
			herbMaximumWield = 6;
			herbSkillCapeBonusWield = 2;
			farmingLocationMiddleX = 2813;
			farmingLocationMiddleY = 3463;
		}
		*/
		patchX = 2809;
		patchY = 3335;
		patchCleanObject = 8133;
		patchWeedObject = 8174;
		patchLoopAmount = 4;
		herbMinimumWield = 2;
		herbMaximumWield = 4;
		herbSkillCapeBonusWield = 1;
		farmingLocationMiddleX = 2813;
		farmingLocationMiddleY = 3338;
		rakeTooFarAwayBeginX = 2810;
		rakeTooFarAwayEndX = 2811;
		rakeTooFarAwayBeginY = 3336;
		rakeTooFarAwayEndY = 3337;
	}

	public static int patchX;

	public static int patchY;

	public static int patchCleanObject;

	public static int patchWeedObject;

	public static int patchLoopAmount;

	private static int herbMinimumWield;

	private static int herbMaximumWield;

	private static int herbSkillCapeBonusWield;

	private static int rakeTooFarAwayBeginX;

	private static int rakeTooFarAwayEndX;

	private static int rakeTooFarAwayBeginY;

	private static int rakeTooFarAwayEndY;

	private static int farmingLocationMiddleX;

	private static int farmingLocationMiddleY;

	public static void updateOnRegionChange(Player player) {
		if (player.playerAssistant.distanceToPoint(farmingLocationMiddleX, farmingLocationMiddleY) > 60) {
			return;
		}
		if (player.seedWatered) {
			updateHerbPatch(player, false);
		} else if (player.patchRaked) {
			cleanPatch(player, true);
		}
	}

	public static boolean checkItemOnObject(Player player, int itemId, int objectId) {
		for (int j = 0; j < VALID_SEEDS.length; j++) {
			if (itemId == VALID_SEEDS[j] && objectId == patchCleanObject) {
				plantSeed(player, VALID_SEEDS[j], HERBS[j], HERB_EXPS[j], j);
				return true;
			}
		}
		if (objectId == patchCleanObject) {
			if (player.seedWatered) {
				player.getPA().sendMessage("Patch has already been watered.");
				return true;
			}
			if (System.currentTimeMillis() - player.farmingStageDelay <= 2000) {
				return true;
			}
			player.farmingStageDelay = System.currentTimeMillis();
			if (!hasValidWateringCan(player, itemId)) {
				return true;
			}
			waterSeed(player);
			return true;
		} else if (itemId == RAKE && objectId == patchWeedObject) {
			rakePatch(player);
			return true;
		}
		return false;

	}

	private final static int[] useableWateringCans =
			{5333, 5334, 5335, 5336, 5337, 5338, 5339, 5340};

	private static boolean hasValidWateringCan(Player player, int itemId) {
		if (itemId == 5331) {
			player.playerAssistant.sendMessage("Your watering can is empty.");
			return false;
		}
		int wateringCanId = 0;
		for (int index = 0; index < useableWateringCans.length; index++) {
			if (ItemAssistant.hasItemInInventory(player, useableWateringCans[index])) {
				wateringCanId = useableWateringCans[index];
				break;
			}
		}
		if (wateringCanId == 0) {
			player.playerAssistant.sendMessage("You do not have a watering can.");
			return false;
		}
		int slot = ItemAssistant.getItemSlot(player, wateringCanId);
		ItemAssistant.deleteItemFromInventory(player, wateringCanId, 1);

		// If watering can is the last dose, subtract by one because if not, it will give a noted watering can instead.
		if (wateringCanId == 5333) {
			wateringCanId = 5332;
		}
		ItemAssistant.addItemToInventory(player, wateringCanId - 1, 1, slot, true);
		return true;
	}

	private static void plantSeed(Player player, int seedId, int herbId, int exp, int slot) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (player.baseSkillLevel[ServerConstants.FARMING] < FARMING_REQS[slot]) {
			player.getDH().sendStatement("You need a farming level of " + FARMING_REQS[slot] + " to farm these herbs.");
		} else if (!player.seedPlanted && player.patchRaked && ItemAssistant.hasItemAmountInInventory(player, seedId, 1) && ItemAssistant
				                                                                                                                    .hasItemAmountInInventory(player, SEED_DIBBER,
				                                                                                                                                              1)) {
			if (System.currentTimeMillis() - player.farmingStageDelay <= 3000) {
				return;
			}

			// Snap dragon seed.
			if (seedId == 5300) {
				Achievements.checkCompletionSingle(player, 1030);
			}
			// Snap dragon seed.
			else if (seedId == 5304) {
				Achievements.checkCompletionMultiple(player, "1066 1129");
			}
			player.turnPlayerTo(2812, player.getY());
			player.farmingStageDelay = System.currentTimeMillis();
			ItemAssistant.deleteItemFromInventory(player, seedId, ItemAssistant.getItemSlot(player, seedId), 1);
			Skilling.addSkillExperience(player, (int) (SEED_PLANT_EXP[slot]), ServerConstants.FARMING, false);
			player.startAnimation(2291);
			int herbAmount = Misc.random(herbMinimumWield, herbMaximumWield);
			player.farm[0] = herbId;
			player.farm[1] = herbAmount;
			player.playerAssistant.sendFilterableMessage("You plant your seed.");
			player.skillingStatistics[SkillingStatistics.SEEDS_PLANTED]++;
			player.seedPlanted = true;
		} else if (!player.seedPlanted && !ItemAssistant.hasItemInInventory(player, SEED_DIBBER)) {
			player.playerAssistant.sendMessage("You need a seed dibber to plan the seed.");
		} else if (player.seedPlanted) {
			player.playerAssistant.sendMessage("You need to water your plant");
		}
	}

	private static void waterSeed(Player player) {
		if (player.seedPlanted && !player.seedWatered) {
			player.playerAssistant.sendFilterableMessage("You use the watering can on the patch.");
			player.startAnimation(2293);

			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					container.stop();
				}

				@Override
				public void stop() {
					updateHerbPatch(player, false);
				}
			}, 4);
			player.seedWatered = true;
		} else {
			player.playerAssistant.sendMessage("You must plant a seed before you can water the patch.");
		}
	}

	public static int getExp(Player player) {
		for (int j = 0; j < HERBS.length; j++) {
			if (HERBS[j] == player.farm[0]) {
				return (int) (HERB_EXPS[j]);
			}
		}
		return 0;
	}

	private static void cleanPatch(Player player, boolean force) {
		if (!player.patchCleaned || force) {
			for (int i = 0; i < patchLoopAmount; i++) {
				for (int a = 0; a < patchLoopAmount; a++) {
					player.getPA().spawnClientObject(patchCleanObject, patchX + i, patchY + a, -1, 10);
				}
			}
			if (!force) {
				player.patchCleaned = true;
			}
		} else {
			player.playerAssistant.sendMessage("You have already cleaned the patch.");
		}
	}

	public static void updateHerbPatch(Player player, boolean clear) {
		if (clear) {
			for (int i = 0; i < patchLoopAmount; i++) {
				for (int a = 0; a < patchLoopAmount; a++) {
					player.getPA().spawnClientObject(patchWeedObject, patchX + i, patchY + a, -1, 10);
				}
			}
			player.patchRaked = false;
			player.seedWatered = false;
			player.seedPlanted = false;
			player.patchCleaned = false;
			player.farmingXCoordinate = 0;
			player.farmingYCoordinate = 0;
		} else {

			for (int i = 0; i < patchLoopAmount; i++) {
				for (int a = 0; a < patchLoopAmount; a++) {
					player.getPA().spawnClientObject(PATCH_HERBS, patchX + i, patchY + a, -1, 10);
				}
			}
		}
	}

	public static void rakePatch(Player player) {
		if (!ItemAssistant.hasItemInInventory(player, RAKE)) {
			player.getPA().sendMessage("You need a rake to do this.");
			return;
		}
		if (player.patchRaked) {
			player.getPA().sendMessage("Patch has already been raked.");
			return;
		}
		if (GameType.isOsrsPvp()) {
			if (player.getObjectX() >= rakeTooFarAwayBeginX && player.getObjectY() >= rakeTooFarAwayBeginY && player.getObjectX() <= rakeTooFarAwayEndX
			    && player.getObjectY() <= rakeTooFarAwayEndY) {
				player.getPA().sendMessage("Too far away.");
				return;
			}
		}
		player.turnPlayerTo(2812, player.getY());
		player.farmingStageDelay = System.currentTimeMillis();
		player.farmingXCoordinate = 2813;
		player.farmingYCoordinate = 3337;
		if (!player.patchRaked) {
			player.startAnimation(2273);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					container.stop();
				}

				@Override
				public void stop() {
					cleanPatch(player, false);
				}
			}, 6);
			player.patchRaked = true;
		} else {
			player.playerAssistant.sendMessage("You must plant and water a seed before you can rake here!");
		}
		player.playerAssistant.sendFilterableMessage("You rake the patch.");
	}

	public static void pickHerb(final Player player) {
		if (player.farmingEvent) {
			return;
		}
		if (System.currentTimeMillis() - player.farmingStageDelay <= 2500) {
			return;
		}
		player.turnPlayerTo(2812, player.getY());
		player.isFarming = true;
		player.farmingStageDelay = System.currentTimeMillis();
		player.startAnimation(2282);
		player.farmingEvent = true;

		if (ItemAssistant.getFreeInventorySlots(player) == 0) {

			player.farmingEvent = false;
			player.isFarming = false;
			return;
		}

		if (player.farm[0] <= 0 && player.farm[1] <= 0) {
			player.farmingEvent = false;
			player.isFarming = false;
			return;
		}

		int chance = 0;

		ItemAssistant.addItem(player, player.farm[0], 1 * (Misc.hasPercentageChance(chance) ? 2 : 1));
		SoundSystem.sendSound(player, 358, 500);
		double multiplier = 0.55;
		int experience = (int) (getExp(player) * multiplier);
		Skilling.addSkillExperience(player, experience, ServerConstants.FARMING, false);
		Skilling.petChance(player, experience, (int) (129 * multiplier), 4700, ServerConstants.FARMING, null);
		player.farm[1]--;
		if (player.farm[1] == 0) {
			player.farm[0] = -1;
			updateHerbPatch(player, true);
			player.farmingEvent = false;
			player.isFarming = false;
			return;
		}

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (!player.isFarming) {
					container.stop();
					return;
				}
				if (!player.farmingEvent) {
					container.stop();
					return;
				}
				if (player.farm[0] <= 0 && player.farm[1] <= 0) {
					container.stop();
					return;
				}
				if (ItemAssistant.getFreeInventorySlots(player) == 0) {
					container.stop();
					return;
				}

				int chance = 0;
				ItemAssistant.addItem(player, player.farm[0], 1 * (Misc.hasPercentageChance(chance) ? 2 : 1));
				if (Skilling.hasMasterCapeWorn(player, 9810) && Misc.hasPercentageChance(10)) {
					ItemAssistant.addItemToInventoryOrDrop(player, player.farm[0], 1);
					player.getPA().sendMessage("<col=a54704>Your cape allows you to pick an extra herb.");
				}
				SoundSystem.sendSound(player, 358, 500);
				Skilling.addSkillExperience(player, experience, ServerConstants.FARMING, false);
				Skilling.petChance(player, experience, (int) (129 * multiplier), 4700, ServerConstants.FARMING, null);
				player.farm[1]--;
				if (player.farm[1] == 0) {
					player.farm[0] = -1;
					updateHerbPatch(player, true);
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.farmingEvent = false;
				player.isFarming = false;
			}
		}, 2);

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (!player.isFarming) {
					container.stop();
					return;
				}
				if (!player.farmingEvent) {
					container.stop();
					return;
				}
				player.startAnimation(2282);
				player.playerAssistant.sendFilterableMessage("You pick some herbs.");
			}

			@Override
			public void stop() {
			}
		}, 4);

	}

	public static boolean isFarmingObject(Player player, int objectType) {
		if (player.getTransformed() > 0) {
			return false;
		}
		switch (objectType) {

			case PATCH_HERBS:
				if (player.farm[0] > 0 && player.farm[1] > 0) {
					pickHerb(player);
				}
				player.turnPlayerTo(player.getObjectX(), player.getObjectY());
				return true;

			default:
				player.farmingEvent = false;
				return false;
		}
	}
}
