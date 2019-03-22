package game.npc.combat;

import game.npc.NpcHandler;
import game.player.Player;
import game.player.PlayerHandler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class DamageQueue {

	/**
	 * The queue containing all of the damage being dealt by the player
	 */
	private static Queue<Damage> damageQueue = new LinkedList<>();

	/**
	 * Adds a damage object to the end of the queued damage list
	 *
	 * @param damage the damage to be dealt
	 */
	public static void add(Damage damage) {
		damageQueue.add(damage);
	}

	public static void execute() {
		if (damageQueue.size() <= 0) {
			return;
		}
		Damage damage;
		Queue<Damage> updatedQueue = new LinkedList<>();
		while ((damage = damageQueue.poll()) != null) {
			if (damage.getHitsplatDelay() > 1) {
				damage.decreaseHitsplatDelay();
			}
			if (damage.getHitsplatDelay() == 1) {
				Player player = PlayerHandler.players[damage.getPlayerIndex()];

				if (player != null && !player.isDisconnected()) {
					if (player.getPlayerName().equals(damage.getPlayerName())) {
						Predicate<Player> requirement = damage.getRequirementOrNull();

						int resultOfDamage = 0;

						if (requirement == null || requirement.test(player)) {
							resultOfDamage = NpcHandler.applyDamageOnPlayerFromNPC(player.getPlayerId(), NpcHandler.npcs[damage.getTarget()], damage.getDamageType(), true,
							                                                           damage.getFixedDamage(), damage.getMaximumDamage());

							BiConsumer<Integer, Player> onSuccessfulHit = damage.getOnSuccessfulHit();

							if (onSuccessfulHit != null) {
								onSuccessfulHit.accept(resultOfDamage, player);
							}
						}
						BiConsumer<Integer, Player> onHitRegardless = damage.getOnHitRegardless();

						if (onHitRegardless != null) {
							onHitRegardless.accept(resultOfDamage, player);
						}
					}
				}
			}
			if (damage.getHitsplatDelay() > 1) {
				updatedQueue.add(damage);
			}
		}
		damageQueue.addAll(updatedQueue);
	}

}
