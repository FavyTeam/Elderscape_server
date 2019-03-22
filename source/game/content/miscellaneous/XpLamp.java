package game.content.miscellaneous;

import core.GameType;
import core.ServerConstants;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

import java.util.ArrayList;

/**
 * Xp lamp.
 *
 * @author Mgt Madness, created on 26-08-2016.
 */
public class XpLamp {

	public enum XpLampData {
		XP_LAMP_2_MILLION(16251, 2000000),
		XP_LAMP_1_MILLION(13146, 1000000),
		XP_LAMP_350k(16252, 350000);

		private XpLampData(int lampItemId, int experienceReward) {
			this.lampItemId = lampItemId;
			this.experienceReward = experienceReward;
		}

		private int lampItemId;

		public int getLampItemId() {
			return lampItemId;
		}

		private int experienceReward;

		private int getExperienceReward() {
			return experienceReward;
		}

	}


	private final static int[] xpLampButtons =
			{10252, 10253, 10254, 10255, 11000, 11001, 11002, 11003, 11004, 11005, 11006, 11007, 47002, 54090, 11008, 11009, 11010, 11011, 11012, 11013, 11014};

	final static int[] skillOrder =
			{0, 2, 4, 6, 1, 3, 5, 16, 15, 17, 12, 20, 18, 19, 14, 13, 10, 7, 11, 8, 9};


	public static boolean xpLampButton(Player player, int button) {
		for (int index = 0; index < xpLampButtons.length; index++) {
			if (xpLampButtons[index] == button) {
				if (player.isInRandomEventType("SELECT_SKILL")) {
					RandomEvent.randomEventAnswer(player, button, index);
					return true;
				} else {
					if (xpLampReward(player, button, index)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static ArrayList<String> skillingBans = new ArrayList<String>();

	private static boolean xpLampReward(Player player, int button, int index) {
		XpLampData xpLampInstance = null;
		for (XpLampData data : XpLampData.values()) {
			if (player.getFirstItemClicked() == data.getLampItemId()) {
				xpLampInstance = data;
				break;
			}
		}
		if (xpLampInstance == null) {
			return true;
		}
		if (player.getDuelStatus() >= 1) {
			return true;
		}
		if (!ItemAssistant.hasItemInInventory(player, xpLampInstance.getLampItemId())) {
			return true;
		}
		if (System.currentTimeMillis() - player.xpLampUsedTime <= 43200000) {
			int minutes = (int) ((43200000 - (System.currentTimeMillis() - player.xpLampUsedTime)) / 60000);
			String string = "minutes";
			if (minutes > 59) {
				minutes /= 60;
				string = "hours";
				if (minutes <= 1) {
					string = "hour";
				}
			}
			player.getPA().sendMessage("You may use the Xp lamp in " + minutes + " " + string + ".");
			return true;
		}
		if (GameType.isOsrsPvp()) {
			if (skillOrder[index] <= ServerConstants.MAGIC) {
				player.getPA().sendMessage("You cannot use Xp lamps on combat skills.");
				return true;
			}
		}

		if (player.xpLock) {
			player.getPA().sendMessage("You have Xp lock turned on.");
			return true;
		}
		int xpGiven = xpLampInstance.getExperienceReward();
		player.getPA().closeInterfaces(true);
		ItemAssistant.deleteItemFromInventory(player, xpLampInstance.getLampItemId(), 1);
		player.xpLampUsedTime = System.currentTimeMillis();
		if (player.isSupremeDonator()) {
			xpGiven *= 8;
		} else if (player.isImmortalDonator()) {
			xpGiven *= 7;
		} else if (player.isUberDonator()) {
			xpGiven *= 6;
		} else if (player.isUltimateDonator()) {
			xpGiven *= 5;
		} else if (player.isLegendaryDonator()) {
			xpGiven *= 4;
		} else if (player.isExtremeDonator()) {
			xpGiven *= 3;
		} else if (player.isSuperDonator()) {
			xpGiven *= 2;
		}
		Skilling.addSkillExperience(player, xpGiven, skillOrder[index], true);
		player.getPA().sendMessage("You rub the lamp and receive " + Misc.formatRunescapeStyle(xpGiven) + " experience in " + ServerConstants.SKILL_NAME[skillOrder[index]] + ".");
		player.getPA().sendMessage("You may rub another lamp in 12 hours time.");
		return true;

	}

	/**
	 * @return True if the itemId given is an Experience lamp.
	 */
	public static boolean isExperienceLamp(Player player, int itemId) {
		for (XpLampData data : XpLampData.values()) {
			if (itemId == data.getLampItemId()) {
				player.getPA().sendFrame126("Choose the skill you would like to level up", 2810);
				player.getPA().displayInterface(2808);
				return true;
			}
		}
		return false;
	}

}
