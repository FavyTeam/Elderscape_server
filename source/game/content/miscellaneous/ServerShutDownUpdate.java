package game.content.miscellaneous;

import core.Server;
import core.ServerConstants;
import game.content.clanchat.ClanChatHandler;
import game.content.commands.AdministratorCommand;
import game.content.donator.MysteryBox;
import game.content.highscores.Highscores;
import game.content.highscores.HighscoresDaily;
import game.content.highscores.HighscoresHallOfFame;
import game.content.interfaces.NpcDoubleItemsInterface.DoubleItemsLog;
import game.content.interfaces.donator.DonatorMainTab;
import game.content.interfaces.donator.DonatorMainTab.AccountOffersGiven;
import game.content.minigame.lottery.Lottery;
import game.content.packet.CommandPacket;
import game.content.staff.StaffActivity;
import game.content.staff.StaffManagement;
import game.content.staff.StaffPresence;
import game.content.tradingpost.TradingPost;
import game.content.wildernessbonus.KillReward;
import game.content.worldevent.Tournament;
import game.content.worldevent.WorldEvent;
import game.log.CoinEconomyTracker;
import game.log.FlaggedData;
import game.log.GameTickLog;
import game.log.NewPlayerIpTracker;
import game.log.StakingLog;
import game.npc.pet.Pet;
import game.player.LogOutUpdate;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.Ban;
import game.player.punishment.BannedData;
import game.player.punishment.Blacklist;
import game.player.punishment.DuelArenaBan;
import game.player.punishment.IpMute;
import game.player.punishment.RagBan;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import network.connection.DonationManager;
import network.connection.InvalidAttempt;
import network.connection.InvalidAttempt.AutoBlacklisted;
import network.connection.VoteManager;
import tools.discord.api.DiscordBot;
import utility.ChargebackPlayerAutoJail;
import utility.EmailSystem;
import utility.FileUtility;
import utility.HighestPlayerCount;
import utility.LeakedSourceApi;
import utility.Misc;
import utility.OsBotCommunication;

/**
 * Saves executed on server shutdown.
 *
 * @author MGT Madness, created on 21-07-2017.
 */
public class ServerShutDownUpdate implements Runnable {
	/**
	 * Store the time the server was saved.
	 */
	public static long timeServerAutoSaved;

	/**
	 * True if the save method is being run. Because this can be spammed by miss clicking save logs or i click save and the server auto save kicks in. Will cause issues because
	 * it is also run on a seperate thread when being called manually by save logs button.
	 */
	public static boolean serverSaveActive;

	/**
	 * Save server every x minutes.
	 */
	public static void serverAutoLogsSaveLoop() {
		if (System.currentTimeMillis() - timeServerAutoSaved < 60000 * Server.saveTimer) {
			return;
		}
		timeServerAutoSaved = System.currentTimeMillis();
		(new Thread(new ServerShutDownUpdate())).start();
	}

	@Override
	public void run() {
		serverRestartContentUpdate("Auto server logs save", false, false);
	}

