package game.content.miscellaneous;

import core.ServerConstants;
import game.content.profile.ProfileSearch;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * Special attack weapon maximum hit tracker.
 *
 * @author MGT Madness, created on 04-02-2015.
 */
public class SpecialAttackTracker {

	public static void displayInterface(Player player, boolean toggle) {

		if (toggle) {
			player.viewingNpcMaxHits = !player.viewingNpcMaxHits;
		}
		boolean npc = player.viewingNpcMaxHits;
		Player searched = PlayerHandler.players[player.getProfileSearchOnlinePlayerId()];
		if (player.isProfileSearchOnline) {
			if (npc) {
				for (int index = 0; index < ProfileSearch.maximumSpecialAttackNpc.length; index++) {
					ProfileSearch.maximumSpecialAttackNpc[index] = searched.maximumSpecialAttackNpc[index];
					ProfileSearch.weaponAmountUsedNpc[index] = searched.weaponAmountUsedNpc[index];
				}
			} else {
				for (int index = 0; index < ProfileSearch.maximumSpecialAttack.length; index++) {
					ProfileSearch.maximumSpecialAttack[index] = searched.maximumSpecialAttack[index];
					ProfileSearch.weaponAmountUsed[index] = searched.weaponAmountUsed[index];
				}
			}
		}
		if (npc) {

			String[][] array =
					{
							{ProfileSearch.maximumSpecialAttackNpc[11] + "-" + ProfileSearch.maximumSpecialAttackNpc[12], "Used " + ProfileSearch.weaponAmountUsedNpc[11]},
							{ProfileSearch.maximumSpecialAttackNpc[7] + "-" + ProfileSearch.maximumSpecialAttackNpc[8], "Used " + ProfileSearch.weaponAmountUsedNpc[7]},
							{ProfileSearch.maximumSpecialAttackNpc[5] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[5]},
							{ProfileSearch.maximumSpecialAttackNpc[9] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[9]},
							{
									"(" + ProfileSearch.maximumSpecialAttackNpc[15] + "-" + ProfileSearch.maximumSpecialAttackNpc[16] + "-"
									+ ProfileSearch.maximumSpecialAttackNpc[17] + "-" + ProfileSearch.maximumSpecialAttackNpc[18] + ")",
									"Used " + ProfileSearch.weaponAmountUsedNpc[15]
							},
							{ProfileSearch.maximumSpecialAttackNpc[3] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[3]},
							{ProfileSearch.maximumSpecialAttackNpc[0] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[0]},
							{ProfileSearch.maximumSpecialAttackNpc[1] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[1]},
							{ProfileSearch.maximumSpecialAttackNpc[4] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[4]},
							{ProfileSearch.maximumSpecialAttackNpc[13] + "-" + ProfileSearch.maximumSpecialAttackNpc[14], "Used " + ProfileSearch.weaponAmountUsedNpc[13]},
							{ProfileSearch.maximumSpecialAttackNpc[33] + "-" + ProfileSearch.maximumSpecialAttackNpc[34], "Used " + ProfileSearch.weaponAmountUsedNpc[33]},
							{ProfileSearch.maximumSpecialAttackNpc[32] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[32]},
							{ProfileSearch.maximumSpecialAttackNpc[2] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[2]},
							{ProfileSearch.maximumSpecialAttackNpc[10] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[10]},
							{ProfileSearch.maximumSpecialAttackNpc[27] + "-" + ProfileSearch.maximumSpecialAttackNpc[28], "Used " + ProfileSearch.weaponAmountUsedNpc[27]},
							{ProfileSearch.maximumSpecialAttackNpc[25] + "-" + ProfileSearch.maximumSpecialAttackNpc[26], "Used " + ProfileSearch.weaponAmountUsedNpc[25]},
							{ProfileSearch.maximumSpecialAttackNpc[29] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[29]},
							{ProfileSearch.maximumSpecialAttackNpc[30] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[30]},
							{ProfileSearch.maximumSpecialAttackNpc[31] + "", "Used " + ProfileSearch.weaponAmountUsedNpc[31]},
					};

			for (int index = 0; index < array.length; index++) {
				player.getPA().sendFrame126(array[index][0], 25415 + (index * 5));
				player.getPA().sendFrame126(array[index][1], 25413 + (index * 5));
			}
		} else {
			String[][] array =
					{
							{ProfileSearch.maximumSpecialAttack[11] + "-" + ProfileSearch.maximumSpecialAttack[12], "Used " + ProfileSearch.weaponAmountUsed[11]},
							{ProfileSearch.maximumSpecialAttack[7] + "-" + ProfileSearch.maximumSpecialAttack[8], "Used " + ProfileSearch.weaponAmountUsed[7]},
							{ProfileSearch.maximumSpecialAttack[5] + "", "Used " + ProfileSearch.weaponAmountUsed[5]},
							{ProfileSearch.maximumSpecialAttack[9] + "", "Used " + ProfileSearch.weaponAmountUsed[9]},
							{
									"(" + ProfileSearch.maximumSpecialAttack[15] + "-" + ProfileSearch.maximumSpecialAttack[16] + "-" + ProfileSearch.maximumSpecialAttack[17] + "-"
									+ ProfileSearch.maximumSpecialAttack[18] + ")",
									"Used " + ProfileSearch.weaponAmountUsed[15]
							},
							{ProfileSearch.maximumSpecialAttack[3] + "", "Used " + ProfileSearch.weaponAmountUsed[3]},
							{ProfileSearch.maximumSpecialAttack[0] + "", "Used " + ProfileSearch.weaponAmountUsed[0]},
							{ProfileSearch.maximumSpecialAttack[1] + "", "Used " + ProfileSearch.weaponAmountUsed[1]},
							{ProfileSearch.maximumSpecialAttack[4] + "", "Used " + ProfileSearch.weaponAmountUsed[4]},
							{ProfileSearch.maximumSpecialAttack[13] + "-" + ProfileSearch.maximumSpecialAttack[14], "Used " + ProfileSearch.weaponAmountUsed[13]},
							{ProfileSearch.maximumSpecialAttack[33] + "-" + ProfileSearch.maximumSpecialAttack[34], "Used " + ProfileSearch.weaponAmountUsed[33]},
							{ProfileSearch.maximumSpecialAttack[32] + "", "Used " + ProfileSearch.weaponAmountUsed[32]},
							{ProfileSearch.maximumSpecialAttack[2] + "", "Used " + ProfileSearch.weaponAmountUsed[2]},
							{ProfileSearch.maximumSpecialAttack[10] + "", "Used " + ProfileSearch.weaponAmountUsed[10]},
							{ProfileSearch.maximumSpecialAttack[27] + "-" + ProfileSearch.maximumSpecialAttack[28], "Used " + ProfileSearch.weaponAmountUsed[27]},
							{ProfileSearch.maximumSpecialAttack[25] + "-" + ProfileSearch.maximumSpecialAttack[26], "Used " + ProfileSearch.weaponAmountUsed[25]},
							{ProfileSearch.maximumSpecialAttack[29] + "", "Used " + ProfileSearch.weaponAmountUsed[29]},
							{ProfileSearch.maximumSpecialAttack[30] + "", "Used " + ProfileSearch.weaponAmountUsed[30]},
							{ProfileSearch.maximumSpecialAttack[31] + "", "Used " + ProfileSearch.weaponAmountUsed[31]},
					};

			for (int index = 0; index < array.length; index++) {
				player.getPA().sendFrame126(array[index][0], 25415 + (index * 5));
				player.getPA().sendFrame126(array[index][1], 25413 + (index * 5));
			}
		}
		player.getPA().sendFrame126("Change to max hits on " + (npc ? "players" : "Npcs"), 20250);
		player.getPA().sendFrame126("Max hits of " + Misc.capitalize(player.getProfileNameSearched()), 25405);
		player.getPA().displayInterface(25403);
	}

