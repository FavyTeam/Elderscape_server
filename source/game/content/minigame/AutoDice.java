package game.content.minigame;

import game.content.combat.Combat;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Automatic dice system where the player can dice and flower poker safely with scam proof, similair to duel arena.
 *
 * @author MGT Madness, created on 03-02-2017.
 */
public class AutoDice {

	public final static boolean AUTOMATIC_DICE_ENABLED = false;

	public final static int GAMBLE_INTERFACE_ID = 26000;

	/*
	 * Both players right click dice each other.
	 * An interface pops up.
	 * They pick the game option, 55x2, 70x3, flower poker, there are a few more flower poker games, need to ask players.
	 * Able to add items from inventory to the interface.
	 * They are given 5 seconds after the last change to make sure, once they accept, the game will start.
	 * Once the game starts, both sides can do their rolls/plants if they have a dice bag in inventory or at least 25 seeds.
	 * The game can only start if one of them as the required items to do the game and enough seeds for themselves and the other person if the other person does not have seeds.
	 *
	 * The item stored array is being used by trade, duel and dice, test possible exploits where a player is in trade/duel and they accept a dice request or
	 * player is in dice interface with items places and then i try to accept a trade/duel request.
	 * Test more exploits where a player tries to interact with the gambling interface button while not in the interface with another player.
	 */

	/**
	 * Right click gamble option on another player.
	 *
	 * @return
	 */
	public static boolean gambleOptionClicked(Player player, Player other) {
		if (!AUTOMATIC_DICE_ENABLED) {
			return false;
		}
		if (!Area.inDiceZone(player)) {
			return false;
		}
		if (player.hasLastCastedMagic()) {
			return false;
		}

		player.setMeleeFollow(true);
		if (player.findOtherPlayerId > 0) {
			return true;
		}
		player.setGambledPlayerOptionName(other.getPlayerName());
		player.findOtherPlayerId = 20;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.findOtherPlayerId > 0) {
					player.findOtherPlayerId--;
					if (player.getPA().withinDistanceOfTargetPlayer(other, 1)) {
						sendGambleRequest(player, other);
						container.stop();
					}
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.findOtherPlayerId = 0;
			}
		}, 1);
		return true;
	}

	private static void sendGambleRequest(Player player, Player other) {
		if (!AUTOMATIC_DICE_ENABLED) {
			return;
		}

		if (other.getPA().playerIsBusy()) {
			player.turnPlayerTo(other.getX(), other.getY());
			player.getPA().sendMessage(other.getPlayerName() + " is busy.");
			Combat.resetPlayerAttack(player);
			return;
		}
		// Send gamble request in chatbox as a light blue colour
		if (other.getGambledPlayerOptionName().equals(player.getPlayerName())) {
			displayAutoDiceInterface(player);
			displayAutoDiceInterface(other);
		}
	}

	private static void displayAutoDiceInterface(Player player) {
		//Empty item id interface ids
		//reset selected game mode ids
		player.getPA().displayInterface(GAMBLE_INTERFACE_ID);
	}

	public static boolean isAutoDiceInterfaceClicked(Player player, int buttonId) {
		if (!AUTOMATIC_DICE_ENABLED) {
			return false;
		}
		switch (buttonId) {
			case 1:
				return true;
		}
		return false;
	}

	public static void declineGamblingInterface(Player player) {
		if (!AUTOMATIC_DICE_ENABLED) {
			return;
		}
		if (player.isInGambleMatch()) {
			return;
		}
		if (player.getInterfaceIdOpened() != GAMBLE_INTERFACE_ID) {
			return;
		}
		refundBothPlayers(player);
	}

	/**
	 * Cancel the currently active gambling match and refund both sides. This is only called when the server is restarting and has force logged everyone.
	 */
	public static void cancelGamblingMatchAndRefund(Player player) {
		if (!AUTOMATIC_DICE_ENABLED) {
			return;
		}
		if (!player.isInGambleMatch()) {
			return;
		}
		refundBothPlayers(player);
	}


	private static void refundBothPlayers(Player player) {
		refundOfferedItems(player);
		Player other = Misc.getPlayerByName(player.getGambledPlayerOptionName());
		if (other != null) {
			refundOfferedItems(other);
		}
	}

	private static void refundOfferedItems(Player player) {
		for (GameItem item : player.getTradeAndDuel().offeredItems) {
			if (item.getAmount() < 1) {
				continue;
			}
			if (item.isStackable()) {
				ItemAssistant.addItem(player, item.getId(), item.getAmount());
			} else {
				for (int i = 0; i < item.getAmount(); i++) {
					ItemAssistant.addItem(player, item.getId(), 1);
				}
			}
		}
	}

}
