package game.content.combat.effect;

import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.combat.vsplayer.range.RangedAmmoUsed;
import game.player.Player;

public class DropAmmoEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		RangedAmmoUsed.dropAmmo(attacker, victim.getX(), victim.getY(), victim.getHeight());
	}
}
