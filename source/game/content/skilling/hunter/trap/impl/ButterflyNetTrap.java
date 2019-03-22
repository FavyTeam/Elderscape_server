package game.content.skilling.hunter.trap.impl;

import java.util.stream.Stream;

import game.content.skilling.Skill;
import game.content.skilling.Skilling;
import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterEquipment;
import game.content.skilling.hunter.HunterStyle;
import game.content.skilling.hunter.trap.HunterTrapTechnique;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-05-03 at 10:15 AM
 */
public class ButterflyNetTrap implements HunterTrapTechnique {

	@Override
	public boolean trap(Player hunter, HunterCreature creature) {
		if (creature.getAttributes().getOrDefault(HunterCreature.CAUGHT, false)) {
			return false;
		}
		boolean captured = style().getTechnique().capture(hunter, creature, this);

		if (captured) {
			onCapture(hunter, creature);
		} else {
			onFailCapture(hunter, creature);
		}
		return captured;
	}

	@Override
	public void onCapture(Player hunter, HunterCreature creature) {
		creature.getAttributes().put(HunterCreature.CAUGHT, true);
		Skilling.addSkillExperience(hunter, creature.experienceGained(), Skill.HUNTER.getId(), false);
		Stream.of(creature.captureReward()).forEach(item -> Skilling.addHarvestedResource(hunter, item.getId(), 1));
		creature.killIfAlive();
		ItemAssistant.deleteItemFromInventory(hunter, 11260, 1);
		Stream.of(creature.captureReward()).forEach(item -> ItemAssistant.addItemToInventoryOrDrop(hunter, item.getId(), item.getAmount()));
	}

	@Override
	public void onFailCapture(Player hunter, HunterCreature creature) {

	}

	@Override
	public HunterEquipment equipment() {
		return HunterEquipment.BUTTERFLY_NET;
	}

	@Override
	public HunterStyle style() {
		return HunterStyle.BUTTERFLY_NETTING;
	}

}
