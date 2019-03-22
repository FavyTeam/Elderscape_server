package game.content.bank;

import game.player.Player;
import utility.Misc;

public class BankPin {

	private static int actionButtons[] =
			{58025, 58026, 58027, 58028, 58029, 58030, 58031, 58032, 58033, 58034};

	public static void close(Player player) {
		player.enteredPin = "";
		player.getPA().closeInterfaces(true);
		player.bankPinInterfaceStep = 1;
	}

	private static void enterPin(Player player, int button) {
		for (int i = 0; i < getActionButtons().length; i++) {
			if (getActionButtons()[i] == button) {
				player.enteredPin += getBankPins(player)[i] + "";
			}
		}
		switch (player.bankPinInterfaceStep) {
			case 1:
				player.bankPinInterfaceStep = 2;
				resend(player);
				break;
			case 2:
				player.bankPinInterfaceStep = 3;
				resend(player);
				break;
			case 3:
				player.bankPinInterfaceStep = 4;
				resend(player);
				break;
			case 4:
				if (!player.setPin) {
					player.bankPin = player.enteredPin.trim();
					player.fullPin = player.enteredPin.trim();
					player.setPin = true;
					player.hasEnteredPin = true;
					player.playerAssistant.sendMessage("You have successfully set your bankpin to <col=255>" + player.bankPin + ".");
					player.playerAssistant.sendMessage("Please do not forget your bank pin, write it down on your computer or write it on a");
					player.playerAssistant.sendMessage("piece of paper.");
					resend(player);
				} else {
					if (player.bankPin.equalsIgnoreCase(player.enteredPin.trim())) {
						player.playerAssistant.sendMessage("You have successfully entered your bank pin which is <col=255>" + player.bankPin + ".");
						player.fullPin = player.enteredPin.trim();
						player.hasEnteredPin = true;
						resend(player);
					} else {
						player.playerAssistant.sendMessage("The pin you entered is incorrect.");
						close(player);
					}
				}
				player.bankPinInterfaceStep = 1;
				break;
		}
	}

	private static int[] getActionButtons() {
		return actionButtons;
	}

	private static int[] getBankPins(Player player) {
		return player.bankPins;
	}

	public static String getFullPin(Player player) {
		return player.fullPin;
	}

	private static void mixNumbers(Player player) {
		for (int i = 0; i < player.bankPins.length; i++) {
			player.bankPins[i] = -1;
		}
		for (int i = 0; i < player.bankPins.length; i++) {
			for (int i2 = 0; i2 < 9999; i2++) {
				boolean can = true;
				int random = Misc.random(9);
				for (int i3 = 0; i3 < player.bankPins.length; i3++) {
					if (random == player.bankPins[i3]) {
						can = false;
						random = Misc.random(9);
					}
				}
				if (!can) {
					continue;
				} else {
					player.bankPins[i] = random;
					break;
				}
			}
		}
		sendPins(player);
	}

	public static void open(Player player) {
		player.enteredPin = "";
		player.setUsingBankInterface(true);
		if (!(player.fullPin.equalsIgnoreCase(""))) {
			Bank.openUpBank(player, player.bankingTab, true, true);
			return;
		}
		player.bankPinInterfaceStep = 1;
		player.getPA().displayInterface(7424);

		// Parts which show ??? which turn to *** when a pin is entered.
		player.getPA().sendFrame126("?", 14913);
		player.getPA().sendFrame126("?", 14914);
		player.getPA().sendFrame126("?", 14915);
		resend(player);
	}

	public static void pinEnter(Player player, int button) {
		if (!Bank.hasBankingRequirements(player, false)) {
			return;
		}
		switch (player.bankPinInterfaceStep) {
			case 1:
			case 2:
			case 3:
			case 4:
				enterPin(player, button);
				break;
		}
	}

	private static void resend(Player player) {
		if (!(player.fullPin.equalsIgnoreCase(""))) {
			Bank.openUpBank(player, player.bankingTab, true, true);
			return;
		}
		mixNumbers(player);
		switch (player.bankPinInterfaceStep) {
			case 1:
				player.getPA().sendFrame126("First click the FIRST digit", 15313);
				break;
			case 2:
				player.getPA().sendFrame126("Then click the SECOND digit", 15313);
				player.getPA().sendFrame126("*", 14913);
				break;
			case 3:
				player.getPA().sendFrame126("Then click the THIRD digit", 15313);
				player.getPA().sendFrame126("*", 14914);
				break;
			case 4:
				player.getPA().sendFrame126("And lastly click the FOURTH digit", 15313);
				player.getPA().sendFrame126("*", 14915);
				break;
		}
		sendPins(player);
	}

	public void reset(Player player) {
		player.bankPin = "";
		player.attempts = 3;
		player.enteredPin = "";
		player.fullPin = "";
	}

	private static void sendPins(Player player) {
		if (!(player.fullPin.equalsIgnoreCase(""))) {
			Bank.openUpBank(player, player.bankingTab, true, true);
			return;
		}
		for (int i = 0; i < getBankPins(player).length; i++) {
			player.getPA().sendFrame126("" + getBankPins(player)[i], 14883 + i);
		}
	}
}
