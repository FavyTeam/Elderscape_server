package game.content.combat.vsplayer.range;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.item.impl.PreEocCompletionistCape;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.PlayerHandler;

public class RangedData {

	/**
	 * List of arrows.
	 */
	public final static int[] ARROW = {
			//
			882, // Bronze arrow.
			884, // Iron arrow.
			886, // Steel arrow.
			888, // Mithril arrow.
			890, // Adamant arrow.
			892, // Rune arrow.
			21326, // Amethyst arrow.
			11212, // Dragon arrow.
	};
	public final static int[] ARROW_OSRS = {
			//
			21326, // Amethyst arrow.
	};

	public static final int[][] BOW_PRE_EOC = {
			//
			{18_331, 890}, // maple longbow sight
			{18_373, 892, 21326}, // gravite bow
			{18_332, 892, 21326}, // magic longbow sight
	};

	public final static int[][] BOW_OSRS = {
			// 3rd age bow, Dragon arrow.
			{12424, 11212},
			// Magic shortbow (i), Rune arrow, Amethyst arrow.
			{12788, 892, 21326},
			// Twisted bow, Dragon arrow, Amethyst arrow
			{20997, 11212, 21326},
			// Twisted bow (locals), Dragon arrow, Amethyst arrow
			{16052, 11212, 21326},
			// Twisted bow (thuggahhh), Dragon arrow, Amethyst arrow
			{16056, 11212, 21326},
			// Twisted bow white, Dragon arrow, Amethyst arrow
			{16282, 11212, 21326},
			// Dark bow, Dragon arrow, Amethyst arrow
			{11235, 11212, 21326},
			// Dark bow, Dragon arrow, Amethyst arrow
			{12765, 11212, 21326},
			// Dark bow, Dragon arrow, Amethyst arrow
			{12766, 11212, 21326},
			// Dark bow, Dragon arrow, Amethyst arrow
			{12767, 11212, 21326},
			// Dark bow, Dragon arrow, Amethyst arrow
			{12768, 11212, 21326},};


	/**
	 * List of bows and their corresponding maximum arrow used with it.
	 */
	public final static int[][] BOW = {
			// Shortbow, Iron arrow.
			{841, 884},
			// Oak shortbow, Steel arrow.
			{843, 886},
			// Willow shortbow, Mithril arrow.
			{849, 888},
			// Maple shortbow, Adamant arrow.
			{853, 890},
			// Yew shortbow, Adamant arrow.
			{857, 890},
			// Magic shortbow, Rune arrow, Amethyst arrow.
			{861, 892, 21326},
			// Magic longbow, Rune arrow, Amethyst arrow.
			{859, 892, 21326},};

	public static enum CrossBow {
		

		BRONZE_CROSSBOW("", 9174, 9140, GameType.ANY),
		IRON_CROSSBOW("", 9177, 9140, GameType.ANY),
		STEEL_CROSSBOW("", 9179, 9141, GameType.ANY),
		MITHRIL_CROSSBOW("", 9181, 9142, GameType.ANY),
		ADAMANT_CROSSBOW("", 9183, 9143, GameType.ANY),
		RUNE_CROSSBOW("", 9185, 9342, GameType.ANY),
		
		ARMADYL_CROSSBOW("Armadyl crossbow", 5, 21950, GameType.OSRS),
		DRAGON_HUNTER_CROSSBOW("", 21012, 21950, GameType.OSRS),
		DRAGON_CROSSBOW("", 21902, 21950, GameType.OSRS),
		
		CHAOTIC_CROSSBOW("", 18357, 9342, GameType.PRE_EOC);

		private String itemName = "";
		private int weaponId, ammoId;
		private GameType gameType = null;

		private CrossBow(String itemName, int weaponId, int ammoId, GameType gameType) {
			this.itemName = itemName;
			this.weaponId = weaponId;
			this.ammoId = ammoId;
			this.gameType = gameType;
		}
	}

