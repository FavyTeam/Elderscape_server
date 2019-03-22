package game.npc.impl.dragon.impl.rune;

import core.ServerConstants;
import game.position.Position;
import game.position.PositionUtils;
import game.entity.Entity;
import game.entity.EntityType;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.npc.impl.dragon.DragonCombatStrategy;
import game.player.Player;
import utility.Misc;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MK on 2018-06-28 at 3:06 PM
 */
public class RuneDragonCombatStrategy extends DragonCombatStrategy {

    private RuneDragonAttack attack = RuneDragonAttack.REGULAR;

    /**
     * Determines the attack type of the entity, representing what style of combat is being used.
     *
     * @param attacker
     * @param defender
     * @return the attack type, or -1 if none can be found.
     */
    @Override
    public int calculateAttackType(Entity attacker, Entity defender) {
        int random = ThreadLocalRandom.current().nextInt(0, 100);

        attack = random < 30 ? RuneDragonAttack.RANGE : random < 70 ? RuneDragonAttack.REGULAR
                : random < 90 ? RuneDragonAttack.ELECTRICITY : RuneDragonAttack.ONYX_BOLT;

        if (attack == RuneDragonAttack.ONYX_BOLT) {
            return ServerConstants.RANGED_ICON;
        } else if (attack == RuneDragonAttack.ELECTRICITY) {
            return ServerConstants.MAGIC_ICON;
        }
        return super.calculateAttackType(attacker, defender);
    }

    /**
     * References when {@link #isCustomAttack()} returns true, in which case we're responsible the
     * regular combat process.
     *
     * @param attacker the attacker.
     * @param defender the defender.
     */
    @Override
    public void onCustomAttack(Entity attacker, Entity defender) {
        if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
            Npc npc = (Npc) attacker;

            Player player = (Player) defender;

            if (attack == RuneDragonAttack.ELECTRICITY) {
                Position target = new Position(player);

                Set<Position> positions = target.surroundingUnblocked(1);

                if (!positions.isEmpty()) {
                    target = PositionUtils.randomOr(positions, target);
                }
                player.getPA().createPlayersProjectile(npc, target, 50, 50, 1488, 30, 30, 0, 0, 20);

                npc.getEventHandler().addEvent(npc, new RuneDragonElectricityEvent(npc, player, target), 1);
            } else if (attack == RuneDragonAttack.ONYX_BOLT) {
                player.getPA().createPlayersProjectile(npc, player, 50, 100, 440, 25, 15, Misc.lockon(player), 45, 20);
                DamageQueue.add(new Damage(player, npc, ServerConstants.RANGED_ICON, 4, 29, -1, null, (d, p) -> npc.heal(d)));
            } else if (attack == RuneDragonAttack.RANGE) {
                player.getPA().createPlayersProjectile(npc, player, 50, 100, 1459, 15, 15, Misc.lockon(player), 45, 20);
                DamageQueue.add(new Damage(player, npc, ServerConstants.RANGED_ICON, 4, 29, -1, null, null, (d, p) -> p.gfx0(1460)));
            } else if (attack == RuneDragonAttack.REGULAR) {
                super.onCustomAttack(attacker, defender);
            }
        }

    }

    /**
     * Retrieves the custom emote for the attacking entity, or -1 if there is no custom emote.
     *
     * @param attacker the entity making the attack animation.
     * @return the attack emote, or -1 by default for no custom attack emote.
     */
    @Override
    public int getCustomAttackAnimation(Entity attacker) {
        return super.getCustomAttackAnimation(attacker);
    }
}
