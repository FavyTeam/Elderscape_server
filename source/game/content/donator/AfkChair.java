package game.content.donator;

import core.Server;
import core.ServerConstants;
import game.content.combat.Combat;
import game.item.ItemAssistant;
import game.object.clip.Region;
import game.object.custom.Object;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;

public class AfkChair {

	public static void afk(final Player player) {
		if (player.getDoingAgility()) {
			return;
		}
		if (!player.isLegendaryDonator() && !player.getPlayerName().equalsIgnoreCase("jason")) {
			return;
		}
		if (Region.objectExistsHere(player) && !player.idleEventUsed) {
			player.getPA().sendMessage("You cannot afk on this spot.");
			return;
		}
		if (ItemAssistant.hasItemEquippedSlot(player, 4084, ServerConstants.WEAPON_SLOT)) {
			return;
		}
		if (player.getPlayerName().equalsIgnoreCase("jason") ||
				(player.isLegendaryDonator()) && !player.idleEventUsed && player.getTransformed() == 0 && !Combat.inCombat(player) && !player.resting
		    && !Area.inDangerousPvpAreaOrClanWars(player)) {
			Movement.stopMovement(player);
			Combat.resetPlayerAttack(player);
			player.idleEventUsed = true;
			player.turnPlayerTo(player.getX(), player.getY() - 1);
			player.playerStandIndex = 3363;
			player.startAnimation(3363);
			int face = 2;
	
			// King Thrones.
			if (player.throneId >= 13665) {
				face = 0;
			}
			new Object(player, player.throneId, player.getX(), player.getY(), player.getHeight(), face, 10, -1, -1);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (player.idleEventUsed) {
						player.gfx0(277);
						player.getPA().requestUpdates();
					} else {
						container.stop();
					}
				}
	
				@Override
				public void stop() {
					AfkChair.resetAfk(player, false);
				}
			}, 4);
		}
	}

	public static void resetAfk(Player player, boolean loggingOn) {
		DonatorContent.getOffChair(player, loggingOn);
		if (player.idleEventUsed) {
			if (!loggingOn) {
				player.startAnimation(65535);
				Combat.updatePlayerStance(player);
			}
			player.getPA().requestUpdates();
			player.idleEventUsed = false;
			for (int index = 0; index < player.toRemove.size(); index++) {
				if (player.toRemove.get(index).objectId == player.throneId) {
					Server.objectManager.toRemove.add(player.toRemove.get(index));
					break;
				}
			}
			player.toRemove.clear();
		}
	}

}
