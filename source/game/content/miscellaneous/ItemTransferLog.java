package game.content.miscellaneous;

import core.ServerConstants;
import game.content.clanchat.ClanChatHandler;
import game.content.packet.PrivateMessagingPacket;
import game.content.tradingpost.TradingPost;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.log.StakingLog;
import game.player.Area;
import game.player.Player;
import game.player.punishment.BannedData;
import java.util.ArrayList;
import java.util.List;
import utility.FileUtility;
import utility.Misc;

/**
 * Log when a player transfers items through trading, dropping, staking.
 *
 * @author MGT Madness, created on 07-04-2017.
 */
public class ItemTransferLog {

	public final static int MINIMUM_MINUTES_FRIENDS = 10;


	public final static int WEALTH_TRANSFER_NOTIFICATION = 200_000;

	/**
	 * If both sides present x blood money or more into the stake/trade/kill, do not flag.
	 */
	public final static int WEALTH_FROM_EACH_PLAYER_PRESENTED = 400_000;

	/**
	 * If both parties have x account wealth, ignore them.
	 */
	public final static int ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED = 900_000;

	/**
	 * name-data to add on seperate line
	 */
	public static ArrayList<String> data = new ArrayList<String>();

	/**
	 * Store rwt data here.
	 */
	public static ArrayList<String> rwtAlert = new ArrayList<String>();

	public static ArrayList<String> detailedItemPickUp = new ArrayList<String>();

	public static ArrayList<String> detailedItemPickUpExpensive = new ArrayList<String>();

	/**
	 * Store chat log here.
	 */
	public static ArrayList<String> rwtChatLog = new ArrayList<String>();


	public static ArrayList<String> pickupItemToFindTuqan = new ArrayList<String>();

	public static ArrayList<String> tradeCompletedToFindGodPasto = new ArrayList<String>();

	private String playerName;

	private long timeAdded;

	private String rwtText;

	/**
	 * This is created because some clever Rwters would log on the buyer's account, do the Rwt trade,
	 * then log off and the deal is done.
	 */
	public ItemTransferLog(String playerName, long timeAdded, String rwtText) {
		this.playerName = playerName;
		this.timeAdded = timeAdded;
		this.rwtText = rwtText;
	}

	public static List<ItemTransferLog> itemTransferLogList = new ArrayList<ItemTransferLog>();

