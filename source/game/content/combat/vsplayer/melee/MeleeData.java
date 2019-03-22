package game.content.combat.vsplayer.melee;

import core.GameType;
import core.ServerConstants;
import game.content.music.SoundSystem;
import game.player.Player;
import game.player.PlayerHandler;

/**
 * Handle Melee animations etc..
 *
 * @author MGT Madness, created on 20-11-2013.
 */
public class MeleeData {

	/**
	 * Check if the player is wielding a halberd.
	 *
	 * @param player The associated player.
	 * @return True, if the player is wielding a halberd.
	 */
	public static boolean usingHalberd(Player player) {
		switch (player.getWieldedWeapon()) {
			case 3190:
			case 3192:
			case 3194:
			case 3196:
			case 3198:
			case 3200:
			case 3202:
			case 3204:
				return true;
			default:
				return false;
		}
	}

	public static int getWeaponAnimation(Player player, String weaponName) {
		weaponName = weaponName.toLowerCase();
		Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];

		int weaponAnimationPreEoc = getWeaponAnimationPreEoc(player, victim, weaponName);
		if (weaponAnimationPreEoc != -1) {
			return weaponAnimationPreEoc;
		}
		switch (weaponName) {
			case "unarmed":
				if (player.getCombatStyle(ServerConstants.ACCURATE) || player.getCombatStyle(ServerConstants.DEFENSIVE)) {
					SoundSystem.sendSound(player, victim, 417, 300);
					return GameType.isOsrs() ? 422 : 422;
				}
				if (player.getCombatStyle(ServerConstants.AGGRESSIVE)) {
					SoundSystem.sendSound(player, victim, 417, 300);
					return 423;
				}
				break;
		}
		if (weaponName.contains("halberd")) {
			if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
				return 2080;
			} else {
				return 440;
			}
		}
		if (weaponName.startsWith("dragon dagger")) {
			SoundSystem.sendSound(player, victim, 793, 270);
			return 402;
		}
		if (weaponName.contains("dagger") && !weaponName.contains("abyssal")) {
			SoundSystem.sendSound(player, victim, 793, 270);
			return 386;
		}
		if (weaponName.contains("2h sword")) {
			SoundSystem.sendSound(player, victim, 396, 400);
			//406 is crush/smash for 2h.
			return GameType.isOsrs() ? 407 : 7041;
		}
		if (weaponName.contains("godsword") || weaponName.contains("saradomin sword") || weaponName.contains("blessed sword") || weaponName.contains("spade")) {
			SoundSystem.sendSound(player, victim, 396, 400);
			if (player.getCombatStyle(ServerConstants.DEFENSIVE)) {
				return GameType.isOsrs() ? 7055 : 7049;
			} else {
				return GameType.isOsrs() ? 7045 : 7041;
			}
			//7054 is crush/smash with gs.
		}
		if (weaponName.equalsIgnoreCase("dragon sword") || weaponName.equalsIgnoreCase("rune sword") || weaponName.equalsIgnoreCase("toktz-xil-ak")) {
			if (player.getCombatStyle(ServerConstants.AGGRESSIVE) && player.aggressiveType.contains("AGGRESSIVE OTHER1")) {
				SoundSystem.sendSound(player, victim, 396, 400);
				return 390;
			}
			else {
				SoundSystem.sendSound(player, victim, 396, 400);
				return 386;
			}
		}
		if (weaponName.contains("sword")) {
			SoundSystem.sendSound(player, victim, 396, 400);
			return 390;
		}
		if (weaponName.contains("battleaxe") && !weaponName.equalsIgnoreCase("Leaf-bladed battleaxe") ||
		    //@formatter:off
		    // Bronze to Rune axe (woodcutting)
		    player.getWieldedWeapon() >= 1349 && player.getWieldedWeapon() <= 1359 ||
		    // Dragon axe (woodcutting)
		    player.getWieldedWeapon() == 6739)
		//@formatter:on
		{
			SoundSystem.sendSound(player, victim, 396, 400);
			return 395;
		}
		if (weaponName.contains("scimitar") || weaponName.contains("longsword") || weaponName.contains("darklight")) {
			switch (player.getCombatStyle()) {
				case ServerConstants.ACCURATE:
					SoundSystem.sendSound(player, victim, 396, 400);
					return 390;
				case ServerConstants.AGGRESSIVE:
					SoundSystem.sendSound(player, victim, 396, 400);
					return 390;
				case ServerConstants.DEFENSIVE:
					SoundSystem.sendSound(player, victim, 396, 400);
					return 390;
				case ServerConstants.CONTROLLED:
					SoundSystem.sendSound(player, victim, 396, 400);
					return 386;
			}
		}
		if (weaponName.contains("viggora's chainmace")) {
			switch (player.getCombatStyle()) {
				case ServerConstants.ACCURATE:
					return 245;
				case ServerConstants.AGGRESSIVE:
					return 245;
				case ServerConstants.DEFENSIVE:
					return 245;
				case ServerConstants.CONTROLLED:
					return 246;
			}
		}
		if (weaponName.contains("scythe of vitur")) {
			switch (player.getCombatStyle()) {
				case ServerConstants.AGGRESSIVE:
					return 8056;
				case ServerConstants.DEFENSIVE:
					return 8056;
				case ServerConstants.CONTROLLED:
					return 8010;
			}
		}
		if (weaponName.contains("pickaxe")) {
			SoundSystem.sendSound(player, victim, 396, 400);
			return 401;
		}
		if (weaponName.contains("tentacle")) {
			SoundSystem.sendSound(player, victim, 1080, 300);
			return 1658;
		}
		if (weaponName.equals("elder maul")) {
			return 7516;
		}
		if (weaponName.equals("dragon claws")) {
			return 393;
		}
		if (weaponName.contains("granite maul")) {
			SoundSystem.sendSound(player, victim, 1079, 350);
			return 1665;
		}
		switch (player.getWieldedWeapon()) {
			case 22324: // Ghrazi rapier
				return 8145;

			case 13271: // Abyssal dagger.
				switch (player.getCombatStyle()) {
					case ServerConstants.ACCURATE:
					case ServerConstants.AGGRESSIVE:
						if (player.aggressiveType.contains("OTHER")) {
							return 3294;
						}
						SoundSystem.sendSound(player, victim, 396, 400);
						return 3297;
				}
				return 3294;

			case 20727: // Leaf-bladed battleaxe
				if (player.aggressiveType.contains("OTHER")) {
					return 3852;
				}
				return 7004;
			// Dinh's bulwark
			case 21015:
			case 16259:
				return 7511;
			// Hunting blade
			case 20779:
				return 7328;
			// Toktz-xil-ek.
			case 6525:
				SoundSystem.sendSound(player, victim, 793, 270);
				return 386;
			case 1434:
			case 11061: // Ancient mace
				// Dragon mace
				return 401;

			case 4726:
				// Guthan's spear
				return 2080;
			case 4747:
				// Torags hammers
				return 0x814;
			case 13905:
			case 13907:
			case 11824:
			case 1249:
			case 22610: // Vesta's spear (osrs)
				return 2080;

			// Zamorakian hasta
			case 11889:
				return 428;
			case 4718:

				player.specialAttackWeaponUsed[30] = 1;
				player.setWeaponAmountUsed(30);
				SoundSystem.sendSound(player, victim, 1057, 0);
				// Dharok's greataxe
				if (player.aggressiveType.contains("OTHER")) {
					return 2066;
				}
				return 2067;
			case 4710:
				// Ahrim's staff
				return 406;

			// Staff of the dead.
			case 11791:
			case 22296: // Staff of light
			case 12904:
			case 16209:
			case 16272: // Toxic staff of the dead
			case 15486:
			case 21_777:
				return 440;
			// Boxing gloves
			case 7671:
			case 7673:
				return 3678;
			case 12727:
				// Event rpg
				return 2323;
			case 4755:
				// Verac's flail
				return 2062;
			case 13576:
			case 22622: // Statius's warhammer
				// Dragon warhammer
				return 401;
			case 10887: // Barrelchest anchor
				return 5865;

			case 4151: // Abyssal whip
			case 12773: // Volcanic abyssal whip
			case 12774: // Frozen abyssal whip
			case 21371:
			case 15_441:
			case 15_442:
			case 15_443:
			case 15_444:
				SoundSystem.sendSound(player, victim, 1080, 300);
				return 1658;
			case 6528: // Obby maul
			case 18354:
			case 20756: // Hill giant club
				return 2661;
			default:
				SoundSystem.sendSound(player, victim, 417, 300);
				return 451;
		}
	}

	private static int getWeaponAnimationPreEoc(Player player, Player victim, String weaponName) {
		if (!GameType.isPreEoc()) {
			return -1;
		}
		switch (player.getWieldedWeapon()) {
			// Chaotic maul
			case 18353:
				return 13055;
		}
		return -1;
	}

}
