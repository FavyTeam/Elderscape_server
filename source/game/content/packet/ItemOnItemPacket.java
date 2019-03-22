package game.content.packet;

import core.GameType;
import core.Plugin;
import game.content.godbook.PageCombining;
import game.content.item.ItemInteractionManager;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.BraceletOfEthereum;
import game.content.miscellaneous.CombineGodsword;
import game.content.miscellaneous.CrystalCombining;
import game.content.miscellaneous.ItemCombining;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.MaxCape;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.SpiritShieldCrafting;
import game.content.skilling.Firemaking;
import game.content.skilling.crafting.BattlestaffMaking;
import game.content.skilling.crafting.GemCrafting;
import game.content.skilling.crafting.GlassBlowing;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.crafting.LeatherCrafting;
import game.content.skilling.fletching.BowStringFletching;
import game.content.skilling.fletching.DartMaking;
import game.content.skilling.fletching.Fletching;
import game.content.skilling.herblore.Herblore;
import game.content.skilling.herblore.SecondaryGrinding;
import game.content.skilling.smithing.SmithingOtherItem;
import game.item.ItemAssistant;
import game.item.PotionCombining;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

public class ItemOnItemPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {

		int itemUsedWithSlot = player.getInStream().readUnsignedWord();
		int itemUsedSlot = player.getInStream().readUnsignedWordA();
		if (ItemAssistant.isNulledSlot(itemUsedWithSlot)) {
			return;
		}
		if (ItemAssistant.isNulledSlot(itemUsedSlot)) {
			return;
		}
		int itemUsedWithId = player.playerItems[itemUsedWithSlot] - 1;
		int itemUsedId = player.playerItems[itemUsedSlot] - 1;

