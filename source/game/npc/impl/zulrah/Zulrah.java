package game.npc.impl.zulrah;

import game.position.Position;
import game.entity.MovementState;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.TransientAttributeKey;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.npc.impl.zulrah.rotation.impl.FirstRotation;
import game.npc.impl.zulrah.sequence.ZulrahSequence;
import game.player.Player;

import java.util.Set;
import core.GameType;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-04-02 at 5:14 PM
 */
//@CustomNpcComponent(id = 2042, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 2042))
public class Zulrah extends Npc {

	enum SpawnState {
		UN_SPAWNED,

		SPAWNED
	}

	public static final AttributeKey<Integer> PERMANENT_KILLER_ID = new TransientAttributeKey<>(0);

	private final ZulrahCombatStrategy strategy = new ZulrahCombatStrategy(new FirstRotation());

	private SpawnState spawnState = SpawnState.UN_SPAWNED;

	public Zulrah(int npcId, int npcType) {
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
		return new Zulrah(index, npcType);
	}

	/**
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		setMovementState(MovementState.DISABLED);
		setNeverRandomWalks(true);
		setWalkingHomeDisabled(true);
		setVisible(false);
		setRequiresReplacement(true);
	}

	public void spawn(Player attacker) {
		if (spawnState != SpawnState.UN_SPAWNED) {
			return;
		}
		spawnState = SpawnState.SPAWNED;
		getAttributes().put(PERMANENT_KILLER_ID, attacker.getPlayerId());
		strategy.transform(this, attacker);
	}

	@Override
	public Position getDropPosition() {
		ZulrahSequence sequence = strategy.getCurrentSequence();

		if (sequence != null && sequence.getLocation() != null) {
			switch (sequence.getLocation()) {
				case NORTH:
					return new Position(2268, 3070, getHeight());
				case SOUTH:
					return new Position(2268, 3068, getHeight());
				case WEST:
					return new Position(2262, 3073, getHeight());
				case EAST:
					return new Position(2274, 3074, getHeight());
			}
		}
		return new Position(2268, 3070, getHeight());
	}

	/**
	 * A listener function that is referenced when the non-playable character is removed after dying.
	 */
	@Override
	public void afterDeath() {
		super.afterDeath();

		getLocalNpcs().forEach(Npc::killIfAlive);
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {
		super.onSequence();

		if (isDead()) {
			return;
		}
		Set<Player> localPlayers = super.getLocalPlayers();

		if (localPlayers == null || localPlayers.isEmpty()) {
			// should we end if no players are here?
			return;
		}

		if (getKillerId() == 0) {
			setKillerId(getAttributes().getOrDefault(PERMANENT_KILLER_ID, 0));
		}
	}

	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return strategy;
	}
}
