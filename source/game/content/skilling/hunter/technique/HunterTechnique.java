package game.content.skilling.hunter.technique;

import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterStyle;
import game.content.skilling.hunter.trap.HunterTrap;
import game.content.skilling.hunter.trap.HunterTrapTechnique;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-01-22 at 9:55 AM
 */
public interface HunterTechnique  {

	HunterTrap createTrap(Player hunter);

	boolean capture(Player hunter, HunterCreature creature, HunterTrapTechnique trap);

	boolean check(Player hunter, HunterTrap trap);

	HunterStyle getStyle();

}
