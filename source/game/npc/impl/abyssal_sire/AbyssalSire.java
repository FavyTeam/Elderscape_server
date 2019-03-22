package game.npc.impl.abyssal_sire;

import com.google.common.collect.ImmutableMap;
import core.ServerConstants;
import core.GameType;
import game.position.Position;
import game.content.combat.CombatConstants;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.TransientAttributeKey;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.object.clip.Region;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.type.GameTypeIdentity;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by Jason MacKeigan on 2018-02-21 at 2:46 PM
 */
//@CustomNpcComponent(id = 5886, type = GameType.OSRS)
@CustomNpcComponent(identities = {
		@GameTypeIdentity(type=GameType.OSRS, identity = 5886)
})
public class AbyssalSire extends Npc implements EntityCombatStrategy {

	public static final AttributeKey<Boolean> AWAKE = new TransientAttributeKey<>(false);

	private static final Map<Integer, Integer> DISORIENTING_SPELLS = ImmutableMap.of(
			13023, 100,
			12999, 75,
			13011, 50,
			12987, 25
	                                                                                );

	private AbyssalSireAttack attack = AbyssalSireAttack.POISON;

	private AbyssalSirePhase phase = AbyssalSirePhase.SLEEPING;

	private Collection<Npc> tentacles = new ArrayList<>();

	private Collection<Npc> respiratorySystems = new ArrayList<>();

	private Collection<Npc> spawns = new ArrayList<>();

	private AbyssalSireRegion region;

	private AbyssalSirePhaseProgressionEvent phaseProgressionEvent;

	private AbyssalSireExplosionState explosionState = AbyssalSireExplosionState.NONE;

	private Player target;

	private int disorientation;

	private int damageDisorientation;

	public AbyssalSire(int npcId, int type) {
		super(npcId, type);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new AbyssalSire(index, npcType);
	}

	//TODO remove after finished testing
	@Deprecated
	public void debugForceToMoveSouth() {
		lastDamageTaken = System.currentTimeMillis();
		moveSouth();
	}

	private void firstWake() {
		phase = AbyssalSirePhase.AWAKE;
		respiratorySystems.forEach(npc -> {
			npc.transform(5915);
			npc.getAttributes().put(AWAKE, true);
		});
		tentacles.forEach(npc -> {
			npc.transform(5912);
			npc.getAttributes().put(AWAKE, true);
		});
		setIdleAnimation(4529);
	}

	private void wake() {
		phase = AbyssalSirePhase.AWAKE;
		tentacles.forEach(npc -> {
			npc.transform(5912);
			npc.getAttributes().put(AWAKE, true);
		});
		respiratorySystems.forEach(npc -> {
			npc.getAttributes().put(AWAKE, false);
		});
		setIdleAnimation(4529);
		disorientation = 0;
		damageDisorientation = 0;
	}

	private void lastSleep() {
		attack = AbyssalSireAttack.POISON;
		phase = AbyssalSirePhase.SLEEPING;
		tentacles.forEach(npc -> {
			npc.transform(5909);
			npc.getAttributes().put(AWAKE, false);
		});
		respiratorySystems.forEach(npc -> {
			npc.transform(5914);
			npc.getAttributes().put(AWAKE, false);
		});
		spawns.stream().filter(n -> !n.isDead() && !n.needRespawn).forEach(n -> n.setDead(true));
		spawns.clear();
		explosionState = AbyssalSireExplosionState.NONE;
		setIdleAnimation(4527);
		setClippingIgnored(false);
		setFacingEntityDisabled(false);
		setKillerId(0);
		resetFace();
		turnNpc(getX(), getY() - 1);
		target = null;
		disorientation = 0;
		damageDisorientation = 0;

		if (phaseProgressionEvent != null) {
			getEventHandler().stopIfEventEquals(phaseProgressionEvent);
			phaseProgressionEvent = null;
		}
		//TODO ensure that tentacles and respiratory systems are alive
	}

	private void disorient() {
		setIdleAnimation(4527);
		resetFace();
		turnNpc(getX(), getY() - 1);
		phase = AbyssalSirePhase.DISORIENTED;
		tentacles.forEach(npc -> {
			npc.transform(5909);
			npc.getAttributes().put(AWAKE, false);
		});
		respiratorySystems.forEach(npc -> {
			npc.getAttributes().put(AWAKE, true);
		});
		if (phaseProgressionEvent != null) {
			phaseProgressionEvent.stop();
		}
		CycleEventContainer<Entity> container = getEventHandler().addEvent(this, phaseProgressionEvent = new AbyssalSirePhaseProgressionEvent(), 50);

		container.addStopListener(() -> {
			if (respiratorySystems.stream().allMatch(system -> system == null || system.isDead())) {
				moveSouth();
			} else {
				wake();
			}
		});
	}

