package game.npc.impl.donator_boss;

import java.util.concurrent.ThreadLocalRandom;

import core.ServerConstants;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Written by Owain, 05/07/18, based on Jason's Corporeal beast strategy
 */
public class DonatorBossCombatStrategy extends NpcCombatStrategy {

	public static final int NPC_ID = 7884;

	public static final int MAX_HIT_MELEE = 25;

	public static final int MAX_HIT_MAGIC = 25;

	public static final int MELEE_ANIMATION = 7769;

	public static final int MAGIC_ANIMATION = 7770;

	public static final int MAGIC_PROJECTILE_ID_1 = 1441;

	public static final int MAGIC_PROJECTILE_ID_2 = 1444;

	public static final int IMPACT_GRAPHIC = 1460;

	private DonatorBossAttack attack = DonatorBossAttack.MELEE;

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return true if the attacker can attack the defender, also true by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		return true;
	}

	/**
	 * Determines if the defender can be attacked by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @return true if the defender can be attacked, and true by default.
	 */
	@Override
	public boolean canBeAttacked(Entity attacker, Entity defender) {
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

				if (attackerAsNpc.distanceTo(defenderAsPlayer.getX(), defenderAsPlayer.getY()) <= 2) {
					attack = DonatorBossAttack.MELEE;

					return ServerConstants.MELEE_ICON;
				}
			}
		}
		attack = DonatorBossAttack.MAGIC;

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

			Npc defenderAsNpc = (Npc) defender;
			if (damage > 0 && defenderAsNpc.getCurrentHitPoints() - damage <= 0) {
				return damage;
			}
		}
		return damage;
	}

	public static void handleDeathTransformation(Npc npc) {
		if (npc.npcType != NPC_ID || npc == null) {
			return;
		}
		npc.getEventHandler().addEvent(npc, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
					npc.transform(7885);
					npc.requestAnimation(7777);
					container.stop();
			}

			@Override
			public void stop() {

			}
		}, 1);
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

			if (attack == DonatorBossAttack.MELEE) {
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 51, -1));
			}
			else if (attack == DonatorBossAttack.MAGIC) {
				defenderAsPlayer.getEventHandler().addEvent(defenderAsPlayer, new CycleEvent<Entity>() {
					int i = 0;

					@Override
					public void execute(CycleEventContainer<Entity> container) {
						i++;
						switch (i) {

							case 2:
								DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 32, -1));
								defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 80, MAGIC_PROJECTILE_ID_1, 80, 31, -defenderAsPlayer.getPlayerId() - 1, 0, 0);
								break;

							case 3:
								DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 60, -1));
								defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 80, MAGIC_PROJECTILE_ID_2, 80, 31, -defenderAsPlayer.getPlayerId() - 1, 0, 0);
								container.stop();
								break;
						}
					}

					@Override
					public void stop() {
					}
				}, 1);
				//1st 'throw'
				//2nd 'throw'

				//defenderAsPlayer.getPA().createPlayersStillGfx(86, defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight(), 70);
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
				return MELEE_ANIMATION;
			} else if (attackerAsNpc.attackType == ServerConstants.MAGIC_ICON) {
				return MAGIC_ANIMATION;
			}
			return -1;
		}
		return -1;
	}
}
