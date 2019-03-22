package game.content.quest.tab;

import core.GameType;
import game.player.Player;
import game.player.PlayerHandler;
import java.util.ArrayList;
import utility.Misc;

/**
 * Quest tab interface updating.
 *
 * @author MGT Madness, created on 22-02-2015.
 */
public class InformationTab {

	private static int interfaceIdStart = 0;

	private static int buttonIdStart = 0;

	private static int buttonIdEnd = 0;

	/**
	 * Read the configuration for the Information tab on the Quest tab.
	 */
	public static void loadQuestTabInformationTabConfig() {
		if (GameType.isOsrsPvp()) {
			interfaceIdStart = 28026;
			buttonIdStart = 109134;
			buttonIdEnd = 109150;
		} else {
			interfaceIdStart = 23002;
			buttonIdStart = 89228;
			buttonIdEnd = 89246;
		}
	}

	//TODO seperate update quest tab for eco and pvp
	public static void updateQuestTab(Player player) {
		if (player == null) {
			return;
		}
		String playersOnlineString = "Players online: @gre@" + PlayerHandler.getBoostedPlayerCount();
		if (Misc.serverRecentlyLaunched()) {
			playersOnlineString = "Server recently restarted";
		}
		if (GameType.isOsrsPvp()) {
			String[] content =
					{
							"<img=18>@yel@ Server",
							updateTime(player),
							updateDate(player),
							"",
							"<img=19>@yel@ Players",
							playersOnlineString,
							"Edgeville wild count: @gre@" + ActivityTab.playersInEdgeville,
							"Under 30 wild count: @gre@" + ActivityTab.playersUnder30Wild,
							"Deep wild count: @gre@" + ActivityTab.playersOver30Wild,
							"Edge pvp count: @gre@" + ActivityTab.edgePvpPlayers,
							"",
							"<img=17>@yel@ Pk statistics",
							"Kills: @gr3@" + player.getWildernessKills(false),
							"Deaths: @gr3@" + player.getWildernessDeaths(false),
							"Kdr: @gr3@" + Misc.getKDR(player.getWildernessKills(false), player.getWildernessDeaths(false)),
							"Melee main kills: @gr3@" + player.getMeleeMainKills(),
							"Hybrid kills: @gr3@" + player.getHybridKills(),
							"Berserker pure kills: @gr3@" + player.getBerserkerPureKills(),
							"Pure kills: @gr3@" + player.getPureKills(),
							"Ranged tank kills: @gr3@" + player.getRangedTankKills(),
							"F2p kills: @gr3@" + player.getF2pKills(),
							"Current killstreak: @gr3@" + player.currentKillStreak,
							"Highest killstreak: @gr3@" + player.killStreaksRecord,
							"Safe kills: @gr3@" + player.safeKills,
							"Safe deaths: @gr3@" + player.safeDeaths,
							"Bot kills: @gr3@" + player.playerBotKills,
							"Bot deaths: @gr3@" + player.playerBotDeaths,
							"Current bot killstreak: @gr3@" + player.playerBotCurrentKillstreak,
							"Highest bot killstreak: @gr3@" + player.playerBotHighestKillstreak,
					};
			for (int i = 0; i < content.length; i++) {
				player.getPA().sendFrame126(content[i], interfaceIdStart + i);
			}
		} else {
			String[] content =
					{
							"<img=18>@yel@ Server",
							updateTime(player),
							updateDate(player),
							"",
							"<img=19>@yel@ Players",
							playersOnlineString,
							"Edgeville wild count: @gre@" + ActivityTab.playersInEdgeville,
							"Outside Edge wild count: @gre@" + (ActivityTab.playersUnder30Wild + ActivityTab.playersOver30Wild),
							"",
							"<img=32>@yel@ Statistics",
							"Boss score: @gr3@" + player.bossScoreUnCapped,
							"Barrows runs: @gr3@" + player.getBarrowsRunCompleted(),
							"Clue scrolls: @gr3@" + player.getClueScrollsCompleted(),
							"Kills: @gr3@" + player.getWildernessKills(false),
							"Deaths: @gr3@" + player.getWildernessDeaths(false),
							"Kdr: @gr3@" + Misc.getKDR(player.getWildernessKills(false), player.getWildernessDeaths(false)),
							"Melee main kills: @gr3@" + player.getMeleeMainKills(),
							"Hybrid kills: @gr3@" + player.getHybridKills(),
							"Berserker pure kills: @gr3@" + player.getBerserkerPureKills(),
							"Pure kills: @gr3@" + player.getPureKills(),
							"Ranged tank kills: @gr3@" + player.getRangedTankKills(),
							"F2p kills: @gr3@" + player.getF2pKills(),
							"Current killstreak: @gr3@" + player.currentKillStreak,
							"Highest killstreak: @gr3@" + player.killStreaksRecord,
					};
			for (int i = 0; i < content.length; i++) {
				player.getPA().sendFrame126(content[i], interfaceIdStart + i);
			}
		}
	}

