package game.npc.impl.superior.impl;

import core.GameType;
import core.ServerConstants;
import game.position.Position;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.npc.impl.superior.SuperiorNpc;
import game.npc.impl.superior.SuperiorNpcCombatStrategy;
import game.player.Player;
import game.type.GameTypeIdentity;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MK on 2018-07-11 at 1:22 PM
 */
@CustomNpcComponent(identities = @GameTypeIdentity(type = GameType.OSRS, identity = 7410))
public class GreaterAbyssalDemon extends SuperiorNpc {

    private final EntityCombatStrategy strategy = new GreaterAbyssalDemonCombatStrategy();

    public GreaterAbyssalDemon(int npcId, int npcType) {
        super(npcId, npcType);
    }

    @Override
    public int parent() {
        return 415;
    }

    @Override
    public Npc copy(int index) {
        return new GreaterAbyssalDemon(index, npcType);
    }

    @Override
    public EntityCombatStrategy getCombatStrategyOrNull() {
        return strategy;
    }

    private static class GreaterAbyssalDemonCombatStrategy extends SuperiorNpcCombatStrategy {

        enum GreaterAbyssalDemonAttackType {
            TELEPORT,

            REGULAR
        }

        private int teleportsRemaining;

        private GreaterAbyssalDemonAttackType attackType;

        @Override
        public int calculateAttackType(Entity attacker, Entity defender) {
            if (attackType == GreaterAbyssalDemonAttackType.TELEPORT && teleportsRemaining > 0) {
                return ServerConstants.MELEE_ICON;
            }
            if (ThreadLocalRandom.current().nextInt(0, 100) < 10) {
                teleportsRemaining = 4;
                attackType = GreaterAbyssalDemonAttackType.TELEPORT;
                return ServerConstants.MELEE_ICON;
            }
            attackType = GreaterAbyssalDemonAttackType.REGULAR;
            return ServerConstants.MELEE_ICON;
        }

        @Override
        public void onCustomAttack(Entity attacker, Entity defender) {
            if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
                Npc attackerAsNpc = (Npc) attacker;

                Player defenderAsPlayer = (Player) defender;

                if (attackType == GreaterAbyssalDemonAttackType.TELEPORT) {
                    DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, attackerAsNpc.getDefinition().maximumDamage, -1,
                            null, null, (d, p) -> {
                        Position teleport = new Position(p).surroundingUnblocked(1).stream().findAny().orElse(null);

                        if (teleport == null) {
                            return;
                        }
                        attackerAsNpc.move(teleport);
                    }));
                    teleportsRemaining--;
                } else {
                    if (ThreadLocalRandom.current().nextInt(0, 100) <= 15) {
                        Position teleport = new Position(defenderAsPlayer).surroundingUnblocked(2).stream().findAny().orElse(null);

                        if (teleport == null) {
                            return;
                        }
                        defenderAsPlayer.move(teleport);
                    }
                    DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, attackerAsNpc.getDefinition().maximumDamage, -1));
                }
            }
        }

        /**
         * Referenced only if {@link #isCustomAttack()} returns true, in which case we can change some
         * important combat related information to the attacker and defender, like attack speed.
         *
         * @param attacker the entity making the attack.
         * @param defender the entity defending themselves.
         */
        @Override
        public void afterCustomAttack(Entity attacker, Entity defender) {
            if (attackType == GreaterAbyssalDemonAttackType.TELEPORT && attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
                Npc attackerAsNpc = (Npc) attacker;

                attackerAsNpc.attackTimer = 2;
            }
        }

        /**
         * Determines if we're going to handle the entire attack process our self.
         *
         * @return {@code true} if it's a custom attack, by default, false.
         */
        @Override
        public boolean isCustomAttack() {
            return true;
        }
    }

}
