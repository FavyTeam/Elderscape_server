package game.content.minigame.barrows;

import core.GameType;
import core.ServerConstants;
import game.content.bank.Bank;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import utility.Misc;

/**
 * Repair Barrows equipment.
 *
 * @author MGT Madness, created on 22-02-2015.
 */
public class BarrowsRepair {

	private static int getBarrowsRepairCostPerPiece() {
		return GameType.isOsrsEco() ? 75000 : 100;
	}

	/**
	 * List of barrows pieces followed by the repaired version.
	 */
	public static int[][] brokenBarrows =
			{
					{4708, 4860},
					{4710, 4866},
					{4712, 4872},
					{4714, 4878},
					{4716, 4884},
					{4720, 4896},
					{4718, 4890},
					{4720, 4896},
					{4722, 4902},
					{4732, 4932},
					{4734, 4938},
					{4736, 4944},
					{4738, 4950},
					{4724, 4908},
					{4726, 4914},
					{4728, 4920},
					{4730, 4926},
					{4745, 4956},
					{4747, 4962},
					{4749, 4968},
					{4751, 4974},
					{4753, 4980},
					{4755, 4986},
					{4757, 4992},
					{4759, 4998},

					//noted
					{4709, 4861},
					{4711, 4867},
					{4713, 4873},
					{4715, 4879},
					{4717, 4885},
					{4721, 4897},
					{4719, 4891},
					{4721, 4897},
					{4723, 4903},
					{4733, 4933},
					{4735, 4939},
					{4737, 4945},
					{4739, 4951},
					{4725, 4909},
					{4727, 4915},
					{4729, 4921},
					{4731, 4927},
					{4746, 4957},
					{4748, 4963},
					{4750, 4969},
					{4752, 4975},
					{4754, 4981},
					{4756, 4987},
					{4758, 4993},
					{4760, 4999}
			};

	/**
	 * Repair the Barrows equipment.
	 *
	 * @param player The associated player.
	 */
	public static void repair(Player player) {
		int totalCost = 0;
		int costPerPiece = getRepairCost(player);
		int originalCostPerPiece = costPerPiece;
		boolean hasRepairedAnItem = false;
		boolean hasBarrows = false;
		for (int j = 0; j < player.playerItems.length; j++) {
			if (player.playerItems[j] <= 1) {
				continue;
			}
			for (int i = 0; i < brokenBarrows.length; i++) {
				if (player.playerItems[j] - 1 == brokenBarrows[i][1]) {
					hasBarrows = true;
					costPerPiece = originalCostPerPiece;
					int singlePieceCost = costPerPiece;
					int cashAmount = ItemAssistant.getItemAmount(player, ServerConstants.getMainCurrencyId());
					if (cashAmount <= 0 && originalCostPerPiece > 0) {
						break;
					}
					boolean noted = false;
					if (ItemDefinition.getDefinitions()[player.playerItems[j] - 1].note) {
						costPerPiece = costPerPiece * player.playerItemsN[j];
						noted = true;
					}

					int howManyICanRepair = 0;
					// Player does not have enough money
					if (costPerPiece > cashAmount && noted && cashAmount >= singlePieceCost && originalCostPerPiece > 0) {
						//500 bm
						// it costs 100
						// and i have 6 dh broken pieces noted

						howManyICanRepair = cashAmount / singlePieceCost;
						costPerPiece = singlePieceCost * howManyICanRepair;


					}
					if (costPerPiece > cashAmount && !hasRepairedAnItem && originalCostPerPiece > 0) {
						player.getDH().sendDialogues(3); // You do not have enough coins dialogue.
						return;
					}
					ItemAssistant.deleteItemFromInventory(player, ServerConstants.getMainCurrencyId(), costPerPiece);
					hasRepairedAnItem = true;
					totalCost += costPerPiece;
					if (howManyICanRepair > 0) {
						player.playerItemsN[j] -= howManyICanRepair;
						if (player.playerItemsN[j] <= 0) {
							player.playerItems[j] = 0;
						}
						ItemAssistant.addItemToInventoryOrDrop(player, brokenBarrows[i][0], howManyICanRepair);
					} else {
						if (Bank.enableDupe) {
							player.playerItems[j] = brokenBarrows[i][0] + 1;
						} else {
							player.playerItems[j] = brokenBarrows[i][0] + 1;
							player.playerItems[j] = 0;
							int previousAmount = player.playerItemsN[j];
							player.playerItemsN[j] = 0;
							ItemAssistant.addItemToInventoryOrDrop(player, brokenBarrows[i][0], previousAmount);

						}
					}
				}
			}
		}
		if (!hasBarrows) {
			player.getDH().sendDialogues(214); // You do not have any barrows equipment.
			return;
		}
		if (totalCost > 0 && ItemAssistant.getItemAmount(player, ServerConstants.getMainCurrencyId()) == 0) {
			player.getPA().sendMessage("You need at least " + originalCostPerPiece + " " + ServerConstants.getMainCurrencyName() + " repair.");
			return;
		}
		if (totalCost > 0) {
			//String plural = (totalCost > costPerPiece ? "s." : ".");
			player.getDH().sendItemChat("", "Bob repairs your items for " + Misc.formatNumber(totalCost) + " " + ServerConstants.getMainCurrencyName() + ".",
			                            ServerConstants.getMainCurrencyId() + 5, 200, 15, 0);
		} else if (originalCostPerPiece == 0) {
			player.getDH().sendItemChat("", "Bob repairs your items for free!", ServerConstants.getMainCurrencyId() + 5, 200, 15, 0);
		}
	}

	public static int getRepairCost(Player player) {
		int costPerPiece = getBarrowsRepairCostPerPiece();
		if (player.isLegendaryDonator()) {
			costPerPiece -= GameType.isOsrsEco() ? 75000 : 100;
		} else if (player.isExtremeDonator()) {
			costPerPiece -= GameType.isOsrsEco() ? 56000 : 75;
		} else if (player.isSuperDonator()) {
			costPerPiece -= GameType.isOsrsEco() ? 37000 : 50;
		} else if (player.isDonator()) {
			costPerPiece -= GameType.isOsrsEco() ? 19000 : 25;
		}
		return costPerPiece;
	}

}
