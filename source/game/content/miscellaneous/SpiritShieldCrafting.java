package game.content.miscellaneous;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Spirit shield related creation.
 *
 * @author MGT Madness, created on 23-12-2015.
 */
public class SpiritShieldCrafting {

	private final static int[] SIGILS =
			{12827, 12819, 12823};

	private final static int HOLY_ELIXIR = 12833;

	private final static int SPIRIT_SHIELD = 12829;

	private final static int BLESSED_SPIRIT_SHIELD = 12831;


	/**
	 * Spirit shield creation.
	 *
	 * @param player The associated player.
	 */
	public static boolean createSpiritShield(Player player, int itemUsed, int itemUsedOn) {
		boolean hasSpiritShieldComponent = false;
		for (int i = 0; i < SIGILS.length; i++) {
			if (itemUsed == SIGILS[i]) {
				hasSpiritShieldComponent = true;
			}
		}
		if (itemUsed == HOLY_ELIXIR || itemUsed == BLESSED_SPIRIT_SHIELD || itemUsed == SPIRIT_SHIELD) {
			hasSpiritShieldComponent = true;
		}
		if (!hasSpiritShieldComponent) {
			return false;
		}

		boolean createBlessed = false;
		if ((itemUsed == HOLY_ELIXIR || itemUsedOn == HOLY_ELIXIR) && (itemUsed == SPIRIT_SHIELD || itemUsedOn == SPIRIT_SHIELD)) {
			if (player.baseSkillLevel[ServerConstants.PRAYER] < 85) {
				player.getDH().sendStatement("You need 85 Prayer to create a Blessed spirit shield.");
				return false;
			}
			createBlessed = true;
		}
		if (createBlessed) {
			ItemAssistant.deleteItemFromInventory(player, SPIRIT_SHIELD, 1);
			ItemAssistant.deleteItemFromInventory(player, HOLY_ELIXIR, 1);
			ItemAssistant.addItem(player, BLESSED_SPIRIT_SHIELD, 1);
			player.getDH().sendItemChat("", "You create a Blessed spirit shield.", BLESSED_SPIRIT_SHIELD, 200, 0, -10);
		}
		int sigilUsed = 0;
		if (itemUsed == BLESSED_SPIRIT_SHIELD || itemUsedOn == BLESSED_SPIRIT_SHIELD) {
			for (int i = 0; i < SIGILS.length; i++) {
				if (itemUsed == SIGILS[i] || itemUsedOn == SIGILS[i]) {
					sigilUsed = SIGILS[i];
				}
			}
		}
		if (sigilUsed == 0) {
			return false;
		}
		//
		if (player.baseSkillLevel[ServerConstants.PRAYER] < 90) {
			player.getDH().sendStatement("You need 90 Prayer to use this sigil.");
			return false;
		}
		if (player.baseSkillLevel[ServerConstants.SMITHING] < 85) {
			player.getDH().sendStatement("You need 85 Smithing to use this sigil.");
			return false;
		}
		ItemAssistant.deleteItemFromInventory(player, sigilUsed, 1);
		ItemAssistant.deleteItemFromInventory(player, BLESSED_SPIRIT_SHIELD, 1);
		ItemAssistant.addItem(player, sigilUsed - 2, 1);
		player.getDH().sendItemChat("", "You create the " + ItemAssistant.getItemName(sigilUsed - 2) + ".", sigilUsed - 2, 200, 0, -10);
		Skilling.addSkillExperience(player, 1800, ServerConstants.SMITHING, false);
		return true;
	}

}
