package game.content.combat;

import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.interfaces.InterfaceAssistant;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Everything related to the Combat/Attack interface.
 */
public class CombatInterface {


	private static String[] unarmedItemNames =
			{"unarmed", "snowball", "easter carrot", "boxing gloves", "rubber chicken", "event rpg"};

	/**
	 * @param player
	 * @param exclude List of Combat Styles that are not in the current interface.
	 * @param changeTo If current combat style contains any of the 'exclude', then change combat style to 'changeTo'
	 */
	private static void reAdjustCombatStyle(Player player, String exclude, int changeTo) {
		if (exclude.contains("AGGRESSIVE") && player.getCombatStyle(ServerConstants.AGGRESSIVE)) {
			player.setCombatStyle(changeTo);
			return;
		}
		if (exclude.contains("ACCURATE") && player.getCombatStyle(ServerConstants.ACCURATE)) {
			player.setCombatStyle(changeTo);
			return;
		}
		if (exclude.contains("CONTROLLED") && player.getCombatStyle(ServerConstants.CONTROLLED)) {
			player.setCombatStyle(changeTo);
			return;
		}
		if (exclude.contains("DEFENSIVE") && player.getCombatStyle(ServerConstants.DEFENSIVE)) {
			player.setCombatStyle(changeTo);
			return;
		}
	}

