package game.npc.impl.lizard_shaman;

import core.Server;
import core.ServerConstants;
import core.GameType;
import game.content.combat.Combat;
import game.entity.Entity;
import game.entity.MovementState;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.type.GameTypeIdentity;
import utility.Misc;

/**
 * Created by Jason MacKeigan on 2018-01-17 at 11:13 AM
 */
//@CustomNpcComponent(id = 6768, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 6768))
public class LizardShamanSpawn extends Npc {

	private final LizardShamanSpawnCombatStrategy strategy = new LizardShamanSpawnCombatStrategy();

	private Player target;

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {
		super.onSequence();

		if (target != null) {
			Server.npcHandler.followPlayer(npcIndex, target.getPlayerId());
		}
	}

	public LizardShamanSpawn(int npcId, int type) {
		super(npcId, type);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new LizardShamanSpawn(index, npcType);
	}

	/**
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		getEventHandler().addEvent(this, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer container) {
				if (container.getExecutions() == 8) {
					setMovementState(MovementState.DISABLED);
				} else if (container.getExecutions() == 9) {
					requestAnimation(7159);

				} else if (container.getExecutions() == 10) {
					target.getPA().createPlayersStillGfx(1295, getX(), getY(), getHeight(), 28);
					for (Player player : PlayerHandler.getPlayers(p -> p.distanceToPoint(getX(), getY()) <= 2 && p.getHeight() == getHeight())) {
						Combat.createHitsplatOnPlayerNormal(player, Misc.random(5, 11), ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
					}
				} else if (container.getExecutions() == 11) {
					Pet.deletePet(NpcHandler.npcs[npcIndex]);
					container.stop();
				}
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	public void setTarget(Player target) {
		this.target = target;
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return strategy;
	}
}
