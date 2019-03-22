package game.content.commands;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.vsplayer.LootKey;
import game.content.donator.MysteryBox.MysteryBoxData;
import game.content.donator.Yell;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.donator.DonatorMainTab;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.YoutubeRank;
import game.content.staff.StaffManagement;
import game.content.tradingpost.TradingPost;
import game.content.tradingpost.TradingPostItems;
import game.content.worldevent.Tournament;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.punishment.Ban;
import game.player.punishment.Blacklist;
import game.player.punishment.DuelArenaBan;
import game.player.punishment.IpMute;
import game.player.punishment.RagBan;
import game.player.punishment.YellMute;
import java.io.File;
import java.util.ArrayList;
import network.connection.DonationManager;
import network.connection.DonationManager.OsrsPaymentLimit;
import network.sql.SQLConstants;
import network.sql.SQLNetwork;
import network.sql.query.impl.DoubleParameter;
import network.sql.query.impl.StringParameter;
import tools.EconomyScan;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import utility.EmailSystem;
import utility.Misc;
import utility.OsBotCommunication;
import utility.WebsiteLogInDetails;

public class ModeratorCommand {
	public static boolean moderatorCommand(Player player, String command) {
		if (player.isHeadModeratorRank()) {
			if (command.startsWith("signup")) {
				if (command.contains("off")) {
					StaffManagement.hoursPlayTimeRequired = -1;
					StaffManagement.signUpThreadId = -1;
					StaffManagement.hoursActivePlayedTimeInTheLastWeek = -1;
					player.getPA().sendMessage("Sign ups turned off.");
				} else {
					try {
						String[] parse = command.split(" ");
						StaffManagement.hoursPlayTimeRequired = Integer.parseInt(parse[2]);
						StaffManagement.signUpThreadId = Integer.parseInt(parse[1]);
						StaffManagement.hoursActivePlayedTimeInTheLastWeek = Integer.parseInt(parse[3]);
						player.getPA().sendMessage(
								"Sign up message set to thread " + StaffManagement.signUpThreadId + " and hours required to " + StaffManagement.hoursPlayTimeRequired + " and");
						player.getPA().sendMessage("last 7 days active hours to " + StaffManagement.hoursActivePlayedTimeInTheLastWeek + ".");
					} catch (Exception e) {
						player.getPA().sendMessage("Use as ::signup threadId hoursRequired last7DaysHoursRequired");
					}
				}
				return true;
			}
		}
		if (command.startsWith("wealth")) {
			String target = command.substring(7);

			try {
				String ip = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + target + ".txt", "lastSavedIpAddress", 3);
				String uid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + target + ".txt", "addressUid", 3);
				if (!Blacklist.isBlacklisted(target, ip, "", "", uid, false)) {
					player.getPA().sendMessage(target + " is not banned, cannot check wealth.");
					return true;
				}
				EconomyScan.doNotPrint = true;
				EconomyScan.scanAccount(new File(ServerConstants.getCharacterLocationWithoutLastSlash() + "/" + target + ".txt"));
				player.getPA().sendMessage(target + " account wealth is: " + Misc.formatRunescapeStyle(EconomyScan.accountWealth));
			} catch (Exception e) {
				player.getPA().sendMessage("Use as ::wealth mgt madness (it only works on banned players)");
			}
			return true;
		} else if (command.startsWith("unbanrefund")) {
			String target = command.substring(12);

			try {
				String ip = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + target + ".txt", "lastSavedIpAddress", 3);
				String uid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + target + ".txt", "addressUid", 3);
				if (!Blacklist.isBlacklisted(target, "", "", "", "", false)) {
					player.getPA().sendMessage(target + " is not banned.");
					return true;
				}
				File account = new File(ServerConstants.getCharacterLocationWithoutLastSlash() + "/" + target + ".txt");
				EconomyScan.doNotPrint = true;
				EconomyScan.scanAccount(account);
				int maximumKeptLimit = 1000000;
				if (EconomyScan.accountWealth > maximumKeptLimit) {
					NormalCommand.deleteItemsFromAccount(account, true);
					if (NormalCommand.accountCleanDeletionFailed) {
						player.getPA().sendMessage("Failed to clean account of: " + target + ", try again.");
						return true;
					}
					for (int index = 0; index < DiceSystem.depositVault.size(); index++) {
						String parse[] = DiceSystem.depositVault.get(index).split("-");
						if (parse[0].equalsIgnoreCase(target)) {
							DiceSystem.depositVault.remove(index);
							break;
						}
					}
					for (int index = 0; index < TradingPost.tradingPostData.size(); index++) {
						TradingPost data = TradingPost.tradingPostData.get(index);
						if (data.getName().equalsIgnoreCase(target)) {
							TradingPost.tradingPostData.remove(index);
							index--;
						}
					}
					for (int index = 0; index < TradingPostItems.tradingPostItemsData.size(); index++) {
						TradingPostItems data = TradingPostItems.tradingPostItemsData.get(index);
						if (data.getName().equalsIgnoreCase(target)) {
							TradingPostItems.tradingPostItemsData.remove(index);
							index--;
						}
					}
					long refundAmount = EconomyScan.accountWealth;
					if (refundAmount > maximumKeptLimit) {
						refundAmount = maximumKeptLimit;
					}
					ItemAssistant.addItemReward(null, target, ServerConstants.getMainCurrencyId(), (int) refundAmount, false, -1);
					player.getPA().sendMessage(
							target + " bank is cleared and refunded " + Misc.formatRunescapeStyle(refundAmount) + " blood money, his bank was " + Misc.formatRunescapeStyle(
									EconomyScan.accountWealth) + ".");
					DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL,
					                           player.getPlayerName() + " has unbanned '" + Misc.capitalize(target) + "' and refunded him " + Misc.formatRunescapeStyle(
							                           refundAmount) + ", his bank was " + Misc.formatRunescapeStyle(EconomyScan.accountWealth) + ".");
				} else {
					player.getPA().sendMessage(target + " has been unbanned, his bank is " + Misc.formatRunescapeStyle(EconomyScan.accountWealth) + ".");
					DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL,
					                           player.getPlayerName() + " has unbanned '" + Misc.capitalize(target) + "', his bank is " + Misc.formatRunescapeStyle(
							                           EconomyScan.accountWealth) + ".");
				}
				DonationManager.setOsrsInInventory(Misc.roundDoubleToNearestOneDecimalPlace(DonationManager.getOsrsInInventory() + DonationManager.OSRS_PAYMENT_FOR_UNBAN));
				DonationManager.osrsToday += DonationManager.OSRS_PAYMENT_FOR_UNBAN;
				SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_OSRS_HISTORY) + " (time, entity, action, total_expected) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, "Mod"), new StringParameter(3, player.getPlayerName() + " has unbanned " + Misc.capitalize(target) + " for 15m"), new DoubleParameter(4, DonationManager.getOsrsInInventory()));
				double profit = (double) DonationManager.OSRS_PAYMENT_FOR_UNBAN * DonationManager.OSRS_DONATION_MULTIPLIER;
				profit = (profit * (DonationManager.PERCENTAGE_PROFIT_PER_MIL / 100.0));
				profit = Misc.roundDoubleToNearestTwoDecimalPlaces(profit);
				DonationManager.totalPaymentsToday += profit;

				Ban.unBan(player, "unban " + target, true, true);
				if (!DonationManager.botCheckRunning) {
					DonationManager.botCheckRunning = true;
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}

						@Override
						public void stop() {
							OsBotCommunication.addTextInDirectoryRandomized(OsBotCommunication.BOT_FILE_LOCATION, "rsn=blank#" + System.currentTimeMillis());
							DonationManager.botCheckRunning = false;
						}
					}, ServerConfiguration.DEBUG_MODE ? 10 : 50);
				}
			} catch (Exception e) {
				player.getPA().sendMessage("Use as ::wealth mgt madness (it only works on banned players)");
			}
			return true;
		} else if (command.startsWith("gambleban")) {
			DuelArenaBan.duelBan(player, command);
			return true;
		}
		if (command.startsWith("ungambleban")) {
			try {
				String target = command.substring(12);
				target = Misc.capitalize(target);
				boolean removed = false;
				for (int index = 0; index < DuelArenaBan.duelBan.size(); index++) {
					String[] parse = DuelArenaBan.duelBan.get(index).split(ServerConstants.TEXT_SEPERATOR);
					String nameParse = parse[0];
					long expiryParse = Long.parseLong(parse[3]);
					long hours = (expiryParse - System.currentTimeMillis()) / Misc.getHoursToMilliseconds(1);
					if (target.equals(nameParse)) {
						// Prevent a moderator from ungambling banning a player who banned himself.
						if (hours <= 168) {
							player.getPA().sendMessage("You cannot remove a player's self ban: " + target + ", has x" + hours + " hours left.");
							continue;
						}
						DuelArenaBan.duelBan.remove(index);
						player.getPA().sendMessage(nameParse + " has been unbanned from gambling.");
						index--;
						removed = true;
					}
				}
				if (removed) {
					DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has un-gamble banned '" + target + "'.");
				} else {
					player.getPA().sendMessage("This player is not gamble banned '" + target + "'");
				}
			} catch (Exception e) {
				player.getPA().sendMessage("Use as ::unduelban ronald");
			}
			return true;
		} else if (command.equals("toggleyell")) {
			Yell.toggleYellSystem(player);
			return true;
		} else if (command.startsWith("kick")) {
			AdministratorCommand.kick(player, command);
			return true;
		} else if (command.startsWith("undiceban")) {
			if (!ClanChatHandler.inDiceCc(player, false, false)) {
				player.getPA().sendMessage("Please enter 'Dice' cc before unbanning.");
				return true;
			}
			try {
				boolean unbanned = false;
				String target = command.substring(10);
				for (int index = 0; index < ClanChatHandler.clans[player.getClanId()].banned.size(); index++) {
					String bannedName = ClanChatHandler.clans[player.getClanId()].banned.get(index);
					if (target.equalsIgnoreCase(bannedName)) {
						ClanChatHandler.clans[player.getClanId()].banned.remove(index);
						ClanChatHandler.clans[player.getClanId()].updated = true;
						DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has undice banned '" + bannedName + "'");
						player.getPA().sendMessage("You have un-dice banned '" + bannedName + "'.");
						unbanned = true;
						break;
					}
				}
				if (!unbanned) {
					player.getPA().sendMessage("Player is not dice banned to begin with: '" + target + "'.");
				}
			} catch (Exception e) {
				player.getPA().sendMessage("Use as ::undiceban subarashii");
			}
			return true;
		} else if (command.startsWith("unipmute")) {
			IpMute.unIpMute(player, command);
			return true;
		} else if (command.startsWith("yellmute")) {
			YellMute.yellMute(player, command);
			return true;
		} else if (command.startsWith("transfervault")) {
			DiceSystem.transferVault(player, command);
			return true;
		} else if (command.equals("vault")) {
			int frameIndex = 0;
			player.getPA().sendFrame126("Deposit vaults", 25003);
			for (int index = 0; index < DiceSystem.depositVault.size(); index++) {
				String parse[] = DiceSystem.depositVault.get(index).split("-");
				player.getPA().sendFrame126(parse[0] + ": " + Misc.formatRunescapeStyle(Integer.parseInt(parse[1])), 25008 + frameIndex);
				frameIndex++;
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			return true;
		} else if (command.startsWith("jail")) {
			AdministratorCommand.jail(player, command);
			return true;
		} else if (command.startsWith("unjail")) {
			AdministratorCommand.unJail(player, command, false);
			return true;
		} else if (command.startsWith("guest")) {
			AdministratorCommand.guest(player, command);
			return true;
		} else if (command.startsWith("giveyt")) {
			YoutubeRank.giveYoutuber(player, command);
			return true;
		} else if (command.startsWith("unragban")) {
			RagBan.unRagBan(player, command);
			return true;
		} else if (command.startsWith("tournamentkick")) {
			Tournament.tournamentKick(player, command);
			return true;
		} else if (command.startsWith("07donatedamount")) {
			try {
				String parse[] = command.split(" ");
				double amount = Double.parseDouble(parse[1]);
				if (amount > WebsiteLogInDetails.OSRS_DONATION_LIMIT || amount <= 0) {
					return true;
				}
				if (!OsrsPaymentLimit.canAcceptOsrsPayment(player, (int) Misc.getDoubleRoundedUp(amount))) {
					return true;
				}
				String name = command.replace("07donatedamount", "");
				name = name.replace(" " + parse[1] + " ", "");
				double usdAmount = amount;
				usdAmount *= DonationManager.OSRS_DONATION_MULTIPLIER;
				double profit = (usdAmount * (DonationManager.PERCENTAGE_PROFIT_PER_MIL / 100.0));
				profit = Misc.roundDoubleToNearestTwoDecimalPlaces(profit);
				DonationManager.totalPaymentsToday += profit;
				int tokens = DonationManager.getTokensAmountForUsd(usdAmount);
				boolean online = false;
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
					Player loop = PlayerHandler.players[i];
					if (loop == null) {
						continue;
					}
					if (loop.getPlayerName().equalsIgnoreCase(name)) {
						online = true;
						if (ItemAssistant.addItem(loop, 7478, tokens)) {
							OsrsPaymentLimit.osrsPaymentLimitList.add(new OsrsPaymentLimit((int) Misc.getDoubleRoundedUp(amount), System.currentTimeMillis()));
							DonatorMainTab.paymentClaimedInUsd(loop, usdAmount);
							SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_PAYMENT_LATEST) + " (time, ign, usd, type) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, loop.getPlayerName()), new DoubleParameter(3, profit), new StringParameter(4, "OSRS"));
							loop.getPA().sendMessage(
									ServerConstants.BLUE_COL + "You receive x" + Misc.formatNumber(tokens) + " Donator tokens for donating x" + amount + "m 07 which is " + Misc.roundDoubleToNearestTwoDecimalPlaces(usdAmount) + "$");
							loop.getPA().sendMessage(ServerConstants.BLUE_COL + "Thank you so much for donating which will keep " + ServerConstants.getServerName() + " growing!");
							loop.getPA().sendMessage(ServerConstants.BLUE_COL + "Spend these at the Donator shop to earn a Donator rank or Sell the tokens");
							loop.getPA().sendMessage(
									ServerConstants.BLUE_COL + "to other players for " + TradingPost.getAverageTokenPrice() + " " + ServerConstants.getMainCurrencyName()
									                                                                                                               .toLowerCase()
									+ " each at the Donator npc trading post.");
							player.getPA().sendMessage(tokens + " Donator tokens awarded to: " + loop.getPlayerName() + " for donating: " + amount + "m.");
							int reward = -1;
							double rewardAmount = 1;
							int extraMills = 0;
							if (!DonationManager.botCheckRunning) {
								DonationManager.botCheckRunning = true;
								CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
									@Override
									public void execute(CycleEventContainer container) {
										container.stop();
									}

									@Override
									public void stop() {
										OsBotCommunication.addTextInDirectoryRandomized(OsBotCommunication.BOT_FILE_LOCATION, "rsn=blank#" + System.currentTimeMillis());
										DonationManager.botCheckRunning = false;
									}
								}, ServerConfiguration.DEBUG_MODE ? 10 : 50);
							}

							if (amount >= 80) {
								reward = MysteryBoxData.LEGENDARY_MYSTERY_BOX.getItemId();
								rewardAmount = (double) amount / 80.0;
								rewardAmount = Misc.getDoubleRoundedDown(rewardAmount);
								extraMills = (int) Misc.getDoubleRoundedUp(amount) - (80 * (int) rewardAmount);
							}
							else if (amount >= 40) {
								reward = MysteryBoxData.SUPER_MYSTERY_BOX.getItemId();
								extraMills = (int) Misc.getDoubleRoundedUp(amount) - 40;
							}
							else if (amount >= 20) {
								reward = MysteryBoxData.MYSTERY_BOX.getItemId();
								extraMills = (int) Misc.getDoubleRoundedUp(amount) - 20;
							}
							else {
								extraMills = (int) Misc.getDoubleRoundedUp(amount);
							}
							if (reward > 0) {
								player.getPA().sendMessage(ServerConstants.GREEN_COL_PLAIN + "You are rewarded x" + (int) rewardAmount + " " + ItemAssistant.getItemName(reward) + " for being a trustworthy Moderator!");
								ItemAssistant.addItemReward(null, player.getPlayerName(), reward, (int) rewardAmount, false, -1);
							}
							if (extraMills > 0) {
								rewardAmount = extraMills * 1000;
								player.getPA().sendMessage(ServerConstants.GREEN_COL_PLAIN + "You are rewarded x" + (int) rewardAmount + " " + ItemAssistant.getItemName(13307) + ".");
								ItemAssistant.addItemReward(null, player.getPlayerName(), 13307, (int) rewardAmount, false, -1);
							}
							DonationManager.osrsReceivedThisServerSession += amount;
							DonationManager.setOsrsInInventory(Misc.roundDoubleToNearestOneDecimalPlace(DonationManager.getOsrsInInventory() + Misc.getDoubleRoundedUp(amount)));
							DonationManager.osrsToday += amount;
							SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_OSRS_HISTORY) + " (time, entity, action, total_expected) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, "Mod"), new StringParameter(3, player.getPlayerName() + " has taken " + amount + "m osrs from " + loop.getPlayerName()), new DoubleParameter(4, DonationManager.getOsrsInInventory()));
							EconomyScan.donatorTokensGainedThisSession += amount;
							EconomyScan.donatorTokensGainedThisHour += amount;

							// Email
							ArrayList<String> content = new ArrayList<String>();
							content.add("Moderator: " + player.getPlayerName());
							content.add("Moderator ip: " + player.addressIp);
							content.add("Moderator uid: " + player.addressUid);
							content.add("Donator: " + loop.getPlayerName());
							content.add("Donator ip: " + loop.addressIp);
							content.add("Donator uid: " + loop.addressUid);
							content.add("Donated: " + amount + "m for " + tokens + " Donator tokens");
							EmailSystem.addPendingEmail("Osrs: " + DonationManager.getOsrsInInventory() + "m, payment by Mod: " + amount + "m", content, "mgtdt@yahoo.com");

						} else {
							player.getPA().sendMessage("His inventory is too full.");
							loop.getPA().sendMessage("Get an inventory space to receive your donation!");
						}
						break;
					}
				}
				if (!online) {
					player.getPA().sendMessage(name + " is not online.");
				}

				return true;
			} catch (Exception e) {
				player.getPA().sendMessage("Use as ::07donatedamount 50 mgt madness, which means the player donated 50m 07 gold.");
				return true;
			}
		}
		switch (command) {
			case "lootkeytest":
				LootKey.displayKeyLoot(player);
				return true;
			case "checklimit":
			case "checkcap":
			case "checkdonationlimit":
			case "checkdonationcap":
				if (player.isAdministratorRank()) {
					if (DonationManager.osrsReceivedThisServerSession == WebsiteLogInDetails.OSRS_DONATION_LIMIT) {
						player.getPA().sendMessage("<col=bc14cc>The daily limit of " + WebsiteLogInDetails.OSRS_DONATION_LIMIT + "m for OSRS donations has been reached.");
					player.getPA().sendMessage("<col=bc14cc>Please wait until tomorrow for it to reset.");
					return true;
				}
				else {
						int cap = WebsiteLogInDetails.OSRS_DONATION_LIMIT - DonationManager.osrsReceivedThisServerSession;
						player.getPA().sendMessage("<col=bc14cc>Another " + cap + "m in donations can be taken before the daily cap of " + WebsiteLogInDetails.OSRS_DONATION_LIMIT + "m is reached.");
				}
				}
				return true;

		}
		return false;
	}
}
