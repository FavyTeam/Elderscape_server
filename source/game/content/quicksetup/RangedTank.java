package game.content.quicksetup;

import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;


/**
 * Store the ranged equipment and inventory data.
 *
 * @author MGT Madness, created on 28-04-2016.
 */
public class RangedTank {
	// Weapon, shield, arrows, extra inventory weapon, extra inventory weapon arrows.
	private static int[][] weaponSet =
			{
					// @formatter:off.
					{
							// Rune crossbow, Unholy book, Dragon bolts (e), Dark bow, Dragon arrows.
							9185, 1201, 9244, 11235, 11212
					},
					{
							// Magic shortbow, Rune arrow, Rune crossbow, Dragon bolts (e)
							861, -1, 892, 9185, 9244
					},
			};

	public static int[][] inventory =
			{
					{
							557, 200
					}, {
					9075, 80
			}, {
					560, 40
			}, {
					3024, 1
			}, {
					2444, 1
			}, {
					6685, 1
			}, {
					3024, 1
			}, {
					3024, 1
			}, {
					9185, 1
			}, {
					9244, 15
			}, {
					1201, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}, {
					385, 1
			}
			};

	/**
	 * Set all equipment except weapon and shield.
	 */
	public static int[][] equipmentSet(Player bot) {
		int randomWeaponSet = 0;
		boolean dHides = Misc.hasPercentageChance(30);
		if (!bot.bot) {
			randomWeaponSet = 1;
			dHides = true;
		}
		int ammoAmount = 14;

		// @formatter:off.
		int[][] equipment =
				{
						{
								dHides ? 10828 : 11664, 1 // Head.
						},
						{
								10499, 1 // Cape.
						},
						{
								1712, 1 // Amulet.
						},
						{
								weaponSet[randomWeaponSet][0], 1 // Weapon.
						},
						{
								dHides ? 2503 : 8839, 1 // Body.
						},
						{
								weaponSet[randomWeaponSet][1], 1 // Shield.
						},
						{
								-1, 1 // Empty.
						},
						{
								dHides ? 2497 : 8840, 1  // Leg.
						},
						{
								-1, 1 // Empty.
						},
						{
								dHides ? 7461 : 8842, 1 // Hand.
						},
						{
								6328, 1 // Feet.
						},
						{
								-1, 1 // Empty.
						},
						{
								-1, 1 // Ring.
						},
						{
								weaponSet[randomWeaponSet][2], !bot.bot ? 25 : ammoAmount // Arrow.
						}
				};
		// @formatter:on.
		return equipment;
	}

	public static int[][] equipmentSetBot(Player bot) {
		int randomWeaponSet = 0;
		boolean dHides = Misc.hasPercentageChance(30);
		if (!bot.bot) {
			randomWeaponSet = 1;
			dHides = true;
		}
		int ammoAmount = 14;
		bot.botPureWeaponSet = randomWeaponSet;

		// @formatter:off.
		int[][] equipment =
				{
						{
								dHides ? 10828 : 11664, 1 // Head.
						},
						{
								10499, 1 // Cape.
						},
						{
								1712, 1 // Amulet.
						},
						{
								weaponSet[randomWeaponSet][0], 1 // Weapon.
						},
						{
								dHides ? 2503 : 8839, 1 // Body.
						},
						{
								weaponSet[randomWeaponSet][1], 1 // Shield.
						},
						{
								-1, 1 // Empty.
						},
						{
								dHides ? 2497 : 8840, 1  // Leg.
						},
						{
								-1, 1 // Empty.
						},
						{
								dHides ? 7461 : 8842, 1 // Hand.
						},
						{
								6328, 1 // Feet.
						},
						{
								-1, 1 // Empty.
						},
						{
								-1, 1 // Ring.
						},
						{
								weaponSet[randomWeaponSet][2], !bot.bot ? 25 : ammoAmount // Arrow.
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
		bot.botSpecialAttackWeapon = weaponSet[randomWeaponSet][3];
		bot.botDebug.add("Special attack weapon set to: " + ItemAssistant.getItemName(bot.botSpecialAttackWeapon));
		bot.botPrimaryWeapon = weaponSet[randomWeaponSet][0];
		bot.botShield = weaponSet[randomWeaponSet][1];
		bot.botArrowPrimary = weaponSet[randomWeaponSet][2];
		bot.botArrowSpecial = weaponSet[randomWeaponSet][4];
		if (bot.botSpecialAttackWeapon > 0) {
			ItemAssistant.addItem(bot, bot.botSpecialAttackWeapon, 1);
			ItemAssistant.addItem(bot, bot.botArrowSpecial, 4);
		}

		// Hand cannon as primary weapon.
		if (bot.botPrimaryWeapon == 15241) {
			bot.botSpecialAttackWeapon = 15241;
			ItemAssistant.replaceEquipmentSlot(bot, ServerConstants.ARROW_SLOT, bot.botArrowPrimary, 40, false);
		}

		int[][] inventory =
				{
						// @formatter:off.
						{
								2444, 1 // Ranging potion.
						},
						{
								2442, 1 // Super defence potion.
						},
						{
								2434, 1 // Prayer potion.
						},
						{
								2434, 1 // Prayer potion.
						},
						{
								560, 20 // Death rune.
						},
						{
								9075, 40 // Astral rune.
						},
						{
								557, 100 // Earth rune.
						}

						// @formatter:on.
				};
		return inventory;
	}

	;

}
