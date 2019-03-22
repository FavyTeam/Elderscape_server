package game.content.quicksetup;

import core.GameType;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.bank.BankButtons;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.Poison;
import game.content.combat.Venom;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.EditCombatSkill;
import game.content.miscellaneous.OverlayTimer;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.SpellBook;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import utility.Misc;

/**
 * Quick set-up interface actions.
 *
 * @author MGT Madness, created on 16-03-2015.
 */
public class QuickSetUp {
	/**
	 * Display the quick set up interface.
	 */
	public static void displayInterface(Player player) {
		if (GameMode.getGameMode(player, "ULTIMATE IRON MAN")) {
			player.getDH().sendStatement("As an Ultimate Ironman, you cannot use presets.");
			return;
		}
		Presets.openPresetInterface(player);
		player.getPA().displayInterface(24280);
	}

	/**
	 * True, if the button clicked is a quick set-up button.
	 *
	 * @param player The associated player.
	 * @param buttonId The button identity clicked.
	 */
	public static boolean isQuickSetUpButton(Player player, int buttonId) {
		if (buttonId >= 94226 && buttonId <= 95038 || buttonId >= 89080 && buttonId <= 89112) {
			if (player.getHeight() == 20) {
				return true;
			}
			if (player.getDuelStatus() >= 1) {
				return true;
			}
			if (System.currentTimeMillis() - player.timeSkilled < 90000) {
				int secondsLeft = (int) (90 - ((System.currentTimeMillis() - player.timeSkilled) / 1000));
				player.getPA().sendMessage("You cannot use presets while you just skilled, wait " + secondsLeft + " seconds.");
				return true;
			}
			if (!player.isAdministratorRank()) {
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				if (System.currentTimeMillis() - player.lastQuickSetUpClicked <= 600 && (buttonId >= 94226 && buttonId <= 94254 || buttonId == 95002)) {
					return true;
				}
				InterfaceAssistant.closeDialogueOnly(player);
			} else {
				InterfaceAssistant.closeDialogueOnly(player);
			}
		}
		player.lastQuickSetUpClicked = System.currentTimeMillis();

		// Custom presets buttons
		if (buttonId >= 89080 && buttonId <= 89112) {
			if (GameMode.getGameMode(player, "ULTIMATE IRON MAN")) {
				player.getDH().sendStatement("As an Ultimate Ironman, you cannot use presets.");
				return true;
			}
			int index = 0;
			index = buttonId - 89080;
			index /= 4;
			Presets.presetKit(player, index + 1);
			return true;
		}

		if (GameType.isOsrsEco()) {
			return false;
		}

		switch (buttonId) {

			case 95006:
				vengeanceRunes(player);
				return true;

			case 95010:
				barrageRunes(player);
				return true;

			case 95014:
				teleBlockRunes(player);
				return true;

			case 94226:
				mainMelee(player);
				return true;
			case 94230:
				mainHybrid(player);
				return true;

			case 94234:
				mainTribrid(player);
				return true;

			case 94238:
				berserkerMelee(player);
				return true;

			case 94242:
				berserkerHybrid(player);
				return true;

			case 94246:
				pure(player);
				return true;

			case 94250:
				pureTribrid(player);
				return true;

			case 94254:
				rangedTank(player);
				return true;

			case 95002:
				f2pRanged(player);
				return true;
		}
		return false;
	}

	//@formatter:off
	private static int[] randomTeamCape = {4315, 4317, 4319, 4321, 4323, 4325, 4327, 4329, 4331, 4333, 4335, 4337, 4339, 4341, 4343, 4345, 4347, 4349, 4351, 4353, 4355, 4357, 4359,
			4361, 4363, 4365, 4367, 4369, 4371, 4373, 4375, 4377, 4379, 4381, 4383, 4385, 4387, 4389, 4391, 4393, 4395, 4397, 4399, 4401, 4403, 4405, 4407, 4409, 4411, 4413
	};

	private static int[] randomGodCape = {2412, 2413, 2414};

	private static int[] randomMysticTop = {4091, 4101, 4111};

