package game.npc.impl.crazy_archaeologist;

import com.google.common.collect.ImmutableList;
import core.ServerConstants;
import game.entity.Entity;
import game.entity.EntityType;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-06-06 at 1:15 PM
 */
public class DerangedArchaeologistCombatStrategy extends CrazyArchaeologistCombatStrategy {

	public static final List<String> DERANGED = ImmutableList.copyOf(Arrays.asList(
			"Round and round and round and round!",
			"The plants! They're alive!",
			"They came from the ground! They came from the ground!!!",
			"The doors won't stay closed forever!",
			"They're cheering! Why are they cheering?",
			"Time is running out! She will rise again!",
			"No hiding!"));

	/**
	 * The custom damage that should be dealt, or -1 if the parent damage should be taken into consideration.
	 *
	 * @param attacker the attacker dealing the damage.
	 * @param defender the defender taking the damage.
	 * @param entityAttackType
	 * @return the custom calculation of damage, or -1 if the parent damage should be used instead.
	 */
	@Override
	public int calculateCustomDamage(Entity attacker, Entity defender, int entityAttackType) {
		if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
			Npc npc = (Npc) attacker;

			if (npc.attackType == ServerConstants.MELEE_ICON) {
				return ThreadLocalRandom.current().nextInt(8, 29);
			}
		}
		return -1;
	}

	/**
	 * Determines the attack type of the entity, representing what style of combat is being used.
	 *
	 * @param attacker
	 * @param defender
	 * @return the attack type, or -1 if none can be found.
	 */
	@Override
	public int calculateAttackType(Entity attacker, Entity defender) {
		if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
			Player player = (Player) defender;

			Npc npc = (Npc) attacker;

			if (player.distanceToPoint(npc.getX(), npc.getY()) <= 1) {
				return ServerConstants.MELEE_ICON;
			}
		}
		return super.calculateAttackType(attacker, defender);
	}

	/**
	 * References when {@link #isCustomAttack()} returns true, in which case we're responsible the
	 * regular combat process.
	 *
	 * @param attacker the attacker.
	 * @param defender the defender of the attack.
	 */
	@Override
	public void onCustomAttack(Entity attacker, Entity defender) {
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		if (attackerAsNpc.attackType == ServerConstants.MELEE_ICON) {
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 28, -1));
			return;
		}
		super.onCustomAttack(attacker, defender);
	}

	@Override
	protected String getSpecialMessage() {
		return "Learn to Read!";
	}

	@Override
	protected List<String> getSpeakableText() {
		return DERANGED;
	}
}
