package game.npc.impl;

import core.ServerConstants;
import game.content.combat.Poison;
import game.npc.Npc;
import game.player.Player;
import utility.Misc;

public class VenenatisTest {

	public static void venenatisAttack(Npc npc, Player player) {
		// This needs to be true in order to manipulate attack animation, normal attack hitsplat, attack type and to also set a fixed damage if needed.
		npc.useCleverBossMechanics = true;

		int attackHitsplatDelay = 3;
		int attackType = ServerConstants.MAGIC_ICON;
		int attackAnimation = 5327;


		int random = Misc.random(10);
		if (random <= 5) {
			attackType = ServerConstants.MAGIC_ICON;
			npc.gfx100(164);
			npc.projectileId = 165;
			npc.endGfx = 166;
		} else if (random <= 9) {
			attackType = ServerConstants.MAGIC_ICON;
			npc.gfx100(170);
			npc.projectileId = 171;
			npc.endGfx = 172;
		} else {
			attackType = ServerConstants.MELEE_ICON;
			npc.gfx100(-1);
			npc.projectileId = -1;
			npc.endGfx = -1;
			npc.cleverBossFixedDamage = Misc.random(1, 20); // Fixed damage means it will always hit this number on the next attack even if player is praying.
			player.gfx(254, 70);
			player.doingActionEvent(4);
			Poison.appendPoison(null, player, false, 10);
			player.getPA().sendFilterableMessage("Venenatis hurls her web at you, sticking you to the ground.");
		}
		npc.cleverBossAttackAnimation = attackAnimation;
		npc.cleverBossHitsplatDelay = attackHitsplatDelay;
		npc.cleverBossHitsplatType = attackType;
	}
}
