package game.content.combat.effect;

import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.entity.Entity;
import game.npc.Npc;
import game.player.Player;

/**
 * Handles the korasi sword special attack
 * 
 * @author 2012
 *
 */
public class KorasiSwordSpecialEffect implements EntityDamageEffect {

	/**
	 * The end gfx
	 */
	private static final int VICTIM_GFX = 2_795;

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		/*
		 * No damage
		 */
		if (damage.getDamage() == 0) {
			return;
		}
		/*
		 * The attacker
		 */
		Player attacker = (Player) damage.getSender();
		/*
		 * Invalid attacker
		 */
		if (attacker == null) {
			return;
		}
		/*
		 * The victim
		 */
		Player victim = (Player) damage.getTarget();
		/*
		 * The gfx
		 */
		victim.gfx0(VICTIM_GFX);
	}

	/**
	 * Performing the special on npc
	 * 
	 * @param player the player
	 * @param npc the npc
	 */
	public static void onNpc(Player player, Npc npc) {
		npc.gfx0(VICTIM_GFX);
	}
}
