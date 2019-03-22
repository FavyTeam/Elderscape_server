package game.content.skilling.smithing;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Smithing other items such as a Godsword, dragon square, dfs etc...
 *
 * @author MGT Madness, created on 06-10-2015.
 */
public class SmithingOtherItem {

	/**
	 * Smith various items.
	 */
	public static boolean smithVariousItems(Player player, int itemUsed, int usedWith) {
		if (usedWith == 11286 && itemUsed == 1540 || usedWith == 1540 && itemUsed == 11286) {
			if (player.baseSkillLevel[ServerConstants.SMITHING] < 90) {
				player.getDH().sendStatement("You need 90 smithing to create a Dragonfire shield.");
				return false;
			}
			player.playerAssistant.sendMessage("You create a Dragonfire shield!");
			player.skillingStatistics[SkillingStatistics.ITEMS_SMITHED]++;
			ItemAssistant.deleteItemFromInventory(player, 11286, 1);
			ItemAssistant.deleteItemFromInventory(player, 1540, 1);
			ItemAssistant.addItem(player, 11284, 1);
			Skilling.addSkillExperience(player, 2000, ServerConstants.SMITHING, false);
			return true;
		} else if (usedWith == 22006 && itemUsed == 1540 || usedWith == 1540 && itemUsed == 22006) {
			if (player.baseSkillLevel[ServerConstants.SMITHING] < 90) {
				player.getDH().sendStatement("You need 90 smithing to create a Dragonfire ward.");
				return false;
			}
			player.playerAssistant.sendMessage("You create a Dragonfire ward!");
			player.skillingStatistics[SkillingStatistics.ITEMS_SMITHED]++;
			ItemAssistant.deleteItemFromInventory(player, 22006, 1);
			ItemAssistant.deleteItemFromInventory(player, 1540, 1);
			ItemAssistant.addItem(player, 22002, 1);
			Skilling.addSkillExperience(player, 2000, ServerConstants.SMITHING, false);
			return true;
		} else if (usedWith == 2366 || itemUsed == 2368) {
			if (ItemAssistant.hasItemInInventory(player, 2366) && ItemAssistant.hasItemInInventory(player, 2368)) {
				if (player.baseSkillLevel[ServerConstants.SMITHING] < 60) {
					player.getDH().sendStatement("You need 60 smithing to create a dragon sq shield.");
					return false;
				}
				ItemAssistant.deleteItemFromInventory(player, 2366, 1);
				ItemAssistant.deleteItemFromInventory(player, 2368, 1);
				ItemAssistant.addItem(player, 1187, 1);
				player.skillingStatistics[SkillingStatistics.ITEMS_SMITHED]++;
				Skilling.addSkillExperience(player, 75, ServerConstants.SMITHING, false);
				player.playerAssistant.sendMessage("You combine the parts and create a Dragon sq shield.");
				return true;
			}
		}
		return false;
	}

}
