package game.content.skilling.hunter.technique.impl.bird_snaring;

import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterStyle;
import game.content.skilling.hunter.technique.HunterTechnique;
import game.content.skilling.hunter.trap.HunterTrap;
import game.content.skilling.hunter.trap.HunterTrapTechnique;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-05-03 at 9:37 AM
 */
public class ImplingCatchingTechnique implements HunterTechnique {

	@Override
	public HunterTrap createTrap(Player hunter) {
		throw new UnsupportedOperationException("This is not supported for catching implings.");
	}

	@Override
	public boolean capture(Player hunter, HunterCreature creature, HunterTrapTechnique trap) {
		return trap.caught(hunter, creature);
	}

	@Override
	public boolean check(Player hunter, HunterTrap trap) {
		throw new UnsupportedOperationException("This is not supported for catching implings.");
	}

	@Override
	public HunterStyle getStyle() {
		return HunterStyle.BUTTERFLY_NETTING;
	}
}
