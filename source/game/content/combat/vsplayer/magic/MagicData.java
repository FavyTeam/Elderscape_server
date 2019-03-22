package game.content.combat.vsplayer.magic;

import java.util.ArrayList;
import java.util.List;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.item.impl.CelestialSurgeBox;
import game.content.miscellaneous.RunePouch;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

public class MagicData {

	public static enum CombinationRunes {
		MIST_RUNE(4695, new int[] {556, 555}), // Air rune, Water rune
		DUST_RUNE(4696, new int[] {556, 557}), // Air rune, Earth rune
		MUD_RUNE(4698, new int[] {555, 557}), // Water rune, Earth rune
		SMOKE_RUNE(4697, new int[] {556, 554}), // Air rune, Fire rune
		STEAM_RUNE(4694, new int[] {555, 554}), // Water rune, Fire rune
		LAVA_RUNE(4699, new int[] {557, 554}); // Earth rune, Fire rune

		private int runeItemId;

		private int[] combinedRunesId;

		private CombinationRunes(int runeItemId, int[] combinedRunesId) {
			this.runeItemId = runeItemId;
			this.combinedRunesId = combinedRunesId;
		}

		public int getRuneItemId() {
			return runeItemId;
		}

		public int[] getCombinationRunesId() {
			return combinedRunesId;
		}

		public static boolean isCombinationRune(int itemId) {
			for (int index = 0; index < CombinationRunes.values().length; index++) {
				if (CombinationRunes.values()[index].getRuneItemId() == itemId) {
					return true;
				}
			}
			return false;
		}
	}

	public static enum MagicItemsUnlimitedRunes {
		//@formatter:off
		ARMADYL_BATTLESTAFF(21_777, null, ServerConstants.WEAPON_SLOT, new int[]{556}, GameType.PRE_EOC),
		LAW_STAFF(18_342, null, ServerConstants.WEAPON_SLOT, new int[] {563}, GameType.PRE_EOC),
		NATURE_STAFF(18_341, null, ServerConstants.WEAPON_SLOT, new int[]{561}, GameType.PRE_EOC),
		TOME_OF_FROST(18_346, null, ServerConstants.SHIELD_SLOT, new int[]{555}, GameType.PRE_EOC),

		TOME_OF_FIRE(20714, null, ServerConstants.SHIELD_SLOT, new int[] {554}, GameType.OSRS),
		MIST_BATTLESTAFF(20730, null, ServerConstants.WEAPON_SLOT, new int[] {556, 555}, GameType.OSRS), // Air rune, Water rune
		DUST_BATTLESTAFF(20736, null, ServerConstants.WEAPON_SLOT, new int[] {556, 557}, GameType.OSRS), // Air rune, Earth rune
		SMOKE_BATTLESTAFF(11998, null, ServerConstants.WEAPON_SLOT, new int[] {556, 554}, GameType.OSRS), // Air rune, Fire rune
		STEAM_BATTLESTAFF(11787, null, ServerConstants.WEAPON_SLOT, new int[] {555, 554}, GameType.OSRS), // Water rune, Fire rune
		KODAI_WAND(-1, "Kodai wand", ServerConstants.WEAPON_SLOT, new int[] {555}, GameType.OSRS), // Water rune

		STAFF_OF_WATER(1383, null, ServerConstants.WEAPON_SLOT, new int[] {555}, GameType.ANY),
		STAFF_OF_AIR(1381, null, ServerConstants.WEAPON_SLOT, new int[] {556}, GameType.ANY),
		STAFF_OF_EARTH(1385, null, ServerConstants.WEAPON_SLOT, new int[] {557}, GameType.ANY),
		STAFF_OF_FIRE(1387, null, ServerConstants.WEAPON_SLOT, new int[] {554}, GameType.ANY),
		MUD_BATTLESTAFF(6562, null, ServerConstants.WEAPON_SLOT, new int[] {555, 557}, GameType.ANY), // Water rune, Earth rune
		LAVA_BATTLESTAFF(3053, null, ServerConstants.WEAPON_SLOT, new int[] {557, 554}, GameType.ANY), // Earth rune, Fire rune
		LAVA_BATTLESTAFF_OR(21198, null, ServerConstants.WEAPON_SLOT, new int[] {557, 554}, GameType.OSRS); // Earth rune, Fire rune
		//@formatter:on

		private int itemId;

		private String nameContains;

		private int equipmentSlot;

		private int[] runesProvided;

		private GameType mode = null;

