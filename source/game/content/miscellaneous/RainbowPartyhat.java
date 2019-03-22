package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Rainbow partyhat creation.
 *
 * @author MGT Madness, created on 17-06-2016.
 */
public class RainbowPartyhat {

	private final static int[] PARTY_HATS =
			{1038, 1040, 1042, 1044, 1046, 1048};

	public static boolean partyHatCombine(Player player, int itemUsedId, int itemUsedWithId) {
		boolean hasItemUsedPartyhat = false;
		boolean hasItemUsedWithPartyhat = false;

		for (int index = 0; index < PARTY_HATS.length; index++) {
			if (itemUsedId == PARTY_HATS[index]) {
				hasItemUsedPartyhat = true;
			}
			if (itemUsedWithId == PARTY_HATS[index]) {
				hasItemUsedWithPartyhat = true;
			}
		}
		boolean missingPartyhat = false;
		if (hasItemUsedPartyhat && hasItemUsedWithPartyhat) {
			for (int index = 0; index < PARTY_HATS.length; index++) {
				if (!ItemAssistant.hasItemAmountInInventory(player, PARTY_HATS[index], 1)) {
					player.getPA().sendMessage("You are missing the " + ItemAssistant.getItemName(PARTY_HATS[index]) + ".");
					missingPartyhat = true;
				}
			}

			if (missingPartyhat) {
				return true;
			}

			// Delete and give partyhat.
			for (int index = 0; index < PARTY_HATS.length; index++) {
				ItemAssistant.deleteItemFromInventory(player, PARTY_HATS[index], 1);
			}
			ItemAssistant.addItem(player, 11863, 1);
			player.getDH().sendItemChat("", "You've combined the partyhats into a rainbow partyhat!", 11863, 200, 15, 0);
			return true;
		}

		return false;
	}

	public static void dismantleRainbow(Player player) {
		if (!ItemAssistant.hasInventorySlotsAlert(player, 5)) {
			return;
		}
		ItemAssistant.deleteItemFromInventory(player, 11863, 1);
		for (int index = 0; index < PARTY_HATS.length; index++) {
			ItemAssistant.addItem(player, PARTY_HATS[index], 1);
		}
	}
}
