package game.npc.impl.scorpia;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import core.Server;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.object.clip.Region;

/**
 * Created by Jason MacKeigan on 2018-01-05 at 1:03 PM
 */
public class ScorpiaCombatStrategy implements EntityCombatStrategy {

	/**
	 * The boss that operates this strategy.
	 */
	private final Scorpia scorpia;

	/**
	 * The list of guardians for this scorpia boss.
	 */
	private final List<Npc> guardians = new ArrayList<>();

	/**
	 * Determines whether or not the guardians have been spawned, by default unspawned.
	 */
	private ScorpiaSpawnState spawnState = ScorpiaSpawnState.UNSPAWNED;

	/**
	 * Creates a new strategy using the given boss.
	 *
	 * @param scorpia the boss that is relative to this strategy.
	 */
	public ScorpiaCombatStrategy(Scorpia scorpia) {
		this.scorpia = scorpia;
	}

	/**
	 * Referenced when the defender is damaged by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @param damage the damage being dealt.
	 * @param entityAttackType
	 */
	@Override
	public void onDamageTaken(Entity attacker, Entity defender, int damage, int entityAttackType) {
		if (spawnState == ScorpiaSpawnState.UNSPAWNED && scorpia.getCurrentHitPoints() <= scorpia.maximumHitPoints / 2) {

			int[] teleportCoords = new int[2];

			int guardianX = scorpia.getX() + 4;
			int guardianY = scorpia.getY() + 2;
			if (!Region.isStraightPathUnblocked(guardianX, guardianY, scorpia.getX(), scorpia.getY(), scorpia.getHeight(), 1, 1, false)) {
				teleportCoords = NpcHandler.teleportPlayerNextToNpc(0, 0, false, scorpia.getX(), scorpia.getY(), scorpia.getHeight(), 1, 1);
				guardianX = teleportCoords[0];
				guardianY = teleportCoords[1];
			}
			guardians.add(NpcHandler.spawnNpc(6617, guardianX, guardianY, scorpia.getHeight()));

			guardianX = scorpia.getX() + 4;
			guardianY = scorpia.getY() + 1;
			if (!Region.isStraightPathUnblocked(guardianX, guardianY, scorpia.getX(), scorpia.getY(), scorpia.getHeight(), 1, 1, false)) {
				teleportCoords = NpcHandler.teleportPlayerNextToNpc(0, 0, false, scorpia.getX(), scorpia.getY(), scorpia.getHeight(), 1, 1);
				guardianX = teleportCoords[0];
				guardianY = teleportCoords[1];
			}
			guardians.add(NpcHandler.spawnNpc(6617, guardianX, guardianY, scorpia.getHeight()));

			if (guardians.stream().allMatch(Objects::isNull)) {
				spawnState = ScorpiaSpawnState.UNSPAWNED;
				guardians.clear();
			}
			for (Npc npc : guardians) {
				if (npc instanceof ScorpiaGuardian) {
					ScorpiaGuardian guardian = (ScorpiaGuardian) npc;

					guardian.setScorpia(scorpia);

					Server.npcHandler.followNpc(guardian.npcIndex, scorpia.npcIndex);
					npc.faceNpc(scorpia.npcIndex);
				} else {
					spawnState = ScorpiaSpawnState.UNSPAWNED;
					guardians.clear();
					break;
				}
			}
			spawnState = ScorpiaSpawnState.SPAWNED;
		}
	}

	/**
	 * The current toggle of spawning scorpia is in.
	 *
	 * @return the current toggle.
	 */
	public ScorpiaSpawnState getSpawnState() {
		return spawnState;
	}

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		return true;
	}

	/**
	 * The list of guardians that have been spawned, if any.
	 *
	 * @return the list of guardians.
	 */
	List<Npc> getGuardians() {
		return guardians;
	}

	/**
	 * The type of the attacker.
	 *
	 * @return the entity type.
	 */
	@Override
	public EntityType getAttackerType() {
		return EntityType.NPC;
	}

	/**
	 * The type of the defender.
	 *
	 * @return the entity type.
	 */
	@Override
	public EntityType getDefenderType() {
		return EntityType.PLAYER;
	}

}
