package game.content.interfaces;


import core.GameType;
import core.ServerConstants;
import game.content.highscores.HighscoresDaily;
import game.content.minigame.lottery.Lottery;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.PriceChecker;
import game.content.skilling.Skilling;
import game.content.worldevent.BloodKey;
import game.content.worldevent.Tournament;
import game.content.worldevent.WorldEvent;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import java.util.ArrayList;
import utility.Misc;

/**
 * Interface updating methods go here.
 *
 * @author MGT Madness, created on 12-01-2014.
 */
public class InterfaceAssistant {

	public static void updateSpecialAttackOrbPercentage(Player player, String percentage) {
		player.getPA().sendFrame126(percentage, 41_090);
	}

	public static void closeCombatOverlay(Player player) {
		player.getPA().sendMessage(":packet:closecombatbox");
	}

	public static void setSpriteLoadingPercentage(Player player, int interfaceId, int percentage) {
		player.getPA()
				.sendMessage(":packet:spriteloadingpercentage " + interfaceId + " " + percentage);
	}

	public static void setTextCountDownSecondsLeft(Player player, int interfaceId, int seconds) {
		player.getPA().sendMessage(":packet:textcountdown " + interfaceId + " " + seconds);
	}

	public static void setInventoryOverlayId(Player player, int inventoryOverlayId) {
		player.getPA().sendMessage(":packet:setinventoryoverlay " + inventoryOverlayId);
	}

	/**
	 * Launch the interface that shows over the chatbox that requests a number from the player.
	 *
	 * @param action The unique string to this action, the packet is received from the client in
	 *        BankXPacket class.
	 */
	public static void showAmountInterface(Player player, String action, String text) {
		player.setAmountInterface(action);
		player.getOutStream().createFrame(27);
		player.getPA().sendMessage(":packet:enteramounttext " + text);
	}

	public static void changeInterfaceSprite(Player player, int interfaceId, int newSpriteId) {
		player.getPA().sendMessage(":packet:interfaceedit: " + interfaceId + " " + newSpriteId);
	}

	/**
	 * Change the model id displayed on the interface.
	 */
	public static void changeInterfaceModel(Player player, int interfaceId, int newModelId,
			int modelZoom, int xRotate, int yRotate, int xOffset, int yOffset) {
		player.getPA().sendMessage(":packet:interfacemodeledit: " + interfaceId + " " + newModelId
				+ " " + modelZoom + " " + xRotate + " " + yRotate + " " + xOffset + " " + yOffset);
	}

	public static void sendOverlayTimer(Player player, int overlaySpriteId, int secondsDurationLeft) {
		if (secondsDurationLeft > 0) {
			player.getPA().sendMessage(":packet:startoverlaytimer " + overlaySpriteId + " " + secondsDurationLeft);
		}
	}

	public static void stopOverlayTimer(Player player, int overlaySpriteId) {
		player.getPA().sendMessage(":packet:stopoverlaytimer " + overlaySpriteId);
	}

	public static void stopAllOverlayTimers(Player player) {
		player.getPA().sendMessage(":packet:stopalloverlaytimers");
	}

	public static void setFixedScrollMax(Player player, int interfaceId, int scrollMax) {
		player.getPA().sendMessage(":packet:scrollmax " + interfaceId + " " + scrollMax);
	}


	public static void setFixedScrollMax(Player player, int interfaceId, double entries,
			int multiplier, int minimumScrollLength) {
		int scrollLength = (int) Misc.getDoubleRoundedUp(entries * multiplier);
		if (scrollLength < minimumScrollLength) {
			scrollLength = minimumScrollLength;
		}
		InterfaceAssistant.setFixedScrollMax(player, interfaceId, scrollLength);
	}

	public static void scrollUp(Player player) {
		player.getPA().sendMessage(":packet:scrollup");
	}

	public static void summoningOrbOn(Player player) {
		player.getPA().sendMessage(":packet:summoningorbon");
	}

	public static void summoningOrbOff(Player player) {
		player.getPA().sendMessage(":packet:summoningorboff");
	}

