package game.content.skilling.hunter.trap;

import core.ServerConstants;
import game.content.skilling.Skill;
import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterEquipment;
import game.content.skilling.hunter.HunterStyle;
import game.player.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-05-03 at 10:17 AM
 */
public interface HunterTrapTechnique {

	boolean trap(Player hunter, HunterCreature creature);

	void onCapture(Player hunter, HunterCreature creature);

	void onFailCapture(Player hunter, HunterCreature creature);

	HunterEquipment equipment();

	HunterStyle style();

	default int calculateMaximumProbability(Player hunter, int levelRequired) {
		//50% change initially
		int probability = 50;

		// reduce probability by increasing by half of the level required
		probability += levelRequired / 2;

		// increase probability by roughly 40% of hunter skill or 50
		probability -= Math.min(hunter.baseSkillLevel[Skill.HUNTER.getId()] / 2, 40);

		if (hunter.playerEquipment[ServerConstants.HEAD_SLOT] == 10045 && hunter.playerEquipment[ServerConstants.BODY_SLOT] == 10043
		    && hunter.playerEquipment[ServerConstants.LEG_SLOT] == 10041) {
			probability -= 10;
		}
		// dont allow probability to be >= 85%
		if (probability < 15) {
			return 15;
		}
		return probability;
	}

	default boolean caught(Player hunter, HunterCreature creature) {
		return ThreadLocalRandom.current().nextInt(0, 100) >
		       ThreadLocalRandom.current().nextInt(0, calculateMaximumProbability(hunter, creature.levelRequired()) + 1);
	}

}
