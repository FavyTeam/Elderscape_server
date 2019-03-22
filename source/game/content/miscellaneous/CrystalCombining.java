package game.content.miscellaneous;

import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Crystal combining for Primordial boots etc..
 *
 * @author MGT Madness, created on 23-11-2016.
 */
public class CrystalCombining {

	public static enum CrystalBoots {
		ETERNAL_BOOTS(13235, 6920, 13227),
		PEGASIAN_BOOTS(13237, 2577, 13229),
		PRIMORDIAL_BOOTS(13239, 11840, 13231),
		INFERNAL_AXE(13241, 6739, 13233),
		INFERNAL_PICKAXE(13243, 11920, 13233);

		private int crystalBootsResult;

		private int bootsIngredient;

		private int crystalIngredient;


		private CrystalBoots(int crystalBootsResult, int bootsIngredient, int crystalIngredient) {
			this.crystalBootsResult = crystalBootsResult;
			this.bootsIngredient = bootsIngredient;
			this.crystalIngredient = crystalIngredient;
		}

		public int getCrystalBoots() {
			return crystalBootsResult;
		}

		public int getIngredient() {
			return bootsIngredient;
		}

		public int getCrystalIngredient() {
			return crystalIngredient;
		}

	}


	public static boolean isCrystalBootsParts(Player player, int itemUsedId, int itemUsedWithId) {

		int index = -1;
		for (CrystalBoots data : CrystalBoots.values()) {
			if (itemUsedId == data.getCrystalIngredient() && itemUsedWithId == data.getIngredient()
			    || itemUsedWithId == data.getCrystalIngredient() && itemUsedId == data.getIngredient()) {
				index = data.ordinal();
				break;
			}
		}

		if (index == -1) {
			return false;
		}
		/*
		if (CrystalBoots.INFERNAL_AXE.ordinal() == index)
		{
				if (player.baseSkillLevel[ServerConstants.FIREMAKING] < 85)
				{
						player.getDH().sendStatement("You need 85 Firemaking to create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".");
						return false;
				}
		}
		else if (CrystalBoots.INFERNAL_PICKAXE.ordinal() == index)
		{
				if (player.baseSkillLevel[ServerConstants.SMITHING] < 85)
				{
						player.getDH().sendStatement("You need 85 Runecrafting to create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".");
						return false;
				}
		}
		else
		{
				if (player.baseSkillLevel[ServerConstants.RUNECRAFTING] < 60)
				{
						player.getDH().sendStatement("You need 60 Runecrafting to create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".");
						return false;
				}
		
				if (player.baseSkillLevel[ServerConstants.MAGIC] < 60)
				{
						player.getDH().sendStatement("You need 60 Magic to create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".");
						return false;
				}
		}
		*/
		player.getDH()
		      .sendItemChat("", "You create the " + ItemAssistant.getItemName(CrystalBoots.values()[index].getCrystalBoots()) + ".", CrystalBoots.values()[index].getCrystalBoots(),
		                    200, 24, 0);

		player.skillingStatistics[SkillingStatistics.ITEMS_SMITHED]++;
		ItemAssistant.deleteItemFromInventory(player, CrystalBoots.values()[index].getCrystalIngredient(), 1);
		ItemAssistant.deleteItemFromInventory(player, CrystalBoots.values()[index].getIngredient(), 1);
		ItemAssistant.addItem(player, CrystalBoots.values()[index].getCrystalBoots(), 1);
		////Skilling.addSkillExperience(player, 200, ServerConstants.MAGIC);
		//Skilling.addSkillExperience(player, 200, ServerConstants.SMITHING);
		return true;
	}
}
