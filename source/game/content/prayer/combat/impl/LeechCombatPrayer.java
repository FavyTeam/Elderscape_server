package game.content.prayer.combat.impl;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.prayer.combat.CombatPrayer;
import game.content.skilling.Skilling;
import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import utility.Misc;

/**
 * Handles leech curse prayers
 * 
 * @author 2012
 *
 */
public class LeechCombatPrayer extends CombatPrayer {

	/**
	 * The chance of leeching
	 */
	private static final int CHANCE = 8;

	/**
	 * The leech graphics
	 */
	private static final int[][] GRAPHICS = {{2232, 2231}, // attack
			{2238, 2236}, // range
			{2242, 2240}, // magic
			{2246, 2244}, // defence
			{2250, 2248}, // strength
			{2256, 2258}, // special
			{2254, 2252}, // energy
	};

	/**
	 * The skills
	 */
	private static final int[] SKILLS = {ServerConstants.ATTACK, ServerConstants.RANGED,
			ServerConstants.MAGIC, ServerConstants.DEFENCE, ServerConstants.STRENGTH};

	/**
	 * Represents the leech prayer
	 * 
	 * @param prayer the prayer
	 */
	public LeechCombatPrayer(int prayer) {
		super(prayer);
	}

	/**
	 * Gets the drain leech percentage
	 * 
	 * @param player the player
	 * @return the percentage
	 */
	private static int getDrainPercentage(Player player) {
		return Combat.wasUnderAttackByAnotherPlayer(player, 150_000) ? 10 : 26;
	}

	/**
	 * Gets the boost leech percentage
	 * 
	 * @param player the player
	 * @return the percentage
	 */
	private static int getBoostPercentage(Player player) {
		/*
		 * The original boost
		 */
		int boost = Combat.wasAttackingAnotherPlayer(player, 60_000) ? 5 : 10;
		/*
		 * Amulet of zealots boost
		 */
		if (Misc.arrayHasNumber(player.playerEquipment, 19_892)) {
			boost += 10;
		}
		return boost;
	}

	/**
	 * Universal leech method
	 * 
	 * @param player the attacker
	 * @param prayer the prayer id
	 * @param victim the victim
	 */
	private void leech(final Player player, final Player victim, final int prayer) {
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				/*
				 * Start
				 */
				if (container.getExecutions() == 1) {
					player.startAnimation(12_575);
					player.getPA().createPlayersProjectile2(player, victim, 50, 50, GRAPHICS[prayer][0],
							60, 40, -victim.getPlayerId() - 1, 53, 0, player.getHeight());

				}
				/*
				 * Leech
				 */
				else if (container.getExecutions() == getHitDelay(player, victim)) {
					leechEffect(player, victim, prayer >= 5 ? prayer : SKILLS[prayer]);
				}
				/*
				 * End
				 */
				if (container.getExecutions() == 5) {
					container.stop();
				}
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	/**
	 * Leeching a skill
	 * 
	 * @param player the player
	 * @param victim the victim
	 * @param skill the skill
	 */
	private void leechEffect(Player player, Player victim, int skill) {
		victim.gfx0(GRAPHICS[getPrayer()][1]);

		/*
		 * Leech special
		 */
		if (skill == 5) {
			/*
			 * Check amount
			 */
			if (victim.getSpecialAttackAmount() >= 2.0 && player.getSpecialAttackAmount() <= 8.0) {
				/*
				 * Decrease victim
				 */
				victim.setSpecialAttackAmount(victim.getSpecialAttackAmount() - 2.0, false);
				CombatInterface.addSpecialBar(victim, victim.getWieldedWeapon());
				victim.getPA()
						.sendMessage(player.getPlayerName() + " has leeched your Special Attack!");
				/*
				 * Increaseplayer
				 */
				player.setSpecialAttackAmount(player.getSpecialAttackAmount() + 2.0, false);
				CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
			}
		}
		/*
		 * Leech energy
		 */
		else if (skill == 6) {
			/*
			 * Leech special
			 */
			if (victim.runEnergy >= 10 && player.runEnergy <= 90) {
				/*
				 * Decrase victim
				 */
				victim.runEnergy -= 10;
				victim.getPA().sendMessage(player.getPlayerName() + " has leeched your Run Energy!");
				/*
				 * Increase player
				 */
				victim.runEnergy += 10;

			}
		} else {
			/*
			 * The boost to increase
			 */
			int increase = Skilling.getLevelForExperience(player.skillExperience[skill])
					/ getBoostPercentage(player);
			/*
			 * The max possible increase
			 */
			int max = Skilling.getLevelForExperience(player.skillExperience[skill]) + increase;
			/*
			 * Increases skill
			 */
			Skilling.increaseCombatSkill(player, skill, increase, max);

			/*
			 * The drain to Decrease
			 */
			int decrease = Skilling.getLevelForExperience(player.skillExperience[skill])
					/ getDrainPercentage(victim);
			/*
			 * Decreases skill
			 */
			Skilling.decreaseCombatSkill(player, skill, decrease);
			/*
			 * Notify
			 */
			victim.getPA().sendMessage(player.getPlayerName() + " has leeched your "
					+ ServerConstants.SKILL_NAME[skill] + " skill!");
		}
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
		 * Leech prayer
		 */
		leech(player, victim, getPrayer());
	}
}
