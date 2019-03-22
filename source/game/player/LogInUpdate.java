package game.player;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotContent;
import game.container.impl.MoneyPouch;
import game.content.bank.Bank;
import game.content.clanchat.DefaultClanChat;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.Poison;
import game.content.combat.Venom;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.commands.AdministratorCommand;
import game.content.consumable.Potions;
import game.content.consumable.RegenerateSkill;
import game.content.donator.Yell;
import game.content.highscores.HighscoresDaily;
import game.content.interfaces.AreaInterface;
import game.content.interfaces.ChangePasswordInterface;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.donator.DonatorMainTab;
import game.content.minigame.WarriorsGuild;
import game.content.minigame.barrows.Barrows;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.ClaimPrize;
import game.content.miscellaneous.GodWarsDungeonInterface;
import game.content.miscellaneous.OutdatedClient;
import game.content.miscellaneous.OverlayTimer;
import game.content.miscellaneous.PlayerGameTime;
import game.content.miscellaneous.RandomEvent;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.WelcomeMessage;
import game.content.miscellaneous.Wolpertinger;
import game.content.miscellaneous.XpBonus;
import game.content.miscellaneous.YoutubeRank;
import game.content.music.Music;
import game.content.prayer.book.regular.RegularPrayer;
import game.content.profile.ProfileRank;
import game.content.quest.QuestHandler;
import game.content.quest.tab.ActivityTab;
import game.content.quest.tab.InformationTab;
import game.content.skilling.HitPointsRegeneration;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.content.staff.StaffManagement;
import game.content.starter.NewPlayerContent;
import game.content.tradingpost.TradingPostItems;
import game.content.worldevent.Tournament;
import game.content.worldevent.WorldEvent;
import game.item.ItemAssistant;
import game.log.FlaggedData;
import game.npc.pet.Pet;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.pet.PlayerPet;
import game.player.pet.PlayerPetManager;
import game.player.pet.PlayerPetState;
import game.player.punishment.Blacklist;
import game.player.punishment.IpMute;
import game.player.punishment.RagBan;
import network.connection.HostList;
import network.connection.VoteManager;
import utility.LeakedSourceApi;
import utility.Misc;

/**
 * Update the game for the player that just logged in.
 *
 * @author MGT Madness, created on 13-12-2013.
 */
public class LogInUpdate {