	/**
	 * List of weapons that do not need ammo: ITEM_ID/DELETE_WEAPON_AS_AMMO (0=yes, 1=no)/SHORT_RANGED_WEAPON (0=no, 1=yes)
	 */
	public final static int[][] NO_AMMO_RANGED_WEAPON = {
			//
			{863, 0, 1}, // Iron knife.
			{864, 0, 1}, // Bronze knife.
			{865, 0, 1}, // Steel_knife.
			{869, 0, 1}, // Black_knife.
			{866, 0, 1}, // Mithril_knife.
			{867, 0, 1}, // Adamant_knife.
			{868, 0, 1}, // Rune_knife.
			{806, 0, 1}, // Bronze dart.
			{807, 0, 1}, // Iron dart.
			{808, 0, 1}, // Steel dart.
			{809, 0, 1}, // Mithril dart.
			{810, 0, 1}, // Adamant dart.
			{811, 0, 1}, // Rune dart.
			{4214, 1, 0}, // Crystal bow
		{22547, 1, 0}, // Craw's bow (u)
		{22550, 1, 0}, // Craw's bow
			{6522, 0, 1}, // Toktz-xil-ul.
			{10033, 0, 1}, // grey chin
			{10034, 0, 1}, // red chin
			{11230, 0, 1}, // Dragon dart.
	};
	public final static int[][] NO_AMMO_RANGED_WEAPON_OSRS = {
			//
			{11959, 0, 1}, // black chin
			{12926, 1, 1}, // Toxic blowpipe.
			{20849, 0, 1}, // Dragon thrownaxe
		{22634, 1, 1}, // Morrigan's throwing axe
		{22636, 1, 1}, // Morrigan's javelin
	};

	public static final int[][] NO_AMMO_RANGED_WEAPON_PRE_EOC = {
			//
			{13_879, 0, 1}, // morrigan javelin
			{13_883, 0, 1}, // morrigan axe
	};

	public static enum SpecialRangedWeapon {
		KARILS_CROSSBOW("", 4734, 4740, GameType.ANY),
		HUNTERS_CROSSBOW("", 10156, 10159, GameType.ANY),
		HEAVY_BALLISTA("Heavy ballista", 5, 19484, GameType.OSRS),
		LIGHT_BALLISTA("", 19478, 19484, GameType.OSRS),
		ROYAL_CROSSBOW("", 24_338, 24_336, GameType.PRE_EOC),
		HAND_CANNON("", 15241, 15243, GameType.PRE_EOC);

		private String itemName = "";
		private int weaponId, ammoId;
		private GameType gameType = null;

