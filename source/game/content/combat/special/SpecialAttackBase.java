package game.content.combat.special;

import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.combat.vsplayer.range.RangedFormula;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.PlayerHandler;

/**
 * Special attacks.
 *
 * @author MGT Madness, created on 21-11-2013.
 */
public class SpecialAttackBase {

	public static EntityDamageType damageType = null;

	public static EntityDamage<Player, Player> effect = new EntityDamage<Player, Player>(null, null, 0, 0, null, 0, false, false);
	public static void activateSpecial(final Player attacker, int weapon, int theTarget) {
		Player victim = null;
		if (attacker.getNpcIdAttacking() <= 0) {
			victim = PlayerHandler.players[theTarget];
		}
		Npc targetNpc = NpcHandler.npcs[theTarget];

		String itemName = ItemAssistant.getItemName(weapon);
		int delay = Combat.getHitDelay(attacker, itemName.toLowerCase());

		if (targetNpc == null && attacker.getNpcIdAttacking() > 0) {
			return;
		}
		attacker.setSpecEffect(0);
		attacker.setUsingDarkBowSpecialAttack(false);
		attacker.setProjectileStage(0);
		attacker.specDamage = 1.0;
		attacker.setSpecialAttackAccuracyMultiplier(1.0);

		int damageAmount = 0;

		boolean victimExists = false;
		if (attacker.getNpcIdAttacking() > 0) {
			attacker.setOldNpcIndex(targetNpc.npcIndex);
		} else if (attacker.getPlayerIdAttacking() > 0) {
			if (victim != null) {
				victim.setUnderAttackBy(attacker.getPlayerId());
				victim.setLastAttackedBy(attacker.getPlayerId());
				victimExists = true;
			}
		}

		SpecialAttackPreEoc.specialAttackPreEoc(attacker, victim, weapon, victimExists, delay, targetNpc, itemName);
		SpecialAttackOsrs.specialAttackOsrs(attacker, victim, weapon, victimExists, delay, targetNpc, itemName);
		SpecialAttackAll.specialAttackAll(attacker, victim, weapon, victimExists, delay, targetNpc, itemName);

		if (damageType != null && victimExists) {
			int maximumDamage = 0;
			switch (damageType) {
				case MELEE :
					damageAmount = MeleeFormula.calculateMeleeDamage(attacker, victim, 0);
					maximumDamage = attacker.maximumDamageMelee;
					Combat.performBlockAnimation(victim, attacker);
					break;
				case RANGED :
					damageAmount = RangedFormula.calculateRangedDamage(attacker, victim, false);
					maximumDamage = attacker.maximumDamageRanged;
					break;
				case MAGIC :
					maximumDamage = attacker.getMaximumDamageMagic();
					break;
			}
			EntityDamage<Player, Player> damage = new EntityDamage<Player, Player>(victim, attacker, damageAmount, delay, damageType, maximumDamage, true, false);
			for (int index = 0; index < effect.getEffects().size(); index++) {
				damage.addEffect(effect.getEffects().get(index));
			}
			attacker.getIncomingDamageOnVictim().add(damage);
			Combat.attackApplied(attacker, victim, damageType.toString(), true);
		}
		effect.clearEffects();
		damageType = null;
	}

}