	/**
	 * Update game for the player on login.
	 */
	public static void update(Player player) {
		for (int index = 0; index < player.playerItems.length; index++) {
			if (player.playerItemsN[index] == -1 || player.playerItems[index] == 1) {
				player.playerItemsN[index] = 0;
				player.playerItems[index] = 0;
			}
		}
		if (player.playerEquipmentN[ServerConstants.ARROW_SLOT] == -1) {
			player.clearEquipmentSlot(player, ServerConstants.ARROW_SLOT);
			}
		updateBeforeNotice(player);
		if (!player.bot) {
			player.getOutStream().createFrame(249);
			player.getOutStream().writeByteA(1);
			player.getOutStream().writeWordBigEndianA(player.getPlayerId());
		}
		player.saveCharacter = true;
		//ChargebackPlayerAutoJail.isPlayerToNotifyLoggedIn(player);
		Skilling.updateAllSkillTabFrontText(player);
		interfaceFramesUpdateOnLogIn(player);
		RegularPrayer.resetAllPrayerGlows(player);
		player.getPA().resetCameraShake(); // Reset screen
		player.getPA().setChatOptions(player.publicChatMode, player.privateChat, player.tradeChatMode); // Reset private messaging options
		InterfaceAssistant.splitPrivateChat(player);
		InterfaceAssistant.showTabs(player);
		AutoCast.resetAutocast(player, true);
		Combat.resetPrayers(player);
		CombatInterface.updateSpecialBar(player);
		player.getPA().showOption(4, 0, "Follow", 4);
		player.getPA().showOption(5, 0, "Trade With", 3);
		ItemAssistant.resetItems(player, 3214);//this is the former initialize method in the client class
		ItemAssistant.calculateEquipmentBonuses(player);
		ItemAssistant.updateEquipmentInterface(player);
		player.getPA().privateMessagingLogIn();
		CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
		Follow.resetFollow(player);
		player.getPA().sendFrame36(172, player.getAutoRetaliate(), false);
		Skull.startSkullTimerEvent(player);
		Pet.ownerLoggedIn(player);
		player.getPA().sendFrame214();
		PlayerGameTime.startMilliSecondsOnline(player);
		WelcomeMessage.sendWelcomeMessage(player);
		DefaultClanChat.createDefaultClanChat(player);
		Combat.restoreSpecialAttackEvent(player);
		Combat.updatePlayerStance(player); // Added here to update the player's client.
		PlayerContentTick.updateClientHeight(player, true);
		player.handler.updatePlayer(player, player.getOutStream());
		player.handler.updateNpc(player, player.getOutStream());
		player.flushOutStream();
		Server.objectManager.changeRegionPacketClientObjectUpdate(player, false);
		InterfaceAssistant.updateCombatLevel(player); // Must be after updatePlayer.
		ItemAssistant.updateEquipmentLogIn(player);
		Skilling.updateTotalLevel(player);
		Combat.updatePlayerStance(player); // Added here to update for other players.
		player.setAppearanceUpdateRequired(true);
		Potions.overloadReBoostEvent(player);
		RegenerateSkill.logInUpdate(player);
		Poison.appendPoison(null, player, true, 0);
		Venom.appendVenom(null, player, true);
		Potions.antiFirePotionEvent(player);
		Potions.staminaPotionEvent(player);
		OverlayTimer.updateOverlayTimersOnLogIn(player);
		Skilling.updateTotalSkillExperience(player, Skilling.getExperienceTotal(player));
		HitPointsRegeneration.startHitPointsRegeneration(player);
		updateAppearanceForOtherPlayers(player);
		ProfileRank.saveCurrentRanks(player);
		Skilling.sendXpToDisplay(player);
		BotContent.gearUp(player, true);
		player.loadQuests();
		Bank.updateClientLastXAmount(player, player.lastXAmount);
		Zombie.logInUpdate(player);
		AgilityAssistant.runEnergyGain(player);
		RunePouch.updateRunePouchMainStorage(player, true);
		HighscoresDaily.getInstance().informHighscores(player);
		WorldEvent.logInUpdate(player);
		Tournament.logOutUpdate(player, true);
		ClaimPrize.checkForReward(player, true);
		ClaimPrize.checkOnLogIn(player);
		IpMute.ipMuteLogInUpdate(player);
		RagBan.loggedIn(player);
		RandomEvent.randomEventLogInUpdate(player);
		YoutubeRank.logInUpdate(player);
		Pet.claimPet(player);
		FlaggedData.isFlaggedPlayerForPacketTracking(player);
		RagBan.loggedInWithMultiLogging(player);
		player.getPA().alertNotSameIp();
		player.getPA().saveGameEvent();
		player.getPA().loopedSave();
		TradingPostItems.tradingPostItemsLogInNotice(player);
		player.loggingInFinished = true;
		OutdatedClient.warnPlayer(player);
		player.getPA().adminPrivateLogInNotification();
		VoteManager.voteAlert(player);
		StaffManagement.pendingRankLogIn(player);
		player.setFlaggedForRwt(player.isModeratorRank() ? true : false);
		player.getPA().removedFromPrivateInstance();
		QuestHandler.updateAllQuestTab(player);
		WarriorsGuild.startTokenDrainingEvent(player);
		DonatorMainTab.updateAccountOffer(player, false);
		Server.getMinigameManager().login(player);
		XpBonus.initiateXpBonusIfActive(player);
		MoneyPouch.onLogin(player);
		Yell.sendYellModeToClient(player);
		player.getPA().chatEffectUpdate();
		LeakedSourceApi.checkForLeakedSourceEntry(player, false);
		ChangePasswordInterface.logIn(player);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				AdministratorCommand.floodCheck(player);
				container.stop();
			}

