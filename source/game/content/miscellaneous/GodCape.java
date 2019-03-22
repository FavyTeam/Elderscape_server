package game.content.miscellaneous;

import game.content.achievement.Achievements;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * God cape claiming.
 *
 * @author MGT Madness, created on 27-01-2015.
 */
public class GodCape {
	/**
	 * Claim the God cape animation.
	 */
	private final static int claimCapeAnimation = 645;

	/**
	 * @param player The associated player.
	 * @return True, if the player owns any of the God capes.
	 */
	private static boolean godCapeOwned(Player player) {
		if (ItemAssistant.hasSingularUntradeableItem(player, ItemAssistant.getItemId("saradomin cape", false)) || ItemAssistant.hasSingularUntradeableItem(player, ItemAssistant
				                                                                                                                                                           .getItemId(
						                                                                                                                                                           "guthix cape",
						                                                                                                                                                           false))
		    || ItemAssistant.hasSingularUntradeableItem(player, ItemAssistant.getItemId("zamorak cape", false))) {
			return true;
		}

		return false;
	}

	/**
	 * Give God cape to the player if requirements are met.
	 *
	 * @param player The associated player.
	 * @param itemName Name of the God cape.
	 */
	public static void giveGodCape(final Player player, final String itemName) {
		if (godCapeOwned(player)) {
			player.playerAssistant.sendMessage("You already own a God cape.");
			return;
		}
		if (ItemAssistant.getFreeInventorySlots(player) < 1) {
			player.playerAssistant.sendMessage("You need at least 1 inventory space.");
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				player.godCapeClaimingTimer++;
				if (player.godCapeClaimingTimer == 1) {
					Movement.playerWalk(player, player.getObjectX(), player.getObjectY() - 2);
				} else if (player.godCapeClaimingTimer == 2) {
					player.turnPlayerTo(player.getObjectX(), player.getObjectY());
				} else if (player.godCapeClaimingTimer == 3) {
					Achievements.checkCompletionSingle(player, 1018);
					player.startAnimation(claimCapeAnimation);
					ItemAssistant.addItem(player, ItemAssistant.getItemId(itemName, false), 1);
					String[] godName = itemName.split(" ");
					player.singularUntradeableItemsOwned.add(Integer.toString(ItemAssistant.getItemId(itemName, false)));
					player.playerAssistant.sendMessage(Misc.optimize(godName[0]) + " blesses you with a cape.");
				}
				if (player.godCapeClaimingTimer == 3) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.godCapeClaimingTimer = 0;
			}
		}, 1);
	}
}
