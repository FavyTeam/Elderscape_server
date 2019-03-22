package game.content.prayer.book.regular;

import core.GameType;
import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.player.Player;

/**
 * @author relex lawl / relex
 * <p>
 * I do not give permission for this to be released on any website
 * <p>
 * Old class I decided to upgrade
 * 
 * TODO: Convert and remove
 */

public class QuickPrayers {

	public static final int MAX_PRAYERS = 29;

	private static final int[] defPrayId =
			{67050, 67055, 67063, 67075, 89182};

	private static final int[] strPrayId =
			{67051, 67056, 67064, 67075, 89182};

	private static final int[] atkPrayId =
			{67052, 67057, 67065, 67075, 89182};

	private static final int[] rangePrayId =
			{67053, 67061, 67069, 89183};

	private static final int[] magePrayId =
			{67054, 67062, 67070, 89184};

	private static final int[] headIconsId =
			{67066, 67067, 67068, 67071, 67072, 67073};

	private static final int[] defPray =
			{0, 5, 13, ServerConstants.CHIVALRY, 25};

	private static final int[] strPray =
			{1, 6, 14, ServerConstants.CHIVALRY, 25};

	private static final int[] atkPray =
			{2, 7, 15, ServerConstants.CHIVALRY, 25};

	private static final int[] rangePray =
			{3, 11, 19, ServerConstants.RIGOUR};

	private static final int[] magePray =
			{4, 12, 20, ServerConstants.AUGURY};

	private static final int[] headIcons =
			{16, 17, 18, 21, 22, 23};

	private static final int[][] data =
			{
					{67050, 650, 0},
					{67051, 651, 1},
					{67052, 652, 2},
					{67053, 653, 3},
					{67054, 654, 4},
					{67055, 655, 5},
					{67056, 656, 6},
					{67057, 657, 7},
					{67058, 658, 8},
					{67059, 659, 9},
					{67060, 660, 10},
					{67061, 661, 11},
					{67062, 662, 12},
					{67063, 663, 13},
					{67064, 664, 14},
					{67065, 665, 15},
					{67066, 666, 16},
					{67067, 667, 17},
					{67068, 668, 18},
					{67069, 669, 19},
					{67070, 670, 20},
					{67071, 671, 21},
					{67072, 672, 22},
					{67073, 673, 23}, // Smite
					{67074, 674, ServerConstants.PRESERVE}, // Preserve
					{67075, 675, ServerConstants.CHIVALRY}, // Chivalry
					{89182, 677, ServerConstants.PIETY}, // Piety
					{89183, 678, ServerConstants.RIGOUR}, // Rigour
					{89184, 679, ServerConstants.AUGURY}
			}; // Augury

