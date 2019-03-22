package game.content.minigame.impl.fight_pits;

import game.content.minigame.*;
import game.player.Boundary;

import java.util.Set;

/**
 * Created by Jason MacKeigan on 2018-01-30 at 2:28 PM
 */
public class FightPitsCombatArea extends MinigameArea {

	public FightPitsCombatArea(Boundary boundary, Set<MinigamePlayerParticipant> playerParticipants, Set<MinigameNpcParticipant> npcParticipants) {
		super(MinigameAreaKey.FIGHT_PIT_COMBAT_AREA, boundary, MinigameAreaCombatSafety.UNSAFE, MinigameAreaDeathSafety.SAFE, playerParticipants, npcParticipants);
	}



	public FightPitsCombatArea(Boundary boundary) {
		super(MinigameAreaKey.FIGHT_PIT_COMBAT_AREA, MinigameAreaCombatSafety.UNSAFE, MinigameAreaDeathSafety.SAFE, boundary);
	}
}
