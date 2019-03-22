package game.content.combat;

import core.ServerConstants;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.util.concurrent.TimeUnit;

/**
 * Appends venom.
 *
 * @author Owain, created on 15-12-2017.
 */
public class Venom {

	public final static boolean ENABLE_VENOM = true;

	public static void removeVenom(Player player) {
		player.venomDamage = 6;
		player.venomHits = 0;
		informClientOfVenomOff(player);
	}

	/**
	 * Inform the client to turn on the venom orb.
	 *
	 * @param player The associated player.
	 */
	public static void informClientOfVenomOn(Player player) {
		player.getPA().sendMessage(":packet:venomon:");
	}

	/**
	 * Inform the client to turn off the venom orb.
	 *
	 * @param player The associated player.
	 */
	public static void informClientOfVenomOff(Player player) {
		player.getPA().sendMessage(":packet:venomoff:");
	}

	public final static int TICKS_UNTIL_VENOM = 30;

	/**
	 * Applies the venom effect on the player
	 */
	public static void appendVenom(final Player attacker, final Player player, boolean logInUpdate) {
		if (player.venomEvent) {
			return;
		}
		if (player.venomHits == 0 && logInUpdate) {
			return;
		}
		if (!logInUpdate) {
			// Has anti-venom effect.
			if (System.currentTimeMillis() < player.venomImmunityExpireTime) {
				return;
			}
			if (Combat.hasSerpentineHelm(player)) {
				return;
			}
			player.setTimeCanDisconnectAtBecauseOfCombat(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
			player.getPA().sendMessage("You have been poisoned by venom!");
			player.venomHitsplatsLeft = 50000;
			player.venomTicksUntillDamage = TICKS_UNTIL_VENOM;//18 seconds
			player.poisonDamage = 0; // Reset poison.
			if (attacker == null) {
				if (player.venomDamage == 6) {
					Combat.createHitsplatOnPlayerNormal(player, player.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
				} else if (player.venomDamage >= 20) {
					Combat.createHitsplatOnPlayerNormal(player, 20, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
				} else {
					Combat.createHitsplatOnPlayerNormal(player, player.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
				}
				player.venomHits += 1;
				player.venomDamage += 2;
			} else {
				attacker.ignoreInCombat = true;
				if (player.venomDamage == 6) {
					Combat.createHitsplatOnPlayerPvp(attacker, player, player.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
				} else if (player.venomDamage >= 20) {
					Combat.createHitsplatOnPlayerPvp(attacker, player, 20, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
				} else {
					Combat.createHitsplatOnPlayerPvp(attacker, player, player.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
				}
				player.venomHits += 1;
				player.venomDamage += 2;
				attacker.ignoreInCombat = false;
			}
		}
		player.venomEvent = true;
		informClientOfVenomOn(player);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent<Object>() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getDead()) {
					container.stop();
					return;
				}
				if (System.currentTimeMillis() < player.venomImmunityExpireTime) {
					container.stop();
					return;
				}
				if (player.venomHits == 0) {
					container.stop();
					return;
				}

				player.venomTicksUntillDamage--;
				if (player.venomTicksUntillDamage == 0) {
					player.setTimeCanDisconnectAtBecauseOfCombat(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
					if (attacker == null) {
						if (player.venomDamage == 6) {
							Combat.createHitsplatOnPlayerNormal(player, player.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
						} else if (player.venomDamage >= 20) {
							Combat.createHitsplatOnPlayerNormal(player, 20, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
						} else {
							Combat.createHitsplatOnPlayerNormal(player, player.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
						}
						player.venomDamage += 2;
						player.venomHits += 1;
					} else {
						attacker.ignoreInCombat = true;
						if (player.venomDamage == 6) {
							Combat.createHitsplatOnPlayerPvp(attacker, player, player.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
						} else if (player.venomDamage >= 20) {
							Combat.createHitsplatOnPlayerPvp(attacker, player, 20, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
						} else {
							Combat.createHitsplatOnPlayerPvp(attacker, player, player.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
						}
						player.venomDamage += 2;
						player.venomHits += 1;
						attacker.ignoreInCombat = false;
					}
					player.venomHitsplatsLeft--;
					player.venomTicksUntillDamage = TICKS_UNTIL_VENOM;
				}
			}

			@Override
			public void stop() {
				player.venomEvent = false;
				informClientOfVenomOff(player);
			}
		}, 1);
	}

	public static boolean isReduceVenomToPoison(Player player) {
		if (player.venomEvent) {
			Venom.removeVenom(player);
			player.venomEvent = false;
			player.poisonImmune = 0; // Reset poison immunity so the player gets poisoned after reducing venom
			Poison.appendPoison(null, player, false, 6);
			return true;
		}
		return false;
	}

}
