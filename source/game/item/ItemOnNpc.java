package game.item;

import core.GameType;
import core.Plugin;
import core.ServerConstants;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.item.ItemInteractionManager;
import game.content.miscellaneous.Ectofuntus.Bonemeal;
import game.content.miscellaneous.RevenantCavesNpc;
import game.content.skilling.summoning.pet.SummoningPetManager;
import game.content.skilling.summoning.pet.impl.BabyTroll;
import game.content.skilling.thieving.Stalls.StallItems;
import game.item.UntradeableRepairing.Untradeables;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class ItemOnNpc {

	public static void itemOnNpc(Player player, int itemId, int npcId, int slot) {

		// Cleaner at Resource wilderness area.
		Npc npc = NpcHandler.npcs[npcId];
		if (npc == null) {
			return;
		}
		player.setNpcType(npc.npcType);
		player.turnPlayerTo(npc.getX(), npc.getY());

		if (RevenantCavesNpc.isNpc(player, itemId, npc, slot)) {
			return;
		}

		if (ItemInteractionManager.handleItemOnNpc(player, itemId, npc)) {
			return;
		}
		if (BabyTroll.nameTroll(player, itemId, npc)) {
			return;
		}
		if (SummoningPetManager.feed(player, npc, itemId)) {
			return;
		}

		if (itemId == 2518) {
			ItemAssistant.deleteItemFromInventory(player, 2518, 1);
			player.cannotIssueMovement = true;
			player.startAnimation(2779);
			final String[] randomResponses =
					{"Stop that!", "Argh!", "I hate rotten tomatoes!", "What the...?", "Are you serious!", "I'll report you kid.", "Ffs.", "Omg..."};
			final String[] tomatoesanyone =
					{"Take that!", "I hope you like tomatoes!", "Headshot.", "Next time don't afk noob.", "Mmm, they're nice and squishy..."};
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				int i = 0;

				@Override
				public void execute(CycleEventContainer container) {
					i++;
					switch (i) {
						case 1:
							final int offY = (player.getX() - npc.getX()) * -1;
							final int offX = (player.getY() - npc.getY()) * -1;
							player.forcedChat(tomatoesanyone[Misc.random(tomatoesanyone.length - 1)], false, false);
							player.getPA().createPlayersProjectile(player.getX(), player.getY(), offX, offY, 50, 40, 30, 30, 30, 5, 1, 2);
							break;
						case 2:
							npc.forceChat(randomResponses[Misc.random(randomResponses.length - 1)]);
							npc.gfx0(31);
							npc.turnNpc(player.getX(), player.getY());
							break;
						case 3:
							player.cannotIssueMovement = false;
							container.stop();
							break;
					}
				}

				public void stop() {
				}
			}, 1);
		}

		// Cleaner at resource wilderness
		if (npc.npcType == 2901) {
			if (ItemDefinition.getDefinitions()[itemId].note) {
				player.getDH().sendNpcChat("You can only use unnoted items.", DialogueHandler.FacialAnimation.CALM_2.getAnimationId());
				return;
			}
			int amountToNote = ItemAssistant.getItemAmount(player, itemId);
			int notedId = ItemAssistant.getNotedItem(itemId);
			if (ItemDefinition.getDefinitions()[notedId].note) {
				ItemAssistant.deleteItemFromInventory(player, itemId, amountToNote);
				ItemAssistant.addItemToInventory(player, notedId, amountToNote, slot, true);

				int offset = 25;
				if (ItemAssistant.getItemName(itemId).contains("bar")) {
					offset = 14;
				}
				player.getDH().sendItemChat("", "The cleaner has noted for you x" + amountToNote + " " + ItemAssistant.getItemName(itemId) + ".", itemId, 200, offset, 0);
			} else {
				player.getDH().sendNpcChat("This item is not noteable.", DialogueHandler.FacialAnimation.CALM_2.getAnimationId());

			}
			return;
		}

		// Master farmer
		if (npc.npcType == 5832) {
			NpcDefinition farmer = NpcDefinition.DEFINITIONS[5832];
			if (ItemDefinition.getDefinitions()[itemId].note) {
				player.setDialogueChainAndStart(new DialogueChain().npc(farmer, FacialAnimation.CALM_1, "I can only convert unnoted herbs."));
				return;
			}
			int amountToNote = ItemAssistant.getItemAmount(player, itemId);
			int notedId = ItemAssistant.getNotedItem(itemId);
			if (ItemDefinition.getDefinitions()[notedId].note) {
				ItemAssistant.deleteItemFromInventory(player, itemId, amountToNote);
				ItemAssistant.addItemToInventory(player, notedId, amountToNote, slot, true);
				player.setDialogueChainAndStart(new DialogueChain().npc(farmer, FacialAnimation.HAPPY, "There you are, all done!"));
			}
			else {
				player.setDialogueChainAndStart(new DialogueChain().npc(farmer, FacialAnimation.SAD, "Oh dear, it seems that this item is not noteable."));

			}
			return;
		}

		// Lorelai warrior's guild
		if (npc.npcType == 2135) {
			if (itemId == 8850) {
				player.getDH().sendDialogues(591);
			} else {
				player.getDH().sendNpcChat("I'm not interested unless you have a rune defender.", DialogueHandler.FacialAnimation.CALM_2.getAnimationId());
			}
		}

		// Aemad, stall npc
		else if (npc.npcType == 1177) {
			for (final StallItems g : StallItems.values()) {
				if (itemId == g.getItemId()) {
					int amount = ItemAssistant.getItemAmount(player, g.getItemId());
					int totalprice = GameType.isOsrsEco() ? g.getValue() * amount : g.getValue() / 300 * amount;

					if (ItemAssistant.playerHasItem(player, g.getItemId(), amount, slot)) {
						ItemAssistant.deleteItemFromInventory(player, g.getItemId(), amount);
						ItemAssistant.addItemToInventory(player, ServerConstants.getMainCurrencyId(), totalprice, slot, true);
						String s = amount == 1 ? "" : "s";
						player.getDH().sendItemChat("Stall trader", "You sell " + amount + " " + ItemAssistant.getItemName(g.getItemId()) + "" + s + " for " + totalprice + " " + ServerConstants.getMainCurrencyName() + ".", g.getItemId(), 200, 14, 0);
					}
					return;
				}
			}
		}

		// Abbot Langley, npc at chaos altar in 38 wild
		else if (npc.npcType == 2577) {
			for (final Bonemeal b : Bonemeal.values()) {
				if (itemId == b.getBoneId() + 1) {
					int amountToUnNote = ItemAssistant.getItemAmount(player, itemId);
					int freeSpace = ItemAssistant.getFreeInventorySlots(player);
					int unNotedId = ItemAssistant.getUnNotedItem(itemId);
					if (ItemAssistant.hasItemAmountInInventory(player, itemId, amountToUnNote)) {
						int amountToAdd = amountToUnNote;
						if (ItemAssistant.getFreeInventorySlots(player) < amountToUnNote) {
							amountToAdd = freeSpace;
						}

						// So if you unnote 28 bones with 27 inventory space, it unnotes all bones.
						if (freeSpace + 1 == amountToUnNote) {
							amountToAdd = freeSpace + 1;
						}
						ItemAssistant.deleteItemFromInventory(player, itemId, amountToAdd);
						ItemAssistant.addItem(player, unNotedId, amountToAdd);
						player.getDH().sendItemChat("", "Abbot Langley un-notes your " + ItemAssistant.getItemName(unNotedId) + ".", unNotedId, 200, 10, 0);
					}
					return;
				}
			}
			player.getDH().sendNpcChat("I'm afraid I can only swap your noted bones.", DialogueHandler.FacialAnimation.CALM_2.getAnimationId());
			return;
		}

		// Perdu, untradeable repairing
		else if (npc.npcType == 7456) {
			for (final Untradeables u : Untradeables.values()) {
				if (itemId == u.getBrokenId()) {
					UntradeableRepairing.FixItem(player);
					return;
				}
			}
		}
		switch (itemId) {
			default:
				if (Plugin.execute("item_" + itemId + "_on_npc_" + npc.npcType, player)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;
		}
	}

}