	public static void canBeSelected(Player c, int actionId) {
		boolean[] prayer = new boolean[MAX_PRAYERS];
		for (int j = 0; j < prayer.length; j++) {
			prayer[j] = true;
		}
		switch (actionId) {
			case 67050:
			case 67055:
			case 67063:
				for (int j = 0; j < defPrayId.length; j++) {
					if (defPrayId[j] != actionId) {
						prayer[defPray[j]] = false;
					}
				}
				break;

			case 67051:
			case 67056:
			case 67064:
				for (int j = 0; j < strPray.length; j++) {
					if (strPrayId[j] != actionId) {
						prayer[strPray[j]] = false;
					}
				}
				for (int j = 0; j < rangePray.length; j++) {
					if (rangePrayId[j] != actionId) {
						prayer[rangePray[j]] = false;
					}
				}
				for (int j = 0; j < magePray.length; j++) {
					if (magePrayId[j] != actionId) {
						prayer[magePray[j]] = false;
					}
				}
				break;

			case 67052:
			case 67057:
			case 67065:
				for (int j = 0; j < atkPray.length; j++) {
					if (atkPrayId[j] != actionId) {
						prayer[atkPray[j]] = false;
					}
				}
				for (int j = 0; j < rangePray.length; j++) {
					if (rangePrayId[j] != actionId) {
						prayer[rangePray[j]] = false;
					}
				}
				for (int j = 0; j < magePray.length; j++) {
					if (magePrayId[j] != actionId) {
						prayer[magePray[j]] = false;
					}
				}
				break;

			case 67053:
			case 67061:
			case 67069:
			case 89183:
				if (actionId == 89183) {
					for (int j = 0; j < defPray.length; j++) {
						if (defPrayId[j] != actionId) {
							prayer[defPray[j]] = false;
						}
					}
				}
				for (int j = 0; j < atkPray.length; j++) {
					if (atkPrayId[j] != actionId) {
						prayer[atkPray[j]] = false;
					}
				}
				for (int j = 0; j < strPray.length; j++) {
					if (strPrayId[j] != actionId) {
						prayer[strPray[j]] = false;
					}
				}
				for (int j = 0; j < rangePray.length; j++) {
					if (rangePrayId[j] != actionId) {
						prayer[rangePray[j]] = false;
					}
				}
				for (int j = 0; j < magePray.length; j++) {
					if (magePrayId[j] != actionId) {
						prayer[magePray[j]] = false;
					}
				}
				break;

			case 67054:
			case 67062:
			case 67070:
			case 89184:
				if (actionId == 89184) {
					for (int j = 0; j < defPray.length; j++) {
						if (defPrayId[j] != actionId) {
							prayer[defPray[j]] = false;
						}
					}
				}
				for (int j = 0; j < atkPray.length; j++) {
					if (atkPrayId[j] != actionId) {
						prayer[atkPray[j]] = false;
					}
				}
				for (int j = 0; j < strPray.length; j++) {
					if (strPrayId[j] != actionId) {
						prayer[strPray[j]] = false;
					}
				}
				for (int j = 0; j < rangePray.length; j++) {
					if (rangePrayId[j] != actionId) {
						prayer[rangePray[j]] = false;
					}
				}
				for (int j = 0; j < magePray.length; j++) {
					if (magePrayId[j] != actionId) {
						prayer[magePray[j]] = false;
					}
				}
				break;

			case 67066:
			case 67067:
			case 67068:
			case 67071:
			case 67072:
			case 67073:
				for (int j = 0; j < headIcons.length; j++) {
					if (headIconsId[j] != actionId) {
						prayer[headIcons[j]] = false;
					}
				}
				break;

			case 67075:
				for (int j = 0; j < atkPray.length; j++) {
					if (atkPrayId[j] != actionId) {
						prayer[atkPray[j]] = false;
					}
				}
				for (int j = 0; j < strPray.length; j++) {
					if (strPrayId[j] != actionId) {
						prayer[strPray[j]] = false;
					}
				}
				for (int j = 0; j < rangePray.length; j++) {
					if (rangePrayId[j] != actionId) {
						prayer[rangePray[j]] = false;
					}
				}
				for (int j = 0; j < magePray.length; j++) {
					if (magePrayId[j] != actionId) {
						prayer[magePray[j]] = false;
					}
				}
				for (int j = 0; j < defPray.length; j++) {
					if (defPrayId[j] != actionId) {
						prayer[defPray[j]] = false;
					}
				}
				break;

			case 89182:
				for (int j = 0; j < atkPray.length; j++) {
					if (atkPrayId[j] != actionId) {
						prayer[atkPray[j]] = false;
					}
				}
				for (int j = 0; j < strPray.length; j++) {
					if (strPrayId[j] != actionId) {
						prayer[strPray[j]] = false;
					}
				}
				for (int j = 0; j < rangePray.length; j++) {
					if (rangePrayId[j] != actionId) {
						prayer[rangePray[j]] = false;
					}
				}
				for (int j = 0; j < magePray.length; j++) {
					if (magePrayId[j] != actionId) {
						prayer[magePray[j]] = false;
					}
				}
				for (int j = 0; j < defPray.length; j++) {
					if (defPrayId[j] != actionId) {
						prayer[defPray[j]] = false;
					}
				}
				break;
		}
		for (int i = 0; i < MAX_PRAYERS; i++) {
			if (!prayer[i]) {
				c.quickPrayers[i] = false;
				for (int j = 0; j < data.length; j++) {
					if (i == data[j][2]) {
						c.getPA().sendFrame36(data[j][1], 0, false);
					}
				}
			}
		}
	}

	public static boolean clickPray(Player c, int actionId) {
		for (int j = 0; j < data.length; j++) {
			if (data[j][0] == actionId) {
				canBeSelected(c, actionId);
				if (c.quickPrayers[data[j][2]]) {
					c.quickPrayers[data[j][2]] = false;
					c.getPA().sendFrame36(data[j][1], 0, false);
				} else {
					c.quickPrayers[data[j][2]] = true;
					c.getPA().sendFrame36(data[j][1], 1, false);
				}
				return true;
			}
		}
		return false;
	}

	public static void loadCheckMarks(Player player) {
		for (int j = 0; j < data.length; j++) {
			player.getPA().sendFrame36(data[j][1], player.quickPrayers[data[j][2]] ? 1 : 0, false);
		}
	}

	public static void clickConfirm(Player player) {
		player.playerAssistant.setSidebarInterface(5, GameType.isPreEoc() ? 35800 : 5608);
	}

	public static void selectQuickInterface(Player player) {
		QuickPrayers.loadCheckMarks(player);
		player.playerAssistant.setSidebarInterface(5, 22923);
		player.getPA().sendFrame106(5);
	}

	public static void turnOffQuicks(Player player) {
		player.quickPray = false;
		for (int i = 0; i < player.quickPrayers.length; i++) {
			if (player.quickPrayers[i] && player.prayerActive[i]) {
				RegularPrayer.activatePrayer(player, i);
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
				InterfaceAssistant.quickPrayersOff(player);
			}
		}
	}

	public static void turnOnQuicks(Player player) {
		for (int i = 0; i < player.quickPrayers.length; i++) {
			if (player.quickPrayers[i]) {
				if (!player.prayerActive[i]) {
					RegularPrayer.activatePrayer(player, i);
				}
				if (player.prayerActive[i]) {
					player.quickPray = true;
					player.setUpdateRequired(true);
					player.setAppearanceUpdateRequired(true);
				}
			}
		}
		if (player.quickPray) {
			InterfaceAssistant.quickPrayersOn(player);
		} else {
			player.getPA().sendMessage("You don't have any quick prayers set up.");
			return;
		}
	}
}
