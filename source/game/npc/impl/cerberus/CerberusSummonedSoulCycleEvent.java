package game.npc.impl.cerberus;

import game.entity.Entity;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-02-13 at 11:49 AM
 */
public class CerberusSummonedSoulCycleEvent extends CycleEvent<Entity> {

	private final Npc cerberus;

	private final List<SummonedSoul> summonedSouls = new ArrayList<>();

	private final Player target;

	private State state = State.WALKING;

	private SummonedSoul attacking;

	public CerberusSummonedSoulCycleEvent(Npc cerberus, Player target) {
		this.cerberus = cerberus;
		this.target = target;

		List<SummonedSoulMobType> types = new ArrayList<>(Arrays.asList(SummonedSoulMobType.values()));

		Collections.shuffle(types);

		int x = cerberus.getSpawnPositionX() - 1;

		for (SummonedSoulMobType type : types) {
			Npc npc = NpcHandler.spawnNpc(type.getMobId(), x, cerberus.getSpawnPositionY() + 11, cerberus.getHeight());

			if (!(npc instanceof SummonedSoul)) {
				state = State.COMPLETED;
				throw new ClassCastException("Npc is not an instance of SummonedSoul.");
			}
			summonedSouls.add((SummonedSoul) npc);
			x++;
		}
		attacking = summonedSouls.get(0);
	}

	/**
	 * Retrieves the next summoned soul to attack, or null if there are no more after this one.
	 *
	 * @return the next soul to attack, or null if there is no more to attack.
	 */
	private SummonedSoul nextOrNone() {
		int indexOfCurrent = summonedSouls.indexOf(attacking);

		if (indexOfCurrent == -1) {
			throw new IllegalStateException("Index of current attacking soul is -1, does not exist.");
		}

		if (indexOfCurrent + 1 > summonedSouls.size() - 1) {
			return null;
		}

		return summonedSouls.get(indexOfCurrent + 1);
	}

	/**
	 * Code which should be ran when the event is executed
	 *
	 * @param container
	 */
	@Override
	public void execute(CycleEventContainer<Entity> container) {
		if (container.getExecutions() >= 100 || cerberus == null || target == null || cerberus.isDead() || target.dead) {
			container.stop();
			return;
		}

		if (state == State.WALKING) {
			if (summonedSouls.stream().allMatch(npc -> npc.getY() == (cerberus.getSpawnPositionY() + 5))) {
				attacking.setAttackable(true);
				attacking.setFacingEntityDisabled(false);
				summonedSouls.forEach(soul -> soul.setKillerId(target.getPlayerId()));
				state = State.ATTACKING;
			} else {
				summonedSouls.forEach(npc -> npc.walkTileInDirection(npc.getX(), cerberus.getSpawnPositionY() + 5));
			}
		} else if (state == State.ATTACKING) {
			if (attacking.hasAttacked()) {
				attacking.setKillerId(0);
				attacking.setAttackable(false);
				attacking = nextOrNone();

				if (attacking != null) {
					attacking.setAttackable(true);
				} else {
					state = State.WALKING_BACK;
				}
			}
		} else if (state == State.WALKING_BACK) {
			if (summonedSouls.stream().allMatch(npc -> npc.getY() == npc.getSpawnPositionY())) {
				state = State.COMPLETED;
			} else {
				summonedSouls.forEach(npc -> {
					npc.walkTileInDirection(npc.getX(), npc.getSpawnPositionY() + 11);
					npc.setFacingEntityDisabled(false);
					npc.resetFace();
					npc.turnNpc(npc.getX(), npc.getSpawnPositionY() + 11);
				});
			}
		} else if (state == State.COMPLETED) {
			container.stop();
		}
	}

	/**
	 * Code which should be ran when the event stops
	 */
	@Override
	public void stop() {
		summonedSouls.forEach(npc -> {
			if (!npc.isDead()) {
				npc.setDead(true);
			}
		});
	}

	public State getState() {
		return state;
	}

	public enum State {
		WALKING,

		ATTACKING,

		WALKING_BACK,

		COMPLETED
	}
}
