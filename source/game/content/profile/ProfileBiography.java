package game.content.profile;

import game.player.Player;
import utility.Misc;

/**
 * Profile biography system.
 *
 * @author MGT Madness, created on 16-01-2016.
 */
public class ProfileBiography {

	/**
	 * Receive the biography text from the client.
	 *
	 * @param player
	 * @param text
	 * @return
	 */
	public static boolean receiveBiographyFromClient(Player player, String text) {

		if (text.contains("biography")) {
			boolean declineText = false;
			if (text.length() < 11) {
				return true;
			}
			int value = Integer.parseInt(text.substring(9, 10));
			if (Misc.checkForOffensive(text) || declineText) {
				player.playerAssistant.sendMessage("You may not use offensive language.");
				switch (value) {
					case 1:
						player.biographyLine1 = "";
						player.getPA().sendFrame126("", 25359);
						break;
					case 2:
						player.biographyLine2 = "";
						player.getPA().sendFrame126("", 25360);
						break;
					case 3:
						player.biographyLine3 = "";
						player.getPA().sendFrame126("", 25361);
						break;
					case 4:
						player.biographyLine4 = "";
						player.getPA().sendFrame126("", 25362);
						break;
					case 5:
						player.biographyLine5 = "";
						player.getPA().sendFrame126("", 25373);
						break;
				}
				return true;
			}
			text = text.substring(11);
			switch (value) {
				case 1:
					player.biographyLine1 = text;
					break;
				case 2:
					player.biographyLine2 = text;
					break;
				case 3:
					player.biographyLine3 = text;
					break;
				case 4:
					player.biographyLine4 = text;
					break;
				case 5:
					player.biographyLine5 = text;
					break;
			}
			return true;
		}
		return false;
	}

	/**
	 * @param player
	 * @param buttonId
	 * @return True, if the button is a profile biography button.
	 */
	public static boolean isBiographyButton(Player player, int buttonId) {
		switch (buttonId) {
			case 99015:
				if (!player.getPlayerName().equals(player.getProfileNameSearched())) {
					player.playerAssistant.sendMessage("You may only edit your own profile.");
					return true;
				}
				player.playerAssistant.sendMessage(":profilebiography1" + player.biographyLine1);
				return true;

			case 99016:
				if (!player.getPlayerName().equals(player.getProfileNameSearched())) {
					player.playerAssistant.sendMessage("You may only edit your own profile.");
					return true;
				}
				player.playerAssistant.sendMessage(":profilebiography2" + player.biographyLine2);
				return true;

			case 99017:
				if (!player.getPlayerName().equals(player.getProfileNameSearched())) {
					player.playerAssistant.sendMessage("You may only edit your own profile.");
					return true;
				}
				player.playerAssistant.sendMessage(":profilebiography3" + player.biographyLine3);
				return true;

			case 99018:
				if (!player.getPlayerName().equals(player.getProfileNameSearched())) {
					player.playerAssistant.sendMessage("You may only edit your own profile.");
					return true;
				}
				player.playerAssistant.sendMessage(":profilebiography4" + player.biographyLine4);
				return true;

			case 99029:
				if (!player.getPlayerName().equals(player.getProfileNameSearched())) {
					player.playerAssistant.sendMessage("You may only edit your own profile.");
					return true;
				}
				player.playerAssistant.sendMessage(":profilebiography5" + player.biographyLine5);
				return true;
		}
		return false;
	}

}
