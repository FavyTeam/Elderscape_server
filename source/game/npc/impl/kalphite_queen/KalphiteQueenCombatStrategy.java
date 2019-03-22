package game.npc.impl.kalphite_queen;

import core.ServerConstants;
import game.position.Position;
import game.content.combat.Combat;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.npc.data.NpcDefinition;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-01-12 at 10:06 AM
 */
public class KalphiteQueenCombatStrategy implements EntityCombatStrategy {

	private final NpcDefinition firstFormDefinition = NpcDefinition.getDefinitions()[KalphiteQueenPhase.UNTRANSFORMED.getId()];

	private final NpcDefinition secondFormDefinition = NpcDefinition.getDefinitions()[KalphiteQueenPhase.TRANSFORMED.getId()];

	/**
	 * Determines whether or not we want to use the new mechanics, or the original for {@code false}.
	 */
	private static final boolean NEW_MECHANICS = true;

	/**
	 * The phase that this kalphite queen is currently in.
	 */
	private KalphiteQueenPhase phase = KalphiteQueenPhase.UNTRANSFORMED;

	/**
	 * Transforms the given npc to the
	 */
	private void transform(Npc npc) {
		if (phase != KalphiteQueenPhase.UNTRANSFORMED) {
			throw new IllegalStateException("Phase must be UNTRANSFORMED, is currently: " + phase);
		}
		if (npc.getTransformOrId() == KalphiteQueenPhase.TRANSFORMED.getId()) {
			throw new IllegalStateException("The npc is already transformed.");
		}
		phase = KalphiteQueenPhase.TRANSFORMING;

		CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (container.getExecutions() == 1) {
					npc.requestAnimation(6242);
					npc.heal(255);
				} else if (container.getExecutions() == 4) {
					npc.gfx0(1055);
				} else if (container.getExecutions() >= 8) {
					container.stop();
					phase = KalphiteQueenPhase.TRANSFORMED;
					npc.transform(KalphiteQueenPhase.TRANSFORMED.getId());
				}
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		return phase != KalphiteQueenPhase.TRANSFORMING;
	}

	/**
	 * Determines if the defender can be attacked by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @return {@code true} if the defender can be attacked, and {@code true} by default.
	 */
	@Override
	public boolean canBeAttacked(Entity attacker, Entity defender) {
		return phase != KalphiteQueenPhase.TRANSFORMING;
	}

