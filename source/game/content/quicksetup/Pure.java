package game.content.quicksetup;

import core.GameType;
import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Store the pure equipment and inventory data.
 *
 * @author MGT Madness, created on 17-03-2015.
 */
public class Pure {



	/**
	 * Spawn the random items.
	 *
	 * @param player The associated player.
	 */
	public static void random(Player player) {
		int randomOutFitSet = Misc.random(GameType.isOsrs() ? robeAndHatSet.length - 1 : robeAndHatSetPreEOC.length -1);
		int randomWeaponSet = Misc.random(weaponSet.length - 1);
		int ammoAmount = randomWeaponSet == 6 ? 15 : 40;
		/*
		{861, -1, 892, 1725, 1215}, // Magic short bow to dds.
		{4587, 3842, -1, 1725, 1215}, // Dragon scimitar to dds.
		{4587, 3842, -1, 1725, 1434},}; // Dragon scimitar to Dragon mace.
		*/

		boolean primaryRanged = randomWeaponSet == 0;

		int bodySlotItem =  GameType.isOsrs() ? robeAndHatSet[randomOutFitSet][1] : robeAndHatSetPreEOC[randomOutFitSet][1];

		int legSlotItem = primaryRanged ? 2497 :  GameType.isOsrs() ? robeAndHatSet[randomOutFitSet][2] : robeAndHatSetPreEOC[randomOutFitSet][2];

		int[] result = new int[2];
		result =

				pureEquipmentSetModifications(player, bodySlotItem, legSlotItem, 0, 0);

		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.CAPE_SLOT, primaryRanged ? 10499 : QuickSetUp.getTeamCape(false), 1, false);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.HEAD_SLOT,  GameType.isOsrs() ? robeAndHatSet[randomOutFitSet][0] : robeAndHatSetPreEOC[randomOutFitSet][0], 1, false);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.BODY_SLOT, result[0], 1, false);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.LEG_SLOT, result[1], 1, false);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.WEAPON_SLOT, weaponSet[randomWeaponSet][0], 1, false);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.SHIELD_SLOT, weaponSet[randomWeaponSet][1], 1, false);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.ARROW_SLOT, weaponSet[randomWeaponSet][2], ammoAmount, false);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.AMULET_SLOT, weaponSet[randomWeaponSet][3], 1, false);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.HAND_SLOT, 7458, 1, false);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.FEET_SLOT, 3105, 1, false);
		ItemAssistant.addItemToInventory(player, weaponSet[randomWeaponSet][4], 1, randomWeaponSet == 5 ? 8 : 21, false);

		if (primaryRanged) {
			ItemAssistant.addItemToInventory(player, 2444, 1, 2, false); // Ranging potion.
		}
		ItemAssistant.addItem(player, 385, ItemAssistant.getFreeInventorySlots(player));
	}

	public static int[] pureEquipmentSetModifications(Player player, int bodySlotItem, int legSlotItem, int lastSlotId, int lastSlotAmount) {
		boolean blackDHideChaps = (legSlotItem == 2497) ? true : false;
		int[] result = new int[4];
		result[0] = bodySlotItem;
		result[1] = blackDHideChaps ? 2497 : legSlotItem;
		result[2] = lastSlotId;
		result[3] = lastSlotAmount;

		return result;
	}

	public static int[][] robeAndHatSet =
			{
					{2910, 1035, 1033},
					{660, 640, 650},
					{658, 638, 648},
					{656, 636, 646},
					{664, 644, 654},
					{2940, 2936, 2938},
					{2930, 2926, 2928},
					{579, 577, 1011},
					{2920, 2916, 2918},
					{662, 642, 652},
					{ 2900, 2896, 2898},
					{1017, 426, 428},
					{6109, 6107, 6108}
			};
	public static int[][] robeAndHatSetPreEOC =
			{
					{2910, 1035, 1033},
					{660, 640, 650},
					{658, 638, 648},
					{656, 636, 646},
					{664, 644, 654},
					{2940, 2936, 2938},
					{2930, 2926, 2928},
					{579, 577, 1011},
					{2920, 2916, 2918},
					{662, 642, 652},
					{ 2900, 2896, 2898},
					{1017, 426, 428},
					{6109, 6107, 6108}
			};

	// Weapon, shield, arrows,  amulet, extra inventory weapon
	private static int[][] weaponSet =
			{
					{861, -1, 892, 1725, 1215}, // Magic short bow to dds.
					{4587, 3842, -1, 1725, 1215}, // Dragon scimitar to dds.
					{4587, 3842, -1, 1725, 1434},
			}; // Dragon scimitar to Dragon mace.

	// Weapon, shield, arrows,  amulet, extra inventory weapon
	public static int[][] weaponSetBot =
			{
					// @formatter:off.
					{
							// Dragon scimitar, Unholy book, Amulet of strength, Dragon dagger.
							4587, 3842, -1, 1725, 1215
					},
					{
							// Dragon scimitar, Unholy book, Amulet of strength, Dragon mace.
							4587, 3842, -1, 1725, 1434
					},
					{
							// Dragon scimitar, Unholy book, Amulet of strength, Dragon claws.
							4587, 3842, -1, 1725, 20784
					},
					{
							// Dragon scimitar, Unholy book, Amulet of strength, Armadyl godsword.
							4587, 3842, -1, 1725, 11802
					},
					{
							// Saradomin sword, Amulet of strength, Dragon dagger.
							11838, -1, -1, 1725, 1215
					},
					{
							// Magic shortbow, Amulet of glory, Armadyl godsword.
							861, -1, 892, 1712, 11802
					},
					{
							// Magic shortbow, Amulet of glory, Dragon claws.
							861, -1, 892, 1712, 20784
					},
					{
							// Magic shortbow, Amulet of strength, Dragon dagger.
							861, -1, 892, 1725, 1215
					},
					{
							// Magic shortbow, Amulet of glory, Dark bow.
							861, -1, 892, 1712, 11235
					}
					// @formatter:on.
			};

	private final static int INITIATE_GEAR = 4;

	private static boolean isPrimaryRanged(int randomWeaponSet) {
		if (randomWeaponSet >= 5) {
			return true;
		}
		return false;
	}

	private static boolean hasMelee(int randomWeaponSet) {
		if (randomWeaponSet <= 7) {
			return true;
		}
		return false;
	}

	/**
	 * Set all equipment except weapon and shield.
	 */
	public static int[][] equipmentSetBot(Player bot) {
		boolean initiate = bot.botPkType.equals("INITIATE");
		int randomOutFitSet = Misc.random(GameType.isOsrs() ? robeAndHatSet.length- 1 : robeAndHatSetPreEOC.length -1);
		int randomWeaponSet = initiate ? Misc.random(INITIATE_GEAR) : Misc.random(weaponSetBot.length - 1);
		int ammoAmount = 15;
		boolean primaryRanged = isPrimaryRanged(randomWeaponSet);
		bot.botPureWeaponSet = randomWeaponSet;

		// @formatter:off.
		int[][] equipment =
				{
						{
								initiate ? 5574 : GameType.isOsrs() ? robeAndHatSet[randomOutFitSet][0] : robeAndHatSetPreEOC[randomOutFitSet][0], 1 // Head.
						},
						{
								primaryRanged ? 10499 : QuickSetUp.getTeamCape(true), 1 // Cape.
						},
						{
								weaponSetBot[randomWeaponSet][3], 1 // Amulet.
						},
						{
								weaponSetBot[randomWeaponSet][0], 1 // Weapon.
						},
						{
								initiate ? 5575 :  GameType.isOsrs() ? robeAndHatSet[randomOutFitSet][1] : robeAndHatSetPreEOC[randomOutFitSet][1], 1 // Body.
						},
						{
								initiate ? (randomWeaponSet == 4 ? -1 : 8848) : weaponSetBot[randomWeaponSet][1], 1 // Shield.
						},
						{
								-1, 1 // Empty.
						},
						{
								initiate ? 5576 : (primaryRanged ? 2497 :  GameType.isOsrs() ? robeAndHatSet[randomOutFitSet][2] : robeAndHatSetPreEOC[randomOutFitSet][2]), 1  // Leg.
						},
						{
								-1, 1 // Empty.
						},
						{
								7458, 1 // Hand.
						},
						{
								3105, 1 // Feet.
						},
						{
								-1, 1 // Empty.
						},
						{
								-1, 1 // Ring.
						},
						{
								weaponSetBot[randomWeaponSet][2], ammoAmount // Arrow.
						}
				};
		// @formatter:on.
		return equipment;
	}

	/**
	 * Set all inventory, including weapon and shield in equipment.
	 */
	public static int[][] inventory(Player bot) {
		int randomWeaponSet = bot.botPureWeaponSet;
		bot.botSpecialAttackWeapon = weaponSetBot[randomWeaponSet][4];
		bot.botDebug.add("Special attack weapon set to: " + ItemAssistant.getItemName(bot.botSpecialAttackWeapon));
		bot.botPrimaryWeapon = weaponSetBot[randomWeaponSet][0];
		bot.botSpecialAttackWeaponShield = 0;
		bot.botShield = bot.botPkType.equals("INITIATE") ? (randomWeaponSet == 4 ? -1 : 8848) : weaponSetBot[randomWeaponSet][1];
		bot.botArrowSpecial = 0;
		bot.botArrowPrimary = 0;

		// If set is saradomin sword, set defender as special shield.
		if (bot.botPkType.equals("INITIATE") && randomWeaponSet == 4) {
			bot.botSpecialAttackWeaponShield = 8848;
			ItemAssistant.addItem(bot, 8848, 1);
		}

		if (weaponSetBot[randomWeaponSet][4] == 11235) // Dark bow.
		{
			ItemAssistant.addItem(bot, 11212, 6); // Dragon arrow.
			bot.botArrowSpecial = 11212;
			bot.botArrowPrimary = weaponSetBot[randomWeaponSet][2];
		}

		// Also change the method isPrimaryRanged.
		if (isPrimaryRanged(randomWeaponSet)) {
			ItemAssistant.addItem(bot, 2444, 1); // Ranging potion.
		}
		if (hasMelee(randomWeaponSet)) {
			ItemAssistant.addItem(bot, 2440, 1); // Super strength.
		}


		int[][] inventory =
				{
						// @formatter:off.
						{
								weaponSetBot[randomWeaponSet][4], 1 // Extra weapon.
						},
						{
								2434, 1 // Prayer potion.
						},
						{
								2434, 1 // Prayer potion.
						}

				};
		return inventory;
	}

	;

	public static int[][] inventory = {
			{
					2440, 1
			},
			{
					2436, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					6685, 1
			},
			{
					3024, 1
			},
			{
					3024, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			},
			{
					-1, 1
			}
	};

}
