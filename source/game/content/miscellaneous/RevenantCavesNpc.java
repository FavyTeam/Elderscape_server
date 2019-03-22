package game.content.miscellaneous;

import core.GameType;
import core.ServerConstants;
import game.content.dialogueold.DialogueHandler;
import game.content.starter.GameMode;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

public class RevenantCavesNpc {

	private final static int getAnnounceClaimAmount() {
		return GameType.isOsrsEco() ? 2_000_000 : 20_000;
	}

	private final static int PLAYER_FROZEN_AMOUNT_SECONDS = 60;

	private final static int PLAYER_CAN_BE_ATTACKED_X_SECONDS_BEFORE_UNFROZEN = 10;

	public static enum StatuetteData {

		EMBLEM(21807, 5_000_000),
		TOTEM(21810, 10_000_000),
		STATUETTE(21813, 20_000_000),
		MEDALLION(22299, 40_000_000),
		EFFIGY(22302, 80_000_000),
		RELIC(22305, 160_000_000);

		public int itemId;

		public int value;

		StatuetteData(final int itemId, final int value) {
			this.itemId = itemId;
			this.value = value;
		}

		public int getItemId() {
			return itemId;
		}

		public int getValue() {
			return value;
		}
	}

	public static int emblemTraderNpcIndex;

	public static void storeEmblemTraderNpcIndex() {
		Npc emblemTrader = NpcHandler.getNpcByNpcId(7941);
		if (emblemTrader != null) {
			emblemTraderNpcIndex = emblemTrader.npcIndex;
		}
	}

	private static void freezePlayerAndPreventLoggingOut(Player player) {
		player.setTimePlayerCanBeAttacked(System.currentTimeMillis() + Misc.getSecondsToMilliseconds(PLAYER_FROZEN_AMOUNT_SECONDS - 10));
		player.setTimeCanDisconnectAtBecauseOfCombat(System.currentTimeMillis() + Misc.getSecondsToMilliseconds(PLAYER_FROZEN_AMOUNT_SECONDS));
		player.timeCanTradeAndDrop = System.currentTimeMillis() + Misc.getSecondsToMilliseconds(PLAYER_FROZEN_AMOUNT_SECONDS);
		// Make the npc freeze the player with barrage spell animation and player gfx barrage too.
		// Make the npc say Mwahaha! Reinforcements are coming your way!
		Npc npc = NpcHandler.getNpcByNpcIndex(emblemTraderNpcIndex);
		npc.setNeverRandomWalks(true);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent<Object>() {
			@Override
			public void execute(CycleEventContainer container) {
				switch (container.getExecutions()) {
					case 0:
						npc.requestAnimation(1979);
						npc.facePlayer(player.getPlayerId());
						npc.forceChat("Ultimate barrage!");
						break;

					case 1:

						player.playerAssistant.sendFilterableMessage(ServerConstants.RED_COL + "You have been Frozen!");
						player.setFrozenLength(PLAYER_FROZEN_AMOUNT_SECONDS * 1000);
						OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ICE_BARRAGE, PLAYER_FROZEN_AMOUNT_SECONDS);
						Movement.stopMovement(player);
						player.gfx0(369);
						player.frozenBy = -1;
						DamageQueue.add(new Damage(player, npc, ServerConstants.MAGIC_ICON, 1, 45, Misc.random(30, 45)));
						break;

					case 4:
						npc.forceChat("Mwahaha! Reinforcements are coming your way!");
						break;

					case 7:
						npc.setNeverRandomWalks(false);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {}
		}, 1);
	}

	public static boolean isNpc(Player player, int itemId, Npc npc, int slot) {
		if (GameType.isPreEoc()) {
			return false;
		}
		if (npc.npcType == 7941) { // Emblem trader at revs 
			if (player.getCombatLevel() != 126) {
				player.getPA().sendMessage("You need to be 126 combat to trade in the statues!");
				return true;
			}
			if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < player.getBaseHitPointsLevel()) {
				player.getPA().sendMessage("You need to be at least 99 hitpoints to exchange.");
				return true;
			}
			player.resetDamageTaken();
			for (final StatuetteData data : StatuetteData.values()) {
				if (itemId == data.getItemId()) {
					int amount = ItemAssistant.getItemAmount(player, data.getItemId());
					int totalprice = GameType.isOsrsEco() ? data.getValue() * amount : BloodMoneyPrice.getBloodMoneyPrice(data.getItemId()) * amount;

					if (ItemAssistant.playerHasItem(player, data.getItemId(), amount, slot)) {
						ItemAssistant.deleteItemFromInventory(player, data.getItemId(), amount);
						ItemAssistant.addItemToInventory(player, ServerConstants.getMainCurrencyId(), totalprice, slot, true);
						String s = amount == 1 ? "" : "s";
						player.getDH().sendItemChat("Statuette Reward", "", "You exchange " + amount + " " + ItemAssistant.getItemName(data.getItemId()) + "" + s + " for " + Misc.formatRunescapeStyle(totalprice) + " " + ServerConstants.getMainCurrencyName() + ".", "", data.getItemId(), 200, 35, 0);
						if (totalprice >= getAnnounceClaimAmount()) {
							Announcement.announce(GameMode.getGameModeName(player) + " has just traded in emblems worth " + Misc.formatRunescapeStyle(totalprice) + " " + ServerConstants.getMainCurrencyName() + " in the", ServerConstants.DARK_BLUE);
							Announcement.announce(ServerConstants.RED_COL + "Revenant cave, head east, south, then west at level 35 wild!");
							freezePlayerAndPreventLoggingOut(player);
						}
					} else {
						player.getDH().sendNpcChat("This item is not noteable.", DialogueHandler.FacialAnimation.CALM_2.getAnimationId());

					}
					return true;
				}
			}
			return true;
		}
		return false;
	}

}
