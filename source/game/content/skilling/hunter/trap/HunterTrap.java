package game.content.skilling.hunter.trap;

import game.content.skilling.Skill;
import game.content.skilling.Skilling;
import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterEquipment;
import game.content.skilling.hunter.HunterStyle;
import game.content.skilling.hunter.HunterTrapObjectManager;
import game.object.custom.Object;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-01-22 at 10:21 AM
 */
public abstract class HunterTrap extends Object implements HunterTrapTechnique {

	private final String owner;

	private boolean triggered;

	private final HunterStyle style;

	private HunterCreature trapped;

	private boolean itemReturned;

	public HunterTrap(int id, int x, int y, int height, int face, int type, int newId, int ticks, String owner, HunterStyle style) {
		super(id, x, y, height, face, type, newId, ticks, false);
		this.owner = owner;
		this.style = style;
	}

	/**
	 * Referenced when the object is removed from the manager.
	 */
	@Override
	public void onRemove() {
		super.onRemove();

		if (trapped != null) {
			trapped.killIfAlive();
		}
		HunterTrapObjectManager.getSingleton().remove(this);
	}

	public boolean trap(Player hunter, HunterCreature creature) {
		if (tick <= 0 || triggered || trapped != null || hunter == null || hunter.isDisconnected()
		    || creature == null || creature.isDead() || creature.getAttributes().getOrDefault(HunterCreature.CAUGHT, false)) {
			return false;
		}
		boolean captured = style.getTechnique().capture(hunter, creature, this);

		if (captured) {
			this.tick = 180;
			this.trapped = creature;
			this.trapped.setVisible(false);
			onCapture(hunter, creature);
		} else {
			hunter.getPA().sendMessage("Your trap was triggered, but failed to catch anything.");
			onFailCapture(hunter, creature);
		}
		return captured;
	}

	public void check(Player hunter) {
		if (tick <= 0) {
			return;
		}
		if (trapped != null && style.getTechnique().check(hunter, this)) {
			Skilling.addSkillExperience(hunter, trapped.experienceGained(), Skill.HUNTER.getId(), false);
		}
	}

	public void setItemReturned(boolean itemReturned) {
		this.itemReturned = itemReturned;
	}

	public boolean isItemReturned() {
		return itemReturned;
	}

	public HunterCreature getTrapped() {
		return trapped;
	}

	public HunterStyle getStyle() {
		return style;
	}

	public abstract HunterEquipment equipment();

	public boolean isTriggered() {
		return triggered;
	}

	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}

	public String getOwner() {
		return owner;
	}
}
