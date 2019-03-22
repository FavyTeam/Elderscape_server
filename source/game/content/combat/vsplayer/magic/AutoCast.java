package game.content.combat.vsplayer.magic;

import core.GameType;
import game.content.combat.CombatConstants;
import game.content.combat.CombatInterface;
import game.player.Player;
import game.player.movement.Movement;

public class AutoCast {

	/**
	 * The polypore autocast id
	 */
	public static final int POLYPORE_AUTOCAST = 58;

	public static boolean isOldAutoCastButton(Player player, int buttonId) {
		for (int j = 0; j < oldAutoCastId.length; j += 2) {
			if (oldAutoCastId[j] == buttonId) {
				AutoCast.assignOldAutocast(player, buttonId);
				return true;
			}
		}
		switch (buttonId) {
			case 1093:
			case 1094:
			case 1097:
				AutoCast.openOldAutoCastInterface(player);
				return true;

			case 24017: // Close button on the old autocast interface. Ancient magicks.
			case 7212: // Close button on the old autocast interface. Modern spellbook.
				AutoCast.resetAutocast(player, true);
				CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
				return true;
		}
		return false;
	}

	public static int[] oldAutoCastId =
			{
					51133,
					32,
					51185,
					33,
					51091,
					34,
					24018,
					35,
					51159,
					36,
					51211,
					37,
					51111,
					38,
					51069,
					39,
					51146,
					40,
					51198,
					41,
					51102,
					42,
					51058,
					43,
					51172,
					44,
					51224,
					45,
					51122,
					46,
					51080,
					47,
					7038,
					0,
					7039,
					1,
					7040,
					2,
					7041,
					3,
					7042,
					4,
					7043,
					5,
					7044,
					6,
					7045,
					7,
					7046,
					8,
					7047,
					9,
					7048,
					10,
					7049,
					11,
					7050,
					12,
					7051,
					13,
					7052,
					14,
					7053,
					15,
					47019,
					27,
					47020,
					25,
					47021,
					12,
					47022,
					13,
					47023,
					14,
					47024,
					15
			};

	public static void assignOldAutocast(Player player, int button) {
		if (player.spellBook.equals("LUNAR")) {
			AutoCast.resetAutocast(player, true);
			return;
		}
		if (button >= 7038 && button <= 7053 && !player.spellBook.equals("MODERN")) {
			return;
		}
		if (((button >= 51058 && button <= 51224) || button == 24018) && !player.spellBook.equals("ANCIENT")) {
			return;
		}
		for (int j = 0; j < oldAutoCastId.length; j++) {
			if (oldAutoCastId[j] == button) {
				player.usingOldAutocast = true;
				player.setAutoCasting(true);
				player.setAutocastId(oldAutoCastId[j + 1]);
				player.getPA().sendFrame36(108, 1, false);
				player.playerAssistant.setSidebarInterface(0, 328);
				player.playerAssistant.sendMessage(":autocasthighlight" + CombatConstants.MAGIC_SPELLS[player.getAutocastId()][0] + ":");
				break;
			}
		}
	}

	public static int[] spellIds =
			{
					4128,
					4130,
					4132,
					4134,
					4136,
					4139,
					4142,
					4145,
					4148,
					4151,
					4153,
					4157,
					4159,
					4161,
					4164,
					4165,
					4129,
					4133,
					4137,
					6006,
					6007,
					6026,
					6036,
					6046,
					6056,
					4147,
					6003,
					47005,
					4166,
					4167,
					4168,
					48157,
					50193,
					50187,
					50101,
					50061,
					50163,
					50211,
					50119,
					50081,
					50151,
					50199,
					50111,
					50071,
					50175,
					50223,
					50129,
					50091,
					
			/*
			// Pre-eoc
			127_088,
			127_108,
			127_128,
			127_148,
			161023,
			161026,
			161029,
			161032
			*/
			};

	public static boolean assignNewAutocast(Player player, int buttonId) {
		for (int i = 0; i < spellIds.length; i++) {
			if (buttonId == spellIds[i]) {

				if (player.spellBook.equals("LUNAR")) {
					AutoCast.resetAutocast(player, true);
					return true;
				}
				if (buttonId >= 4128 && buttonId <= 4168 && !player.spellBook.equals("MODERN")) {
					return true;
				}
				if (buttonId >= 50061 && buttonId <= 50223 && !player.spellBook.equals("ANCIENT")) {
					return true;
				}
				if (GameType.isPreEoc()) {
					if (i >= 48 && i <= 51) {
						i += 6;
					} else if (i >= 52 && i <= 56) {
						i += 7;
					}
				}
				player.setAutoCasting((player.getAutocastId() != i) ? true : false);
				if (player.getAutoCasting()) {
					player.getPA().sendFrame36(43, -1, false);
					player.setAutocastId(i);
					player.usingOldAutocast = true;
					resetCombatNotFollow(player);
					player.playerAssistant.sendMessage(":autocasthighlight" + CombatConstants.MAGIC_SPELLS[player.getAutocastId()][0] + ":");
					player.getPA().sendFrame36(108, 1, false); // Autocast on combat interface.
				} else {
					AutoCast.resetAutocast(player, true);
				}
				return true;
			}
		}
		return false;
	}

	private static void resetCombatNotFollow(Player player) {
		player.setUsingRanged(false);
		player.setMeleeFollow(false);
		player.resetNpcIdentityAttacking();
		player.resetFaceUpdate();
		player.resetPlayerIdAttacking();
		player.setSpellId(-1);
	}

	public static void resetAutocast(Player player, boolean updateSpecialBar) {
		player.setAutocastId(-1);
		player.usingOldAutocast = false;
		player.setAutoCasting(false);
		player.getPA().sendFrame36(108, 0, false); // Autocast on combat interface.
		if (updateSpecialBar) {
			player.playerAssistant.sendMessage(":resetautocast:");
			CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
		}
		resetCombatNotFollow(player);
	}

	public static void openOldAutoCastInterface(Player player) {
		if (player.getAutocastId() > -1) {
			AutoCast.resetAutocast(player, true);
			Movement.stopMovement(player);
		} else {
			player.getPA().sendFrame36(43, -1, false);
			resetCombatNotFollow(player);
			if (player.spellBook.equals("ANCIENT")) {
				player.playerAssistant.setSidebarInterface(0, 1689);
			} else if (player.spellBook.equals("MODERN")) {
				player.playerAssistant.setSidebarInterface(0, 1829);
			}
			player.getPA().sendMessage(":packet:otherbutton 350");
		}

	}

}
