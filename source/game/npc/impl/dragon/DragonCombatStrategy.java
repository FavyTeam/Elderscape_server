package game.npc.impl.dragon;

import core.ServerConstants;
import game.content.combat.Combat;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.npc.data.NpcDefinition;
import game.player.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-06-18 at 2:36 PM
 */
public class DragonCombatStrategy extends NpcCombatStrategy {

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
			Npc npc = (Npc) attacker;

			Player player = (Player) defender;

			NpcDefinition definition = NpcDefinition.getDefinitions()[npc.npcType];

			if (definition != null) {
				if (player.distanceToPoint(npc.getX(), npc.getY()) <= definition.size + 1) {
					if (ThreadLocalRandom.current().nextInt(0, 100) <= 80) {
						return ServerConstants.MELEE_ICON;
					}
				}
			}
		}
		return ServerConstants.DRAGONFIRE_ATTACK;
	}

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

			Player player = (Player) defender;

			NpcDefinition definition = NpcDefinition.getDefinitions()[npc.npcType];

			if (definition != null) {
				if (entityAttackType == ServerConstants.DRAGONFIRE_ATTACK) {
					int antifire = Combat.antiFire(player, false, true);

					return antifire == 0 ? ThreadLocalRandom.current().nextInt(0, 61) :
							antifire == 1 ? ThreadLocalRandom.current().nextInt(0, 41) : 0;
				}
			}
		}
		return -1;
	}

	/**
	 * References when {@link #isCustomAttack()} returns true, in which case we're responsible the
	 * regular combat process.
	 *
	 * @param attacker the attacker.
	 * @param defender the defender.
	 */
	@Override
	public void onCustomAttack(Entity attacker, Entity defender) {
		if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
			Npc npc = (Npc) attacker;

			Player player = (Player) defender;

			NpcDefinition definition = NpcDefinition.getDefinitions()[npc.npcType];

			if (definition != null) {
				if (npc.attackType == ServerConstants.MELEE_ICON) {
					DamageQueue.add(new Damage(player, npc, npc.attackType, 1, definition.maximumDamage, -1));
				} else if (npc.attackType == ServerConstants.DRAGONFIRE_ATTACK) {
					DamageQueue.add(new Damage(player, npc, npc.attackType, 3, 60, -1));
					npc.gfx100(1);
				}
			}
		}
	}

	/**
	 * Retrieves the custom emote for the attacking entity, or -1 if there is no custom emote.
	 *
	 * @param attacker the entity making the attack animation.
	 * @return the attack emote, or -1 by default for no custom attack emote.
	 */
	@Override
	public int getCustomAttackAnimation(Entity attacker) {
		if (attacker.getType() == EntityType.NPC) {
			Npc npc = (Npc) attacker;

			if (npc.attackType == ServerConstants.MELEE_ICON) {
				if (ThreadLocalRandom.current().nextBoolean()) {
					return 91;
				}
				return 80;
			}
			return 81;
		}
		return -1;
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
	 * Determines if a boss should perform a block operation.
	 *
	 * @return true by default.
	 */
	@Override
	public boolean performsBlockAnimation() {
		return false;
	}
}
