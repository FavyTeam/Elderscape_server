package game.player;

import core.ServerConfiguration;
import game.content.combat.Combat;
import game.content.combat.InitiateCombat;
import game.content.miscellaneous.LootNotification;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.movement.Follow;

/**
 * The method which is called every game tick, only add inside it player content.
 *
 * @author MGT Madness, created on 02-03-2015.
 */
public class PlayerContentTick {

	/**
	 * Player content tick before the movement update
	 *
	 * @param player
	 */
	public static void preMovementContentTick(Player player) {
		if (ServerConfiguration.FORCE_ITEM_UPDATE) {
			player.playerAssistant.sendMessage(":compu:");
		}

		if (player.getPlayerIdToFollow() > 0) {
			Follow.followPlayer(player, false);
		} else if (player.getNpcIdToFollow() > 0) {
			Follow.followNpc(player);
		}
		player.gameTicksOnline++;
		if (player.gameTicksOnline == 10) {
			player.secondsBeenOnline += 6;
			player.gameTicksOnline = 0;
		}
		Combat.preMovementCombatFix(player);
	}

	/**
	 * Combat is called after movement, just like Runescape, to have Runescape combat effects.
	 *
	 * @param player
	 */
	public static void afterMovementContentTick(Player player) {
		if (!Combat.wasUnderAttackByAnotherPlayer(player, 9600)) {
			player.setUnderAttackBy(0);
		}

		InitiateCombat.applyHitSplatHandler(player, false);
		player.getIncomingNpcDamage().process(false);
		player.getIncomingDamageOnVictim().process(false);
		InitiateCombat.attackHandler(player);
		player.getIncomingNpcDamage().process(true);
		player.getIncomingDamageOnVictim().process(true);
		InitiateCombat.applyHitSplatHandler(player, true);
		Skilling.sendXpDropAmount(player);
		player.setBotActionApplied(false);
		player.packetsSentThisTick = 0;
		player.botWornItemThisTick = false;
		ItemAssistant.sendGroundItemCustompacket(player);
		ItemAssistant.sendGroundItemRemoveCustompacket(player);
		LootNotification.endOfGameTick(player);
	}

	public static void updateClientHeight(Player player, boolean logIn) {
		if (player == null) {
			return;
		}
		if (player.startHeight != player.getHeight() || logIn) {
			player.getPA().sendMessage(":packet:height" + player.getHeight());
		}

	}
}
