package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.combat.vsplayer.range.RangedFormula;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;

/**
 * Created by Jason MK on 2018-08-28 at 6:01 PM
 */
public class ChinchompaEffect implements EntityDamageEffect<Player, Player> {

    @Override
    public EntityDamage<Player, Player> onCalculation(EntityDamage<Player, Player> damage) {
        int affected = 0;

        damage.setSendXpDrop(false);

        int totalDamage = damage.getDamage();

        //TODO change to local players so i dont have to test on bots
        for (Player player : PlayerHandler.players) {
            if (player == null || player == damage.getTarget() || player == damage.getSender()) {
                continue;
            }

            if (player.distanceToPoint(damage.getTarget().getX(), damage.getTarget().getY()) <= 1
                    && AttackPlayer.hasAttackRequirements(damage.getSender(), player, false, false)
                    && Area.inMulti(player.getX(), player.getY())) {

                EntityDamage<Player, Player> chinDamage = new EntityDamage<>(player, damage.getSender(),
                        RangedFormula.calculateRangedDamage(damage.getSender(), player, false), 4, EntityDamageType.RANGED, 0, false, false);

                chinDamage.setSendXpDrop(false);

				chinDamage.addEffect(new GfxEndEffect(157, 100));

                damage.getSender().getIncomingDamageOnVictim().add(chinDamage);
				//AttackPlayer.successfulAttackInitiated(attacker, loop);

                totalDamage += chinDamage.getDamage();
            }
            if (++affected > 9) {
                break;
            }
        }

        if (totalDamage > 0) {
            Combat.addCombatExperience(damage.getSender(), ServerConstants.RANGED_ICON, totalDamage);
        }
        return damage;
    }


    @Override
    public void onApply(EntityDamage<Player, Player> damage) {

    }
}
