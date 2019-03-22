package game.content.miscellaneous;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.entity.EntityType;
import game.player.Player;
import java.util.ArrayList;
import utility.FileUtility;
import utility.HackLogHistory;
import utility.Misc;

/**
 * Hack log.
 *
 * @author MGT Madness, created on 15-04-2017
 */
public class HackLog {
	/**
	 * Store data of new ip logging into an account.
	 */
	public static ArrayList<String> hackLog = new ArrayList<String>();


	public final static String FILE_LOCATION = "backup/logs/bruteforce/hacklog.txt";

	public static void saveHackLog() {
		FileUtility.saveArrayContents(FILE_LOCATION, hackLog);
		hackLog.clear();
	}

	public static void addNewHackEntry(Player player) {
		if (player.isCombatBot() || player.getType() == EntityType.PLAYER_PET) {
			return;
		}
		// Rarely the hacker's mac matches the victim. So if that's the case, still enter it as a hack.
		if (Misc.uidMatches(player.addressUid, player.lastUidAddress) || player.addressIp.equals(player.lastSavedIpAddress)) {
			return;
		}
		ItemTransferLog.logInPlayerChanged(player);
		player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Warning:");
		player.playerAssistant.sendMessage("Last ip address connected to this account is: " + player.lastSavedIpAddress);
		player.playerAssistant.sendMessage("Last logged in (hours): " + PlayerGameTime.calculateHoursFromLastVisit(player));
		player.playerAssistant.sendMessage("Your current ip address is: " + player.addressIp);

		int frameIndex = 0;
		player.getPA().sendFrame126("@red@Hacker warning!", 25003);
		//@formatter:off
		String[] text = {"Last ip address connected to this account is: " + player.lastSavedIpAddress,
				"Last logged in (hours): " + PlayerGameTime.calculateHoursFromLastVisit(player),
				"Your current ip address is: " + player.addressIp,
				"",
				"@dre@If you are a hacker, you will be ip banned and all your",
				"@dre@accounts will be wiped. Think wisely before commiting a crime.",
				"@dre@Account hijacking and wealth stealing is not allowed.",
				"@dre@You are not allowed to change pass unless you are original owner!",
				"@dre@Account sharing is allowed with the permission of account owner.",
				"@dre@You will be ipbanned for breaking rules.",
				"",
				"@blu@If you got hacked and items on your account are definitely missing",
				"@blu@Change your password by typing in ::changepassword newPassHere",
				"@blu@please report the hack to a staff member on ::discord",
				"@blu@So the hacker gets banned and possible item recovery."
		};
		//@formatter:on
		for (int index = 0; index < text.length; index++) {
			player.getPA().sendFrame126(text[index], 25008 + frameIndex);
			frameIndex++;
		}
		InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
		InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
		player.getPA().displayInterface(25000);
		hackLog.add("[" + player.getPlayerName() + "] on " + Misc.getDateAndTime());
		hackLog.add("New Ip: " + player.addressIp);
		hackLog.add("New Uid: " + player.addressUid);
		hackLog.add("Last logged in (hours): " + PlayerGameTime.calculateHoursFromLastVisit(player));
		hackLog.add("Old Ip: " + player.lastSavedIpAddress);
		hackLog.add("Old Uid: " + player.lastUidAddress);
		hackLog.add("------");
		HackLogHistory.hackLogList.add(new HackLogHistory(player.getPlayerName(), player.addressIp, player.addressUid, player.lastSavedIpAddress, player.lastUidAddress));
	}
}
