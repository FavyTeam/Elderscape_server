package game.content.skilling.fishings.spot.impl;

import java.util.Arrays;
import core.GameType;
import game.position.Position;
import game.content.skilling.fishings.spot.FishingSpot;
import game.npc.CustomNpcComponent;
import game.type.GameTypeIdentity;

/**
 * Represents karambwan fishing spots
 * 
 * @author 2012
 *
 */
@CustomNpcComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = 3915),})
public class DonatorKarambwanFishingSpot extends FishingSpot {

	public DonatorKarambwanFishingSpot(int npcId, int npcType) {
		super(npcId, npcType);
		setSpots(Arrays.asList(new Position(2527, 2711, 0), new Position(2535, 2713, 0)));
	}

	public DonatorKarambwanFishingSpot copy(int index) {
		return new DonatorKarambwanFishingSpot(index, super.npcType);
	}
}