		private SpecialRangedWeapon(String itemName, int weaponId, int ammoId, GameType gameType) {
			this.itemName = itemName;
			this.weaponId = weaponId;
			this.ammoId = ammoId;
			this.gameType = gameType;
		}
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player is wielding a short ranged weapon. Such as knifes, javelin, throwing
	 *         axe and Toxic blowpipe.
	 */
	public static boolean isWieldingRangedWeaponWithNoArrowSlotRequirement(Player player) {
		for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON.length; value++) {
			if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON[value][0]) {
				player.setIsWieldingRangedWeaponWithNoArrowSlotRequirement(true);
				return true;
			}
		}
		if (GameType.isOsrs()) {
			for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON_OSRS.length; value++) {
				if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON_OSRS[value][0]) {
					player.setIsWieldingRangedWeaponWithNoArrowSlotRequirement(true);
					return true;
				}
			}
		}
		if (GameType.isPreEoc()) {
			for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON_PRE_EOC.length; value++) {
				if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON_PRE_EOC[value][0]) {
					player.setIsWieldingRangedWeaponWithNoArrowSlotRequirement(true);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isWieldingRangedWeaponToDeleteFromHandSlot(Player player) {
		for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON.length; value++) {
			if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON[value][0] && RangedData.NO_AMMO_RANGED_WEAPON[value][1] == 0) {
				player.setIsWieldingRangedWeaponWithNoArrowSlotRequirement(true);
				return true;
			}
		}
		if (GameType.isOsrs()) {
			for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON_OSRS.length; value++) {
				if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON_OSRS[value][0] && RangedData.NO_AMMO_RANGED_WEAPON_OSRS[value][1] == 0) {
					player.setIsWieldingRangedWeaponWithNoArrowSlotRequirement(true);
					return true;
				}
			}
		}
		if (GameType.isPreEoc()) {
			for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON_PRE_EOC.length; value++) {
				if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON_PRE_EOC[value][0] && RangedData.NO_AMMO_RANGED_WEAPON[value][1] == 0) {
					player.setIsWieldingRangedWeaponWithNoArrowSlotRequirement(true);
					return true;
				}
			}
		}
		return false;
	}

	public static int getRangedAttackEmote(Player attacker) {
		String weaponName = ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase();
		Player victim = PlayerHandler.players[attacker.getPlayerIdAttacking()];
		attacker.lastAttackAnimationTimer = System.currentTimeMillis();
		boolean soundApplied = false;
		if (GameType.isPreEoc()) {
			switch (attacker.getWieldedWeapon()) {
				case 15_241:
					return 12_153;
				case 13_879:
					return 10_501;
				case 13_883:
					return 10_504;
			}
		}
		if (weaponName.equals("heavy ballista") || weaponName.equals("light ballista")) {
			return 7218;
		}
		switch (attacker.getWieldedWeapon()) {
			case 22636: // Morrigan's javelin	
				return 806;
			case 22634: // Morrigan's throwing axe
			case 20849: // Dragon thrownaxe
				return 929;
			case 12926: // Toxic blowpipe.
				return 5061;
			case 6522:
				// Toktz-xil-ul (tzhaar range weapon, looks like a hoolahoop)
				return 2614;
			case 4734:
				// Karil's crossbow
				return 2075;
		}
		if (weaponName.contains("knife") || weaponName.contains("dart") || weaponName.contains("javelin") || weaponName.contains("thrownaxe")) {
			SoundSystem.sendSound(attacker, victim, 365, 570);
			return 806;
		}
		if (weaponName.contains("karil")) {
			return 2075;
		}
		if (weaponName.contains("'bow") || weaponName.contains("crossbow")) {
			SoundSystem.sendSound(attacker, victim, 370, 700);
			return 4230;
		}
		if (weaponName.contains("bow")) {
			SoundSystem.sendSound(attacker, victim, 370, 300);
			return 426;
		}
		if (weaponName.contains("chinchompa")) {
			SoundSystem.sendSound(attacker, victim, 370, 300);
			return 2779;
		}
		if (!soundApplied) {
			SoundSystem.sendSound(attacker, victim, 417, 300);
		}
		return 451;
	}

	/**
	 * @param player
	 * @param hasArrowEquipped True, if the player has an arrow equipped.
	 * @return True, if the player has the ammo required to use their Ranged weapon.
	 */
	public static boolean hasRequiredAmmo(Player player, boolean hasArrowEquipped) {

		if (player.getWieldedWeapon() == 12926 && player.blowpipeDartItemAmount == 0) {
			player.playerAssistant.sendMessage("You have run out of darts!");
			return false;
		}
		if (isWieldingRangedWeaponWithNoArrowSlotRequirement(player)) {
			player.setDroppedRangedItemUsed(player.getWieldedWeapon());
			return true;
		}

		if (!hasArrowEquipped) {
			player.playerAssistant.sendMessage("You have run out of ammo!");
			return false;
		}

		int weapon = player.getWieldedWeapon();
		int ammo = player.playerEquipment[ServerConstants.ARROW_SLOT];

		if (hasBowEquipped(player) && hasArrowEquipped(player)) {
			if (ammo <= getHighestArrow(player, weapon) || canUseOddArrowWithBow(player, weapon, ammo)) {
				if (ammo == 11212 && player.playerEquipmentN[ServerConstants.ARROW_SLOT] <= 1) {
					player.getPA().sendMessage("You need 2 dragon arrows.");
					return false;
				}
				player.setDroppedRangedItemUsed(ammo);
				return true;
			}
		}

		if (hasCrossBowEquipped(player) && hasBoltEquipped(player)) {
			if (ammo <= getHighestBolt(player, weapon)) {
				player.setDroppedRangedItemUsed(ammo);
				return true;
			}
		}

		for (int value = 0; value < SpecialRangedWeapon.values().length; value++) {
			SpecialRangedWeapon instance = SpecialRangedWeapon.values()[value];
			if (!GameType.getGameType(instance.gameType)) {
				continue;
			}
			if ((weapon == instance.weaponId || ItemAssistant.getItemName(weapon).equals(instance.itemName)) && ammo == instance.ammoId) {
				// Hand cannon.
				if (weapon != 15241) {
					player.setDroppedRangedItemUsed(ammo);
				}
				return true;
			}
		}

		player.playerAssistant.sendMessage("You cannot use a " + ItemAssistant.getItemName(weapon) + " with a " + ItemAssistant.getItemName(ammo) + ".");
		return false;
	}

	/**
	 * True if the player's weapon can be used with Amethyst arrow.
	 */
	private static boolean canUseOddArrowWithBow(Player player, int weapon, int ammo) {
		for (int value = 0; value < BOW.length; value++) {
			if (weapon == BOW[value][0] && BOW[value].length == 3) {
				if (BOW[value][2] == ammo) {
					return true;
				}
			}
		}
		if (GameType.isOsrs()) {
			for (int value = 0; value < BOW_OSRS.length; value++) {
				if (weapon == BOW_OSRS[value][0] && BOW_OSRS[value].length == 3) {
					if (BOW_OSRS[value][2] == ammo) {
						return true;
					}
				}
			}
		}
		if (GameType.isPreEoc()) {
			for (int value = 0; value < BOW_PRE_EOC.length; value++) {
				if (weapon == BOW_PRE_EOC[value][0] && BOW_PRE_EOC[value].length == 3) {
					if (BOW_PRE_EOC[value][2] == ammo) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get the highest bolt use-able depending on the bow used.
	 *
	 * @param player The associated player.
	 * @param weapon The weapon used by the player.
	 * @return The highest bolt use-able.
	 */
	public static int getHighestBolt(Player player, int weapon) {
		for (int value = 0; value < CrossBow.values().length; value++) {
			CrossBow instance = CrossBow.values()[value];
			if (!GameType.getGameType(instance.gameType)) {
				continue;
			}
			if (weapon == instance.weaponId || ItemAssistant.getItemName(weapon).equals(instance.itemName)) {
				return instance.ammoId;
			}
		}
		return 0;
	}

	/**
	 * Get the highest arrow use-able depending on the bow used.
	 *
	 * @param player The associated player.
	 * @param weapon The weapon used by the player.
	 * @return The highest arrow use-able.
	 */
	private static int getHighestArrow(Player player, int weapon) {
		for (int value = 0; value < BOW.length; value++) {
			if (weapon == BOW[value][0]) {
				return BOW[value][1];
			}
		}
		if (GameType.isOsrs()) {
			for (int value = 0; value < BOW_OSRS.length; value++) {
				if (weapon == BOW_OSRS[value][0]) {
					return BOW_OSRS[value][1];
				}
			}
		}
		if (GameType.isPreEoc()) {
			for (int value = 0; value < BOW_PRE_EOC.length; value++) {
				if (weapon == BOW_PRE_EOC[value][0]) {
					return BOW_PRE_EOC[value][1];
				}
			}
		}
		return 0;
	}

	/**
	 * Check if the player has arrow/s equipped.
	 *
	 * @param player The associated player.
	 * @return True, if the player has arrow/s equipped.
	 */
	public static boolean hasArrowEquipped(Player player) {
		int amount = 1;
		for (int value : ARROW) {
			if (player.playerEquipment[ServerConstants.ARROW_SLOT] == value && player.playerEquipmentN[ServerConstants.ARROW_SLOT] >= amount) {
				return true;
			}
		}
		if (GameType.isOsrs()) {
			for (int value : ARROW_OSRS) {
				if (player.playerEquipment[ServerConstants.ARROW_SLOT] == value && player.playerEquipmentN[ServerConstants.ARROW_SLOT] >= amount) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player has an Ava bag or Completionist cape equipped.
	 */
	public static boolean hasAvaRelatedItem(Player player) {
		if (hasAvaEquipped(player) || player.playerEquipment[ServerConstants.CAPE_SLOT] == 14011 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 13337) {
			return true;
		}
		if (Skilling.hasMasterCapeWorn(player, 9756)) {
			return true;
		}
		return false;
	}

	public static boolean hasAvaEquipped(Player player) {
		if (GameType.isPreEoc()) {
			if (PreEocCompletionistCape.capeEquipped(player)) {
				return true;
			}
		}
		if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 10498 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 10499
		    || player.playerEquipment[ServerConstants.CAPE_SLOT] == 22109 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 21898) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the player has a bow equipped.
	 *
	 * @param player The associated player.
	 * @return True, if the player has a bow equipped.
	 */
	public static boolean hasBowEquipped(Player player) {
		for (int value = 0; value < BOW.length; value++) {
			if (player.getWieldedWeapon() == BOW[value][0]) {
				return true;
			}
		}
		if (GameType.isOsrs()) {
			for (int value = 0; value < BOW_OSRS.length; value++) {
				if (player.getWieldedWeapon() == BOW_OSRS[value][0]) {
					return true;
				}
			}
		}
		if (GameType.isPreEoc()) {
			for (int value = 0; value < BOW_PRE_EOC.length; value++) {
				if (player.getWieldedWeapon() == BOW_PRE_EOC[value][0]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * True if the player has a ranged weapon equipped or a ranged weapon  in inventory.
	 *
	 * @param player
	 * @return
	 */
	public static boolean hasRangedWeapon(Player player) {
		if (player.getWieldedWeapon() > 0) {
			if (ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase().contains("bow")) {
				return true;
			}
		}
		for (int index = 0; index < player.playerItems.length; index++) {
			int itemId = player.playerItems[index] - 1;
			if (itemId <= 0) {
				continue;
			}
			if (ItemAssistant.getItemName(itemId).toLowerCase().contains("bow")) {
				return true;
			}
		}


		for (int value = 0; value < SpecialRangedWeapon.values().length; value++) {
			SpecialRangedWeapon instance = SpecialRangedWeapon.values()[value];
			if (!GameType.getGameType(instance.gameType)) {
				continue;
			}
			if (player.getWieldedWeapon() == instance.weaponId || ItemAssistant.getItemName(player.getWieldedWeapon()).equals(instance.itemName)) {
				return true;
			}
			for (int index = 0; index < player.playerItems.length; index++) {
				if (player.playerItems[index] - 1 == instance.weaponId || ItemAssistant.getItemName(player.playerItems[index] - 1).equals(instance.itemName)) {
					return true;
				}
			}
		}
		for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON.length; value++) {
			if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON[value][0]) {
				return true;
			}
			if (ItemAssistant.hasItemInInventory(player, RangedData.NO_AMMO_RANGED_WEAPON[value][0])) {
				return true;
			}
		}
		if (GameType.isOsrs()) {
			for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON_OSRS.length; value++) {
				if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON_OSRS[value][0]) {
					return true;
				}
				if (ItemAssistant.hasItemInInventory(player, RangedData.NO_AMMO_RANGED_WEAPON_OSRS[value][0])) {
					return true;
				}
			}
		}
		if (GameType.isPreEoc()) {
			for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON_PRE_EOC.length; value++) {
				if (player.getWieldedWeapon() == RangedData.NO_AMMO_RANGED_WEAPON_PRE_EOC[value][0]) {
					return true;
				}
				if (ItemAssistant.hasItemInInventory(player, RangedData.NO_AMMO_RANGED_WEAPON_PRE_EOC[value][0])) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isRangedItem(Player player, int itemId) {
		if (ItemAssistant.getItemName(itemId).toLowerCase().contains("bow")) {
			return true;
		}

		for (int value = 0; value < SpecialRangedWeapon.values().length; value++) {
			SpecialRangedWeapon instance = SpecialRangedWeapon.values()[value];
			if (!GameType.getGameType(instance.gameType)) {
				continue;
			}
			if (itemId == instance.weaponId || ItemAssistant.getItemName(itemId).equals(instance.itemName)) {
				return true;
			}
		}
		for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON.length; value++) {
			if (itemId == RangedData.NO_AMMO_RANGED_WEAPON[value][0]) {
				return true;
			}
		}
		if (GameType.isOsrs()) {
			for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON_OSRS.length; value++) {
				if (itemId == RangedData.NO_AMMO_RANGED_WEAPON_OSRS[value][0]) {
					return true;
				}
			}
		}
		if (GameType.isPreEoc()) {
			for (int value = 0; value < RangedData.NO_AMMO_RANGED_WEAPON_PRE_EOC.length; value++) {
				if (itemId == RangedData.NO_AMMO_RANGED_WEAPON_PRE_EOC[value][0]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if the player has bolt/s equipped.
	 *
	 * @param player The associated player.
	 * @return True, if the player has bolt/s equipped.
	 */
	public static boolean hasBoltEquipped(Player player) {
		int ammo = player.playerEquipment[ServerConstants.ARROW_SLOT];
		if (ammo >= 9140 && ammo <= 9245 || ammo >= 9337 && ammo <= 9342 || ammo >= 21924 && ammo <= 21950 || ammo == 21905) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the player has a crossbow equipped.
	 *
	 * @param player The associated player.
	 * @return True, if the player has a crossbow equipped.
	 */
	public static boolean hasCrossBowEquipped(Player player) {
		for (int value = 0; value < CrossBow.values().length; value++) {
			CrossBow instance = CrossBow.values()[value];
			if (!GameType.getGameType(instance.gameType)) {
				continue;
			}
			if (player.getWieldedWeapon() == instance.weaponId || ItemAssistant.getItemName(player.getWieldedWeapon()).equals(instance.itemName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the player has a crossbow equipped.
	 *
	 * @param player The associated player.
	 * @return True, if the player has a crossbow equipped.
	 */
	public static boolean hasSpecialRangedWeaponEquipped(Player player) {
		for (int value = 0; value < SpecialRangedWeapon.values().length; value++) {
			SpecialRangedWeapon instance = SpecialRangedWeapon.values()[value];
			if (!GameType.getGameType(instance.gameType)) {
				continue;
			}
			if (player.getWieldedWeapon() == instance.weaponId || ItemAssistant.getItemName(player.getWieldedWeapon()).equals(instance.itemName)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @param player The associated player.
	 * @return True, if the player is wielding a medium ranged weapon.
	 */
	public static boolean isWieldingMediumRangeRangedWeapon(Player player) {
		if (hasBowEquipped(player) || hasCrossBowEquipped(player)) {
			player.setUsingMediumRangeRangedWeapon(true);
			return true;
		}

		if (hasSpecialRangedWeaponEquipped(player)) {
			player.setUsingMediumRangeRangedWeapon(true);
			return true;
		}

		return false;
	}

	/**
	 * @return True, if the player is wearing full void ranged.
	 */
	public static boolean wearingFullVoidRanged(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 11664 && Combat.hasVoidTop(player) && Combat.hasVoidBottom(player)
		       && player.playerEquipment[ServerConstants.HAND_SLOT] == 8842;
	}

	/**
	 * @return True, if the player is wearing full void ranged elite.
	 */
	public static boolean wearingFullVoidRangedElite(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 11664 && player.playerEquipment[ServerConstants.BODY_SLOT] == 13072
		       && player.playerEquipment[ServerConstants.LEG_SLOT] == 13073 && player.playerEquipment[ServerConstants.HAND_SLOT] == 8842;
	}

	/**
	 * @return True, if the player is wearing full void mage elite.
	 */
	public static boolean wearingFullVoidMageElite(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 11663 && player.playerEquipment[ServerConstants.BODY_SLOT] == 13072
		       && player.playerEquipment[ServerConstants.LEG_SLOT] == 13073 && player.playerEquipment[ServerConstants.HAND_SLOT] == 8842;
	}
}
