package game.content.packet;

import core.GameType;
import game.bot.BotCommunication;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.RandomEvent;
import game.content.staff.StaffActivity;
import game.player.Area;
import game.player.Player;
import game.player.punishment.Mute;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Chat
 **/
public class ChatPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int value1 = player.getInStream().readUnsignedByteS();
		int value2 = player.getInStream().readUnsignedByteS();
		byte byte1 = (byte) (player.packetSize - 2); // Text size.
		player.setChatTextEffects(value1);
		player.setChatTextColor(value2);
		player.setChatTextSize(byte1);
		player.inStream.readBytes_reverseA(player.getChatText(), player.getChatTextSize(), 0);
		String text = Misc.textUnpack(player.getChatText(), packetSize - 2);
		if (player.isModeratorRank() || player.isSupportRank()) {
			if (Misc.checkForOffensive(text)) {
				player.getPA().sendMessage("Your message was not sent due to flaming, be a nice guy.");
				return;
			}
		}
		RandomEvent.isNpcRandomEventChatPacketSent(player, text);

		if (Mute.isMuted(player)) {
			return;
		}
		String scamCurrencyBegin = GameType.isOsrsPvp() ? "blood" : "coin";
		if (text.toLowerCase().contains("img=") || text.toLowerCase().contains("col=")) {
			return;
		}

		// Prevent "i am currently risking 225,600 blood money." Scam.
		else if (text.toLowerCase().contains(scamCurrencyBegin) && text.toLowerCase().contains("am") && text.contains("risk")) {
			return;
		}
		// Prevent "i'm currently risking xxx blood money.' Scam.
		else if (text.toLowerCase().contains(scamCurrencyBegin) && text.toLowerCase().contains("risk") && text.contains("i") && text.toLowerCase().contains("'")
		         && text.toLowerCase().contains("m")) {
			return;
		} else if (Misc.containsPassword(player.playerPass, text)) {
			return;
		}
		if (trackPlayer) {
			PacketHandler.chatAndPmLog.add(player.getPlayerName() + " at " + Misc.getDateAndTime());
			PacketHandler.chatAndPmLog.add("Typed in chat: " + text);
		}
		if (Area.inDiceZone(player)) {
			DiceSystem.diceZoneChatLog.add(Misc.getDateAndTimeLog() + player.getPlayerName() + ": " + text);
		}

		if (player.secondsBeenOnline < 30) {
			player.getPA().sendMessage("You cannot talk for 30 seconds after joining.");
			return;
		}
		if (PlayerMiscContent.isNewPlayerSpamming(player, text)) {
			return;
		}
		if (text.equalsIgnoreCase(player.autoTypeText)) {
			if (!Area.inDiceZone(player) && !Area.inMarketArea(player)) {
				player.getPA().sendMessage("Message not sent, you cannot auto type outside the Market and Dice zone area.");
				return;
			}
		}
		/*
		if (text.equals(player.lastTextSent))
		{
			if (!Area.inMarketArea(player))
			{
				// Whole edge area that is populated with players.
				if (Area.isWithInArea(player, 3075, 3100, 3485, 3520))
				{
					for (int index = 0; index < ServerConstants.flaggedTradeWords.length; index++)
					{
						if (text.toLowerCase().contains(ServerConstants.flaggedTradeWords[index]))
						{
							player.getDH().sendStatement("Please use ::market for buying/selling items.Your message was not sent.");
							return;
						}
					}
				}
			}
		}
		player.lastTextSent = text;
		*/
		player.autoTypeText = "";
		StaffActivity.addStaffActivity(player);
		BotCommunication.playerToBot(player, text);
		player.setChatTextUpdateRequired(true);
		player.rwtChat.add("[" + Misc.getDateAndTime() + "] CHAT: " + text);

	}
}
