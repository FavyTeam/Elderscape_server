package game.npc.impl.vorkath;

import game.content.combat.CombatConstants;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-02-07 at 11:37 PM
 */
public class ZombifiedSpawnCombatStrategy extends NpcCombatStrategy {

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		return true;
	}

	/**
	 * Retrieves the custom emote for the attacking entity, or -1 if there is no custom emote.
	 *
	 * @param attacker the entity making the attack animation.
	 * @return the attack emote, or -1 by default for no custom attack emote.
	 */
	@Override
	public int getCustomAttackAnimation(Entity attacker) {
		return 65535;
	}

	/**
	 * Determines if we're going to handle the entire attack process our self.
	 *
	 * @return {@code true} if it's a custom attack, by default, false.
	 */
	@Override
	public boolean isCustomAttack() {
		return true;
	}

	/**
	 * The custom damage to be taken from another entity, or -1 if no custom damage is to be calculated.
	 *
	 * @param attacker the entity dealing the damage.
	 * @param defender the entity taking the damage.
	 * @param damage the amount of damage calculated before being modified for this sequence of combat.
	 * @param attackType the type of attacking the damage was for.
	 * @return the custom damage taken, or -1 if no custom damage is calculated.
	 */
	@Override
	public int calculateCustomDamageTaken(Entity attacker, Entity defender, int damage, int attackType) {
		if (attacker.getType() != EntityType.PLAYER) {
			return -1;
		}
		Player attackerAsPlayer = (Player) attacker;

		if (attackerAsPlayer.getOldSpellId() == -1) {
			return -1;
		}
		if (attackerAsPlayer.isUsingMagic() && CombatConstants.MAGIC_SPELLS[attackerAsPlayer.getOldSpellId()][0] == 1171) {
			return 38;
		}
		return -1;
	}
}
