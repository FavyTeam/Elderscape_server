package game.content.skilling.hunter.creature.boxable;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterStyle;
import game.item.GameItem;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.player.Player;
import game.player.PlayerHandler;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-05-02 at 10:05 AM
 */
//@CustomNpcComponent(id = 2912, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 2912))
public class BlackChinchompaHunterCreature extends HunterCreature {

	public BlackChinchompaHunterCreature(int npcId, int npcType) {
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
		return new BlackChinchompaHunterCreature(index, npcType);
	}

	@Override
	public int levelRequired() {
		return 73;
	}

	@Override
	public int experienceGained() {
		return 315;
	}

	@Override
	public int objectTransformedOnCapture() {
		return 9382;
	}

	@Override
	public GameItem[] captureReward() {
		return new GameItem[] {
				new GameItem(11959, 1)
		};
	}

	@Override
	public void onDeath() {
		super.onDeath();

		if (getAttributes().getOrDefault(HunterCreature.CAUGHT)) {
			return;
		}
		for (Player loop : getLocalPlayers()) {
			if (loop.getPA().withinDistanceOfTargetNpc(this, 2)) {
				Combat.createHitsplatOnPlayerNormal(loop, 2, ServerConstants.NORMAL_HITSPLAT_COLOUR,
						ServerConstants.NO_ICON);
				loop.startAnimation(Combat.getBlockAnimation(loop));
			}
		}
		gfx0(128);
		forceChat("Squeak!");
	}

	@Override
	public HunterStyle getStyle() {
		return HunterStyle.BOX_TRAPPING;
	}
}
