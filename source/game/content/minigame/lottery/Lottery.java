package game.content.minigame.lottery;

import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.Announcement;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.util.ArrayList;
import utility.FileUtility;
import utility.Misc;

/**
 * Lottery system.
 *
 * @author MGT Madness, created on 23-09-2017.
 */
public class Lottery {

	public static int lotteryNpcIndex;

	public static int getLotteryTicketCost() {
		return GameType.isOsrsEco() ? 2000000 : 5000;
	}

	/**
	 * 15 minutes before draw time.
	 */
	public final static String[][] DRAW_TIME_ANNOUNCEMENT = {
			//@formatter:off
			{"08:30 PM", "08:45 PM"},
			{"02:40 AM", "02:55 AM"},
			//@formatter:on
	};

	/**
	 * The amount of money that is taxed from the lottery.
	 */
	public final static int COMMISION_PERCENTAGE = 15;

	/**
	 * Location of where the total amount of tickets purchased is saved at.
	 */
	private final static String TOTAL_TICKETS_FILE = "backup/logs/lottery/total_tickets.txt";

	/**
	 * Location of where the lottery entries for each player is saved at.
	 */
	private final static String LOTTERY_ENTRIES_FILE = "backup/logs/lottery/entries.txt";


	/**
	 * The total amount of tickets purchased from the lottery, this is also used to calculate the total pot.
	 */
	private static int totalTicketsPurchased;

	/**
	 * Store time of when pre-draw announcement has started.
	 */
	public static long timePreDrawAnnounced;

	/**
	 * Asking Durial what the current pot is at.
	 */
	public static void currentPotAtDialogueOption(Player player) {

		Npc npc = NpcHandler.npcs[lotteryNpcIndex];
		npc.forceChat("The pot is currently at " + getTotalPotString() + "!");
		player.getDH().sendNpcChat("The current lottery pot is at " + getTotalPotString() + "!", "You have a " + getWinningPercentage(player.getPlayerName()) + "% chance to win.",
		                           FacialAnimation.HAPPY.getAnimationId());
		player.nextDialogue = 596; // Bring back to lottery option menu.
	}

	/**
	 * @return The total lottery pot.
	 */
	private static String getTotalPotString() {
		return Misc.formatRunescapeStyle(getTotalPotNumber()) + " " + ServerConstants.getMainCurrencyName();
	}

	private static int getTotalPotNumber() {
		int totalPotWorth = (totalTicketsPurchased * getLotteryTicketCost());
		double commission = 100 - COMMISION_PERCENTAGE;
		commission /= 100.0;
		return (int) (totalPotWorth * commission);
	}

	/**
	 * @return The winning percentage of the player.
	 */
	private static double getWinningPercentage(String playerName) {
		LotteryDatabase data = LotteryDatabase.getPlayerLotteryInstance(playerName);
		if (data == null) {
			return 0;
		}
		return Misc.roundDoubleToNearestTwoDecimalPlaces(((double) data.getTicketsPurchased() / (double) totalTicketsPurchased) * 100.0);
	}

	/**
	 * Asking Durial what my winning percentage is.
	 */
	public static void percentageOfWinningDialogueOption(Player player) {
		player.getDH().sendNpcChat("You have a " + getWinningPercentage(player.getPlayerName()) + "% chance to win.", FacialAnimation.HAPPY.getAnimationId());
		player.nextDialogue = 596; // Bring back to lottery option menu.
	}

	/**
	 * Asking Durial that i want to buy tickets/
	 */
	public static void buyLotteryTicketsDialogueOption(Player player) {
		InterfaceAssistant.closeDialogueOnly(player);
		InterfaceAssistant
				.showAmountInterface(player, "BUY LOTTERY TICKETS", "Enter the amount of tickets you will buy for " + Misc.formatNumber(getLotteryTicketCost()) + " each");
	}

