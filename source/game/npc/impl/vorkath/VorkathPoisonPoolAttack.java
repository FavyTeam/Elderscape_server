package game.npc.impl.vorkath;

import core.Server;
import core.ServerConstants;
import game.position.Position;
import game.content.combat.Combat;
import game.entity.Entity;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-02-07 at 9:55 PM
 */
public class VorkathPoisonPoolAttack extends CycleEvent<Entity> {

	private final Npc vorkath;

	private final Player target;

	private final List<Object> poisonObjects;

	private State state = State.UN_STARTED;

	private int executionForObjectCreation;

	private enum State {
		UN_STARTED,

		SPAWNING_POISON,

		FINISHED
	}

	public VorkathPoisonPoolAttack(Npc vorkath, Player player, List<Object> poisonObjects) {
		this.vorkath = vorkath;
		target = player;
		this.poisonObjects = poisonObjects;
	}

	/**
	 * Code which should be ran when the event is executed
	 *
	 * @param container
	 */
	@Override
	public void execute(CycleEventContainer<Entity> container) {
		if (target == null || target.dead || vorkath.isDead() || vorkath.distanceTo(target.getX(), target.getY()) > 14) {
			container.stop();
			return;
		}
		if (vorkath.attackTimer <= 4) {
			container.stop();
			return;
		}
		if (state == State.UN_STARTED) {
			poisonObjects.forEach(object -> {
				Server.objectManager.addObject(object);
			});
			state = State.SPAWNING_POISON;
			executionForObjectCreation = container.getExecutions() + 4;
		}
		if (state == State.SPAWNING_POISON) {
			if (container.getExecutions() < executionForObjectCreation) {
				return;
			}
			state = State.FINISHED;
		}
		if (state == State.FINISHED && container.getExecutions() % 2 == 0 && poisonObjects.stream().anyMatch(
				o -> o.objectX == target.getX() && o.objectY == target.getY())) {
			Combat.createHitsplatOnPlayerNormal(target, ThreadLocalRandom.current().nextInt(4, 8),
					ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
		}
		vorkath.facePlayer(target.getPlayerId());
		vorkath.underAttackBy = target.getPlayerId();
		vorkath.lastDamageTaken = System.currentTimeMillis();
		Position damagePosition = new Position(target.getX(), target.getY(), target.getHeight());
		DamageQueue.add(new Damage(target, vorkath, ServerConstants.MAGIC_ICON, 4, 32, -1, p -> damagePosition.matches(target.getX(), target.getY(), target.getHeight())));
		target.getPA().createPlayersProjectile(vorkath, target, 50, 100, 1482, 50, 40, 0, 45, 0);
	}

	/**
	 * Code which should be ran when the event stops
	 */
	@Override
	public void stop() {
		poisonObjects.forEach(object -> Server.objectManager.removeObject(object));
	}
}
