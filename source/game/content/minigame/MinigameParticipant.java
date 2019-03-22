package game.content.minigame;

import game.entity.Entity;


/**
 * Created by Jason MacKeigan on 2018-01-30 at 2:08 PM
 * <p>
 * A unique participant in a minigame that is of type entity.
 */
public abstract class MinigameParticipant<T extends Entity> {

	/**
	 * The entity that is the participant.
	 */
	protected final T entity;

	/**
	 * Creates a new participant for the given entity.
	 *
	 * @param entity the entity participant.
	 */
	public MinigameParticipant(T entity) {
		this.entity = entity;
	}

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();

	public T getEntity() {
		return entity;
	}
}
