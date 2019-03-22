package game.npc.impl.tekton;

import core.ServerConstants;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.player.PlayerHandler;
import game.position.Position;
import game.position.PositionUtils;
import game.position.distance.DistanceAlgorithms;
import utility.Misc;

/**
 * Created by Jason MK on 2018-07-27 at 4:01 PM
 */
public class TektonCombatStrategy extends NpcCombatStrategy {

    private TektonAttackType attackType = TektonAttackType.SLASH;

    private Position targetPosition;

    @Override
    public boolean canAttack(Entity attacker, Entity defender) {
        return attacker.getAttributes().getOrDefault(Tekton.STATE) != TektonState.HAMMERING;
    }

    @Override
    public boolean canBeAttacked(Entity attacker, Entity defender) {
        return defender.getAttributes().getOrDefault(Tekton.STATE) != TektonState.HAMMERING;
    }

    @Override
    public int calculateAttackType(Entity attacker, Entity defender) {
        if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
            Npc attackerAsNpc = (Npc) attacker;

            if (attackerAsNpc.getAttributes().getOrDefault(Tekton.STATE) == TektonState.WALKING_TO_ANVIL
                || attackerAsNpc.getAttributes().getOrDefault(Tekton.STATE) == TektonState.WALKING_TO_TARGET) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public void onCustomAttack(Entity attacker, Entity defender) {
        if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
            Npc attackerAsNpc = (Npc) attacker;

            Player defenderAsPlayer = (Player) defender;

            if (attackerAsNpc.attackType == ServerConstants.MELEE_ICON) {
                if (targetPosition != null) {
                    DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, 0, 1, NpcHandler.
                            calculateNpcMeleeDamage(attackerAsNpc, defenderAsPlayer, -1, attackerAsNpc.getDefinition().maximumDamage),
                            -1, p -> targetPosition.distanceTo(p.getX(), p.getY()) < 2, null, null));
                }
                attacker.getAttributes().increase(Tekton.CONSECUTIVE_ATTACKS);
            }
        }
    }

    @Override
    public int calculateCustomDamageTaken(Entity attacker, Entity defender, int damage, int attackType) {
        if (attackType == ServerConstants.MELEE_ICON) {
            return damage;
        }
        return 0;
    }

    @Override
    public int calculateCustomCombatDefence(Entity attacker, Entity defender, int defence, int attackType) {
        if (defender.getAttributes().is(Tekton.ENRAGED)) {
            return defence + 200;
        }
        return defence;
    }

    @Override
    public void onAttackCooldown(Entity attacker, int cyclesRemaining) {
        if (attacker.getType() == EntityType.NPC) {
            Npc attackerAsNpc = (Npc) attacker;

            if (attackerAsNpc.oldIndex <= 0) {
                return;
            }
            if (attackerAsNpc.getAttributes().getOrDefault(Tekton.STATE) != TektonState.ATTACKING) {
                return;
            }
            Player defenderAsPlayer = PlayerHandler.players[attackerAsNpc.oldIndex];

            if (defenderAsPlayer == null) {
                defenderAsPlayer = attackerAsNpc.getLocalPlayers().stream().findAny().orElse(null);

                if (defenderAsPlayer == null) {
                    // should we reset state?
                    return;
                }
                attackerAsNpc.setKillerId(defenderAsPlayer.getPlayerId());
            }
            if (attackerAsNpc.getLocalPlayers().stream().noneMatch(local ->
                    PositionUtils.withinDistance(attackerAsNpc, new Position(local), 1, DistanceAlgorithms.EUCLIDEAN))) {
                return;
            }
            if (cyclesRemaining == 2) {
                targetPosition = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());
                attackerAsNpc.setFacingEntityDisabled(true);
                attackerAsNpc.turnNpc(targetPosition.getX(), targetPosition.getY());
            }
        }
    }

    @Override
    public int getCustomAttackAnimation(Entity attacker) {
        if (attacker instanceof Npc) {
            Npc attackerAsNpc = (Npc) attacker;

            if (attackerAsNpc.attackType == 1) {
                return 65535;
            }
            attackType = Misc.random(TektonAttackType.values());

            if (attackType == TektonAttackType.SLASH) {
                return attackerAsNpc.getAttributes().is(Tekton.ENRAGED) ? Tekton.ENRAGED_SLASH_ANIMATION : Tekton.SLASH_ANIMATION;
            } else if (attackType == TektonAttackType.SMASH) {
                return attackerAsNpc.getAttributes().is(Tekton.ENRAGED) ? Tekton.ENRAGED_SMASH_ANIMATION : Tekton.SMASH_ANIMATION;
            } else if (attackType == TektonAttackType.POKE) {
                return attackerAsNpc.getAttributes().is(Tekton.ENRAGED) ? Tekton.ENRAGED_POKE_ANIMATION : Tekton.POKE_ANIMATION;
            }
        }
        return 65535;
    }

    @Override
    public boolean isCustomAttack() {
        return true;
    }

    @Override
    public boolean canFindTarget(Entity attacker) {
        TektonState state = attacker.getAttributes().getOrDefault(Tekton.STATE);

        return state != TektonState.HAMMERING && state != TektonState.WALKING_TO_ANVIL && state != TektonState.WALKING_TO_TARGET;
    }

    @Override
    public boolean performsBlockAnimation() {
        return false;
    }
}
