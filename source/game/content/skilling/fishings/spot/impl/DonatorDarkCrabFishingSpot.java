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
@CustomNpcComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = 3914),})
public class DonatorDarkCrabFishingSpot extends FishingSpot {

	public DonatorDarkCrabFishingSpot(int npcId, int npcType) {
		super(npcId, npcType);
		setSpots(Arrays.asList(new Position(2532, 2711, 0), new Position(2532, 2711, 0)));
	}

	public DonatorDarkCrabFishingSpot copy(int index) {
		return new DonatorDarkCrabFishingSpot(index, super.npcType);
	}
}