	/**
	 * Determines the attack type of the entity, representing what style of combat is being used.
	 *
	 * @return the attack type, or -1 if none can be found.
	 */
	@Override
	public int calculateAttackType(Entity attacker, Entity defender) {
		if (attacker.getType() != EntityType.NPC || defender.getType() != EntityType.PLAYER) {
			return -1;
		}
		int randomResult = ThreadLocalRandom.current().nextInt(0, 100);

		if (randomResult <= 20) {
			return ServerConstants.MAGIC_ICON;
		}
		if (randomResult <= 50) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			if (attackerAsNpc.distanceTo(defenderAsPlayer.getX(), defenderAsPlayer.getY()) <= 3) {
				return ServerConstants.MELEE_ICON;
			}
		}
		return ServerConstants.RANGED_ICON;
	}

	/**
	 * Referenced when the defender is damaged by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @param damage the damage being dealt.
	 * @param entityAttackType
	 */
	@Override
	public void onDamageTaken(Entity attacker, Entity defender, int damage, int entityAttackType) {

	}

	/**
	 * References when {@link #isCustomAttack()} returns true, in which case we're responsible the
	 * regular combat process.
	 *
	 * @param attacker the attacker.
	 * @param defender
	 */
	@Override
	public void onCustomAttack(Entity attacker, Entity defender) {
		if (attacker.getType() != EntityType.NPC || defender.getType() != EntityType.PLAYER) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		int attackType = attackerAsNpc.attackType;

		if (attackType == ServerConstants.MAGIC_ICON) {
			attackerAsNpc.gfx0(attackerAsNpc.getTransformOrId() == KalphiteQueenPhase.TRANSFORMED.getId() ? 279 : 278);
			if (NEW_MECHANICS) {
				damageAllInArea(attackerAsNpc);
			} else {
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 95, 280, 43, 0, -defenderAsPlayer.getPlayerId() - 1, 45, 30);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 31, -1, null, (d, p) -> p.gfx0(281)));
			}
		} else if (attackType == ServerConstants.RANGED_ICON) {
			defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 95, 473, 43, 0, -defenderAsPlayer.getPlayerId() - 1, 45, 30);
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 4, 31, -1));
		} else if (attackType == ServerConstants.MELEE_ICON) {
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 2, 31, -1));
		}
	}

	private void damageAllInArea(Npc attacker) {
		List<Player> affected = PlayerHandler.getPlayers(player -> player.distanceToPoint(attacker.getX(), attacker.getY()) <= 8 && player.getHeight() == attacker.getHeight());

		affected.forEach(player -> {
			Position damageLocation = new Position(player.getX(), player.getY(), player.getHeight());

			player.getPA().createPlayersProjectile(attacker, player, 50, 95, 280, 43, 0, 0, 45, 30);
			player.getPA().createPlayersStillGfx(281, damageLocation.getX(), damageLocation.getY(), damageLocation.getZ(), 95);
			DamageQueue.add(new Damage(player, attacker, ServerConstants.MAGIC_ICON, 4, 31, -1, p ->
					                                                                                    p.getX() == damageLocation.getX() && p.getY() == damageLocation.getY()
					                                                                                    && p.getHeight() == damageLocation.getZ(), null));
		});

	}

	/**
	 * The custom damage to be taken from another entity, or -1 if no custom damage is to be calculated.
	 *
	 * @param attacker the entity dealing the damage.
	 * @param defender the entity taking the damage.
	 * @param damage the amount of damage calculated before being modified for this sequence of combat.
	 * @return the custom damage taken, or -1 if no custom damage is calculated.
	 */
	@Override
	public int calculateCustomDamageTaken(Entity attacker, Entity defender, int damage, int attackType) {
		if (defender.getType() != EntityType.NPC || attacker.getType() != EntityType.PLAYER) {
			return damage;
		}
		if (phase == KalphiteQueenPhase.UNTRANSFORMED) {
			if (attackType == ServerConstants.MAGIC_ICON || attackType == ServerConstants.RANGED_ICON) {
				return 0;
			}
			Npc kalphite = (Npc) defender;

			if (kalphite.getCurrentHitPoints() - damage <= 0) {
				damage = Math.max(0, kalphite.getCurrentHitPoints() - 1);
				transform(kalphite);
			}
			return damage;
		} else if (phase == KalphiteQueenPhase.TRANSFORMED) {
			if (attackType == ServerConstants.MELEE_ICON) {
				Player playerAttacker = (Player) attacker;

				if (Combat.wearingFullVerac(playerAttacker)) {
					return damage + 1;
				}
				return 0;
			}
		} else if (phase == KalphiteQueenPhase.TRANSFORMING) {
			return 0;
		}
		return damage;
	}

	/**
	 * Retrieves the custom emote for the attacking entity, or -1 if there is no custom emote.
	 *
	 * @param attacker the entity making the attack animation.
	 * @return the attack emote, or -1 by default for no custom attack emote.
	 */
	@Override
	public int getCustomAttackAnimation(Entity attacker) {
		if (attacker.getType() != EntityType.NPC) {
			return -1;
		}
		Npc kalphite = (Npc) attacker;

		NpcDefinition definition = kalphite.getTransformOrId() == KalphiteQueenPhase.TRANSFORMED.getId() ? secondFormDefinition : firstFormDefinition;

		if (kalphite.attackType == ServerConstants.MAGIC_ICON) {
			return definition.magicAttackAnimation;
		} else if (kalphite.attackType == ServerConstants.RANGED_ICON) {
			return definition.rangedAttackAnimation;
		}
		return definition.attackAnimation;
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
	 * The type of the attacker.
	 *
	 * @return the entity type.
	 */
	@Override
	public EntityType getAttackerType() {
		return EntityType.NPC;
	}

	/**
	 * The type of the defender.
	 *
	 * @return the entity type.
	 */
	@Override
	public EntityType getDefenderType() {
		return EntityType.PLAYER;
	}

}
