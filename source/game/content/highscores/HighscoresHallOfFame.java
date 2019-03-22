package game.content.highscores;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.profile.Profile;
import game.content.starter.GameMode;
import game.player.Player;
import utility.FileUtility;
import utility.Misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Hall of Fame.
 *
 * @author MGT Madness, created on 07-09-2016.
 */
public class HighscoresHallOfFame {

	public static void displayInterface(Player player) {
		//19872 is title then the 3 after it is the 1st to 3rd place and repeat.
		int interfaceId = 19872;
		player.currentHallOfFame.clear();
		for (int index = 0; index < hallOfFameList.size(); index++) {
			int amountSupposedToBe = interfaceId + 8;
			String[] parse = hallOfFameList.get(index).split("=");
			for (int i = 0; i < parse.length; i++) {
				String name = parse[i];
				if (name.contains("@yel@")) {
					name = name.replace("@yel@", "");
					player.getPA().changeTextColour(interfaceId, ServerConstants.YELLOW_HEX);
				} else if (name.contains("@gr4@")) {
					name = name.replace("@gr4@", "");
					player.getPA().changeTextColour(interfaceId, 0xb0b0b0);
				}
				player.currentHallOfFame.add(name + "=" + (interfaceId + 57288));
				player.getPA().sendFrame126(name, interfaceId);
				interfaceId++;
			}
			if (interfaceId != amountSupposedToBe) {
				interfaceId = amountSupposedToBe;
			}
		}
		double amount = hallOfFameList.size() * 21.1;
		InterfaceAssistant.setFixedScrollMax(player, 19867, (int) amount);

		player.getPA().displayInterface(19850);
	}

	public static boolean isButton(Player player, int buttonId) {
		switch (buttonId) {
			case 77141:
				player.highscoresTabClicked = -1;
				HighscoresInterface.displayHighscoresInterface(player);
				return true;
		}

		for (int index = 0; index < player.currentHallOfFame.size(); index++) {
			String[] parse = player.currentHallOfFame.get(index).split("=");
			if (parse[1].equals(Integer.toString(buttonId))) {
				String name = parse[0];

				name = name.replace("@yel@", "");
				name = name.replace("@gr4@", "");
				player.setProfileNameSearched(name);
				player.lastProfileTabText = "INFO";
				Profile.viewCorrectTab(player, player.lastProfileTabText, true);
				return true;
			}
		}

		return false;
	}


	public static ArrayList<String> hallOfFameList = new ArrayList<String>();

	public static void loadHallOfFameData() {
		//hallOfFameList
		String location = "backup/logs/highscores/hall of fame.txt";
		try {
			BufferedReader file = new BufferedReader(new FileReader(location));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					hallOfFameList.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void enterToHallOfFame(Player player, String goalName) {
		if (player.isAdministratorRank()) {
			return;
		}
		String gameMode = player.getGameMode();
		String playerName = player.getPlayerName();
		String colour = "";
		if (gameMode.equals("IRON MAN")) {
			colour = "@gr4@";
		} else if (gameMode.equals("GLADIATOR")) {
			colour = "@yel@";
		}
		int saveIndex = -1;
		// Remove the matching index of arraylist and add a new one with the newer data.
		for (int index = 0; index < hallOfFameList.size(); index++) {
			if (!hallOfFameList.get(index).contains("=") && hallOfFameList.get(index).contains(goalName)) {
				saveIndex = index;
				break;
			}
			String[] parse = hallOfFameList.get(index).split("=");
			if (!parse[0].equals(goalName)) {
				continue;
			}
			if (parse.length == 4) {
				return;
			}
			for (int i = 0; i < parse.length; i++) {
				if (i == 0) {
					continue;
				}
				if (parse[i].toLowerCase().contains(playerName.toLowerCase())) {
					return;
				}
			}
			String old = hallOfFameList.get(index);
			hallOfFameList.remove(index);
			String number = parse.length == 3 ? "3rd" : "2nd";
			announceHallOfFame(player, playerName, goalName, number);
			hallOfFameList.add(index, old + "=" + colour + Misc.capitalize(playerName));
			return;
		}
		announceHallOfFame(player, playerName, goalName, "1st");
		hallOfFameList.remove(saveIndex);
		hallOfFameList.add(saveIndex, goalName + "=" + colour + Misc.capitalize(playerName));
	}

	private static void announceHallOfFame(Player player, String playerName, String goalName, String number) {
		String to = "to";
		switch (goalName) {
			case "All Achievements":
			case "All Titles":
				to = "to finish";
				break;
			case "Divine Sp. Shield Drop":
			case "Dragon Claws Drop":
				to = "to receive";
				break;
		}
		player.getPA().announce(GameMode.getGameModeName(player) + " is the " + number + " player " + to + " " + goalName + "!");
		player.getPA().sendScreenshot(number + " to " + goalName, 2);
	}

	public static void save() {
		FileUtility.deleteAllLines("backup/logs/highscores/hall of fame.txt");
		FileUtility.saveArrayContents("backup/logs/highscores/hall of fame.txt", hallOfFameList);

	}

}
