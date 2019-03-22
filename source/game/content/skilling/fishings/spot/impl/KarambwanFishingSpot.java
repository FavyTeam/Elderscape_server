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
@CustomNpcComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = 3916),})
public class KarambwanFishingSpot extends FishingSpot {

	public KarambwanFishingSpot(int npcId, int npcType) {
		super(npcId, npcType);
		setSpots(Arrays.asList(new Position(2899, 3119, 0), new Position(2896, 3120, 0),
				new Position(2911, 3119, 0), new Position(2912, 3119, 0)));
	}

	public KarambwanFishingSpot copy(int index) {
		return new KarambwanFishingSpot(index, super.npcType);
	}
}
