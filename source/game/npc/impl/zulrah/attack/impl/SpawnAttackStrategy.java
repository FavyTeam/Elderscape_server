package game.npc.impl.zulrah.attack.impl;

import game.position.Position;
import game.entity.Entity;
import game.entity.EntityType;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.impl.zulrah.ZulrahDangerousLocation;
import game.npc.impl.zulrah.attack.ZulrahAttackStrategy;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-10 at 12:26 PM
 */
public class SpawnAttackStrategy implements ZulrahAttackStrategy {

	private final List<ZulrahDangerousLocation> venomousLocations;

	private ZulrahDangerousLocation currentVenomousLocation;

	public SpawnAttackStrategy(List<ZulrahDangerousLocation> venomousLocations) {
		this.venomousLocations = new ArrayList<>(venomousLocations);
	}

	@Override
	public void onStart(Npc zulrah) {
		zulrah.resetFace();
		zulrah.setFacingEntityDisabled(true);
	}

	@Override
	public void onEnd(Npc zulrah) {
		zulrah.resetFace();
		zulrah.setFacingEntityDisabled(false);
	}

	/**
	 * References when {@link #isCustomAttack()} returns true, in which case we're responsible the
	 * regular combat process.
	 *
	 * @param attacker the attacker.
	 * @param defender
	 */
	@Override
	public void onCustomAttack(Entity attacker, Entity defender) {
		if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			currentVenomousLocation = venomousLocations.stream().findFirst().orElse(null);

			if (currentVenomousLocation == null) {
				return;
			}
			venomousLocations.remove(currentVenomousLocation);

			attackerAsNpc.turnNpc(currentVenomousLocation.getPositions()[0].getX(),
			                      currentVenomousLocation.getPositions()[0].getY());

			for (Position location : currentVenomousLocation.getPositions()) {
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, location, 50, 125, 1047, 43, 31, 0, 65, 0);
			}
			attackerAsNpc.getEventHandler().addEvent(attackerAsNpc, new CycleEvent<Entity>() {
				@Override
				public void execute(CycleEventContainer<Entity> container) {
					container.stop();

					if (currentVenomousLocation == null) {
						return;
					}
					for (Position position : currentVenomousLocation.getPositions()) {
						NpcHandler.spawnNpc(defenderAsPlayer, 2045, position.getX(), position.getY(),
						                    defenderAsPlayer.getHeight(), true, false);
					}
				}

				@Override
				public void stop() {

				}
			}, 2);
		}
	}

	/**
	 * Retrieves the custom emote for the attacking entity, or -1 if there is no custom emote.
	 *
	 * @param attacker the entity making the attack animation.
	 * @return the attack emote, or -1 by default for no custom attack emote.
	 */
	@Override
	public int getCustomAttackAnimation(Entity attacker) {
		return 5069;
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
	 * Determines if the defender can be attacked by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @return {@code true} if the defender can be attacked, and {@code true} by default.
	 */
	@Override
	public boolean canBeAttacked(Entity attacker, Entity defender) {
		return true;
	}

	/**
	 * Determines if we're going to handle the entire attack process our self.
	 *
	 * @return {@code true} if it's a custom attack, by default, false.
	 */
	@Override
	public boolean isCustomAttack() {
		return true;
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

	@Override
	public boolean isComplete() {
		return venomousLocations.isEmpty() && currentVenomousLocation == null;
	}
}
