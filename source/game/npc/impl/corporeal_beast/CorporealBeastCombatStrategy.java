package game.npc.impl.corporeal_beast;

import core.ServerConstants;
import game.position.Position;
import game.content.combat.Combat;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.item.ItemDefinition;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.object.clip.Region;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-02-16 at 4:56 PM
 */
public class CorporealBeastCombatStrategy extends NpcCombatStrategy {

	private CorporealBeastAttack attack = CorporealBeastAttack.MELEE;

	private Npc darkEnergyCore;

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			if (!Region.isStraightPathUnblocked(attackerAsNpc.getX(), attackerAsNpc.getY(),
					defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight(), 1, 1, false)) {
				return false;
			}
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
		int attackPercentile = ThreadLocalRandom.current().nextInt(0, 100);

		if (attackPercentile <= 20) {
			if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
				Npc attackerAsNpc = (Npc) attacker;

				Player defenderAsPlayer = (Player) defender;

				if (attackerAsNpc.distanceTo(defenderAsPlayer.getX(), defenderAsPlayer.getY()) <= 4) {
					attack = CorporealBeastAttack.MELEE;

					return ServerConstants.MELEE_ICON;
				}
			}
		}
		attack = attackPercentile <= 60 ? CorporealBeastAttack.MAGIC_SMALL : attackPercentile <= 90
				                                                                     ? CorporealBeastAttack.MAGIC_AOE : CorporealBeastAttack.MAGIC_LARGE;

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
		if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			if (entityAttackType == ServerConstants.MAGIC_ICON && defenderAsPlayer.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
				return (int) (NpcHandler.calculateNpcMeleeDamage(attackerAsNpc, defenderAsPlayer, -1, 65) * .66);
			} else if (entityAttackType == ServerConstants.MELEE_ICON && defenderAsPlayer.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
				return 0;
			}
			return -1;
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
		if (attacker.getType() == EntityType.PLAYER && defender.getType() == EntityType.NPC) {
			Player attackerAsPlayer = (Player) attacker;

			Npc defenderAsNpc = (Npc) defender;

			if (damage > 0 && defenderAsNpc.getCurrentHitPoints() - damage <= 0) {
				return damage;
			}
			int weaponId = attackerAsPlayer.playerEquipment[ServerConstants.WEAPON_SLOT];

			if (weaponId > -1 & weaponId < ItemDefinition.getDefinitions().length) {
				ItemDefinition weaponDefinition = ItemDefinition.getDefinitions()[weaponId];

				if (weaponDefinition != null) {
					String name = weaponDefinition.name;

					if (name != null) {
						name = name.toLowerCase();

						if (name.contains("halberd") || name.contains("spear")) {
							spawnEnergyCoreIfDamaged(defenderAsNpc, damage);
							return damage;
						}
					}
				}
			}
			spawnEnergyCoreIfDamaged(defenderAsNpc, damage);
			return damage / 2;
		}
		return damage;
	}

	private void spawnEnergyCoreIfDamaged(Npc defenderAsNpc, int damage) {
		if ((darkEnergyCore == null || darkEnergyCore.isDead()) && damage >= 32 && ThreadLocalRandom.current().nextInt(0, 1) == 0) {
			//TODO should we remove the core?
			darkEnergyCore = NpcHandler.spawnNpc(320, defenderAsNpc.getX(), defenderAsNpc.getY(), defenderAsNpc.getHeight());
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
		if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			if (attack == CorporealBeastAttack.MELEE) {
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 51, -1));
			} else if (attack == CorporealBeastAttack.MAGIC_SMALL) {
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 50, 314, 80, 31, -defenderAsPlayer.getPlayerId() - 1, 0, 0);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 3, 32, -1));
			} else if (attack == CorporealBeastAttack.MAGIC_LARGE) {
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 50, 316, 80, 31, -defenderAsPlayer.getPlayerId() - 1, 0, 0);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 3, 65, -1));
			} else if (attack == CorporealBeastAttack.MAGIC_AOE) {
				Position origin = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, origin, 50, 100, 315, 80, 0, 0, 65, 0);

				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 32, -1, null, (i, p) -> {
					List<Position> randomPositionsSurrounding = new ArrayList<>();

					int attempts = 25;

					while (randomPositionsSurrounding.size() < 5 && attempts-- > 0) {
						Position next = origin.randomTranslate(-4, 4, -4, 4);

						if (randomPositionsSurrounding.contains(next)) {
							continue;
						}

						if (Region.getClipping(next.getX(), next.getY(), next.getZ()) != 0) {
							continue;
						}
						randomPositionsSurrounding.add(next);
					}

					randomPositionsSurrounding.forEach(position -> {
						defenderAsPlayer.getPA().createPlayersProjectile(origin, position, 50, 50, 315, 0, 0, 0, 0, 30);
						defenderAsPlayer.getPA().createPlayersStillGfx(317, position.getX(), position.getY(), 0, 50);
						attackerAsNpc.getEventHandler().addEvent(attackerAsNpc, new CycleEvent<Entity>() {
							@Override
							public void execute(CycleEventContainer<Entity> container) {
								container.stop();

								attackerAsNpc.getLocalPlayers().stream().filter(p -> p.distanceToPoint(position.getX(), position.getY()) <= 1).forEach(p -> {
									Combat.createHitsplatOnPlayerNormal(p, NpcHandler.calculateNpcMagicDamage(attackerAsNpc, defenderAsPlayer, -1, 32),
									                                    ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
								});
							}

							@Override
							public void stop() {

							}
						}, 3);
					});
				}));
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
		if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			Npc attackerAsNpc = (Npc) attacker;

			if (attackerAsNpc.attackType == ServerConstants.MELEE_ICON) {
				return 1682;
			} else if (attackerAsNpc.attackType == ServerConstants.MAGIC_ICON) {
				return 1680;
			}
			return -1;
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

	public Npc getDarkEnergyCore() {
		return darkEnergyCore;
	}
}