	/**
	 * Update the combat interface with the correct interface according to the weapon wielded.
	 **/
	public static void showCombatInterface(Player player, int weapon) {
		String itemName = ItemAssistant.getItemName(weapon);
		for (int i = 0; i < unarmedItemNames.length; i++) {
			if (itemName.toLowerCase().equals(unarmedItemNames[i])) {
				player.playerAssistant.setSidebarInterface(0, 5855); // punch, kick, block
				player.getPA().sendFrame126(itemName, 5857);
				reAdjustCombatStyle(player, "CONTROLLED", ServerConstants.AGGRESSIVE);
				CombatInterface.updateClickedCombatStyle(player);
				return;
			}
		}
		boolean doNotChange = false;
		if ((GameType.isPreEoc() && weapon == 19780)) {
			player.playerAssistant.setSidebarInterface(0, 4705);
			player.getPA().sendFrame246(426, 200, weapon);
			player.getPA().sendFrame126(itemName, 4708);
			reAdjustCombatStyle(player, "CONTROLLED", ServerConstants.AGGRESSIVE);
			doNotChange = true;
		} else if (itemName.toLowerCase().contains("whip")
				|| itemName.toLowerCase().contains("abyssal tentacle")) {
			player.playerAssistant.setSidebarInterface(0, 12290); // flick, lash, deflect
			player.getPA().sendFrame246(12291, 200, weapon);
			player.getPA().sendFrame126(itemName, 12293);
			reAdjustCombatStyle(player, "AGGRESSIVE", ServerConstants.CONTROLLED);
			doNotChange = true;
		} else if (itemName.toLowerCase().contains("chinchompa")) {
			player.playerAssistant.setSidebarInterface(0, 27500); // first param
			player.getPA().sendFrame126(itemName, 27503); // first param + 2
			reAdjustCombatStyle(player, "DEFENSIVE", ServerConstants.CONTROLLED);
			doNotChange = true;
		} else if (itemName.toLowerCase().contains("dagger")) {
			player.playerAssistant.setSidebarInterface(0, 2276); // stab, lunge, slash, block
			player.getPA().sendFrame246(2277, 200, weapon);
			player.getPA().sendFrame126(itemName, 2279);
		}
		else if (itemName.toLowerCase().contains("bow") || itemName.toLowerCase().contains("ballista") || itemName.toLowerCase().contains("cannon") || itemName.toLowerCase().contains("javelin") || itemName.toLowerCase().contains("throwing") || itemName.toLowerCase().contains("dragon thrownaxe")) {
			player.playerAssistant.setSidebarInterface(0, 1764); // accurate, rapid, longrange
			player.getPA().sendFrame246(1765, 200, weapon);
			player.getPA().sendFrame126(itemName, 1767);
			reAdjustCombatStyle(player, "DEFENSIVE", ServerConstants.CONTROLLED);
		} else if (!itemName.toLowerCase().contains("saradomin sword") && !itemName.toLowerCase().contains("blessed sword") && !itemName.toLowerCase().contains("2h") && (
				itemName.toLowerCase().contains("rapier") || itemName.toLowerCase().contains("sword") && !itemName.toLowerCase().contains("godsword") && !itemName.toLowerCase()
				                                                                                                                                                  .contains(
						                                                                                                                                                  "longsword"))
				|| itemName.toLowerCase().contains("toktz-xil-ek") || itemName.toLowerCase().contains("toktz-xil-ak")) {
			player.playerAssistant.setSidebarInterface(0, 2276); // stab, lunge, slash, block
			player.getPA().sendFrame246(2277, 200, weapon);
			player.getPA().sendFrame126(itemName, 2279);
		} else if (itemName.toLowerCase().contains("staff") || itemName.toLowerCase().contains("wand") || itemName.toLowerCase().contains("staff of light")
				|| itemName.toLowerCase().contains("trident") || itemName.toLowerCase().contains("thammaron's sceptre")) {
			player.playerAssistant.setSidebarInterface(0, 328); // bash, pound, focus.
			player.getPA().sendFrame246(329, 200, weapon);
			player.getPA().sendFrame126(itemName, 331);
			reAdjustCombatStyle(player, "CONTROLLED", ServerConstants.AGGRESSIVE);
			doNotChange = true;
		}
		else if (itemName.toLowerCase().contains("dart") || itemName.toLowerCase().contains("knife") || itemName.toLowerCase().contains("blowpipe") || itemName.toLowerCase()
		                                                                                                                                                         .contains(
				                                                                                                                                                         "toktz-xil-ul")) {
			player.playerAssistant.setSidebarInterface(0, 4446); // accurate, rapid, longrange
			player.getPA().sendFrame246(4447, 200, weapon);
			player.getPA().sendFrame126(itemName, 4449);
			reAdjustCombatStyle(player, "DEFENSIVE", ServerConstants.CONTROLLED);
		} else if (itemName.toLowerCase().contains("pickaxe")) {
			player.playerAssistant.setSidebarInterface(0, 5570); // spike, impale, smash, block
			player.getPA().sendFrame246(5571, 200, weapon);
			player.getPA().sendFrame126(itemName, 5573);
			reAdjustCombatStyle(player, "CONTROLLED", ServerConstants.AGGRESSIVE);
			doNotChange = true;
		} else if (itemName.toLowerCase().contains("axe") || itemName.toLowerCase().contains("hatchet")) {
			player.playerAssistant.setSidebarInterface(0, 1698); // chop, hack, smash, block
			player.getPA().sendFrame246(1699, 200, weapon);
			player.getPA().sendFrame126(itemName, 1701);
			reAdjustCombatStyle(player, "CONTROLLED", ServerConstants.AGGRESSIVE);
			doNotChange = true;
		} else if (itemName.toLowerCase().contains("claws")) {
			player.playerAssistant.setSidebarInterface(0, 7762);
			player.getPA().sendFrame246(7763, 200, weapon);
			player.getPA().sendFrame126(itemName, 7765);
		} else if (itemName.toLowerCase().contains("halberd") || itemName.toLowerCase().contains("scythe")) {
			player.playerAssistant.setSidebarInterface(0, 8460); // jab, swipe, fend
			player.getPA().sendFrame246(8461, 200, weapon);
			player.getPA().sendFrame126(itemName, 8463);
			reAdjustCombatStyle(player, "ACCURATE", ServerConstants.AGGRESSIVE);
		} else if (itemName.toLowerCase().contains("spear") || itemName.toLowerCase().contains("hasta")) {
			player.playerAssistant.setSidebarInterface(0, 4679); // lunge, swipe, pound, block
			player.getPA().sendFrame246(4680, 200, weapon);
			player.getPA().sendFrame126(itemName, 4682);
			reAdjustCombatStyle(player, "AGGRESSIVE ACCURATE", ServerConstants.CONTROLLED);
			doNotChange = true;
		} else if (itemName.toLowerCase().contains("mace") || itemName.toLowerCase().contains("barrelchest") || itemName.toLowerCase().contains("flail") || itemName.toLowerCase()
		                                                                                                                                                            .contains(
				                                                                                                                                                            "cane")) {
			player.playerAssistant.setSidebarInterface(0, 3796);
			player.getPA().sendFrame246(3797, 200, weapon);
			player.getPA().sendFrame126(itemName, 3799);
		} else if (itemName.toLowerCase().contains("maul") || itemName.toLowerCase().contains("hammer") || itemName.toLowerCase().contains("tzhaar-ket-om")
		           || itemName.toLowerCase().contains("dinh's") || itemName.contains("spade") || itemName.contains("club")) {
			player.playerAssistant.setSidebarInterface(0, 425); // war hamer equip.
			player.getPA().sendFrame246(426, 200, weapon);
			player.getPA().sendFrame126(itemName, 428);
			reAdjustCombatStyle(player, "CONTROLLED", ServerConstants.AGGRESSIVE);
			doNotChange = true;
		} else if (itemName.toLowerCase().contains("godsword") || itemName.toLowerCase().contains("2h") || itemName.toLowerCase().contains("saradomin sword")
				|| itemName.toLowerCase().contains("sara's blessed sword")
				|| (GameType.isPreEoc() && weapon == 19780)) {
			player.playerAssistant.setSidebarInterface(0, 4705); // Godsword.
			player.getPA().sendFrame246(426, 200, weapon);
			player.getPA().sendFrame126(itemName, 4708);
			reAdjustCombatStyle(player, "CONTROLLED", ServerConstants.AGGRESSIVE);
			doNotChange = true;
		} else {
			player.playerAssistant.setSidebarInterface(0, 2423); // chop, slash, lunge, block
			player.getPA().sendFrame246(2424, 200, weapon);
			player.getPA().sendFrame126(itemName, 2426);
		}
		switch (weapon) {
			case 20779:
				player.playerAssistant.setSidebarInterface(0, 425); // war hamer equip.
				player.getPA().sendFrame246(426, 200, weapon);
				player.getPA().sendFrame126(itemName, 428);
				reAdjustCombatStyle(player, "CONTROLLED", ServerConstants.AGGRESSIVE);
				doNotChange = true;
				break;
		}

		// Whip is the only main weapon where the aggressive option is controlled, so make it easier by if i use whip and switch to another weapon, it puts the other weapon
		//on aggressive instead of longrange/lunge.
		if (player.wasWearingAggressiveSharedXpWeapon && !doNotChange) {
			reAdjustCombatStyle(player, "CONTROLLED", ServerConstants.AGGRESSIVE);
		}
		player.wasWearingAggressiveSharedXpWeapon = false;
		CombatInterface.updateClickedCombatStyle(player);
	}

