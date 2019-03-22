package game.content.donator;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.dialogue.DialogueChain;
import game.content.miscellaneous.Teleport;
import game.entity.Entity;
import game.entity.MovementState;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Area;
import game.player.Player;
import game.player.PlayerAssistant;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Refill Hp, Prayer, Special and cure poison.
 *
 * @author MGT Madness, created on 16-09-2016, in the 12 hour flight from Malaysia to Egypt.
 */
public class DonatorContent {


	public static void getOffChair(Player player, boolean loggingOut) {

		if (player.usingChair) {
			if (!loggingOut) {
				player.startAnimation(65535);
				Combat.updatePlayerStance(player);
			}
			player.getPA().requestUpdates();
			player.usingChair = false;
			for (int index = 0; index < OccupiedChair.chairData.size(); index++) {
				OccupiedChair chairData = OccupiedChair.chairData.get(index);
				if (chairData.getX() == player.getObjectX() && chairData.getY() == player.getObjectY()) {
					OccupiedChair.chairData.remove(index); // Delete the chair from the list, so another player can come along and occupy it.
					break; // break will stop looping through the list to save performance.
				}
			}
		}
	}

	public static void sitInChair(final Player player) {
		if (player.usingChair) {
			return;
		}

		// Loop through all chairs on the server to see if any chairs exist, if one does exist in the coords given, then do not sit on it.
		for (int index = 0; index < OccupiedChair.chairData.size(); index++) {
			OccupiedChair chairData = OccupiedChair.chairData.get(index);
			if (chairData.getX() == player.getObjectX() && chairData.getY() == player.getObjectY()) {
				player.getPA().sendMessage("This chair is occupied.");
				return;
			}
		}

		// Since this chair coord is not occupied, we now occupy it by adding the chair location in the chair list.
		OccupiedChair.chairData.add(new OccupiedChair(player.getObjectX(), player.getObjectY()));
		player.usingChair = true;
		player.turnPlayerTo(2201, 3249);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (!player.usingChair) {
					container.stop();
					return;
				}
				player.turnPlayerTo(player.getX() - 1, player.getY());
			}

			@Override
			public void stop() {
			}
		}, 1);
		player.getPA().movePlayer(2202, 3249, 0);
		player.playerStandIndex = 3363;
	}

	public static boolean canUseFeature(Player player, DonatorTokenUse.DonatorRankSpentData data) {
		if (player.donatorTokensRankUsed >= data.getTokensRequired()) {
			return true;
		}
		String rankName = Misc.capitalize(data.toString()).replace("_", " ");
		String fullRankName = "<img=" + data.getPlayerRights() + ">" + rankName;
		player.getPA().sendMessage("You need to be " + Misc.getAorAnWithOutKey(rankName) + " " + fullRankName + " to use this feature.");
		player.getPA().sendMessage("Type in ::donate to view all the amazing benefits of being " + Misc.getAorAnWithOutKey(rankName) + " " + fullRankName + "!");
		player.getPA().sendMessage("Donating helps keep " + ServerConstants.getServerName() + " alive and growing!");
		return false;
	}

	/**
	 * Teleport to immortal donator upon request accepted.
	 */
	public static void teleportToImmortalDonator(Player player) {
		if (player.getDuelStatus() >= 1 || Area.inDangerousPvpArea(player) || player.getHeight() == 20) {
			player.playerAssistant.sendMessage("You cannot teleport now.");
			return;
		}
		Teleport.spellTeleport(player, player.teleToXPermission, player.teleToYPermission, player.teleToHeightPermission, false);
	}

	/**
	 * Smooth dance emote.
	 */
	public static void smoothDanceEmote(Player player) {
		player.cannotIssueMovement = true;
		Movement.stopMovement(player);
		player.startAnimation(7535);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer container) {
				i++;
				switch (i) {
					case 3:
						player.startAnimation(7534);
						player.cannotIssueMovement = true;
						break;

					case 5:
						player.startAnimation(7533);
						player.cannotIssueMovement = false;
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	/**
	 * Crazy dance emote.
	 */
	public static void crazyDanceEmote(Player player) {
		player.cannotIssueMovement = true;
		Movement.stopMovement(player);
		player.startAnimation(7536);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer container) {
				i++;
				switch (i) {
					case 6:
						player.startAnimation(7537);
						player.cannotIssueMovement = false;
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	public static void enterDonatorDungeon(Player player) {
		player.getPA().sendFadingScreen("", PlayerAssistant.FadingScreenState.FADE_IN, (byte) 2, 2);
		player.setDialogueChain(new DialogueChain().statement("You enter the dungeon...")).start(player);
		player.doingActionEvent(7);
		player.setMovementState(MovementState.DISABLED);
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			int i = 0;
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				i++;
				switch (i) {
					case 1:
						player.startAnimation(827);
						break;
					case 4:
						spawnInstancedRoom(player);
						break;
					case 5:
						player.setDialogueChain(new DialogueChain().statement("...and find yourself in Dawn's lair.")).start(player);
						player.getPA().movePlayer(1696, 4568, 24 + (player.getPlayerId() * 4));
						player.getPA().sendFadingScreen("", PlayerAssistant.FadingScreenState.FADE_OUT, (byte) 2, 0);
						break;

					case 8:
						player.setMovementState(MovementState.WALKABLE);
						player.getPA().closeInterfaces(true);
						container.stop();
						break;
				}
			}
			@Override
			public void stop() {
			}
		}, 1);
	}

	public static void leaveDonatorDungeon(Player player) {
		player.getPA().sendFadingScreen("", PlayerAssistant.FadingScreenState.FADE_IN, (byte) 2, 0);
		player.doingActionEvent(7);
		player.setMovementState(MovementState.DISABLED);
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				i++;
				switch (i) {
					case 4:
						player.getPA().movePlayer(2554, 2721, 0);
						player.getPA().sendFadingScreen("", PlayerAssistant.FadingScreenState.FADE_OUT, (byte) 2, 0);
						break;

					case 7:
						player.setMovementState(MovementState.WALKABLE);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	public static void spawnInstancedRoom(Player player) {
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				container.stop();
			}

			@Override
			public void stop() {
				for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
					Npc npc = NpcHandler.npcs[index];
					if (npc == null) {
						continue;
					}
					if (npc.getHeight() != player.getHeight()) {
						continue;
					}
					if (npc.npcType == 7884) {
						Pet.deletePet(npc);
					}
				}
				Npc npc = NpcHandler.spawnNpc(player, 7884, 1694 + Misc.random(5), 4575 + Misc.random(5), 24 + (player.getPlayerId() * 4), false, false);
				npc.instancedNpcForceRespawn = true;
			}
		}, 1);

	}
}