	//TODO combine this with updateQuestTab method
	public static boolean isQuestTabInformationButton(Player player, int buttonId) {
		if (buttonId >= buttonIdStart && buttonId <= buttonIdEnd) {
			int index = buttonId - buttonIdStart;
			ArrayList<String> list = new ArrayList<String>();
			if (GameType.isOsrsPvp()) {
				list.add("My kills are: " + player.getWildernessKills(false));
				list.add("My deaths are: " + player.getWildernessDeaths(false));
				list.add("My kdr is: " + Misc.getKDR(player.getWildernessKills(false), player.getWildernessDeaths(false)));
				list.add("My melee main kills are: " + player.getMeleeMainKills());
				list.add("My hybrid kills are: " + player.getHybridKills());
				list.add("My berserker pure kills are: " + player.getBerserkerPureKills());
				list.add("My pure kills are: " + player.getPureKills());
				list.add("My ranged tank kills are: " + player.getRangedTankKills());
				list.add("My f2p kills are: " + player.getF2pKills());
				list.add("My current killstreak is: " + player.currentKillStreak);
				list.add("My highest killstreak is: " + player.killStreaksRecord);
				list.add("My safe kills are: " + player.safeKills);
				list.add("My safe deaths are: " + player.safeDeaths);
				list.add("My bot kills are: " + player.playerBotKills);
				list.add("My bot deaths are: " + player.playerBotDeaths);
				list.add("My current bot killstreak is: " + player.playerBotCurrentKillstreak);
				list.add("My highest bot killstreak is: " + player.playerBotHighestKillstreak);
			} else {
				list.add("My boss score is: " + player.bossScoreUnCapped);
				list.add("My barrows runs are: " + player.getBarrowsRunCompleted());
				list.add("My clue scrolls completed are: " + player.getClueScrollsCompleted());
				list.add("My kills are: " + player.getWildernessKills(false));
				list.add("My deaths are: " + player.getWildernessDeaths(false));
				list.add("My kdr is: " + Misc.getKDR(player.getWildernessKills(false), player.getWildernessDeaths(false)));
				list.add("My melee main kills are: " + player.getMeleeMainKills());
				list.add("My hybrid kills are: " + player.getHybridKills());
				list.add("My berserker pure kills are: " + player.getBerserkerPureKills());
				list.add("My pure kills are: " + player.getPureKills());
				list.add("My ranged tank kills are: " + player.getRangedTankKills());
				list.add("My f2p kills are: " + player.getF2pKills());
				list.add("My current killstreak is: " + player.currentKillStreak);
				list.add("My highest killstreak is: " + player.killStreaksRecord);
			}
			if (index > list.size() - 1) {
				return true;
			}
			if (System.currentTimeMillis() - player.lastQuickchatButtonClicked < 1500) {
				return true; //The delay which stops players from flooding chatbox by spam clicking
			} else {
				player.lastQuickchatButtonClicked = System.currentTimeMillis();
				player.getPA().quickChat(list.get(index));
				return true;
			}
		}
		return false;
	}

	public static String updateDate(Player player) {
		return "Server date: @gr3@" + PlayerHandler.currentDate;
	}

	public static String updateTime(Player player) {
		return "Server time: @gr3@" + PlayerHandler.currentTime;

	}

}
