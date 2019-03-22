package game.content.combat.effect;

import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.player.Player;
import game.player.movement.Movement;

public class ZamorakGodswordSpecialEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		if (damage.getDamage() > 0 && !victim.isCombatBot() && victim.canBeFrozen()) {
			victim.setFrozenLength(20000);
			victim.frozenBy = attacker.getPlayerId();
			victim.gfx0(369);
			victim.getPA().sendMessage("<col=ff0000>You have been frozen!");
			attacker.getPA().sendMessage("You have frozen your target.");
			if (!victim.bot) {
				Movement.stopMovement(victim);
			}
			Combat.resetPlayerAttack(victim);
		}
	}

}
