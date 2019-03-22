package game.content.minigame;

import game.npc.Npc;

import java.util.Objects;

/**
 * Created by Jason MacKeigan on 2018-01-30 at 2:12 PM
 * <p>
 * An npc that is a minigame participant.
 */
public class MinigameNpcParticipant extends MinigameParticipant<Npc> {

	private final int hashCode;

	public MinigameNpcParticipant(Npc entity) {
		super(entity);
		this.hashCode = Objects.hash(entity.npcType, entity.npcIndex);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj instanceof MinigameNpcParticipant) {
			MinigameNpcParticipant other = (MinigameNpcParticipant) obj;

			return other.entity.npcType == entity.npcType && other.entity.npcIndex == entity.npcIndex;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
