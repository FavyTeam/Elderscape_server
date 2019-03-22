package game.content.combat;

import core.GameType;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotContent;
import game.content.achievement.Achievements;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.item.chargeable.ChargeableCollection;
import game.content.item.impl.TrackingHelmet;
import game.content.minigame.Minigame;
import game.content.minigame.MinigameArea;
import game.content.minigame.MinigameAreaDeathSafety;
import game.content.minigame.MinigamePlayerParticipant;
import game.content.minigame.TargetSystem;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.AutoBuyBack;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.BraceletOfEthereum;
import game.content.miscellaneous.ItemTransferLog;
import game.content.miscellaneous.ItemsToInventoryDeath;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.Transform;
import game.content.packet.PrivateMessagingPacket;
import game.content.quest.tab.InformationTab;
import game.content.quicksetup.QuickSetUp;
import game.content.starter.GameMode;
import game.content.wildernessbonus.KillReward;
import game.content.wildernessbonus.WildernessRisk;
import game.content.worldevent.BloodKey;
import game.content.worldevent.Tournament;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import game.player.punishment.BannedData;
import utility.Misc;

/**
 * Death stages.
 *
 * @author MGT Madness, created on 25-11-2013.
 */
public class Death {

	public static int findPlayerWhoDidMostDamage(Player victim) {
		int killer = victim.getPlayerId();
		if (System.currentTimeMillis() - victim.timeUnderAttackByAnotherPlayerOther >= 60000) {
			return killer;
		}
		int damage = 0;
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			if (PlayerHandler.players[index] == null) {
				continue;
			}
			if (index == victim.getPlayerId()) {
				continue;
			}
			Player killerInstanceCheck = Misc.getPlayerByIndex(index);
			if (killerInstanceCheck != null) {
				if (!killerInstanceCheck.getPlayerName().equals(victim.damageTakenName[index])) {
					continue;
				}
			}
			if (victim.damageTaken[index] > damage) {
				damage = victim.damageTaken[index];
				killer = index;
			}
		}
		return killer;
	}

	/**
	 * Apply death, once the player reaches 0 hitpoints.
	 *
	 * @param victim The player that died.
	 */
	public static void deathStage(final Player victim) {
		Player killer = null;
		if (System.currentTimeMillis() - victim.getTimeUnderAttackByAnotherPlayer() >= 5000 && !Area.inDangerousPvpArea(victim)) {
			victim.setLastAttackedBy(0);
		}
		killer = PlayerHandler.players[findPlayerWhoDidMostDamage(victim)];
		if (killer != null) {
			if (killer.getPlayerName().equals(victim.getPlayerName())) {
				killer = null;
			}
		}
		final Player killer1 = killer;

		applyDeath(victim, killer1);
		CycleEventHandler.getSingleton().addEvent(victim, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				victim.startAnimation(836);
				deathEvent(victim, killer1);
			}
		}, 2);

	}

	private static void applyDeath(Player victim, Player killer) {
		hardcoreIronManDeathStage1(victim);
		victim.timePrayerActivated = new long[50];
		victim.setDead(true);
		victim.resetFaceUpdate();
		victim.resetPlayerTurn();
		victim.setUsingSpecialAttack(false);
		victim.timeExitedWilderness = 0;
		victim.timeVictimExitedWilderness = 0;
		victim.playerIdCanAttackInSafe = 0;
		victim.playerIdAttackingMeInSafe = 0;
		InterfaceAssistant.offCityTimer(victim);
		victim.timeInPlayerCombat = 0;
		BotContent.death(victim);
		BotContent.diedToBot(victim, killer);
		boolean killerExists = killer != null;
		if (killerExists) {
			killer.lastPlayerKilled = victim.getPlayerName();
			killer.timeExitedWilderness = 0;
			killer.timeVictimExitedWilderness = 0;
			killer.playerIdCanAttackInSafe = 0;
			killer.playerIdAttackingMeInSafe = 0;
			InterfaceAssistant.offCityTimer(killer);
			if (EdgeAndWestsRule.isUnderEdgeAndWestsProtectionRules(killer)) {
				killer.killedPlayerImmuneTime = System.currentTimeMillis();
			}
			killer.getPA().resetCombatTimer();
			killer.timeInPlayerCombat = 0;
			boolean faceVictim = killer.getFace() == victim.getPlayerId() + 32768;
			Combat.resetPlayerAttack(killer); // Resets the face, so we have to do the face update again.
			if (faceVictim) {
				killer.setFace(victim.getPlayerId() + 32768);
			}
			if (victim.getDuelStatus() != 6 && killer.getDuelStatus() == 5) {
				// Make sure the killer's last dueled with name is same as victim.
				if (killer.getLastDueledWithName().equals(victim.getPlayerName())) {
					killer.setDuelStatus(killer.getDuelStatus() + 1);
				}
			}
		}
		Transform.unTransform(victim);
		Movement.stopMovement(victim);
		if (victim.getDuelStatus() <= 4) {
			if (killerExists) {
				if (Area.inDangerousPvpArea(victim) || Area.inSafePkFightZoneAll(victim)) {
					if (killer.getPlayerId() != victim.getPlayerId()) {
						killer.playerAssistant.sendMessage(deathMessage(GameMode.getGameModeName(victim)));
						if (!diedInSafeArea(killer, victim)) {
							killer.getPA().sendKillScreenshot(killer, victim, false);
						}
					}
				}
			}
			victim.playerAssistant.sendMessage("Oh dear, you are dead!");
			if (!Area.inDangerousPvpArea(victim)) {
				victim.safeDeaths++;
				if (killerExists) {
					safeKillReward(killer);
				}
			}
		}
		CombatInterface.addSpecialBar(victim, victim.getWieldedWeapon());
		victim.getPA().resetFollowers();
		if (killerExists) {
			//GrimReaper.spawnDeath(victim, killer);
			killer.setUnderAttackBy(0);
			killer.attackedPlayers.clear();
		}
	}

	private static void safeKillReward(Player killer) {
		killer.safeKills++;

		// Incase killer is at wilderness and did most damage to victim but someone else finished off the victim.
		// So the killer would gain special in the wild in the middle of a fight.
		if (Area.inClanWarsDangerousArea(killer)) {
			killer.setSpecialAttackAmount(10, false);
		}
		killer.getPA().resetCombatTimer();
		CombatInterface.addSpecialBar(killer, killer.getWieldedWeapon());
		Achievements.checkCompletionMultiple(killer, "1048 1068");
	}

	/**
	 * Give life to a dead player
	 */
	public static void respawnPlayer(Player killer, Player victim) {
		InterfaceAssistant.closeCombatOverlay(victim);
		hardcoreIronManDeath(victim);
		QuickSetUp.heal(victim, false, false);
		victim.timeExitedWildFromTarget = System.currentTimeMillis();
		TargetSystem.leftWild(victim);
		victim.resetFaceUpdate();
		victim.redemptionOrWrathActivated = false;
		victim.resetNpcIdentityAttacking();
		victim.resetPlayerIdAttacking();
		victim.playerBotCurrentKillstreak = 0;
		victim.itemsKeptOnDeathList.clear();
		victim.drainRunEnergyFaster = 0;
		victim.miasmicSpeedTimeElapsed = 0;
		victim.immuneToMeleeAttacks = 0;
		if (killer != null) {
			//65535 means the killer did not issue a new face and is still facing the victim.
			boolean faceVictim = killer.getFace() == 65535;
			if (faceVictim) {
				killer.resetFaceUpdate();
			}
			killer.itemsKeptOnDeathList.clear();
		}
		diedInNonSafeArea(killer, victim);
		Combat.resetPrayers(victim); // Must be kept after gathering itemsKeptOnDeathList
		victim.rfdWave = 0;
		victim.setUnderAttackBy(0);
		victim.setNpcIndexAttackingPlayer(0);
		Combat.resetPlayerAttack(victim);
		Skull.clearSkull(victim);
		victim.getPA().requestUpdates();
		victim.timeDied = System.currentTimeMillis();
		Combat.updatePlayerStance(victim);
		deathRespawnArea(victim, killer);
		victim.gfx0(65535);
		if (victim.getDuelStatus() != 6) {
			victim.getTradeAndDuel().myStakedItems.clear();
		}
		victim.getPA().resetCombatTimer();
		victim.setUsingFightCaves(false);
		InformationTab.updateQuestTab(killer);
		InformationTab.updateQuestTab(victim);
		victim.setLastAttackedBy(0);
		ItemsKeptOnDeath.addItemsToInventoryThatAreKeptOnDeath(victim);
		ItemsToInventoryDeath.addItemsAfterDeath(victim);
		victim.startAnimation(65535);
		victim.dragonSpearEffectStack.clear();
		victim.dragonSpearTicksLeft = 0;
		victim.dragonSpearEvent = false;
		AutoBuyBack.autoBuyBack(victim);
	}

	private final static int HARDCORE_IRON_MAN_DEATH_TOTAL_LEVEL_MINIMUM_ANNOUNCE = 500;

	/**
	 * Demote the Hardcore iron man when he respawns.
	 */
	private static void hardcoreIronManDeath(Player victim) {
		if (GameMode.getGameMode(victim, "HARDCORE IRON MAN")) {
			victim.getDH().sendStatement("@dre@Game over! You are no longer a Hardcore Ironman.");
			if (victim.getTotalLevel() >= HARDCORE_IRON_MAN_DEATH_TOTAL_LEVEL_MINIMUM_ANNOUNCE) {
				Announcement.announce("<img=26><col=7c2300> " + victim.getPlayerName() + " has died as a Hardcore Ironman with a total level of " + victim.getTotalLevel() + "!");
			}
			victim.setGameMode("STANDARD IRON MAN");
			victim.playerRights = 9;
			victim.setUpdateRequired(true);
			victim.setAppearanceUpdateRequired(true);
		}
	}

	/**
	 * Take a screenshot of the Hardcore iron man dying at the moment he hits 0 hp.
	 */
	private static void hardcoreIronManDeathStage1(Player victim) {
		if (GameMode.getGameMode(victim, "HARDCORE IRON MAN")) {
			victim.getPA().sendScreenshot("hardcore ironman death", 3);
		}
	}

	/**
	 * The area to teleport the player to.
	 *
	 * @param victim The associated player.
	 */
	private static void deathRespawnArea(Player victim, Player killer) {
		Minigame victimMinigame = victim.getMinigame();

		if (victimMinigame != null) {
			victimMinigame.onDeath(victim);
			return;
		}
		if (victim.isJailed()) {
			return;
		}
		if (victim.getHeight() == 20) {
			Tournament.playerDied(killer, victim);
		} else if (Zombie.inZombieMiniGameArea(victim, victim.getX(), victim.getY())) {
			victim.getPA().movePlayer(3657, 3519, 0);
		} else if (Area.inHweenArea(victim)) {
			victim.getPA().movePlayer(3788, 9226, 0);
		} else if (victim.isUsingFightCaves()) {
			victim.getPA().movePlayer(2439, 5169, 0);
		} else if (victim.getDuelStatus() >= 5) {
			duelArenaDeath(victim);
		} else if (Area.inSafePkFightZoneAll(victim)) {
			victim.getPA().movePlayer(3324 + Misc.random(7), 4759 + Misc.random(1), 0);
		} else if (victim.getHeight() == 4) {
			victim.getPA().movePlayer(3093, 3493, 4);
		} else {
			if (System.currentTimeMillis() - victim.timeMovedFromDoubleDuelDeath <= 1200) {
				return;
			}
			if (victim.isCombatBot()) {
				victim.getPA().movePlayer(3088, 3509, 0);
			} else {
				victim.getPA().movePlayer(3101 + Misc.random(3), 3492 + Misc.random(6), 0);
			}
		}
	}

	/**
	 * The death stages done through Cycle Event.
	 */
	private static void deathEvent(final Player victim, final Player killer) {

		CycleEventHandler.getSingleton().addEvent(victim, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				Death.respawnPlayer(killer, victim);
			}
		}, 4);

	}

	public static String deathMessage(String name) {
		int deathMessage = Misc.random(12);
		switch (deathMessage) {
			case 0:
				return "With a crushing blow, you defeat " + name + ".";
			case 1:
				return "It's a humiliating defeat for " + name + ".";
			case 2:
				return "" + name + " didn't stand a chance against you.";
			case 3:
				return "You've defeated " + name + ".";
			case 4:
				return "" + name + " regrets the day they met you in combat.";
			case 5:
				return "It's all over for " + name + ".";
			case 6:
				return "" + name + " falls before your might.";
			case 7:
				return "Can anyone defeat you? Certainly not " + name + ".";
			case 8:
				return "You were clearly a better fighter than " + name + ".";
			case 9:
				return "You pwned " + name + ".";
			case 10:
				return "You have sent " + name + " to Edgeville.";
			case 11:
				return "You owned " + name + ".";
			case 12:
				return "You demolished " + name + ".";
		}
		return null;
	}

	/**
	 * Append specific updates to the player that died in the Wilderness.
	 */
	private static void diedInNonSafeArea(Player killer, Player victim) {
		if (diedInSafeArea(killer, victim)) {
			return;
		}
		Minigame minigame = victim.getMinigame();

		if (minigame != null) {
			MinigameArea area = minigame.getAreaOrNull(victim);

			if (area != null) {
				MinigamePlayerParticipant participant = area.getPlayerParticipant(victim);

				if (participant != null) {
					if (area.getDeathSafety() == MinigameAreaDeathSafety.UNSAFE && area.managesItemsForUnsafeDeath()) {
						area.onManageItemsForUnsafeDeath(participant);
						return;
					}
				}
			}
		}
		final ChargeableCollection collection = victim.getAttributes().getOrDefault(Player.CHARGEABLE_COLLECTION_KEY);
		collection.onDeath(killer, victim);

		ItemsKeptOnDeath.getItemsKeptOnDeath(victim, false, false);

		if (killer != null) {
			ItemsKeptOnDeath.getItemsKeptOnDeath(killer, false, false); // Used to check if attacker has risk, used if attacker has more than 20k risk.
		}
		BloodKey.diedWithBloodKey(victim);
		TargetSystem.death(victim, killer);
		ItemsKeptOnDeath.deleteItemsKeptOnDeath(victim);
		dropItemsForKiller(killer, victim);
		ItemAssistant.deleteAllItems(victim);
	}

	private static boolean diedInSafeArea(Player killer, Player victim) {
		if (victim.isAdministratorRank() || victim.isUsingFightCaves() || victim.getDuelStatus() == 5) {
			if (ServerConfiguration.DEBUG_MODE && victim.getPlayerName().toLowerCase().contains("jason")) {
				return false;
			}
			return true;
		}
		if (killer != null) {
			if (killer.isAdministratorRank() || killer.getDuelStatus() == 6) {
				return true;
			}
		}
		Minigame victimMinigame = victim.getMinigame();

		if (victimMinigame != null) {
			MinigameArea area = victimMinigame.getAreaOrNull(victim);

			if (area != null) {
				return area.getDeathSafety() == MinigameAreaDeathSafety.SAFE;
			}
		}
		// Must be kept here or if both players die at same time, one of them will lose everything in inventory.
		if (Area.inDuelArena(victim)) {
			return true;
		}
		if (victim.getHeight() == 20) {
			return true;
		}
		if (victim.isInZombiesMinigame()) {
			Zombie.playerDeath(victim, false);
			return true;
		}

		if (Area.inSafePkFightZoneAll(victim)) {
			return true;
		}

		if (Area.isWithInArea(victim, 1889, 1910, 5345, 5366)) {
			return true;
		}
		return false;
	}

	/**
	 * Append updates for when the player died in Duel arena.
	 *
	 * @param victim The associated player.
	 */
	private static void duelArenaDeath(Player victim) {
		victim.getPA().movePlayer(ServerConstants.DUEL_ARENA_X + (Misc.random(ServerConstants.RANDOM_DISTANCE)),
		                          ServerConstants.DUEL_ARENA_Y + (Misc.random(ServerConstants.RANDOM_DISTANCE)), 0);
		Player killer = victim.getTradeAndDuel().getPartnerDuel();

		if (killer != null) {
			if (killer.getDead()) {
				killer.safeKills++;
				killer.getTradeAndDuel().declineDuel(false);
				victim.getTradeAndDuel().declineDuel(false);
				killer.timeMovedFromDoubleDuelDeath = System.currentTimeMillis();
				killer.getPA().sendMessage("You have both lost the duel.");
				victim.getPA().sendMessage("You have both lost the duel.");
				killer.getPA().movePlayer(ServerConstants.DUEL_ARENA_X + (Misc.random(ServerConstants.RANDOM_DISTANCE)),
				                          ServerConstants.DUEL_ARENA_Y + (Misc.random(ServerConstants.RANDOM_DISTANCE)), 0);
				victim.getPA().createPlayerHints(10, -1);
				killer.getPA().createPlayerHints(10, -1);

			} else {
				ItemTransferLog.duelWon(killer, victim);
				killer.getTradeAndDuel().duelVictory();
				killer.safeKills++;
				if (GameType.isPreEoc()) {
					victim.getAttributes().increase(TrackingHelmet.DUEL_DEATHS);
					killer.getAttributes().increase(TrackingHelmet.DUEL_KILLS);
				}
				victim.getPA().sendMessage("You have lost the duel.");
				killer.getPA().sendMessage("You have won the duel!");
				//log here
				killer.getPA().movePlayer(ServerConstants.DUEL_ARENA_X + (Misc.random(ServerConstants.RANDOM_DISTANCE)),
				                          ServerConstants.DUEL_ARENA_Y + (Misc.random(ServerConstants.RANDOM_DISTANCE)), 0);
				killer.getPA().createPlayerHints(10, -1);
				victim.getPA().createPlayerHints(10, -1);
			}
		} else {
			ItemTransferLog.duelWon(null, victim);
			victim.getPA().sendMessage("You have won the duel!");
			// log here
			victim.getTradeAndDuel().duelVictory();
			victim.getPA().createPlayerHints(10, -1);
		}
		victim.safeDeaths++;
	}

	public static void diedToBot(Player killer, Player victim) {
		Server.itemHandler.createGroundItem(killer, 526, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, "", "", "", "", "diedToBot create bones");
		for (int e = 0; e < victim.playerEquipment.length; e++) {
			int equipmentItemId = victim.playerEquipment[e];
			if (equipmentItemId <= 0) {
				continue;
			}
			if (ItemAssistant.isItemToUntradeableShopOnDeath(equipmentItemId)) {
				victim.itemsToShop.add(equipmentItemId + " " + victim.playerEquipmentN[e]);
			} else if (ItemAssistant.isItemToInventoryOnDeath(equipmentItemId)) {
				victim.itemsToInventory.add(equipmentItemId + " " + victim.playerEquipmentN[e]);
			}
		}

		for (int i = 0; i < victim.playerItems.length; i++) {
			int item = victim.playerItems[i] - 1;
			if (item <= 0) {
				continue;
			}
			if (ItemAssistant.isItemToUntradeableShopOnDeath(item)) {
				victim.itemsToShop.add(item + " " + victim.playerItemsN[i]);
			} else if (ItemAssistant.isItemToInventoryOnDeath(item)) {
				victim.itemsToInventory.add(item + " " + victim.playerItemsN[i]);
			}
		}
	}

	/**
	 * Victim drops items for the killer.
	 **/
	public static void dropItemsForKiller(Player killer, Player victim) {
		if (killer == null) {
			killer = victim;
		}
		killer.lootValueFromKill = 0;
		killer.lastTimeKilledPlayer = 0;
		victim.itemsToInventory.clear();
		boolean killerExists = killer.getPlayerName() != victim.getPlayerName();
		boolean victimBot = victim.isCombatBot();
		if (killer.isCombatBot()) {
			diedToBot(killer, victim);
			return;
		}
		if (killer.toggleLootKey) { //Temporary just to test TODO
			killer.getPA().sendMessage("<col=ff0000>Your victim's loot key has been placed in your inventory.");
			ItemAssistant.addItemToInventoryOrDrop(killer, 16140, 1);
			return;
		}
		victim.victimBotWealth = 0;
		if (killerExists) {
			if (!Area.inEdgevilleWilderness(victim) && victim.getHeight() != 4) {
				victim.timeDiedInWilderness = System.currentTimeMillis();
			}
			if (GameType.isPreEoc()) {
				if (Area.inEdgevilleWilderness(victim) && Area.inEdgevilleWilderness(killer)) {
					killer.getAttributes().increase(TrackingHelmet.EDGE_KILLS);
					victim.getAttributes().increase(TrackingHelmet.EDGE_DEATHS);
				}
			}
			WildernessRisk.carriedWealth(killer, false);
			WildernessRisk.carriedWealth(victim, false);
			KillReward.giveLoot(killer, victim);
			killer.coinsPile = 0;
			boolean flagged = false;
			boolean ignored = victim.riskedWealth >= ItemTransferLog.WEALTH_FROM_EACH_PLAYER_PRESENTED && killer.riskedWealth >= ItemTransferLog.WEALTH_FROM_EACH_PLAYER_PRESENTED;
			if (victim.riskedWealth - killer.riskedWealth >= ItemTransferLog.WEALTH_TRANSFER_NOTIFICATION && !Misc.isSamePlayer(killer, victim) && !ignored) {
				long victimAccountWealth = ItemAssistant.getAccountBankValueLong(victim);
				long killerAccountWealth = ItemAssistant.getAccountBankValueLong(killer);
				if (victimAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED
				    && killerAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED) {
					ignored = true;
				}
				if (killerAccountWealth >= ItemTransferLog.ACCOUNT_WEALTH_FROM_EACH_PLAYER_IGNORED) {
					ignored = true;
				}
				if (!ignored) {
					if (PrivateMessagingPacket.getMinutesFriendsFor(killer, victim.getPlayerName()) < ItemTransferLog.MINIMUM_MINUTES_FRIENDS && PrivateMessagingPacket.getMinutesFriendsFor(victim, killer.getPlayerName()) < ItemTransferLog.MINIMUM_MINUTES_FRIENDS) {
						victim.setFlaggedForRwt(true);
						killer.setFlaggedForRwt(true);
						flagged = true;
						String string = Misc.getDateAndTime() + " PVP [" + BannedData.getSourceName(killer) + "] " + " killed [" + BannedData.getSourceName(victim) + "] with risk: " + Misc.formatRunescapeStyle(killer.riskedWealth) + " vs " + Misc.formatRunescapeStyle(victim.riskedWealth) + " at " + victim.getX() + ", " + victim.getY() + ", " + victim.getHeight();
						ItemTransferLog.rwtAlert.add(string + ", friends: " + PrivateMessagingPacket.getMinutesFriendsFor(killer, victim.getPlayerName()));
					}
				}
			}
			if (!flagged) {
				killer.lootValueFromKill = victim.riskedWealth;
				killer.lastTimeKilledPlayer = System.currentTimeMillis();
			}
		}
		boolean ignoreEquipmentAndInventoryLoot = false;
		if (GameMode.getGameModeContains(victim, "IRON MAN") && !killer.getPlayerName().equals(victim.getPlayerName())) {
			ignoreEquipmentAndInventoryLoot = true;
		}
		if (!ignoreEquipmentAndInventoryLoot) {
			for (int e = 0; e < victim.playerEquipment.length; e++) {
				int equipmentItemId = victim.playerEquipment[e];
				if (equipmentItemId <= 0) {
					continue;
				}
				int equipmentItemAmount = victim.playerEquipmentN[e];

				if (victim.bot && ServerConstants.getItemValue(equipmentItemId) > ServerConstants.getBotMaximumWealthItemDrop() && !victim.getPlayerName().equals("Remy E")) {
					continue;
				}
				killer.coinsPile += getUntradeableItemBloodMoneyDropPrice(equipmentItemId);
				victimLoot(victim, killer, equipmentItemId, equipmentItemAmount, victimBot, killerExists);
			}
			for (int i = 0; i < victim.playerItems.length; i++) {
				int itemId = victim.playerItems[i] - 1;
				if (itemId <= 0) {
					continue;
				}
				if (itemId == 12791) {
					RunePouch.dropRunePouchLoot(victim, killer);
				} else if (itemId == 11941) {
					LootingBag.lootingBagDeath(victim, killer, killerExists);
				}
				int itemAmount = victim.playerItemsN[i];
				if (victim.bot && ServerConstants.getItemValue(itemId) > ServerConstants.getBotMaximumWealthItemDrop() && !victim.getPlayerName().equals("Remy E")) {
					continue;
				}
				killer.coinsPile += getUntradeableItemBloodMoneyDropPrice(itemId);
				victimLoot(victim, killer, itemId, itemAmount, victimBot, killerExists);
			}
		}
		if (killerExists) {
			if (killer.coinsPile > 0 && !ignoreEquipmentAndInventoryLoot) {
				Server.itemHandler.createGroundItem(killer, ServerConstants.getMainCurrencyId(), victim.getX(), victim.getY(), victim.getHeight(), killer.coinsPile, false, 0, true,
						victim.getPlayerName(), killer.getPlayerName(), victim.addressIp, victim.addressUid, "dropItemsForKiller create coinsPile");
			}
			Server.itemHandler.createGroundItem(killer, 526, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "dropItemsForKiller create bones");
		}
	}

	/**
	 * Get the blood money amount that will be dropped for the player when the victim risks an untradeable item that drops blood money.
	 *
	 * @param itemId
	 * @return
	 */
	private static int getUntradeableItemBloodMoneyDropPrice(int itemId) {
		if (ItemAssistant.isItemToUntradeableShopOnDeath(itemId)) {
			return ServerConstants.getItemValue(itemId) / 10;
		}
		return 0;
	}

	/**
	 * Used for when a player dies and i have to figure out what to do with the player's inventory items, equipment & looting bag contents.
	 * This sorts out the coinPile, items to shop, create item on ground, bone on ground & create normal version of item on ground.
	 */
	public static void victimLoot(Player victim, Player killer, int itemId, int amount, boolean victimBot, boolean killerExists) {
		if (ItemAssistant.isItemToUntradeableShopOnDeath(itemId)) {
			victim.itemsToShop.add(itemId + " " + amount);
		} else {
			// Lava dragon constant drops and green dragons.
			if (itemId == 1753 || itemId == 536 || itemId == 11943 || itemId == 11992 || itemId == 1747) {
				if (killerExists) {
					killer.coinsPile += ServerConstants.getItemValue(itemId);
					return;
				}
			}
			// Charged ethereum bracelet.
			else if (itemId == 21816 && killerExists) {
				BraceletOfEthereum.diedWithBracelet(victim, killer, itemId);
				return;
			} else {
				int originalItemId = itemId;
				if (killerExists) {
					itemId = ItemAssistant.getNormalItemVersion(killer, victim, itemId);
				}
				// The item was not transformed.
				if (originalItemId == itemId) {
					if (ItemAssistant.isItemToInventoryOnDeath(itemId)) {
						victim.itemsToInventory.add(itemId + " " + amount);
						return;
					}
				}
				if (killerExists) {
					Blowpipe.diedWithBlowpipe(victim, killer, itemId);
				}
				Server.itemHandler
						.createGroundItem(killer, itemId, victim.getX(), victim.getY(), victim.getHeight(), amount, false, 0, true, victim.getPlayerName(), killer.getPlayerName(),
								victim.addressIp, victim.addressUid, "victimLoot " + killer.getPlayerName() + ", " + victim.getPlayerName());
			}

		}

	}

}
