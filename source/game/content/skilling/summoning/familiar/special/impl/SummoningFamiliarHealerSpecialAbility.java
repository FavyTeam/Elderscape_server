package game.content.skilling.summoning.familiar.special.impl;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.content.skilling.summoning.familiar.special.SummoningFamiliarSpecialAbility;
import game.player.Player;

/**
 * Represents the healer summoning familiar special ability
 *
 * @author 2012
 */
public class SummoningFamiliarHealerSpecialAbility implements SummoningFamiliarSpecialAbility {

	/**
	 * The type of healing
	 */
	public enum HealingType {
		/*
		 * Healing through cycle
		 */
		CYCLE,
		/*
		 * Healing through percentange
		 */
		PERCENTANGE,
		/*
		 * Healing directely
		 */
		DIRECT
	}


	/**
	 * The amount to heal
	 */
	private int healAmount;

	/**
	 * The type
	 */
	private HealingType type;

	/**
	 * Represents the familiar healing ability
	 *
	 * @param heal the amount to heal
	 * @param type the type
	 */
	public SummoningFamiliarHealerSpecialAbility(int heal, HealingType type) {
		this.setHealAmount(heal);
		this.setType(type);
	}

	@Override
	public boolean sendScroll(Player player) {
		/*
		 * Dead
		 */
		if (player.getDead()) {
			return false;
		}
		/*
		 * Directly healing
		 */
		if (getType().equals(HealingType.DIRECT)) {
			player.addToHitPoints(getHealAmount());
			return true;
		} else if (getType().equals(HealingType.PERCENTANGE)) {
			/*
			 * The hitpoints
			 */
			int hitpoints = Skilling.getLevelForExperience(player.skillExperience[ServerConstants.HITPOINTS]);
			/*
			 * The percetange to heal
			 */
			int amount = (int) (hitpoints * 0.15);
			/*
			 * The maximum
			 */
			int max = (int) (hitpoints * 0.15);
			/*
			 * Fixes
			 */
			if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) + amount > max) {
				amount = max - player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
			}
			/*
			 * No available
			 */
			if (amount == 0) {
				player.getPA().sendMessage("You already have max possible hitpoints.");
				return false;
			}
			/*
			 * Heals
			 */
			player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += amount;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
			return true;
		}
		return false;
	}

	@Override
	public void cycle(Player player) {
		/*
		 * Cycle healing
		 */
		if (getType().equals(HealingType.CYCLE)) {
			player.addToHitPoints(getHealAmount());
		}
	}

	@Override
	public double getSkillBonus(Player player, int skill) {
		return 1.0;
	}

	/**
	 * Sets the healAmount
	 *
	 * @return the healAmount
	 */
	public int getHealAmount() {
		return healAmount;
	}

	/**
	 * Sets the healAmount
	 *
	 * @param healAmount the healAmount
	 */
	public void setHealAmount(int healAmount) {
		this.healAmount = healAmount;
	}

	/**
	 * Sets the type
	 *
	 * @return the type
	 */
	public HealingType getType() {
		return type;
	}

	/**
	 * Sets the type
	 *
	 * @param type the type
	 */
	public void setType(HealingType type) {
		this.type = type;
	}

	@Override
	public int getPercentageRequired() {
		return 0;
	}
}
