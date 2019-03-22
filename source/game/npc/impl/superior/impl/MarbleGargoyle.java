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
import game.player.movement.Movement;
import game.type.GameTypeIdentity;
import utility.Misc;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MK on 2018-07-16 at 10:31 AM
 */
@CustomNpcComponent(identities = @GameTypeIdentity(type = GameType.OSRS, identity = 7407))
public class MarbleGargoyle extends SuperiorNpc {

    private final EntityCombatStrategy strategy = new MarbleGargoyleCombatStrategy();

    public MarbleGargoyle(int npcId, int npcType) {
        super(npcId, npcType);
    }

    @Override
    public int parent() {
        return 412;
    }

    @Override
    public Npc copy(int index) {
        return new MarbleGargoyle(index, npcType);
    }

    @Override
    public EntityCombatStrategy getCombatStrategyOrNull() {
        return strategy;
    }

    private static class MarbleGargoyleCombatStrategy extends SuperiorNpcCombatStrategy {

        enum AttackType {
            MELEE,

            RANGE,

            SPECIAL
        }

        private AttackType attackType = AttackType.MELEE;

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

            if (random < 50) {
                attackType = AttackType.MELEE;
                return ServerConstants.MELEE_ICON;
            } else if (random < 90) {
                attackType = AttackType.RANGE;
                return ServerConstants.RANGED_ICON;
            }
            attackType = AttackType.SPECIAL;
            return ServerConstants.MAGIC_ICON;
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
                Npc npcAttacker = (Npc) attacker;

                Player playerDefender = (Player) defender;

                if (attackType == AttackType.MELEE) {
                    DamageQueue.add(new Damage(playerDefender, npcAttacker, ServerConstants.MELEE_ICON, 1, npcAttacker.getDefinition().maximumDamage));
                } else if (attackType == AttackType.RANGE) {
                    playerDefender.getPA().createPlayersProjectile(npcAttacker, playerDefender, 50, 100, 679, 43, 20, Misc.lockon(playerDefender), 0, 0);
                    DamageQueue.add(new Damage(playerDefender, npcAttacker, ServerConstants.RANGED_ICON, 3, npcAttacker.getDefinition().rangedMaximumDamage));
                } else if (attackType == AttackType.SPECIAL) {
                    Position target = new Position(playerDefender);

                    playerDefender.getPA().createPlayersProjectile(npcAttacker, target, 50, 90, 142, 30, 20, 0, 0, 0);
                    DamageQueue.add(new Damage(playerDefender, npcAttacker, ServerConstants.MAGIC_ICON, 4, 38, -1, p ->
                            target.matches(playerDefender.getX(), playerDefender.getY(), playerDefender.getHeight()), (f, s) -> {
                        s.setFrozenLength(TimeUnit.SECONDS.toMillis(5));
                        s.frozenBy = -1;
                        Movement.stopMovement(s);
                        s.getPA().sendMessage("You have been trapped in stone!");
                    }, null));
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
            return attackType == AttackType.SPECIAL ? 7815 : 7811;
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

        /**
         * Determines if a boss should perform a block operation.
         *
         * @return true by default.
         */
        @Override
        public boolean performsBlockAnimation() {
            return false;
        }
    }
}
