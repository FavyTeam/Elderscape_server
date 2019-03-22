package game.content.packet;

import core.ServerConfiguration;
import game.content.skilling.Farming;
import game.object.ObjectEvent;
import game.object.ObjectRePathing;
import game.object.click.FirstClickObject;
import game.object.click.FourthClickObject;
import game.object.click.SecondClickObject;
import game.object.click.ThirdClickObject;
import game.object.clip.ObjectDefinitionServer;
import game.object.clip.Region;
import game.player.Player;
import game.player.movement.Follow;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Click Object
 */

public class ClickObjectPacket implements PacketType {

	public static final int FIRST_CLICK = 132, SECOND_CLICK = 252, THIRD_CLICK = 70, FOURTH_CLICK = 234;

	static void reset(Player player) {
		player.clickObjectType = 0;
		player.setObjectId(0);
		player.setObjectX(0);
		player.setObjectY(0);
		player.objectYOffset = 0;
		player.objectXOffset = 0;
		player.objectDistance = 1;
		player.resetFaceUpdate();
		player.resetNpcIdentityAttacking();
		player.resetPlayerIdAttacking();
		player.playerAssistant.stopAllActions();
		player.setClickNpcType(0);
		Follow.resetFollow(player);
	}

	static boolean canUseObjects(Player player) {
		if (player.doingAnAction() || player.getDoingAgility() || !player.isTutorialComplete() || player.isTeleporting() || player.getTransformed() > 0) {
			return false;
		}

		if (player.cannotIssueMovement) {
			return false;
		}
		if (player.isInRandomEvent()) {
			return false;
		}
		return true;
	}

