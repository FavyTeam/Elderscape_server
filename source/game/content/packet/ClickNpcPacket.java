package game.content.packet;

import core.ServerConfiguration;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.vsnpc.CombatNpc;
import game.content.combat.vsnpc.MagicOnNpcPacket;
import game.content.combat.vsplayer.range.RangedData;
import game.npc.Npc;
import game.npc.NpcEvent;
import game.npc.NpcHandler;
import game.npc.clicknpc.FirstClickNpc;
import game.npc.clicknpc.FourthClickNpc;
import game.npc.clicknpc.SecondClickNpc;
import game.npc.clicknpc.ThirdClickNpc;
import game.npc.data.NpcDefinition;
import game.player.Player;
import game.player.movement.Follow;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Click NPC
 */
public class ClickNpcPacket implements PacketType {
	public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155, SECOND_CLICK = 17, THIRD_CLICK = 21, FOURTH_CLICK = 18;

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		if (player.doingAnAction() || player.isTeleporting() || player.getDoingAgility() || !player.isTutorialComplete()) {
			return;
		}
		if (player.getDead()) {
			return;
		}
		if (player.isInRandomEvent()) {
			return;
		}
		player.resetNpcIdentityAttacking();
		player.setNpcClickIndex(0);
		player.resetPlayerIdAttacking();
		player.setClickNpcType(0);
		Follow.resetFollow(player);
		player.clickObjectType = 0;
		switch (packetType) {

			/**
			 * Attack npc melee or range
			 **/
			case ATTACK_NPC:
				int npcId = player.getInStream().readUnsignedWordA();
				if (trackPlayer) {
					PacketHandler.saveData(player.getPlayerName(), "npcId: " + npcId);
				}
				if (npcId > NpcHandler.NPC_INDEX_OPEN_MAXIMUM || npcId < 0) {
					return;
				}
				Npc npc = NpcHandler.npcs[npcId];
				if (npc == null) {
					break;
				}
				if (!player.playerAssistant.withinDistance(npc)) {
					break;
				}
				player.setNpcIdentityAttacking(npcId);
				if (ServerConfiguration.DEBUG_MODE) {
					Misc.print("[Attack npc: " + npc.npcType + "]");
				}
				if (npc.maximumHitPoints == 0 && player.getNpcIdAttacking() == npc.npcIndex) {
					player.resetNpcIdentityAttacking();
					break;
				}
				if (trackPlayer) {
					PacketHandler.saveData(player.getPlayerName(), "attack npc: " + npc.npcType);
				}
				player.setNpcIdToFollow(npc.npcIndex);
				player.faceUpdate(player.getNpcIdAttacking());
				player.setLastCastedMagic(false);

				// Reset movement, this is to cancel the walking packet, because big bosses like Bandos, Callisto, are
				// 2 tiles off x and y on the client, so the client walks the wrong way.

				if (Follow.isBigNpc(npc.npcType) > 0) {
					Movement.resetWalkingQueue(player);
					Movement.stopMovementDifferent(player);
				}
				boolean usingBow = false;
				boolean isWieldingRangedWeaponWithNoArrowSlotRequirement = false;
				boolean usingCross = Combat.getUsingCrossBow(player);
				if (player.getAutocastId() > -1) {
					player.setAutoCasting(true);
					player.setLastCastedMagic(true);
				} else {

					if (npc.npcType >= 1610 && npc.npcType <= 1612 && player.getWieldedWeapon() != 12899) {
						player.getPA().sendMessage("You can only use magic spells on this npc.");
						Movement.stopMovement(player);
						player.resetNpcIdentityAttacking();
						player.turnPlayerTo(npc.getX(), npc.getY());
						Combat.resetPlayerAttack(player);
						return;
					}
				}
				if (!player.getAutoCasting() && player.getSpellId() > 0) {
					player.setSpellId(-1);
				}
				if (player.getWieldedWeapon() >= 4214 && player.getWieldedWeapon() <= 4223) {
					usingBow = true;
				}
				if (RangedData.isWieldingMediumRangeRangedWeapon(player)) {
					usingBow = true;
				}
				if (RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(player)) {
					isWieldingRangedWeaponWithNoArrowSlotRequirement = true;
				}
				if ((usingBow || player.getAutoCasting()) && player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 8)) {
					Movement.stopMovement(player);
				}