	public static void updateClickedCombatStyle(Player player) {

		if (player.autoCasting) {
			return;
		}
		int itemEquipped = player.getWieldedWeapon();
		boolean set = false;
		String weapon = ItemAssistant.getItemName(itemEquipped).toLowerCase();

		if (weapon.startsWith("dragon dagger") || weapon.startsWith("abyssal dagger")) {
			weapon = "dragon dagger";
		} else if (weapon.contains("staff") || weapon.contains("trident")) {
			weapon = "staff";
		} else if (weapon.contains("spear")) {
			weapon = "spear";
		}

		if (weapon.contains("bow") && player.getCombatStyle(ServerConstants.DEFENSIVE)) {
			player.setCombatStyle(ServerConstants.CONTROLLED);
		}
		/*
		 *
		 * Might be needed in future, so when player is on smash mode on dh axe and switches to Ags, then back to dharok axe, maybe its supposed
		 * to go back to smash?
		if (player.aggressiveType.contains("AGGRESSIVE") && player.getCombatStyle(ServerConstants.AGGRESSIVE))
		{
				String parse[] = player.aggressiveType.split(" ");
				//player.aggressiveType = "AGGRESSIVE OTHER1 " + player.getWieldedWeapon() + " " + 1706;
				int weaponId = Integer.parseInt(parse[2]);
				if (player.getWieldedWeapon() == weaponId)
				{
						int frameId = Integer.parseInt(parse[3]);
						player.getPA().sendMessage(":packet:combatstyle " + frameId);
				}
		}
		*/

		// Slash on dragon dagger is wrong because Slash and Lunge are both aggressive.
		// Same issue with Dragon claws and dragon 2h.

		if (weapon.contains("chinchompa")) {
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				player.getPA().sendFrame36(43, 1, false);
			} else if (player.getCombatStyle(ServerConstants.AGGRESSIVE)) {
				player.getPA().sendFrame36(43, 2, false);
			} else if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
				player.getPA().sendFrame36(43, 3, false);
			}
			return;
		}
		switch (weapon) {

			case "torag's hammers":
				if (player.getCombatStyle(ServerConstants.DEFENSIVE)) {
					player.getPA().sendFrame36(43, 2, false);
					set = true;
				}
				break;

			case "spear":
				if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
					player.getPA().sendFrame36(43, 0, false);
					set = true;
				}
				break;

			case "staff":
			case "master wand":
				if (player.getCombatStyle(ServerConstants.DEFENSIVE)) {
					player.getPA().sendFrame36(43, 2, false);
					set = true;
				}
				break;

			case "dragon mace":
				if (player.getCombatStyle(ServerConstants.DEFENSIVE)) {
					player.getPA().sendFrame36(43, 3, false);
					set = true;
				}
				break;

			case "tzhaar-ket-om":
			case "granite maul":
			case "chaotic maul":
				if (player.getCombatStyle(ServerConstants.DEFENSIVE)) {
					player.getPA().sendFrame36(43, 2, false);
					set = true;
				}
				break;


			case "dragon halberd":
				if (player.getCombatStyle(ServerConstants.DEFENSIVE)) {
					player.getPA().sendFrame36(43, 0, false);
					set = true;
				} else if (player.getCombatStyle(ServerConstants.AGGRESSIVE)) {
					player.getPA().sendFrame36(43, 2, false);
					set = true;
				} else if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
					player.getPA().sendFrame36(43, 1, false);
					set = true;
				}
				break;

			case "abyssal whip":
			case "volcanic abyssal whip":
			case "frozen abyssal whip":
			case "abyssal tentacle":
				if (player.getCombatStyle(ServerConstants.DEFENSIVE)) {
					player.getPA().sendFrame36(43, 2, false);
					set = true;
				} else if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
					player.getPA().sendFrame36(43, 1, false);
					set = true;
				}
				break;

			case "dragon warhammer":
				if (player.getCombatStyle(ServerConstants.DEFENSIVE)) {
					player.getPA().sendFrame36(43, 2, false);
					set = true;
				}
				break;

		}
		if (set) {
			return;
		}
		if (player.getWieldedWeapon() > 0) {
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				player.getPA().sendFrame36(43, 0, false);
			} else if (player.getCombatStyle(ServerConstants.AGGRESSIVE)) {
				player.getPA().sendFrame36(43, 1, false);
			} else if (player.getCombatStyle(ServerConstants.DEFENSIVE)) {
				player.getPA().sendFrame36(43, 3, false);
			} else if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
				player.getPA().sendFrame36(43, 2, false);
			}
		} else {
			if (player.getCombatStyle() == 0) {
				player.getPA().sendFrame36(43, 1, false);
			} else if (player.getCombatStyle() == 1) {
				player.getPA().sendFrame36(43, 2, false);
			} else if (player.getCombatStyle() == 2) {
				player.getPA().sendFrame36(43, 0, false);
			} else if (player.getCombatStyle() == 3) {
				player.getPA().sendFrame36(43, 0, false);
			}
		}
	}

	public static boolean isCombatInterfaceButton(Player player, int buttonId) {
		switch (buttonId) {

			// ACCURATE:
			case 107116:

			case 9125:
				// Punch (unarmed)
			case 22230:
			case 6221:
				// flick (whip)
			case 48010:
				// spike (pickaxe)
			case 21200:
				// bash (staff)
			case 1080:
				// chop (axe)
			case 6168:
				// accurate (long bow)
			case 6236:
				// accurate (darts)
			case 17102:
				// stab (dagger)
			case 8234:
				// pound (Mace)
			case 14128:
				// Chop
			case 18103:
				// Chop (claws)
			case 30088:
				// Reap (Pickaxe)
			case 3014:
				// Pound (hammer)
			case 1177:
				// Bash (battlestaff)
			case 23249:
				// Pound (Dragon mace).
			case 14218:
				player.setCombatStyle(ServerConstants.ACCURATE);
				if (player.getAutoCasting()) {
					AutoCast.resetAutocast(player, true);
				}
				return true;

			// AGGRESSIVE:
			case 107115:

			case 9128:
				// Kick (unarmed)
			case 22229:
			case 6220:
				// Impale (pickaxe)
			case 21203:
				// Smash (pickaxe)
			case 21202:
				// Pound (staff)
			case 1079:
				// Hack (axe)
			case 6171:
			case 6235:
			case 17101:
				// Lunge
			case 8237:
				// Slash
			case 8236:
				// Pummel (mace)
			case 14221:
				// Slash
			case 18106:
				// Smash
			case 18105:
			case 30091:
				// Chop (pickaxe)
			case 3017:
				// Jab (pickaxe)
			case 3016:
				// Pummel (hammer)
			case 1176:
				// Pound (battlestaff)
			case 23248:
				//Swipe (halberd)
			case 33019:
				// Smash (axe)
			case 6170:
				player.setCombatStyle(ServerConstants.AGGRESSIVE);
				if (player.getAutoCasting()) {
					AutoCast.resetAutocast(player, true);
				}
				return true;

			// DEFENSIVE:
			case 107114:

			case 9126:
				// Block (unarmed)
			case 22228:
				// deflect (whip)
			case 48008:
				// block (pickaxe)
			case 21201:
				// focus - block (staff)
			case 1078:
				// block (axe)
			case 6169:
				// block (spear)
			case 18078:
				// block (dagger)
			case 8235:
				// block (mace)
			case 14219:
			case 18104:
				// block (claws)
			case 30089:
			case 3015:
				// block (warhammer/hammer)
			case 1175:
				// block (battlestaff)
			case 23247:
				// fend (halberd)
			case 33018:
				player.setCombatStyle(ServerConstants.DEFENSIVE);
				if (player.getAutoCasting()) {
					AutoCast.resetAutocast(player, true);
				}
				return true;

			// CONTROLLED:
			case 9127:
				// lash (whip)
			case 48009:
				// longrange (long bow)
			case 6234:
				// longrange
			case 6219:
				// longrange (darts)
			case 17100:
				// Spike (mace)
			case 14220:
				// Pound (spear)
			case 18079:
				// Swipe (spear)
			case 18080:
				// Lunge (spear)
			case 18077:
			case 33020: // Halberd Jab.
				// Slash (claws)
			case 30090:
				player.setCombatStyle(ServerConstants.CONTROLLED);
				if (player.getAutoCasting()) {
					AutoCast.resetAutocast(player, true);
				}
				return true;
		}
		return false;
	}

	/**
	 * Weapons special bar, adds the spec bars to weapons that require them and
	 * removes the spec bars from weapons which don't require them
	 **/
	public static void addSpecialBar(Player player, int weapon) {
		if (player.getTank()) {
			player.setSpecialAttackAmount(10.0, false);
		}

		if (GameType.isPreEoc()) {
			if (weapon == 11696 || weapon == 11698 || weapon == 11700) { //godswords except ags
				player.getPA().sendFrame171(0, 7699);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7711);
				return;
			}
			if (weapon == 13902) {
				player.getPA().sendFrame171(0, 7474);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7486);
				return;
			} else
			if (weapon == 19780) {
				player.getPA().sendFrame171(0, 7699);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7711);
				return;
			} else if (weapon == 21371) {
				player.getPA().sendFrame171(0, 12323);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 12335);
				return;
			}
		}
		if (GameType.isOsrs() && Misc.arrayHasNumber(ServerConstants.ARMADYL_GODSWORDS_OSRS, weapon, 0) || GameType.isPreEoc() && weapon == 11694) {
			player.getPA().sendFrame171(0, 7699);
			CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7711);
			return;
		}
		if (Combat.hasAbyssalTentacle(player, weapon)) {
			player.getPA().sendFrame171(0, 12323);
			CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 12335);
			return;
		}
		if (ItemAssistant.getItemName(weapon).equals("Dragon claws")) {
			player.getPA().sendFrame171(0, 7800);
			CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7812);
			return;
		}

		if (ItemAssistant.getItemName(weapon).contains("Granite maul")) {
			player.getPA().sendFrame171(0, 7474);
			CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7486);
			return;
		}

		// Dragon crossbow
		if (weapon == 21902 && ServerConfiguration.DEBUG_MODE) {
			player.getPA().sendFrame171(0, 7549);
			CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7561);
			return;
		}

		if (GameType.isOsrs()) {

			String itemName = ItemAssistant.getItemName(weapon);
			if (itemName.equals("Heavy ballista") || itemName.equals("Light ballista")) {
				player.getPA().sendFrame171(0, 7549);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7561);
				return;
			}

			if (itemName.equals("Armadyl crossbow")) {
				player.getPA().sendFrame171(0, 7549);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7561);
				return;
			}
		}
		switch (weapon) {


			case 4151: // Abyssal whip
			case 12773: // Volcanic abyssal whip
			case 12774: // Frozen abyssal whip
			case 15_441:
			case 15_442:
			case 15_443:
			case 15_444:
				player.getPA().sendFrame171(0, 12323);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 12335);
				break;
			case 859:
				// Magic bows
			case 861:
			case 12788: // Magic shortbow (i).
			case 11235:
			case 12765:
			case 12766:
			case 12767:
			case 12768:
			case 13879:
			case 13883:
			case 15241 : // Hand cannon
			case 20849: // Dragon thrown axe
			case 22634: // Morrigan's throwing axe osrs
			case 22636: // Morrigan's javelin osrs
				player.getPA().sendFrame171(0, 7549);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7561);
				break;

			case 22622: // Statius's warhammer osrs
				player.getPA().sendFrame171(0, 7474);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7486);
				break;
			case 4587:
			case 13899:
			case 13901:
			case 20000:
			case 1305: // Dragon longsword
			case 12904: // Toxic staff of the dead
			case 16209: // Toxic staff of the dead
			case 11791: // Staff of the dead
			case 16272: // Toxic staff of the dead
			case 22296: // Staff of light
			case 15486:
			case 22613: // Vesta's longsword osrs
				player.getPA().sendFrame171(0, 7599);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7611);
				break;
			case 11802:
			case 11806:
			case 11808:
			case 11804:
			case 20368: // Ags
			case 20374: // Zgs
			case 20372: // Sgs
			case 20370: // Bgs
			case 11838:
			case 12808: // Sara's blessed sword.
				player.getPA().sendFrame171(0, 7699);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7711);
				break;
			case 3204:
				// d hally
				player.getPA().sendFrame171(0, 8493);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 8505);
				break;
			case 1377:
				// d battleaxe
				player.getPA().sendFrame171(0, 7499);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7511);
				break;

			case 12848:
			case 13576:
			case 21015: // Dinh's bulwark
			case 16259:
				player.getPA().sendFrame171(0, 7474);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7486);
				break;
			case 1249:
			case 11824:
				// dspear //zammy spear
			case 13905:
			case 13907:
			case 11889: // Zamorakian hasta
			case 22610: // Vesta's spear osrs
				player.getPA().sendFrame171(0, 7674);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7686);
				break;

			case 12926: // Toxic blowpipe.
				player.getPA().sendFrame171(0, 7649);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7661);
				break;

			case 1215:
				// dragon dagger
			case 1231:
			case 5680:
			case 5698:
			case 13271: // Abyssal dagger.
			case 21009: // Dragon sword
				player.getPA().sendFrame171(0, 7574);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7586);
				break;
			case 1434:
			case 10887:
				// dragon mace
			case 11061: // Ancient mace
				player.getPA().sendFrame171(0, 7624);
				CombatInterface.specialAmount(player, weapon, player.getSpecialAttackAmount(), 7636);
				break;
			default:
				player.getPA().sendFrame171(1, 7649);
				player.getPA().sendFrame171(1, 7674);
				player.getPA().sendFrame171(1, 7800);
				player.getPA().sendFrame171(1, 7624); // mace interface
				player.getPA().sendFrame171(1, 7474); // hammer, gmaul
				player.getPA().sendFrame171(1, 7499); // axe
				player.getPA().sendFrame171(1, 7549); // bow interface
				player.getPA().sendFrame171(1, 7574); // sword interface
				player.getPA().sendFrame171(1, 7599); // scimmy sword interface, for most swords.
				player.getPA().sendFrame171(1, 7699); // scimmy sword interface, for most swords.
				player.getPA().sendFrame171(1, 8493);
				player.getPA().sendFrame171(1, 12323); // whip interface

				showCombatInterface(player, weapon);
				break;
		}
	}

	/**
	 * Specials bar filling amount
	 **/
	public static void specialAmount(Player player, int weapon, double specAmount, int barId) {
		player.specBarId = barId;
		player.getPA().sendComponentOffset(specAmount >= 10 ? 500 : 0, 0, (--barId));
		player.getPA().sendComponentOffset(specAmount >= 9 ? 500 : 0, 0, (--barId));
		player.getPA().sendComponentOffset(specAmount >= 8 ? 500 : 0, 0, (--barId));
		player.getPA().sendComponentOffset(specAmount >= 7 ? 500 : 0, 0, (--barId));
		player.getPA().sendComponentOffset(specAmount >= 6 ? 500 : 0, 0, (--barId));
		player.getPA().sendComponentOffset(specAmount >= 5 ? 500 : 0, 0, (--barId));
		player.getPA().sendComponentOffset(specAmount >= 4 ? 500 : 0, 0, (--barId));
		player.getPA().sendComponentOffset(specAmount >= 3 ? 500 : 0, 0, (--barId));
		player.getPA().sendComponentOffset(specAmount >= 2 ? 500 : 0, 0, (--barId));
		player.getPA().sendComponentOffset(specAmount >= 1 ? 500 : 0, 0, (--barId));
		CombatInterface.updateSpecialBar(player);
		showCombatInterface(player, weapon);
	}

	/**
	 * Special attack text and what to highlight or blackout
	 **/
	public static void updateSpecialBar(Player player) {
		String percent = Double.toString(player.getSpecialAttackAmount());
		if (percent.contains(".")) {
			percent = percent.replace(".", "");
		}
		if (percent.startsWith("0") && !percent.equals("00")) {
			percent = percent.replace("0", "");
		}
		if (percent.startsWith("0") && percent.equals("00")) {
			percent = percent.replace("00", "0");
		}
		if (player.isUsingSpecial()) {
			player.getPA().sendFrame126("@yel@Special Attack (" + percent + "%)", player.specBarId);
		} else {
			player.getPA().sendFrame126("@bla@Special Attack (" + percent + "%)", player.specBarId);
		}
		/*
		 * Update special orb
		 */
		InterfaceAssistant.updateSpecialAttackOrbPercentage(player, percent);
	}

	public static int sendClickedCombatStyle(Player player, int buttonId) {
		player.aggressiveType = "DEFAULT"; // Reset to normal.
		switch (buttonId) {
			case 48010: // flick
				return 12298;
			case 48009: // controlled
				return 12297;
			case 48008: // defensive
				return 12296;

			case 107116:
				return 27508;
			case 107115:
				return 27507;
			case 107114:
				return 27506;

			case 9128:
				return 2432;
			case 9125:
				return 2429;
			case 9127:
				return 2431;
			case 9126:
				return 2430;

			case 8237:
				return 2285;
			case 8234:
				return 2282;
			case 8236:
				player.aggressiveType = "AGGRESSIVE OTHER1 " + player.getWieldedWeapon() + " " + 2284;
				return 2284;
			case 8235:
				return 2283;

			case 6235:
				return 1771;
			case 6236:
				return 1772;
			case 6234:
				return 1770;

			case 1080:
				return 336;
			case 1079:
				return 335;
			case 1078:
				return 334;

			case 33020:
				return 8468;
			case 33019:
				return 8467;
			case 33018:
				return 8466;

			case 6168:
				return 1704;
			case 6171:
				return 1707;
			case 6170:
				player.aggressiveType = "AGGRESSIVE OTHER1 " + player.getWieldedWeapon() + " " + 1706;
				return 1706;
			case 6169:
				return 1705;

			case 17102:
				return 4454;
			case 17101:
				return 4453;
			case 17100:
				return 4452;

			case 14218:
				return 3802;
			case 14221:
				return 3805;
			case 14220:
				return 3804;
			case 14219:
				return 3803;

			case 22230:
				return 5862;
			case 22229:
				return 5861;
			case 22228:
				return 5860;

			case 21200:
				return 5576;
			case 21203:
				return 5579;
			case 21202:
				return 5578;
			case 21201:
				return 5577;

			case 30088:
				return 7768;
			case 30091:
				return 7771;
			case 30090:
				return 7770;
			case 30089:
				return 7769;

			case 18077:
				return 4685;
			case 18080:
				return 4688;
			case 18079:
				return 4687;
			case 18078:
				return 4686;

			case 18103:
				return 4711;
			case 18106:
				return 4714;
			case 18105:
				return 4713;
			case 18104:
				return 4712;

			case 1177:
				return 433;
			case 1176:
				return 432;
			case 1175:
				return 431;
		}
		return 0;
	}
}