	/**
	 * Make sure everything added here has support of being executed every 20 minutes while server is online.
	 */
	public static void serverRestartContentUpdate(String calledFrom, boolean restart, boolean logOut) {
		if (serverSaveActive) {
			Misc.print("Server save is already active!");
			return;
		}
		serverSaveActive = true;
		Misc.printDontSave("Server save initiated: " + calledFrom + ", restart: " + restart + ", logout: " + logOut);
		long time = System.currentTimeMillis();
		if (logOut) {
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				Player loop = PlayerHandler.players[i];
				if (loop == null || !loop.isActive()) {
					continue;
				}
				try {
					if (loop.getDoingAgility()) {
						loop.setX(loop.agilityEndX);
						loop.setY(loop.agilityEndY);;
					}
					LogOutUpdate.main(PlayerHandler.players, i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		DonationManager.currentOsrsBalanceSave();
		HighscoresDaily.getInstance().saveDailyHighscoresType();
		CoinEconomyTracker.updateCoinEconomyLog();
		GameTickLog.saveLagLogFile();
		Highscores.saveHighscoresFiles();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		FileUtility.saveArrayContents("backup/logs/trade/" + dateFormat.format(cal.getTime()) + ".txt", ItemTransferLog.data);
		ItemTransferLog.data.clear();
		//FileUtility.saveArrayContents("backup/logs/tournament_debug.txt", Tournament.debug);
		//Tournament.debug.clear();
		PmLog.savePmLog();
		HackLog.saveHackLog();
		FileUtility.saveArrayContents(PmLog.FILE_LOCATION + "alert.txt", PmLog.alertLog);
		PmLog.alertLog.clear();
		DiceSystem.serverSave();
		FileUtility.saveArrayContents("backup/logs/eventdebug.txt", WorldEvent.debug);
		WorldEvent.debug.clear();
		FileUtility.saveArrayContents("backup/logs/trade/rigged stake.txt", StakingLog.data);
		StakingLog.data.clear();
		FlaggedData.loadFlaggedData();
		DuelArenaBan.saveDuelBans();
		if (InvalidAttempt.autBlacklistUpdated) {
			InvalidAttempt.autBlacklistUpdated = false;
			AutoBlacklisted.save();
		}
		GiveAway.saveGiveAwayFiles();
		OsBotCommunication.saveFile();
		AutoBlacklisted.loadAutoBlacklist();
		FileUtility.saveArrayContents("backup/logs/bruteforce/autoblacklisted reason.txt", InvalidAttempt.autoBlacklistReason);
		InvalidAttempt.autoBlacklistReason.clear();
		InvalidAttempt.saveInvalidAttemptLog();
		FileUtility.saveArrayContentsSilent("backup/logs/blacklisted/flood_block_reason.txt", Blacklist.floodBlockReason);
		Blacklist.floodBlockReason.clear();
		Blacklist.loadPermanentBlacklist();
		Blacklist.loadBlacklistedData();
		FileUtility.saveArrayContents("./backup/logs/pvp/kills.txt", KillReward.killLog);
		KillReward.killLog.clear();
		FileUtility.saveArrayContents("./backup/logs/bruteforce/banned_log_in_attempts.txt", InvalidAttempt.bannedAttemptsHistory);
		InvalidAttempt.bannedAttemptsHistory.clear();
		//FileUtility.saveArrayContents("backup/logs/wild_multilog_debug.txt", RagBan.wildDebug);
		//RagBan.wildDebug.clear();
		FileUtility.saveArrayContents("backup/logs/player base/collection ip name.txt", NewPlayerIpTracker.ipCollectionListIpName);
		NewPlayerIpTracker.ipCollectionListIpName.clear();
		TradingPost.serverSave();
		AdministratorCommand.packetLogSave(null);
		InvalidAttempt.saveInvalidAttemptLog();
		FileUtility.saveArrayContents("backup/logs/unknowncommands.txt", CommandPacket.unknownCommands);
		CommandPacket.unknownCommands.clear();
		LeakedSourceApi.saveToFile();
		FileUtility.saveArrayContents("./backup/logs/vote items.txt", VoteManager.voteRareItems);
		VoteManager.voteRareItems.clear();
		FileUtility.deleteAllLines("./backup/logs/tournament titles.txt");
		FileUtility.saveArrayContents("./backup/logs/tournament titles.txt", Tournament.tournamentTitleWinners);
		FileUtility.deleteAllLines("./backup/logs/petsclaim.txt");
		FileUtility.saveArrayContents("./backup/logs/petsclaim.txt", Pet.petsToClaim);
		FileUtility.deleteAllLines("./backup/logs/ipmute.txt");
		FileUtility.saveArrayContents("./backup/logs/ipmute.txt", IpMute.ipMutedData);
		FileUtility.deleteAllLines("./backup/logs/ban.txt");
		FileUtility.saveArrayContents("./backup/logs/ban.txt", Ban.bannedList);
		FileUtility.deleteAllLines("./backup/logs/ragban.txt");

		FileUtility.deleteAllLines("./backup/logs/donations/notify.txt");
		FileUtility.saveArrayContents("./backup/logs/donations/notify.txt", ChargebackPlayerAutoJail.notifyList);

		FileUtility.saveArrayContents("backup/logs/mystery_box.txt", MysteryBox.mysteryBoxHistory);
		MysteryBox.mysteryBoxHistory.clear();

		FileUtility.saveArrayContents("backup/logs/donations/received_history.txt", DonationManager.donationReceivedHistory);
		DonationManager.donationReceivedHistory.clear();

		AccountOffersGiven.saveAccountOffersGiven();
		VoteManager.saveVoteSettings();
		RagBan.saveRagBanList();
		FileUtility.saveArrayContents("./backup/logs/system log/packetloglag.txt", GameTickLog.singlePlayerPacketLog);
		GameTickLog.singlePlayerPacketLog.clear();
		AdministratorCommand.saveAllPacketAbuse(null);
		KillReward.saveKillTypeLog();
		ClanChatHandler.serverRestart(logOut);
		PlayerHandler.getUpTime();
		Tournament.saveLastEvenType();
		NewPlayerIpTracker.save("SAVE DATA", "");
		DonationManager.saveDailyDonationsProgress();
		DonationManager.saveDailyOsrsProgress();
		DonationManager.saveDailyBmtPayments();
		DonationManager.saveDailyCustomPayments();
		HighscoresHallOfFame.save();
		ClaimPrize.save();
		HighestPlayerCount.saveHighestPlayerCountProgress();
		Lottery.saveLotteryFiles();
		StaffManagement.saveStaffManagementFiles();
		StaffPresence.saveStaffPresence();
		StaffActivity.saveStaffActivity();
		Blacklist.saveFloodIps();
		StaffManagement.saveSignUpData();
		FileUtility.saveArrayContentsSilent("discord_bot_debug.txt", DiscordBot.debug);
		DiscordBot.debug.clear();
		DonatorMainTab.saveItemsOnOfferFile();
		FileUtility.saveArrayContentsSilent("backup/logs/item_pickup_detailed.txt", ItemTransferLog.detailedItemPickUp);
		ItemTransferLog.detailedItemPickUp.clear();
		FileUtility.saveArrayContentsSilent("backup/logs/item_pickup_detailed_expensive.txt", ItemTransferLog.detailedItemPickUpExpensive);
		ItemTransferLog.detailedItemPickUpExpensive.clear();
		FileUtility.saveArrayContentsSilent("backup/logs/item_pickup_tuqan.txt", ItemTransferLog.pickupItemToFindTuqan);
		ItemTransferLog.pickupItemToFindTuqan.clear();
		FileUtility.saveArrayContentsSilent("backup/logs/item_pickup_trade_completed_godpaso.txt", ItemTransferLog.tradeCompletedToFindGodPasto);
		ItemTransferLog.tradeCompletedToFindGodPasto.clear();
		FileUtility.saveArrayContentsSilent("backup/logs/random_event_log.txt", RandomEvent.randomEventLog);
		RandomEvent.randomEventLog.clear();
		FileUtility.saveArrayContentsSilent("backup/logs/logout/logout_" + System.currentTimeMillis() + Misc.random(10_000_000) + ".txt", PlayerHandler.disconnectReason);
		PlayerHandler.disconnectReason.clear();
		FileUtility.saveArrayContentsSilent("backup/logs/logout/playertick_" + System.currentTimeMillis() + Misc.random(10_000_000) + ".txt", PlayerHandler.individualPlayerPacketLog);
		PlayerHandler.individualPlayerPacketLog.clear();
		FileUtility.saveArrayContentsSilent("backup/logs/donations/account_offers_log.txt", DonatorMainTab.accountOfferLog);
		DonatorMainTab.accountOfferLog.clear();
		DonationManager.saveLatest07Rates();
		BannedData.readBannedData();
		DoubleItemsLog.saveLog();
		YoutubePaid.savePaidYoutubersFile();

		if (!ItemTransferLog.rwtAlert.isEmpty()) {
			EmailSystem.addPendingEmail("Real world trade alert", ItemTransferLog.rwtAlert, "mgtdt@yahoo.com");
		}
		FileUtility.saveArrayContentsSilent("backup/logs/rwt/alert.txt", ItemTransferLog.rwtAlert);
		ItemTransferLog.rwtAlert.clear();
		if (restart || logOut) {
			FileUtility.addLineOnTxt("backup/logs/system log/save log.txt", Misc.getDateAndTime() + " Saved. Logout: " + logOut + ". Restart: " + restart + ", " + calledFrom);
		}

		PlayerHandler.logOut = logOut;
		FileUtility.saveArrayContents("./backup/logs/system log/output.txt", Misc.consolePrint);
		Misc.consolePrint.clear();
		if (logOut) {
			Server.getSqlNetwork().shutdown();
		}
		Misc.print("Server content save took: " + (System.currentTimeMillis() - time) + " ms");
		serverSaveActive = false;
	}
}