		private MagicItemsUnlimitedRunes(int itemId, String nameContains, int equipmentSlot, int[] runesProvided, GameType mode) {
			this.itemId = itemId;
			this.nameContains = nameContains;
			this.equipmentSlot = equipmentSlot;
			this.runesProvided = runesProvided;
			this.mode = mode;
		}

		public int getItemId() {
			return itemId;
		}
		
		public String getNameContains() {
			return nameContains;
		}

		public int getEquipmentSlot() {
			return equipmentSlot;
		}

		public int[] getRunesProvided() {
			return runesProvided;
		}

		public GameType getMode() {
			return mode;
		}


		public static MagicItemsUnlimitedRunes getEquipmentSlotInstanceIfItemIdWorn(int equipmentSlot, int itemId) {
			for (int index = 0; index < MagicItemsUnlimitedRunes.values().length; index++) {
				MagicItemsUnlimitedRunes instance = MagicItemsUnlimitedRunes.values()[index];
				if (!GameType.getGameType(instance.getMode())) {
					continue;
				}
				if (instance.getEquipmentSlot() == equipmentSlot && (instance.getItemId() == itemId && instance.getItemId() != -1 || ItemAssistant.getItemName(itemId).equals(instance.getNameContains()))) {
					return MagicItemsUnlimitedRunes.values()[index];
				}
			}
			return null;
		}
	}

	private final static boolean DEBUG_REQUIRED_MAGIC_RUNES = false;

