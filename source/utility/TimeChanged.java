package utility;

import core.Server;
import core.ServerConstants;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.interfaces.donator.DonatorMainTab;
import game.content.quest.tab.ActivityTab;
import game.content.quest.tab.InformationTab;
import game.content.staff.StaffActivity;
import game.content.staff.StaffPresence;
import game.content.worldevent.Tournament;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import network.connection.DonationManager;
import network.connection.VoteTracker;
import network.sql.SQLConstants;
import network.sql.SQLMethods;
import network.sql.SQLNetwork;
import network.sql.query.impl.IntParameter;
import network.sql.query.impl.StringParameter;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import tools.discord.content.DiscordCommands;
import tools.multitool.ReferralScan;

/**
 * Handles what happens when the time has changed, every 60 seconds.
 *
 * @author MGT Madness, created on 28-10-2017.
 */
public class TimeChanged {

	/**
	 * This player counter is updated every minute. So if it drops more than 10 in 1 minute, it will send an email to me, could be a ddos etc..
	 */
	public static int playersUpdatedEveryMinute;


	/**
	 * Save the exact time of when the date time changed check was last checked.
	 */
	public static long lastUpdatedTimeAndDate;


	/**
	 * Update date and time for player every 60 seconds.
	 */
	public static boolean updateTimeAndDate() {
		if (System.currentTimeMillis() - TimeChanged.lastUpdatedTimeAndDate < 10000) {
			return false;
		}
		TimeChanged.lastUpdatedTimeAndDate = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
		Calendar cal = Calendar.getInstance();
		String newTime = dateFormat.format(cal.getTime());
		if (newTime.equals(PlayerHandler.currentTime)) {
			return false;
		}

		updateBoostedPlayerCount();
		SQLMethods.updateStatsTimeTable();
		PlayerHandler.currentTime = newTime;
		int amountDropped = TimeChanged.playersUpdatedEveryMinute - PlayerHandler.getRealPlayerCount();
		if (amountDropped >= ServerConstants.PLAYER_DROP_ALERT & !PlayerHandler.updateRunning) {
			EmailSystem.addPendingEmail("Players dropped by: " + amountDropped + "!", "Players dropped by: " + amountDropped + "/" + TimeChanged.playersUpdatedEveryMinute + "!", "mohamed_ffs25ffs@yahoo.com");
		}
		SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_PLAYER_ONLINE) + " (time, online_count) VALUES(?, ?)", new StringParameter(1, Misc.getDateAndTime()), new IntParameter(2, PlayerHandler.getRealPlayerCount()));
		TimeChanged.playersUpdatedEveryMinute = PlayerHandler.getRealPlayerCount();
		DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal1 = Calendar.getInstance();
		PlayerHandler.currentDate = dateFormat1.format(cal1.getTime());
		ActivityTab.updatePlayerActivityCounter();
		Tournament.currentTime();
		HighestPlayerCount.timeChanged();
		StaffPresence.checkStaffPresence(newTime);
		StaffActivity.checkStaffActivity(newTime);
		return true;
	}


	private static void updateBoostedPlayerCount() {
		PlayerHandler.playerCountBoostedUpdatedEveryMinute = 0;
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player loop = PlayerHandler.players[i];
			if (loop == null) {
				continue;
			}
			if (loop.bot) {
				continue;
			}
			PlayerHandler.playerCountBoostedUpdatedEveryMinute++;
		}
		PlayerHandler.playerCountBoostedUpdatedEveryMinute *= WebsiteLogInDetails.PLAYER_COUNT_MODIFIER;
	}

	/**
	 * When the time changes, the Information/activity tabs are updated for the player.
	 */
	public static void updateTimeForPlayer(Player player) {
		InformationTab.updateQuestTab(player);
		ActivityTab.updateActivityTab(player);
	}


	public static void newDayStartedUpdate() {
		String oldDate = Misc.millisecondsToDateOnly(System.currentTimeMillis() - Misc.getHoursToMilliseconds(2));
		DonatorMainTab.rotateItemsOnOfferList(true);
		VoteTracker.voteList.clear();

		DiscordCommands.addOutputText("Staff presence missing for the last 24 hours:");
		ArrayList<String> staff = new ArrayList<String>();
		for (int index = 0; index < StaffPresence.staffPresenceList.size(); index++) {
			StaffPresence instance = StaffPresence.staffPresenceList.get(index);
			if (instance.getMinutesPresent() >= 30) {
				continue;
			}
			String text = instance.getHour() + ": " + instance.getMinutesPresent() + "/60";
			DiscordCommands.addOutputText(text);
			staff.add(text);
		}
		DiscordBot.sendMessage(DiscordConstants.STAFF_ACTIVITY_LOG_CHANNEL, DiscordCommands.queuedBotString, true);
		EmailSystem.addPendingEmail("Staff activity report", staff, "mohamed_ffs25ffs@yahoo.com");

		Server.logsCleanUp();
		NpcDoubleItemsInterface.dayChanged();
		StaffPresence.staffPresenceList.clear();
		double osrsProfit = DonationManager.osrsToday * DonationManager.OSRS_DONATION_MULTIPLIER;
		osrsProfit = (osrsProfit * (DonationManager.PERCENTAGE_PROFIT_PER_MIL / 100.0));
		osrsProfit = Misc.roundDoubleToNearestTwoDecimalPlaces(osrsProfit);
		SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_PAYMENT_DAILY_TOTAL) + " (date, total, bmt_total, osrs_total, custom_total) VALUES(?, ?, ?, ?, ?)", new StringParameter(1, Misc.getDateOnlyDashes()), new IntParameter(2, (int) DonationManager.totalPaymentsToday), new IntParameter(3, (int) DonationManager.bmtPaymentsToday), new IntParameter(4, (int) osrsProfit), new IntParameter(5, (int) DonationManager.customPaymentsToday));
		DonationManager.saveDailyDonationsData(oldDate);
		DonationManager.saveDailyOsrsData(oldDate);
		DonationManager.osrsReceivedThisServerSession = 0;
		DonationManager.saveDailyCustomPaymentsData(oldDate);

		CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent<Object>() {
			@Override
			public void execute(CycleEventContainer<Object> container) {
				container.stop();
			}

			@Override
			public void stop() {
				// Keep at the end, because it takes the longest to execute on a different thread for the discord bot.
				DiscordBot.sendMessage(DiscordConstants.OWNER_COMMAND_LOG_CHANNEL, "Daily referral statistics:", false);
				DiscordCommands.referralDonatedAmountLimit = 0;
				ReferralScan.initiate();
			}
		}, 10);
	}

}
