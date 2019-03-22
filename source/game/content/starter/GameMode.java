package game.content.starter;


import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.highscores.HighscoresHallOfFame;
import game.content.miscellaneous.PlayerRank;
import game.content.skilling.Skilling;
import game.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Game modes.
 *
 * @author MGT Madness, created on 05-07-2015.
 */
public class GameMode {

	public static class PkerStarters {
		public static List<PkerStarters> pkerStarters = new ArrayList<PkerStarters>();

		public String ip = "";

		public String uid = "";

		public PkerStarters(String ip, String uid) {
			this.ip = ip;
			this.uid = uid;
		}
	}

	public static double getDropRate(Player player, double chance) {
		double modifier = 1.0;
		double chanceNew = (double) chance;
		if (player.isSupremeDonator()) {
			modifier -= 0.60;
		} else if (player.isImmortalDonator()) {
			modifier -= 0.50;
		} else if (player.isUberDonator()) {
			modifier -= 0.40;
		} else if (player.isUltimateDonator()) {
			modifier -= 0.30;
		} else if (player.isLegendaryDonator()) {
			modifier -= 0.20;
		} else if (player.isExtremeDonator()) {
			modifier -= 0.10;
		} else if (player.isSuperDonator()) {
			modifier -= 0.05;
		}
		if (GameMode.getDifficulty(player, "GLADIATOR")) {
			modifier -= 0.05;
		}
		if (player.playerEquipment[ServerConstants.RING_SLOT] == 2572) {
			modifier -= 0.01;
		} else if (player.playerEquipment[ServerConstants.RING_SLOT] == 12785) {
			modifier -= 0.03;
		}
		chanceNew *= modifier;
		return chanceNew;
	}

	public static void sendDescription(Player player, String starterType) {
		int interfaceIdStart = 0;
		ArrayList<String> text = new ArrayList<String>();
		if (starterType.equals("NORMAL")) {
			text.add("x500 combat experience");
			text.add("x60 skilling experience rate.");
			text.add("Ability to set combat levels after");
			text.add("maxed combat.");
			interfaceIdStart = 23476;
		}
		if (starterType.equals("GLADIATOR")) {
			text.add("x80 combat experience");
			text.add("x10 skilling experience rate.");
			text.add("Ability to set combat levels after");
			text.add("maxed combat.");
			text.add("+5% rare droprate bonus");
			text.add("+100 bank slots.");
			text.add("Noted bone, hide & bar drops.");
			text.add("+20% more coins from voting.");
			text.add("+20% bonus blood money from pking.");
			text.add("+20% bonus slayer points.");
			interfaceIdStart = 23476;
		} else if (starterType.equals("NONE")) {
			text.add("No restrictions apply to you.");
			interfaceIdStart = 23512;
		} else if (starterType.equals("PKER")) {
			text.add("You will be levelled up to");
			text.add("70 att, 99 str, 99 ranged, 99 mage");
			text.add("& 52 prayer.");
			text.add("You will instantly receive");
			text.add("Mithril gloves & unholy book.");
			text.add("No entry to total level highscores.");
			interfaceIdStart = 23512;
		} else if (starterType.equals("STANDARD IRON MAN")) {
			text.add("Cannot receive any item that belongs");
			text.add("to another player.");
			interfaceIdStart = 23512;
		} else if (starterType.equals("ULTIMATE IRON MAN")) {
			text.add("Cannot receive any item that belongs");
			text.add("to another player.");
			text.add("Cannot use banks.");
			text.add("All items will be dropped on death in");
			text.add("dangerous deaths.");
			interfaceIdStart = 23512;
		} else if (starterType.equals("HARDCORE IRON MAN")) {
			text.add("Cannot receive any item that belongs");
			text.add("to another player.");
			text.add("You only have 1 life!");
			text.add("A dangerous death will downgrade your");
			text.add("game mode to Standard Ironman.");
			interfaceIdStart = 23512;
		}
		for (int index = 0; index < 12; index++) {
			String string = "";
			if (index <= text.size() - 1) {
				string = text.get(index);
			}
			player.getPA().sendFrame126(string, interfaceIdStart + index);
		}
	}

	/**
	 * Example: The 'Gladiator' MGT Madness
	 *
	 * @return The player's game mode.
	 */
	public static String getGameModeName(Player player) {
		return player.getCapitalizedName();
	}

	/**
	 * @return True, if the gameMode String given, matches the player's game mode.
	 */
	public static boolean getGameMode(Player player, String gameMode) {
		if (player.getGameMode().equals(gameMode)) {
			return true;
		}
		return false;
	}

	/**
	 * @return True if the player's game mode contains the given gameMode.
	 */
	public static boolean getGameModeContains(Player player, String gameMode) {
		if (player.getGameMode().contains(gameMode)) {
			return true;
		}
		return false;
	}

