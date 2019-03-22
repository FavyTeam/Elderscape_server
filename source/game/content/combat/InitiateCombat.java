package game.content.combat;

import game.content.combat.vsnpc.CombatNpc;
import game.content.combat.vsplayer.AttackPlayer;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.PlayerHandler;

/**
 * Methods related to initiating combat.
 *
 * @author MGT Madness, created on 27-03-2015.
 */
public class InitiateCombat {

	/**
	 * Handle when the player is allowed to attack.
	 *
	 * @param player The associated player.
	 */
	public static void attackHandler(Player player) {
		if (player.getAttackTimer() > 0) {
			player.setAttackTimer(player.getAttackTimer() - 1);
		}
		if (player.getNpcIdAttacking() > 0 && player.getAttackTimer() == 0) {
			Npc NPC = NpcHandler.npcs[player.getNpcIdAttacking()];
			CombatNpc.attackNpc(player, NPC);
		}
		if (player.getPlayerIdAttacking() > 0) {
			Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];
			if (player.getAttackTimer() == 0) {
				AttackPlayer.handlePlayerAttack(player, victim, false);
			}
			else if (player.getAttackTimer() > 0 && player.graniteMaulSpecialAttackClicks > 0 && player.getSpecialAttackAmount() >= 5 && Combat.hasGraniteMaulEquipped(player)) {
				AttackPlayer.handlePlayerAttack(player, victim, true);
			}
			else if (player.getAttackTimer() > 0 && player.getWieldedWeapon() == 20849 && player.isUsingSpecial() && player.getSpecialAttackAmount() >= 2.5) {
				AttackPlayer.handlePlayerAttack(player, victim, true);
			}
		}
	}

	/**
	 * Handle when the hitsplat is going to be applied.
	 *
	 * @param player The associated player.
	 */
	public static void applyHitSplatHandler(Player player, boolean decrease) {
		if (decrease) {
			if (player.getHitDelay() > 1) {
				player.setHitDelay(player.getHitDelay() - 1);
			}
		}

		if (player.getHitDelay() == (decrease ? 1 : 2)) {
			if (player.getOldNpcIndex() > 0) {

				Npc npc = NpcHandler.npcs[player.getOldNpcIndex()];

				CombatNpc.applyHitSplatOnNPC(player, npc);
			}
		}
	}

}