	@Override
	public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer) {

		int objectXLength = 0;
		int objectYLength = 0;
		ObjectDefinitionServer objectDefinition = null;

		switch (packetType) {

			case FIRST_CLICK:
				reset(player);
				int objectX = player.getInStream().readSignedWordBigEndianA();
				int objectId = player.getInStream().readUnsignedWord();
				int objectY = player.getInStream().readUnsignedWordA();

				if (!canUseObjects(player)) {
					return;
				}

				if (player.farmingXCoordinate > 0 && objectId == Farming.PATCH_HERBS) {
					objectX = player.farmingXCoordinate;
					objectY = player.farmingYCoordinate;
					player.setWalkingPacketQueue(objectX, objectY, ObjectEvent::stoppedMovingObjectAction);
					player.objectWalkingQueueUsed = true;
				}
				else if (objectId == Farming.patchWeedObject) {
					objectX = 2813;
					objectY = 3337;
					player.setWalkingPacketQueue(objectX, objectY, ObjectEvent::stoppedMovingObjectAction);
					player.objectWalkingQueueUsed = true;
				}

				if (trackPlayer) {
					PacketHandler.saveData(player.getPlayerName(), "objectX: " + objectX);
					PacketHandler.saveData(player.getPlayerName(), "objectId: " + objectId);
					PacketHandler.saveData(player.getPlayerName(), "objectY: " + objectY);
				}
				if (!Region.objectExists(player, objectId, objectX, objectY, player.getHeight())) {
					if (ServerConfiguration.DEBUG_MODE) {
						Misc.print("Object does not exist, first click [ID: " + objectId + "] [Object X: " + objectX + "] [Object Y: " + objectY + "] [Object Height: " + player.getHeight() + "] [Player X from object: " + (player.getX() - objectX) + "] [Player Y from object: " + (player.getY() - objectY) + "]");
					}
					return;
				}
				player.turnPlayerTo(objectX, objectY);
				player.setObjectX(objectX);
				player.setObjectId(objectId);
				player.setObjectY(objectY);

				// Rune essence mine/
				if (player.getObjectId() == 2491) {
					if (player.getObjectX() == 2927 && player.getObjectY() == 4814) {
						player.setObjectX(2929);
						player.setObjectY(4816);
					} else if (player.getObjectX() == 2893 && player.getObjectY() == 4812) {
						player.setObjectX(2895);
						player.setObjectY(4814);
					} else if (player.getObjectX() == 2925 && player.getObjectY() == 4848) {
						player.setObjectX(2927);
						player.setObjectY(4850);
					} else if (player.getObjectX() == 2891 && player.getObjectY() == 4847) {
						player.setObjectX(2893);
						player.setObjectY(4849);
					}
				}
				// Staircase inside the Varrock west bank dungeon.
				else if (player.getObjectId() == 11805 && player.getObjectX() == 3187 && player.getObjectY() == 9833) {
					player.setObjectX(3189);
					player.setObjectY(9833);
				}

				if (ServerConfiguration.DEBUG_MODE) {
					Misc.print("FIRST CLICK Object [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY()
					           + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
				}

				if (Math.abs(player.getX() - player.getObjectX()) > 25 || Math.abs(player.getY() - player.getObjectY()) > 25) {
					Movement.resetWalkingQueue(player);
					break;
				}

				objectDefinition = ObjectDefinitionServer.getObjectDef(player.getObjectId());

				if (objectDefinition == null) {
					return;
				}
				objectXLength = objectDefinition.xLength();
				objectYLength = objectDefinition.yLength();
				player.setInteractingObjectDefinition(objectDefinition);
				if (objectXLength >= 2 || objectYLength >= 2) {
					player.objectDistance = 2;
				}

				if (objectXLength == 3 || objectXLength == 4) {
					player.objectXOffset = 1;
				}
				if (objectYLength == 3 || objectYLength == 4) {
					player.objectYOffset = 1;
				}
				if (objectYLength == 4 || objectXLength == 4) {
					player.objectDistance += 1;
				}

				switch (objectId) {
					case 19040:
						player.objectDistance = 2;
						break;
					case 31878: //Easter event rabbit hole
					case 29111: //New deposit vault object
					case 677: // Corp lair
					case 20671: //torags crypt stairs
						player.objectDistance = 3;
						break;
					case 2114:
						player.objectDistance = 4;
						break;
					case 28855: // wc guild dungeon
						player.objectDistance = 5;
						break;
				}
//				if (!Region.inDistance(objectDefinition, object.face, objectX, objectY, player.getX(), player.getY())) {
//					WalkToObjectEvent existing = player.getWalkToObjectEvent();
//
//					if (existing != null) {
//						player.getEventHandler().stopIfEventEquals(existing);
//					}
//					player.setWalkToObjectEvent(new WalkToObjectEvent(object));
//					player.getEventHandler().addEvent(player, player.getWalkToObjectEvent(), 1);
//					return;
//				}
//				game.entity.movement.Movement movement = game.entity.movement.Movement.create(player, objectX, objectY, true, objectXLength, objectYLength, 25);
//
				//player.setInteractingObject(object);
				ObjectRePathing.applyObjectRepathing(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
				if (ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.getX() == player.specialObjectActionPoint[3] && player.getY() == player.specialObjectActionPoint[4] && !player.tempMoving) {
					FirstClickObject.firstClickObjectOsrs(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
				} else {
					player.clickObjectType = 1;
					ObjectEvent.clickObjectType1Event(player);
				}
				break;

			case SECOND_CLICK:
				reset(player);
				objectId = player.getInStream().readUnsignedWordBigEndianA();
				objectY = player.getInStream().readSignedWordBigEndian();
				objectX = player.getInStream().readUnsignedWordA();
				if (!canUseObjects(player)) {
					return;
				}
				if (trackPlayer) {
					PacketHandler.saveData(player.getPlayerName(), "objectId1: " + objectId);
					PacketHandler.saveData(player.getPlayerName(), "objectY1: " + objectY);
					PacketHandler.saveData(player.getPlayerName(), "objectX1: " + objectX);
				}
				if (!Region.objectExists(player, objectId, objectX, objectY, player.getHeight())) {
					if (ServerConfiguration.DEBUG_MODE) {
						Misc.print("Object does not exist, second click [ID: " + objectId + "] [Object X: " + objectX + "] [Object Y: " + objectY + "] [Object Height: " + player.getHeight() + "] [Player X from object: " + (player.getX() - objectX) + "] [Player Y from object: " + (player.getY() - objectY) + "]");
					}
					return;
				}
				player.turnPlayerTo(objectX, objectY);
				player.setObjectId(objectId);
				player.setObjectY(objectY);
				player.setObjectX(objectX);
				if (ServerConfiguration.DEBUG_MODE) {
					Misc.print("SECOND CLICK Object [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY()
					           + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
				}

				objectDefinition = ObjectDefinitionServer.getObjectDef(player.getObjectId());
				if (objectDefinition == null) {
					return;
				}
				objectXLength = objectDefinition.xLength();
				objectYLength = objectDefinition.yLength();
				if (objectXLength >= 2 || objectYLength >= 2) {
					player.objectDistance = 2;
				}
				if (player.getObjectId() == 29111) //New deposit vault object
				{
					player.objectDistance = 3;
				}

				if (objectXLength == 3 || objectXLength == 4) {
					player.objectXOffset = 1;
				}
				if (objectYLength == 3 || objectYLength == 4) {
					player.objectYOffset = 1;
				}
				if (objectYLength == 4 || objectXLength == 4) {
					player.objectDistance += 1;
				}


				ObjectRePathing.applyObjectRepathing(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
				if (ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.getX() == player.specialObjectActionPoint[3] && player.getY() == player.specialObjectActionPoint[4] && !player.tempMoving) {
					SecondClickObject.secondClickObjectOsrs(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
//				} else if (!ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && !player.tempMoving) {
//					SecondClickObject.secondClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
				} else {
					player.clickObjectType = 2;
					ObjectEvent.clickObjectType2Event(player);

				}
				break;

			case THIRD_CLICK:
				reset(player);
				objectX = player.getInStream().readSignedWordBigEndian();
				objectY = player.getInStream().readUnsignedWord();
				objectId = player.getInStream().readUnsignedWordBigEndianA();
				if (!canUseObjects(player)) {
					return;
				}

				if (trackPlayer) {
					PacketHandler.saveData(player.getPlayerName(), "objectX2: " + objectX);
					PacketHandler.saveData(player.getPlayerName(), "objectY2: " + objectY);
					PacketHandler.saveData(player.getPlayerName(), "objectId2: " + objectId);
				}

				if (!Region.objectExists(player, objectId, objectX, objectY, player.getHeight())) {
					if (ServerConfiguration.DEBUG_MODE) {
						Misc.print("Object does not exist, third click [ID: " + objectId + "] [Object X: " + objectX + "] [Object Y: " + objectY + "] [Object Height: " + player.getHeight() + "] [Player X from object: " + (player.getX() - objectX) + "] [Player Y from object: " + (player.getY() - objectY) + "]");
					}
					return;
				}
				player.turnPlayerTo(objectX, objectY);
				player.setObjectId(objectId);
				player.setObjectY(objectY);
				player.setObjectX(objectX);

				if (ServerConfiguration.DEBUG_MODE) {
					Misc.print("THIRD CLICK Object [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY()
					           + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
				}

				objectDefinition = ObjectDefinitionServer.getObjectDef(player.getObjectId());
				if (objectDefinition == null) {
					return;
				}
				objectXLength = objectDefinition.xLength();
				objectYLength = objectDefinition.yLength();
				if (objectXLength >= 2 || objectYLength >= 2) {
					player.objectDistance = 2;
				}

				if (objectXLength == 3 || objectXLength == 4) {
					player.objectXOffset = 1;
				}
				if (objectYLength == 3 || objectYLength == 4) {
					player.objectYOffset = 1;
				}
				if (objectYLength == 4 || objectXLength == 4) {
					player.objectDistance += 1;
				}
				if (player.getObjectId() == 29111) //New deposit vault object
				{
					player.objectDistance = 3;
				}
				ObjectRePathing.applyObjectRepathing(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
				if (ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.getX() == player.specialObjectActionPoint[3] && player.getY() == player.specialObjectActionPoint[4] && !player.tempMoving) {
					ThirdClickObject.thirdClickObjectOsrs(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
//				} else if (!ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && !player.tempMoving) {
//					ThirdClickObject.thirdClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
				} else {
					player.clickObjectType = 3;
					ObjectEvent.clickObjectType3Event(player);
				}
				break;

			case FOURTH_CLICK:
				reset(player);
				objectX = player.getInStream().readSignedWordBigEndian();
				objectId = player.getInStream().readUnsignedWord();
				objectY = player.getInStream().readUnsignedWordBigEndianA();
				objectId += 128;
				objectX -= 128;
				if (!canUseObjects(player)) {
					return;
				}

				if (trackPlayer) {
					PacketHandler.saveData(player.getPlayerName(), "objectX2: " + objectX);
					PacketHandler.saveData(player.getPlayerName(), "objectY2: " + objectY);
					PacketHandler.saveData(player.getPlayerName(), "objectId2: " + objectId);
				}


				if (!Region.objectExists(player, objectId, objectX, objectY, player.getHeight())) {
					if (ServerConfiguration.DEBUG_MODE) {
						Misc.print("Object does not exist, fourth click [ID: " + objectId + "] [Object X: " + objectX + "] [Object Y: " + objectY + "] [Object Height: " + player.getHeight() + "] [Player X from object: " + (player.getX() - objectX) + "] [Player Y from object: " + (player.getY() - objectY) + "]");
					}
					return;
				}
				player.turnPlayerTo(objectX, objectY);
				player.setObjectId(objectId);
				player.setObjectY(objectY);
				player.setObjectX(objectX);

				if (ServerConfiguration.DEBUG_MODE) {
					Misc.print("FOURTH CLICK Object [ID: " + player.getObjectId() + "] [Object X: " + player.getObjectX() + "] [Object Y: " + player.getObjectY()
					           + "] [Player X from object: " + (player.getX() - player.getObjectX()) + "] [Player Y from object: " + (player.getY() - player.getObjectY()) + "]");
				}
				objectDefinition = ObjectDefinitionServer.getObjectDef(player.getObjectId());
				if (objectDefinition == null) {
					return;
				}
				objectXLength = objectDefinition.xLength();
				objectYLength = objectDefinition.yLength();
				if (objectXLength >= 2 || objectYLength >= 2) {
					player.objectDistance = 2;
				}

				if (objectXLength == 3 || objectXLength == 4) {
					player.objectXOffset = 1;
				}
				if (objectYLength == 3 || objectYLength == 4) {
					player.objectYOffset = 1;
				}
				if (objectYLength == 4 || objectXLength == 4) {
					player.objectDistance += 1;
				}
				ObjectRePathing.applyObjectRepathing(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
				if (ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.getX() == player.specialObjectActionPoint[3] && player.getY() == player.specialObjectActionPoint[4] && !player.tempMoving) {
					FourthClickObject.fourthClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
//				} else if (!ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && !player.tempMoving) {
//					FourthClickObject.fourthClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
				} else {
					player.clickObjectType = 4;
					ObjectEvent.clickObjectType4Event(player);
				}
				break;
		}

	}

}
