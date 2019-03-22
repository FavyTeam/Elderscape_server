package game.npc.impl.superior.impl;

import core.GameType;
import core.ServerConstants;
import game.position.Position;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.TransientAttributeKey;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.npc.impl.superior.SuperiorNpc;
import game.npc.impl.superior.SuperiorNpcCombatStrategy;
import game.player.Player;
import game.type.GameTypeIdentity;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MK on 2018-07-12 at 10:30 AM
 */
//7411
    //6716, 6723, 7649
@CustomNpcComponent(identities = @GameTypeIdentity(type = GameType.OSRS, identity = 7411))
public class Nechryarch extends SuperiorNpc {

    public static AttributeKey<Set<Npc>> MINIONS = new TransientAttributeKey<>(new HashSet<>());

    private final EntityCombatStrategy strategy = new NechryarchCombatStrategy();

    public Nechryarch(int npcId, int npcType) {
        super(npcId, npcType);
    }

    @Override
    public void afterDeath() {
        super.afterDeath();

        Set<Npc> npcs = getAttributes().getOrDefault(MINIONS);

        for (Npc npc : npcs) {
            npc.setItemsDroppable(false);
            npc.killIfAlive();
        }
    }

    @Override
    public EntityCombatStrategy getCombatStrategyOrNull() {
        return strategy;
    }

    @Override
    public int parent() {
        return 8;
    }

    @Override
    public Npc copy(int index) {
        return new Nechryarch(index, npcType);
    }

    private static class NechryarchCombatStrategy extends SuperiorNpcCombatStrategy {

        private int spawned;

        /**
         * Determines the attack type of the entity, representing what style of combat is being used.
         *
         * @param attacker
         * @param defender
         * @return the attack type, or -1 if none can be found.
         */
        @Override
        public int calculateAttackType(Entity attacker, Entity defender) {
            return 0;
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
                Npc attackerAsNpc = (Npc) attacker;

                Player defenderAsPlayer = (Player) defender;

                DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, attackerAsNpc.getDefinition().maximumDamage));

                int random = ThreadLocalRandom.current().nextInt(0, 100);

                int modifier = 20 - (spawned * 5);

                if (spawned < 3 && random < modifier) {
                    Position playerPosition = new Position(defenderAsPlayer);

                    List<Position> surrounding = new ArrayList<>(playerPosition.surroundingUnblocked(1));

                    Collections.shuffle(surrounding);

                    int[] npcs = new int[] { 6716, 6723, 7649 };

                    for (int npc : npcs) {
                        Position location = surrounding.stream().findAny().orElse(playerPosition);

                        surrounding.remove(location);

                        Npc spawn = NpcHandler.spawnNpc(defenderAsPlayer, npc, location.getX(), location.getY(), location.getZ(), true, false);

                        Set<Npc> minions = attackerAsNpc.getAttributes().getOrDefault(Nechryarch.MINIONS);

                        minions.add(spawn);
                    }
                    spawned++;
                }
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
