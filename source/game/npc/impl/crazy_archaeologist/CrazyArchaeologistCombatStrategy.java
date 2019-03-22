package game.npc.impl.crazy_archaeologist;

import com.google.common.collect.ImmutableList;
import core.ServerConstants;
import game.position.Position;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import utility.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-01-09 at 12:49 PM
 */
public class CrazyArchaeologistCombatStrategy implements EntityCombatStrategy {

	/**
	 * A list of speech displayed by this non-playable character.
	 */
	public static final List<String> CRAZY = ImmutableList.copyOf(Arrays.asList(
			"I'm Bellock - respect me!",
			"Get off my site!",
			"No-one messes with Bellock's dig!",
			"These ruins are mine!",
			"Taste my knowledge!",
			"You belong in a museum!"));

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

		if (attackerAsNpc.attackType == ServerConstants.MAGIC_ICON) {
			attackerAsNpc.forceChat(getSpecialMessage());

			Position playerPosition = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

			Position secondPosition = playerPosition.translate(1 + Misc.random(2), 0, 0);

			Position thirdPosition = playerPosition.translate(0, -3 + Misc.random(2), 0);

			defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, playerPosition, 50, 95, getSpecialProjectileId(), 43, 0, 0, 45, 30);
			defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, secondPosition, 50, 95, getSpecialProjectileId(), 43, 0, 0, 45, 30);
			defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, thirdPosition, 50, 95, getSpecialProjectileId(), 43, 0, 0, 45, 30);
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 24, -1,
			                           p -> p.getX() == playerPosition.getX() && p.getY() == playerPosition.getY() && p.getHeight() == playerPosition.getZ()));
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 24, -1,
			                           p -> p.getX() == secondPosition.getX() && p.getY() == secondPosition.getY() && p.getHeight() == secondPosition.getZ()));
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 24, -1,
			                           p -> p.getX() == thirdPosition.getX() && p.getY() == thirdPosition.getY() && p.getHeight() == thirdPosition.getZ()));
			defenderAsPlayer.getPA().createPlayersStillGfx(157, playerPosition.getX(), playerPosition.getY(), playerPosition.getZ(), 95);
			defenderAsPlayer.getPA().createPlayersStillGfx(157, secondPosition.getX(), secondPosition.getY(), secondPosition.getZ(), 95);
			defenderAsPlayer.getPA().createPlayersStillGfx(157, thirdPosition.getX(), thirdPosition.getY(), thirdPosition.getZ(), 95);
		} else {
			if (!getSpeakableText().isEmpty()) {
				attackerAsNpc.forceChat(getSpeakableText().get(ThreadLocalRandom.current().nextInt(0, getSpeakableText().size())));
			}
			defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 75, getProjectileId(), 43, 40, 0, 45, 20);
			attackerAsNpc.getDamage().add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 3, 15, -1));
		}
	}

	protected List<String> getSpeakableText() {
		return CRAZY;
	}

	protected String getSpecialMessage() {
		return "Rain of Knowledge!";
	}

	protected int getProjectileId() {
		return 1259;
	}

	protected int getSpecialProjectileId() {
		return 1260;
	}

	/**
	 * Referenced only if {@link #isCustomAttack()} returns true, in which case we can change some
	 * important combat related information to the attacker and defender, like attack speed.
	 *
	 * @param attacker the entity making the attack.
	 * @param defender the defender of the attack.
	 */
	@Override
	public void afterCustomAttack(Entity attacker, Entity defender) {

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
	 * @return the attack type, or -1 if none can be found.
	 */
	@Override
	public int calculateAttackType(Entity attacker, Entity defender) {
		if (ThreadLocalRandom.current().nextInt(0, 7) == 0) {
			return ServerConstants.MAGIC_ICON;
		}
		return ServerConstants.RANGED_ICON;
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