	private static int[] randomMysticBottom = {4093, 4103, 4113};
	//@formatter:on

	public static int getRandomMysticBottom() {
		return randomMysticBottom[Misc.random(randomMysticBottom.length - 1)];
	}

	public static int getRandomMysticTop() {
		return randomMysticTop[Misc.random(randomMysticTop.length - 1)];
	}

	/**
	 * @param player The associated player.
	 * @return Get a random team cape.
	 */
	public static int getTeamCape(boolean fireCape) {
		int random = 5;
		if (fireCape) {
			random = 6;
		}
		random = Misc.random(1, random);
		if (random <= 5) {
			return randomTeamCape[Misc.random(randomTeamCape.length - 1)];
		} else {
			// Fire cape.
			return 6570;
		}
	}

	public static int getMainMeleeHelmetBot(boolean berserker) {
		if (Misc.hasPercentageChance(80)) {
			return berserker ? 3751 : 10828; // Helm of neitiznot.
		} else {
			return 10548; // Fighter hat.
		}
	}

	public static int getMeleeAmuletBot() {
		if (Misc.hasPercentageChance(30)) {
			return 1725; // Amulet of strength.
		} else {
			return 1712; // Amulet of glory(4).
		}
	}

	public static int getMeleePlatebodyBot() {
		int random = Misc.random(1, 2);
		switch (random) {
			case 1:
				return 1127; // Rune platebody.
			case 2:
				return 10551; // Fighter torso.
		}
		return -1;
	}

	/**
	 * Bank inventory and equipment.
	 *
	 * @param player The associated player.
	 */
	public static void bankInventoryAndEquipment(Player player) {
		player.bankIsFullWhileUsingPreset = false;
		BankButtons.depositInventoryItems(player, false);
		BankButtons.depositWornItems(player, false, false, false);
	}



