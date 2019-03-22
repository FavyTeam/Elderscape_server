package game.content.skilling.fishings.spot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import game.position.Position;
import game.content.skilling.fishing.Fishing;
import game.npc.Npc;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Represents a fishing spot
 * 
 * @author 2012
 *
 */
public class FishingSpot extends Npc {

	/**
	 * The move time
	 */
	private static final int MOVE_TIME = 100;

	/**
	 * The current position;
	 */
	private Position currentPosition;

	/**
	 * The fishing spots
	 */
	private List<Position> spots;

	/**
	 * Represents a fish spot
	 * 
	 * @param npcId the npc id
	 * @param npcType the npc type
	 */
	public FishingSpot(int npcId, int npcType) {
		super(npcId, npcType);
		/*
		 * The current position
		 */
		this.currentPosition = new Position(getSpawnPositionX(), getSpawnPositionY(), 0);
		/*
		 * The fishing event
		 */
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent<Object>() {
			@Override
			public void execute(CycleEventContainer<Object> container) {
				/*
				 * The positions
				 */
				List<Position> positions = new ArrayList<>(spots);
				/*
				 * Remove current
				 */
				positions.remove(currentPosition);
				/*
				 * Shuffle
				 */
				Collections.shuffle(positions);
				/*
				 * The next position
				 */
				Position next = positions.stream().findAny().orElse(currentPosition);
				/*
				 * Stop all players fishing
				 */
				getLocalPlayers().stream().filter(Objects::nonNull).filter(
						p -> p.getAttributes().getOrDefault(Fishing.FISHING_SPOT) != currentPosition)
						.forEach(t -> t.forceStopSkillingEvent = true);
				/*
				 * Change position
				 */
				move(next);
			}

			@Override
			public void stop() {
			}
		}, MOVE_TIME);
	}

	/**
	 * Gets the spots
	 *
	 * @return the spots
	 */
	public List<Position> getSpots() {
		return spots;
	}

	/**
	 * Sets the spots
	 * 
	 * @param spots the spots
	 */
	public void setSpots(List<Position> spots) {
		this.spots = spots;
	}
}
