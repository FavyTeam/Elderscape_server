package game.content.skilling.hunter;

import core.Server;
import game.content.skilling.hunter.trap.HunterTrap;
import game.object.custom.ObjectManagerServer;
import game.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jason MacKeigan on 2018-01-22 at 10:41 AM
 */
public final class HunterTrapObjectManager {

	private static final HunterTrapObjectManager singleton = new HunterTrapObjectManager();

	List<HunterTrap> objects = new ArrayList<>();

	private HunterTrapObjectManager() {

	}

	public void onLogout(Player player) {

	}

	public void add(HunterTrap object) {
		objects.add(object);
	}

	public void remove(HunterTrap object) {
		objects.removeIf(trap -> trap.getOwner() == object.getOwner() && trap.objectId == object.objectId
		                && trap.objectX == object.objectX && trap.objectY == object.objectY);
	}

	public List<HunterTrap> getObjects() {
		return objects;
	}

	public static HunterTrapObjectManager getSingleton() {
		return singleton;
	}
}
