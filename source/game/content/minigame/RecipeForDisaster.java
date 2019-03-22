package game.content.minigame;

import core.GameType;
import game.content.combat.Combat;
import game.content.miscellaneous.Teleport;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class RecipeForDisaster {
	public final static int[] NPC_WAVE_LIST =
			{6369, 6370, 6371, 6372, 6374, 6368};

	public static void isRfdNpc(Player player, Npc npc) {
		if (GameType.isOsrsPvp()) {
			return;
		}
		boolean isNpc = false;
		for (int index = 0; index < NPC_WAVE_LIST.length; index++) {
			if (npc.npcType == NPC_WAVE_LIST[index]) {
				isNpc = true;
			}
		}

		if (!isNpc) {
			return;
		}
		player.rfdWave++;
		if (player.rfdWave == 6) {
			Teleport.startTeleport(player, 3086 + Misc.random(3), 3508 + Misc.random(4), 0, "MODERN");
		}
		if (player.rfdWave > player.highestRfdWave) {
			player.highestRfdWave = player.rfdWave - 1;
		}
	}

	public static void spawnNextWave(final Player player, boolean firstSpawn, int npcType) {
		boolean isRfdNpc = false;
		for (int i = 0; i < NPC_WAVE_LIST.length; i++) {
			if (npcType == NPC_WAVE_LIST[i]) {
				isRfdNpc = true;
			}
		}
		if (!isRfdNpc) {
			return;
		}
		if (player.rfdWave >= NPC_WAVE_LIST.length) {
			player.rfdWave = 0;
			player.playerAssistant.sendMessage("Congratulations, you have completed the Recipe for Disaster minigame!");
			return;
		}
		final int npcId = NPC_WAVE_LIST[player.rfdWave];
		Combat.resetPrayers(player);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				NpcHandler.spawnNpc(player, npcId, 1901, 5353, 24 + (player.getPlayerId() * 4), true, true);
			}
		}, 3);
	}

	public static boolean hasGlovesRequirements(Player player, int itemId) {
		if (GameType.isOsrsPvp()) {
			return true;
		}
		if (player.getShopId() != 13) {
			return true;
		}
		int wave = 5;

		//@formatter:off
		switch (itemId) {

			// Barrows gloves
			case 7462:
				if (player.highestRfdWave >= wave) {
					return true;
				}
				player.playerAssistant
						.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;

			// Dragon gloves
			case 7461:
				wave = 4;
				if (player.highestRfdWave >= wave) {
					return true;
				}
				player.playerAssistant
						.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;

			// Rune gloves
			case 7460:
				wave = 3;
				if (player.highestRfdWave >= wave) {
					return true;
				}
				player.playerAssistant
						.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;

			// Adamant gloves
			case 7459:
				wave = 2;
				if (player.highestRfdWave >= wave) {
					return true;
				}
				player.playerAssistant
						.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;

			// Mithril gloves
			case 7458:
				wave = 1;
				if (player.highestRfdWave >= wave) {
					return true;
				}
				player.playerAssistant
						.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;

			case 7457: // Black gloves
			case 7456: // Steel gloves
			case 7455: // Iron gloves
			case 7454: // Bronze gloves
				wave = 0;
				if (player.highestRfdWave >= wave) {
					return true;
				}
				player.playerAssistant
						.sendMessage(ItemAssistant.getItemName(itemId) + " is unlocked after defeating " + NpcDefinition.getDefinitions()[NPC_WAVE_LIST[wave]].name + ".");
				return false;
		}
		//@formatter:on
		return false;
	}
}
