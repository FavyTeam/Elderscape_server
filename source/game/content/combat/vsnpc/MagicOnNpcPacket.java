package game.content.combat.vsnpc;

import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.movement.Movement;
import network.packet.PacketHandler;

/**
 * Magic on NPC packet action.
 *
 * @author MGT Madness, created on 26-03-2015.
 */
public class MagicOnNpcPacket {
	public static void magicOnNpcPacket(Player player, int castingSpellId, boolean trackPlayer) {
		Npc npc = NpcHandler.npcs[player.getNpcIdAttacking()];
		if (npc == null) {
			return;
		}
		if (!player.playerAssistant.withinDistance(npc)) {
			return;
		}
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "Magic on npc: " + npc.npcType);
		}
		// Dinh's bulwark
		if ((player.getWieldedWeapon() == 21015 || player.getWieldedWeapon() == 16259)) {
			player.getPA().sendMessage("Your bulwark gets in the way.");
			return;
		}
		player.faceUpdate(npc.npcIndex);
		player.setLastCastedMagic(true);
		player.setNpcIdToFollow(player.getNpcIdAttacking());
		if (npc.maximumHitPoints == 0 || npc.npcType == 944) {
			player.playerAssistant.sendMessage("You can't attack this npc. ");
			return;
		}
		for (int i = 0; i < CombatConstants.MAGIC_SPELLS.length; i++) {
			if (castingSpellId == CombatConstants.MAGIC_SPELLS[i][0]) {
				player.setSpellId(i);
				player.setLastCastedMagic(true);
				break;
			}
		}
		if (Combat.spellbookPacketAbuse(player, player.getSpellId())) {
			Combat.resetPlayerAttack(player);
			return;
		}
		player.setAutoCasting(false);
		if (player.hasLastCastedMagic()) {
			if (player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), CombatConstants.MAGIC_FOLLOW_DISTANCE)) {
				Movement.stopMovement(player);
			}
		}
	}

}
