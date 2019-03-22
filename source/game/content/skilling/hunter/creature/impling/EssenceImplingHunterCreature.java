package game.content.skilling.hunter.creature.impling;

import core.GameType;
import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterStyle;
import game.item.GameItem;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-05-03 at 9:43 AM
 */
//@CustomNpcComponent(id = 1649, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 1649))
public class EssenceImplingHunterCreature extends HunterCreature {

	public EssenceImplingHunterCreature(int npcId, int npcType) {
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
		return new EssenceImplingHunterCreature(index, npcType);
	}

	@Override
	public void lookForTrap() {
		// does not look for trap
	}

	@Override
	public int levelRequired() {
		return 42;
	}

	@Override
	public int experienceGained() {
		return 29;
	}

	@Override
	public int objectTransformedOnCapture() {
		throw new UnsupportedOperationException("This is not needed for this type of hunter creature.");
	}

	@Override
	public GameItem[] captureReward() {
		return new GameItem[] { new GameItem(11246) };
	}

	@Override
	public HunterStyle getStyle() {
		return HunterStyle.BUTTERFLY_NETTING;
	}

}