	/**
	 * Delete the runes according to the spell used.
	 *
	 * @param player The associated player.
	 * @param usedSpell The spell used.
	 */
	public static boolean requiredRunes(Player player, int usedSpell, String action) {
		// Trident of the swamp.
		if (usedSpell == 52 && GameType.isOsrs()) {
			return true;
		}
		if (usedSpell == 12) {
			if (player.playerEquipment[ServerConstants.SHIELD_SLOT] == CelestialSurgeBox.CELESTIAL_SURGE_BOX) {
				if (CelestialSurgeBox.getCastLeft(player) > 0) {
					if (action.equalsIgnoreCase("DELETE RUNES")) {
						CelestialSurgeBox.drain(player);
					}
					return true;
				}
			}
		}
		// Get all the runes required
		ArrayList<Integer> runeItemIdNeeded = new ArrayList<Integer>();
		int runeIdNeeded = 0;
		int amountNeeded = 0;
		if (CombatConstants.MAGIC_SPELLS[usedSpell][8] > 0) {
			runeIdNeeded = CombatConstants.MAGIC_SPELLS[usedSpell][8];
			amountNeeded = CombatConstants.MAGIC_SPELLS[usedSpell][8 + 1];
			for (int index = 0; index < amountNeeded; index++) {
				runeItemIdNeeded.add(runeIdNeeded);
			}
			if (CombatConstants.MAGIC_SPELLS[usedSpell][10] > 0) {
				runeIdNeeded = CombatConstants.MAGIC_SPELLS[usedSpell][10];
				amountNeeded = CombatConstants.MAGIC_SPELLS[usedSpell][10 + 1];
				for (int index = 0; index < amountNeeded; index++) {
					runeItemIdNeeded.add(runeIdNeeded);
				}
				if (CombatConstants.MAGIC_SPELLS[usedSpell][12] > 0) {
					runeIdNeeded = CombatConstants.MAGIC_SPELLS[usedSpell][12];
					amountNeeded = CombatConstants.MAGIC_SPELLS[usedSpell][12 + 1];
					for (int index = 0; index < amountNeeded; index++) {
						runeItemIdNeeded.add(runeIdNeeded);
					}
					if (CombatConstants.MAGIC_SPELLS[usedSpell][14] > 0) {
						runeIdNeeded = CombatConstants.MAGIC_SPELLS[usedSpell][14];
						amountNeeded = CombatConstants.MAGIC_SPELLS[usedSpell][14 + 1];
						for (int index = 0; index < amountNeeded; index++) {
							runeItemIdNeeded.add(runeIdNeeded);
						}
					}
				}
			}
		}
		if (DEBUG_REQUIRED_MAGIC_RUNES) {
			Misc.print(runeItemIdNeeded + "");
		}
		List<RunesInStorage> runesToDelete = new ArrayList<RunesInStorage>();
		
		// Gather the runes the player has that are required for the spell
		 List<RunesInStorage> runesInStorage = new ArrayList<RunesInStorage>();
		for (int index = 0; index < player.playerItems.length; index++) {
			int itemId = player.playerItems[index] - 1;
			int itemAmount = player.playerItemsN[index];
			if (itemId >= 2 && (runeItemIdNeeded.contains(itemId) || CombinationRunes.isCombinationRune(itemId))) {
				runesInStorage = RunesInStorage.addEntry(runesInStorage, itemId, itemAmount);
			}
		}
		boolean hasRunePouch = ItemAssistant.hasItemInInventory(player, 12791);
		if (hasRunePouch) {
			for (int index = 0; index < player.runePouchItemAmount.length; index++) {
				int itemId = player.runePouchItemId[index];
				int itemAmount = player.runePouchItemAmount[index];
				if (itemId >= 1 && (runeItemIdNeeded.contains(itemId) || CombinationRunes.isCombinationRune(itemId))) {
					runesInStorage = RunesInStorage.addEntry(runesInStorage, itemId, itemAmount);
				}
			}
		}
		if (DEBUG_REQUIRED_MAGIC_RUNES) {
			for (int index = 0; index < runesInStorage.size(); index++) {
				RunesInStorage instance = runesInStorage.get(index);
				Misc.print(ItemAssistant.getItemName(instance.runeId) + " x" + instance.runeAmount);
			}
		}

		// Check if player has required runes.
		MagicItemsUnlimitedRunes magicWornItemsInstance = MagicItemsUnlimitedRunes.getEquipmentSlotInstanceIfItemIdWorn(ServerConstants.SHIELD_SLOT, player.playerEquipment[ServerConstants.SHIELD_SLOT]);
		if (magicWornItemsInstance != null) {
			for (int index = 0; index < magicWornItemsInstance.getRunesProvided().length; index++) {
				int runeIdProvided = magicWornItemsInstance.getRunesProvided()[index];
				runeItemIdNeeded = Misc.removeArraylistIntegerMatch(runeItemIdNeeded, runeIdProvided, true);
			}
		}
		if (DEBUG_REQUIRED_MAGIC_RUNES) {
			Misc.print("After shield effect: " + runeItemIdNeeded);
		}
		if (!runeItemIdNeeded.isEmpty()) {
			magicWornItemsInstance = MagicItemsUnlimitedRunes.getEquipmentSlotInstanceIfItemIdWorn(ServerConstants.WEAPON_SLOT, player.getWieldedWeapon());
			if (magicWornItemsInstance != null) {
				for (int index = 0; index < magicWornItemsInstance.getRunesProvided().length; index++) {
					int runeIdProvided = magicWornItemsInstance.getRunesProvided()[index];
					runeItemIdNeeded = Misc.removeArraylistIntegerMatch(runeItemIdNeeded, runeIdProvided, true);
				}
			}
			if (DEBUG_REQUIRED_MAGIC_RUNES) {
				Misc.print("After weapon effect: " + runeItemIdNeeded);
			}
		}
		// Checking if player has combination rune, so if required rune is Air and water rune and the player has Mist rune, then delete.
		if (!runeItemIdNeeded.isEmpty()) {
			for (int a = 0; a < runesInStorage.size(); a++) {
				RunesInStorage runesStorage = runesInStorage.get(a);
				if (runesStorage.combinationRunesInstance == null) {
					continue;
				}
				int originalRuneAmount = runesStorage.runeAmount;
				for (int b = 0; b < originalRuneAmount; b++) {
					int runesLength = runesStorage.combinationRunesInstance.getCombinationRunesId().length;
					boolean hasAllRunes = true;
					for (int i = 0; i < runesLength; i++) {
						if (!runeItemIdNeeded.contains(runesStorage.combinationRunesInstance.getCombinationRunesId()[i])) {
							hasAllRunes = false;
							break;
						}
					}
					if (hasAllRunes) {
						runesToDelete = RunesInStorage.addEntry(runesToDelete, runesStorage.combinationRunesInstance.getRuneItemId(), 1);
						runesStorage.setRuneAmount(runesStorage.runeAmount - 1);
						for (int i = 0; i < runesLength; i++) {
							runeItemIdNeeded = Misc.removeArraylistIntegerMatch(runeItemIdNeeded, runesStorage.combinationRunesInstance.getCombinationRunesId()[i], false);
						}
						if (runesStorage.runeAmount == 0) {
							runesInStorage.remove(a);
							a--;
						}
					} else {
						break;
					}
				}
			}
			if (DEBUG_REQUIRED_MAGIC_RUNES) {
				Misc.print("After combina runes: " + runeItemIdNeeded);
			}
		}
		// Check if player has normal runes, so if player has water rune, delete 1 water rune from required runes.
		if (!runeItemIdNeeded.isEmpty()) {
			for (int a = 0; a < runesInStorage.size(); a++) {
				RunesInStorage runesStorage = runesInStorage.get(a);
				if (runesStorage.combinationRunesInstance != null) {
					continue;
				}
				int runeIdRequired = runesStorage.runeId;
				int originalRuneAmount = runesStorage.runeAmount;
				for (int i = 0; i < originalRuneAmount; i++) {
					if (runeItemIdNeeded.contains(runeIdRequired)) {
						runeItemIdNeeded = Misc.removeArraylistIntegerMatch(runeItemIdNeeded, runeIdRequired, false);
						runesToDelete = RunesInStorage.addEntry(runesToDelete, runeIdRequired, 1);
						runesStorage.setRuneAmount(runesStorage.runeAmount - 1);
						if (runesStorage.runeAmount == 0) {
							runesInStorage.remove(a);
							a--;
						}
					} else {
						break;
					}
				}
			}
			if (DEBUG_REQUIRED_MAGIC_RUNES) {
				Misc.print("After single runes:  " + runeItemIdNeeded);
			}
		}
		// Check if player has a combination rune to match it with a single rune. So if player has Mud rune, he can match it with an Earth rune.
		if (!runeItemIdNeeded.isEmpty()) {
			for (int a = 0; a < runesInStorage.size(); a++) {
				RunesInStorage runesStorage = runesInStorage.get(a);
				if (runesStorage.combinationRunesInstance == null) {
					continue;
				}
				int originalRuneAmount = runesStorage.runeAmount;
				if (originalRuneAmount > 20) {
					originalRuneAmount = 20;
				}
				for (int b = 0; b < originalRuneAmount; b++) {
					int runesLength = runesStorage.combinationRunesInstance.getCombinationRunesId().length;
					for (int i = 0; i < runesLength; i++) {
						if (runeItemIdNeeded.contains(runesStorage.combinationRunesInstance.getCombinationRunesId()[i])) {
							runesToDelete = RunesInStorage.addEntry(runesToDelete, runesStorage.combinationRunesInstance.getRuneItemId(), 1);
							runesStorage.setRuneAmount(runesStorage.runeAmount - 1);
							runeItemIdNeeded = Misc.removeArraylistIntegerMatch(runeItemIdNeeded, runesStorage.combinationRunesInstance.getCombinationRunesId()[i], false);
							if (runesStorage.runeAmount == 0) {
								runesInStorage.remove(a);
								a--;
							}
							break;
						}
					}
				}
			}
			if (DEBUG_REQUIRED_MAGIC_RUNES) {
				Misc.print("After half combin: " + runeItemIdNeeded);
			}
		}


		if (runeItemIdNeeded.isEmpty()) {
			if (action.equals("DELETE RUNES")) {
				if (GameType.isOsrs()) {
					// Staff of light, chance of saving runes.
					boolean staff = false;
					if (GameType.isPreEoc()) {
						staff = player.getWieldedWeapon() == 15486;
					} else if (GameType.isOsrs()) {
						staff = ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase().contains("staff of the dead") || player.getWieldedWeapon() == 22296;
					}
					if (staff) {
						if (Misc.hasPercentageChance(13)) {
							return true;
						}
					}
					// Kodai wand, chance to save runes, spell who's max hit is more than 0, which means it is a combat spell.
					if (CombatConstants.MAGIC_SPELLS[usedSpell][6] > 0 && ItemAssistant.getItemName(player.getWieldedWeapon()).equals("Kodai wand") && Misc.hasPercentageChance(15)) {
						return true;
					}
				}


				for (int index = 0; index < runesToDelete.size(); index++) {
					RunesInStorage runesInStorageInstance = runesToDelete.get(index);
					if (DEBUG_REQUIRED_MAGIC_RUNES) {
						Misc.print("To delete: " + ItemAssistant.getItemName(runesInStorageInstance.runeId) + " x" + runesInStorageInstance.runeAmount);
					}
					deleteRuneFromPlayer(player, runesInStorageInstance.runeId, runesInStorageInstance.runeAmount, hasRunePouch);
				}

			}
			return true;
		}
		return false;
	}
	
