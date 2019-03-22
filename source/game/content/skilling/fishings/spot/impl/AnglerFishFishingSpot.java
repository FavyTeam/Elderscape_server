package game.content.skilling.fishings.spot.impl;

import java.util.Arrays;
import core.GameType;
import game.position.Position;
import game.content.skilling.fishings.spot.FishingSpot;
import game.npc.CustomNpcComponent;
import game.type.GameTypeIdentity;

/**
 * Represents Anglerfish fishing spots
 * 
 * @author 2012
 *
 */
@CustomNpcComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = 3910),})
public class AnglerFishFishingSpot extends FishingSpot {

	public AnglerFishFishingSpot(int npcId, int npcType) {
		super(npcId, npcType);
		setSpots(Arrays.asList(new Position(2527, 2711, 0), new Position(2532, 2711, 0)));
	}
	public AnglerFishFishingSpot copy(int index) {
		return new AnglerFishFishingSpot(index, super.npcType);
	}
}