	/**
	 * Receive the x amount tickets wanted to be bought by the player.
	 */
	public static boolean receiveLotteryBuyAmount(Player player, String action, int xAmount) {
		if (!action.equals("BUY LOTTERY TICKETS")) {
			return false;
		}
		if (player.isAdministratorRank() && !ServerConfiguration.DEBUG_MODE) {
			player.getPA().sendMessage("" + ServerConstants.getServerName() + " is not on debug mode.");
			player.getPA().closeInterfaces(true);
			return true;
		}
		int currentCurrencyAmount = ItemAssistant.getItemAmount(player, ServerConstants.getMainCurrencyId());
		int maxAmount = Integer.MAX_VALUE / getLotteryTicketCost();
		if (xAmount > maxAmount) {
			xAmount = maxAmount;
		}
		if (xAmount * getLotteryTicketCost() > currentCurrencyAmount) {
			xAmount = currentCurrencyAmount / getLotteryTicketCost();
		}
		if (xAmount == 0) {
			player.getPA().closeInterfaces(true);
			player.getPA()
			      .sendMessage("You need at least " + Misc.formatNumber(getLotteryTicketCost()) + " " + ServerConstants.getMainCurrencyName() + " to buy a lottery ticket.");
			return true;
		}
		LotteryDatabase data = LotteryDatabase.getPlayerLotteryInstance(player.getPlayerName());
		if (data == null) {
			LotteryDatabase.lotteryDatabase.add(new LotteryDatabase(player.getPlayerName(), xAmount));
		} else {
			data.setTickestPurchased(data.getTicketsPurchased() + xAmount);
		}
		totalTicketsPurchased += xAmount;
		ItemAssistant.deleteItemFromInventory(player, ServerConstants.getMainCurrencyId(), xAmount * getLotteryTicketCost());
		player.getDH().sendStatement(
				"You have purchased x" + Misc.formatNumber(xAmount) + " lottery ticket" + Misc.getPluralS(xAmount) + " for " + Misc.formatNumber(xAmount * getLotteryTicketCost())
				+ " " + ServerConstants.getMainCurrencyName() + ".");
		Npc npc = NpcHandler.npcs[lotteryNpcIndex];
		npc.forceChat("The pot is currently at " + getTotalPotString() + "!");
		return true;

	}

	/**
	 * Save the lottery files.
	 */
	public static void saveLotteryFiles() {
		FileUtility.deleteAllLines(TOTAL_TICKETS_FILE);
		FileUtility.deleteAllLines(LOTTERY_ENTRIES_FILE);
		FileUtility.addLineOnTxt(TOTAL_TICKETS_FILE, totalTicketsPurchased + "");

		ArrayList<String> line = new ArrayList<String>();
		for (int index = 0; index < LotteryDatabase.lotteryDatabase.size(); index++) {
			LotteryDatabase data = LotteryDatabase.lotteryDatabase.get(index);
			line.add(data.getPlayerName() + ServerConstants.TEXT_SEPERATOR + data.getTicketsPurchased());
		}
		FileUtility.saveArrayContentsSilent(LOTTERY_ENTRIES_FILE, line);
	}

