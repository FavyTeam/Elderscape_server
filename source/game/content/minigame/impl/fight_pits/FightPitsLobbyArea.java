package game.content.minigame.impl.fight_pits;

import game.content.minigame.*;
import game.player.Boundary;

import java.util.Set;

/**
 * Created by Jason MacKeigan on 2018-01-30 at 2:27 PM
 */
public class FightPitsLobbyArea extends MinigameArea {

	public FightPitsLobbyArea(Boundary boundary, Set<MinigamePlayerParticipant> playerParticipants, Set<MinigameNpcParticipant> npcParticipants) {
		super(MinigameAreaKey.FIGHT_PIT_LOBBY, boundary, MinigameAreaCombatSafety.SAFE, MinigameAreaDeathSafety.SAFE, playerParticipants, npcParticipants);
	}

	public FightPitsLobbyArea(Boundary boundary) {
		super(MinigameAreaKey.FIGHT_PIT_LOBBY,
		      MinigameAreaCombatSafety.SAFE, MinigameAreaDeathSafety.SAFE, boundary);
	}
}