	public static void tradeCompleted(Player one, Player two) {
		int playerOneTotal = 0;
		int playerTwoTotal = 0;
			String rwtSave = "";
			String tokensOne = "";
			String tokensTwo = "";
			for (GameItem item : one.getTradeAndDuel().offeredItems) {
				if (item.getId() > 0 && item.getAmount() > 0) {
					int itemPrice = ServerConstants.getItemValue(item.getId());
					if (item.getId() == 7478) {
						tokensOne = "(" + item.getAmount() + " tokens) ";
						itemPrice = TradingPost.averagePrice;
					}
					playerOneTotal += itemPrice * item.getAmount();
				}
			}
			for (GameItem item : two.getTradeAndDuel().offeredItems) {
				if (item.getId() > 0 && item.getAmount() > 0) {
					int itemPrice = ServerConstants.getItemValue(item.getId());
					if (item.getId() == 7478) {
						tokensTwo = "(" + item.getAmount() + " tokens)";
						itemPrice = TradingPost.averagePrice;
					}
					playerTwoTotal += itemPrice * item.getAmount();
				}
			}
			if (ClanChatHandler.inDiceCc(one, false, false) && ClanChatHandler.inDiceCc(two, false, false)) {
				GameTimeSpent.setLastActivity(one, "DICING");
				GameTimeSpent.setLastActivity(two, "DICING");
				ClanChatHandler.sendDiceClanMessage("[Manager]", one.getClanId(),
				                                    one.getPlayerName() + " traded with " + two.getPlayerName() + ", " + Misc.formatRunescapeStyle(playerOneTotal) + " and "
				                                    + Misc.formatRunescapeStyle(playerTwoTotal));
			}
			if ((playerOneTotal + playerTwoTotal) >= 10000) {
				String incc = "";
				if (ClanChatHandler.inDiceCc(one, false, false) && ClanChatHandler.inDiceCc(two, false, false)) {
					incc = "Dicing";
				}
				String line = Misc.getDateAndTime() + " TRADE: " + incc + " [" + one.getPlayerName() + "] trading with [" + two.getPlayerName() + "], " + Misc.formatRunescapeStyle(
						playerOneTotal) + " " + tokensOne + "and " + Misc.formatRunescapeStyle(playerTwoTotal) + " " + tokensTwo;
				rwtSave = Misc.getDateAndTime() + " TRADE: " + incc + " [" + BannedData.getSourceName(one) + "] trading with [" + BannedData.getSourceName(two) + "], "
				          + Misc.formatRunescapeStyle(playerOneTotal) + " " + tokensOne + "and " + Misc.formatRunescapeStyle(playerTwoTotal) + " " + tokensTwo;
			YoutubePaid.addEntry(one.getPlayerName(), line);
			YoutubePaid.addEntry(two.getPlayerName(), line);
				data.add(line);

			RealWorldTradePoints.addSingleXfer(one.addressUid, one.addressIp, one.getLowercaseName(), false);
			RealWorldTradePoints.addSingleXfer(two.addressUid, two.addressIp, two.getLowercaseName(), false);
			}

			// Real world trade notification.
			int wealthTransfer = playerOneTotal - playerTwoTotal;
			int originalWealthTransfer = wealthTransfer;
			wealthTransfer = Misc.convertToPositive(wealthTransfer);
			//boolean poorPlayerOne = playerOneTotal <= BUY_TOTAL_RISK;
			//boolean poorPlayerTwo = playerTwoTotal <= BUY_TOTAL_RISK;
			boolean ignored = playerOneTotal >= WEALTH_FROM_EACH_PLAYER_PRESENTED && playerTwoTotal >= WEALTH_FROM_EACH_PLAYER_PRESENTED;

		if (wealthTransfer >= WEALTH_TRANSFER_NOTIFICATION) {
			long playerOneAccountWealth = ItemAssistant.getAccountBankValueLong(one);
			long playerTwoAccountWealth = ItemAssistant.getAccountBankValueLong(two);
			tradeCompletedToFindGodPasto.add("[" + Misc.getDateAndTime() + "] new entry----");
			tradeCompletedToFindGodPasto.add(one.getPlayerName() + ", " + one.addressIp + ", " + one.addressUid + ", " + Misc.formatRunescapeStyle(playerOneTotal) + ", " + Misc.formatRunescapeStyle(playerOneAccountWealth));
			tradeCompletedToFindGodPasto.add(two.getPlayerName() + ", " + two.addressIp + ", " + two.addressUid + ", " + Misc.formatRunescapeStyle(playerTwoTotal) + ", " + Misc.formatRunescapeStyle(playerTwoAccountWealth));
			tradeCompletedToFindGodPasto.add(rwtSave);
			tradeCompletedToFindGodPasto.add("originalWealthTransfer: " + originalWealthTransfer);
		}

		boolean samePlayer = Misc.isSamePlayer(one, two);
		if (wealthTransfer >= WEALTH_TRANSFER_NOTIFICATION && !ignored) {
				// Ignore dice area trades if one the players have dice in inventory.
				if (rwtSave.contains("TRADE: Dicing") && (ItemAssistant.hasItemInInventory(one, 16004) || ItemAssistant.hasItemInInventory(two, 16004))) {
					return;
				}
				long playerOneAccountWealth = ItemAssistant.getAccountBankValueLong(one);
				long playerTwoAccountWealth = ItemAssistant.getAccountBankValueLong(two);
				if (playerOneAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED
				    && playerTwoAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED) {
					ignored = true;
				}
				if (originalWealthTransfer < 0 && playerOneAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED) {
					ignored = true;
				}
				if (originalWealthTransfer > 0 && playerTwoAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED) {
					ignored = true;
				}
				if (!ignored) {
					one.setFlaggedForRwt(true);
					two.setFlaggedForRwt(true);
				if (!samePlayer) {
					if (PrivateMessagingPacket.getMinutesFriendsFor(one, two.getPlayerName()) < MINIMUM_MINUTES_FRIENDS && PrivateMessagingPacket.getMinutesFriendsFor(two, one.getPlayerName()) < MINIMUM_MINUTES_FRIENDS) {
						RealWorldTradePoints.addSingleXfer(one.addressUid, one.addressIp, one.getLowercaseName(), true);
						RealWorldTradePoints.addSingleXfer(two.addressUid, two.addressIp, two.getLowercaseName(), true);
						rwtAlert.add(rwtSave + ", friends: " + PrivateMessagingPacket.getMinutesFriendsFor(one, two.getPlayerName()));
					}
				} else {
					String buyerName = one.getPlayerName();
					if (playerOneTotal > playerTwoTotal) {
						buyerName = two.getPlayerName();
					}
					itemTransferLogList.add(new ItemTransferLog(buyerName.toLowerCase(), System.currentTimeMillis(), rwtSave));
				}
				}
			}
	}

