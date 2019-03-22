package game.content.profile;

import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.highscores.HighscoresHallOfFame;
import game.content.interfaces.InterfaceAssistant;
import game.player.Player;
import utility.Misc;

import java.io.*;
import java.util.ArrayList;

/**
 * Rare drop log system.
 *
 * @author MGT Madness, created on 05-01-2015.
 */
public class RareDropLog {

	/**
	 * Apply the rare drop to the player's file log.
	 *
	 * @param player
	 * @param drop The string to save in the log.
	 */
	public static void appendRareDrop(Player player, String drop) {
		String playerName = player.getPlayerName();
		Achievements.checkCompletionMultiple(player, "1049 1057 1120");
		if (drop.contains("Dragon claws")) {
			HighscoresHallOfFame.enterToHallOfFame(player, "Dragon Claws Drop");
		}
		Achievements.addToAchievementProgress(player, "rareDrops");
		PlayerTitle.checkCompletionMultiple(player, "60 61 62 63 64 65", "");
		// Not using file utility, because it puts everything to lowercase.
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("backup/logs/rare drop log/" + playerName + ".txt", true));
			bw.write(drop);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static int amountOfLines = 0;

	/**
	 * View the rare drop.
	 *
	 * @param player The associate player.
	 */
	public static void viewRareDropLog(Player player, String playerNameToSearch) {
		amountOfLines = 0;
		ArrayList<String> rareDropList = new ArrayList<String>();
		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/rare drop log/" + playerNameToSearch + ".txt"));
			String line;
			while ((line = file.readLine()) != null) {
				rareDropList.add(line);
				amountOfLines++;
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
			Misc.print("Problem reading file, rare drop log.");
		}
		int index = 0;
		for (int i = rareDropList.size() - 1; i > -1; i--) {
			player.getPA().sendFrame126(rareDropList.get(i), 25855 + index);
			index++;
		}
		double amount = (index) * 15.2;
		InterfaceAssistant.setFixedScrollMax(player, 25742, (int) amount);
		InterfaceAssistant.clearFrames(player, 25855 + index, 25954);
	}

}
