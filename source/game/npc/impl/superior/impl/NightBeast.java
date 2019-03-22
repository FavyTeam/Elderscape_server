package game.npc.impl.superior.impl;

import core.GameType;
import core.ServerConstants;
import game.position.Position;
import game.content.skilling.Skill;
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

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MK on 2018-07-11 at 11:39 AM
 */
@CustomNpcComponent(identities = @GameTypeIdentity(type = GameType.OSRS, identity = 7409))
public class NightBeast extends SuperiorNpc {

    private final EntityCombatStrategy strategy = new NightBeastCombatStrategy();

    public NightBeast(int npcId, int npcType) {
        super(npcId, npcType);
    }

    @Override
    public int parent() {
        return 4005;
    }

    @Override
    public Npc copy(int index) {
        return new NightBeast(index, npcType);
    }

    @Override
    public EntityCombatStrategy getCombatStrategyOrNull() {
        return strategy;
    }

    private class NightBeastCombatStrategy extends SuperiorNpcCombatStrategy {

        private int specialAttacksRemaining;

        @Override
        public int calculateAttackType(Entity attacker, Entity defender) {
            if (specialAttacksRemaining > 0) {
                return ServerConstants.MAGIC_ICON;
            }
            if (ThreadLocalRandom.current().nextInt(0, 100) <= 5) {
                specialAttacksRemaining = 3;
                return ServerConstants.MAGIC_ICON;
            }
            return 0;
        }

        @Override
        public int calculateCustomDamage(Entity attacker, Entity defender, int entityAttackType) {
            if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
                Player defenderAsPlayer = (Player) defender;

                if (entityAttackType == ServerConstants.MAGIC_ICON) {
                    return defenderAsPlayer.currentCombatSkillLevel[Skill.HITPOINTS.getId()] / 3;
                }
            }
            return -1;
        }

        @Override
        public void onCustomAttack(Entity attacker, Entity defender) {
            if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
                Player defenderAsPlayer = (Player) defender;

                Npc attackerAsNpc = (Npc) attacker;

                if (attackerAsNpc.attackType == ServerConstants.MAGIC_ICON) {
                    specialAttacksRemaining--;

                    Position playerPosition = new Position(defenderAsPlayer);

                    Set<Position> surrounding = playerPosition.surrounding(1);

                    surrounding.add(playerPosition);

                    surrounding.forEach(position -> defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, position, 50, 100, 130, 43, 5, 0, 0, 0));
                    DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 31, -1,
                            p -> surrounding.stream().anyMatch(p2 -> p2.matches(p.getX(), p.getY(), p.getHeight())),
                            null, (d, p) -> p.gfx0(131)));
                } else {
                    DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, 0, 1, attackerAsNpc.getDefinition().maximumDamage, -1));
                }
            }
        }

        @Override
        public boolean isCustomAttack() {
            return true;
        }
    }
}
