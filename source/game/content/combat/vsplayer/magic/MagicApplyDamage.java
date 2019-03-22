package game.content.combat.vsplayer.magic;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.worldevent.BloodKey;
import game.player.Player;

/**
 * Apply the Magic hitsplat and other effects after it.
 *
 * @author MGT Madness, created on 21-11-2013.
 */
public class MagicApplyDamage {

	/**
	 * Apply the magic hitsplat.
	 *
	 * @param attacker The player dealing the hitsplat.
	 * @param victim The player receiving the hitsplat.
	 */
	public static void applyMagicHitsplatOnPlayer(final Player attacker, Player victim) {

		int damage = attacker.getMagicDamage();

		if (attacker.isMagicSplash()) {
			damage = 0;
			BloodKey.goldenSkullPlayerDamaged(victim, attacker, 0);
		}
		victim.setUnderAttackBy(attacker.getPlayerId());
		victim.setLastAttackedBy(attacker.getPlayerId());
		if (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][6] != 0) {
			if (!attacker.isMagicSplash()) {
				Combat.createHitsplatOnPlayerPvp(attacker, victim, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MAGIC_ICON, false, "MAGIC", -1);
			}
		}
		victim.setUpdateRequired(true);
		attacker.setUsingMagic(false);
		attacker.setOldSpellId(0);
		if (attacker.getSpellId() == -1 && attacker.getPlayerIdAttacking() == 0) {
			attacker.resetFaceUpdate();
			if (attacker.hasLastCastedMagic()) {
				attacker.resetPlayerIdToFollow();
			}
		}
		attacker.hitsplatApplied = System.currentTimeMillis();
	}

}
