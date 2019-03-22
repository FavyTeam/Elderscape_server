package game.content.quicksetup;

import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Store the main melee equipment and inventory data.
 *
 * @author MGT Madness, created on 16-03-2015.
 */
public class MeleeMain {


	public final static int[] expensivePrimaryWeapons =
			{
					// @formatter:off.
					11802, // Armadyl Godsword.
					// @formatter:on.
			};

	public final static int[][] welfareSpecialAttackWeapons =
			{
					// @formatter:off.
					{
							1215, 25 // Dragon dagger.
					},
					{
							1434, 25 // Dragon mace.
					}
					// @formatter:on.
			};

	public final static int[][] allSpecialAttackWeapons =
			{
					// @formatter:off.
					{
							1215, 25 // Dragon dagger.
					},
					{
							1434, 25 // Dragon mace.
					},
					{
							11802, 50 // Armadyl Godsword.
					},
					{
							20784, 50 // Dragon claws.
					},
					{
							13576, 50 // Dragon warhammer.
					}
			};


	public static int[][] inventory = {
			{
					2440, 1
			},
			{
					2436, 1
			},
			{
					3024, 1
			},
			{
					557, 200
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
					9075, 80
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					560, 40
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					1215, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			},
			{
					385, 1
			}
	};

	/**
	 * Set all inventory, including weapon and shield in equipment.
	 */
	public static int[][] inventory(Player bot) {
		boolean berserker = bot.botPkType.equals("BERSERKER");
		int defender = berserker ? 8850 : 12954;
		int primaryWeapon = -1;
		int shieldItem = -1;
		int specialAttackWeapon = -1;
		int random = Misc.random(1, 100);
		if (random <= 55) {
			primaryWeapon = berserker ? 4587 : 4151; // Dragon scimitar.
			shieldItem = defender; // Dragon defender.
			specialAttackWeapon = allSpecialAttackWeapons[Misc.random(allSpecialAttackWeapons.length - (berserker ? 2 : 1))][0];
		} else if (random <= 85) {
			primaryWeapon = berserker ? 4587 : 4151; // Abyssal whip.
			shieldItem = defender; // Dragon defender.
			specialAttackWeapon = welfareSpecialAttackWeapons[Misc.random(welfareSpecialAttackWeapons.length - 1)][0];
		} else {
			primaryWeapon = expensivePrimaryWeapons[Misc.random(expensivePrimaryWeapons.length - 1)];
			if (primaryWeapon == 11802) // Armadyl godsword.
			{
				specialAttackWeapon = primaryWeapon;
			} else {
				specialAttackWeapon = welfareSpecialAttackWeapons[Misc.random(welfareSpecialAttackWeapons.length - 1)][0];
			}
		}
		bot.botSpecialAttackWeapon = specialAttackWeapon;
		bot.botDebug.add("Special attack weapon set to: " + specialAttackWeapon);
		bot.botPrimaryWeapon = primaryWeapon;
		ItemAssistant.replaceEquipmentSlot(bot, ServerConstants.WEAPON_SLOT, primaryWeapon, 1, false);
		if (shieldItem > 0) {
			ItemAssistant.replaceEquipmentSlot(bot, ServerConstants.SHIELD_SLOT, shieldItem, 1, false);
		}

		// Dragon dagger and dragon mace.
		if ((specialAttackWeapon == 1215 || specialAttackWeapon == 1434) && shieldItem == -1) {
			ItemAssistant.addItem(bot, defender, 1); // Dragon defender.
			shieldItem = defender;
			bot.botSpecialAttackWeaponShield = defender;
		} else {
			bot.botSpecialAttackWeaponShield = 0;
		}
		bot.botShield = shieldItem;

		int[][] inventory = {
				// @formatter:off.
				{
						primaryWeapon == specialAttackWeapon ? 385 : specialAttackWeapon, 1
				},
				{
						2440, 1 // Super strength.
				},
				{
						2436, 1 // Super attack.
				},
				{
						2442, 1 // Super defence.
				},
				{
						2434, 1 // Super restore.
				},
				{
						2434, 1 // Super restore.
				},
				{
						2434, 1 // Super restore.
				},
				{
						557, 200 // Earth rune.
				},
				{
						9075, 80 // Astral rune.
				},
				{
						560, 40 // Death rune.
				}
				// @formatter:on.
		};
		return inventory;
	}

	;

	/**
	 * Set all equipment except weapon and shield.
	 */
	public static int[][] equipmentSetBot(boolean berserker) {

		// @formatter:off.
		int[][] equipment =
				{
						{
								QuickSetUp.getMainMeleeHelmetBot(berserker), 1 // Head.
						},
						{
								QuickSetUp.getTeamCape(true), 1 // Cape.
						},
						{
								QuickSetUp.getMeleeAmuletBot(), 1 // Amulet.
						},
						{
								-1, 1 // Weapon.
						},
						{
								QuickSetUp.getMeleePlatebodyBot(), 1 // Body.
						},
						{
								-1, 1 // Shield.
						},
						{
								-1, 1 // Empty.
						},
						{
								1079, 1  // Leg.
						},
						{
								-1, 1 // Empty.
						},
						{
								7462, 1 // Hand.
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
								-1, 1 // Arrow.
						}
				};
		return equipment;
	}

	public static int[][] equipmentSet(Player player) {

		int headSlotItem = 10828; // Helm of Neitiznot.
		int bodySlotItem = 1127; // Rune plate body.
		int legSlotItem = 1079; // Rune plate legs.


		int[][] equipment = {
				{
						headSlotItem, 1
				},
				{
						QuickSetUp.getTeamCape(false), 1
				},
				{
						1712, 1
				},
				{
						4587, 1 // Dragon scimitar.
				},
				{
						bodySlotItem, 1
				},
				{
						8850, 1
				},
				{
						-1, 1
				},
				{
						legSlotItem, 1
				},
				{
						-1, 1
				},
				{
						7461, 1
				},
				{
						3105, 1
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
		return equipment;
	}

}
