package game.content.minigame.zombie;

import core.Server;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.highscores.HighscoresZombie;
import game.content.miscellaneous.Teleport;
import game.content.quicksetup.QuickSetUp;
import game.item.GroundItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

import java.util.ArrayList;

/**
 * Zombie minigame.
 *
 * @author MGT Madness, created on 05-10-2016.
 */
public class Zombie {

	/**
	 * Send duo request.
	 */
	public static void requestDuo(Player playerWhoSentRequest, Player playerReceivingRequest) {
		/*
			playerWhoSentRequest.resetPlayerIdToFollow();
			if (ZombieGameInstance.getCurrentInstance(playerWhoSentRequest.getPlayerName()) != null)
			{
					playerWhoSentRequest.getPA().sendMessage("You still have an active zombies game.");
					return;
			}
			if (ItemAssistant.hasEquipment(playerWhoSentRequest))
			{
					playerWhoSentRequest.getPA().sendMessage("Please bank all your items.");
					return;
			}
			for (int i = 0; i < playerWhoSentRequest.playerItems.length; i++)
			{
					if (playerWhoSentRequest.playerItems[i] >= 1)
					{
							playerWhoSentRequest.getPA().sendMessage("Please bank all your items.");
							return;
					}
			}
			// If the person i am requesting from has already requested a duel from me, then start minigame.
			if (playerReceivingRequest.requestDuoName.equals(playerWhoSentRequest.getPlayerName()))
			{
					playerWhoSentRequest.requestDuoName = "";
					playerReceivingRequest.requestDuoName = "";
					startMinigame(playerReceivingRequest, playerWhoSentRequest);
					return;
			}
		
			playerWhoSentRequest.playerAssistant.sendMessage("Sending duo request...");
			playerWhoSentRequest.turnPlayerTo(playerReceivingRequest.getX(), playerReceivingRequest.getY());
			playerWhoSentRequest.requestDuoName = playerReceivingRequest.getPlayerName();
			playerReceivingRequest.playerAssistant.sendMessage(":packet:zombierequest " + playerWhoSentRequest.getPlayerName());
			*/
	}

	/**
	 * Update the account on log-in.
	 */
	public static void logInUpdate(Player player) {
		if (inZombieMiniGameArea(player, player.getX(), player.getY()) && player.getHeight() > 0) {
			int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
			if (minigameInstanceIndex == -1) {
				Teleport.startTeleport(player, 3659, 3516, 0, "ZOMBIE");
				ItemAssistant.deleteAllItems(player);
			} else {
				Player partner = PlayerHandler.players[ZombieGameInstance.getPartnerInstance(player).getPlayerId()];
				if (player.getHeight() != partner.getHeight()) {
					return;
				}
				player.setZombiePartnerId(ZombieGameInstance.getPartnerInstance(player).getPlayerId());
				player.setInZombiesMinigame(true);
				partner.setZombiePartnerId(player.getPlayerId());
				player.waitingForWave = partner.waitingForWave;
				if (ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName().equals(player.getPlayerName())) {
					player.zombieWavePoints = ZombieGameInstance.instance.get(minigameInstanceIndex).playerOnePoints;
					ZombieGameInstance.instance.get(minigameInstanceIndex).playerOneAvailable = true;
				} else {
					player.zombieWavePoints = ZombieGameInstance.instance.get(minigameInstanceIndex).playerTwoPoints;
					ZombieGameInstance.instance.get(minigameInstanceIndex).playerTwoAvailable = true;
				}

				player.getPA().sendFrame126("Wave: " + ZombieGameInstance.instance.get(minigameInstanceIndex).wave, 20232);
				player.getPA().sendFrame126("Zombies left: " + ZombieGameInstance.instance.get(minigameInstanceIndex).totalWaveZombiesLeft, 20233);
				boolean iAmPlayerOne = false;
				if (ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName().equals(player.getPlayerName())) {
					iAmPlayerOne = true;
				}
				updateContributionInterfaceText(player, iAmPlayerOne, minigameInstanceIndex);

				if (player.isReadyForNextZombieWave) {
					player.getPA().sendFrame126("You are ready.", 20242);
				} else {
					player.getPA().sendFrame126("You are not ready.", 20242);
				}

				if (partner != null) {
					if (partner.isReadyForNextZombieWave) {
						player.getPA().sendFrame126(partner.getPlayerName() + " is ready.", 20243);
					} else {
						player.getPA().sendFrame126(partner.getPlayerName() + " is not ready.", 20243);
					}
				}
				if (!player.getPA().withinDistance(partner) && player.getHeight() == partner.getHeight()) {
					player.getPA().movePlayer(partner.getX(), partner.getY(), player.getHeight());
				}
			}
		}
	}

	/**
	 * Start the minigame.
	 */
	@SuppressWarnings("unused")
	private static void startMinigame(Player playerOne, Player playerTwo) {

		Achievements.checkCompletionSingle(playerOne, 1093);
		Achievements.checkCompletionSingle(playerTwo, 1093);
		QuickSetUp.heal(playerOne, false, true);
		QuickSetUp.heal(playerTwo, false, true);
		playerOne.getPA().resetStats();
		playerTwo.getPA().resetStats();

		playerOne.setZombiePartnerId(playerTwo.getPlayerId());
		playerTwo.setZombiePartnerId(playerOne.getPlayerId());

		// Create the new minigame instance, these data are shared between both players.
		ZombieGameInstance instance = new ZombieGameInstance(playerOne.getPlayerName(), playerTwo.getPlayerName(), 1, ZombieWaveInstance.getWaveData(1).getChestCoordinates(),
		                                                     getZombiesToSpawn(1, getZombiesTotalForWave(1)), ZombieWaveInstance.getWaveData(1).getChestId(),
		                                                     playerOne.getPlayerId() * (0 + 4), getZombiesTotalForWave(1), ZombieWaveInstance.getWaveData(1).zombieSpawns, true,
		                                                     true);
		ZombieGameInstance.instance.add(instance);

		newWave(playerOne, playerTwo);

		playerOne.getPA().sendMessage("You and " + playerTwo.getPlayerName() + " have started the Zombie survival!");
		playerTwo.getPA().sendMessage("You and " + playerOne.getPlayerName() + " have started the Zombie survival!");

		playerOne.zombieWavePoints = 0;
		playerTwo.zombieWavePoints = 0;
		playerOne.setInZombiesMinigame(true);
		playerTwo.setInZombiesMinigame(true);

		resetInterfaceText(playerOne);
		resetInterfaceText(playerTwo);
	}

