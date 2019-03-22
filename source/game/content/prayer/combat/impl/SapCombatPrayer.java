package game.content.prayer.combat.impl;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.prayer.combat.CombatPrayer;
import game.content.skilling.Skilling;
import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import utility.Misc;

/**
 * Handles sap combat prayer
 * 
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>
 * 
 */
public class SapCombatPrayer extends CombatPrayer {

	/**
	 * The chance of sapping
	 */
	private static final int CHANCE = 4;

	/**
	 * Represents sap prayer
	 * 
	 * @param prayer the prayer
	 */
	public SapCombatPrayer(int prayer) {
		super(prayer);
	}

	/**
	 * Gets the sap percentage
	 * 
	 * @param player
	 *            the player
	 * @return the percentage
	 */
	private static int getPercentage(Player player) {
		return Combat.wasUnderAttackByAnotherPlayer(player, 90_000) ? 20
				: 10;
	}

	/**
	 * The sap graphics
	 */
	private static final int[] GRAPHICS = {2214, // attack
			2217, // ranged
			2220, // mage
			2223 // spirit
	};

	/**
	 * The skills
	 */
	private static final int[] SKILLS = {ServerConstants.ATTACK, ServerConstants.RANGED,
			ServerConstants.MAGIC, ServerConstants.SUMMONING};

	/**
	 * Universal sap prayer method
	 * 
	 * @param player
	 *            the player
	 * @param victim
	 *            the victim
	 * @param prayer
	 *            the prayer
	 * @param skills
	 *            the skills being lowered
	 */
	public void sap(final Player player, final Player victim,
			final int prayer) {
		/*
		 * Checks for victim null
		 */
		if (victim == null) {
			return;
		}
		/*
		 * The graphic
		 */
		final int graphic = GRAPHICS[prayer];
		/*
		 * Gfx
		 */
		player.startAnimation(12569);
		player.gfx0(graphic);
		/*
		 * Projectile
		 */
		player.getPA().createPlayersProjectile2(player, victim, 50, 50, graphic + 1, 60, 40,
				-victim.getPlayerId() - 1, 53, 0, player.getHeight());
		/*
		 * Effect
		 */
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				/*
				 * Decrease skills
				 */
				Skilling.decreaseCombatSkill(victim, SKILLS[prayer],
						victim.getCurrentCombatSkillLevel(SKILLS[prayer]) / getPercentage(victim));
				/*
				 * End gfx
				 */
				victim.gfx0(graphic + 2);
				container.stop();
			}

			@Override
			public void stop() {

			}
		}, getHitDelay(player, victim));
	}

	@Override
	public void execute(Player player, Player victim, int damage) {
		/*
		 * Invalid target
		 */
		if (victim == null) {
			return;
		}
		/*
		 * Dead
		 */
		if (victim.currentCombatSkillLevel[ServerConstants.HITPOINTS] < 1 || victim.dead) {
			return;
		}
		/*
		 * Chance
		 */
		if (Misc.random(CHANCE) != 1) {
			return;
		}
		/*
		 * Sap
		 */
		sap(player, victim, getPrayer());
	}
}