	/**
	 * @param player The associated player.
	 * @param array The array of items to spawn into the inventory.
	 */
	public static void spawnInventory(Player player, int[][] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i][0] <= 1) {
				continue;
			}
			//player.botDebug.add("Added: " + array[i][0]);
			ItemAssistant.addItem(player, array[i][0], array[i][1]);
		}
		player.botDebug.add("Sharks spawned: " + ItemAssistant.getFreeInventorySlots(player));
		ItemAssistant.addItem(player, 385, ItemAssistant.getFreeInventorySlots(player));
	}

	/**
	 * Wear the given equipment.
	 *
	 * @param player The associated player.
	 * @param array The equipment array to equip.
	 */
	public static void spawnEquipmentForBot(Player player, int[][] array, boolean spawnInInventory) {
		for (int i = 0; i < array.length; i++) {
			if (spawnInInventory) {
				if (array[i][0] > 1) {
					ItemAssistant.addItem(player, array[i][0], array[i][1]);
				}
			} else {
				ItemAssistant.replaceEquipmentSlot(player, i, array[i][0], array[i][1], false);
			}
		}
	}

	/**
	 * Change spellbook and prayer book.
	 *
	 * @param player The associated player.
	 * @param prayer The prayer book to change into.
	 * @param magic The spellbook to change into.
	 */
	public static void setPrayerAndMagicBook(Player player, String magic) {
		if (magic.equals("ANCIENT")) {
			SpellBook.ancientMagicksSpellBook(player, false);
		} else if (magic.equals("LUNAR")) {
			SpellBook.lunarSpellBook(player, false);
		} else if (magic.equals("MODERN")) {
			SpellBook.modernSpellBook(player, false);
		}
	}

	/**
	 * Change combat skills.
	 *
	 * @param player The associated player.
	 * @param accountType The account type to change into.
	 */
	public static void setCombatSkills(Player player, String accountType, boolean custom, int[] customSkills) {
		int[] skills = new int[7];
		switch (accountType) {
			case "MAIN":
				skills = new int[]
				{99, 99, 99, 99, 99, 99, 99};
				break;
			case "BERSERKER":
				skills = new int[]
				{75, 45, 99, 99, 99, 52, 99};
				break;
			case "F2P":
				skills = new int[]
				{80, 1, 99, 99, 99, 44, 90};
				break;
			case "RANGED TANK":
				skills = new int[]
				{50, 85, 50, 99, 99, 52, 99};
				break;
			case "PURE":
			case "INITIATE":
				skills = new int[]
				{75, accountType.equals("INITIATE") ? 20 : 1, 99, 99, 99, 52, 99};
				break;
		}
		if (customSkills != null) {
			skills = customSkills;
		}
		for (int i = 0; i < 7; i++) {
			if (i != 3) {
				player.skillExperience[i] = Skilling.getExperienceForLevel(skills[i]);
				player.currentCombatSkillLevel[i] = skills[i];
				player.baseSkillLevel[i] = skills[i];
			}
		}
		Combat.resetPrayers(player);
		EditCombatSkill.calculateHitPoints(player);
		for (int i = 0; i < 7; i++) // This for loop has to be after calculateHitPoints, because giving extra
		//experience in attack skill, will give additional hitpoint experience.
		{
			if (i != 3) {
				if (player.baseSkillLevel[i] == 99) {
					player.skillExperience[i] = (player.skillExperience[i] + player.combatExperienceGainedAfterMaxed[i]);
				}
			}
		}

		for (int i = 0; i < 7; i++) {
			player.getPA().setSkillLevel(i, player.baseSkillLevel[i], player.skillExperience[i]);
		}
		Combat.refreshCombatSkills(player);
	}

	/**
	 * Wear the given equipment.
	 *
	 * @param player The associated player.
	 * @param array The equipment array to equip.
	 */
	private static void spawnEquipment(Player player, int[][] array) {
		for (int i = 0; i < array.length; i++) {
			if (player.playerEquipment[i] > 0) {
				continue;
			}
			ItemAssistant.replaceEquipmentSlot(player, i, array[i][0], array[i][1], false);
		}
	}


	/**
	 * Update equipment visuals to the player's client and other information relateing to an equipment change.
	 *
	 * @param player The associated player.
	 */
	public static void updateEquipment(Player player) {
		Combat.updatePlayerStance(player);
	}

	private static long getRestoreSpecialDelay(Player player) {
		if (player.isImmortalDonator()) {
			return 1000;
		}
		if (player.isUberDonator()) {
			return 5000;
		}
		if (player.isUltimateDonator()) {
			return 10000;
		}
		if (player.isLegendaryDonator()) {
			return 20000;
		}
		if (player.isExtremeDonator()) {
			return 30000;
		}
		if (player.isSuperDonator()) {
			return 40000;
		}
		if (player.isDonator()) {
			return 50000;
		}
		return 60000;
	}

	/**
	 * Restore hitpoints and prayer points.
	 *
	 * @param player The associated player.
	 * @param resetPrayers TODO
	 */
	public static void heal(Player player, boolean specialDelay, boolean resetPrayers) {
		// Box of health at clan wars.
		if (player.getObjectX() == 3327 && player.getObjectY() == 4757) {
			specialDelay = false;
		}
		player.chargeSpellTime = 0;
		player.imbuedHeartEndTime = 0;
		player.teleBlockEndTime = 0;
		player.dfsDelay = 0;
		OverlayTimer.stopAllOverlayTimers(player);
		player.lastVeng = 0;
		player.setAntiFirePotionTimer(0);
		player.setFrozenLength(0);
		player.frozenBy = 0;
		player.venomImmunityExpireTime = 0;
		player.poisonImmune = 0;
		player.overloadReboostTicks = 0;
		player.timeClawsOfGuthixAffected = 0;
		player.timeZamorakFlamesAffected = 0;
		player.setHitPoints(player.getBaseHitPointsLevel());
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
		player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
		player.setUsingSpecialAttack(false);
		if (System.currentTimeMillis() - player.timeUsedHealthBoxSpecial >= getRestoreSpecialDelay(player) && specialDelay || !specialDelay) {
			if (specialDelay && player.getSpecialAttackAmount() <= 7.0) {
				player.timeUsedHealthBoxSpecial = System.currentTimeMillis();
			}
			player.setSpecialAttackAmount(10.0, false);
			player.weaponSpecialUpdatedId = player.getWieldedWeapon();
			CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
		} else if (specialDelay) {
			if (player.getSpecialAttackAmount() < 10.0) {
				long seconds = getRestoreSpecialDelay(player) - (System.currentTimeMillis() - player.timeUsedHealthBoxSpecial);
				seconds /= 1000;
				player.getPA().sendMessage("You may restore your special attack in " + seconds + " second" + Misc.getPluralS(seconds) + ".");
			}
		}

		player.setVengeance(false);
		player.runEnergy = 100.0;
		Poison.removePoison(player);
		Venom.removeVenom(player);
		player.timeStaffOfTheDeadSpecialUsed = 0;
		AgilityAssistant.updateRunEnergyInterface(player);
		if (resetPrayers) {
			Combat.resetPrayers(player);
		}
		player.resetDamageTaken();
		Skilling.resetCombatSkills(player);
		for (int i = 0; i < 7; i++) {
			player.getPA().setSkillLevel(i, player.baseSkillLevel[i], player.skillExperience[i]);
		}
		AutoCast.resetAutocast(player, true);
		AgilityAssistant.removeStaminaEffect(player);
		player.antiBoxingData.clear();
	}

	public static int getRandomGodCape() {
		return randomGodCape[Misc.random(randomGodCape.length - 1)];
	}

	//@formatter:off
	private static int[][] inventoryTankTest = {
			{12695, 1},
			{11802, 1},
			{12954, 1},
			{4749, 1},
			{3040, 1},
			{11840, 1},
			{12006, 1},
			{4751, 1},
			{3024, 1},
			{11936, 1},
			{565, 800},
			{4712, 1},
			{3024, 1},
			{3024, 1},
			{3024, 1},
			{560, 1600},
			{6685, 1},
			{6685, 1},
			{12625, 1},
			{555, 2400},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
			{6685, 1},
	};

	private static int[][] equipmentTankTest = {
			{10828, 1},
			{2414, 1},
			{6585, 1},
			{12904, 1},
			{4736, 1},
			{6889, 1},
			{-1, 1},
			{4714, 1},
			{-1, 1},
			{7462, 1},
			{6920, 1},
			{-1, 1},
			{-1, 1},
			{-1, 1},
	};
	//@formatter:on

	public static void tankTestBot(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, inventoryTankTest);
		spawnEquipment(player, equipmentTankTest);
		updateEquipment(player);
		heal(player, false, true);
		setCombatSkills(player, "MAIN", false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
	}

	/**
	 * Apply the main hybrid button action.
	 *
	 * @param player The associated player.
	 */
	public static void mainHybrid(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, HybridMain.inventorySet(player));
		spawnEquipment(player, HybridMain.equipmentSet(player));
		updateEquipment(player);
		heal(player, true, true);
		setCombatSkills(player, "MAIN", false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
		Presets.isPresetFlagged(player, player.bankIsFullWhileUsingPreset);
	}



	/**
	 * Apply the main hybrid button action.
	 *
	 * @param player The associated player.
	 */
	public static void mainHybridTournament(Player player, String type, String stats) {
		//{"Pure tribrid", "Berserker hybrid", "Main hybrid welfare", "Main hybrid barrows"};
		if (type.contains("hybrid") || type.contains("Nh")) {
			spawnInventory(player, TournamentSets.tournamentInventory(player, type));
			spawnEquipment(player, TournamentSets.tournamentEquipment(player, type));
		} else {
			spawnInventory(player, TournamentSets.pureTribridInventory);
			spawnEquipment(player, PureTribrid.getEquipment());
		}
		player.runePouchItemId[0] = 555;
		player.runePouchItemAmount[0] = 3000;
		player.runePouchItemId[1] = 560;
		player.runePouchItemAmount[1] = 2000;
		player.runePouchItemId[2] = 565;
		player.runePouchItemAmount[2] = 1000;
		RunePouch.updateRunePouchMainStorage(player, false);
		updateEquipment(player);
		heal(player, false, true);
		setCombatSkills(player, stats, false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
	}

	/**
	 * Apply the main hybrid button action.
	 *
	 * @param player The associated player.
	 */
	public static void mainDharokTournament(Player player, String type, String stats) {
		spawnInventory(player, TournamentSets.tournamentInventoryDharok(player, type));
		spawnEquipment(player, TournamentSets.tournamentEquipmentDharok(player, type));
		player.runePouchItemId[0] = 557;
		player.runePouchItemAmount[0] = 1000;
		player.runePouchItemId[1] = 9075;
		player.runePouchItemAmount[1] = 400;
		player.runePouchItemId[2] = 560;
		player.runePouchItemAmount[2] = 200;
		RunePouch.updateRunePouchMainStorage(player, false);
		updateEquipment(player);
		heal(player, false, true);
		setCombatSkills(player, stats, false, null);
		setPrayerAndMagicBook(player, "LUNAR");
	}

	public static void pureF2pTournament(Player player, String type, String stats) {
		spawnInventory(player, TournamentSets.tournamentInventoryF2p(player, type));
		spawnEquipment(player, TournamentSets.tournamentEquipmentF2p(player, type));
		updateEquipment(player);
		heal(player, false, true);
		setCombatSkills(player, stats, false, null);
		setPrayerAndMagicBook(player, "MODERN");
	}

	public static void mainBarrowsBridTournament(Player player, String type, String stats) {
		spawnInventory(player, TournamentSets.tournamentInventoryBarrowsBrid(player, type));
		spawnEquipment(player, TournamentSets.tournamentEquipmentBarrowsBrid(player, type));
		player.runePouchItemId[0] = 555;
		player.runePouchItemAmount[0] = 3000;
		player.runePouchItemId[1] = 560;
		player.runePouchItemAmount[1] = 2000;
		player.runePouchItemId[2] = 565;
		player.runePouchItemAmount[2] = 1000;
		RunePouch.updateRunePouchMainStorage(player, false);
		updateEquipment(player);
		heal(player, false, true);
		setCombatSkills(player, stats, false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
	}

	public static void maxBridTournament(Player player, String type, String stats) {
		spawnInventory(player, TournamentSets.tournamentInventoryMaxBrid(player, type));
		spawnEquipment(player, TournamentSets.tournamentEquipmentMaxBrid(player, type));
		player.runePouchItemId[0] = 555;
		player.runePouchItemAmount[0] = 3000;
		player.runePouchItemId[1] = 560;
		player.runePouchItemAmount[1] = 2000;
		player.runePouchItemId[2] = 565;
		player.runePouchItemAmount[2] = 1000;
		RunePouch.updateRunePouchMainStorage(player, false);
		updateEquipment(player);
		heal(player, false, true);
		setCombatSkills(player, stats, false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
	}

	/**
	 * Apply the main melee button action.
	 *
	 * @param player The associated player.
	 */
	public static void mainMelee(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, MeleeMain.inventory);
		spawnEquipment(player, MeleeMain.equipmentSet(player));
		updateEquipment(player);
		heal(player, true, true);
		setCombatSkills(player, "MAIN", false, null);
		setPrayerAndMagicBook(player, "LUNAR");
		Presets.isPresetFlagged(player, player.bankIsFullWhileUsingPreset);
	}

	/**
	 * Apply the main tribrid button action.
	 *
	 * @param player The associated player.
	 */
	private static void mainTribrid(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, TribridMain.inventorySet(player));
		spawnEquipment(player, TribridMain.equipmentSet(player));
		updateEquipment(player);
		heal(player, true, true);
		setCombatSkills(player, "MAIN", false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
		Presets.isPresetFlagged(player, player.bankIsFullWhileUsingPreset);
	}

	/**
	 * Apply the main hybrid button action.
	 *
	 * @param player The associated player.
	 */
	private static void berserkerHybrid(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, BerserkerHybrid.inventorySet(player));
		spawnEquipment(player, BerserkerHybrid.getEquipment());
		updateEquipment(player);
		heal(player, true, true);
		setCombatSkills(player, "BERSERKER", false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
		Presets.isPresetFlagged(player, player.bankIsFullWhileUsingPreset);
	}

	/**
	 * Apply the main hybrid button action.
	 *
	 * @param player The associated player.
	 */
	private static void berserkerMelee(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, BerserkerMelee.inventory);
		spawnEquipment(player, BerserkerMelee.equipmentSet(player));
		updateEquipment(player);
		heal(player, true, true);
		setCombatSkills(player, "BERSERKER", false, null);
		setPrayerAndMagicBook(player, "LUNAR");
		Presets.isPresetFlagged(player, player.bankIsFullWhileUsingPreset);
	}

	/**
	 * Apply the pure button action.
	 *
	 * @param player The associated player.
	 */
	private static void pure(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, Pure.inventory);
		Pure.random(player);
		updateEquipment(player);
		heal(player, true, true);
		setCombatSkills(player, "PURE", false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
		Presets.isPresetFlagged(player, player.bankIsFullWhileUsingPreset);
	}

	/**
	 * Apply the pure tribrid button action.
	 *
	 * @param player The associated player.
	 */
	private static void pureTribrid(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, PureTribrid.inventory);
		spawnEquipment(player, PureTribrid.getEquipment());
		updateEquipment(player);
		heal(player, true, true);
		setCombatSkills(player, "PURE", false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
		Presets.isPresetFlagged(player, player.bankIsFullWhileUsingPreset);
	}

	/**
	 * Apply the ranged button action.
	 *
	 * @param player The associated player.
	 */
	private static void rangedTank(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, RangedTank.inventory);
		spawnEquipment(player, RangedTank.equipmentSet(player));
		updateEquipment(player);
		heal(player, true, true);
		setCombatSkills(player, "RANGED TANK", false, null);
		setPrayerAndMagicBook(player, "LUNAR");
		Presets.isPresetFlagged(player, player.bankIsFullWhileUsingPreset);
	}

	private static void f2pRanged(Player player) {
		bankInventoryAndEquipment(player);
		spawnInventory(player, F2pSet.getInventory(player, true));
		spawnEquipment(player, F2pSet.getEquipment(true));
		updateEquipment(player);
		heal(player, true, true);
		setCombatSkills(player, "MAIN", false, null);
		setPrayerAndMagicBook(player, "MODERN");
		Presets.isPresetFlagged(player, player.bankIsFullWhileUsingPreset);
	}

	/**
	 * True if the player is using f2p only gear.
	 * All equipment must be f2p.
	 * Must not have any stat above the base level, except for strength which can be up to 112.
	 * And must be on modern spellbook.
	 */
	public static boolean isUsingF2pOnly(Player player, boolean message, boolean prayerCheck) {
		if (!message && prayerCheck) {
			if (!ItemAssistant.hasEquipment(player)) {
				return false;
			}
		}
		if (!player.spellBook.equals("MODERN")) {
			if (message) {
				player.getPA().sendMessage("You must be using f2p only to attack this player.");
				player.getPA().sendMessage("Please switch to the modern spellbook.");
			}
			return false;
		}
		for (int index = 0; index < 7; index++) {
			if (index == ServerConstants.STRENGTH) {
				if (player.currentCombatSkillLevel[ServerConstants.STRENGTH] > 112) {
					if (message) {
						player.getPA().sendMessage("You must be using f2p only to attack this player.");
						player.getPA().sendMessage("Please lower your strength level to 112.");
					}
					return false;
				}
				continue;
			}
			if (player.currentCombatSkillLevel[index] > player.baseSkillLevel[index]) {
				if (message) {
					player.getPA().sendMessage("You must be using f2p only to attack this player.");
					player.getPA().sendMessage("Please lower your " + ServerConstants.SKILL_NAME[index] + " to 99.");
				}
				return false;
			}
		}

		int item = 0;
		for (int index = 0; index < player.playerEquipment.length; index++) {
			item = player.playerEquipment[index];
			if (item <= 0) {
				continue;
			}
			if (!ItemDefinition.getDefinitions()[item].f2p) {
				if (message) {
					player.getPA().sendMessage("You must be using f2p only to attack this player.");
					player.getPA().sendMessage("Please bank your " + ItemAssistant.getItemName(item) + ".");
				}
				return false;
			}
		}

		for (int index = 0; index < player.playerItems.length; index++) {

			item = player.playerItems[index] - 1;
			if (item <= 0) {
				continue;
			}
			if (!ItemDefinition.getDefinitions()[item].f2p && !ItemDefinition.getDefinitions()[item].note) {
				if (message) {
					player.getPA().sendMessage("You must be using f2p only to attack this player.");
					player.getPA().sendMessage("Please bank your " + ItemAssistant.getItemName(item) + ".");
				}
				return false;
			}
		}

		if (prayerCheck) {

			for (int index = 0; index < 8; index++) {
				if (player.prayerActive[21 + index]) {
					if (message) {
						player.getPA().sendMessage("You must be using f2p only to attack this player.");
						player.getPA().sendMessage("Please turn off all non-f2p prayers.");
					}
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Spawn vengeance runes.
	 *
	 * @param player The associated player.
	 */
	public static void vengeanceRunes(Player player) {
		int[] runes =
				{563, 562, 555, 565};
		for (int i = 0; i < runes.length; i++) {
			if (ItemAssistant.hasItemInInventory(player, runes[i])) {
				Bank.addItemToBank(player, runes[i], ItemAssistant.getItemAmount(player, runes[i]), true);
				ItemAssistant.deleteItemFromInventory(player, runes[i], ItemAssistant.getItemSlot(player, runes[i]), ItemAssistant.getItemAmount(player, runes[i]));
			}
		}
		if (ItemAssistant.addItem(player, 560, 40) && ItemAssistant.addItem(player, 9075, 80) && ItemAssistant.addItem(player, 557, 200)) {
			player.playerAssistant.sendMessage("Vengeance runes have been added to your inventory.");
		}

		SpellBook.lunarSpellBook(player, true);
	}

	/**
	 * Spawn barrage runes.
	 *
	 * @param player The associated player.
	 */
	public static void barrageRunes(Player player) {
		int[] runes =
				{563, 562, 9075, 557};
		for (int i = 0; i < runes.length; i++) {
			if (ItemAssistant.hasItemInInventory(player, runes[i])) {
				Bank.addItemToBank(player, runes[i], ItemAssistant.getItemAmount(player, runes[i]), true);
				ItemAssistant.deleteItemFromInventory(player, runes[i], ItemAssistant.getItemSlot(player, runes[i]), ItemAssistant.getItemAmount(player, runes[i]));
			}
		}
		if (ItemAssistant.addItem(player, 565, 200) && ItemAssistant.addItem(player, 560, 400) && ItemAssistant.addItem(player, 555, 600)) {
			player.playerAssistant.sendMessage("Barrage runes have been added to your inventory.");
		}
		SpellBook.ancientMagicksSpellBook(player, true);

	}

	/**
	 * Spawn tele block runes.
	 *
	 * @param player The associated player.
	 */
	public static void teleBlockRunes(Player player) {
		int[] runes =
				{9075, 557, 555, 565};
		for (int i = 0; i < runes.length; i++) {
			if (ItemAssistant.hasItemInInventory(player, runes[i])) {
				Bank.addItemToBank(player, runes[i], ItemAssistant.getItemAmount(player, runes[i]), true);
				ItemAssistant.deleteItemFromInventory(player, runes[i], ItemAssistant.getItemSlot(player, runes[i]), ItemAssistant.getItemAmount(player, runes[i]));
			}
		}
		if (ItemAssistant.addItem(player, 563, 30) && ItemAssistant.addItem(player, 562, 30) && ItemAssistant.addItem(player, 560, 30)) {
			player.playerAssistant.sendMessage("Tele block runes have been added to your inventory.");
		}
		SpellBook.modernSpellBook(player, true);

	}

}
