package game.content.packet;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.donator.AfkChair;
import game.content.interfaces.ChangePasswordInterface;
import game.content.miscellaneous.MithrilSeeds;
import game.content.miscellaneous.RandomEvent;
import game.content.miscellaneous.Transform;
import game.content.music.SoundSystem;
import game.object.ObjectEvent;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import game.player.movement.PathFinder;
import network.connection.VoteManager;
import network.packet.PacketHandler;
import network.packet.PacketType;

public class WalkingPacket implements PacketType {
	private final static int MINIMAP_CLICK_PACKET = 248;

	private final static int TILE_CLICK_PACKET = 164;

	private final static int OBJECT_OR_NPC_CLICK_PACKET = 98;

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		PathFinder.loops1 = 0;
		PathFinder.loops2 = 0;
		PathFinder.loops3 = 0;
		PathFinder.loops4 = 0;
		PathFinder.loops5 = 0;
		PathFinder.loops6 = 0;
		PathFinder.destX = 0;
		PathFinder.destY = 0;
		if (player.passwordChangeForce) {
			ChangePasswordInterface.display(player);
			return;
		}
		if (player.notifyFlagged) {
			player.getPA().displayInterface(25000); // Quest interface to re-show the charge back text.
			return;
		}
		if (!player.isTutorialComplete()) {
			player.setNpcType(306);
			player.getDH().sendDialogues(GameType.isOsrsPvp() ? 270 : 560);
			return;
		}
		if (player.quests.length >= 6 && player.questStages.length >= 6) {
			if (player.getQuest(5) != null) {
			if (player.getQuest(5).getStage() == 4) {
				player.getDH().sendDialogues(1635409878);
				return;
			}
			}
		}
		if (player.getTransformed() == 5) {
			Transform.unTransform(player);
		}
		if (player.doingAnAction() && !player.isAnEgg) {
			return;
		}

		if (player.dragonSpearEvent) {
			return;
		}
		if (player.cannotIssueMovement) {
			return;
		}
		if (player.isInRandomEventType("SELECT_SKILL")) {
			RandomEvent.summonRandomEventClickSkillInterface(player);
			return;
		}
		if (RandomEvent.showRandomEventNpcDialogue(player)) {
			return;
		}
		if (player.diceRulesForce) {
			player.getPA().displayInterface(28870);
			return;
		}
		if (player.closePmInterfaceOnWalk) {
			player.getPA().sendMessage(":packet:closepminterface");
		}

		// If player is 3 versions outdated, then prevent from walking.
		if (player.clientVersion <= Server.clientVersion - 3) {
			player.getPA().sendMessage(ServerConstants.RED_COL + "Your client is very oudated!");
			player.getPA().sendMessage(ServerConstants.RED_COL + "Please update by downloading the client from www.dawntained.com");
			player.getPA().sendMessage(ServerConstants.RED_COL + "or restart your client.");
			return;
		}
		if (player.getWalkToObjectEvent() != null) {
			player.getEventHandler().stopIfEventEquals(player.getWalkToObjectEvent());
		}
		player.doNotClosePmInterface = true;
		player.getPA().closeInterfaces(false);
		MithrilSeeds.resetPlayerPlantData(player);
		// When clicking on Npc/object, packet 98 is sent. Which is a walking packet.
		if (packetType == MINIMAP_CLICK_PACKET || packetType == TILE_CLICK_PACKET) {
			player.clickObjectType = 0;
			player.setClickNpcType(0);
			player.resetNpcIdentityAttacking();
			player.resetPlayerIdAttacking();
			player.resetPlayerTurn();
			player.resetFaceUpdate();
			Follow.resetFollow(player);
		}
		player.hasLastAttackedAPlayer = false;
		player.getDH().dialogueWalkingReset();
		AfkChair.resetAfk(player, false);
		player.playerAssistant.stopAllActions();
		player.isUsingDeathInterface = false;
		if (player.isAnEgg) {
			Transform.unTransform(player);
		}

		if (player.timeFinishedTutorial > 0 && player.isTutorialComplete()) {
			player.timeFinishedTutorial = 0;
			VoteManager.newPlayerVoteAlert(player);
			Movement.stopMovement(player);
			return;
		}

		if (player.isFrozen()) {
			if (PlayerHandler.players[player.getPlayerIdAttacking()] != null) {
				if (player.playerAssistant.withInDistance(player.getX(), player.getY(), PlayerHandler.players[player.getPlayerIdAttacking()].getX(),
				                                          PlayerHandler.players[player.getPlayerIdAttacking()].getY(), 1) && packetType != OBJECT_OR_NPC_CLICK_PACKET) {
					player.resetPlayerIdAttacking();
					return;
				}
			}
			if (packetType != OBJECT_OR_NPC_CLICK_PACKET) {
				player.resetPlayerIdAttacking();
			}
			SoundSystem.sendSound(player, 221, 0);
			player.getPA().sendFilterableMessage("A magical force stops you from moving.");
			return;
		}

