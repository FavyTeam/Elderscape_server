package game.player.punishment;

import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Store a list of banned ips and uid and source name. So when they rwt, it says their source.
 *
 * @author MGT Madness, created on 17-11-2017
 */
public class BannedData {
	public static List<BannedData> bannedDataDb = new ArrayList<BannedData>();

	private String sourceName = "";

	private String ip = "";

	private String uid = "";

	public BannedData(String sourceName, String ip, String uid) {
		this.sourceName = sourceName;
		this.ip = ip;
		this.uid = uid;
	}

	public static String getSourceName(Player player) {
		String name = player.getPlayerName() + "(" + ItemAssistant.getAccountBankValue(player) + ")";
		for (int index = 0; index < bannedDataDb.size(); index++) {
			BannedData instance = bannedDataDb.get(index);
			if (!player.addressIp.isEmpty()) {
				if (instance.ip.equals(player.addressIp) || Misc.uidMatches(player.addressUid, instance.uid)) {
					return name + " source: " + instance.sourceName;
				}
			}

		}
		return name;
	}

	public static String getSourceName(Player player, long forceWealth) {
		String name = player.getPlayerName() + "(" + Misc.formatRunescapeStyle(forceWealth) + ")";
		for (int index = 0; index < bannedDataDb.size(); index++) {
			BannedData instance = bannedDataDb.get(index);
			if (!player.addressIp.isEmpty()) {
				if (instance.ip.equals(player.addressIp) || Misc.uidMatches(player.addressUid, instance.uid)) {
					return name + " source: " + instance.sourceName;
				}
			}

		}
		return name;
	}

	public static String getSourceName(String playerName, String ip, String uid) {
		String name = playerName;
		for (int index = 0; index < bannedDataDb.size(); index++) {
			BannedData instance = bannedDataDb.get(index);
			if (!ip.isEmpty()) {
				if (instance.ip.equals(ip) || Misc.uidMatches(uid, instance.uid)) {
					return name + " source: " + instance.sourceName;
				}
			}

		}
		return name;
	}

	public static void readBannedData() {
		bannedDataDb.clear();
		for (int index = 0; index < Ban.bannedList.size(); index++) {
			String name = Ban.bannedList.get(index);
			String ip = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
			String uid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
			bannedDataDb.add(new BannedData(name, ip, uid));
		}
		for (int index = 0; index < Blacklist.blacklistedAccounts.size(); index++) {
			String name = Blacklist.blacklistedAccounts.get(index);
			String ip = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
			String uid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
			bannedDataDb.add(new BannedData(name, ip, uid));
		}
	}
}
