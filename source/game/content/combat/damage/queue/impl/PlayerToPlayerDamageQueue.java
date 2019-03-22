package game.content.combat.damage.queue.impl;

import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.damage.queue.EntityDamageQueue;
import game.content.worldevent.BloodKey;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-05-02 at 2:38 PM
 */
public class PlayerToPlayerDamageQueue extends EntityDamageQueue<Player, Player> {

    @Override
    public void apply(EntityDamage<Player, Player> damage) {
        Player victim = damage.getTarget();
        Player attacker = damage.getSender();

        // Cannot damage victim checks
        if (victim.getDead()) {
            return;
        }
        if ((victim.isTeleporting() || victim.getDoingAgility()) && !victim.usingTeleportWithNoCombatQueueing) {
            damage.setSkip(true);
            damage.setSendXpDrop(false);
            attacker.getIncomingDamageOnVictim().add(damage);
            return;
        }
        victim.setUnderAttackBy(attacker.getPlayerId());
        victim.setLastAttackedBy(attacker.getPlayerId());
        Combat.autoRetaliate(victim, attacker);
        boolean nonDamageSpell = damage.getDamageType() == EntityDamageType.MAGIC && CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][16] == 0;
        if (damage.isMagicSplash() || nonDamageSpell) {
            BloodKey.goldenSkullPlayerDamaged(victim, attacker, 0);
            Combat.magicOnHitsplatReset(attacker);
            if (nonDamageSpell) {
                damage.getEffects().forEach(effect -> effect.onApply(damage));
                Combat.magicOnHitsplatReset(attacker);
            }
            return;
        }
        Combat.createHitsplatOnPlayerPvp(attacker, victim, damage.getDamage(), damage.getDamageType().getPreEocHitsplatColour(), damage.getDamageType().getPreEocHitsplatIcon(), false, damage.getDamageType().name().toString(), damage.getMaximumDamage());
        damage.getEffects().forEach(effect -> effect.onApply(damage));
        if (damage.getDamageType() == EntityDamageType.RANGED || damage.getDamageType() == EntityDamageType.DRAGON_FIRE) {
            Combat.performBlockAnimation(victim, attacker);
        }
        if (damage.getDamage() > 0) {
            Combat.appendVengeanceVsPlayer(attacker, victim, damage.getDamage());
        }
        if (damage.getDamageType() == EntityDamageType.MAGIC) {
            Combat.magicOnHitsplatReset(attacker);
        }
        attacker.hitsplatApplied = System.currentTimeMillis();
    }
}