	public static class RunesInStorage {
		public int runeId = 0;
		public int runeAmount = 0;

		public CombinationRunes combinationRunesInstance;

		public void setRuneAmount(int amount) {
			runeAmount = amount;
		}

		public RunesInStorage(int runeId, int runeAmount, CombinationRunes combinationRunesInstance) {
			this.runeId = runeId;
			this.runeAmount = runeAmount;
			this.combinationRunesInstance = combinationRunesInstance;
		}

		public static List<RunesInStorage> addEntry(List<RunesInStorage> list, int runeId, int runeAmount) {
			for (int index = 0; index < list.size(); index++) {
				RunesInStorage instance = list.get(index);
				if (instance.runeId == runeId) {
					instance.setRuneAmount(instance.runeAmount + runeAmount);
					return list;
				}
			}

			CombinationRunes combinationRunesInstance = null;
			for (int index = 0; index < CombinationRunes.values().length; index++) {
				CombinationRunes instance = CombinationRunes.values()[index];
				if (instance.getRuneItemId() == runeId) {
					combinationRunesInstance = instance;
					break;
				}
			}
			list.add(new RunesInStorage(runeId, runeAmount, combinationRunesInstance));
			return list;
		}

		public static boolean hasRune(List<RunesInStorage> list, int runeId, int runeAmount) {
			for (int index = 0; index < list.size(); index++) {
				RunesInStorage instance = list.get(index);
				if (instance.runeId == runeId && instance.runeAmount >= runeAmount) {
					return true;
				}
			}
			return false;
		}
	}

