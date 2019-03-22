package game.npc.impl.dragon.impl.adamant;

import core.ServerConstants;
import game.position.Position;
import game.content.combat.vsnpc.CombatNpc;
import game.entity.Entity;
import game.entity.EntityType;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.npc.impl.dragon.DragonCombatStrategy;
import game.player.Player;
import utility.Misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Jason MacKeigan on 2018-06-18 at 2:54 PM
 */
public class AdamantDragonCombatStrategy extends DragonCombatStrategy {

	//440 kbd gfx

	private AdamantDragonAttackType attackType = AdamantDragonAttackType.REGULAR;

	/**
	 * Determines the attack type of the entity, representing what style of combat is being used.
	 *
	 * @param attacker
	 * @param defender
	 * @return the attack type, or -1 if none can be found.
	 */
	@Override
	public int calculateAttackType(Entity attacker, Entity defender) {
		int random = ThreadLocalRandom.current().nextInt(0, 100);

		attackType = random < 10 ? AdamantDragonAttackType.RUBY_BOLT
				             : random < 30 ? AdamantDragonAttackType.AREA_OF_EFFECT
									   : random <= 55 ? AdamantDragonAttackType.RANGE
											     : AdamantDragonAttackType.REGULAR;

		if (attackType == AdamantDragonAttackType.RANGE) {
			return ServerConstants.RANGED;
		} else if (attackType == AdamantDragonAttackType.AREA_OF_EFFECT
		           || attackType == AdamantDragonAttackType.RUBY_BOLT) {
			return ServerConstants.MAGIC_ICON;
		}
		return super.calculateAttackType(attacker, defender);
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
			Player player = (Player) defender;

			Npc npc = (Npc) attacker;

			if (attackType == AdamantDragonAttackType.AREA_OF_EFFECT) {
				final Position target = new Position(player.getX(), player.getY(), player.getHeight());

				player.getPA().createPlayersProjectile(npc, player, 50, 105, 1486, 43, 15, 0, 45, 20);
				DamageQueue.add(new Damage(player, npc, ServerConstants.MAGIC, 5, 29, -1, p ->
						target.matches(p.getX(), p.getY(), p.getHeight()), null, (damage, p) -> {
					p.getPA().createPlayersStillGfx(1487, target.getX(), target.getY(), 0, 0);

					List<Position> surrounding = new ArrayList<>(target.surrounding(1));

					Collections.shuffle(surrounding);

					List<Position> targets = surrounding.stream().limit(2).collect(Collectors.toList());

					targets.forEach(t -> {
						int indexOf = Math.max(0, targets.indexOf(t));

						player.getPA().createPlayersProjectile(target, t, 50, 105 + (indexOf * 40), 1486, 35, 15, 0, 45 + (indexOf * 40), 20);

						DamageQueue.add(new Damage(player, npc, ServerConstants.MAGIC, 5 + (indexOf * 2), 29, -1, pl ->
								t.distanceTo(pl.getX(), pl.getY()) <= 1,
								null, (d, pl2) -> {
							pl2.getPA().createPlayersStillGfx(1487, t.getX(), t.getY(), 0, 0);
						}));
					});
				}));
			} else if (attackType == AdamantDragonAttackType.RUBY_BOLT) {
				player.getPA().createPlayersProjectile(npc, player, 50, 100, 440, 25, 15, Misc.lockon(player), 45, 0);
				DamageQueue.add(new Damage(player, npc, ServerConstants.RANGED_ICON, 4, 29, -1, null, (d, p) -> {
					CombatNpc.applyHitSplatOnNpc(p, npc, (int) (d * .2D), ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.RANGED_ICON, 1);
				}));
			} else if (attackType == AdamantDragonAttackType.RANGE) {
				player.getPA().createPlayersProjectile(npc, player, 50, 100, 1462, 15, 15, Misc.lockon(player), 45, 0);
				DamageQueue.add(new Damage(player, npc, ServerConstants.RANGED_ICON, 4, 29, -1, null, null, (d, p) -> p.gfx0(1463)));
			} else {
				super.onCustomAttack(attacker, defender);
			}
		}
	}



	/**
	 * Referenced only if {@link #isCustomAttack()} returns true, in which case we can change some
	 * important combat related information to the attacker and defender, like attack speed.
	 *
	 * @param attacker the entity making the attack.
	 * @param defender the entity defending themselves.
	 */
	@Override
	public void afterCustomAttack(Entity attacker, Entity defender) {
		if (attackType == AdamantDragonAttackType.AREA_OF_EFFECT && attacker.getType() == EntityType.NPC
				&& defender.getType() == EntityType.PLAYER) {
			Npc npc = (Npc) attacker;

			npc.attackTimer += 5;
		}
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
}