	public static void duelWon(Player stakeWinner, Player stakeLoser) {
		int myStakedWealth = 0;
		int otherStakedWealth = 0;
		// If winner is nulled, then the loser wins the stake rewards.
		if (stakeWinner == null) {
			for (GameItem item : stakeLoser.getTradeAndDuel().myStakedItems) {
				if (item.getId() > 0 && item.getAmount() > 0) {
					myStakedWealth += ServerConstants.getItemValue(item.getId()) * item.getAmount();
				}
			}
			for (GameItem item1 : stakeLoser.getTradeAndDuel().otherStakedItems) {
				if (item1.getId() > 0 && item1.getAmount() > 0) {
					otherStakedWealth += ServerConstants.getItemValue(item1.getId()) * item1.getAmount();
				}
			}
			if ((myStakedWealth + otherStakedWealth) >= 10000) {
				String line = Misc.getDateAndTime() + " STAKE: [" + stakeLoser.getPlayerName() + "] won vs NULLED KILLER!, " + Misc.formatRunescapeStyle(myStakedWealth) + " vs " + Misc.formatRunescapeStyle(otherStakedWealth);
				data.add(line);
				YoutubePaid.addEntry(stakeLoser.getPlayerName(), line);
			}
		} else {
			String tokensWinner = "";
			String tokensLoser = "";
			String rwtSave = "";
			for (GameItem item : stakeWinner.getTradeAndDuel().myStakedItems) {
				int itemPrice = ServerConstants.getItemValue(item.getId());
				if (item.getId() == 7478) {
					tokensWinner = "(" + item.getAmount() + " tokens)";
					itemPrice = TradingPost.averagePrice;
				}
				if (item.getId() > 0 && item.getAmount() > 0) {
					myStakedWealth += itemPrice * item.getAmount();
				}
			}
			for (GameItem item : stakeWinner.getTradeAndDuel().otherStakedItems) {
				int itemPrice = ServerConstants.getItemValue(item.getId());
				if (item.getId() == 7478) {
					tokensLoser = "(" + item.getAmount() + " tokens)";
					itemPrice = TradingPost.averagePrice;
				}
				if (item.getId() > 0 && item.getAmount() > 0) {
					otherStakedWealth += itemPrice * item.getAmount();
				}
			}
			if ((myStakedWealth + otherStakedWealth) >= 10000) {
				String line = Misc.getDateAndTime() + " STAKE: [" + stakeWinner.getPlayerName() + "] won vs [" + stakeLoser.getPlayerName() + "], " + Misc.formatRunescapeStyle(
						myStakedWealth) + " " + tokensWinner + " vs " + Misc.formatRunescapeStyle(otherStakedWealth) + " " + tokensLoser;
				rwtSave = Misc.getDateAndTime() + " STAKE: [" + BannedData.getSourceName(stakeWinner) + "] won vs [" + BannedData.getSourceName(stakeLoser) + "], "
				          + Misc.formatRunescapeStyle(myStakedWealth) + " " + tokensWinner + " vs " + Misc.formatRunescapeStyle(otherStakedWealth) + " " + tokensLoser;
				data.add(line);
				YoutubePaid.addEntry(stakeLoser.getPlayerName(), line);
				YoutubePaid.addEntry(stakeWinner.getPlayerName(), line);
				RealWorldTradePoints.addSingleXfer(stakeWinner.addressUid, stakeWinner.addressIp, stakeWinner.getLowercaseName(), false);
				RealWorldTradePoints.addSingleXfer(stakeLoser.addressUid, stakeLoser.addressIp, stakeLoser.getLowercaseName(), false);
			}
			int winnerTotalAttacks = stakeWinner.stakeAttacks + stakeWinner.stakeSpecialAttacks;
			int loserTotalAttacks = stakeLoser.stakeAttacks + stakeLoser.stakeSpecialAttacks;
			if ((myStakedWealth + otherStakedWealth) >= WEALTH_TRANSFER_NOTIFICATION) {

				// If the difference in total attacks is 2, then log the stake.
				if (winnerTotalAttacks - loserTotalAttacks <= -2 || winnerTotalAttacks - loserTotalAttacks >= 2) {
					StakingLog.saveRiggedStakingSession(stakeWinner, stakeLoser, myStakedWealth, otherStakedWealth, tokensWinner, tokensLoser);
				}
			}

			// Real world trade notification.
			int wealthTransfer = myStakedWealth - otherStakedWealth;
			int wealthTransferOriginal = wealthTransfer;
			wealthTransfer = Misc.convertToPositive(wealthTransfer);
			boolean ignored = myStakedWealth >= WEALTH_FROM_EACH_PLAYER_PRESENTED && otherStakedWealth >= WEALTH_FROM_EACH_PLAYER_PRESENTED;
			boolean samePlayer = Misc.isSamePlayer(stakeWinner, stakeLoser);
			if (wealthTransfer >= WEALTH_TRANSFER_NOTIFICATION && !ignored) {
				long stakeWinnerAccountWealth = ItemAssistant.getAccountBankValueLong(stakeWinner);
				long stakeLoserAccountWealth = ItemAssistant.getAccountBankValueLong(stakeLoser);
				if (stakeWinnerAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED
				    && stakeLoserAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED) {
					ignored = true;
				}
				if (stakeWinnerAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED && wealthTransferOriginal < 0) {
					ignored = true;
				}
				if (stakeLoserAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED && wealthTransferOriginal > 0) {
					ignored = true;
				}
				if (myStakedWealth > otherStakedWealth) {
					ignored = true;
				}
				if (!ignored) {
					stakeWinner.setFlaggedForRwt(true);
					stakeLoser.setFlaggedForRwt(true);
					String rwtText = rwtSave + ", attacks: " + BannedData.getSourceName(stakeWinner) + ": " + winnerTotalAttacks + ", " + BannedData.getSourceName(stakeLoser) + ": " + loserTotalAttacks;
					if (!samePlayer) {
						if (PrivateMessagingPacket.getMinutesFriendsFor(stakeWinner, stakeLoser.getPlayerName()) < MINIMUM_MINUTES_FRIENDS && PrivateMessagingPacket.getMinutesFriendsFor(stakeLoser, stakeWinner.getPlayerName()) < MINIMUM_MINUTES_FRIENDS) {
							rwtAlert.add(rwtText + ", friends: " + PrivateMessagingPacket.getMinutesFriendsFor(stakeWinner, stakeLoser.getPlayerName()));
							RealWorldTradePoints.addSingleXfer(stakeWinner.addressUid, stakeWinner.addressIp, stakeWinner.getLowercaseName(), true);
							RealWorldTradePoints.addSingleXfer(stakeLoser.addressUid, stakeLoser.addressIp, stakeLoser.getLowercaseName(), true);
						}
					} else {
						itemTransferLogList.add(new ItemTransferLog(stakeWinner.getLowercaseName(), System.currentTimeMillis(), rwtText));
					}
				}
			}
		}
	}

