package game.player;

import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.object.clip.Region;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class GrimReaper {

	private static int[] teleportGrimReaperNextToVictim(Player victim, Player attacker) {
		int[] teleportCoords = new int[2];
		int targetX = 0;
		int targetY = 0;
		teleportCoords[0] = 0;
		teleportCoords[1] = 0;


		targetX = victim.getX();
		targetY = victim.getY() + 1;
		if ((attacker.getX() != targetX || attacker.getY() != targetY) && Region.isStraightPathUnblocked(victim.getX(), victim.getY(), targetX, targetY, victim.getHeight(), 1, 1,
		                                                                                                 false)) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = victim.getX();
		targetY = victim.getY() - 1;
		if ((attacker.getX() != targetX || attacker.getY() != targetY) && Region.isStraightPathUnblocked(victim.getX(), victim.getY(), targetX, targetY, victim.getHeight(), 1, 1,
		                                                                                                 false)) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		// Do not spawn on an object (victim tile to death npc is walkable)
		// do not spawn on attacker
		// spawn next to victim
		targetX = victim.getX() + 1;
		targetY = victim.getY();
		if ((attacker.getX() != targetX || attacker.getY() != targetY) && Region.isStraightPathUnblocked(victim.getX(), victim.getY(), targetX, targetY, victim.getHeight(), 1, 1,
		                                                                                                 false)) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}


		targetX = victim.getX() + 1;
		targetY = victim.getY() + 1;
		if ((attacker.getX() != targetX || attacker.getY() != targetY) && Region.isStraightPathUnblocked(victim.getX(), victim.getY(), targetX, targetY, victim.getHeight(), 1, 1,
		                                                                                                 false)) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = victim.getX() - 1;
		targetY = victim.getY();
		if ((attacker.getX() != targetX || attacker.getY() != targetY) && Region.isStraightPathUnblocked(victim.getX(), victim.getY(), targetX, targetY, victim.getHeight(), 1, 1,
		                                                                                                 false)) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = victim.getX() - 1;
		targetY = victim.getY() - 1;
		if ((attacker.getX() != targetX || attacker.getY() != targetY) && Region.isStraightPathUnblocked(victim.getX(), victim.getY(), targetX, targetY, victim.getHeight(), 1, 1,
		                                                                                                 false)) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = victim.getX() + 1;
		targetY = victim.getY() - 1;
		if ((attacker.getX() != targetX || attacker.getY() != targetY) && Region.isStraightPathUnblocked(victim.getX(), victim.getY(), targetX, targetY, victim.getHeight(), 1, 1,
		                                                                                                 false)) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = victim.getX() - 1;
		targetY = victim.getY() + 1;
		if ((attacker.getX() != targetX || attacker.getY() != targetY) && Region.isStraightPathUnblocked(victim.getX(), victim.getY(), targetX, targetY, victim.getHeight(), 1, 1,
		                                                                                                 false)) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}
		return teleportCoords;
	}

	public static void spawnDeath(Player victim, Player attacker) {
		String[] Phrases =
				{
						"Let me escort you to Edgeville, " + victim.getPlayerName() + "...",
						"" + victim.getPlayerName() + " is mine!",
						"Now is the time you die, " + victim.getPlayerName() + ".",
						"Muahahahahaha!",
						"There is no escape, " + victim.getPlayerName() + "...",
						"Beware Mortals. " + victim.getPlayerName() + " travels with me now.",
						"I claim " + victim.getPlayerName() + " as my own.",
						"Your time here is over, " + victim.getPlayerName() + ".",
						"I have come for you, " + victim.getPlayerName() + "!"
				};

		// Do not spawn on an object
		// do not spawn on attacker
		// spawn next to victim
		int[] coords = teleportGrimReaperNextToVictim(victim, attacker);
		Npc deathNpcInstance = NpcHandler.spawnNpc(null, 5567, coords[0], coords[1], victim.getHeight(), false, false);
		deathNpcInstance.turnNpc(victim.getX(), victim.getY());
		deathNpcInstance.setFrozenLength(50000); // Stop the npc from moving.
		CycleEventHandler.getSingleton().addEvent(victim, new CycleEvent() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer container) {
				i++;
				switch (i) {
					case 1:
						deathNpcInstance.requestAnimation(437); //scythe swing
						deathNpcInstance.forceChat(Phrases[Misc.random(Phrases.length - 1)]);
						break;

					case 5:
						victim.getPA().createPlayersStillGfx(86, deathNpcInstance.getSpawnPositionX(), deathNpcInstance.getSpawnPositionY(), deathNpcInstance.getHeight(), 10);
						break;
					case 6:
						Pet.deletePet(deathNpcInstance);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}
}
