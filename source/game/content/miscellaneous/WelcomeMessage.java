package game.content.miscellaneous;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.staff.StaffManagement;
import game.log.GameTickLog;
import game.player.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import utility.WebsiteLogInDetails;

/**
 * Welcome message.
 *
 * @author MGT Madness, created on 17-02-2016.
 */
public class WelcomeMessage {

	/**
	 * Store the welcome messages.
	 */
	public static ArrayList<String> welcomeMessageList = new ArrayList<String>();

	/**
	 * Load the welcome message from the text file.
	 */
	public static void loadWelcomeMessage() {
		welcomeMessageList.clear();
		try {
			BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getDedicatedServerConfigLocation() + "welcome message.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				welcomeMessageList.add(line);
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final static String[] SPAWN_VERSION_MESSAGE = {
			//@formatter:off
			"We are currently in spawn mode temporarily, the current character",
			"files in use is from 20-06-2018 2 PM.",
			"Once the stability issue has been fixed, your latest character", 
			"files will be restored, which means 0 progress will be lost!",
			"",
			"So for now, just play for fun and kill",
			"scorpions at Scorpia tp and Wild agility course for mad Blood money!",
			//@formatter:on
	};

	/**
	 * Send the welcome message.
	 *
	 * @param player The associated player.
	 */
	public static void sendWelcomeMessage(Player player) {
		if (WebsiteLogInDetails.SPAWN_VERSION) {
			int frameIndex = 0;
			player.getPA().sendMessage(ServerConstants.RED_COL + "We are currently in spawn mode temporarily.");
			player.getPA().sendFrame126("Notice!", 25003);
			for (int index = 0; index < SPAWN_VERSION_MESSAGE.length; index++) {
				player.getPA().sendFrame126(SPAWN_VERSION_MESSAGE[index], 25008 + frameIndex);
				frameIndex++;
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			return;
		}
		for (int index = 0; index < welcomeMessageList.size(); index++) {
			player.playerAssistant.sendMessage(welcomeMessageList.get(index));
		}
		if (StaffManagement.signUpThreadId >= 0 && StaffManagement.hoursPlayTimeRequired >= 0) {
			if (PlayerGameTime.getHoursOnline(player) >= StaffManagement.hoursPlayTimeRequired
			    && GameTimeSpent.getActivePlayTimeHoursInLastWeek(player) >= StaffManagement.hoursActivePlayedTimeInTheLastWeek) {
				player.getPA().sendMessage(
						ServerConstants.GREEN_COL_PLAIN + "You may sign up to become a Support and potentially a Moderator on ::thread " + StaffManagement.signUpThreadId);
			}
			if (player.isHeadModeratorRank()) {
				player.getPA().sendMessage(
						"<img=31>Head Mod: Sign ups is set to thread " + StaffManagement.signUpThreadId + " and hours required " + StaffManagement.hoursPlayTimeRequired + " and");
				player.getPA().sendMessage("last 7 days active hours to " + StaffManagement.hoursActivePlayedTimeInTheLastWeek + ".");
			}
		} else {
			if (player.isHeadModeratorRank()) {
				player.getPA().sendMessage("<img=31>Head Mod: Sign ups is currently off.");
			}
		}
	}

}