			@Override
			public void stop() {
			}
		}, 25);

		if (AdministratorCommand.musicCheck) {
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (player.isCombatBot()) {
						return;
					}
					if (player.secondsBeenOnline > 600) {
						return;
					}
					if (player.musicPacketsReceived < 2) {
						if (!player.addressIp.isEmpty() && !Blacklist.floodIps.contains(player.addressIp) || !Blacklist.floodAccountBans.contains(player.getPlayerName())) {
							Blacklist.floodBlockReason.add("[" + Misc.getDateAndTime() + "] " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid
							                               + ": booted for music received: " + player.musicPacketsReceived);
							player.setDisconnected(true, "update floodIps or floodAccountBans");
							player.setTimeOutCounter(ServerConstants.TIMEOUT + 10);
							if (!Blacklist.floodAccountBans.contains(player.getPlayerName())) {
								Blacklist.floodAccountBans.add(player.getPlayerName().toLowerCase());
							}
							if (!Blacklist.floodIps.contains(player.addressIp)) {
								Blacklist.floodIps.add(player.addressIp);
							}
						}
					}
					if (!player.addressIp.isEmpty() && Blacklist.floodIps.contains(player.addressIp) || Blacklist.floodAccountBans.contains(player.getPlayerName())) {
						player.setDisconnected(true, "update, blacklist floodIps or floodAccountBans");
						player.setTimeOutCounter(ServerConstants.TIMEOUT + 10);
						if (!Blacklist.floodAccountBans.contains(player.getPlayerName())) {
							Blacklist.floodAccountBans.add(player.getPlayerName().toLowerCase());
						}
						if (!Blacklist.floodIps.contains(player.addressIp)) {
							Blacklist.floodIps.add(player.addressIp);
						}
					}
					container.stop();
				}

				@Override
				public void stop() {
				}
			}, 35);
		}
		PlayerPetState playerPetState = player.getPlayerPetState();

		if (playerPetState != null && playerPetState.isSummoned()) {
			PlayerPet pet = PlayerPetManager.getSingleton().create(player);

			player.setPlayerPet(pet);
		}
	}

	private static void connectionCheck(Player player) {

		if (player.bot) {
			return;
		}
		if (HostList.countConnections(player.addressIp) > (ServerConfiguration.DEBUG_MODE ? 2000 : ServerConstants.IPS_ALLOWED)) {
			player.setDisconnected(true, "connectionCheck");
			player.setTimeOutCounter(ServerConstants.TIMEOUT + 1);
			player.hasTooManyConnections = true;
		} else {
			HostList.connections.add(player.addressIp);
		}
	}

	private static void updateAppearanceForOtherPlayers(final Player player) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				player.setAppearanceUpdateRequired(true);
			}
		}, 1);
	}

	private static void interfaceFramesUpdateOnLogIn(Player player) {
		InformationTab.updateQuestTab(player);
		Skilling.updateSkillTabExperienceHover(player, 0, true);
		ActivityTab.updateActivityTab(player);
	}

	/**
	 * Update certain parts of the game for the player, before the player can see the update happening.
	 *
	 * @param player The assosciated player.
	 */
	private static void updateBeforeNotice(Player player) {
		// Sometimes the player will log in and his x and y will be -1.
		if (player.teleportToY >= 0 && player.teleportToX >= 0) {
			player.setX(player.teleportToX);
			player.setY(player.teleportToY);
		}
		Skull.updateSkullOnLogIn(player);
		player.playerAssistant.calculateCombatLevel();
		connectionCheck(player);
		player.playerAssistant.sendMessage(":rights" + player.playerRights + ":"); // Tell client the player rights, to use on player name chat area crown.
		NewPlayerContent.logIn(player);
		player.getPA().sendFrame36(173, player.runModeOn ? 1 : 0, false); // Inform the client that running is on.
		InterfaceAssistant.informClientRestingState(player, "off");
		AgilityAssistant.updateRunEnergyInterface(player);
		AreaInterface.updateWalkableInterfaces(player);
		Wolpertinger.updateSummoningOrb(player);
		player.getTradeAndDuel().removeFromArena();
		Music.requestMusicStateFromClient(player);
		Barrows.updateSavedBarrowsProgress(player);
		RegularPrayer.updateRigourAndAugury(player);
		Barrows.updateBarrowsInterface(player);
		GodWarsDungeonInterface.updateGwdInterface(player);
		player.getPA().updateDisplayBots();
		Area.removeFromUnaccessibleArea(player);
	}

}