				if (isWieldingRangedWeaponWithNoArrowSlotRequirement && player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), CombatConstants.getAttackDistance(player))) {
					Movement.stopMovement(player);
				}
				if (!usingCross && !usingBow && !isWieldingRangedWeaponWithNoArrowSlotRequirement && !player.hasLastCastedMagic()) {
					player.setMeleeFollow(true);
				} else {
					player.setMeleeFollow(false);
				}
				int size = 1;
				if (NpcDefinition.getDefinitions()[npc.npcType] != null) {
					size = NpcDefinition.getDefinitions()[npc.npcType].size;
				}
				if (size > 1) {
					player.ignoreBigNpcWalkPacket = true;
				}
				CombatNpc.setEngageWithMagicVariables(player);
				break;

			/**
			 * Attack npc with magic
			 **/
			case MAGE_NPC:
				int npcId1 = player.getInStream().readSignedWordBigEndianA();
				int castingSpellId = player.getInStream().readSignedWordA();
				if (npcId1 > NpcHandler.NPC_INDEX_OPEN_MAXIMUM || npcId1 < 0) {
					return;
				}
				if (trackPlayer) {
					PacketHandler.saveData(player.getPlayerName(), "npcId1: " + npcId1);
					PacketHandler.saveData(player.getPlayerName(), "castingSpellId: " + castingSpellId);
				}
				if (ServerConfiguration.DEBUG_MODE) {
					Misc.print("[Mage npc: " + npcId1 + "]");
				}
				player.setNpcIdentityAttacking(npcId1);
				MagicOnNpcPacket.magicOnNpcPacket(player, castingSpellId, trackPlayer);
				break;

			case FIRST_CLICK:
				handleClickNpc(player, 1, player.inStream.readSignedWordBigEndian(), trackPlayer, "First");
				break;

			case SECOND_CLICK:
				handleClickNpc(player, 2, player.inStream.readUnsignedWordBigEndianA(), trackPlayer, "Second");
				break;

			case THIRD_CLICK:
				handleClickNpc(player, 3, player.inStream.readSignedWord(), trackPlayer, "Third");
				break;

			case FOURTH_CLICK:
				handleClickNpc(player, 4, player.inStream.readSignedWordBigEndian(), trackPlayer, "Fourth");
				break;
		}
	}

	private void handleClickNpc(Player player, int clickType, int npcIndex, boolean trackPlayer, String clickTypeString) {
		if (npcIndex > NpcHandler.NPC_INDEX_OPEN_MAXIMUM || npcIndex < 0) {
			return;
		}
		Npc npc = NpcHandler.npcs[npcIndex];
		if (npc == null) {
			return;
		}
		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("[" + clickTypeString + " click npc: " + npc.npcType + "]");
		}

		if (!player.playerAssistant.withinDistance(npc)) {
			return;
		}

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "npcId" + clickType + ": " + npcIndex);
		}
		player.setNpcClickIndex(npcIndex);
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), clickTypeString + " click on npc: " + npc.npcType);
		}
		player.getPA().closeInterfaces(true);
		player.setNpcIdToFollow(npcIndex);
		player.setMeleeFollow(true);
		player.setNpcType(npc.npcType);
		player.faceUpdate(npc.npcIndex);

		if (player.getPA().playerOnNpc(player, npc)) {
			Movement.movePlayerFromUnderEntity(player);
		}

		// Move the player to the correct location if the npc is behind a wall.
		int[] location = getNpcBehindWallInteractLocation(npc);
		if (location != null) {
			player.setWalkingPacketQueue(location[0], location[1]);
			player.resetNpcIdToFollow();
		}
		if (!player.getPA().playerOnNpc(player, npc) && player.playerAssistant.withInDistance(npc.getX(), npc.getY(), player.getX(), player.getY(), getNpcInteractionDistance(npc)) && player.getPA().canInteractWithNpc(npc)) {
			switch (clickType) {
				case 1:
					FirstClickNpc.firstClickNpc(player, player.getNpcType());
					break;
				case 2:
					SecondClickNpc.secondClickNpc(player, player.getNpcType());
					break;
				case 3:
					ThirdClickNpc.thirdClickNpc(player, player.getNpcType());
					break;
				case 4:
					FourthClickNpc.fourthClickNpc(player, player.getNpcType());
					break;
			}
		} else {
			player.setClickNpcType(clickType);
			switch (clickType) {
				case 1:
					NpcEvent.clickNpcType1Event(player);
					break;
				case 2:
					NpcEvent.clickNpcType2Event(player);
					break;
				case 3:
					NpcEvent.clickNpcType3Event(player);
					break;
				case 4:
					NpcEvent.clickNpcType4Event(player);
					break;
			}
		}
	}

	/**
	 * @return The distance between the player and this non-combat npc for them to interact.
	 */
	public static int getNpcInteractionDistance(Npc npc) {
		if (NpcDefinition.getDefinitions()[npc.npcType] == null) {
			return 1;
		}
		return npc.behindWallDistance > 0 ? npc.behindWallDistance : NpcDefinition.getDefinitions()[npc.npcType].size;
	}

	/**
	 * @return The location of where the player must be in order to interact with this non-combat npc.
	 */
	public static int[] getNpcBehindWallInteractLocation(Npc npc) {
		if (npc.behindWallDistance == 0) {
			return null;
		}
		int xOffset = 0;
		int yOffset = 0;
		int[] location = new int[2];
		switch (npc.faceAction) {
			case "WEST":
				xOffset--;
				break;
			case "EAST":
				xOffset++;
				break;
			case "NORTH":
				yOffset++;
				break;
			case "SOUTH":
				yOffset--;
				break;
		}
		if (xOffset != 0 || yOffset != 0) {
			location[0] = npc.getX() + (xOffset * npc.behindWallDistance);
			location[1] = npc.getY() + (yOffset * npc.behindWallDistance);
		}
		return location;
	}
}
