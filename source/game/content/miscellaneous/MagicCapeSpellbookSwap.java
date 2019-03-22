package game.content.miscellaneous;

import core.GameType;
import game.player.Player;
import utility.Misc;

/**
 * Magic cape, spellbook swap feature.
 * @author MGT Madness, created on 07-06-2018.
 */
public class MagicCapeSpellbookSwap {

	public final static int HYBRID_KILLS_AMOUNT_REQUIRED = 75;

	public final static int MAXIMUM_USES_PER_DAY = 5;

	public static boolean operatedMagicCape(Player player, int itemId) {
		if (!GameType.isOsrs()) {
			return false;
		}
		//// Magic cape and (t) version
		if (itemId == 9762 || itemId == 9763) {
			if (!MagicCapeSpellbookSwap.canOperateMagicCape(player)) {
				return true;
			}
			player.dynamicOptions.clear();
			player.dynamicOptions.add("MODERN");
			player.dynamicOptions.add("ANCIENT");
			player.dynamicOptions.add("LUNAR");
			player.dynamicOptions.remove(player.spellBook);
			String option1 = Misc.capitalize(player.dynamicOptions.get(0)).replace("Ancient", "Ancient magicks");
			String option2 = Misc.capitalize(player.dynamicOptions.get(1)).replace("Ancient", "Ancient magicks");
			player.getDH().sendDialogues(720);
			player.getDH().sendOption(option1, option2, "Cancel");
			// On Osrs you can right click the cape in inventory or in equipment and then select any book you want.
			return true;
		}
		return false;
	}

	public static void operateMagicCapeOptionUsed(Player player, int optionIndex) {
		switch (player.dynamicOptions.get(optionIndex)) {
			case "MODERN":
				SpellBook.modernSpellBook(player, true);
				break;
			case "ANCIENT":
				SpellBook.ancientMagicksSpellBook(player, true);
				break;
			case "LUNAR":
				SpellBook.lunarSpellBook(player, true);
				break;
		}
		player.getPA().closeInterfaces(true);
		boolean dateMatches = player.getDateUsedSpellbookSwap().equals(Misc.getDateOnlyDashes());
		if (!dateMatches) {
			player.setDateUsedSpellbookSwap(Misc.getDateOnlyDashes());
			player.setSpellbookSwapUsedOnSameDateAmount(0);
		}
		player.setSpellbookSwapUsedOnSameDateAmount(player.getSpellbookSwapUsedOnSameDateAmount() + 1);
		int usesLeft = (MAXIMUM_USES_PER_DAY - player.getSpellbookSwapUsedOnSameDateAmount());
		if (usesLeft == 0) {
			player.getPA().sendMessage("You have used up all your charges for today.");
		} else {
			player.getPA().sendMessage("You can use " + usesLeft + " more spellbook " + Misc.getPluralWordWithKey("charge", usesLeft) + " today.");
		}
	}
	public static boolean canOperateMagicCape(Player player) {
		if (player.getHybridKills() < HYBRID_KILLS_AMOUNT_REQUIRED && !player.isUberDonator()) {
			player.getPA().sendMessage("You need " + HYBRID_KILLS_AMOUNT_REQUIRED + " hybrid kills to access the spellbook swap.");
			return false;
		}
		boolean dateMatches = player.getDateUsedSpellbookSwap().equals(Misc.getDateOnlyDashes());
		if (dateMatches && player.getSpellbookSwapUsedOnSameDateAmount() == MAXIMUM_USES_PER_DAY) {
			player.getPA().sendMessage("You have already used " + MAXIMUM_USES_PER_DAY + " spellbook charges today.");
			return false;
		}
		return true;
	}

}
