package game.player.punishment;

import core.ServerConstants;
import game.player.Player;
import utility.Misc;

/**
 * Mute & Un-mute feature.
 *
 * @author MGT Madness, created on 01-02-2016.
 */
public class Mute {

	/**
	 * Calculate the time untill un-mute and notify player.
	 */
	public static void calculateTimeTillUnmute(Player player, long timeUnmuted, String message) {
		long totalSeconds = 0;
		double decimalHours = 0;
		int totalMinutes = 0;
		int integerHours = 0;
		double minutesExtra = 0;
		double lastOne = 0;
		totalSeconds = (timeUnmuted - System.currentTimeMillis()) / 1000;
		totalMinutes = (int) totalSeconds / 60;
		decimalHours = totalMinutes / 60.0;
		if (decimalHours < 1) {
			player.playerAssistant.sendMessage(message + totalMinutes + " minute" + Misc.getPluralS(totalMinutes) + ".");
			return;
		}
		integerHours = (int) decimalHours;
		lastOne = (decimalHours - integerHours) * 100;
		minutesExtra = (60.0 / 100.0) * lastOne;
		player.playerAssistant.sendMessage(
				message + integerHours + " hour" + Misc.getPluralS(integerHours) + " and " + (int) minutesExtra + " minute" + Misc.getPluralS((int) minutesExtra) + ".");
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player is Account muted or Player muted.
	 */
	public static boolean isMuted(Player player) {
		if (player.ipMuted) {
			String serveLeft = "";
			for (int index = 0; index < IpMute.ipMutedData.size(); index++) {
				String[] parse = IpMute.ipMutedData.get(index).split(ServerConstants.TEXT_SEPERATOR);
				String storedName = parse[0];
				String ip = parse[1];
				String uid = parse[2];
				int hours = 0;
				long time = 0;
				//  length 3 = old mute system, length 5 is new one where it is timed and stores hours length and time muted.
				if (parse.length == 5) {
					hours = Integer.parseInt(parse[3]);
					time = Long.parseLong(parse[4]);
				}
				if (ip.equals(player.addressIp) || player.getPlayerName().toLowerCase().equalsIgnoreCase(storedName) || Misc.uidMatches(player.addressUid, uid)) {
					if ((time + Misc.getHoursToMilliseconds(hours)) < System.currentTimeMillis() && hours > 0) {
						IpMute.ipMutedData.remove(index);
						player.ipMuted = false;
						return false;
					}
					long hoursToServeLeft = (time + Misc.getHoursToMilliseconds(hours)) - System.currentTimeMillis();
					hoursToServeLeft /= Misc.getHoursToMilliseconds(1);
					if (hoursToServeLeft < 1 && hoursToServeLeft > 0 || hours == 1) {
						serveLeft = "You have less than an hour left of your mute.";
					} else {
						serveLeft = "You have " + hoursToServeLeft + " hours of mute left.";
					}
					if (hours == 0) {
						serveLeft = "You are permanently muted.";
					}

					// if more than 2 weeks
					if (hours == 0 || hoursToServeLeft > 336) {
						player.getPA().sendMessage("You are permanently muted for committing a serious offense.");
						player.getPA().sendMessage("You have to pay 10m OSRS fine to be un-muted. Buy the gold from ::goldwebsites");
						player.getPA().sendMessage("Then give it to any Moderator on ::staff to be un-muted.");
					}
					break;
				}
			}
			player.getPA().sendMessage(serveLeft + " Please read the ::rules");
			if (!player.ipMutedOriginalName.isEmpty()) {
				player.getPA().sendMessage("Your other account '" + Misc.capitalize(player.ipMutedOriginalName) + "' caused the ip mute.");
			}
			return true;
		}
		return false;
	}
}