	private void walkHome() {
		setClippingIgnored(true);
		phase = AbyssalSirePhase.WALKING_HOME;
		transform(5890);
		setIdleAnimation(4533);
		resetFollowing();
		resetFace();
		setKillerId(0);
	}

	private void moveSouth() {
		setClippingIgnored(true);
		phase = AbyssalSirePhase.WALKING_SOUTH;
		transform(5890);
		setIdleAnimation(4533);
		requestAnimation(4528);
		resetFollowing();
		resetFace();
		setKillerId(0);
	}

	private void transformToMelee() {
		setClippingIgnored(false);
		phase = AbyssalSirePhase.MELEE_COMBAT;
		transform(5890);
		setIdleAnimation(4533);
	}

	private void walkToCenter() {
		phase = AbyssalSirePhase.WALKING_TO_CENTER;
		resetFollowing();
		resetFace();
		setKillerId(0);
	}

	private void transformToMagic() {
		phase = AbyssalSirePhase.MAGIC_COMBAT;
		transform(5891);
		requestAnimation(7096);
		setFacingEntityDisabled(true);
		resetFace();
		resetFollowing();
		turnNpc(getX(), getY() - 1);
		getEventHandler().addEvent(this, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				container.stop();

				setIdleAnimation(7097);
			}

			@Override
			public void stop() {

			}
		}, 2);
	}

	/**
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		setIdleAnimation(4527);
		setNeverRandomWalks(true);
		setWalkingHomeDisabled(true);

		this.region = Stream.of(AbyssalSireRegion.values()).filter(r -> r.getSire().getX() == getSpawnPositionX()
		                                                                && r.getSire().getY() == getSpawnPositionY()).findAny().orElse(null);

		Region region = Region.getRegion(getSpawnPositionX(), getSpawnPositionY());

		if (region != null) {
			region.forEachNpc(npc -> {
				if (npc instanceof AbyssalSireTentacle) {
					if (!npc.isDead()) {
						npc.setDead(true);
					}
				}
			});
		}
		List<Position> tentaclePositions = Arrays.asList(
				this.region.getTentacleEast(),
				this.region.getTentacleWest(),
				this.region.getTentacleNorthEast(),
				this.region.getTentacleNorthWest(),
				this.region.getTentacleSouthEast(),
				this.region.getTentacleSouthWest()
		                                                );

		tentaclePositions.forEach(position -> {
			tentacles.add(NpcHandler.spawnNpc(5909, position.getX(), position.getY(), position.getZ()));
		});

		List<Position> respiratoryPositions = Arrays.asList(
				this.region.getRespiratorySystemNorthEast(),
				this.region.getRespiratorySystemNorthWest(),
				this.region.getRespiratorySystemSouthEast(),
				this.region.getRespiratorySystemSouthWest()
		                                                   );

		respiratoryPositions.forEach(position -> respiratorySystems.add(
				NpcHandler.spawnNpc(5914, position.getX(), position.getY(), position.getZ())));
	}

	/**
	 * Referenced when a player is added to this entities local list.
	 *
	 * @param player the player that was being added to the npcs local list.
	 */
	@Override
	public void onAddToLocalList(Player player) {
		super.onAddToLocalList(player);

		if (phase == AbyssalSirePhase.SLEEPING || phase == AbyssalSirePhase.AWAKE) {
			getEventHandler().addEvent(this, new CycleEvent<Entity>() {
				@Override
				public void execute(CycleEventContainer<Entity> container) {
					container.stop();

					if (phase == AbyssalSirePhase.SLEEPING) {
						setIdleAnimation(4527);
					} else if (phase == AbyssalSirePhase.AWAKE) {
						setIdleAnimation(4529);
					}
				}

				@Override
				public void stop() {

				}
			}, 2);
		}
	}

	@Override
	public Position getDropPosition() {
		if (region == null) {
			return super.getDropPosition();
		}
		return region.getCenter();
	}

	/**
	 * A listener function that is referenced when the non-playable character first dies, at the start of the
	 * animation.
	 */
	@Override
	public void onDeath() {
		super.onDeath();

		tentacles.stream().filter(npc -> !npc.isDead()).forEach(npc -> npc.setDead(true));
		respiratorySystems.stream().filter(npc -> !npc.isDead()).forEach(npc -> npc.setDead(true));
		spawns.stream().filter(npc -> !npc.isDead()).forEach(npc -> npc.setDead(true));

		tentacles.clear();
		respiratorySystems.clear();
		spawns.clear();
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {
		super.onSequence();

		if (phase == AbyssalSirePhase.AWAKE) {
			heal(3);
		} else if (phase == AbyssalSirePhase.WALKING_SOUTH) {
			if (getX() == region.getPhaseTwoCenter().getX() - 2 && getY() == region.getPhaseTwoCenter().getY()) {
				transformToMelee();
			} else {
				if (!walkTileInDirection(region.getPhaseTwoCenter().getX() - 2, region.getPhaseTwoCenter().getY())) {
					move(region.getPhaseTwoCenter().translate(-2, 0));
				}
			}
		} else if (phase == AbyssalSirePhase.WALKING_TO_CENTER) {
			if (getX() == region.getCenter().getX() - 2 && getY() == region.getCenter().getY()) {
				transformToMagic();
			} else {
				if (!walkTileInDirection(region.getCenter().getX() - 2, region.getCenter().getY())) {
					move(region.getCenter().translate(-2, 0));
				}
			}
		} else if (phase == AbyssalSirePhase.MELEE_COMBAT) {
			if (target != null) {
				int distanceTo = distanceTo(target.getX(), target.getY());

				if (distanceTo > 5 && distanceTo < 16) {
					target.move(new Position(getX(), getY(), getHeight()));
				}
			}
		}

		if (phase != AbyssalSirePhase.WALKING_HOME && phase != AbyssalSirePhase.SLEEPING
		    && System.currentTimeMillis() - lastDamageTaken > TimeUnit.MINUTES.toMillis(3)
		    || target != null && (target.isDisconnected() || target.distanceToPoint(getX(), getY()) >= 64)) {
			walkHome();
		}

		if (phase == AbyssalSirePhase.WALKING_HOME) {
			if (getX() == region.getSire().getX() && getY() == region.getSire().getY()) {
				lastSleep();
			} else {
				if (!walkTileInDirection(region.getSire().getX(), region.getSire().getY())) {
					move(region.getSire());
				}
			}
		}
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
		if (phase == AbyssalSirePhase.SLEEPING || phase == AbyssalSirePhase.WALKING_SOUTH ||
		    phase == AbyssalSirePhase.WALKING_TO_CENTER || phase == AbyssalSirePhase.DISORIENTED
		    || phase == AbyssalSirePhase.WALKING_HOME) {
			return false;
		}
		if (phase == AbyssalSirePhase.MAGIC_COMBAT && explosionState == AbyssalSireExplosionState.EXPLODING) {
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
		if (target != null && target != attacker) {
			return false;
		}
		if (phase == AbyssalSirePhase.WALKING_SOUTH || phase == AbyssalSirePhase.WALKING_TO_CENTER
		    || phase == AbyssalSirePhase.WALKING_HOME) {
			return false;
		}
		return true;
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
		if (attacker.getType() != EntityType.NPC || defender.getType() != EntityType.PLAYER) {
			return ServerConstants.MELEE_ICON;
		}
		Npc attackerAsNpc = (Npc) attacker;

		int randomChanceOfAttack = ThreadLocalRandom.current().nextInt(0, 100);

		if (phase == AbyssalSirePhase.AWAKE) {
			if (randomChanceOfAttack <= 80 || spawns.stream().filter(n -> n != null && !n.isDead()).count() >= 2) {
				attack = AbyssalSireAttack.POISON;
			} else {
				attack = AbyssalSireAttack.SPAWN;
			}
		} else if (phase == AbyssalSirePhase.MELEE_COMBAT) {
			if (randomChanceOfAttack <= 50) {
				attack = AbyssalSireAttack.SWIPE;
			} else if (randomChanceOfAttack <= 85) {
				attack = AbyssalSireAttack.TENDRIL_SWIPE;
			} else {
				attack = AbyssalSireAttack.DOUBLE_TENDRIL_SWIPE;
			}
		} else if (phase == AbyssalSirePhase.MAGIC_COMBAT) {
			if (explosionState == AbyssalSireExplosionState.NONE && attackerAsNpc.getCurrentHitPoints() <= 139) {
				attack = AbyssalSireAttack.PORTAL;
			} else {
				attack = AbyssalSireAttack.POISON;
			}
		}
		return ServerConstants.MELEE_ICON;
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
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			if (attack == AbyssalSireAttack.SWIPE) {
				if (defenderAsPlayer.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
					return ThreadLocalRandom.current().nextInt(2, 7);
				}
				return NpcHandler.calculateNpcMeleeDamage(attackerAsNpc, defenderAsPlayer, -1, 32);
			} else if (attack == AbyssalSireAttack.TENDRIL_SWIPE) {
				if (defenderAsPlayer.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
					return ThreadLocalRandom.current().nextInt(0, 13);
				}
				return NpcHandler.calculateNpcMeleeDamage(attackerAsNpc, defenderAsPlayer, -1, 40);
			} else if (attack == AbyssalSireAttack.DOUBLE_TENDRIL_SWIPE) {
				if (defenderAsPlayer.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
					return ThreadLocalRandom.current().nextInt(0, 26);
				}
				return NpcHandler.calculateNpcMeleeDamage(attackerAsNpc, defenderAsPlayer, -1, 66);
			}
		}
		return -1;
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
		return damage;
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
		if (attacker.getType() == EntityType.PLAYER && defender.getType() == EntityType.NPC) {
			Player attackerAsPlayer = (Player) attacker;

			Npc defenderAsNpc = (Npc) defender;

			if (phase == AbyssalSirePhase.SLEEPING && phaseProgressionEvent == null) {
				phaseProgressionEvent = new AbyssalSirePhaseProgressionEvent();

				CycleEventContainer<Entity> container = getEventHandler().addEvent(this, phaseProgressionEvent, 6);

				requestAnimation(4528);
				container.addStopListener(() -> {
					if (phase == AbyssalSirePhase.SLEEPING) {
						target = attackerAsPlayer;
						firstWake();
					}
				});
			} else if (phase == AbyssalSirePhase.AWAKE) {
				if (attackerAsPlayer.getOldSpellId() > -1) {
					int spellDisorientation = DISORIENTING_SPELLS.getOrDefault(CombatConstants.MAGIC_SPELLS[attackerAsPlayer.getOldSpellId()][0], 0);

					if (spellDisorientation > 0) {
						disorientation += spellDisorientation;

						if (disorientation >= 100) {
							disorient();
							attackerAsPlayer.getPA().sendMessage("Your shadow spelled disoriented the Abyssal sire.");
						}
					}
				}

				if (phase != AbyssalSirePhase.DISORIENTED) {
					damageDisorientation += damage;

					if (damageDisorientation >= 75) {
						disorient();
						attackerAsPlayer.getPA().sendMessage("Your damage slowly disoriented the Abyssal sire.");
					}
				}
				heal(ThreadLocalRandom.current().nextInt(1, Math.max(5, damage)));
			} else if (phase == AbyssalSirePhase.MELEE_COMBAT) {
				if ((defenderAsNpc.getCurrentHitPoints() <= 200)) {
					walkToCenter();
				}
			}
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
		if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			if (attack == AbyssalSireAttack.SPAWN) {
				Position nextOpenTile = Region.nextOpenTileOrNull(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

				if (nextOpenTile == null) {
					nextOpenTile = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());
				}
				spawns.add(NpcHandler.spawnNpc(defenderAsPlayer, 5917, nextOpenTile.getX(), nextOpenTile.getY(), nextOpenTile.getZ(), true, false));
			} else if (attack == AbyssalSireAttack.SWIPE) {
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 30, -1));
			} else if (attack == AbyssalSireAttack.TENDRIL_SWIPE) {
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 40, -1));
			} else if (attack == AbyssalSireAttack.DOUBLE_TENDRIL_SWIPE) {
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 66, -1));
			} else if (attack == AbyssalSireAttack.POISON) {
				defenderAsPlayer.getPA().createPlayersStillGfx(1275, defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight(), 0);
				defenderAsPlayer.getEventHandler().addEvent(this, new AbyssalSirePoisonEvent(
						new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight()), defenderAsPlayer), 2);
			} else if (attack == AbyssalSireAttack.PORTAL) {
				CycleEventContainer<Entity> container = attackerAsNpc.getEventHandler().addEvent(this,
				                                                                                 new AbyssalSireExplosionEvent(this, defenderAsPlayer), 6);

				defenderAsPlayer.move(new Position(attackerAsNpc.getX(), attackerAsNpc.getY(), attackerAsNpc.getHeight()));
				container.addStopListener(() -> {
					if (isDead()) {
						return;
					}
					setIdleAnimation(7099);
					explosionState = AbyssalSireExplosionState.EXPLODED;

					for (int offset = 0; offset < 5; offset++) {
						spawns.add(NpcHandler.spawnNpc(defenderAsPlayer, 5917, getX(), getY() - offset, getHeight(), true, false));
					}
				});
			}
		}
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

	}

	/**
	 * Retrieves the custom emote for the attacking entity, or -1 if there is no custom emote.
	 *
	 * @param attacker the entity making the attack animation.
	 * @return the attack emote, or -1 by default for no custom attack emote.
	 */
	@Override
	public int getCustomAttackAnimation(Entity attacker) {
		switch (attack) {
			case SWIPE:
				return 5366;
			case TENDRIL_SWIPE:
				return 5369;
			case DOUBLE_TENDRIL_SWIPE:
				return 5755;
			case SPAWN:
				return 4530;
			case POISON:
				return 4531;
			case PORTAL:
				return 7098;
		}
		return -1;
	}

	/**
	 * Determines whether or not an entity should perform an attack animation.
	 *
	 * @return by default this is true.
	 */
	@Override
	public boolean performsAttackAnimation() {
		if (phase == AbyssalSirePhase.MAGIC_COMBAT && attack == AbyssalSireAttack.POISON) {
			return false;
		}
		return true;
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

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return this;
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
