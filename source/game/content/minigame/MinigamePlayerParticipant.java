package game.content.minigame;

import game.player.Player;

import java.util.Objects;

/**
 * Created by Jason MacKeigan on 2018-01-30 at 2:09 PM
 * <p>
 * Represents a player that is a participant in a minigame.
 */
public class MinigamePlayerParticipant extends MinigameParticipant<Player> {

	/**
	 * The unique hash for this participant
	 */
	private final int hashCode;

	public MinigamePlayerParticipant(Player entity) {
		super(entity);
		this.hashCode = Objects.hash(entity.getNameAsLong());
	}

	public boolean equals(Player player) {
		return entity == player;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj instanceof MinigamePlayerParticipant) {
			MinigamePlayerParticipant other = (MinigamePlayerParticipant) obj;

			return getEntity().getNameAsLong() == other.getEntity().getNameAsLong();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