	/**
	 * Reset the specialAttackWeaponUsed array data.
	 *
	 * @param player The associated player.
	 */
	public static void resetSpecialAttackWeaponUsed(Player player) {
		for (int i = 0; i < player.specialAttackWeaponUsed.length; i++) {
			player.specialAttackWeaponUsed[i] = 0;
		}
	}

	/**
	 * Save the maxium damage of a special attack weapon if it exceeds the player's previous maximum damage.
	 * This only applies to single and double hitsplats.
	 *
	 * @param player The associated player.
	 * @param damage The damage dealt.
	 * @param secondHitsplat True, if the damage belongs to a secondary hitsplat like DDS/Halbred/Saradomin sword.
	 */
	public static void saveMaximumDamage(Player player, int damage, String hitSplat, boolean onNpc) {
		if (hitSplat.equals("FIRST")) {
			player.firstHitSplatDamage = damage;
		}
		for (int i = 0; i < player.specialAttackWeaponUsed.length; i++) {
			if (i != 31 && hitSplat.equals("VENGEANCE")) {
				continue;
			}
			if (player.specialAttackWeaponUsed[i] == 1) {
				if (damage > (onNpc ? player.maximumSpecialAttackNpc[i] : player.maximumSpecialAttack[i]) && System.currentTimeMillis() - player.lastSpecialAttackSaved > 100) {
					if (onNpc) {
						player.maximumSpecialAttackNpc[i] = damage;
					} else {
						player.maximumSpecialAttack[i] = damage;
					}
					boolean announce = false;
					String itemName = "";

					if (damage >= 50) {
						announce = true;
					}
					if (announce) {
						itemName = ServerConstants.SPECIAL_ATTACK_SAVE_NAMES[i];
						if (!onNpc) {
							player.getPA().sendScreenshot(itemName + " " + damage, 1);
						}
						player.getPA().sendMessage(
								ServerConstants.PALE_DARK_BLUE_COL + "New personal record of " + damage + " damage with " + itemName + " on a " + (onNpc ? "Npc" : "player") + "!");
					}
				}
			} else if (player.specialAttackWeaponUsed[i] == 2) {
				boolean broken = false;
				if (onNpc) {
					if ((damage + player.firstHitSplatDamage) > (player.maximumSpecialAttackNpc[i] + player.maximumSpecialAttackNpc[i + 1]) && (
							System.currentTimeMillis() - player.lastSpecialAttackSaved <= 100
							|| player.specialAttackWeaponUsed[21] == 2 && System.currentTimeMillis() - player.lastSpecialAttackSaved <= 1500)) {
						player.maximumSpecialAttackNpc[i] = damage;
						player.maximumSpecialAttackNpc[i + 1] = player.firstHitSplatDamage;
						broken = true;
					}
				} else {
					if ((damage + player.firstHitSplatDamage) > (player.maximumSpecialAttack[i] + player.maximumSpecialAttack[i + 1]) && (
							System.currentTimeMillis() - player.lastSpecialAttackSaved <= 100
							|| player.specialAttackWeaponUsed[21] == 2 && System.currentTimeMillis() - player.lastSpecialAttackSaved <= 1500)) {
						player.maximumSpecialAttack[i] = damage;
						player.maximumSpecialAttack[i + 1] = player.firstHitSplatDamage;
						broken = true;
					}
				}
				if (hitSplat.equals("SECOND") && broken) {
					boolean announce = false;
					String itemName = "";
					int currentDamage = damage + player.firstHitSplatDamage;
					if (currentDamage >= 50) {
						announce = true;
					}

					if (announce) {
						itemName = ServerConstants.SPECIAL_ATTACK_SAVE_NAMES[i];
						if (!onNpc) {
							player.getPA().sendScreenshot(itemName + " " + damage, itemName.equals("Dark bow") ? 2 : 1);
						}
						player.getPA().sendMessage(
								ServerConstants.PALE_DARK_BLUE_COL + "New personal record of " + currentDamage + " damage with " + itemName + " on a " + (onNpc ? "Npc" : "player")
								+ "!");
					}
				}
			}
		}
		if (!hitSplat.equals("VENGEANCE")) {
			player.lastSpecialAttackSaved = System.currentTimeMillis();
		}
	}

