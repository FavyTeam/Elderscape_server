package game.content.skilling.hunter.creature.boxable;

import core.GameType;
import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterStyle;
import game.item.GameItem;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-05-02 at 10:05 AM
 */
//@CustomNpcComponent(id = 2910, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 2910))
public class ChinchompaHunterCreature extends HunterCreature {

	public ChinchompaHunterCreature(int npcId, int npcType) {
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
		return new ChinchompaHunterCreature(index, npcType);
	}

	@Override
	public int levelRequired() {
		return 53;
	}

	@Override
	public int experienceGained() {
		return 198;
	}

	@Override
	public int objectTransformedOnCapture() {
		return 9382;
	}

	@Override
	public GameItem[] captureReward() {
		return new GameItem[] {
				new GameItem(10033, 1)
		};
	}

	@Override
	public HunterStyle getStyle() {
		return HunterStyle.BOX_TRAPPING;
	}
}
