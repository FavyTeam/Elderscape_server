package game.npc.impl.vorkath;

import com.google.common.collect.ImmutableList;
import core.Server;
import core.ServerConstants;
import game.position.Position;
import game.content.combat.Combat;
import game.content.combat.Venom;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.object.clip.Region;
import game.object.custom.Object;
import game.player.Boundary;
import game.player.Player;
import game.player.movement.Movement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import utility.Misc;

/**
 * Created by Jason MacKeigan on 2018-02-06 at 11:35 AM
 */
public class VorkathCombatStrategy extends NpcCombatStrategy {

	/**
	 * The number of consecutive basic attacks that must occur before a special does.
	 */
	private static final int ATTACKS_BEFORE_SPECIAL = 6;

	/**
	 * A list of all available basic attacks.
	 */
	private static final List<VorkathAttack> BASIC_ATTACKS = ImmutableList.of(
			VorkathAttack.MELEE, VorkathAttack.RANGED, VorkathAttack.MAGIC,
			VorkathAttack.DRAGON_FIRE_GREEN, VorkathAttack.DRAGON_FIRE_RED,
			VorkathAttack.DRAGON_FIRE_PINK
	                                                                         );

	/**
	 * A list of special attacks.
	 */
	private static final List<VorkathAttack> SPECIAL_ATTACKS = ImmutableList.of(
			VorkathAttack.POISON_POOL, VorkathAttack.ZOMBIE
	                                                                           );

	/**
	 * The main entire area that combat takes place
	 */
	private static final Boundary MAIN_BOUNDARY = new Boundary(2262, 4056, 2282, 4076);

	/**
	 * The area that the boss lives within.
	 */
	private static final Boundary BOSS_BOUNDARY = new Boundary(2268, 4061, 2276, 4069);

	/**
	 * The next special that this vorkath will execute.
	 */
	private VorkathAttack nextSpecialAttack = Misc.random(SPECIAL_ATTACKS);

	/**
	 * The number of basic consecutive attacks since the last special attack.
	 */
	private int consecutiveBasicAttacks;

	/**
	 * The first basic attack from vorkath, by default, magic.
	 */
	private VorkathAttack attack = VorkathAttack.MAGIC;

	/**
	 * The event that acts as the attack for vorkath.
	 */
	private VorkathPoisonPoolAttack poisonPoolAttack;

	/**
	 * The list of poison objects.
	 */
	private List<Object> poisonObjects = new ArrayList<>();

	/**
	 * The last attack that was executed.
	 */
	private VorkathAttack lastAttack = attack;

	/**
	 * The spawned created, or null if none.
	 */
	private Npc zombifiedSpawn;


	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		if (attacker.getAttributes().getOrDefault(Vorkath.STATE_ATTRIBUTE, VorkathState.UNPOKED) != VorkathState.POKED) {
			return false;
		}
		if (attacker.getType() != EntityType.NPC) {
			return false;
		}
		Npc npc = (Npc) attacker;

		if (zombifiedSpawn == null) {
			return true;
		}
		Region region = npc.getRegionOrNull();

		if (region == null) {
			return false;
		}

