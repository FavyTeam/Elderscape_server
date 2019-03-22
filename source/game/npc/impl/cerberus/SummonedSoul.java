package game.npc.impl.cerberus;

import core.ServerConstants;
import core.GameType;
import game.content.combat.Combat;
import game.entity.Entity;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.type.GameTypeIdentity;

import java.util.stream.Stream;

/**
 * Created by Jason MacKeigan on 2018-02-13 at 10:57 PM
 */
//@CustomNpcComponent(id = {5867, 5868, 5869}, type = GameType.OSRS)
@CustomNpcComponent(identities = {
		@GameTypeIdentity(type=GameType.OSRS, identity = 5867),
		@GameTypeIdentity(type=GameType.OSRS, identity = 5868),
		@GameTypeIdentity(type=GameType.OSRS, identity = 5869)
})
public class SummonedSoul extends Npc {

	private final SummonedSoulMobType type;

	private final NpcCombatStrategy strategy;

	private boolean attackable;

	private boolean attacked;

	public SummonedSoul(int npcId, int type) {
		super(npcId, type);
		super.setNeverRandomWalks(true);
		super.setWalkingHomeDisabled(true);
		super.setClippingIgnored(true);

		SummonedSoulMobType summonedSoulMobType = Stream.of(SummonedSoulMobType.values()).filter(t -> t.getMobId() == type)
		                                                .findAny().orElseThrow(IllegalArgumentException::new);

		this.type = summonedSoulMobType;
		this.strategy = new SummonedSoulCombatStrategy(this, summonedSoulMobType);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new SummonedSoul(index, npcType);
	}

	public void setAttackable(boolean attackable) {
		this.attackable = attackable;
	}

	public void setAttacked(boolean attacked) {
		this.attacked = attacked;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public boolean hasAttacked() {
		return attacked;
	}

	public SummonedSoulMobType getMobType() {
		return type;
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return strategy;
	}

	private final class SummonedSoulCombatStrategy extends NpcCombatStrategy {

		private final SummonedSoul summonedSoul;

		private final SummonedSoulMobType type;

		private SummonedSoulCombatStrategy(SummonedSoul summonedSoul, SummonedSoulMobType type) {
			this.summonedSoul = summonedSoul;
			this.type = type;
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
			return false;
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
			if (attacked) {
				return false;
			}
			return attackable;
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
				Player defenderAsPlayer = (Player) defender;

				if (defenderAsPlayer.prayerActive[type.getProtectionPrayer()]) {
					return 0;
				}
			}
			return 30;
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

				if (type == SummonedSoulMobType.MELEE) {
					DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 5, -1, 30));
					defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 125, 1248, 43, 31, -defenderAsPlayer.getPlayerId() - 1, 65, 0);
				} else if (type == SummonedSoulMobType.RANGE) {
					DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 5, -1, 30));
					defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 125, 124, 43, 31, -defenderAsPlayer.getPlayerId() - 1, 65, 0);
				} else if (type == SummonedSoulMobType.MAGIC) {
					DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 5, -1, 30));
					defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 125, 100, 43, 31, -defenderAsPlayer.getPlayerId() - 1, 65, 0);
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
			if (type == SummonedSoulMobType.RANGE) {
				return 4489;
			} else if (type == SummonedSoulMobType.MAGIC) {
				return 4504;
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
		 * Referenced when damage is applied to the defender from the attacker.
		 *
		 * @param attacker the entity damaging the defender.
		 * @param defender the entity being damaged by the attacker.
		 * @param damage the amount of damage being applied to the defender.
		 * @param attackType
		 */
		@Override
		public void onDamageDealt(Entity attacker, Entity defender, int damage, int attackType) {
			if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
				Player defenderAsPlayer = (Player) defender;

				summonedSoul.setAttacked(true);
				if (defenderAsPlayer.prayerActive[type.getProtectionPrayer()]) {
					Combat.applyPrayerReduction(defenderAsPlayer, 30);
				}
			}
		}

	}
}
