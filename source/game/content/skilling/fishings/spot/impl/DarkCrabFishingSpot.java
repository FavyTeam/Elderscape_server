package game.content.skilling.fishings.spot.impl;

import java.util.Arrays;
import core.GameType;
import game.position.Position;
import game.content.skilling.fishings.spot.FishingSpot;
import game.npc.CustomNpcComponent;
import game.type.GameTypeIdentity;

/**
 * Represents the dark crab fishing spot
 * 
 * @author 2012
 *
 */
@CustomNpcComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = 3911),})
public class DarkCrabFishingSpot extends FishingSpot {

	public DarkCrabFishingSpot(int npcId, int npcType) {
		super(npcId, npcType);
		setSpots(Arrays.asList(new Position(3050, 3704, 0), new Position(3052, 3705, 0),
				new Position(3044, 3700, 0), new Position(3047, 3699, 0), new Position(3052, 3697, 0)));
	}

	public DarkCrabFishingSpot copy(int index) {
		return new DarkCrabFishingSpot(index, super.npcType);
	}
}
