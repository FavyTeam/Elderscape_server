package game.player;

import core.Server;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.combat.Combat;
import game.content.interfaces.donator.DonatorShop;
import game.content.miscellaneous.ServerShutDownUpdate;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.entity.Entity;
import game.item.ItemAssistant;
import game.log.GameTickLog;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.object.clip.Region;
import game.player.event.CycleEventAdapter;
import game.player.event.CycleEventContainer;
import game.player.movement.Movement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import network.packet.Stream;
import utility.HighestPlayerCount;
import utility.Misc;
import utility.TimeChanged;

public class PlayerHandler {

	/**
	 * Player slots are occupied from index 1 and above.
	 */
	public static Player players[] = new Player[ServerConstants.MAXIMUM_PLAYERS];

	public static List<Player> list = new ArrayList<Player>();

	private static int playerCount;

	public static boolean updateAnnounced;

	public static boolean updateRunning;

	public static int updateSeconds;

	public static long updateStartTime;

	private boolean kickAllPlayers = false;

	public ArrayList<Integer> playersPidList = new ArrayList<Integer>();
	public static ArrayList<String> disconnectReason = new ArrayList<String>();
	public static ArrayList<String> individualPlayerPacketLog = new ArrayList<String>();

	private static Player[] copyPlayers() {
		Player[] clients = new Player[players.length];

		System.arraycopy(players, 0, clients, 0, players.length);

		return clients;
	}

	public static List<Player> getPlayers() {
		return Arrays.stream(copyPlayers()).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static List<Player> getPlayers(Predicate<Player> retainIf) {
		return Arrays.stream(copyPlayers()).filter(Objects::nonNull).filter(retainIf).collect(Collectors.toList());
	}

	public static Player getPlayerForName(long nameAsLong) {
		for (Player player : players) {
			if (player == null) {
				continue;
			}
			if (player.getNameAsLong() == nameAsLong) {
				return player;
			}
		}
		return null;
	}

	public static Player getPlayerForName(String name) {
		return getPlayerForName(Misc.stringToLong(name));
	}


	static {
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			players[i] = null;
		}
	}

