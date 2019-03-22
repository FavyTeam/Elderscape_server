package game.entity;

import game.position.Position;
import game.entity.attributes.AttributeMap;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.Npc;
import game.object.clip.Region;
import game.player.Player;
import game.player.event.CycleEventHandler;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Jason MacKeigan on 2018-01-05 at 12:36 PM
 * <p>
 * Represents a specific singular type of character within the game.
 */
public abstract class Entity {

	/**
	 * The type of entity that this is.
	 */
	private final EntityType type;

	/**
	 * A mapping of attributes for this entity.
	 */
	private final AttributeMap attributes = new AttributeMap();

	/**
	 * The event handler for managing entity specific events.
	 */
	private final CycleEventHandler<Entity> eventHandler = new CycleEventHandler<>(entity -> entity != null && entity.type != EntityType.PLAYER_BOT);

	/**
	 * The local players around this entity, if any.
	 */
	private final Set<Player> localPlayers = new HashSet<>();

	/**
	 * The local npcs around this entity, if any.
	 */
	private final Set<Npc> localNpcs = new HashSet<>();

	/**
	 * Determines whether or not the entity can walk at all.
	 */
	private MovementState movementState = MovementState.WALKABLE;

	/**
	 * The region this entity is in.
	 */
	private Region region;

	/**
	 * Determines if this entity needs to be replaced on the map, likely after a multiple tile movement.
	 */
	private boolean requiresReplacement;

	/**
	 * A flag to determine if this entity is visible within the world.
	 */
	private boolean visible = true;

	/**
	 * Creates a new entity from the given type.
	 *
	 * @param type the type of entity.
	 */
	protected Entity(EntityType type) {
		this.type = type;
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	public abstract EntityCombatStrategy getCombatStrategyOrNull();

	/**
	 * Moves the entity to a new position.
	 *
	 * @param position the new position.
	 */
	public void move(Position position) {
		requiresReplacement = true;
		onPositionChange();
		//TODO change this entities 'position' to the new position
	}

	/**
	 * Called when the position of the entity has changed.
	 */
	public void onPositionChange() {

	}

	/**
	 * Called when the region of the entity changes
	 */
	public void onRegionChange() {

	}

	/**
	 * Called when the entity is added to the world.
	 */
	public void onAdd() {

	}

	/**
	 * Called when the entity is removed from the world.
	 */
	public void onRemove() {

	}

	/**
	 * Called every 600ms.
	 */
	public void onSequence() {

	}

	/**
	 * Changes the current movement toggle to the next, determining if the entity should
	 * walk or not. You <b>cannot</b> pass {@code null} as a parameter.
	 *
	 * @param movementState the new toggle of movement.
	 */
	public void setMovementState(MovementState movementState) {
		this.movementState = Objects.requireNonNull(movementState);
	}

	/**
	 * The cycle event handler for this npc.
	 *
	 * @return the event handler.
	 */
	public final CycleEventHandler<Entity> getEventHandler() {
		return eventHandler;
	}

	/**
	 * Retrieves the type of entity that this is.
	 *
	 * @return the type of entity.
	 */
	public EntityType getType() {
		return type;
	}

	/**
	 * Retrieves the current toggle of movement, by default this is {@link MovementState#WALKABLE}.
	 * This is never null.
	 *
	 * @return the current toggle of movement.
	 */
	public MovementState getMovementState() {
		return movementState;
	}

	/**
	 * Determines whether or not this entity requires replacement on the map.
	 *
	 * @return by default, false.
	 */
	public boolean isRequiringReplacement() {
		return requiresReplacement;
	}

	/**
	 * Changes whether or not this entity needs to be replaced on the map.
	 *
	 * @param requiresReplacement the new toggle of requiring replacement.
	 */
	public void setRequiresReplacement(boolean requiresReplacement) {
		this.requiresReplacement = requiresReplacement;
	}

	/**
	 * Sets the region that this entity is in.
	 *
	 * @param region the region this entity is in.
	 */
	public void setRegion(Region region) {
		this.region = region;
	}

	/**
	 * The region that this entity is in, or null.
	 *
	 * @return the region.
	 */
	public Region getRegionOrNull() {
		return region;
	}

	/**
	 * A list of local players around this entity.
	 * It does not return this player!
	 *
	 * @return the local players around this entity.
	 */
	public Set<Player> getLocalPlayers() {
		return localPlayers;
	}

	/**
	 * A list of local npcs around this entity.
	 *
	 * @return the local npcs around this entity.
	 */
	public Set<Npc> getLocalNpcs() {
		return localNpcs;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public AttributeMap getAttributes() {
		return attributes;
	}
}