	/**
	 * @return True if the given difficulty matches the player's chosen difficulty.
	 */
	public static boolean getDifficulty(Player player, String difficulty) {
		if (player.getDifficultyChosen().equals(difficulty)) {
			return true;
		}
		return false;
	}

	/**
	 * Append action of the game mode interface button.
	 *
	 * @param player The associated player.
	 * @param buttonId The button identity used.
	 * @return True, if the button belongs to the game mode interface.
	 */
	public static boolean isGameModeButton(Player player, int buttonId) {
		switch (buttonId) {

			// Normal
			case 91151:
				if (player.isTutorialComplete()) {
					return false;
				}
				normalButtonAction(player);
				return true;

			// Gladiator
			case 91154:
				if (player.isTutorialComplete()) {
					return false;
				}
				if (player.selectedGameMode.equals("PKER")) {
					player.getPA().sendMessage("You cannot select Gladiator difficulty with Pker game mode.");
					return true;
				}
				player.selectedDifficulty = "GLADIATOR";
				sendDescription(player, "GLADIATOR");
				player.getPA().setInterfaceClicked(23439, 23450, true);
				return true;

			// None
			case 91157:
				if (player.isTutorialComplete()) {
					return false;
				}
				player.selectedGameMode = "NONE";
				sendDescription(player, "NONE");
				player.getPA().setInterfaceClicked(23440, 23453, true);
				return true;

			// Pker
			case 91160:
				if (player.isTutorialComplete()) {
					return false;
				}
				if (player.selectedDifficulty.equals("GLADIATOR")) {
					player.getPA().sendMessage("You cannot select Pker game mode with Gladiator difficulty.");
					return true;
				}
				player.selectedGameMode = "PKER";
				sendDescription(player, "PKER");
				player.getPA().setInterfaceClicked(23440, 23456, true);
				return true;

			// Standard Iron Man
			case 91163:
				if (player.isTutorialComplete()) {
					return false;
				}
				player.selectedGameMode = "STANDARD IRON MAN";
				sendDescription(player, "STANDARD IRON MAN");
				player.getPA().setInterfaceClicked(23440, 23459, true);
				return true;

			// Hardcore Iron Man
			case 91166:
				if (player.isTutorialComplete()) {
					return false;
				}
				player.selectedGameMode = "HARDCORE IRON MAN";
				sendDescription(player, "HARDCORE IRON MAN");
				player.getPA().setInterfaceClicked(23440, 23462, true);
				return true;

			// Ultimate Iron man
			case 92005:
				if (player.isTutorialComplete()) {
					return false;
				}
				player.selectedGameMode = "ULTIMATE IRON MAN";
				sendDescription(player, "ULTIMATE IRON MAN");
				player.getPA().setInterfaceClicked(23440, 23557, true);
				return true;

			// Confirm
			case 91175:
				if (player.isTutorialComplete()) {
					return false;
				}
				player.setTutorialComplete(true);
				NewPlayerContent.completelyEndTutorial(player);
				player.setGameMode(player.selectedGameMode);
				player.setDifficultyChosen(player.selectedDifficulty);
				PlayerRank.demoteAndGiveBackDonatorOrIronManRank(player, false);
				NewPlayerContent.giveEcoStarterKit(player);
				return true;
		}

		return false;
	}

	private static void normalButtonAction(Player player) {
		player.selectedDifficulty = "NORMAL";
		sendDescription(player, "NORMAL");
		player.getPA().setInterfaceClicked(23439, 23447, true);
	}

	/**
	 * Announce to everyone that the player has achieved maxed combat.
	 *
	 * @param player The associated player.
	 */
	public static void announceMaxedCombatOrMaxedTotal(Player player) {
		if (player.getCombatLevel() == 126 && player.baseSkillLevel[ServerConstants.PRAYER] == 99 && player.loggingInFinished && !player.getAbleToEditCombat()) {
			player.setAbleToEditCombat(true);
			player.getPA().announce(GameMode.getGameModeName(player) + " has finally achieved maxed combat.");
			player.getPA().sendScreenshot("maxed combat", 2);
		}

		if (Skilling.getOriginalTotalLevel(player) == 2080 && !player.announceMaxLevel) {
			HighscoresHallOfFame.enterToHallOfFame(player, "2079 Total"); // Changing it to 2080 might crash because it doesn't exist in hall of fame.
			PlayerTitle.checkCompletionSingle(player, 53);
			Achievements.checkCompletionSingle(player, 1152);
			player.playerAssistant.announce(GameMode.getGameModeName(player) + " has achieved maximum total level.");
			player.getPA().sendScreenshot("maxed total level", 2);
			player.announceMaxLevel = true;
		}

	}

}