	/**
	 * Reset interface text.
	 */
	private static void resetInterfaceText(Player player) {

		player.getPA().sendFrame126("Wave: 1", 20232);
		player.getPA().sendFrame126("Zombies left: " + ZombieWaveInstance.getWaveData(1).getTotalZombies(), 20233);
		player.getPA().sendFrame126("Contribution: ?", 20234);
		player.getPA().sendFrame126("Zombies left: " + getZombiesTotalForWave(1), 20233);
	}

	/**
	 * Load zombie shop.
	 */
	public static boolean loadZombieWaveShop(Player player, int objectUsed, boolean forceOpen) {
		if (player == null) {
			return true;
		}
		for (int index = 0; index < ZombieWaveInstance.verifiedObjects.size(); index++) {
			int objectId = Integer.parseInt(ZombieWaveInstance.verifiedObjects.get(index).substring(0, ZombieWaveInstance.verifiedObjects.get(index).indexOf(",")));
			if (objectUsed == objectId || forceOpen) {
				int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
				if (minigameInstanceIndex == -1) {
					return false;
				}
				if (ZombieGameInstance.instance.get(minigameInstanceIndex) == null) {
					return false;
				}
				if (forceOpen && !player.usingShop) {
					return true;
				}
				if (ZombieGameInstance.instance.get(minigameInstanceIndex).totalWaveZombiesLeft > 0) {
					player.getPA().sendMessage("You may use the chest after the zombies have been defeated.");
					return true;
				}
				player.usingShop = true;
				player.setShopId(9);
				player.getPA().sendFrame248(3824, 3822);
				player.getPA().sendFrame126("Zombie points: " + player.zombieWavePoints, 19301);
				int waveInstanceIndex = ZombieWaveInstance.getWaveDataIndex(ZombieGameInstance.getCurrentInstance(player.getPlayerName()).wave);
				String[] parse = ZombieWaveInstance.instance.get(waveInstanceIndex).getShopList().split(" ");
				ArrayList<String> list = new ArrayList<String>();
				for (int a = 0; a < parse.length; a++) {
					if (parse[a].isEmpty()) {
						continue;
					}
					list.add(parse[a]);
				}
				player.getOutStream().createFrameVarSizeWord(53);
				player.getOutStream().writeWord(3900);
				player.getOutStream().writeWord(list.size());
				for (int i = 0; i < list.size(); i++) {
					int itemId = Integer.parseInt(list.get(i));
					itemId++;
					int itemAmount = 0;

					if (itemAmount > 254) {
						player.getOutStream().writeByte(255);
						player.getOutStream().writeDWord_v2(itemAmount);
					} else {
						player.getOutStream().writeByte(itemAmount);
					}
					if (itemId > ServerConstants.MAX_ITEM_ID || itemId < 0) {
						itemId = ServerConstants.MAX_ITEM_ID;
					}
					player.getOutStream().writeWordBigEndianA(itemId);

				}
				player.getOutStream().endFrameVarSizeWord();
				player.flushOutStream();
				return true;
			}
		}



		return false;
	}

	/**
	 * Load the chest used for shopping.
	 */
	public static void loadChest(Player player) {
		if (!player.isInZombiesMinigame()) {
			return;
		}

		int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
		if (minigameInstanceIndex == -1) {
			return;
		}
		int wave = ZombieGameInstance.instance.get(minigameInstanceIndex).wave;

		player.getPA()
		      .spawnClientObject(ZombieGameInstance.instance.get(minigameInstanceIndex).getChestId(), ZombieGameInstance.instance.get(minigameInstanceIndex).getChestXCoordinate(),
		                         ZombieGameInstance.instance.get(minigameInstanceIndex).getChestYCoordinate(),
		                         ZombieGameInstance.instance.get(minigameInstanceIndex).getChestFace(), 10);
		String[] parse = ZombieWaveInstance.getWaveData(wave).anvilCoordinates.split(" ");
		player.getPA().spawnClientObject(2031, Integer.parseInt(parse[0]), Integer.parseInt(parse[1]), Integer.parseInt(parse[2]), 10);
	}

