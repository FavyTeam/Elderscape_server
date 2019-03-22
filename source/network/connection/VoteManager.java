package network.connection;

import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.commands.NormalCommand;
import game.content.miscellaneous.Announcement;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.Blacklist;
import utility.FileUtility;
import utility.Misc;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

public class VoteManager {

	public static boolean executeQuery(ResultSet rs, Player player) {
		// Must be kept here because this fixes issues where a player claims a donation and then logs off before the reward appears and ends up not getting reward
		// and it says claimed=1 on the sql db.
		// This player instance is on a different thread.
		if (PlayerHandler.players[player.getPlayerId()] == null || !PlayerHandler.players[player.getPlayerId()].getPlayerName().equals(player.getPlayerName())) {
			return false;
		}
		if (player.getDuelStatus() >= 1 || player.isInTrade() || player.getHeight() == 20) {
			player.getPA().sendMessage("You cannot vote right now.");
			player.websiteMessaged = true;
			return false;
		}

		if (VoteManager.voteLimitReached(player)) {
			return false;
		}

		try {
			int siteId = rs.getInt("site_id");
			String ipAddress = rs.getString("ip_address");
			//2017-09-17 10:54:23
			int tickets = getTotalTickestAmount(player, siteId);
			if (!hasSecondVoteRequirements(player, ipAddress, siteId)) {
				return true;
			}
			if (!ItemAssistant.addItem(player, 4067, tickets)) {
				player.getPA().sendMessage("You need at least 1 inventory space to claim your vote reward.");
				player.websiteMessaged = true;
				return false;
			}

			player.websiteMessaged = true;
			/*// Not too accurate because player can spend it all on cosmetics.
			if (tickets > 5)
			{
				int extraTickets = (int) (Math.floor((double) (tickets - 5)) / (double) 5);
				int extraDonatorBloodMoney = (extraTickets * ServerConstants.VOTE_SHOP_BLOOD_MONEY_AMOUNT);
				CoinEconomyTracker.addIncomeList(player, "votedonator " + extraDonatorBloodMoney);
			}
			*/
			player.totalWebsiteClaimed += tickets;
			rareReward(player);
			player.timeVoted = System.currentTimeMillis();
			player.votesClaimed++;

			player.voteTimes.add(System.currentTimeMillis() + "");
			if (player.voteTimes.size() >= ServerConstants.VOTE_SITES_AMOUNT + 1) {
				player.voteTimes.remove(0);
			}
			player.voteTotalPoints += tickets;
			if (!lastPlayerVoted.equals(player.getPlayerName())) {
				currentVotes++;
				lastPlayerVoted = player.getPlayerName();
			}
			if (currentVotes == 5) {
				int amount = getVoteBloodMoneyAmount(null);
				Announcement.announce("Another 5 players have voted, ::vote for " + Misc.formatNumber(amount) + " bm and a chance to win Ags/Claws!", "<img=10><col=0000ff>");
				currentVotes = 0;
			}
			VoteTracker.voteList.add(new VoteTracker(player.getPlayerName(), player.addressUid, ipAddress, siteId, System.currentTimeMillis()));
			rs.updateInt("claimed", 1);
			rs.updateRow();
			return true;
		} catch (Exception e) {
			Misc.print("Vote manager failure.");
			e.printStackTrace();
		}
		return true;
	}

	public static int extraRunelocusTickets = 5;

	/**
	 * True to limit each account to a specific number of votes to making Vpning harder by around 50%
	 */
	public static boolean limitVotesPerAccount = true;

	/**
	 * True to lock a limited number of votes per computer to reduce Vpning by around 90%
	 */
	public static boolean lockVotesToPc = true;

	private static boolean hasSecondVoteRequirements(Player player, String ipAddress, int siteId) {
		for (int index = 0; index < VoteTracker.voteList.size(); index++) {
			VoteTracker data = VoteTracker.voteList.get(index);

			// This will be true if the player is using the same ip to vote the the same website over and over again.
			// This is a bug on the toplist end, so this is the only way to stop it. These bug abusers are not registering votes.
			boolean voteBugAbuse = ipAddress.equals(data.addressIp) && siteId == data.siteId;

			// True if the same computer already voted on a specific site.
			if (voteBugAbuse) {
				if (System.currentTimeMillis() - data.timeClaimed < ServerConstants.MILLISECONDS_HOUR * 11) {
					return false;
				} else {
					VoteTracker.voteList.remove(index);
					return true;
				}
			}
			if (Misc.uidMatches(player.addressUid, data.uid) && siteId == data.siteId && lockVotesToPc) {
				if (System.currentTimeMillis() - data.timeClaimed < ServerConstants.MILLISECONDS_HOUR * 11) {
					long minutesLeft = (ServerConstants.MILLISECONDS_HOUR * 11) - (System.currentTimeMillis() - data.timeClaimed);
					minutesLeft /= 60000;
					player.getPA().sendMessage("You have recently voted, you can claim more votes in " + minutesLeft + " minute" + Misc.getPluralS(minutesLeft) + ".");
					player.websiteMessaged = true;
					return false;
				} else {
					VoteTracker.voteList.remove(index);
					return true;
				}
			}

			// I have this Runelocus abuser that only votes on Runelocus sites.
			if ((limitVotesPerAccount || lockVotesToPc) && player.getPlayerName().equals(data.name) && siteId == data.siteId) {
				if (System.currentTimeMillis() - data.timeClaimed < ServerConstants.MILLISECONDS_HOUR * 11) {
					long minutesLeft = (ServerConstants.MILLISECONDS_HOUR * 11) - (System.currentTimeMillis() - data.timeClaimed);
					minutesLeft /= 60000;
					player.getPA().sendMessage("You have recently voted, you can claim more votes in " + minutesLeft + " minute" + Misc.getPluralS(minutesLeft) + ".");
					player.websiteMessaged = true;
					return false;
				} else {
					VoteTracker.voteList.remove(index);
					return true;
				}

			}
		}
		return true;
	}