		if (player.doingAnAction()) {
			return;
		}
		if (player.dragonSpearEvent) {
			return;
		}
		if (player.cannotIssueMovement) {
			return;
		}
		if (ItemAssistant.isNulledSlot(itemUsedWithSlot)) {
			return;
		}
		if (ItemAssistant.isNulledSlot(itemUsedSlot)) {
			return;
		}
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "usedWithSlot: " + itemUsedWithSlot);
			PacketHandler.saveData(player.getPlayerName(), "itemUsedSlot: " + itemUsedSlot);
			PacketHandler.saveData(player.getPlayerName(), "useWith: " + itemUsedWithId);
			PacketHandler.saveData(player.getPlayerName(), "itemUsed: " + itemUsedId);
		}


		if (ItemAssistant.nulledItem(itemUsedWithId)) {
			return;
		}

		if (ItemAssistant.nulledItem(itemUsedId)) {
			return;
		}

		if (!ItemAssistant.playerHasItem(player, itemUsedWithId, 1, itemUsedWithSlot) || !ItemAssistant.playerHasItem(player, itemUsedId, 1, itemUsedSlot)) {
			return;
		}


		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		player.playerAssistant.stopAllActions();
		player.lastItemUsedId = itemUsedId;
		player.lastItemUsedWithId = itemUsedWithId;

		if (ItemInteractionManager.handleItemOnItem(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (Fletching.isBoltFletchingRelated(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (GemCrafting.useGemOnChisel(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (itemUsedId == 314 || itemUsedWithId == 314) {
			DartMaking.createDarts(player, itemUsedId, itemUsedWithId);
		}

		if (itemUsedId == 1785 && itemUsedWithId == 1775 || itemUsedId == 1775 && itemUsedWithId == 1785) {
			GlassBlowing.viewInterface(player);
			return;
		}

		if (itemUsedId == 21816 && itemUsedWithId == 21820 || itemUsedId == 21820 && itemUsedWithId == 21816) {
			player.getPA().sendMessage("You'll need to uncharge your bracelet before adding more ether.");
			return;
		}

		if (itemUsedId == 985 && itemUsedWithId == 987 || itemUsedId == 987 && itemUsedWithId == 985) {
			PlayerMiscContent.CombineKeyHalfs(player);
			return;
		}

		if (LeatherCrafting.useNeedleOnLeather(player, itemUsedId, itemUsedWithId)) {
			return;
		}

		if (RunePouch.useWithRunePouch(player, itemUsedId, itemUsedWithId, itemUsedSlot, itemUsedWithSlot)) {
			return;
		}

		if (Blowpipe.useWithBlowpipe(player, itemUsedId, itemUsedWithId, itemUsedSlot, itemUsedWithSlot)) {
			return;
		}

		if (BraceletOfEthereum.useWithBracelet(player, itemUsedId, itemUsedWithId, itemUsedSlot, itemUsedWithSlot)) {
			return;
		}

//		if (WildernessItems.charge(player, itemUsedId, itemUsedWithId, itemUsedSlot, itemUsedWithSlot)) {
//			return;
//		}

		if (Blowpipe.chargeBlowpipe(player, itemUsedId, itemUsedWithId, itemUsedSlot, itemUsedWithSlot)) {
			return;
		}

		if (LootingBag.useWithLootingBag(player, itemUsedId, itemUsedWithId, itemUsedSlot, itemUsedWithSlot)) {
			return;
		}

		if (Fletching.isArrowCombining(player, itemUsedId, itemUsedWithId)) {
			return;
		}

		if (BattlestaffMaking.isUsingItemOnBattlestaff(player, itemUsedId, itemUsedWithId)) {
			return;
		}

		if (SecondaryGrinding.UsingItemOnPestle(player, itemUsedId, itemUsedWithId)) {
			return;
		}

		if (BowStringFletching.useBowStringOnLeather(player, itemUsedId, itemUsedWithId)) {
			return;
		}

		if (ItemCombining.isCombinable(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 12757, 11235, 12766, "You use the Blue paint on the Dark bow.", true, 25, 0)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 12759, 11235, 12765, "You use the Green paint on the Dark bow.", true, 25, 0)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 12761, 11235, 12767, "You use the Yellow paint on the Dark bow.", true, 25, 0)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 12763, 11235, 12768, "You use the White paint on the Dark bow.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 2299, 2299, 2297, "You combine the anchovy pizza halves to create a full pizza.", false, 0, 0)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 2295, 2295, 2293, "You combine the meat pizza halves to create a full pizza.", false, 0, 0)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 985, 987, 989, "You combine the Tooth half of a key and Loop half of a key.", false, 0, 0)) {
			return;
		}

		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 9431, 9452, 9465, "You combine the Runite limb with the Yew stock.", false, 0, 0)) {
			return;
		}

		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 9465, 9438, 9185, "You combine the Runite c'bow (u) with the crossbow string.", false, 0, 0)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 21295, 21285, 21282)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 6570, 13329, 13330)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 2412, 13331, 13332)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 10499, 13337, 13338)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 2413, 13335, 13336)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 2414, 13333, 13334)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 13124, 20760, 20764)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 22109, 21898, 21900)) {
			return;
		}

		//new max capes which use inbued god capes
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 21791, 21776, 21778)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 21795, 21780, 21782)) {
			return;
		}
		if (MaxCape.combineMaxCape(player, itemUsedId, itemUsedWithId, 21793, 21784, 21786)) {
			return;
		}

		//graceful colourings
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11850, 16067, 13579, "You use the dye on the Graceful hood.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11850, 16064, 13591, "You use the dye on the Graceful hood.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11850, 16066, 13603, "You use the dye on the Graceful hood.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11850, 16068, 13615, "You use the dye on the Graceful hood.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11850, 16070, 13627, "You use the dye on the Graceful hood.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11850, 16072, 13667, "You use the dye on the Graceful hood.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11850, 16074, 21061, "You use the dye on the Graceful hood.", true, 0, -15)) {
			return;
		}



		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11852, 16067, 13581, "You use the dye on the Graceful cape.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11852, 16064, 13593, "You use the dye on the Graceful cape.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11852, 16066, 13605, "You use the dye on the Graceful cape.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11852, 16068, 13617, "You use the dye on the Graceful cape.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11852, 16070, 13629, "You use the dye on the Graceful cape.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11852, 16072, 13669, "You use the dye on the Graceful cape.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11852, 16074, 21064, "You use the dye on the Graceful cape.", true, 0, -15)) {
			return;
		}



		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11854, 16067, 13583, "You use the dye on the Graceful top.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11854, 16064, 13595, "You use the dye on the Graceful top.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11854, 16066, 13607, "You use the dye on the Graceful top.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11854, 16068, 13619, "You use the dye on the Graceful top.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11854, 16070, 13631, "You use the dye on the Graceful top.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11854, 16072, 13671, "You use the dye on the Graceful top.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11854, 16074, 21067, "You use the dye on the Graceful top.", true, 0, -15)) {
			return;
		}



		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11856, 16067, 13585, "You use the dye on the Graceful legs.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11856, 16064, 13597, "You use the dye on the Graceful legs.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11856, 16066, 13609, "You use the dye on the Graceful legs.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11856, 16068, 13621, "You use the dye on the Graceful legs.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11856, 16070, 13633, "You use the dye on the Graceful legs.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11856, 16072, 13673, "You use the dye on the Graceful legs.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11856, 16074, 21070, "You use the dye on the Graceful legs.", true, 0, -15)) {
			return;
		}



		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11858, 16067, 13587, "You use the dye on the Graceful gloves.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11858, 16064, 13599, "You use the dye on the Graceful gloves.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11858, 16066, 13611, "You use the dye on the Graceful gloves.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11858, 16068, 13623, "You use the dye on the Graceful gloves.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11858, 16070, 13635, "You use the dye on the Graceful gloves.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11858, 16072, 13675, "You use the dye on the Graceful gloves.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11858, 16074, 21073, "You use the dye on the Graceful gloves.", true, 0, -15)) {
			return;
		}



		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11860, 16067, 13589, "You use the dye on the Graceful boots.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11860, 16064, 13601, "You use the dye on the Graceful boots.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11860, 16066, 13613, "You use the dye on the Graceful boots.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11860, 16068, 13625, "You use the dye on the Graceful boots.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11860, 16070, 13637, "You use the dye on the Graceful boots.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11860, 16072, 13677, "You use the dye on the Graceful boots.", true, 0, -15)) {
			return;
		}
		if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 11860, 16074, 21076, "You use the dye on the Graceful boots.", true, 0, -15)) {
			return;
		}
		if (GameType.isOsrsEco()) {
			if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 5940, 1215, 5698, "The dragon dagger has been poisoned.", true, 20, -10)) {
				return;
			}
		}



		if (PageCombining.isPage(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (Firemaking.colourLogs(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (SpiritShieldCrafting.createSpiritShield(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (CombineGodsword.createGodSwordBlade(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (CombineGodsword.createGodSword(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (Herblore.isHerbloreItemOnItem(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (Firemaking.grabData(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (Fletching.normal(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (Fletching.others(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (SmithingOtherItem.smithVariousItems(player, itemUsedId, itemUsedWithId)) {
			return;
		}

		if (JewelryCrafting.stringAmulet(player, itemUsedId, itemUsedWithId)) {
			return;
		}
		if (CrystalCombining.isCrystalBootsParts(player, itemUsedId, itemUsedWithId)) {
			return;
		}

		PotionCombining.combinePotion(player, true, itemUsedId, itemUsedWithId, itemUsedWithSlot, itemUsedSlot, false);

		if (Plugin.execute("use_item_" + itemUsedId + "_on_" + itemUsedWithId, player)) {
		} else {
			player.getPA().sendMessage("Nothing interesting happens.");
		}
	}

}
