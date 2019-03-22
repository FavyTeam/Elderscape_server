package game.content.combat;

import core.GameType;
import core.ServerConstants;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.util.concurrent.TimeUnit;

/**
 * Combat poisoning.
 *
 * @author MGT Madness, created on 09-04-2014.
 */
public class Poison {
	public static void removePoison(Player player) {
		player.poisonDamage = 0;
		informClientOfPoisonOff(player);
	}

	/**
	 * Inform the client to turn on the poison orb.
	 *
	 * @param player The associated player.
	 */
	public static void informClientOfPoisonOn(Player player) {
		player.playerAssistant.sendMessage(":poisonon:");
	}

	/**
	 * Inform the client to turn off the poison orb.
	 *
	 * @param player The associated player.
	 */
	public static void informClientOfPoisonOff(Player player) {
		player.playerAssistant.sendMessage(":poisonoff:");
	}

	/**
	 * Poison the player.
	 */
	public static void appendPoison(final Player attacker, final Player player, boolean logInUpdate,
			int poisonDamage) {

		if (player.venomEvent) {
			return;
		}
		if (poisonDamage > player.poisonDamage && !Combat.hasSerpentineHelm(player)) {
			player.poisonDamage = poisonDamage;
			player.poisonHitsplatsLeft = 4;
		}
		if (player.poisonEvent) {
			return;
		}
		if (player.poisonDamage == 0 && logInUpdate) {
			return;
		}
		if (GameType.isPreEoc()) {
			if (player.getEquippedShield(18_340)) {
				return;
			}
		}
		if (!logInUpdate) {
			// Has anti poison effect.
			if (System.currentTimeMillis() - player.lastPoisonSip < player.poisonImmune) {
				return;
			}
			if (Combat.hasSerpentineHelm(player)) {
				return;
			}
			player.setTimeCanDisconnectAtBecauseOfCombat(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
			player.playerAssistant.sendMessage("You have been poisoned!");
			player.poisonDamage = poisonDamage;
			player.poisonHitsplatsLeft = 3;
			player.poisonTicksUntillDamage = 100;
			if (attacker == null) {
				Combat.createHitsplatOnPlayerNormal(player, poisonDamage,
						ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
			} else {
				attacker.ignoreInCombat = true;
				Combat.createHitsplatOnPlayerPvp(attacker, player, poisonDamage,
						ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
				attacker.ignoreInCombat = false;
			}
		}
		player.poisonEvent = true;
		Poison.informClientOfPoisonOn(player);

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getDead()) {
					container.stop();
					return;
				}
				if (System.currentTimeMillis() - player.lastPoisonSip < player.poisonImmune) {
					container.stop();
					return;
				}
				if (player.poisonDamage == 0) {
					container.stop();
					return;
				}
				int damage = player.poisonDamage;

				player.poisonTicksUntillDamage--;
				if (player.poisonTicksUntillDamage == 0) {
					player.setTimeCanDisconnectAtBecauseOfCombat(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
					if (attacker == null) {
						Combat.createHitsplatOnPlayerNormal(player, damage,
								ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
					} else {
						attacker.ignoreInCombat = true;
						Combat.createHitsplatOnPlayerPvp(attacker, player, damage,
								ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "",
								-1);
						attacker.ignoreInCombat = false;
					}
					player.poisonHitsplatsLeft--;
					player.poisonTicksUntillDamage = 100;

					if (player.poisonHitsplatsLeft == 0) {
						if (player.poisonDamage == 1) {
							player.playerAssistant.sendMessage("The poison has worn off.");
							container.stop();
							return;
						} else {

							player.poisonDamage--;
							player.poisonHitsplatsLeft = 4;
						}
					}
				}



			}

			@Override
			public void stop() {
				player.poisonEvent = false;
				informClientOfPoisonOff(player);
			}
		}, 1);
	}

}