	/**
	 * Read the lottery file saves.
	 */
	public static void readLotteryFiles() {
		for (int index = 0; index < NpcHandler.npcs.length; index++) {
			Npc npc = NpcHandler.npcs[index];
			if (npc == null) {
				continue;
			}
			if (npc.npcType == 11057) {
				lotteryNpcIndex = npc.npcIndex;
			}
		}
		try {
			totalTicketsPurchased = Integer.parseInt(FileUtility.readFirstLine(TOTAL_TICKETS_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<String> data = FileUtility.readFile(LOTTERY_ENTRIES_FILE);
		for (int index = 0; index < data.size(); index++) {
			String parse[] = data.get(index).split(ServerConstants.TEXT_SEPERATOR);
			String name = parse[0];
			int ticketsPurchased = Integer.parseInt(parse[1]);
			LotteryDatabase.lotteryDatabase.add(new LotteryDatabase(name, ticketsPurchased));
		}
	}

	/**
	 * Asking Durial how does the lottery work.
	 */
	public static void howDoesTheLotteryWorkDialogueOption(Player player) {
		String times = "";
		for (int index = 0; index < DRAW_TIME_ANNOUNCEMENT.length; index++) {
			String extra = " & ";
			if (index == DRAW_TIME_ANNOUNCEMENT.length - 1) {
				extra = "";
			}
			times = times + DRAW_TIME_ANNOUNCEMENT[index][1] + extra;
		}
		player.getDH().sendNpcChat("I am here to make one lucky player a millionaire!", "Everyday at " + times + " server time,", "the lottery will be, drawn and a new millionare will", "be announced!",
		                           FacialAnimation.HAPPY.getAnimationId());
		player.nextDialogue = 598; // Bring back to lottery option menu.
	}

	/**
	 * Start announcing the pre-draw which is 15 minutes before the winner is announced
	 */
	public static void announcePreDraw(String time) {
		for (int index = 0; index < DRAW_TIME_ANNOUNCEMENT.length; index++) {
			if (time.equals(DRAW_TIME_ANNOUNCEMENT[index][0])) {
				preDrawAnnouncement();
				return;
			}
		}
	}

	/**
	 * Announce the winner of the lottery.
	 */
	public static void announceWinner() {
		ArrayList<String> data = new ArrayList<String>();

		for (int index = 0; index < LotteryDatabase.lotteryDatabase.size(); index++) {
			LotteryDatabase lotteryData = LotteryDatabase.lotteryDatabase.get(index);
			for (int i = 0; i < lotteryData.getTicketsPurchased(); i++) {
				data.add(lotteryData.getPlayerName());
			}
		}
		if (data.isEmpty()) {
			Announcement.announce("No one has entered the lottery.", ServerConstants.RED_COL);
			return;
		}
		String lotteryWinnerName = data.get(Misc.random(data.size() - 1));
		Announcement.announce("<img=27><col=a36718> " + lotteryWinnerName + " has won the lottery worth " + getTotalPotString() + " with " + getWinningPercentage(lotteryWinnerName)
		                      + "% chance of winning!");
		Player winner = Misc.getPlayerByName(lotteryWinnerName);
		if (winner != null) {
			winner.getPA().sendScreenshot("Lottery " + getTotalPotString(), 2);
		}
		Npc npc = NpcHandler.npcs[lotteryNpcIndex];
		npc.forceChat("Congratulations " + lotteryWinnerName + " has won the lottery worth " + getTotalPotString() + "!");
		ItemAssistant.addItemReward(null, lotteryWinnerName, ServerConstants.getMainCurrencyId(), getTotalPotNumber(), false, -1);
		int moneySink = getTotalPotNumber() / 85;
		moneySink *= COMMISION_PERCENTAGE;
		CoinEconomyTracker.addSinkList(null, "LOTTERY " + moneySink);
		totalTicketsPurchased = 0;
		LotteryDatabase.lotteryDatabase.clear();
	}

	/**
	 * List of emotes that Durial uses during the pre-draw.
	 */
	public final static int[] EMOTES =
			{866, 2106, 2107, 2108, 2109, 0x850, 3543, 4280};

	/**
	 * @return The amount of minutes left until the winner is announced.
	 */
	private static int getMinutesLeftTillWinner() {
		// minus 1 second so the chatbox announcement is synced wiht what the npc is spamming.
		return (int) (15 - ((System.currentTimeMillis() - (timePreDrawAnnounced - 1000)) / 60000));
	}

	/**
	 * True if winner has been announced.
	 */
	public static boolean winnerAnnounced;

	/**
	 * The pre-draw announcement events.
	 */
	public static void preDrawAnnouncement() {
		winnerAnnounced = false;
		timePreDrawAnnounced = System.currentTimeMillis();
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			int timer = 0;

			@Override
			public void execute(CycleEventContainer container) {
				Npc npc = NpcHandler.npcs[lotteryNpcIndex];
				npc.forceChat("The lottery is at " + getTotalPotString() + "! Talk to Durial321 at ::shops to win, " + getMinutesLeftTillWinner() + " min" + Misc.getPluralS(
						getMinutesLeftTillWinner()) + " left!");
				Announcement.announce(
						"<img=27><col=a36718> The lottery is at " + getTotalPotString() + "! Talk to Durial321 at ::shops to win, " + getMinutesLeftTillWinner() + " min"
						+ Misc.getPluralS(getMinutesLeftTillWinner()) + " left!");
				timer++;
				if (timer == 15) {
					timer = 0;
					container.stop();
				}
			}

			@Override
			public void stop() {
				winnerAnnounced = true;
				announceWinner();
			}
		}, 100);

		Object object1 = new Object();
		CycleEventHandler.getSingleton().addEvent(object1, new CycleEvent() {
			int timer = 0;

			@Override
			public void execute(CycleEventContainer container) {
				if (winnerAnnounced) {
					container.stop();
					return;
				}

				Npc npc = NpcHandler.npcs[lotteryNpcIndex];
				if (Misc.hasPercentageChance(25)) {
					npc.requestAnimation(EMOTES[Misc.random(EMOTES.length - 1)]);
				}
				npc.forceChat(
						"The lottery is at " + getTotalPotString() + "! Talk to me to win, " + getMinutesLeftTillWinner() + " min" + Misc.getPluralS(getMinutesLeftTillWinner())
						+ " left!");
				timer++;
				if (timer == 1500) {
					timer = 0;
					container.stop();
				}
			}

			@Override
			public void stop() {
			}
		}, 1);

	}

	public static void ViewWinnersInterface(Player player) {
		int frameIndex = 0;
		for (int index = 0; index < LotteryDatabase.lotteryDatabase.size(); index++) {
			//player.getPA().sendFrame126(artefactsStrings[index], 25008 + frameIndex);
			frameIndex++;
		}
		player.getPA().sendFrame126("Lottery winners", 25003);
		InterfaceAssistant.setFixedScrollMax(player, 25007, (int) (frameIndex * 20.5));
		InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
		player.getPA().displayInterface(25000);
	}
}
