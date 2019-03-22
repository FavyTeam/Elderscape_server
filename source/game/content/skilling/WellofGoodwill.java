package game.content.skilling;

import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

import java.io.*;
import java.util.Scanner;

public class WellofGoodwill {

	public static int questTabTimer;

	private final static File file = new File("backup/logs/wellofgoodwill.txt");

	private static int amountInWell = 0;

	private static double timeofDoubleExp = 0;

	private static boolean wellActive = false;

	private final static int WELLMAX = 50000000;

	private static int countTimedMessages = 0;

	private static double timeToSet;

	public static int realTime;

	/*
	 * The Duration of time the well will last for
	 */
	public static final double MINUTES = 60.0;

	private int FRAME27_AMOUNT;

	public void addtoWell(Player c, int amount) throws IOException {
		if (amount > WELLMAX - amountInWell) {
			int newAmount = WellofGoodwill.WELLMAX - amountInWell;
			addtoWell(c, newAmount);
		} else {
			if (ItemAssistant.hasItemAmountInInventory(c, 995, amount)) {
				ItemAssistant.deleteItemFromInventory(c, 995, amount);
				amountInWell += amount;
				c.getPA()
				 .announce("<img=31><col=763904> " + c.getPlayerName() + "</col> has just donated <col=763904>" + convertChatCurrency(amount) + "</col> to the Well of Goodwill.");
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("backup/logs/wellofgoodwill.txt", false)));
				out.println(amountInWell);
				out.close();
				if (amountInWell >= WELLMAX) {
					startBonusExp(c);
					amountInWell = 0;
					PrintWriter out2 = new PrintWriter(new BufferedWriter(new FileWriter("backup/logs/wellofgoodwill.txt", false)));
					out2.println(amountInWell);
					out2.close();
				}
			}
		}
	}

	public static void setWellAmount() throws IOException {
		int amount = 0;
		Scanner scanner = new Scanner(file);
		amount = scanner.nextInt();
		scanner.close();
		WellofGoodwill.amountInWell = amount;
	}

	public static int getIntAmountinWell() {
		return WellofGoodwill.amountInWell;
	}

	public static String getAmountinWell() {
		return WellofGoodwill.convertChatCurrency(WellofGoodwill.amountInWell);
	}

	public static double getTime() {
		return WellofGoodwill.timeofDoubleExp;
	}

	public static void setTime(double d) {
		WellofGoodwill.timeofDoubleExp = d;
		timeToSet = d / 4;
	}

	public static double getTimeLeft() {
		return (timeofDoubleExp - realTime) / 100.0;
	}

	public static void setStatus(boolean status) {
		WellofGoodwill.wellActive = status;
	}

	public static boolean getStatus() {
		return WellofGoodwill.wellActive;
	}

	public static final int GETMAX() {
		return WellofGoodwill.WELLMAX;
	}

	public static String convertChatCurrency(int amount) {
		String ShopAdd = "";
		if (amount >= 1000 && amount < 1000000) {
			ShopAdd = (amount / 1000) + "K";
		} else if (amount >= 1000000) {
			ShopAdd = (amount / 1000000) + "M";
		} else {
			ShopAdd = amount + "";
		}
		return ShopAdd;
	}

	public void startBonusExp(Player player) {

		if (!WellofGoodwill.getStatus()) {
			WellofGoodwill.setStatus(true);
			ServerConstants.DOUBLE_EXP = true;
			WellofGoodwill.setTime(100 * MINUTES);
		}

		player.getPA().announce("<img=31><col=763904> The Well of Goodwill has activated Double XP for " + (WellofGoodwill.getTime() / 100) + " minutes!");

		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				realTime = container.getTotalTicks();
				questTabTimer = (int) getTimeLeft();
				if (container.getTotalTicks() > getTime() || !WellofGoodwill.getStatus()) {
					container.stop();
					return;
				}
				if (WellofGoodwill.getStatus() && container.getTotalTicks() >= timeToSet && countTimedMessages == 0) {
					player.getPA().announce("There are <col=763904>" + getTimeLeft() + "</col> minutes of double XP left.");
					countTimedMessages++;
				}
				if (WellofGoodwill.getStatus() && container.getTotalTicks() >= timeToSet * 2 && countTimedMessages == 1) {
					player.getPA().announce("There are <col=763904>" + getTimeLeft() + "</col> minutes of double XP left.");
					countTimedMessages++;
				}
				if (WellofGoodwill.getStatus() && container.getTotalTicks() == timeToSet * 3 && countTimedMessages == 2) {
					player.getPA().announce("There are <col=763904>" + getTimeLeft() + "</col> minutes of double XP left.");
				}
			}

			@Override
			public void stop() {
				player.getPA().announce("<col=763904> The hour of double XP has ended.");
				player.getPA().announce("<img=31><col=763904> Donate to the Well of Goodwill for another hour!");
				WellofGoodwill.setStatus(false);
				ServerConstants.DOUBLE_EXP = false;
				WellofGoodwill.setTime(0);
				timeToSet = 0;
				countTimedMessages = 0;
				realTime = 0;
				questTabTimer = 0;
			}
		}, 0);
	}

	/**
	 * Handles all Dialogues within this class
	 */

	public void sendDialogues(Player player, int dialogue, int itemUsed) {
		switch (dialogue) {
			case 1:
				player.getDH().sendItemChat("Well of Goodwill@bla@", "The Well currently has a total value of " + convertChatCurrency(amountInWell) + ".",
				                            "How much gold would you like to donate?", 1004, 200, 0, 0);
				player.nextChatWell = 2;
				break;
			case 2:
				player.getOutStream().createFrame(27);
				player.setxInterfaceId(24313);
				player.nextChatWell = 0;
				break;
			case 3:
				try {
					addtoWell(player, FRAME27_AMOUNT);
					FRAME27_AMOUNT = 0;
					//player.Xamount = 0;
				} catch (IOException e) {
					player.getPA().sendMessage("Failed to add donation. Please try again.");
					e.printStackTrace();
				}
				break;
		}
	}

	/**
	 * Chooses which Tax Bag to show for the Donation Item Chat based on amount within the well
	 */

	public int taxBagToShow() {
		return amountInWell < 1 ?
				       1000 :
				       amountInWell > 0 && amountInWell < 1000000 ?
						       1001 :
						       amountInWell > 999999 && amountInWell < 9999999 ? 1002 : amountInWell > 9999999 && amountInWell < 19999999 ? 1003 : 1004;
	}

	/**
	 * Handles all X amounts from createFrame(27); within this class
	 */

	public void appendXAmount(Player player, int frameId, int xAmount) {
		switch (frameId) {
			case 24313:
				FRAME27_AMOUNT = xAmount;
				player.getPA().closeInterfaces(true);
				sendDialogues(player, 3, -1);
				player.setxInterfaceId(0);
				break;
		}
	}


	/**
	 * Handles all X amounts from createFrame(27); within this class
	 */

	public static final int FIRST_CLICK = 132, SECOND_CLICK = 252, THIRD_CLICK = 70;


	/**
	 * Handles all X amounts from createFrame(27); within this class
	 */

	public void appendItemOnObject(Player player, int objectId, int itemUsed) {
		switch (objectId) {
			case 30251:
				if (itemUsed == 995 && WellofGoodwill.getStatus()) {
					player.getPA().sendMessage("<col=763904>The Well of Goodwill is already active! Please donate again later.@bla@");
					return;
				} else if (itemUsed == 995 && !WellofGoodwill.getStatus()) {
					sendDialogues(player, 1, -1);
					return;
				}
				break;
		}
	}
}
