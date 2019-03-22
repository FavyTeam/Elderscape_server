package game.content.combat.effect;

import core.GameType;
import core.ServerConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.consumable.RegenerateSkill;
import game.content.skilling.Skilling;
import game.player.Player;
import utility.Misc;

/**
 * Handles the robe effect
 * 
 * @author 2012
 *
 */
public class AkrisaeRobeEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		/*
		 * The player
		 */
		Player player = (Player) damage.getSender();
		/*
		 * Invalid player
		 */
		if (player == null) {
			return;
		}
		/*
		 * Not wearing
		 */
		if (!fullAkrisae(player)) {
			return;
		}
		/*
		 * The victim
		 */
		Player victim = (Player) damage.getTarget();
		/*
		 * Invalid victim
		 */
		if (victim == null) {
			return;
		}
		/*
		 * No chance
		 */
		if (!Misc.hasPercentageChance(60)) {
			return;
		}
		/*
		 * The drain
		 */
		int drain = damage.getDamage() / 3;
		/*
		 * To add
		 */
		int add = damage.getDamage() / 5;
		/*
		 * Decreases
		 */
		Skilling.decreaseCombatSkill(victim, ServerConstants.PRAYER, drain);
		/*
		 * Increases
		 */
		Skilling.increaseCombatSkill(player, ServerConstants.PRAYER, add, 0);
	}

	/**
	 * Handles full akrisae
	 * 
	 * @param player the player
	 * @return full akrisae
	 */
	public static boolean fullAkrisae(Player player) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		return (player.playerEquipment[ServerConstants.HEAD_SLOT] == 21_736
				&& player.playerEquipment[ServerConstants.WEAPON_SLOT] == 21_744
				&& player.playerEquipment[ServerConstants.BODY_SLOT] == 21_752
				&& player.playerEquipment[ServerConstants.LEG_SLOT] == 21_760);
	}
}