	private static void deleteRuneFromPlayer(Player player, int runeId, int runeAmount, boolean hasRunePouch) {
		runeAmount = ItemAssistant.getAmountLeftToDeleteFromInventory(player, runeId, runeAmount);
		if (runeAmount > 0 && hasRunePouch) {
			RunePouch.getAmountLeftToDeleteFromRunePouch(player, runeId, runeAmount);
		}

	}
	/**
	 * If the spell used is a combat spell, append the spell to the player spell variable and set the
	 * player to usingMagic.
	 *
	 * @param attacker The player using the spell.
	 * @param castingSpellId The spell casted by the player.
	 */
	public static void setCombatSpell(Player attacker, int castingSpellId) {
		for (int i = 0; i < CombatConstants.MAGIC_SPELLS.length; i++) {
			if (castingSpellId == CombatConstants.MAGIC_SPELLS[i][0]) {
				attacker.setSpellId(i);
				attacker.setLastCastedMagic(true);
				break;
			}
		}
	}

	/**
	 * True if the player is wearing a Saradomin God cape or Saradomin max cape.
	 */
	public static boolean hasSaradominGodCape(Player player) {
		return player.playerEquipment[ServerConstants.CAPE_SLOT] == 2412 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 13331
		       || player.playerEquipment[ServerConstants.CAPE_SLOT] == 21776 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 21791;
	}

	/**
	 * True if the player is wearing a Zamorak God cape or Zamorak max cape.
	 */
	public static boolean hasZamorakGodCape(Player player) {
		return player.playerEquipment[ServerConstants.CAPE_SLOT] == 2414 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 13333
		       || player.playerEquipment[ServerConstants.CAPE_SLOT] == 21780 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 21795;
	}

	/**
	 * True if the player is wearing a Guthix God cape or Guthix max cape.
	 */
	public static boolean hasGuthixGodCape(Player player) {
		return player.playerEquipment[ServerConstants.CAPE_SLOT] == 2413 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 13335
		       || player.playerEquipment[ServerConstants.CAPE_SLOT] == 21784 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 21793;
	}

	/**
	 * Get the must-use staff for a specific spell.
	 *
	 * @param player The associated player.
	 * @return The staff needed.
	 */
	public static int getStaffNeeded(Player player, int spellUsed) {
		if (spellUsed == -1) {
			return 0;
		}
		switch (CombatConstants.MAGIC_SPELLS[spellUsed][0]) {
			case 32_680:
				return 22_494;
			case 32_600:
			case 32_620:
			case 32_640:
			case 32_660:
				return 13_867;
			case 1539:
				return 1409;
			case 12037:
				return 4170;
			case 1190:
				// If Saradomin staff required and player has Staff of light weapon, then change staff required to Staff of light
				if (player.getWieldedWeapon() == 22296) {
					return player.getWieldedWeapon();
				}
				if (GameType.isPreEoc()) {
					if (player.getWieldedWeapon() == 15486) {
						return player.getWieldedWeapon();
					}
				}
				return 2415;
			case 1191:
				return 2416;
			case 1192:
				// If Zamorak staff required and player has Staff of the dead weapon, then change staff required to Staff of the dead.
				if (player.getWieldedWeapon() == 11791 || player.getWieldedWeapon() == 12904 || player.getWieldedWeapon() == 16209 || player.getWieldedWeapon() == 16272) {
					return player.getWieldedWeapon();
				}
				return 2417;
			default:
				return 0;
		}
	}

	/**
	 * @return True, if the player is wearing full void magic.
	 */
	public static boolean wearingFullVoidMagic(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 11663 && Combat.hasVoidTop(player) && Combat.hasVoidBottom(player)
		       && player.playerEquipment[ServerConstants.HAND_SLOT] == 8842;
	}

}
