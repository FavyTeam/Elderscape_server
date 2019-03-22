package game.content.skilling.hunter;

import game.content.skilling.Skill;
import game.content.skilling.hunter.trap.HunterTrap;
import game.entity.MovementState;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.TransientAttributeKey;
import game.item.GameItem;
import game.npc.Npc;
import game.player.Player;
import game.player.PlayerHandler;


/**
 * Created by Jason MacKeigan on 2018-01-22 at 9:52 AM
 */
public abstract class HunterCreature extends Npc {

	public static final AttributeKey<Boolean> CAUGHT = new TransientAttributeKey<>(false);

	public HunterCreature(int npcId, int npcType) {
		super(npcId, npcType);
		setNeverRandomWalks(false);
		setMovementState(MovementState.WALKABLE);
		setWalkingHomeDisabled(false);
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {
		super.onSequence();
	}

	/**
	 * Called when the position of the entity has changed.
	 */
	@Override
	public void onPositionChange() {
		super.onPositionChange();

		lookForTrap();
	}

	public void lookForTrap() {
		if (getAttributes().getOrDefault(CAUGHT, false)) {
			return;
		}
		HunterTrap trapFound = HunterTrapObjectManager.getSingleton().getObjects().stream().filter(
				trap -> !trap.isTriggered() && trap.objectX == getX() && trap.objectY == getY() && trap.height == getHeight()).findAny().orElse(null);

		if (trapFound != null && trapFound.getStyle() == getStyle() && trapFound.getOwner() != null && !trapFound.isTriggered()) {
			Player player = PlayerHandler.getPlayerForName(trapFound.getOwner());

			if (player == null) {
				return;
			}
			if (player.baseSkillLevel[Skill.HUNTER.getId()] < levelRequired()) {
				return;
			}
			trapFound.trap(player, this);
			trapFound.setTriggered(true);
		}
	}

	public abstract int levelRequired();

	public abstract int experienceGained();

	public abstract int objectTransformedOnCapture();

	public abstract GameItem[] captureReward();

	public abstract HunterStyle getStyle();

}
