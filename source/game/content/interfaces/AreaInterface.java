package game.content.interfaces;

import core.GameType;
import game.content.minigame.AutoDice;
import game.content.minigame.Minigame;
import game.content.minigame.TargetSystem;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.worldevent.Tournament;
import game.entity.EntityType;
import game.player.Area;
import game.player.Player;

/**
 * Update walkable interfaces that are changed if the player has entered an area.
 *
 * @author MGT Madness 2-11-2013
 */

public class AreaInterface {
	
	public static void updateWalkableInterfaces(final Player player) {
		if (player.getType() == EntityType.PLAYER_PET) {
			player.getPA().showOption(3, 0, "Talk-to", 1);
			return;
		}
		TargetSystem.updateArrowHint(player);

		Minigame minigame = player.getMinigame();

		if (minigame != null && minigame.inside(player)) {
			minigame.onUpdateWalkableInterface(player);
		} else if (player.isInZombiesMinigame()) {
			player.getPA().walkableInterface(player.waitingForWave ? 20240 : 20230);
			player.getPA().showOption(3, 0, "Null", 1);
		} else if (Area.inDiceZone(player) && AutoDice.AUTOMATIC_DICE_ENABLED) {
			player.getPA().showOption(3, 0, "Gamble", 1);
		} else if (System.currentTimeMillis() - player.timeExitedWilderness < 10000 || System.currentTimeMillis() - player.timeVictimExitedWilderness < 10000) {
			player.getPA().walkableInterface(player.useBottomRightWildInterface ? 24395 : 24390);
			player.getPA().showOption(3, 0, "Attack", 1);
		} else if (Area.inDangerousPvpArea(player) && !Area.inCityPvpArea(player) || Area.inSafePkFightZone(player)) {
			PlayerMiscContent.calculateWildernessLevel(player);
			InterfaceAssistant.wildernessInterface(player);
			player.getPA().showOption(3, 0, "Attack", 1);
		} else if (player.tournamentTarget >= 0) {
			player.getPA().walkableInterface(-1);
			player.getPA().showOption(3, 0, "Attack", 1);
		} else if (player.getHeight() == 20 && player.tournamentTarget == -1) {
			player.getPA().sendFrame126("Lobby: " + Tournament.playerListLobby.size(), 25982);
			player.getPA().sendFrame126("Tournament: " + Tournament.playerListTournament.size(), 25983);
			player.getPA().walkableInterface(25980);
			player.getPA().showOption(3, 0, "Null", 1);
		} else if (Area.inDuelArena(player)) {
			player.getPA().walkableInterface(201);
			if (player.getDuelStatus() == 5) {
				player.getPA().showOption(3, 0, "Attack", 1);
			} else {
				player.getPA().showOption(3, 0, "Challenge", 1);
			}
		} else if (player.getWieldedWeapon() == 7671 || player.getWieldedWeapon() == 7673) //Boxing gloves
		{
			player.getPA().showOption(3, 0, "Punch", 1);
		}
		else if (player.getWieldedWeapon() == 4566 || player.getWieldedWeapon() == 20590 && GameType.isOsrs()) //Rubber chicken || Stale baguette
		{
			player.getPA().showOption(3, 0, "Whack", 1);
		} else if (player.getWieldedWeapon() == 10501) //Snowball
		{
			player.getPA().showOption(3, 0, "Pelt", 1);
		} else if (Area.inZombieWaitingRoom(player)) {
			player.getPA().walkableInterface(-1);
			player.getPA().showOption(3, 0, "Duo", 1);
		}

		// Barrows interface.
		else if (Area.isInBarrowsChestArea(player)) {
			player.getPA().walkableInterface(22045);
		} else if (Area.inGodWarsDungeon(player)) {
			player.getPA().walkableInterface(25957);
		} else if (Area.inCityPvpArea(player)) {
			displayCityPvpInterface(player, 26000);
			player.getPA().showOption(3, 0, "Attack", 1);
		} else if (Area.inEdgevilleBankPvpInstance(player.getX(), player.getY(), player.getHeight()) && player.getHeight() == 4) {
			player.getPA().sendFrame99(0);
			displayCityPvpInterface(player, 26005);
			player.getPA().showOption(3, 0, "Null", 1);
		} else {
			player.getPA().sendFrame99(0);
			player.getPA().walkableInterface(-1);
			player.getPA().showOption(3, 0, "Null", 1);
		}
		if (!player.hasMultiSign && Area.inMulti(player.getX(), player.getY())) {
			player.hasMultiSign = true;
			player.getPA().multiWay(1);
		} else if (player.hasMultiSign && !Area.inMulti(player.getX(), player.getY())) {
			player.hasMultiSign = false;
			player.getPA().multiWay(-1);
		}
	}

	private static void displayCityPvpInterface(Player player, int interfaceId) {
		int minimumLevel = player.getCombatLevel() - 17;
		int maximumLevel = player.getCombatLevel() + 17;
		if (minimumLevel < 3) {
			minimumLevel = 3;
		}
		if (maximumLevel > 126) {
			maximumLevel = 126;
		}
		player.getPA().sendFrame126(minimumLevel + "-" + maximumLevel, 26002);
		player.getPA().walkableInterface(interfaceId);


	}

}