	public static void pickUpItem(long playerWealthBeforePickUp, Player player, int itemId, int itemAmount, String originalOwnerName, int itemX, int itemY, int itemHeight, String originalOwnerIp,
			String originalOwnerUid, String itemFromDescription) {

		// It means that it is an item being picked up from an npc drop.
		if (originalOwnerName.isEmpty()) {
			return;
		}
		String playerName = player.getPlayerName();
		long worth = ServerConstants.getItemValue(itemId) * itemAmount;
		if (worth < 10000) {
			return;
		}
		String string = "picked up an item that belongs";

		if (System.currentTimeMillis() - player.timeInCombat <= 60000) {
			long time = (System.currentTimeMillis() - player.timeInCombat) / 1000;
			string = "picked up in combat " + time + " secs ago that belongs";
			if (Area.inDuelArena(player)) {
				string = "picked up in combat at duel " + time + " secs ago that belongs";
			}
		}
		String line = Misc.getDateAndTime() + " PICKUP: [" + playerName + "] " + string + " to [" + originalOwnerName + "] " + Misc.formatRunescapeStyle(worth);
		String rwt = Misc.getDateAndTime() + " PICKUP: [" + BannedData.getSourceName(player, playerWealthBeforePickUp) + "] " + string + " to [" + BannedData.getSourceName(originalOwnerName, originalOwnerIp, originalOwnerUid) + "] worth: " + Misc.formatRunescapeStyle(worth);
		String detailed = line + ", at: " + player.getX() + ", " + player.getY() + ", " + player.getHeight() + ", item: " + ItemAssistant.getItemName(itemId) + " x" + Misc.formatNumber(itemAmount) + ", wealth: " + Misc.formatNumber((ItemAssistant.getAccountBankValueLong(player) - worth)) + "description[" + itemFromDescription + "]";
		detailedItemPickUp.add(detailed);
		if (worth >= 150_000) {
			detailedItemPickUpExpensive.add(detailed);
		}

		if (worth >= 150_000) {
			pickupItemToFindTuqan.add("[" + Misc.getDateAndTime() + "] New entry-----");
			pickupItemToFindTuqan.add(playerName + ", " + originalOwnerName + ", " + Misc.formatRunescapeStyle((ItemAssistant.getAccountBankValueLong(player) - worth)));
			pickupItemToFindTuqan.add(line);
			pickupItemToFindTuqan.add(rwt);
			pickupItemToFindTuqan.add("Player: " + playerName + ", " + player.addressIp + ", " + player.addressUid);
			Player owner = Misc.getPlayerByName(originalOwnerName);
			if (owner != null) {
				pickupItemToFindTuqan.add("Owner online: " + owner.getPlayerName() + ", " + owner.addressIp + ", " + owner.addressUid);
			} else {
				pickupItemToFindTuqan.add("Owner offline: " + originalOwnerName);
			}
			pickupItemToFindTuqan.add("Original: " + originalOwnerIp + ", " + originalOwnerUid + ", " + itemFromDescription);
		}
		if (playerName.equalsIgnoreCase(originalOwnerName)) {
			return;
		}

		RealWorldTradePoints.addSingleXfer(player.addressUid, player.addressIp, player.getLowercaseName(), false);
		RealWorldTradePoints.addSingleXfer(originalOwnerUid, originalOwnerIp, originalOwnerName.toLowerCase(), false);
		YoutubePaid.addEntry(player.getPlayerName(), line);
		YoutubePaid.addEntry(originalOwnerName, line);
		data.add(line);

		// Real world trade notification.
		if (worth >= WEALTH_TRANSFER_NOTIFICATION) {
			boolean samePlayer = false;
			Player owner = Misc.getPlayerByName(originalOwnerName);
			if (owner != null) {
				if (Misc.isSamePlayer(player, owner)) {
					samePlayer = true;
				}
			} else {
				if (Misc.uidMatches(player.addressUid, originalOwnerUid) || player.addressIp.equals(originalOwnerIp) && !player.addressIp.isEmpty()) {
					samePlayer = true;
				}
			}
			if (playerWealthBeforePickUp >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED) {
				return;
			}
				player.setFlaggedForRwt(true);
				if (owner != null) {
					owner.setFlaggedForRwt(true);
				}
			String rwtText = rwt + ", " + itemX + ", " + itemY + ", " + itemHeight;
			if (rwtText.contains("picked up in combat")) {
				if (owner.getPlayerName().equals(player.lastPlayerKilled) && !Misc.timeElapsed(player.lastTimeKilledPlayer, Misc.getSecondsToMilliseconds(45))) {
					player.lootValueFromKill -= worth;
					// If you get 600k loot, you also get artefacts, player picks up artefacts, so it now becomes 596k for example. Then player picks up 600k loot, becomes -4k.
					if (player.lootValueFromKill >= -30_000) {
						return;
					}
				}
			}
			if (!samePlayer) {
				if (PrivateMessagingPacket.getMinutesFriendsFor(player, originalOwnerName) < MINIMUM_MINUTES_FRIENDS) {
					rwtAlert.add(rwtText + ", friends: " + PrivateMessagingPacket.getMinutesFriendsFor(player, originalOwnerName));
					RealWorldTradePoints.addSingleXfer(player.addressUid, player.addressIp, player.getLowercaseName(), true);
					RealWorldTradePoints.addSingleXfer(originalOwnerUid, originalOwnerIp, originalOwnerName.toLowerCase(), true);
				}
			} else {
				itemTransferLogList.add(new ItemTransferLog(player.getLowercaseName(), System.currentTimeMillis(), rwtText));
			}
		}
	}

