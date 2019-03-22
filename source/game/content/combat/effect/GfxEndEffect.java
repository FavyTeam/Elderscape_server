package game.content.combat.effect;

import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.player.Player;

public class GfxEndEffect implements EntityDamageEffect {

	private int gfxId, height;

	public GfxEndEffect(int gfxId, int height) {
		this.gfxId = gfxId;
		this.height = height;
	}

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player victim = (Player) damage.getTarget();
		victim.gfx(gfxId, height);

	}
}