	/**
	 * Spawn new wave.
	 */
	private static void newWave(final Player playerOne, final Player playerTwo) {
		int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(playerOne.getPlayerName());
		if (minigameInstanceIndex == -1) {
			return;
		}
		final int currentWave = ZombieGameInstance.instance.get(minigameInstanceIndex).getWave();
		int waveDataIndex = ZombieWaveInstance.getWaveDataIndex(currentWave);
		boolean teleported = false;
		// Teleport if map changed.
		if (!ZombieWaveInstance.instance.get(waveDataIndex).getTeleportCoordinates().isEmpty()) {
			teleported = true;
			Teleport.startTeleport(playerOne, ZombieWaveInstance.instance.get(waveDataIndex).getTeleportXCoordinate() + Misc.random(3),
			                       ZombieWaveInstance.instance.get(waveDataIndex).getTeleportYCoordinate() + Misc.random(3),
			                       ZombieGameInstance.instance.get(minigameInstanceIndex).getHeight(), "ZOMBIE");
			if (playerTwo != null) {
				Teleport.startTeleport(playerTwo, ZombieWaveInstance.instance.get(waveDataIndex).getTeleportXCoordinate() + Misc.random(3),
				                       ZombieWaveInstance.instance.get(waveDataIndex).getTeleportYCoordinate() + Misc.random(3),
				                       ZombieGameInstance.instance.get(minigameInstanceIndex).getHeight(), "ZOMBIE");
			}
			ZombieGameInstance.instance.get(minigameInstanceIndex).zombieSpawns = ZombieWaveInstance.instance.get(waveDataIndex).zombieSpawns;
		}

		if (ZombieWaveInstance.instance.get(waveDataIndex).bossId > 0) {
			ZombieGameInstance.instance.get(minigameInstanceIndex).bossSpawnOrder = Misc.random(1, ZombieGameInstance.instance.get(minigameInstanceIndex).totalWaveZombiesLeft - 1);
		}

		if (!teleported) {
			playerOne.getPA().sendMessage(":packet:zombiecountdown");
			if (playerTwo != null) {
				playerTwo.getPA().sendMessage(":packet:zombiecountdown");
			}
			spawnZombiesEvent(playerOne, playerTwo);
		} else {
			CycleEventHandler.getSingleton().addEvent(playerOne, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					container.stop();
				}

				@Override
				public void stop() {
					playerOne.getPA().sendMessage(":packet:zombiecountdown");
					if (playerTwo != null) {
						playerTwo.getPA().sendMessage(":packet:zombiecountdown");
					}
					if (currentWave == 1) {
						starterKit(playerOne);
						if (playerTwo != null) {
							starterKit(playerTwo);
						}
					}
					spawnZombiesEvent(playerOne, playerTwo);
				}
			}, 5);
		}
	}

	/**
	 * Spawn zombies after a delay.
	 */
	private static void spawnZombiesEvent(final Player playerOne, final Player playerTwo) {
		CycleEventHandler.getSingleton().addEvent(playerOne, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				spawnZombies(playerOne, playerTwo);
			}
		}, 9);
	}

	/**
	 * Spawn boss.
	 */
	private static void spawnBoss(Player player) {
		int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
		if (minigameInstanceIndex == -1) {
			return;
		}
		String[] parse;
		ArrayList<String> zombieSpawns = new ArrayList<String>();
		parse = ZombieGameInstance.instance.get(minigameInstanceIndex).zombieSpawns.split(" ");
		int number = 0;
		String fullCoordinate = "";
		for (int a = 0; a < parse.length; a++) {
			if (parse[a].isEmpty()) {
				continue;
			}
			fullCoordinate = fullCoordinate + parse[a] + " ";
			number++;
			if (number == 2) {
				zombieSpawns.add(fullCoordinate);
				number = 0;
				fullCoordinate = "";
			}
		}
		String[] coordinateRandom = zombieSpawns.get(Misc.random(zombieSpawns.size() - 1)).split(" ");
		NpcHandler.spawnNpcZombie(player, ZombieWaveInstance.getWaveData(ZombieGameInstance.instance.get(minigameInstanceIndex).wave).bossId, Integer.parseInt(coordinateRandom[0]),
		                          Integer.parseInt(coordinateRandom[1]), ZombieGameInstance.instance.get(minigameInstanceIndex).getHeight());
	}

	/**
	 * Spawn zombies.
	 */
	public static void spawnZombies(Player playerOne, Player playerTwo) {
		boolean spawnForPlayerOne = false;
		ArrayList<String> list = new ArrayList<String>();
		int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(playerOne != null ? playerOne.getPlayerName() : playerTwo.getPlayerName());
		if (minigameInstanceIndex == -1) {
			return;
		}
		String[] parse = ZombieGameInstance.instance.get(minigameInstanceIndex).zombieList.split(" ");
		for (int index = 0; index < parse.length; index++) {
			if (parse[index].isEmpty()) {
				continue;
			}
			list.add(parse[index]);
		}
		int zombiesToSpawn = zombiesToSpawnAmount(ZombieGameInstance.instance.get(minigameInstanceIndex).wave);
		boolean noneLeft = false;
		for (int index = 0; index < zombiesToSpawn; index++) {
			if (list.size() == 0) {
				noneLeft = true;
				break;
			}
			int npcId = Integer.parseInt(list.get(Misc.random(list.size() - 1)));
			if (Misc.hasPercentageChance(50) && playerOne != null) {
				spawnForPlayerOne = true;
			} else if (playerTwo != null) {
				spawnForPlayerOne = false;
			} else {
				// Either playerOne is available but did not land on 50 chance or both are null.
				spawnForPlayerOne = true;
			}

			if (spawnForPlayerOne && playerOne == null) {
				return;
			}
			ZombieGameInstance.instance.get(minigameInstanceIndex).spawnedZombiesLeft++;
			ArrayList<String> zombieSpawns = new ArrayList<String>();
			parse = ZombieGameInstance.instance.get(minigameInstanceIndex).zombieSpawns.split(" ");
			int number = 0;
			String fullCoordinate = "";
			for (int a = 0; a < parse.length; a++) {
				if (parse[a].isEmpty()) {
					continue;
				}
				fullCoordinate = fullCoordinate + parse[a] + " ";
				number++;
				if (number == 2) {
					zombieSpawns.add(fullCoordinate);
					number = 0;
					fullCoordinate = "";
				}
			}
			String[] coordinateRandom = zombieSpawns.get(Misc.random(zombieSpawns.size() - 1)).split(" ");
			NpcHandler.spawnNpcZombie(spawnForPlayerOne ? playerOne : playerTwo, npcId, Integer.parseInt(coordinateRandom[0]), Integer.parseInt(coordinateRandom[1]),
			                          ZombieGameInstance.instance.get(minigameInstanceIndex).getHeight());
			list.remove(npcId + "");
		}
		String zombieList = "";
		if (!noneLeft) {
			for (int index = 0; index < list.size(); index++) {
				zombieList = zombieList + list.get(index) + " ";
			}
		}
		ZombieGameInstance.getCurrentInstance(playerOne != null ? playerOne.getPlayerName() : playerTwo.getPlayerName()).zombieList = zombieList;
	}

	/**
	 * True if the given coordinates is in the Zombie minigame fighting area, used for when the player logs in and multi coordinates.
	 */
	public static boolean inZombieMiniGameArea(Player player, int x, int y) {
		if (ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName()) == -1) {
			return false;
		}
		// @formatter:off
		return Area.isWithInArea(x, y, 3490, 3515, 3560, 3580) || Area.isWithInArea(x, y, 2949, 3011, 3668, 3713)
		       || Area.isWithInArea(x, y, 3460, 3518, 3261, 3313)
		       || Area.isWithInArea(x, y, 3470, 3584, 3180, 3263)
		       || Area.isWithInArea(x, y, 3644, 3702, 3453, 3539);
		// @formatter:on
	}

	/**
	 * When zombie is damaged.
	 */
	public static void zombieDamaged(Player player, Npc npc, int damage) {
		if (!player.isInZombiesMinigame()) {
			return;
		}
		int currentMinigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
		if (currentMinigameInstanceIndex == -1) {
			return;
		}
		boolean iAmPlayerOne = false;

		if (ZombieGameInstance.instance.get(currentMinigameInstanceIndex).getPlayerOneName().equals(player.getPlayerName())) {
			ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerOneDamage += damage;
			iAmPlayerOne = true;
		} else {
			ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerTwoDamage += damage;
		}

		updateContributionInterfaceText(player, iAmPlayerOne, currentMinigameInstanceIndex);


	}

	/**
	 * Update the contribution text on the interface.
	 */
	private static void updateContributionInterfaceText(Player player, boolean iAmPlayerOne, int currentMinigameInstanceIndex) {
		int total = ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerOneDamage + ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerTwoDamage;
		double percentage = 0.0;

		if (iAmPlayerOne) {
			percentage = (double) ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerOneDamage / (double) total;
		} else {
			percentage = (double) ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerTwoDamage / (double) total;
		}
		percentage *= 100.0;
		player.getPA().sendFrame126("Contribution: " + (int) percentage + "%", 20234);
		Player partner = PlayerHandler.players[player.getZombiePartnerId()];
		boolean partnerAvailable = ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerOneAvailable;
		if (player.getPlayerName().equals(ZombieGameInstance.instance.get(currentMinigameInstanceIndex).getPlayerOneName())) {
			partnerAvailable = ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerTwoAvailable;
		}
		if (partner != null && partnerAvailable) {
			partner.getPA().sendFrame126("Contribution: " + (100 - (int) percentage) + "%", 20234);
		}

	}

	/**
	 * When a zombie dies.
	 */
	public static boolean zombieKilled(Player player, Npc npc) {
		if (!player.isInZombiesMinigame()) {
			return false;
		}
		for (int index = 0; index < ZombieWaveInstance.allBossIds.size(); index++) {
			int bossId = Integer.parseInt(ZombieWaveInstance.allBossIds.get(index));
			if (bossId == npc.npcType) {
				bossDrops(player, npc);
				return true;
			}
		}
		int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
		if (minigameInstanceIndex == -1) {
			return false;
		}
		ZombieGameInstance.instance.get(minigameInstanceIndex).spawnedZombiesLeft--;
		ZombieGameInstance.instance.get(minigameInstanceIndex).totalWaveZombiesLeft--;

		waveSpecificDrops(player, ZombieGameInstance.instance.get(minigameInstanceIndex).totalWaveZombiesLeft, ZombieGameInstance.instance.get(minigameInstanceIndex).wave,
		                  minigameInstanceIndex, npc);
		if (ZombieGameInstance.instance.get(minigameInstanceIndex).totalWaveZombiesLeft == ZombieGameInstance.instance.get(minigameInstanceIndex).bossSpawnOrder) {
			ZombieGameInstance.instance.get(minigameInstanceIndex).bossSpawnOrder = -1;
			spawnBoss(player);
		}
		int spawnedZombiesLeft = ZombieGameInstance.instance.get(minigameInstanceIndex).spawnedZombiesLeft;
		int totalWaveZombiesLeft = ZombieGameInstance.instance.get(minigameInstanceIndex).totalWaveZombiesLeft;

		player.getPA().sendFrame126("Zombies left: " + totalWaveZombiesLeft, 20233);
		Player partner = PlayerHandler.players[player.getZombiePartnerId()];

		boolean partnerAvailable = ZombieGameInstance.instance.get(minigameInstanceIndex).playerOneAvailable;
		if (player.getPlayerName().equals(ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName())) {
			partnerAvailable = ZombieGameInstance.instance.get(minigameInstanceIndex).playerTwoAvailable;
		}
		if (partner != null && partnerAvailable) {
			partner.getPA().sendFrame126("Zombies left: " + totalWaveZombiesLeft, 20233);
		}
		if (spawnedZombiesLeft == 0) {
			if (ZombieGameInstance.instance.get(minigameInstanceIndex).wave == 20) {
				Achievements.checkCompletionSingle(player, 1094);
				if (partner != null) {
					Achievements.checkCompletionSingle(partner, 1094);
				}
			} else if (ZombieGameInstance.instance.get(minigameInstanceIndex).wave == 60) {
				Achievements.checkCompletionSingle(player, 1134);
				if (partner != null) {
					Achievements.checkCompletionSingle(partner, 1134);
				}
			}
			if (ZombieGameInstance.instance.get(minigameInstanceIndex).wave == ZombieWaveInstance.finalZombieWave) {
				player.getPA().sendMessage("You have completed the final wave, gj wp, honoured.");
				if (partner != null) {
					partner.getPA().sendMessage("You have completed the final wave, gj wp, honoured.");
					playerDeath(partner, true);
				}
				playerDeath(player, true);
				ZombieGameInstance.instance.remove(minigameInstanceIndex);
				return true;
			}

			player.waitingForWave = true;
			player.getPA().walkableInterface(20240);

			player.getPA().sendFrame126("You are not ready.", 20242);
			player.getPA().sendFrame126(ZombieGameInstance.getPartnerName(player) + " is not ready.", 20243);
			int points = 60;
			player.zombieWavePoints += points;
			if (partner != null && partnerAvailable) {
				partner.getPA().sendFrame126("You are not ready.", 20242);
				partner.getPA().sendFrame126(player.getPlayerName() + " is not ready.", 20243);
				partner.waitingForWave = true;
				partner.getPA().walkableInterface(20240);
				partner.zombieWavePoints += points;
			}
			return true;
		}
		if (spawnedZombiesLeft == whenToSpawnZombies(spawnedZombiesLeft, ZombieGameInstance.instance.get(minigameInstanceIndex).wave)) {
			spawnZombies(player, null);
		}
		return true;
	}

	private static void starterKit(Player player) {

		// Toolkit.
		Server.itemHandler.createGroundItem(player, 4051, player.getX() + Misc.random(2), player.getY() + Misc.random(2), player.getHeight(), 1, true, 0, true, "", "", "", "", "zombie starterKit 4051");

		ArrayList<Integer> list = new ArrayList<Integer>();
		int random = Misc.random(1, 10);

		// Weapon set.
		if (random <= 3) {
			list.add(1291);
			list.add(1173);
		} else if (random <= 6) {
			list.add(1277);
			list.add(1173);
		} else if (random <= 10) {
			list.add(1321);
			list.add(1189);
		}

		for (int index = 0; index < list.size(); index++) {
			Server.itemHandler
					.createGroundItem(player, list.get(index), player.getX() + Misc.random(2), player.getY() + Misc.random(2), player.getHeight(), 1, true, 0, true, "", "", "",
							"", "zombie starterKit second");
			Server.itemHandler.createGroundItem(player, 315, player.getX() + Misc.random(2), player.getY() + Misc.random(2), player.getHeight(), 1, true, 0, true, "", "", "", "", "zombie starterKit third");
		}

	}

	/**
	 * Boss drop.
	 */
	private static void bossDrops(Player player, Npc npc) {
		if (ZombieGameInstance.getCurrentInstance(player.getPlayerName()) == null) {
			return;
		}
		String[] drops = ZombieWaveInstance.getWaveData(ZombieGameInstance.getCurrentInstance(player.getPlayerName()).wave).bossDrops.split(" ");
		ArrayList<String> list = new ArrayList<String>();
		for (int a = 0; a < drops.length; a++) {
			if (drops[a].isEmpty()) {
				continue;
			}
			list.add(drops[a]);
		}
		int random = Misc.random(list.size() - 1);
		String[] parseDrop = list.get(random).split("-");
		int itemId = Integer.parseInt(parseDrop[0]);
		int amount = Integer.parseInt(parseDrop[1]);
		Server.itemHandler.createGroundItem(player, itemId, npc.getVisualX(), npc.getVisualY(), npc.getHeight(), amount, true, 0, true, "OTHER: Zombie boss drop", "", "", "", "bossDrops " + npc.name);
	}

	/**
	 * Toggle ready status.
	 */
	public static void toggleReadyStatus(Player player) {
		player.isReadyForNextZombieWave = !player.isReadyForNextZombieWave;


		if (player.isReadyForNextZombieWave) {
			player.getPA().sendFrame126("You are ready.", 20242);
		} else {
			player.getPA().sendFrame126("You are not ready.", 20242);
		}

		boolean otherPlayerAvailable = false;
		int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
		if (minigameInstanceIndex == -1) {
			return;
		}
		if (player.getPlayerName().equals(ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName())) {
			otherPlayerAvailable = ZombieGameInstance.instance.get(minigameInstanceIndex).playerTwoAvailable;
		} else {
			otherPlayerAvailable = ZombieGameInstance.instance.get(minigameInstanceIndex).playerOneAvailable;
		}

		Player partner = PlayerHandler.players[player.getZombiePartnerId()];
		if (partner != null && otherPlayerAvailable) {
			if (player.isReadyForNextZombieWave) {
				partner.getPA().sendFrame126(player.getPlayerName() + " is ready.", 20243);
			} else {
				partner.getPA().sendFrame126(player.getPlayerName() + " is not ready.", 20243);
			}
			if (partner.isReadyForNextZombieWave && player.isReadyForNextZombieWave) {
				player.waitingForWave = false;
				partner.waitingForWave = false;
				player.getPA().walkableInterface(20230);
				partner.getPA().walkableInterface(20230);
				player.getPA().sendMessage(":packet:zombiecountdown");
				partner.getPA().sendMessage(":packet:zombiecountdown");
				nextWave(player);
				partner.isReadyForNextZombieWave = false;
				player.isReadyForNextZombieWave = false;
			}
			return;
		}
		if (!otherPlayerAvailable && player.isReadyForNextZombieWave) {
			player.waitingForWave = false;
			player.getPA().walkableInterface(20230);
			player.getPA().sendMessage(":packet:zombiecountdown");
			nextWave(player);
			player.isReadyForNextZombieWave = false;
		}

	}

	/**
	 * Log out update.
	 */
	public static void logOutUpdate(Player player) {
		if (!player.isInZombiesMinigame()) {
			return;
		}
		int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
		if (minigameInstanceIndex == -1) {
			return;
		}
		Player partner = PlayerHandler.players[player.getZombiePartnerId()];
		if (partner != null) {
			partner.setZombiePartnerId(0);
		}
		if (ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName().equals(player.getPlayerName())) {
			ZombieGameInstance.instance.get(minigameInstanceIndex).playerOnePoints = player.zombieWavePoints;
			ZombieGameInstance.instance.get(minigameInstanceIndex).playerOneAvailable = false;
		} else {
			ZombieGameInstance.instance.get(minigameInstanceIndex).playerTwoPoints = player.zombieWavePoints;
			ZombieGameInstance.instance.get(minigameInstanceIndex).playerTwoAvailable = false;
		}
		if (!ZombieGameInstance.instance.get(minigameInstanceIndex).playerOneAvailable && !ZombieGameInstance.instance.get(minigameInstanceIndex).playerTwoAvailable) {
			int wave = ZombieGameInstance.instance.get(minigameInstanceIndex).wave - 1;
			if (partner != null) {
				partner.highestZombieWave = wave;
				partner.zombiePartner = ZombieGameInstance.getPartnerName(partner);
				partner.getPA().sendMessage("Congratulations! You and " + player.getPlayerName() + " have completed wave " + wave + "!");
			}
			player.highestZombieWave = wave;
			String partnerName = ZombieGameInstance.getPartnerName(player);
			player.zombiePartner = partnerName;
			if (wave > 0) {
				HighscoresZombie.getInstance().sortHighscores(player, partnerName);
			}

			deleteMinigameInstance(minigameInstanceIndex, player);
		}
	}

	/**
	 * Get the zombies to spawn.
	 *
	 * @param wave The wave to spawn for.
	 */
	public static String getZombiesToSpawn(int wave, int zombiesToSpawn) {
		String zombies = "";

		for (int amount = zombiesToSpawn; amount > 0; amount--) {
			if (amount - 33 > 0) {
				zombies = zombies + getRandomZombie("HIGH") + " ";
			} else if (amount - 18 > 0) {
				zombies = zombies + getRandomZombie("MEDIUM") + " ";
			} else {
				zombies = zombies + getRandomZombie("LOW") + " ";
			}
		}
		return zombies;
	}

	/**
	 * Total amount of zombies to spawn for the given wave.
	 */
	private static int getZombiesTotalForWave(int wave) {
		return (int) (4 + (wave * 2));
	}

	/**
	 * Return the amount of zombies alive in order to spawn more zombies.
	 */
	private static int whenToSpawnZombies(int zombiesLeft, int wave) {

		return 1 + (wave / 5);

		//5
	}

	/**
	 * Amount of zombies to spawn infront of the player.
	 */
	private static int zombiesToSpawnAmount(int wave) {
		return 3 + (wave / 5);
	}

	/**
	 * Spawn next wave if the current wave is completed.
	 */
	private static void nextWave(Player player) {
		int currentMinigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
		if (currentMinigameInstanceIndex == -1) {
			return;
		}
		ZombieGameInstance.instance.get(currentMinigameInstanceIndex).wave += 1;
		int wave = ZombieGameInstance.instance.get(currentMinigameInstanceIndex).wave;
		int waveInstanceIndex = ZombieWaveInstance.getWaveDataIndex(wave);
		ZombieGameInstance.instance.get(currentMinigameInstanceIndex).chestCoordinates = ZombieWaveInstance.instance.get(waveInstanceIndex).getChestCoordinates();
		ZombieGameInstance.instance.get(currentMinigameInstanceIndex).zombieList = getZombiesToSpawn(wave, getZombiesTotalForWave(wave));
		ZombieGameInstance.instance.get(currentMinigameInstanceIndex).chestId = ZombieWaveInstance.instance.get(waveInstanceIndex).getChestId();
		ZombieGameInstance.instance.get(currentMinigameInstanceIndex).totalWaveZombiesLeft = getZombiesTotalForWave(wave);
		Player partner = null;
		boolean partnerAvailable = ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerOneAvailable;
		if (player.getPlayerName().equals(ZombieGameInstance.instance.get(currentMinigameInstanceIndex).getPlayerOneName())) {
			partnerAvailable = ZombieGameInstance.instance.get(currentMinigameInstanceIndex).playerTwoAvailable;
		}
		if (partnerAvailable) {
			partner = PlayerHandler.players[player.getZombiePartnerId()];
			if (partner != null) {
				partner.getPA().sendFrame126("Wave: " + wave, 20232);
				partner.getPA().sendFrame126("Zombies left: " + ZombieGameInstance.instance.get(currentMinigameInstanceIndex).totalWaveZombiesLeft, 20233);
			}
		}
		newWave(player, partner);
		player.getPA().sendFrame126("Wave: " + wave, 20232);
		player.getPA().sendFrame126("Zombies left: " + ZombieGameInstance.instance.get(currentMinigameInstanceIndex).totalWaveZombiesLeft, 20233);
	}

	private static String getRandomZombie(String type) {
		int random = Misc.random(1, 3);
		switch (type) {
			case "LOW":
				return (random == 1 ? 3956 : random == 2 ? 3959 : 3962) + "";
			case "MEDIUM":
				return (random == 1 ? 3957 : random == 2 ? 3960 : 3963) + "";
			case "HIGH":
				return (random == 1 ? 3958 : random == 2 ? 3961 : 3964) + "";
		}
		return "";
	}

	/**
	 * Get zombie shop price.
	 */
	public static boolean checkZombieShopPrice(Player player, int itemId) {
		if (player.getShopId() == 9) {
			int cost = getShopPrice(itemId);
			player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + ": currently costs " + cost + " Zombie points.");
			return true;
		}
		return false;
	}

	/**
	 * Get the shop item price.
	 */
	private static int getShopPrice(int itemId) {

		// Runes.
		if (itemId >= 554 && itemId <= 566 || itemId == 9075) {
			return 1;
		}
		switch (itemId) {

			// Vial of water.
			case 227:
				return 1;
			// Feather.
			case 314:
				return 1;
		}
		return 10;
	}

	/**
	 * Buy the item.
	 */
	public static boolean buyZombieShopItem(Player player, int itemIdRequested, int amountRequested) {
		if (player.getShopId() == 9) {
			int waveInstanceIndex = ZombieWaveInstance.getWaveDataIndex(ZombieGameInstance.getCurrentInstance(player.getPlayerName()).wave);
			String[] parse = ZombieWaveInstance.instance.get(waveInstanceIndex).getShopList().split(" ");
			ArrayList<String> list = new ArrayList<String>();
			for (int a = 0; a < parse.length; a++) {
				if (parse[a].isEmpty()) {
					continue;
				}
				list.add(parse[a]);
			}
			for (int i = 0; i < list.size(); i++) {
				int itemIdStock = Integer.parseInt(list.get(i));
				int itemAmountStock = 99999;
				if (itemIdRequested == itemIdStock) {
					int points = player.zombieWavePoints;
					int price = getShopPrice(itemIdRequested);
					if (points >= price) {
						int maxAmountAffordable = points / price;
						if (amountRequested > maxAmountAffordable) {
							amountRequested = maxAmountAffordable;
							if (amountRequested > itemAmountStock) {
								amountRequested = itemAmountStock;
							}
						}

						if (!ItemDefinition.getDefinitions()[itemIdRequested].stackable && ItemAssistant.getFreeInventorySlots(player) < (amountRequested)) {
							amountRequested = ItemAssistant.getFreeInventorySlots(player);
						} else if (ItemDefinition.getDefinitions()[itemIdRequested].stackable && !ItemAssistant.hasItemInInventory(player, itemIdRequested)
						           && ItemAssistant.getFreeInventorySlots(player) == 0) {
							amountRequested = 0;
						}
						if (amountRequested == 0) {
							player.playerAssistant.sendMessage("Not enough inventory space.");
							return true;
						}
						boolean times5 = false;
						if (itemIdRequested >= 554 && itemIdRequested <= 566 || itemIdRequested == 9075) {
							times5 = true;
						}
						ItemAssistant.addItem(player, itemIdRequested, times5 ? (amountRequested * 7) : amountRequested);
						player.zombieWavePoints -= price * amountRequested;
						loadZombieWaveShop(player, 0, true);
						loadZombieWaveShop(PlayerHandler.players[player.getZombiePartnerId()], 0, true);
					} else {
						player.playerAssistant.sendMessage(
								ItemAssistant.getItemName(itemIdRequested) + ": " + (price - points) + " additional Zombie points are required to purchase this item.");
					}
					break;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * When a player dies.
	 */
	public static void playerDeath(Player player, boolean teleport) {
		if (!player.isAdministratorRank()) {
			ItemAssistant.deleteAllItems(player);
		}
		int minigameInstanceIndex = ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName());
		if (minigameInstanceIndex == -1) {
			return;
		}
		if (ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName().equals(player.getPlayerName())) {
			ZombieGameInstance.instance.get(minigameInstanceIndex).playerOneAvailable = false;
		} else {
			ZombieGameInstance.instance.get(minigameInstanceIndex).playerTwoAvailable = false;
		}

		player.setInZombiesMinigame(false);
		int wave = ZombieGameInstance.instance.get(minigameInstanceIndex).wave - 1;
		if (!ZombieGameInstance.instance.get(minigameInstanceIndex).playerOneAvailable && !ZombieGameInstance.instance.get(minigameInstanceIndex).playerTwoAvailable) {
			Player partner = PlayerHandler.players[player.getZombiePartnerId()];
			int coinAmountPerWave = 20000;
			if (partner != null) {
				partner.setZombiePartnerId(0);
				partner.getPA().sendMessage("Congratulations! You and " + player.getPlayerName() + " have reached wave " + wave + "!");
				partner.highestZombieWave = wave;
				partner.zombiePartner = ZombieGameInstance.getPartnerName(partner);
				ItemAssistant.addItemToInventoryOrDrop(partner, 995, coinAmountPerWave * wave);
			}
			player.setZombiePartnerId(0);
			player.highestZombieWave = wave;
			String partnerName = ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName();
			if (ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName().equals(player.getPlayerName())) {
				partnerName = ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerTwoName();
			}
			player.zombiePartner = partnerName;
			if (wave > 0) {
				HighscoresZombie.getInstance().sortHighscores(player, partnerName);
			}
			player.getPA().sendMessage("Congratulations! You and " + partnerName + " have reached wave " + wave + "!");
			ItemAssistant.addItemToInventoryOrDrop(player, 995, coinAmountPerWave * wave);

			// Add to highscores
			//wave

			deleteMinigameInstance(minigameInstanceIndex, player);
		} else {
			player.getPA().sendMessage("Unfortunately, you have died on wave " + wave + ".");
		}
		if (teleport) {
			player.getPA().movePlayer(3657, 3519, 0);
		}
	}

	/**
	 * Delete minigame instance when both players died or both players logged off.
	 */
	private static void deleteMinigameInstance(int minigameInstanceIndex, Player player) {
		removeItemsFromFloor(ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName(), ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerTwoName());

		for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
			Npc npc = NpcHandler.npcs[i];
			if (npc == null) {
				continue;
			}
			if (npc.zombieOwner.equals(ZombieGameInstance.instance.get(minigameInstanceIndex).getPlayerOneName()) || npc.zombieOwner.equals(ZombieGameInstance.instance
					                                                                                                                                .get(minigameInstanceIndex)
					                                                                                                                                .getPlayerTwoName())) {
				Pet.deletePet(npc);
			}
		}
		ZombieGameInstance.instance.remove(minigameInstanceIndex);
		player.setInZombiesMinigame(false);
	}

	/**
	 * Remove all ground items upon minigame disband.
	 */
	private static void removeItemsFromFloor(String playerOneName, String playerTwoName) {
		ArrayList<GroundItem> toRemove = new ArrayList<GroundItem>();
		for (int j = 0; j < Server.itemHandler.items.size(); j++) {
			if (Server.itemHandler.items.get(j) != null) {
				GroundItem i = Server.itemHandler.items.get(j);
				if (i.ownerName.equalsIgnoreCase(playerOneName) || i.ownerName.equalsIgnoreCase(playerTwoName)) {
					toRemove.add(i);
				}

			}

		}
		for (int j = 0; j < toRemove.size(); j++) {
			GroundItem i = toRemove.get(j);
			Server.itemHandler.removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount(), i.getItemHeight());
		}
	}

	/**
	 * Wave specific drops like on wave 10/30/40/50
	 */
	private static void waveSpecificDrops(Player player, int zombiesLeft, int wave, int minigameInstance, Npc npc) {
		boolean drop = false;
		if (wave == 3) {
			if (zombiesLeft == 0 && wave == 3) {
				ZombieGameInstance.instance.get(minigameInstance).waveItems = ZombieWaveInstance.wave5Items + " " + ZombieWaveInstance.wave5Items;
				drop = true;
			}
		} else if (wave >= 4 && wave <= 7) {
			if (zombiesLeft == 1 && wave == 4) {
				ZombieGameInstance.instance.get(minigameInstance).waveItems = ZombieWaveInstance.wave10Items + " " + ZombieWaveInstance.wave10Items;
			} else if (zombiesLeft == 0) {
				drop = true;
			}
		} else if (wave == 8) {
			if (zombiesLeft == 0 && wave == 8) {
				ZombieGameInstance.instance.get(minigameInstance).waveItems = ZombieWaveInstance.wave20Items + " " + ZombieWaveInstance.wave20Items;
				drop = true;
			}
		} else if (wave >= 10 && wave <= 22) {
			if (zombiesLeft == 1 && wave == 10) {
				ZombieGameInstance.instance.get(minigameInstance).waveItems = ZombieWaveInstance.wave30Items + " " + ZombieWaveInstance.wave30Items;
			} else if (zombiesLeft == 0) {
				drop = true;
			}
		} else if (wave >= 23 && wave <= 31) {
			if (zombiesLeft == 1 && wave == 23) {
				ZombieGameInstance.instance.get(minigameInstance).waveItems = ZombieWaveInstance.wave40Items + " " + ZombieWaveInstance.wave40Items;
				drop = true;
			} else if (zombiesLeft == 0) {
				drop = true;
				dropWaveItem(player, minigameInstance, npc);
				dropWaveItem(player, minigameInstance, npc);
			}
		}

		if (drop) {
			// Twice because one for each player, each list will contain 2 of the same item.
			dropWaveItem(player, minigameInstance, npc);
			dropWaveItem(player, minigameInstance, npc);
		}
	}

	/**
	 * Drop wave item.
	 */
	private static void dropWaveItem(Player player, int minigameInstance, Npc npc) {
		if (ZombieGameInstance.instance.get(minigameInstance).waveItems.isEmpty()) {
			return;
		}
		String[] parse = ZombieGameInstance.instance.get(minigameInstance).waveItems.split(" ");
		ArrayList<String> list = new ArrayList<String>();
		for (int a = 0; a < parse.length; a++) {
			if (parse[a].isEmpty()) {
				continue;
			}
			list.add(parse[a]);
		}
		int random = Misc.random(0, list.size() - 1);
		int item = Integer.parseInt(list.get(random));
		list.remove(random);
		String newShopList = "";
		for (int b = 0; b < list.size(); b++) {
			newShopList = newShopList + list.get(b) + " ";
		}
		ZombieGameInstance.instance.get(minigameInstance).waveItems = newShopList;

		// Team cape.
		if (item == 4315) {
			item = QuickSetUp.getTeamCape(false);
		}

		// God cape.
		else if (item == 2412) {
			item = QuickSetUp.getRandomGodCape();
		}

		// Mystic gloves
		else if (item == 4095) {
			item = mysticGloves[Misc.random(mysticGloves.length - 1)];
		}

		// Mystic hat
		else if (item == 4089) {
			item = mysticHats[Misc.random(mysticHats.length - 1)];
		}

		// Mystic top
		else if (item == 4091) {
			item = mysticTops[Misc.random(mysticTops.length - 1)];
		}

		// Mystic bottom
		else if (item == 4093) {
			item = mysticBottoms[Misc.random(mysticBottoms.length - 1)];
		}

		// Mystic boots
		else if (item == 4097) {
			item = mysticBoots[Misc.random(mysticBoots.length - 1)];
		}
		Server.itemHandler.createGroundItem(player, item, npc.getVisualX(), npc.getVisualY(), player.getHeight(), 1, true, 0, true, "", "", "", "", "zombie dropWaveItem");
	}



	public final static int[] mysticHats =
			{
					4089, // Mystic hat.
					4109, // Mystic hat.
					4099, // Mystic hat.
			};

	public final static int[] mysticTops =
			{
					4091, // Mystic robe top.
					4111, // Mystic robe top.
					4101, // Mystic robe top.
			};

	public final static int[] mysticBottoms =
			{
					4093, // Mystic robe bottom.
					4113, // Mystic robe bottom.
					4103, // Mystic robe bottom.
			};

	public final static int[] mysticGloves =
			{
					4095, // Mystic gloves.
					4115, // Mystic gloves.
					4105, // Mystic gloves.
			};

	public final static int[] mysticBoots =
			{
					4097, // Mystic boots.
					4117, // Mystic boots.
					4107, // Mystic boots.
			};
}