	public static void logInPlayerChanged(Player player) {
		String name = player.getLowercaseName();
		for (int index = 0; index < itemTransferLogList.size(); index++) {
			ItemTransferLog instance = itemTransferLogList.get(index);
			if (instance.playerName.equals(name)) {
				rwtAlert.add("HACK TRANSFER: " + instance.rwtText);
				RealWorldTradePoints.addSingleXfer(player.addressUid, player.addressIp, player.getLowercaseName(), true);
				itemTransferLogList.remove(index);
				index--;
			}
		}
	}

	public static class RealWorldTradePoints {

		private static final String RWT_POINTS_LOCATION = "backup/logs/rwt/points.txt";

		public ArrayList<String> uid = new ArrayList<>();

		public ArrayList<String> ip = new ArrayList<>();

		public ArrayList<String> names = new ArrayList<>();

		private int totalXfers;

		private int flaggedXfers;

		public int getTotalXfers() {
			return totalXfers;
		}

		public void setTotalXfers(int value) {
			totalXfers = value;
		}

		public int getFlaggedXfers() {
			return flaggedXfers;
		}

		public void setFlaggedXfers(int value) {
			flaggedXfers = value;
		}

		public static List<RealWorldTradePoints> realWorldTradePointsLists = new ArrayList<RealWorldTradePoints>();