	public boolean newPlayerClient(Player client1) {
		int slot = -1;
		for (int i = 1; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			if (players[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1)
			return false;
		client1.handler = this;
		client1.setPlayerId(slot);
		players[slot] = client1;
		players[slot].setActive(true);
		return true;
	}

	public static int getRealPlayerCount() {
		return playerCount;
	}

	public static int getBoostedPlayerCount() {
		return playerCountBoostedUpdatedEveryMinute;
	}

	public static int playerCountBoostedUpdatedEveryMinute;

	public static void setPlayerCount(int amount) {
		playerCount = amount;
	}

	public void updatePlayerNames() {
		setPlayerCount(0);
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player loop = players[i];
			if (loop == null) {
				continue;
			}
			if (loop.bot) {
				continue;
			}
			setPlayerCount(getRealPlayerCount() + 1);
		}
		if (getRealPlayerCount() > HighestPlayerCount.highestPlayerCountDaily) {
			HighestPlayerCount.highestPlayerCountDaily = getRealPlayerCount();
		}
	}

	public static boolean isPlayerOn(String playerName) {
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player loop = PlayerHandler.players[i];
			if (loop == null) {
				continue;
			}
			if (playerName.toLowerCase().equals(loop.getPlayerName().toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * All packets handled every 600ms.
	 */
	public void packetProcessing() {
		GameTickLog.packetTickDuration = System.currentTimeMillis();
		try {
			playersPidList.clear();
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (PlayerHandler.players[i] == null) {
					continue;
				}
				try {
					playersPidList.add(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Collections.shuffle(playersPidList);
			disconnectReason.add(Misc.getDateAndTimeAndSeconds() + " GameTick: packetProcessing: " + playersPidList.size());
			for (int index = 0; index < playersPidList.size(); index++) {
				Player player = players[playersPidList.get(index)];
				if (player == null) {
					continue;
				}
				player.canFlush = false;
				player.lootThisTick = 0;
				player.startHeight = player.getHeight();
				individualPlayerPacketLog.add(Misc.getDateAndTimeAndSeconds() + " " + player.getPlayerName() + ", " + player.queuedPackets.size());
				while (player.processQueuedPackets()) ;
				itemAndBankUpdate(player);
				if ((player.walkingPacketQueue[0] > 0 || player.walkingPacketQueue[1] > 0) && !player.stopMovementQueue) {
					if (!player.ignoreBigNpcWalkPacket) {
						Movement.playerWalk(player, player.walkingPacketQueue[0], player.walkingPacketQueue[1]);
					}
					player.walkingPacketQueue[0] = 0;
					player.walkingPacketQueue[1] = 0;
				}
				player.ignoreBigNpcWalkPacket = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		GameTickLog.packetTickDuration = System.currentTimeMillis() - GameTickLog.packetTickDuration;
	}

	private void itemAndBankUpdate(Player player) {
		if (player.getInventoryUpdate()) {
			ItemAssistant.resetItems(player, 3214); // Update inventory.
			player.getPA().requestUpdates();
			if (player.getInterfaceIdOpened() == 3900 || DonatorShop.usingDonatorShopInterface(player)) {
				ItemAssistant.resetItems(player, 3823); // Update a different inventory that is used while viewing shops
			}
			if (player.getInterfaceIdOpened() == 24959) {
				ItemAssistant.resetItems(player, 5064); // Update a different inventory that is used while viewing bank
			}
			player.setInventoryUpdate(false);
		}
		if (ItemAssistant.updateEquipment(player)) {
			ItemAssistant.calculateEquipmentBonuses(player);

			// Equipment interface and bank equipment interface.
			if (player.getInterfaceIdOpened() == 15150 || player.getInterfaceIdOpened() == 15106) {
				ItemAssistant.updateEquipmentBonusInterface(player);
			}
			if (player.itemWorn) {
				SoundSystem.sendSound(player, 230, 0);
				player.itemWorn = false;
			}
			ItemAssistant.saveEquipment(player);
		}
		player.weaponSpecialUpdatedId = -5;

		if (player.soundToSend > 0) {
			SoundSystem.sendSound(player, player.soundToSend, player.soundDelayToSend);
			player.soundToSend = 0;
		}

		for (int index = 0; index < player.skillTabMainToUpdate.size(); index++) {
			Skilling.updateSkillTabFrontTextMain(player, player.skillTabMainToUpdate.get(index));
		}
		player.skillTabMainToUpdate.clear();
		for (int index = 0; index < player.queuedPacketMessage.size(); index++) {
			player.getPA().sendFilterableMessage(player.queuedPacketMessage.get(index));
		}
		player.queuedPacketMessage.clear();
		if (player.queuedSetSkillLevel[0] > 0 || player.queuedSetSkillLevel[1] > 0 || player.queuedSetSkillLevel[2] > 0) {
			player.getPA().setSkillLevel(player.queuedSetSkillLevel[0], player.queuedSetSkillLevel[1], player.queuedSetSkillLevel[2]);
			player.queuedSetSkillLevel[0] = 0;
			player.queuedSetSkillLevel[1] = 0;
			player.queuedSetSkillLevel[2] = 0;
		}
		if (player.bankUpdated) {
			Bank.resetBank(player, false);
			player.bankUpdated = false;
		}

	}

	public static String currentTime = "";

	public static String currentDate = "";

	public void playerGameTick() {
		GameTickLog.playerTickDuration = System.currentTimeMillis();
		updatePlayerNames();
		boolean update1 = TimeChanged.updateTimeAndDate();
		if (kickAllPlayers) {
			for (int i = 1; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (players[i] != null) {
					players[i].setDisconnected(true, "kickAllPlayers");
				}
			}
		}

		//store all not nulled indexes in a list
		//then randomize the order of the list, then execute the list
		playersPidList.clear();
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			if (players[i] == null || !players[i].isActive()) {
				continue;
			}
			try {
				playersPidList.add(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Collections.shuffle(playersPidList);
		try {
			for (int index = 0; index < playersPidList.size(); index++) {
				Player player = players[playersPidList.get(index)];
				if (player == null) {
					continue;
				}
				if (hasDisconnectRequirements(player)) {
					LogOutUpdate.main(players, player.getPlayerId());
					continue;
				}
				else {
					player.manualLogOut = false;
				}
				if (update1) {
					TimeChanged.updateTimeForPlayer(player);
				}
				player.getEventHandler().cycle();
				PlayerContentTick.preMovementContentTick(player); //Following npc/player methods
				Movement.postProcessing(player);
				Movement.getNextPlayerMovement(player); //Move the player depending on what follow methods said to do or packets from client says
				PlayerContentTick.afterMovementContentTick(player); // All combat here
				PlayerContentTick.updateClientHeight(player, false);
				player.preProcessing();
				player.onSequence();
				timedOutHandler(player);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//EntityDamageQueue.process();

		GameTickLog.playerTickDuration = System.currentTimeMillis() - GameTickLog.playerTickDuration;

		GameTickLog.playerTickDuration2 = System.currentTimeMillis();

		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			if (players[i] == null || !players[i].isActive()) {
				continue;
			}
			try {
				if (hasDisconnectRequirements(players[i])) {
					LogOutUpdate.main(players, i);
				} else {
					if (!players[i].initialized) {
						int duplicate = 0;
						for (int a = 0; a < ServerConstants.MAXIMUM_PLAYERS; a++) {
							Player loop = PlayerHandler.players[a];
							if (loop == null) {
								continue;
							}
							if (loop.getPlayerName().equalsIgnoreCase(players[i].getPlayerName())) {
								duplicate++;
							}
						}
						if (duplicate > 1) {
							players[i] = null;
						} else {
							LogInUpdate.update(players[i]);
							players[i].onAdd();
							players[i].initialized = true;
						}
					} else {
						players[i].update();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			PlayerContentTick.updateClientHeight(players[i], false);
		}
		if (updateRunning && !updateAnnounced) {
			updateAnnounced = true;
			Server.UpdateServer = true;
		}
		if (updateRunning && (System.currentTimeMillis() - updateStartTime > (updateSeconds * 1000))) {
			kickAllPlayers = true;
		}

		GameTickLog.playerTickDuration2 = System.currentTimeMillis() - GameTickLog.playerTickDuration2;

		GameTickLog.playerTickDuration3 = System.currentTimeMillis();

		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			if (players[i] == null || !players[i].isActive()) {
				continue;
			}
			try {
				players[i].clearUpdateFlags();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (kickAllPlayers) {
			if (System.currentTimeMillis() - timeKickedAllPlayers >= 60000) {
				timeKickedAllPlayers = System.currentTimeMillis();
				ServerShutDownUpdate.serverRestartContentUpdate("Server update", true, true);

			}
		}

		GameTickLog.playerTickDuration3 = System.currentTimeMillis() - GameTickLog.playerTickDuration3;
	}

	public boolean hasDisconnectRequirements(Player player) {
		if (kickAllPlayers) {
			return true;
		}

		if (player.getDead()) {
			return false;
		}
		if (player.isInGambleMatch()) {
			return false;
		}
		if (System.currentTimeMillis() < player.getTimeCanDisconnectAtBecauseOfCombat()) {
			return false;
		}
		if (!player.bot) {
			// Golden skulled.
			if (player.headIconPk == 2) {
				// If not in combat and has been not sending any packets for 5 minutes.
				if (!Combat.inCombat(player) && player.getTimeOutCounter() > 500) {
					return true;
				}
				return false;
			}
		}

		if (System.currentTimeMillis() - player.timeDragonSpearEffected <= 12000) {
			return false;
		}
		if (System.currentTimeMillis() - player.timeDied <= 700) {
			return false;
		}

		if (player.dragonSpearTicksLeft > 0) {
			return false;
		}
		// Client crashes when logging in for the first time after launching client, randomly, so disconnect them.
		if (!Combat.inCombat(player) && !player.bot) {
			if (player.getTimeOutCounter() >= 16) {
				PlayerHandler.disconnectReason.add("[" + Misc.getDateAndTimeAndSeconds() + "] " + player.getPlayerName() + ", hasDisconnectRequirements: Not in combat: " + player.getTimeOutCounter());
				return true;
			}
		}
		if (player.getTimeOutCounter() >= ServerConstants.TIMEOUT && !player.bot) {
			PlayerHandler.disconnectReason.add("[" + Misc.getDateAndTimeAndSeconds() + "] " + player.getPlayerName() + ", hasDisconnectRequirements: In combat possibility: " + player.getTimeOutCounter());
			return true;
		}
		if (Combat.inCombat(player) && !player.isAdministratorRank()) {
			return false;
		}
		if (player.getDuelStatus() >= 5 && Area.inDuelArenaRing(player)) {
			return false;
		}
		if (player.manualLogOut) {
			player.getPA().forceToLogInScreen();
			PlayerHandler.disconnectReason.add("[" + Misc.getDateAndTimeAndSeconds() + "] " + player.getPlayerName() + ", hasDisconnectRequirements: Manual logout: " + player.getTimeOutCounter());
			return true;
		}

		if (!player.isAdministratorRank()) {
			if (System.currentTimeMillis() - player.timeNpcAttackedPlayerLogOutTimer <= 10000) {
				return false;
			}
		}

		if (player.doingAnAction() && !player.bot) {
			return false;
		}

		if (!player.isDisconnected()) {
			return false;
		}
		PlayerHandler.disconnectReason.add("[" + Misc.getDateAndTimeAndSeconds() + "] " + player.getPlayerName() + ", hasDisconnectRequirements: Default: " + player.getTimeOutCounter());
		return true;
	}

	public static boolean canTakeAction;

	/**
	 * True to logOut players, called after backup.zip is created.
	 */
	public static boolean logOut;

	/**
	 * True to restart server, called after backup.zip is created.
	 */
	public static boolean restart;

	public static long timeKickedAllPlayers;

	public static void getUpTime() {
		int hours = (int) ((System.currentTimeMillis() - Server.timeServerOnline) / ServerConstants.MILLISECONDS_HOUR);
		if (hours > 0) {
			Misc.print("Uptime: " + hours + " hour" + (hours == 1 ? "." : "s."));
		} else {
			Misc.print("Uptime: " + ((System.currentTimeMillis() - Server.timeServerOnline) / 60000) + " minutes.");
		}

	}

	public void updateNpc(Player player, Stream str) {
		if (player.bot) {
			return;
		}
		updateBlock.currentOffset = 0;
		if (!player.bot) {
			str.createFrameVarSizeWord(65);
		}
		str.initBitAccess();

		str.writeBits(8, player.npcListSize);
		int size = player.npcListSize;
		player.npcListSize = 0;
		for (int i = 0; i < size; i++) {
			if (!player.rebuildNpcList && !player.npcList[i].isRequiringReplacement() && player.npcList[i].isVisible() && player.playerAssistant.withinDistance(player.npcList[i])) {
				player.npcList[i].updateNpcMovement(str);
				player.npcList[i].appendNpcUpdateBlock(updateBlock, player, false);
				player.npcList[player.npcListSize++] = player.npcList[i];
			} else {
				int id = player.npcList[i].npcIndex;

				player.npcList[i].getLocalPlayers().remove(player);
				player.getLocalNpcs().remove(player.npcList[i]);
				player.npcInListBitmap[id >> 3] &= ~(1 << (id & 7));
				str.writeBits(1, 1);
				str.writeBits(2, 3);
			}
		}
		boolean checkSurroundingRegions = true;

		if (checkSurroundingRegions) {
			List<Region> regions = Region.getSurrounding(player.getX(), player.getY());

			regions.forEach(region -> {
				if (region == null) {
					return;
				}
				region.forEachNpc(npc -> {
					if (npc != null) {
						int id = npc.npcIndex;

						if (npc.isRequiringReplacement() || !npc.isVisible()) {
							return;
						}
						if (!player.rebuildNpcList && (player.npcInListBitmap[id >> 3] & (1 << (id & 7))) != 0) {
							return;
						}
						if (player.playerAssistant.withinDistance(npc)) {
							//player.npcInvisibleDebug.add("Here1: " + player.getPlayerName() + ", " + npc.name);
							player.addNewNpc(npc, str, updateBlock);
							player.getLocalNpcs().add(npc);
							if (!npc.getLocalPlayers().contains(player)) {
								npc.getLocalPlayers().add(player);
								npc.onAddToLocalList(player);
							}
						}
					}
				});
			});
		} else {
			for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
				Npc npc = NpcHandler.npcs[i];

				if (npc != null) {
					int id = npc.npcIndex;

					if (npc.isRequiringReplacement() || !npc.isVisible()) {
						continue;
					}
					if (!player.rebuildNpcList && (player.npcInListBitmap[id >> 3] & (1 << (id & 7))) != 0) {
						continue;
					}
					if (player.playerAssistant.withinDistance(NpcHandler.npcs[i])) {
						//player.npcInvisibleDebug.add("Here1: " + player.getPlayerName() + ", " + NpcHandler.npcs[i].name);
						player.addNewNpc(NpcHandler.npcs[i], str, updateBlock);
						player.getLocalNpcs().add(NpcHandler.npcs[i]);
						if (!NpcHandler.npcs[i].getLocalPlayers().contains(player)) {
							NpcHandler.npcs[i].getLocalPlayers().add(player);
							NpcHandler.npcs[i].onAddToLocalList(player);
						}
					}
				}
			}
		}
		player.rebuildNpcList = false;

		if (updateBlock.currentOffset > 0) {
			str.writeBits(14, 16383);
			str.finishBitAccess();
			str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
		} else {
			str.finishBitAccess();
		}
		if (!player.bot) {
			str.endFrameVarSizeWord();
		}
	}

	public Stream updateBlock = new Stream(new byte[ServerConstants.BUFFER_SIZE]);

	public void updatePlayer(Player plr, Stream str) {
		if (plr.bot) {
			return;
		}
		updateBlock.currentOffset = 0;
		if (updateRunning && !updateAnnounced && !plr.bot) {
			str.createFrame(114);
			str.writeWordBigEndian(updateSeconds * 50 / 30);
		}
		plr.updateThisPlayerMovement(str);
		boolean saveChatTextUpdate = plr.isChatTextUpdateRequired();
		plr.setChatTextUpdateRequired(false);
		plr.appendPlayerUpdateBlock(updateBlock, false);
		plr.setChatTextUpdateRequired(saveChatTextUpdate);
		str.writeBits(8, plr.playerListSize);
		int size = plr.playerListSize;
		if (size > 255) {
			size = 255;
		}
		plr.playerListSize = 0;
		int MAXIMUM_APPEARANCE_PACKET_UPDATE = 40; //61 then client crashes
		int updateAppearancesUpdated = 0;
		for (int i = 0; i < size; i++) {
			if (!plr.didTeleport && !plr.playerList[i].didTeleport && plr.playerList[i].isVisible() && plr.playerAssistant.withinDistance(plr.playerList[i])) {
				plr.playerList[i].updatePlayerMovement(str);
				if (plr.playerList[i].appendPlayerUpdateBlock(updateBlock, updateAppearancesUpdated == MAXIMUM_APPEARANCE_PACKET_UPDATE)) {
					updateAppearancesUpdated++;
				}
				plr.playerList[plr.playerListSize] = plr.playerList[i];
				plr.playerListSize++;
			} else {
				int id = plr.playerList[i].getPlayerId();

				plr.getLocalPlayers().remove(plr.playerList[i]);
				plr.playerInListBitmap[id >> 3] &= ~(1 << (id & 7));
				str.writeBits(1, 1);
				str.writeBits(2, 3);
			}
		}
		int MAXIMUM_NEW_PLAYERS_ADDED = 30;
		int currentCount = 0;
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			if (updateAppearancesUpdated == MAXIMUM_APPEARANCE_PACKET_UPDATE) {
				break;
			}
			if (players[i] == null || !players[i].isActive() || players[i] == plr || !players[i].isVisible()) {
				continue;
			}
			int id = players[i].getPlayerId();
			if ((plr.playerInListBitmap[id >> 3] & (1 << (id & 7))) != 0) {
				continue;
			}
			if (!plr.playerAssistant.withinDistance(players[i])) {
				continue;
			}
			currentCount++;
			updateAppearancesUpdated++;
			//plr.getEventHandler().singularExecution(plr, plr::setFocusRelativeToFace, 1);
			plr.addNewPlayer(players[i], str, updateBlock);
			plr.getLocalPlayers().add(players[i]);
			if (currentCount == MAXIMUM_NEW_PLAYERS_ADDED) {
				break;
			}
		}

		if (updateBlock.currentOffset > 0) {
			str.writeBits(11, 2047);
			str.finishBitAccess();
			str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
		} else {
			str.finishBitAccess();
		}

		if (!plr.bot) {
			str.endFrameVarSizeWord();
		}
	}

	private void timedOutHandler(Player player) {
		player.setTimeOutCounter(player.getTimeOutCounter() + 1);
		if (player.getTimeOutCounter() > ServerConstants.TIMEOUT && !player.bot) {
			player.setDisconnected(true, "timedOutHandler");
		}
	}

}