		// Player in duel screen.
		if ((player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4)) {
			Player o = player.getTradeAndDuel().getPartnerDuel();
			if (o != null) {
				o.getTradeAndDuel().declineDuel(false);
			}
			player.getTradeAndDuel().declineDuel(true);
		}
		player.getTradeAndDuel().claimStakedItems();
		if (player.getDead()) {
			return;
		}
		if (player.isInTrade()) {
			player.getTradeAndDuel().declineTrade1(true);
		}
		player.setTradeStatus(0);
		player.tradeRequested = false;
		player.setTradeWith(0);
		player.lastTradedWithName = "";
		player.duelRequested = false;
		if (player.getDuelStatus() == 0) {
			player.setDuelingWith(0);
		}
		if (packetType == 248) {
			packetSize -= 14;
		}

		if (packetSize < 5) {
			return;
		}
		if (player.noClip) {
			player.setNewWalkCmdSteps((packetSize - 5) / 2);
			if (player.setNewWalkCmdSteps(player.getNewWalkCmdSteps() + 1) > player.walkingQueueSize) {
				player.setNewWalkCmdSteps(0);
				return;
			}

			player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = 0;

			int firstStepX = player.getInStream().readSignedWordBigEndianA() - player.getMapRegionX() * 8;
			for (int i = 1; i < player.getNewWalkCmdSteps(); i++) {
				player.getNewWalkCmdX()[i] = player.getInStream().readSignedByte();
				player.getNewWalkCmdY()[i] = player.getInStream().readSignedByte();
			}

			int firstStepY = player.getInStream().readSignedWordBigEndian() - player.getMapRegionY() * 8;
			player.setNewWalkCmdIsRunning(player.getInStream().readSignedByteC() == 1);
			for (int i1 = 0; i1 < player.getNewWalkCmdSteps(); i1++) {
				player.getNewWalkCmdX()[i1] += firstStepX;
				player.getNewWalkCmdY()[i1] += firstStepY;
			}
		} else {
			player.setNewWalkCmdSteps((packetSize - 5) / 2);
			if (player.setNewWalkCmdSteps(player.getNewWalkCmdSteps() + 1) > player.walkingQueueSize) {
				player.setNewWalkCmdSteps(0);
				return;
			}

			player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = 0;

			int firstStepX = player.getInStream().readSignedWordBigEndianA() - player.getMapRegionX() * 8;
			for (int i = 1; i < player.getNewWalkCmdSteps(); i++) {
				player.getNewWalkCmdX()[i] = player.getInStream().readSignedByte();
				player.getNewWalkCmdY()[i] = player.getInStream().readSignedByte();
			}

			int firstStepY = player.getInStream().readSignedWordBigEndian() - player.getMapRegionY() * 8;
			player.setNewWalkCmdIsRunning(player.getInStream().readSignedByteC() == 1);
			for (int i1 = 0; i1 < player.getNewWalkCmdSteps(); i1++) {
				player.getNewWalkCmdX()[i1] += firstStepX;
				player.getNewWalkCmdY()[i1] += firstStepY;
			}
			int pathX = player.getNewWalkCmdX()[player.getNewWalkCmdSteps() - 1] + player.getMapRegionX() * 8;
			int pathY = player.getNewWalkCmdY()[player.getNewWalkCmdSteps() - 1] + player.getMapRegionY() * 8;
			Movement.stopMovementDifferent(player); // This reset has to be here for the playerWalk to work.
			if (packetType == OBJECT_OR_NPC_CLICK_PACKET && !player.objectWalkingQueueUsed) {
				player.setWalkingPacketQueue(pathX, pathY, ObjectEvent::stoppedMovingObjectAction);
			} else {
				player.setWalkingPacketQueue(pathX, pathY);
			}
			player.objectWalkingQueueUsed = false;
			player.lastFollowType = "";
			player.tempDir1 = Movement.tempGetNextWalkingDirection(player);
			player.tempRunning = player.runModeOn;
			player.tempRunning = player.isNewWalkCmdIsRunning() || player.runModeOn;
			if (player.isRunning) {
				player.tempDir2 = Movement.tempGetNextWalkingDirection(player);
			}
			if (player.tempDir1 == -1) {
				player.tempMoving = false;
			} else {
				player.tempMoving = true;
			}

			if (trackPlayer) {
				PacketHandler.saveData(player.getPlayerName(), "Walk to: " + pathX + ", " + pathY);
			}
		}



	}
}