	/**
	 * Get the total amount of tickets if the player were to claim 1 vote.
	 */
	public static int getTotalTickestAmount(Player player, int siteId) {

		int tickets = 0;
		// Runelocus
		if (siteId == 1) {
			tickets += extraRunelocusTickets;
		}

		if (siteId <= 10) {
			tickets += 5;
		} else if (siteId <= 20) {
			tickets += 4;
		} else if (siteId <= 30) {
			tickets += 3;
		} else if (siteId <= 40) {
			tickets += 2;
		} else if (siteId <= 50) {
			tickets += 1;
		}

		if (GameMode.getDifficulty(player, "GLADIATOR")) {
			tickets *= 1.2;
		}
		tickets += getBonusDonatorTicketPerVote(player);
		return tickets;
	}

	private static int getBonusDonatorTicketPerVote(Player player) {
		if (player.isSupremeDonator()) {
			return 16;
		} else if (player.isImmortalDonator()) {
			return 14;
		} else if (player.isUberDonator()) {
			return 12;
		} else if (player.isUltimateDonator()) {
			return 10;
		} else if (player.isLegendaryDonator()) {
			return 8;
		} else if (player.isExtremeDonator()) {
			return 6;
		} else if (player.isSuperDonator()) {
			return 4;
		} else if (player.isDonator()) {
			return 2;
		}
		return 0;
	}

	public static ArrayList<String> voteRareItems = new ArrayList<String>();

	public static int currentVotes;

	/**
	 * To avoid spam from Vpn voters.
	 */
	public static String lastPlayerVoted = "";

	/**
	 * Blood money reward per 5 vote tickets.
	 */
	public static int voteReward = 1000;

	public static final int LOOT_CHANCE = 600;

