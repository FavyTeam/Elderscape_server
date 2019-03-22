package game.content.quest.tab;

import core.GameType;
import core.ServerConstants;
import game.content.commands.NormalCommand;
import game.content.highscores.HighscoresDaily;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.Teleport;
import game.content.worldevent.Tournament;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import java.util.ArrayList;
import java.util.List;
import utility.Misc;

public class ActivityTab {

	private final static int YELL_COOLDOWN_MINUTES = 10;

	public static List<ActivityTab> activitiesList = new ArrayList<ActivityTab>();

	private ActivityTab(String activityType, long timeYelled) {
		this.activityType = activityType;
		this.timeYelled = timeYelled;
	}

	private String activityType;

	private long timeYelled;

	private String getActivityType() {
		return activityType;
	}

	private long getTimeYelled() {
		return timeYelled;
	}

	private void setTimeYelled(long time) {
		timeYelled = time;
	}

	public static ArrayList<String> activitiesHighscores = new ArrayList<String>();

	/**
	 * Players in Edgeville Pvp instanced area.
	 */
	public static int edgePvpPlayers;

	/**
	 * Players over 30 wilderness.
	 */
	public static int playersOver30Wild;

	/**
	 * Players at 30 wilderness or less.
	 */
	public static int playersUnder30Wild;

	/**
	 * Players in Edgeville wilderness.
	 */
	public static int playersInEdgeville;

	public static int duelArenaPlayers;

	public static int dicingPlayers;

	public static int pestControlPlayers;

	public static int clanWarsPlayers;

	public static int corpoealBeastPlayers;

	/**
	 * Update the text on the activity tab.
	 */
	public static void updateActivityTab(Player player) {
		if (GameType.isOsrsPvp()) {
			return;
		}
		int shortestTournamentTime = Integer.MAX_VALUE;
		String eventName = "";
		String timeLeft = "";
		for (int index = 0; index < Tournament.eventTimes.size(); index++) {
			String parse[] = Tournament.eventTimes.get(index).split("-");
			String string = parse[0].substring(0, 2);
			int eventTime = Integer.parseInt(string);
			int value = Integer.parseInt(HighscoresDaily.getInstance().getTimeLeft(parse[0], "TECHNICAL"));
			if (value < shortestTournamentTime) {
				shortestTournamentTime = value;
				timeLeft = HighscoresDaily.getInstance().getTimeLeft(parse[0], "HIGHSCORES");
				eventName = parse[1];
			}
		}
		player.getPA().sendFrame126(eventName + " in " + timeLeft, 23215);
		updateCurrentActiveEventInterface(player);

		for (int index = 0; index < activitiesHighscores.size(); index++) {
			String[] parse = activitiesHighscores.get(index).split("#");
			String activityName = parse[0];
			int players = Integer.parseInt(parse[1]);
			player.getPA().sendFrame126(activityName + ": " + players + " player" + Misc.getPluralS(players), 23221 + (index * 2));
		}
	}

	/**
	 * Update the interface frame in the Activity tab that shows which event is currently running.
	 */
	public static void updateCurrentActiveEventInterface(Player player) {
		String currentActiveEvent = InterfaceAssistant.returnActiveEventOrUpdateEventInterface(player, true);
		player.getPA().sendFrame126(currentActiveEvent.isEmpty() ? "" : "@yel@" + currentActiveEvent + " event is active!", 23217);
	}

	public static boolean isActivityTabButton(Player player, int buttonId) {
		if (GameType.isOsrsPvp()) {
			return false;
		}
		switch (buttonId) {
			// Call players
			case 90163:
				activityTabCallPlayers(player);
				return true;

			// World event
			case 90167:
				InterfaceAssistant.returnActiveEventOrUpdateEventInterface(player, false);
				return true;
		}

		// Activity tab, clicking on activity to teleport.
		if (buttonId >= 90181 && buttonId <= 90229) {
			teleportToActivity(player, buttonId);
			return true;
		}
		return false;
	}

