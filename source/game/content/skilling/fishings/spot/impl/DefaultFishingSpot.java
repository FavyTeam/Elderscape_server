package game.content.skilling.fishings.spot.impl;

import java.util.Arrays;
import core.GameType;
import game.position.Position;
import game.content.skilling.fishings.spot.FishingSpot;
import game.npc.CustomNpcComponent;
import game.type.GameTypeIdentity;

/**
 * Represents the default fishing spots
 * 
 * @author 2012
 *
 */
@CustomNpcComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = 3913),})
public class DefaultFishingSpot extends FishingSpot {

	public DefaultFishingSpot(int npcId, int npcType) {
		super(npcId, npcType);
		setSpots(Arrays.asList(new Position(2875, 3342, 0), new Position(2876, 3342, 0),
				new Position(2877, 3342, 0), new Position(2879, 3339, 0), new Position(2879, 3338, 0),
				new Position(2879, 3335, 0), new Position(2879, 3334, 0), new Position(2877, 3331, 0),
				new Position(2876, 3331, 0), new Position(2875, 3331, 0)
		));
	}

	public DefaultFishingSpot copy(int index) {
		return new DefaultFishingSpot(index, super.npcType);
	}
}
