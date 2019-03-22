package game.content.prayer.combat.impl;

import core.ServerConstants;
import game.content.combat.vsnpc.CombatNpc;
import game.content.prayer.combat.CombatPrayer;
import game.npc.Npc;
import game.player.Player;

/**
 * Handles the deflecting curse prayer
 * 
 * @author 2012
 *
 */
public class DeflectCombatPrayer extends CombatPrayer {

	/**
	 * The deflect prayer
	 * 
	 * @param prayer
	 *            the prayer
	 */
	public DeflectCombatPrayer(int prayer) {
		super(prayer);
	}

	/**
	 * The deflect graphics
	 */
	private static final int[] GRAPHICS = { 2227, 2228, 2229, 2230, };

	/**
	 * Performing the deflecting
	 * 
	 * @param player
	 *            the attacker
	 */
	private void deflect(Player player) {
		/*
		 * The effect
		 */
		player.startAnimation(12573);
		player.gfx0(GRAPHICS[getPrayer()]);
	}

	@Override
	public int getDeflect(Player player, Player victim, int hit) {
		/*
		 * The damage
		 */
		int damage = (int) (hit * 0.20);
		/*
		 * The effect
		 */
		if (damage > 1) {
			victim.dealDamage(damage);
			deflect(player);
		}
		/*
		 * The new hit
		 */
		return hit - (hit * 40 / 100);
	}

	@Override
	public int getDeflect(Player player, Npc victim, int hit) {
		/*
		 * The damage
		 */
		int damage = (int) (hit * 0.20);
		/*
		 * The effect
		 */
		if (damage > 0) {
			CombatNpc.applyHitSplatOnNpc(player, victim, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR,
                    ServerConstants.NO_ICON, 1);
			deflect(player);
		}
		/*
		 * The new hit
		 */
		return 0;
	}
}
