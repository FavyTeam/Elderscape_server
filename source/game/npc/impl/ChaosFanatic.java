package game.npc.impl;

import core.ServerConstants;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class ChaosFanatic {

	public static int x = 0, x1, x2;

	public static int y = 0, y1, y2;

	public static void attackPlayer(Player player, Npc npc) {
		// This needs to be true in order to manipulate attack animation, normal attack hitsplat, attack type and to also set a fixed damage if needed.
		npc.useCleverBossMechanics = true;

		int attackHitsplatDelay = 3;
		int attackType = ServerConstants.MAGIC_ICON;
		int attackAnimation = 811;

		int random = Misc.random(1, 100);
		int nX = npc.getX();
		int nY = npc.getY();
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;

		String[] messages =
				{"Burn!", "WEUGH!", "Develish Oxen Roll!", "All your wilderness are belong to them!", "AhehHeheuhHhahueHuUEehEahAH",
						"I shall call him squidgy and he shall be my squidgy!"
				};

		npc.forceChat(messages[(int) (Math.random() * messages.length)]);
		// Random disarms players and gfx 557 is sent under the player?

		//Disarm player
		if (Misc.hasPercentageChance(7)) {
			int itemsToRemove = Misc.random(1, 3);
			boolean removed = false;
			for (int index = 0; index < itemsToRemove - 1; index++) {
				for (int i = 0; i < player.playerEquipment.length; i++) {
					if (player.playerEquipment[i] <= 0) {
						continue;
					}
					if (ItemAssistant.getFreeInventorySlots(player) > 0) {
						ItemAssistant.removeItem(player, player.playerEquipment[i], i, false);
						removed = true;
						break;
					}
				}
			}
			if (removed) {
				player.getPA().sendMessage("The Chaos Fanatic has removed some of your worn equipment.");
			}
		}

		if (random <= 70) {
			int projectileId = Misc.hasPercentageChance(70) ? 554 : 551;
			player.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, 65, projectileId, 43, 31, -player.getPlayerId() - 1, 45, 0);
		} else {
			x = player.getX();
			y = player.getY();
			x1 = player.getX() + 1 + Misc.random(2);
			y1 = player.getY() - 1 + Misc.random(2);
			x2 = player.getX() - 1 - Misc.random(2);
			y2 = player.getY() - 1 + Misc.random(2);
			int offY1 = (npc.getX() - x) * -1;
			int offX1 = (npc.getY() - y) * -1;
			int offY2 = (npc.getX() - x1) * -1;
			int offX2 = (npc.getY() - y1) * -1;
			int offY3 = (npc.getX() - x2) * -1;
			int offX3 = (npc.getY() - y2) * -1;
			player.getPA().createPlayersProjectile(nX, nY, offX1, offY1, 50, 95, 551, 43, 0, 0, 45, 30);
			player.getPA().createPlayersProjectile(nX, nY, offX2, offY2, 50, 95, 551, 43, 0, 0, 45, 30);
			player.getPA().createPlayersProjectile(nX, nY, offX3, offY3, 50, 95, 551, 43, 0, 0, 45, 30);
			player.getPA().createPlayersStillGfx(157, x, y, 0, 75);
			player.getPA().createPlayersStillGfx(157, x1, y1, 0, 75);
			player.getPA().createPlayersStillGfx(157, x2, y2, 0, 75);
			npc.cleverBossStopAttack = true;

			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					container.stop();
				}

				@Override
				public void stop() {
					for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
						Player loop = PlayerHandler.players[index];
						if (loop == null) {
							continue;
						}
						if (loop.getX() == x && loop.getY() == y || loop.getX() == x1 && loop.getY() == y1 || loop.getX() == x2 && loop.getY() == y2) {
							if (npc != null) {
								npc.getDamage().add(new Damage(player, npc, ServerConstants.MAGIC_ICON, 1, -1, Misc.random(15, 31)));
							}
						}
					}
				}
			}, 3);
		}


		npc.cleverBossAttackAnimation = attackAnimation;
		npc.cleverBossHitsplatDelay = attackHitsplatDelay;
		npc.cleverBossHitsplatType = attackType;
	}

}