	public static void closeDialogueOnly(Player player) {
		player.hasDialogueOptionOpened = false;
		player.getPA().sendMessage(":packet:closedialogue");
	}

	public static void displayReward(Player player, ArrayList<?> list) {
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(6963);
		player.getOutStream().writeWord(list.size());
		for (int i = 0; i < list.size(); i++) {
			String[] args = ((String) list.get(i)).split(" ");
			int item = Integer.parseInt(args[0]);
			int amount = Integer.parseInt(args[1]);
			if (amount > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(amount);
			} else {
				player.getOutStream().writeByte(amount);
			}
			player.getOutStream().writeWordBigEndianA(item + 1);
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
		player.getPA().displayInterface(6960);
	}

	public static void showSkillingInterface(Player player, String instruction, int zoom, int itemId,
			int offset1, int offset2) {
		player.getPA().sendMessage(":packet:senditemchat 1746 " + offset1 + " " + offset2);
		player.getPA().sendFrame126(instruction, 8879);
		player.getPA().sendFrame126(ItemAssistant.getItemName(itemId), 2799);
		player.getPA().sendFrame246(1746, zoom, itemId);
		player.getPA().sendFrame164(4429);
	}


	/**
	 * Show Wilderness interface.
	 *
	 * @param player The associated player.
	 */
	public static void wildernessInterface(Player player) {
		if (!Area.inDangerousPvpAreaOrClanWars(player)) {
			return;
		}
		player.getPA().sendFrame126(
				Area.inSafePkFightZone(player) ? "@gr3@Safe" : "Level: " + player.getWildernessLevel(),
				player.useBottomRightWildInterface ? 24396 : 24391);
		player.getPA().walkableInterface(player.useBottomRightWildInterface ? 24395 : 24390);
	}

	/**
	 * Sign post at Entrana.
	 *
	 * @param player The associated player.
	 */
	public static void signPost(Player player) {
		player.getPA().displayInterface(13585);
		player.getPA().sendFrame126("@dre@Directions", 13589);
		player.getPA().sendFrame126("West: farming.", 13591);
		player.getPA().sendFrame126("North: mining and smithing.", 13592);
		player.getPA().sendFrame126("North east: runecrafting.", 13593);
		player.getPA().sendFrame126("South east: woodcutting, fishing, cooking", 13594);
		player.getPA().sendFrame126("and runecrafting.", 13595);
		player.getPA().sendFrame126("", 13596);
		player.getPA().sendFrame126("", 13597);
		player.getPA().sendFrame126("", 13598);
		player.getPA().sendFrame126("", 13599);
		player.getPA().sendFrame126("", 13600);
		player.getPA().sendFrame126("", 13601);
		player.getPA().sendFrame126("", 13602);
		player.getPA().sendFrame126("", 13603);
		player.getPA().sendFrame126("", 13604);
		player.getPA().sendFrame126("", 13605);
		player.getPA().sendFrame126("", 13606);
		player.getPA().sendFrame126("", 13607);
		player.getPA().sendFrame126("", 13608);
		player.getPA().sendFrame126("", 13609);
	}

	/**
	 * Inform the client to turn off quick prayer orb glow.
	 *
	 * @param player The associated player.
	 */
	public static void quickPrayersOff(Player player) {
		player.playerAssistant.sendMessage(":quickprayeroff:");
	}

	/**
	 * Inform the client to turn on quick prayer orb glow.
	 *
	 * @param player The associated player.
	 */
	public static void quickPrayersOn(Player player) {
		player.playerAssistant.sendMessage(":quickprayeron:");
	}

	/**
	 * Inform the client about the resting toggle.
	 *
	 * @param player The associated player.
	 * @param state The toggle of the resting.
	 */
	public static void informClientRestingState(Player player, String state) {
		if (state.equals("on")) {
			player.playerAssistant.sendMessage(":restingon:");
		} else if (state.equals("off")) {
			player.playerAssistant.sendMessage(":restingoff:");
		}
	}

	/**
	 * Update the combat level text on the combat tab interface.
	 *
	 * @param player The associated player.
	 */
	public static void updateCombatLevel(Player player) {
		if (GameType.isPreEoc() && player.getSummoning().getFamiliar() != null) {

			int lvl = (int) Math.floor(
					Skilling.getLevelForExperience(player.skillExperience[ServerConstants.SUMMONING])
							* 0.125);
			player.getPA().sendMessage("lvl:" + lvl);
			String combat = (player.getCombatLevel() - lvl) + "+" + lvl;
			player.getPA().sendFrame126("Combat Level: " + combat, 19000);
			player.getPA().sendFrame126("Combat Lvl: " + combat, 20246);
		} else {
			player.getPA().sendFrame126("Combat Level: " + player.getCombatLevel(), 19000);
			player.getPA().sendFrame126("Combat Lvl: " + player.getCombatLevel(), 20246);
		}
	}

	/**
	 * Show the tabs.
	 *
	 * @param player The associated player.
	 */
	public static void showTabs(Player player) {
		player.playerAssistant.sendMessage(":updatetabs:"); // Send information to the client to shown
																				// tabs.
		switch (player.spellBook) {
			case "ANCIENT":
				player.playerAssistant.setSidebarInterface(6, PlayerMiscContent.getAncientMagicksInterface(player));
				player.getPA().sendMessage(":packet:ancientSpellbook:");
				break;

			case "LUNAR":
				player.playerAssistant.setSidebarInterface(6, 29999);
				player.getPA().sendMessage(":packet:lunarSpellbook:");
				break;

			case "MODERN":
				player.playerAssistant.setSidebarInterface(6, 1151);
				player.getPA().sendMessage(":packet:regularSpellbook:");
				break;
		}
		player.playerAssistant.setSidebarInterface(5, GameType.isPreEoc() ? 35800 : 5608); // Normal prayer
																												// book
	}

	/**
	 * Update the split private chat interface.
	 *
	 * @param player The associated interface.
	 */
	public static void splitPrivateChat(Player player) {
		player.getPA().sendFrame36(502, player.splitChat ? 1 : 0, false);
		player.getPA().sendFrame36(287, player.splitChat ? 1 : 0, false);
		if (player.splitChat) {
			player.getPA().sendFrame126("Split Private Chat\\n(currently on)", 22185);
		} else {
			player.getPA().sendFrame126("Split Private Chat\\n(currently off)", 22185);
		}

	}

	/**
	 * Will clear these frames, starting from firstId to lastId
	 */
	public static void clearFrames(Player player, int firstId, int lastId) {
		for (int index = firstId; index <= lastId; index++) {
			if (player.getPA().interfaceText.containsKey(index)) {
				player.getPA().interfaceText.remove(index);
			}
		}
		player.getPA().sendMessage(":packet:clearframes " + firstId + " " + lastId);

	}

	public static void offCityTimer(Player player) {
		player.getPA().sendMessage(":packet:offcitytimer");

	}

	public static void turnOffAllFreezeOverlays(Player player) {
		InterfaceAssistant.stopOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_BIND);
		InterfaceAssistant.stopOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ENTANGLE);
		InterfaceAssistant.stopOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ICE_BARRAGE);
		InterfaceAssistant.stopOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ICE_BLITZ);
		InterfaceAssistant.stopOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ICE_BURST);
		InterfaceAssistant.stopOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ICE_RUSH);
		InterfaceAssistant.stopOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_SNARE);

	}

	/**
	 * @param returnString True to return the current active event.
	 *        <p>
	 *        False to update the quest interface with the current running events, event schedule and
	 *        time left till next event.
	 */
	public static String returnActiveEventOrUpdateEventInterface(Player player,
			boolean returnString) {
		int frameIndex = 0;
		if (!returnString) {
			player.getPA().sendFrame126("Events", 25003);
		}

		// Pk/Pvm events
		if (!WorldEvent.nextEvent.isEmpty() && !WorldEvent.getCurrentEvent().isEmpty()
				&& !WorldEvent.getCurrentEvent().contains(" skill")) {
			if (!returnString) {
				player.getPA().sendFrame126("@gr3@" + WorldEvent.nextEvent, 25008 + frameIndex);
			}
			frameIndex++;
			long minutesLeft = ((WorldEvent.timeEventEnd - System.currentTimeMillis()) / 1000) / 60;
			if (minutesLeft == 0) {
				minutesLeft = 1;
			}
			if (!returnString) {
				player.getPA().sendFrame126("@gr3@" + minutesLeft + " minutes left!",
						25008 + frameIndex);
			}
			frameIndex++;
			if (returnString) {
				return "Pk/Pvm wild";
			}
		} else {
			int shortestTournamentTime = Integer.MAX_VALUE;
			String timeLeft = "";
			for (int index = 0; index < Tournament.eventTimes.size(); index++) {
				if (Tournament.eventTimes.get(index).contains("Pk/Pvm wild")) {
					String parse[] = Tournament.eventTimes.get(index).split("-");
					String string = parse[0].substring(0, 2);
					int eventTime = Integer.parseInt(string);
					int value = Integer.parseInt(HighscoresDaily.getInstance().getTimeLeft(parse[0], "TECHNICAL"));
					if (value < shortestTournamentTime) {
						shortestTournamentTime = value;
						timeLeft = HighscoresDaily.getInstance().getTimeLeft(parse[0], "HIGHSCORES");
					}
				}
			}
			if (timeLeft.equals("Event ended!")) {
				timeLeft = "a minute!";
			}
			if (!returnString) {
				player.getPA().sendFrame126("Next Pk/Pvm event in " + timeLeft, 25008 + frameIndex);
			}
			frameIndex++;
		}

		if (!Misc.timeElapsed(Lottery.timePreDrawAnnounced, Misc.getMinutesToMilliseconds(15))) {
			player.getPA().sendFrame126("@gr3@The lottery is active! Join at ::shops", 25008 + frameIndex);
			frameIndex++;
		} else {
			long shortestTime = Long.MAX_VALUE;
			String timeShortest = "";
			for (int index = 0; index < Lottery.DRAW_TIME_ANNOUNCEMENT.length; index++) {
				long timeFound = Long.parseLong(HighscoresDaily.getInstance().getTimeLeft(Lottery.DRAW_TIME_ANNOUNCEMENT[index][0], "TECHNICAL"));
				if (timeFound < shortestTime) {
					shortestTime = timeFound;
					timeShortest = Lottery.DRAW_TIME_ANNOUNCEMENT[index][0];
				}
			}
			player.getPA().sendFrame126("Next lottery in " + HighscoresDaily.getInstance().getTimeLeft(timeShortest, "HIGHSCORES"), 25008 + frameIndex);
			frameIndex++;
		}

		boolean skillingEventAvailable = false;
		for (int index = 0; index < Tournament.eventTimes.size(); index++) {
			if (Tournament.eventTimes.get(index).contains("Skilling")) {
				skillingEventAvailable = true;
				break;
			}
		}
		if (skillingEventAvailable) {
			// Skilling events
			if (!WorldEvent.nextEvent.isEmpty() && !WorldEvent.getCurrentEvent().isEmpty()
					&& WorldEvent.getCurrentEvent().contains(" skill")) {
				if (!returnString) {
					player.getPA().sendFrame126("@gr3@" + WorldEvent.nextEvent, 25008 + frameIndex);
				}
				frameIndex++;
				long minutesLeft = ((WorldEvent.timeEventEnd - System.currentTimeMillis()) / 1000) / 60;
				if (minutesLeft == 0) {
					minutesLeft = 1;
				}
				if (!returnString) {
					player.getPA().sendFrame126("@gr3@" + minutesLeft + " minutes left!",
							25008 + frameIndex);
				}
				frameIndex++;
				if (returnString) {
					return WorldEvent.getCurrentEvent();
				}
			} else {
				int shortestTournamentTime = Integer.MAX_VALUE;
				String timeLeft = "";
				for (int index = 0; index < Tournament.eventTimes.size(); index++) {
					if (Tournament.eventTimes.get(index).contains("Skilling")) {
						String parse[] = Tournament.eventTimes.get(index).split("-");
						String string = parse[0].substring(0, 2);
						int eventTime = Integer.parseInt(string);
						int value = Integer.parseInt(HighscoresDaily.getInstance().getTimeLeft(parse[0], "TECHNICAL"));
						if (value < shortestTournamentTime) {
							shortestTournamentTime = value;
							timeLeft = HighscoresDaily.getInstance().getTimeLeft(parse[0], "HIGHSCORES");
						}
					}
				}
				if (timeLeft.equals("Event ended!")) {
					timeLeft = "a minute!";
				}
				if (!returnString) {
					player.getPA().sendFrame126("Next Skilling event in " + timeLeft,
							25008 + frameIndex);
				}
				frameIndex++;
			}
		}

		// Tournament
		boolean tournamentTextApplied = false;
		if (!Tournament.getTournamentStatus().isEmpty()) {
			long minutesSinceTournamentAnnounced =
					(System.currentTimeMillis() - Tournament.timeTournamentAnnounced) / 60000;
			String text = "";
			if (minutesSinceTournamentAnnounced <= (Tournament.TOURNAMENT_WILL_START_IN / 100)) {
				text = "The tournament will start soon, gather at ::tournament";
			} else if (minutesSinceTournamentAnnounced <= ((Tournament.TOURNAMENT_WILL_START_IN / 100)
					+ (Tournament.TOURNAMENT_STARTED_WAIT_TIME / 100))) {
				text = "The " + Tournament.eventType
						+ " tournament will start, gear up quickly at ::tournament";
			}
			if (!text.isEmpty()) {
				if (!returnString) {
					player.getPA().sendFrame126("@gr3@" + text, 25008 + frameIndex);
				}
				frameIndex++;
				tournamentTextApplied = true;
				if (returnString) {
					return "Tournament";
				}
			}
		}
		if (!tournamentTextApplied) {
			int shortestTournamentTime = Integer.MAX_VALUE;
			String timeLeft = "";
			for (int index = 0; index < Tournament.eventTimes.size(); index++) {
				if (Tournament.eventTimes.get(index).contains("Tournament")) {
					String parse[] = Tournament.eventTimes.get(index).split("-");
					String string = parse[0].substring(0, 2);
					int eventTime = Integer.parseInt(string);
					int value = Integer.parseInt(HighscoresDaily.getInstance().getTimeLeft(parse[0], "TECHNICAL"));
					if (value < shortestTournamentTime) {
						shortestTournamentTime = value;
						timeLeft = HighscoresDaily.getInstance().getTimeLeft(parse[0], "HIGHSCORES");
					}
				}
			}
			if (timeLeft.equals("Event ended!")) {
				timeLeft = "a minute! ::tournament";
			}
			if (!returnString) {
				player.getPA().sendFrame126("Next tournament in " + timeLeft, 25008 + frameIndex);
			}
			frameIndex++;
		}

		// Blood key
		if (BloodKey.tick >= 0 && BloodKey.keyType.equals("KEY")) {
			if (!returnString) {
				player.getPA().sendFrame126("@gr3@Blood key location is about to be announced!",
						25008 + frameIndex);
			}
			frameIndex++;
			if (returnString) {
				return "Blood key";
			}
		} else if (System.currentTimeMillis() - BloodKey.timeBloodKeySpawned <= Misc
				.getMinutesToMilliseconds(10)) {
			if (!returnString) {
				player.getPA().sendFrame126("@gr3@" + BloodKey.location, 25008 + frameIndex);
			}
			frameIndex++;
			if (returnString) {
				return "Blood key";
			}
		} else {
			int shortestTournamentTime = Integer.MAX_VALUE;
			String timeLeft = "";
			for (int index = 0; index < Tournament.eventTimes.size(); index++) {
				if (Tournament.eventTimes.get(index).contains("Blood key")) {
					String parse[] = Tournament.eventTimes.get(index).split("-");
					String string = parse[0].substring(0, 2);
					int eventTime = Integer.parseInt(string);
					int value = Integer.parseInt(HighscoresDaily.getInstance().getTimeLeft(parse[0], "TECHNICAL"));
					if (value < shortestTournamentTime) {
						shortestTournamentTime = value;
						timeLeft = HighscoresDaily.getInstance().getTimeLeft(parse[0], "HIGHSCORES");
					}
				}
			}
			if (timeLeft.equals("Event ended!")) {
				timeLeft = "a minute!";
			}
			if (!returnString) {
				player.getPA().sendFrame126("Next blood key in " + timeLeft, 25008 + frameIndex);
			}
			frameIndex++;
		}

		// Skotizo
		if (BloodKey.tick >= 0 && BloodKey.keyType.equals("BOSS")) {
			if (!returnString) {
				player.getPA().sendFrame126("@gr3@Skotizo location is about to be announced!",
						25008 + frameIndex);
			}
			frameIndex++;
			if (returnString) {
				return "Skotizo";
			}
		} else if (System.currentTimeMillis() - BloodKey.timeBossSpawned <= Misc
				.getMinutesToMilliseconds(10)) {
			if (!returnString) {
				player.getPA().sendFrame126("@gr3@" + BloodKey.location, 25008 + frameIndex);
			}
			frameIndex++;
			if (returnString) {
				return "Skotizo";
			}
		} else {
			int shortestTournamentTime = Integer.MAX_VALUE;
			String timeLeft = "";
			for (int index = 0; index < Tournament.eventTimes.size(); index++) {
				if (Tournament.eventTimes.get(index).contains("Skotiz")) {
					String parse[] = Tournament.eventTimes.get(index).split("-");
					String string = parse[0].substring(0, 2);
					int eventTime = Integer.parseInt(string);
					int value = Integer.parseInt(HighscoresDaily.getInstance().getTimeLeft(parse[0], "TECHNICAL"));
					if (value < shortestTournamentTime) {
						shortestTournamentTime = value;
						timeLeft = HighscoresDaily.getInstance().getTimeLeft(parse[0], "HIGHSCORES");
					}
				}
			}
			if (timeLeft.equals("Event ended!")) {
				timeLeft = "a minute!";
			}
			if (!returnString) {
				player.getPA().sendFrame126("Next Skotizo in " + timeLeft, 25008 + frameIndex);
			}
			frameIndex++;
		}

		// Daily highscores
		String dayType = "AM";
		if (Integer
				.parseInt(HighscoresDaily.getInstance().getTimeLeft("03:00 AM", "TECHNICAL")) > Integer.parseInt(HighscoresDaily.getInstance().getTimeLeft("03:00 PM", "TECHNICAL"))) {
			dayType = "PM";
		}
		String timeLeft = HighscoresDaily.getInstance().getTimeLeft("03:00 " + dayType, "HIGHSCORES");
		if (timeLeft.equals("Event ended!")) {
			timeLeft = "12h";
		}
		if (!returnString) {
			player.getPA().sendFrame126(HighscoresDaily.getInstance().currentDailyHighscores
					+ " daily highscores in " + timeLeft, 25008 + frameIndex);
		}
		frameIndex++;

		// Schedule
		if (!returnString) {
			player.getPA().sendFrame126("", 25008 + frameIndex);
			frameIndex++;
			player.getPA().sendFrame126("[Schedule]", 25008 + frameIndex);
			frameIndex++;
			for (int index = 0; index < Tournament.eventTimes.size(); index++) {
				String string = Tournament.eventTimes.get(index);
				player.getPA().sendFrame126(string, 25008 + frameIndex);
				frameIndex++;
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, (int) (frameIndex * 20.5));
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
		}
		return "";
	}

	/**
	 * If an interface is closed or player logs out, this method is called.
	 */
	public static void interfaceClosed(Player player) {
		PriceChecker.refundStoredItems(player);
		NpcDoubleItemsInterface.interfaceClosed(player);
	}

	public static void chatEffectOn(Player player) {
		player.getPA().sendFrame36(501, 1, false);
		player.getPA().sendFrame36(171, 0, false);
	}

	public static void chatEffectOff(Player player) {
		player.getPA().sendFrame36(501, 0, false);
		player.getPA().sendFrame36(171, 1, false);
	}

	public static void sendChatBoxMessageOfForcedChat(Player player, String playerName, String message, boolean quickChatIcon) {
		player.getPA().sendMessage(":packet:quickchat!#!" + playerName + "!#!" + quickChatIcon + "!#!" + message);
	}
}
