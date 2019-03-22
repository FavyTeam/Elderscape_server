package game.content.skilling.hunter.creature.bird;

import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterStyle;
import game.item.GameItem;
import game.npc.CustomNpcComponent;
import game.npc.Npc;

import java.util.concurrent.ThreadLocalRandom;
import core.GameType;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-04-30 at 12:51 PM
 */
//@CustomNpcComponent(id = 5548, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 5548))
public class TropicalWagtailHunterCreature extends HunterCreature {

	public TropicalWagtailHunterCreature(int npcId, int npcType) {
		super(npcId, npcType);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new TropicalWagtailHunterCreature(index, npcType);
	}

	@Override
	public int levelRequired() {
		return 19;
	}

	@Override
	public int experienceGained() {
		return 95;
	}

	@Override
	public int objectTransformedOnCapture() {
		return 9348;
	}

	@Override
	public GameItem[] captureReward() {
		return new GameItem[] { new GameItem(526, 1), new GameItem(10087, 5 + ThreadLocalRandom.current().nextInt(0, 6)), new GameItem(9978, 1) };
	}

	@Override
	public HunterStyle getStyle() {
		return HunterStyle.BIRD_SNARING;
	}
}