		public RealWorldTradePoints(ArrayList<String> uid, ArrayList<String> ip, ArrayList<String> names, int totalXfers, int flaggedXfers) {
			this.uid = uid;
			this.ip = ip;
			this.names = names;
			this.totalXfers = totalXfers;
			this.flaggedXfers = flaggedXfers;
		}

		public static void addSingleXfer(String uid, String ip, String name, boolean flagged) {
			for (int index = 0; index < realWorldTradePointsLists.size(); index++) {
				RealWorldTradePoints instance = realWorldTradePointsLists.get(index);
				boolean skip = false;
				for (int i = 0; i < instance.ip.size(); i++) {
					if (instance.ip.get(i).equals(ip)) {
						skip = true;
						break;
					}
				}
				if (skip) {
					if (flagged) {
						instance.setFlaggedXfers(instance.getFlaggedXfers() + 1);
					} else {
					instance.setTotalXfers(instance.getTotalXfers() + 1);
					}
					if (!instance.uid.contains(uid) && !uid.isEmpty()) {
						instance.uid.add(uid);
					}
					if (!instance.names.contains(name) && !name.isEmpty()) {
						instance.names.add(name);
					}
					break;
				}
				for (int i = 0; i < instance.uid.size(); i++) {
					if (Misc.uidMatches(uid, instance.uid.get(i))) {
						skip = true;
						break;
					}
				}
				if (skip) {
					if (flagged) {
						instance.setFlaggedXfers(instance.getFlaggedXfers() + 1);
					} else {
					instance.setTotalXfers(instance.getTotalXfers() + 1);
					}
					if (!instance.ip.contains(ip) && !ip.isEmpty()) {
						instance.ip.add(ip);
					}
					if (!instance.names.contains(name) && !name.isEmpty()) {
						instance.names.add(name);
					}
					break;
				}
			}
		}

