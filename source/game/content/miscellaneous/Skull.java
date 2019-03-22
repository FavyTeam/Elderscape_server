package game.content.miscellaneous;

import core.ServerConstants;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.prayer.book.regular.RegularPrayer;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * White and red skull.
 *
 * @author MGT Madness, created on 29-01-2014.
 */
public class Skull {


	/**
	 * The least amount of killstreak to announce a bounty or a shutdown.
	 */
	public static final int KILL_STREAK_LEAST = 12;


	//If i update this, also update the pkingloot command
	public static enum SkullData {
		TIER_6_SKULL(3),
		TIER_5_SKULL(6),
		TIER_4_SKULL(12),
		TIER_3_SKULL(20),
		TIER_2_SKULL(40),
		TIER_1_SKULL(75),
		TIER_0_SKULL(150);

		public int killstreak;



		private SkullData(int killstreak) {
			this.killstreak = killstreak;
		}
	}

	public static int getShutDownBloodMoney(int index) {
		switch (index) {
			case 0:
				return Artefacts.tier6SkullShutDownBloodMoneyAmount;
			case 1:
				return Artefacts.tier5SkullShutDownBloodMoneyAmount;
			case 2:
				return Artefacts.tier4SkullShutDownBloodMoneyAmount;
			case 3:
				return Artefacts.tier3SkullShutDownBloodMoneyAmount;
			case 4:
				return Artefacts.tier2SkullShutDownBloodMoneyAmount;
			case 5:
				return Artefacts.tier1SkullShutDownBloodMoneyAmount;
			case 6:
				return Artefacts.tier0SkullShutDownBloodMoneyAmount;
		}
		return 0;
	}

	public static int getSkullKillstreakBloodMoney(int index) {
		switch (index) {
			case 0:
				return Artefacts.tier6SkullBloodMoneyAmount;
			case 1:
				return Artefacts.tier5SkullBloodMoneyAmount;
			case 2:
				return Artefacts.tier4SkullBloodMoneyAmount;
			case 3:
				return Artefacts.tier3SkullBloodMoneyAmount;
			case 4:
				return Artefacts.tier2SkullBloodMoneyAmount;
			case 5:
				return Artefacts.tier1SkullBloodMoneyAmount;
			case 6:
				return Artefacts.tier0SkullBloodMoneyAmount;
		}
		return 0;
	}

	public static void updateSkullOnLogIn(Player player) {
		if (!player.getWhiteSkull() && !player.getRedSkull()) {
			return;
		}
		player.skullVisualType = getSkullToShow(player);
	}

	public static int getSkullToShow(Player player) {
		int skull = 0;

		boolean skullTier = false;
		if (player.getRedSkull() || player.getWhiteSkull()) {
			for (int index = 0; index < SkullData.values().length; index++) {
				SkullData instance = SkullData.values()[index];
				if (player.currentKillStreak >= instance.killstreak) {
					skull = index;
					skullTier = true;
				}
			}
		}
		if (skullTier) {
			skull += 4;
			if (player.getRedSkull()) {
				skull += 11;
			}
		} else {
			skull = player.getRedSkull() ? 1 : player.getWhiteSkull() ? 0 : -1;
		}
		player.setAppearanceUpdateRequired(true);
		return skull;
	}

	public static boolean isSkulled(Player player) {
		if (player.getWhiteSkull() || player.getRedSkull()) {
			return true;
		}
		return false;
	}


	/**
	 * Start the cycle event for how long the player killing skull will last for.
	 */
	public static void startSkullTimerEvent(final Player player) {

		if (player.isUsingSkullTimerEvent || player.skullTimer == 0) {
			return;
		}
		if (player.isCombatBot()) {
			return;
		}
		player.isUsingSkullTimerEvent = true;

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				player.skullTimer--;
				if (player.skullTimer <= 1) {
					Skull.clearSkull(player);
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.isUsingSkullTimerEvent = false;
			}
		}, 1);

	}

	/**
	 * Activate white skull.
	 *
	 * @param player The associated player.
	 */
	public static void whiteSkull(Player player) {
		// Golden skulled.
		if (player.headIconPk == 2) {
			return;
		}
		player.timeScannedForWildernessRisk = 0;
		player.setWhiteSkull(true);
		player.setRedSkull(false);
		player.skullTimer = 1200;
		startSkullTimerEvent(player);
		player.headIconPk = 0;
		player.skullVisualType = getSkullToShow(player);
		player.getPA().requestUpdates();
	}

	/**
	 * Skull the attacker, this method is called during combat.
	 *
	 * @param attacker The player attacking.
	 * @param victim The player receiving the attack.
	 */
	public static void combatSkull(Player attacker, Player victim) {
		if (Area.inDangerousPvpArea(attacker)) {
			if (!attacker.attackedPlayers.contains(victim.getPlayerName()) && !victim.attackedPlayers.contains(attacker.getPlayerName())) {
				if (!Misc.timeElapsed(attacker.timeInCombat, Misc.getSecondsToMilliseconds(5)) && attacker.lastPlayerAttackedName.equals(victim.getPlayerName())) {
					return;
				}
				attacker.attackedPlayers.add(victim.getPlayerName());
				if (!attacker.getRedSkull()) {
					Skull.whiteSkull(attacker);
				}
			}
		}
	}

	/**
	 * Activate red skull.
	 *
	 * @param player The associated player.
	 */
	public static void redSkull(Player player) {
		player.setWhiteSkull(false);
		player.setRedSkull(true);
		player.skullTimer = 1200;
		player.timeScannedForWildernessRisk = 0;
		startSkullTimerEvent(player);
		player.headIconPk = 1;
		player.skullVisualType = getSkullToShow(player);
		if (player.prayerActive[ServerConstants.PROTECT_ITEM]) {
			RegularPrayer.activatePrayer(player, ServerConstants.PROTECT_ITEM);
		}
		player.getPA().requestUpdates();
	}

	/**
	 * Activate red skull.
	 *
	 * @param player The associated player.
	 */
	public static void goldenSkullBoss(Player player) {
		player.setWhiteSkull(true);
		player.skullTimer = 144000;
		player.timeScannedForWildernessRisk = 0;
		startSkullTimerEvent(player);
		player.headIconPk = 2;
		player.skullVisualType = 2;
		player.getPA().requestUpdates();
	}

	/**
	 * Turn off the skull.
	 *
	 * @param player The associated player.
	 */
	public static void clearSkull(Player player) {
		if (player.isCombatBot()) {
			return;
		}
		player.setWhiteSkull(false);
		player.setRedSkull(false);
		player.skullVisualType = -1;
		player.headIconPk = -1;
		player.attackedPlayers.clear();
		player.getPA().requestUpdates();
		ItemsKeptOnDeath.updateInterface(player);
	}


}
