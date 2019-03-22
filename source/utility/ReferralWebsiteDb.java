package utility;

import java.sql.ResultSet;
import java.util.ArrayList;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import tools.discord.content.DiscordCommands;
import tools.multitool.ReferralScan;

public class ReferralWebsiteDb {


	public static ArrayList<String> referralData = new ArrayList<String>();

	public static boolean delete;

	public static boolean executeQuery(ResultSet rs) {
		try {
			if (delete) {
				rs.deleteRow();
			} else {
				String siteId = rs.getString("siteId");
				String ip = rs.getString("ip");
				referralData.add(siteId + "!" + ip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void connectionFinished(String actionType) {
		if (!actionType.equals("REFERRAL STATISTICS")) {
			return;
		}
		ReferralScan.ipTracker();
		DiscordBot.sendMessage(DiscordCommands.limitRefs ? DiscordConstants.GRAHAM_CHANNEL : DiscordConstants.OWNER_COMMAND_LOG_CHANNEL, DiscordCommands.queuedBotString, true);

	}

}