	private static void activityTabCallPlayers(Player player) {
		String yell = "";
		String activity = getPlayerCurrentActivity(player);
		switch (activity) {
			case "Duel arena":
				yell = "Players at the Duel arena are asking for a stake! ::duel";
				break;
			case "Corporeal beast":
				yell = "Immediate help is required at the Corporeal beast ::corp";
				break;
			case "Pest control":
				yell = "Pest Control players are asking for more people to join! ::pc";
				break;
			case "Dicers":
				yell = "Dicers are asking for bets at ::dice";
				break;
			case "Clan wars":
				yell = "Safe pkers are looking for some fun at ::cw";
				break;
			/*
			case "Deep wild":
			yell = "";
			break;
			case "Under 30 wild":
			yell = "";
			break;
			*/
			case "Edge wild":
				yell = "Pkers in Edgeville wild are looking for a fight! ::wild";
				break;
		}

		if (yell.isEmpty()) {
			player.getPA().sendMessage("You do not seem to be doing any activity right now..");
			return;
		}
		ActivityTab activityInstance = null;
		for (int index = 0; index < activitiesList.size(); index++) {
			ActivityTab instance = activitiesList.get(index);
			if (instance.getActivityType().equals(activity)) {
				activityInstance = instance;
				break;
			}
		}
		if (activityInstance == null) {
			activitiesList.add(new ActivityTab(activity, System.currentTimeMillis()));
		} else {
			if (System.currentTimeMillis() - activityInstance.getTimeYelled() < Misc.getMinutesToMilliseconds(YELL_COOLDOWN_MINUTES)) {
				long minutesLeft = (Misc.getMinutesToMilliseconds(YELL_COOLDOWN_MINUTES) - (System.currentTimeMillis() - activityInstance.getTimeYelled())) / 60000;
				String left = "minute" + Misc.getPluralS(minutesLeft);
				if (minutesLeft == 0) {
					minutesLeft = (Misc.getMinutesToMilliseconds(YELL_COOLDOWN_MINUTES) - (System.currentTimeMillis() - activityInstance.getTimeYelled())) / 1000;
					left = "second" + Misc.getPluralS(minutesLeft);
				}
				player.getPA().sendMessage(activityInstance.getActivityType() + " yell is currently on cooldown for another " + minutesLeft + " " + left + ".");
				return;
			}
			activityInstance.setTimeYelled(System.currentTimeMillis());
		}
		Announcement.announce(yell, "<img=30><col=006fcd>");
	}

	private static void teleportToActivity(Player player, int buttonId) {
		int index = buttonId - 90181;
		if (index > 0) {
			index /= 2;
		}
		if (index > activitiesHighscores.size() - 1) {
			return;
		}
		String[] split = activitiesHighscores.get(index).split("#");
		String activity = split[0];

		for (index = 0; index < ActivityData.values().length; index++) {
			ActivityData data = ActivityData.values()[index];
			if (activity.equalsIgnoreCase(data.getActivityName())) {
				Teleport.spellTeleport(player, data.x, data.y, data.height, false);
				break;
			}
		}

	}

