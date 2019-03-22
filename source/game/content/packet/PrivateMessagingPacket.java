package game.content.packet;

import core.ServerConstants;
import game.content.clanchat.ClanChatHandler;
import game.content.staff.StaffActivity;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.Mute;
import network.packet.PacketType;
import utility.Misc;

/**
 * Private messaging, friends etc
 **/
public class PrivateMessagingPacket implements PacketType {

	public final int ADD_FRIEND = 188, SEND_PM = 126, REMOVE_FRIEND = 215, CHANGE_PM_STATUS = 95, REMOVE_IGNORE = 74, ADD_IGNORE = 133;

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		switch (packetType) {

			case ADD_FRIEND:
				long friendToAdd = player.getInStream().readQWord();
				addFriendPacket(player, friendToAdd);
				break;

			case SEND_PM:
				long sendMessageToFriendId = player.getInStream().readQWord();

				byte pmchatText[] = new byte[128];

				int pmchatTextSize = (byte) (packetSize - 8);

				if (pmchatTextSize < 0) {
					return;
				}
				player.getInStream().readBytes(pmchatText, pmchatTextSize, 0);

				if (pmchatTextSize > 80) {
					pmchatTextSize = 80;
					byte[] resizedText = new byte[80];
					System.arraycopy(pmchatText, 0, resizedText, 0, resizedText.length);
					pmchatText = resizedText;
				}
				if (Mute.isMuted(player)) {
					break;
				}
				StaffActivity.addStaffActivity(player);

				for (int i1 = 0; i1 < player.friends.length; i1++) {
					if (player.friends[i1][0] == sendMessageToFriendId) {
						boolean pmSent = false;

						for (int i2 = 1; i2 < ServerConstants.MAXIMUM_PLAYERS; i2++) {
							if (PlayerHandler.players[i2] != null && PlayerHandler.players[i2].isActive()
									&& Misc.playerNameToInt64(PlayerHandler.players[i2].getPlayerName()) == sendMessageToFriendId) {
								Player o = PlayerHandler.players[i2];
								if (o != null) {
									if (player.isModeratorRank() || PlayerHandler.players[i2].privateChat == ServerConstants.PRIVATE_ON || (
											PlayerHandler.players[i2].privateChat == ServerConstants.PRIVATE_FRIENDS && o.getPA().hasFriend(
													Misc.playerNameToInt64(player.getPlayerName())))) {
										o.getPA()
												.sendPM(player, player.getPlayerName(), Misc.playerNameToInt64(player.getPlayerName()), player.playerRights, pmchatText, pmchatTextSize,
										         trackPlayer);
										pmSent = true;

									}
								}
								break;
							}
						}
						if (!pmSent) {
							player.playerAssistant.sendMessage("That player is currently offline.");
							break;
						}
						break;
					}
				}
				break;

			case REMOVE_FRIEND:
				long friendToRemove = player.getInStream().readQWord();
				for (int i1 = 0; i1 < player.friends.length; i1++) {
					if (player.friends[i1][0] == friendToRemove) {
						player.friends[i1][0] = 0;
						player.getPA().sendFrame126("", i1 + 14101);
						player.getPA().sendFrame126("", i1 + 17551);
						break;
					}
				}
				PrivateMessagingPacket.setPlayerPrivateMessageStatusForWorld(player);
				ClanChatHandler.updateFriendsList(player, Misc.nameForLong(friendToRemove).replaceAll("_", " "), false);
				break;

			case REMOVE_IGNORE:
				long ignore = player.getInStream().readQWord();

				for (int i = 0; i < player.ignores.length; i++) {
					if (player.ignores[i] == ignore) {
						player.ignores[i] = 0;
						break;
					}
				}
				PrivateMessagingPacket.setPlayerPrivateMessageStatusForWorld(player);
				break;

			case CHANGE_PM_STATUS:
				player.publicChatMode = player.getInStream().readUnsignedByte();
				player.privateChat = player.getInStream().readUnsignedByte();
				player.tradeChatMode = player.getInStream().readUnsignedByte();
				/*For some reason, a player has it bugged where his char file became
				privateChat = 129
				publicChatMode = 146
				So he wowuld log in and instantly crash due to these variables being called inside an array with a size of 4 or so.
				*/
				if (player.publicChatMode > 3 || player.publicChatMode < 0) {
					player.publicChatMode = 3;
				}
				if (player.privateChat > 3 || player.privateChat < 0) {
					player.privateChat = 3;
				}
				if (player.tradeChatMode > 2 || player.tradeChatMode < 0) {
					player.tradeChatMode = 2;
				}
				setPlayerPrivateMessageStatusForWorld(player);
				break;

			case ADD_IGNORE:
				long ignoreAdd = player.getInStream().readQWord();
				addIgnorePacket(player, ignoreAdd);
				break;

		}

	}

	private void addIgnorePacket(Player player, long ignoreAdd) {

		for (int i1 = 0; i1 < player.ignores.length; i1++) {
			if (player.ignores[i1] != 0 && player.ignores[i1] == ignoreAdd) {
				player.playerAssistant.sendMessage(Misc.longToPlayerName(ignoreAdd) + " is already on your ignore list.");
				return;
			}
		}
		boolean ignored = false;
		for (int i = 0; i < player.ignores.length; i++) {
			if (player.ignores[i] == 0) {
				player.ignores[i] = ignoreAdd;
				ignored = true;
				break;
			}
		}
		if (!ignored) {
			player.getPA().sendMessage("Your ignore list is already full.");
			return;
		}
		PrivateMessagingPacket.setPlayerPrivateMessageStatusForWorld(player);
	}

	public static int getMinutesFriendsFor(Player player, String friendName) {
		long friendLong = Misc.stringToLong(friendName.toLowerCase());
		for (int i1 = 0; i1 < player.friends.length; i1++) {
			if (player.friends[i1][0] != 0) {
				if (player.friends[i1][0] == friendLong) {
					long time = (System.currentTimeMillis() - player.friends[i1][1]) / Misc.getMinutesToMilliseconds(1);
					if (time < 0) {
						return 0;
					}
					return (int) time;
				}
			}
		}
		return -1;
	}

	private void addFriendPacket(Player player, long friendToAdd) {

		int emptyIndexFound = -1;
		for (int i1 = 0; i1 < player.friends.length; i1++) {
			if (player.friends[i1][0] != 0) {
				if (player.friends[i1][0] == friendToAdd) {
					player.playerAssistant.sendMessage(Misc.longToPlayerName(friendToAdd) + " is already on your friends list.");
					return;
				}
			}
			else {
				emptyIndexFound = i1;
			}
		}
		if (emptyIndexFound == -1) {
			player.getPA().sendMessage("Your Friends list is full.");
			return;
		}
		player.friends[emptyIndexFound][0] = friendToAdd;
		player.friends[emptyIndexFound][1] = System.currentTimeMillis();
		player.getPA().updatePM(friendToAdd, false);
		ClanChatHandler.updateFriendsList(player, Misc.nameForLong(friendToAdd).replaceAll("_", " "), true);
		PrivateMessagingPacket.setPlayerPrivateMessageStatusForWorld(player);
	}

	public static void setPlayerPrivateMessageStatusForWorld(Player player) {

		switch (player.privateChat) {
			case ServerConstants.PRIVATE_ON:
				setPlayerPrivateMessageStatusForWorldAction(player, false);
				break;
			case ServerConstants.PRIVATE_FRIENDS:
				setPlayerPrivateMessageStatusForWorldAction(player, false);
				break;
			case ServerConstants.PRIVATE_OFF:
				setPlayerPrivateMessageStatusForWorldAction(player, false);
				break;

		}
	}

	public static void setPlayerPrivateMessageStatusForWorldAction(Player player, boolean skipIgnore) {
		long playerNameToLong = Misc.playerNameToInt64(player.getPlayerName());
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop == player) {
				continue;
			}
			if (loop.getPA().hasFriend(playerNameToLong)) {
				loop.getPA().updatePM(playerNameToLong, skipIgnore);
			}
		}

	}

	/**
	 * True if the master has the player in his ignore list.
	 */
	public static boolean isIgnored(Player master, Player player) {
		for (int i1 = 0; i1 < master.ignores.length; i1++) {
			if (master.ignores[i1] != 0 && master.ignores[i1] == Misc.playerNameToInt64(player.getPlayerName())) {
				return true;
			}
		}
		return false;
	}
}