	/**
	 * Gather data on dragon claws damage, to be later used to save the maximum damage.
	 */
	public static void storeDragonClawsDamage(Player player, int damage1, int damage2, int damage3, int damage4) {
		player.specialAttackWeaponUsed[15] = 1;
		if (damage1 != -1) {
			player.storeDragonClawsDamage[0] = damage1;
		}
		if (damage2 != -1) {
			player.storeDragonClawsDamage[1] = damage2;
		}
		if (damage3 != -1) {
			player.storeDragonClawsDamage[2] = damage3;
		}
		if (damage4 != -1) {
			player.storeDragonClawsDamage[3] = damage4;
		}
	}

	public static void saveDragonClawsMaximumDamage(Player player, boolean npc) {
		boolean message = false;
		if (npc) {
			if ((player.storeDragonClawsDamage[0] + player.storeDragonClawsDamage[1] + player.storeDragonClawsDamage[2] + player.storeDragonClawsDamage[3]) > (
					player.maximumSpecialAttackNpc[15] + player.maximumSpecialAttackNpc[16] + player.maximumSpecialAttackNpc[17] + player.maximumSpecialAttackNpc[18])) {
				player.maximumSpecialAttackNpc[15] = player.storeDragonClawsDamage[0];
				player.maximumSpecialAttackNpc[16] = player.storeDragonClawsDamage[1];
				player.maximumSpecialAttackNpc[17] = player.storeDragonClawsDamage[2];
				player.maximumSpecialAttackNpc[18] = player.storeDragonClawsDamage[3];
				message = true;
			}

		} else {
			if ((player.storeDragonClawsDamage[0] + player.storeDragonClawsDamage[1] + player.storeDragonClawsDamage[2] + player.storeDragonClawsDamage[3]) > (
					player.maximumSpecialAttack[15] + player.maximumSpecialAttack[16] + player.maximumSpecialAttack[17] + player.maximumSpecialAttack[18])) {
				player.maximumSpecialAttack[15] = player.storeDragonClawsDamage[0];
				player.maximumSpecialAttack[16] = player.storeDragonClawsDamage[1];
				player.maximumSpecialAttack[17] = player.storeDragonClawsDamage[2];
				player.maximumSpecialAttack[18] = player.storeDragonClawsDamage[3];
				message = true;
			}

		}
		int damage = player.storeDragonClawsDamage[0] + player.storeDragonClawsDamage[1] + player.storeDragonClawsDamage[2] + player.storeDragonClawsDamage[3];
		if (message && damage >= 50) {
			if (!npc) {
				player.getPA().sendScreenshot("Dragon claws " + damage, 1);
			}
			player.getPA()
			      .sendMessage(ServerConstants.PALE_DARK_BLUE_COL + "New personal record of " + damage + " damage with Dragon claws on a " + (npc ? "Npc" : "player") + "!");
		}

	}

}