	/**
	 * Update amount of players doing a specific activity every minute.
	 */
	public static void updatePlayerActivityCounter() {
		ActivityTab.playersInEdgeville = 0;
		ActivityTab.playersUnder30Wild = 0;
		ActivityTab.playersOver30Wild = 0;
		ActivityTab.edgePvpPlayers = 0;
		ActivityTab.duelArenaPlayers = 0;
		ActivityTab.pestControlPlayers = 0;
		ActivityTab.corpoealBeastPlayers = 0;
		ActivityTab.dicingPlayers = 0;
		ActivityTab.clanWarsPlayers = 0;
		activitiesHighscores.clear();
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player player = PlayerHandler.players[i];
			if (player == null) {
				continue;
			}
			if (player.bot) {
				continue;
			}

			switch (getPlayerCurrentActivity(player)) {
				case "Duel arena":
					duelArenaPlayers++;
					break;
				case "Corporeal beast":
					corpoealBeastPlayers++;
					break;
				case "Pest control":
					pestControlPlayers++;
					break;
				case "Dicers":
					dicingPlayers++;
					break;
				case "Clan wars":
					clanWarsPlayers++;
					break;
				case "Deep wild":
					playersOver30Wild++;
					break;
				case "Under 30 wild":
					playersUnder30Wild++;
					break;
				case "Edge wild":
					playersInEdgeville++;
					break;
				case "Edge Pvp":
					edgePvpPlayers++;
					break;
			}
		}
		String[] activities =
				{
						ActivityData.values()[0].getActivityName() + "#" + playersInEdgeville,
						ActivityData.values()[1].getActivityName() + "#" + duelArenaPlayers,
						ActivityData.values()[2].getActivityName() + "#" + corpoealBeastPlayers,
						ActivityData.values()[3].getActivityName() + "#" + pestControlPlayers,
						ActivityData.values()[4].getActivityName() + "#" + dicingPlayers,
						ActivityData.values()[5].getActivityName() + "#" + clanWarsPlayers,
						ActivityData.values()[6].getActivityName() + "#" + playersOver30Wild,
						ActivityData.values()[7].getActivityName() + "#" + playersUnder30Wild,
				};

		for (int index = 0; index < activities.length; index++) {
			activitiesHighscores.add(activities[index]);
		}
		activitiesHighscores = NormalCommand.sort(activitiesHighscores, "#");
	}

	private static String getPlayerCurrentActivity(Player player) {
		if (Area.inDangerousPvpArea(player)) {
			if (Area.inCityPvpArea(player) && GameType.isOsrsPvp()) {
				return "Edge Pvp";
			} else if (Area.inEdgevilleWilderness(player)) {
				return ActivityData.values()[0].getActivityName();
			} else if (player.getWildernessLevel() <= 30) {
				return ActivityData.values()[7].getActivityName();
			} else if (player.getWildernessLevel() > 30) {
				return ActivityData.values()[6].getActivityName();
			}
		}

		if (isPlayerRecentlyActive(player, 60)) {
			if (Area.inDuelArena(player)) {
				return ActivityData.values()[1].getActivityName();
			}
			if (Area.inCorporealBeastLair(player.getX(), player.getY(), player.getHeight())) {
				return ActivityData.values()[2].getActivityName();
			}
			if (Area.inClanWarsDangerousArea(player)) {
				return ActivityData.values()[5].getActivityName();
			}
		}
		return "";
	}

	private static enum ActivityData {
		EDGE_WILD(3087, 3517, 0),
		DUEL_ARENA(3366, 3266, 0),
		CORPOREAL_BEAST(2966, 4382, 2),
		PEST_CONTROL(2658, 2659, 0),
		DICERS(1690, 4250, 0),
		CLAN_WARS(3327, 4758, 0),
		DEEP_WILD(2539, 4714, 0),
		UNDER_30_WILD(3087, 3517, 0);

		private int x;

		private int y;

		private int height;

		private String getActivityName() {
			String[] split = this.name().split("_");
			String string = "";
			for (int index = 0; index < split.length; index++) {
				if (index == 0) {
					string = Misc.capitalize(split[0]) + " ";
				} else {
					string = string + split[index].toLowerCase() + (index == split.length - 1 ? "" : " ");
				}
			}
			return string;
		}

		private ActivityData(int x, int y, int height) {
			this.x = x;
			this.y = y;
			this.height = height;
		}
	}

	/**
	 * @param seconds The amount of seconds maximum that the player was last active to return true.
	 * @return True if the player has been active in the given seconds. Active by chatting, private messaging, clicking on the game screen.
	 */
	public static boolean isPlayerRecentlyActive(Player player, int seconds) {
		return System.currentTimeMillis() - player.getTimePlayerLastActive() <= Misc.getSecondsToMilliseconds(seconds);
	}
}