	public static void rareReward(Player player) {
		// If they vote 4 times a day, they will get a loot every 5 days.
		// The loot is most likely a barrows piece.
		if (Misc.hasOneOutOf(LOOT_CHANCE)) {
			int itemId = Misc.hasPercentageChance(50) ? 11802 : 20784;

			if (!player.profilePrivacyOn) {
				Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " received one " + ItemAssistant.getItemName(itemId) + " from Voting.");
			}
			voteRareItems.add(Misc.getDateAndTime() + ", " + player.getPlayerName() + ", " + ItemAssistant.getItemName(itemId));
			RareDropLog.appendRareDrop(player, "Voting: " + ItemAssistant.getItemName(itemId));
			player.getPA().sendScreenshot(ItemAssistant.getItemName(itemId), 2);
			ItemAssistant.addItemToInventoryOrDrop(player, itemId, 1);
		}
	}

	public static void voteAlert(Player player) {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		// If it has been less than 12 hours since last time claimed vote, then return.
		if (System.currentTimeMillis() - player.timeVoted < Misc.getHoursToMilliseconds(12)) {
			return;
		}
		if (!player.isTutorialComplete()) {
			return;
		}

		// Notify player every 4 hours if they haven't voted.
		if (System.currentTimeMillis() - player.timeVoteNotificationAlerted < Misc.getHoursToMilliseconds(4)) {
			return;
		}
		if (Combat.inCombat(player)) {
			return;
		}
		player.timeVoteNotificationAlerted = System.currentTimeMillis();
		player.getDH()
		      .sendItemChat("Vote for rewards!", "You can ::vote now for " + Misc.formatNumber(getVoteBloodMoneyAmount(player)) + " " + ServerConstants.getMainCurrencyName() + "!",
		                    "Voting will bring hundreds of new players to play!", "If you are lucky, you may win an Ags/D-claws from voting!",
		                    "The top voter of the week will receive 150 Donator tokens!", GameType.isOsrsEco() ? 1004 : 13316, 200, 20, -20);
	}

	private static int getVoteBloodMoneyAmount(Player player) {
		double donatorBonusTickets = 0;
		if (player != null) {
			if (getBonusDonatorTicketPerVote(player) > 0) {
				donatorBonusTickets = (double) getBonusDonatorTicketPerVote(player) * ServerConstants.VOTE_SITES_AMOUNT;
			}
		}
		return (int) (Math.floor((donatorBonusTickets + ServerConstants.VOTE_BASIC_TICKETS_TOTAL) / (double) 5)) * ServerConstants.getVoteShopBloodMoneyRewardAmount();
	}

	public static void newPlayerVoteAlert(Player player) {
		if (GameType.isOsrsEco()) {
			player.getDH().sendItemChat("Vote for rewards!",
			                            "You are eligible to ::vote for " + Misc.formatNumber(getVoteBloodMoneyAmount(player)) + " " + ServerConstants.getMainCurrencyName() + "!",
			                            "If you are lucky, you may win an Ags/D-claws from voting!", "After voting, type in ::claimvote and go to ::shops", 1004, 200, 20, -20);
		} else {
			player.getDH().sendItemChat("Vote for rewards!",
			                            "You can ::vote now for " + Misc.formatNumber(getVoteBloodMoneyAmount(player)) + " " + ServerConstants.getMainCurrencyName() + "! which is",
			                            "enough for alot of barrows items, whip and more!", "If you are lucky, you may win an Ags/D-claws from voting!",
			                            "After voting, type in ::claimvote and go to ::shops", GameType.isOsrsEco() ? 1004 : 13316, 200, 20, -20);
		}
	}

	public static boolean voteLimitReached(Player player) {
		if (!limitVotesPerAccount) {
			return false;
		}
		int recentlyVoted = 0;
		long highestTimeVoted = 0;
		for (int index = 0; index < player.voteTimes.size(); index++) {
			long timeVoted = Long.parseLong(player.voteTimes.get(index));
			if (timeVoted > highestTimeVoted) {
				highestTimeVoted = timeVoted;
			}
			if ((System.currentTimeMillis() - timeVoted) <= (ServerConstants.MILLISECONDS_HOUR * 11)) {
				recentlyVoted++;
			}
		}
		if (recentlyVoted >= ServerConstants.VOTE_SITES_AMOUNT) {
			long minutesLeft = (ServerConstants.MILLISECONDS_HOUR * 11) - (System.currentTimeMillis() - highestTimeVoted);
			minutesLeft /= 60000;
			player.getPA().sendMessage("You have recently claimed a vote, you can claim more votes in " + minutesLeft + " minute" + Misc.getPluralS(minutesLeft) + ".");
			player.websiteMessaged = true;
			return true;
		}
		return false;

	}

	private final static String VOTE_SETTINGS_LOCATION = "backup/logs/vote/settings.txt";

	/**
	 * Read the vote settings such as limited votes per account & pc.
	 */
	public static void readVoteSettings() {
		try {
			FileInputStream config = new FileInputStream(VOTE_SETTINGS_LOCATION);
			Properties property = new Properties();
			property.load(config);
			limitVotesPerAccount = Misc.readPropertyBoolean(property, "limitVotesPerAccount");
			lockVotesToPc = Misc.readPropertyBoolean(property, "lockVotesToPc");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save the vote settings such as limited votes per account & pc.
	 */
	public static void saveVoteSettings() {
		FileUtility.deleteAllLines(VOTE_SETTINGS_LOCATION);
		FileUtility.addLineOnTxt(VOTE_SETTINGS_LOCATION, "limitVotesPerAccount=" + limitVotesPerAccount);
		FileUtility.addLineOnTxt(VOTE_SETTINGS_LOCATION, "lockVotesToPc=" + lockVotesToPc);
	}


	/**
	 * Every 24 hours, save the most voted uid so i can check their alts possibly.
	 */
	public static void saveHighestVotedUid() {
		try {
			ArrayList<String> all = new ArrayList<String>();
			for (int index = 0; index < VoteTracker.voteList.size(); index++) {
				VoteTracker data = VoteTracker.voteList.get(index);
				if (Blacklist.useAbleData(data.uid)) {
					String uid = Misc.formatUid(data.uid);
					all.add(uid + ServerConstants.TEXT_SEPERATOR + "1");
				}
			}

			ArrayList<String> sort = new ArrayList<String>();
			sort = NormalCommand.sort(all, ServerConstants.TEXT_SEPERATOR);

			ArrayList<String> finaList = new ArrayList<String>();
			for (int index = 0; index < sort.size(); index++) {
				String[] parse = sort.get(index).split(ServerConstants.TEXT_SEPERATOR);
				int amount = Integer.parseInt(parse[1]);
				if (amount <= ServerConstants.VOTE_SITES_AMOUNT * 2) {
					break;
				}
				finaList.add(sort.get(index));
			}

			FileUtility.saveArrayContentsSilent("backup/logs/vote/" + Misc.millisecondsToDateOnly(System.currentTimeMillis() - Misc.getHoursToMilliseconds(2)) + ".txt", finaList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