		if (attack == VorkathAttack.ZOMBIE && region.contains(zombifiedSpawn)) {
			return false;
		}
		return true;
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
		return defender.getAttributes().getOrDefault(Vorkath.STATE_ATTRIBUTE, VorkathState.UNPOKED) == VorkathState.POKED;
	}

	/**
	 * The custom damage to be taken from another entity, or -1 if no custom damage is to be calculated.
	 *
	 * @param attacker   the entity dealing the damage.
	 * @param defender   the entity taking the damage.
	 * @param damage     the amount of damage calculated before being modified for this sequence of combat.
	 * @param attackType the type of attacking the damage was for.
	 * @return the custom damage taken, or -1 if no custom damage is calculated.
	 */
	@Override
	public int calculateCustomDamageTaken(Entity attacker, Entity defender, int damage, int attackType) {
		if (zombifiedSpawn != null) {
			Region region = defender.getRegionOrNull();

			if (region == null) {
				return -1;
			}

			if (attack == VorkathAttack.ZOMBIE && region.contains(zombifiedSpawn)) {
				Player player = (Player) attacker;

				player.getPA().sendMessage("The dragon is currently immune to your attacks.");
				Combat.resetPlayerAttack(player);

				return 0;
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
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return -1;
		}
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		if (consecutiveBasicAttacks < ATTACKS_BEFORE_SPECIAL) {
			VorkathAttack randomBasicAttack = Misc.random(BASIC_ATTACKS);

			attack = randomBasicAttack;

			if (randomBasicAttack == VorkathAttack.MELEE) {
				if (attackerAsNpc.distanceTo(defenderAsPlayer.getX(), defenderAsPlayer.getY()) > 3) {
					attack = VorkathAttack.MAGIC;
					return ServerConstants.MAGIC_ICON;
				}
				return ServerConstants.MELEE_ICON;
			}

			if (randomBasicAttack == VorkathAttack.RANGED) {
				return ServerConstants.RANGED_ICON;
			}

			if (randomBasicAttack == VorkathAttack.MAGIC) {
				return ServerConstants.MAGIC_ICON;
			}

			return ServerConstants.DRAGONFIRE_ATTACK;
		}
		attack = nextSpecialAttack;
		nextSpecialAttack = nextSpecialAttack == VorkathAttack.ZOMBIE ? VorkathAttack.POISON_POOL : VorkathAttack.ZOMBIE;

		return ServerConstants.MAGIC_ICON;
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
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return -1;
		}
		Player defenderAsPlayer = (Player) defender;

		if (attack == VorkathAttack.DRAGON_FIRE_GREEN || attack == VorkathAttack.DRAGON_FIRE_PINK) {
			int antifire = Combat.antiFire(defenderAsPlayer, false, false);

			if (antifire == 0) {
				return ThreadLocalRandom.current().nextInt(0, 61);
			} else if (antifire == 1) {
				return ThreadLocalRandom.current().nextInt(0, 41);
			} else if (antifire == 2) {
				return 0;
			}
		}

		if (attack == VorkathAttack.DRAGON_FIRE_RED) {
			return ThreadLocalRandom.current().nextInt(0, defenderAsPlayer.baseSkillLevel[ServerConstants.HITPOINTS] + 1);
		}

		return -1;
	}

	/**
	 * Determines if this entity can hit through prayer.
	 *
	 * @param attacker
	 * @param defender
	 * @param damage
	 * @param type
	 * @return false by default.
	 */
	@Override
	public boolean hitsThroughPrayer(Entity attacker, Entity defender, int damage, int type) {
		if (attack == VorkathAttack.POISON_POOL) {
			return true;
		}
		return false;
	}

	/**
	 * Referenced when damage is applied to the defender from the attacker.
	 *
	 * @param attacker the entity damaging the defender.
	 * @param defender the entity being damaged by the attacker.
	 * @param damage the amount of damage being applied to the defender.
	 * @param attackType
	 */
	@Override
	public void onDamageDealt(Entity attacker, Entity defender, int damage, int attackType) {
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		if (attack == VorkathAttack.POISON_POOL) {
			if (poisonObjects.stream().anyMatch(o -> defenderAsPlayer.getX() == o.objectX && defenderAsPlayer.getY() == o.objectY
			                                         && defenderAsPlayer.getHeight() == o.height)) {
				attackerAsNpc.heal(Math.min(10, damage));
			}
		} else if (attack == VorkathAttack.DRAGON_FIRE_PINK) {
			Combat.resetPrayers(defenderAsPlayer);
			defenderAsPlayer.getPA().sendMessage(ServerConstants.RED_COL + "Your prayers have been disabled!");
		} else if (attack == VorkathAttack.DRAGON_FIRE_GREEN) {
			Venom.appendVenom(null, defenderAsPlayer, false);
		}
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
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		if (poisonPoolAttack != null) {
			attackerAsNpc.getEventHandler().stopIfEventEquals(poisonPoolAttack);
		}
		if (lastAttack == VorkathAttack.ZOMBIE) {
			if (defenderAsPlayer.isFrozen()) {
				defenderAsPlayer.setFrozenLength(0);
			}
			if (zombifiedSpawn != null && !zombifiedSpawn.isDead()) {
				zombifiedSpawn.setDead(true);
			}
		}
		switch (attack) {
			case MELEE:
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 32, -1));
				consecutiveBasicAttacks++;
				break;
			case MAGIC:
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 100, 1479, 50, 40, -defenderAsPlayer.getPlayerId() - 1, 45, 0);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 32, -1, null, (d, p) -> p.gfx0(1480)));
				consecutiveBasicAttacks++;
				break;
			case RANGED:
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 100, 1477, 50, 40, -defenderAsPlayer.getPlayerId() - 1, 45, 0);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 4, 32, -1, null, (d, p) -> p.gfx0(1478)));
				consecutiveBasicAttacks++;
				break;
			case DRAGON_FIRE_GREEN:
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 100, 1470, 50, 40, -defenderAsPlayer.getPlayerId() - 1, 45, 0);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 60, -1, null, (d, p) -> p.gfx0(1472)));
				consecutiveBasicAttacks++;
				break;
			case DRAGON_FIRE_PINK:
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 100, 1471, 50, 40, -defenderAsPlayer.getPlayerId() - 1, 45, 0);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 60, -1, null, (d, p) -> p.gfx0(1473)));
				consecutiveBasicAttacks++;
				break;
			case DRAGON_FIRE_RED:
				Position damagePosition = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, damagePosition, 50, 120, 1481, 150, 20, 0, 45, 30);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 5, 99, -1,
				                           p -> p.getX() == damagePosition.getX() && p.getY() == damagePosition.getY()));
				break;
			case POISON_POOL:
				List<Position> poisonArea = createPoisonArea(defenderAsPlayer.getHeight());

				poisonObjects.forEach(object -> Server.objectManager.removeObject(object));

				poisonObjects.clear();

				for (Position position : poisonArea) {
					defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, position, 50, 100, 1483, 150, 20, 0, 45, 30);

					poisonObjects.add(new game.object.custom.Object(32000, position.getX(), position.getY(), position.getZ(), 0, 10, -1, 30, false));
				}
				poisonPoolAttack = new VorkathPoisonPoolAttack(attackerAsNpc, defenderAsPlayer, poisonObjects);

				attackerAsNpc.getEventHandler().addEvent(attackerAsNpc, poisonPoolAttack, 1);
				consecutiveBasicAttacks = 0;
				break;
			case ZOMBIE:
				ZombifiedSpawnLocation spawnLocation = java.util.stream.Stream.of(ZombifiedSpawnLocation.values())
				                                                              .max(Comparator.comparingInt(location -> location.getPosition().distanceTo
						                                                                                                                              (defenderAsPlayer.getX(),
						                                                                                                                               defenderAsPlayer.getY())))
				                                                              .orElse(ZombifiedSpawnLocation.SOUTH_WEST);

				zombifiedSpawn = NpcHandler.spawnNpc(defenderAsPlayer, 8062, spawnLocation.getPosition().getX(),
				                                     spawnLocation.getPosition().getY(), attackerAsNpc.getHeight(), true, false);

				defenderAsPlayer.setFrozenLength(10_000);
				defenderAsPlayer.frozenBy = -1;
				Movement.stopMovement(defenderAsPlayer);
				defenderAsPlayer.getPA().sendMessage(ServerConstants.RED_COL + "You have been frozen.");
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 100, 395, 50, 40, -defenderAsPlayer.getPlayerId() - 1, 45, 0);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 32, -1, null, (d, p) -> p.gfx0(369)));
				consecutiveBasicAttacks = 0;
				break;
		}
		lastAttack = attack;
	}

	/**
	 * Attempts to create a list of poisoned areas within the main boundary
	 * without being on the inside of the boss boundary.
	 *
	 * @return a list of poisoned areas.
	 */
	private List<Position> createPoisonArea(int height) {
		List<Position> poisonedAreas = new ArrayList<>();

		for (int x = MAIN_BOUNDARY.getMinimumX(); x < MAIN_BOUNDARY.getMaximumX(); x += 2) {
			for (int y = MAIN_BOUNDARY.getMinimumY(); y < MAIN_BOUNDARY.getMaximumY(); y += 2) {
				if (Boundary.isIn(x, y, height, BOSS_BOUNDARY)) {
					continue;
				}
				poisonedAreas.add(new Position(x + ThreadLocalRandom.current().nextInt(0, 3), y + ThreadLocalRandom.current().nextInt(0, 3), height));
			}
		}

		return poisonedAreas;
	}

	/**
	 * Referenced only if {@link #isCustomAttack()} returns true, in which case we can change some
	 * important combat related information to the attacker and defender, like attack speed.
	 *
	 * @param attacker the entity making the attack.
	 * @param defender
	 */
	@Override
	public void afterCustomAttack(Entity attacker, Entity defender) {
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		if (attack == VorkathAttack.POISON_POOL) {
			attackerAsNpc.attackTimer = 30;
		} else if (attack == VorkathAttack.ZOMBIE) {
			attackerAsNpc.attackTimer = 15;
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
		if (attacker.getType() != EntityType.NPC) {
			return -1;
		}
		boolean special = SPECIAL_ATTACKS.contains(attack);

		if (special) {
			if (attack == VorkathAttack.POISON_POOL) {
				return 7957;
			}
			return 7952;
		}

		if (attack == VorkathAttack.MAGIC || attack == VorkathAttack.RANGED) {
			return 7952;
		}

		if (attack == VorkathAttack.MELEE) {
			return 7951;
		}

		if (attack == VorkathAttack.DRAGON_FIRE_GREEN || attack == VorkathAttack.DRAGON_FIRE_PINK) {
			return 7952;
		}

		if (attack == VorkathAttack.DRAGON_FIRE_RED) {
			return 7960;
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
