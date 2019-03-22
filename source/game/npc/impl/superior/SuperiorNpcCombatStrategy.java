package game.npc.impl.superior;

import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.player.Player;

/**
 * Created by Jason MK on 2018-07-06 at 3:03 PM
 */
public class SuperiorNpcCombatStrategy extends NpcCombatStrategy {

    @Override
    public boolean canBeAttacked(Entity attacker, Entity defender) {
        String owner = defender.getAttributes().getOrDefault(SuperiorNpc.SPAWNED_FOR);

        if (owner == null) {
            return false;
        }
        if (attacker.getType() == EntityType.PLAYER) {
            Player playerAttacker = (Player) attacker;

            if (!playerAttacker.getPlayerName().equalsIgnoreCase(owner)) {
                playerAttacker.getPA().sendMessage("This is not your superior monster to attack.");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canAttack(Entity attacker, Entity defender) {
        if (defender.getType() == EntityType.PLAYER) {
            String owner = attacker.getAttributes().getOrDefault(SuperiorNpc.SPAWNED_FOR);

            if (owner == null) {
                return false;
            }
            if (defender.getType() == EntityType.PLAYER) {
                Player playerDefender = (Player) defender;

                return playerDefender.getPlayerName().equalsIgnoreCase(owner);
            }
        }
        return false;
    }

    @Override
    public void onDamageDealt(Entity attacker, Entity defender, int damage, int attackType) {
        attacker.getAttributes().put(SuperiorNpc.LAST_INTERACTION, System.currentTimeMillis());
    }

    @Override
    public void onDamageTaken(Entity attacker, Entity defender, int damage, int entityAttackType) {
        attacker.getAttributes().put(SuperiorNpc.LAST_INTERACTION, System.currentTimeMillis());
    }
}
