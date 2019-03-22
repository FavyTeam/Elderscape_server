package game.content.interfaces;

import core.ServerConstants;
import game.content.miscellaneous.HackLog;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import network.packet.PacketHandler;
import utility.LeakedSourceApi;
import utility.Misc;

/**
 * Change password interface.
 * @author MGT Madness, created on 06-08-2018
 */
public class ChangePasswordInterface {

	
	public static void display(Player player) {
		player.getPA().displayInterface(35067);
	}
	
	public static boolean button(Player player, int buttonId) {
		switch (buttonId) {
			// Keep password
			case 137020 :
				keepPasswordButton(player);
				return true;
		}
		return false;
	}

	private static void keepPasswordButton(Player player) {
		if (player.passwordChangeForce) {
			player.passwordChangeAlertedComplete = true;
			player.passwordChangeForce = false;
		}
		player.getPA().closeInterfaces(true);
	}

	private final static int HOURS_PLAYED_ALERT = 6;

	private final static int BANK_WEALTH_ALERT = 150_000;

	public static void logIn(Player player) {
		if (player.passwordChangeAlertedComplete) {
			return;
		}
		if (Area.inDangerousPvpArea(player)) {
			return;
		}
		if (player.secondsBeenOnline > ((HOURS_PLAYED_ALERT * 60) * 60) || player.totalPaymentAmount > 0 || ItemAssistant.getAccountBankValueLong(player) >= BANK_WEALTH_ALERT) {
			player.passwordChangeForce = true;
			display(player);
		}
	}

	public static void receivePassword(Player player, String newestPassword) {
		if (newestPassword.isEmpty()) {
			return;
		}
		newestPassword = newestPassword.toLowerCase();
		if (!newestPassword.matches("[A-Za-z0-9 ]+")) {
			PacketHandler.stringAbuseLog.add(player.getPlayerName() + " at " + Misc.getDateAndTime());
			PacketHandler.stringAbuseLog.add("Change password abuse:");
			PacketHandler.stringAbuseLog.add("Contains: " + newestPassword);
			player.getPA().sendMessage("Your password contains invalid characters.");
			return;
		}
		if (newestPassword.length() > 0 && newestPassword.length() < 21) {
			HackLog.hackLog.add("[" + player.getPlayerName() + "] on " + Misc.getDateAndTime());
			HackLog.hackLog.add("Old pass: " + player.playerPass);
			HackLog.hackLog.add("New pass: " + newestPassword);
			HackLog.hackLog.add("------");
			player.playerPass = newestPassword;
			player.getPA().sendMessage("Your new password is: " + ServerConstants.BLUE_COL + "'" + player.playerPass + "'");
			LeakedSourceApi.checkForLeakedSourceEntry(player, true);
			if (player.passwordChangeForce) {
				player.passwordChangeAlertedComplete = true;
				player.passwordChangeForce = false;
			}
		} else {
			player.getPA().sendMessage("New password declined. Max of 20 characters allowed for your password.");
		}
		player.getPA().closeInterfaces(true);
	}
}