		public static void loadRealWorldTradePoints() {
			ArrayList<String> line = FileUtility.readFile(RWT_POINTS_LOCATION);

			ArrayList<String> uid = new ArrayList<>();
			ArrayList<String> ip = new ArrayList<>();
			ArrayList<String> names = new ArrayList<>();
			int totalXfers = 0;
			int flaggedXfers = 0;
			for (int index = 0; index < line.size(); index++) {
				String text = line.get(index);
				if (text.startsWith("uid: ")) {
					text = text.substring(5);
					String[] parse = text.split(ServerConstants.TEXT_SEPERATOR);
					for (int i = 0; i < parse.length; i++) {
						
					}
				}
				else if (text.startsWith("ip: ")) {

				} else if (text.startsWith("names: ")) {

				} else if (text.startsWith("total: ")) {

				} else if (text.startsWith("flagged: ")) {

				}
			}
		}

		public static void saveRealWorldTradePoints() {

			ArrayList<String> line = new ArrayList<>();
			String finalLine = "";
			for (int index = 0; index < realWorldTradePointsLists.size(); index++) {
				String string = "";
				RealWorldTradePoints instance = realWorldTradePointsLists.get(index);
				for (int i = 0; i < instance.uid.size(); i++) {
					if (instance.uid.size() - 1 == i) {
						string = string + instance.uid.get(i);
					} else {
						string = string + instance.uid.get(i) + ServerConstants.TEXT_SEPERATOR;
					}
				}
				line.add("uid: " + string);

				string = "";
				for (int i = 0; i < instance.ip.size(); i++) {
					if (instance.ip.size() - 1 == i) {
						string = string + instance.ip.get(i);
					} else {
						string = string + instance.ip.get(i) + ServerConstants.TEXT_SEPERATOR;
					}
				}
				line.add("ip: " + string);

				string = "";
				for (int i = 0; i < instance.names.size(); i++) {
					if (instance.names.size() - 1 == i) {
						string = string + instance.names.get(i);
					} else {
						string = string + instance.names.get(i) + ServerConstants.TEXT_SEPERATOR;
					}
				}
				line.add("names: " + string);

				line.add("total: " + instance.getTotalXfers());
				line.add("flagged: " + instance.getFlaggedXfers());
			}
			FileUtility.deleteAllLines(RWT_POINTS_LOCATION);
			FileUtility.saveArrayContentsSilent(RWT_POINTS_LOCATION, line);
		}
	}
}
