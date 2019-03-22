package game.npc.pet;

import core.ServerConstants;
import game.content.bank.Bank;
import game.content.miscellaneous.Announcement;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.data.NpcDefinition;
import game.player.Player;

/**
 * Boss pet drops after getting a boss kill.
 *
 * @author MGT Madness, created on 16-01-2016.
 */
public class BossPetDrops {

	public final static int DROP_RATE_JAD_PET = 800;

	/**
	 * Chance for Jad pet when exchanging Fire capes. 2k per fire cape.
	 */
	public final static int JAD_PET_EXCHANGE_CHANCE = 400;

	/**
	 * Boss npc id, boss pet inventory id. Does not include Jad, jad is in FightCaves class
	 * Add the npc to Donator content mystery box.
	 * Add the npc also to Drop table interface
	 */
	public final static int[] NORMAL_BOSS_DATA =
			{
					2265, // Dagannoth Supreme
					2266, // Dagannoth Prime
					2267, // Dagannoth Rex
					6503, // Callisto
					3129, // K'ril
					2205, // Commander Zilyana
					3162, // Kree'arra
					2215, // General Graardor
					2054, // Chaos Elemental
					6619, // White chaos elemental pet
					239, // King black dragon
					6504, // Venenatis.
					11011, // Tormented demon.
					11006, // Ice Strykewyrm.
					6611, // Vet'tion jr (purple version)
					496, // Cave kraken
					6618, // Crazy Archaeologist
					6615, // Scorpia.
					6766, // Lizard shaman
					7144, // Demonic gorilla
					963, // Kalphite Queen
					5779, // Giant mole
					319, // Dark core
					5862, // Hellpuppy
					8059, // Vorkath
			// 21 bosses without jad
					//@formatter:on
			};

	public static void awardBoss(Player player, int item, int originalPetInventory, int npcType, String from) {
		boolean received = false;

		if (ItemAssistant.hasSingularUntradeableItem(player, originalPetInventory)) {
			return;
		}
		if (!received) {
			if (player.getPetSummoned() && ItemAssistant.addItem(player, item, 1)) {
				received = true;
				player.getPA().sendMessage("You feel something weird sneaking into your backpack.");
			}
		}
		if (!received && !player.getPetSummoned()) {
			for (int i = 0; i < PetData.petData.length; i++) {
				if (PetData.petData[i][1] == item) {
					Pet.summonNpcOnValidTile(player, PetData.petData[i][0], false);
					player.getPA().sendMessage("You have a funny feeling like you're being followed.");
					received = true;
				}
			}
		}
		if (!received) {
			Bank.addItemToBank(player, item, 1, false);
			player.getPA().sendMessage("Your pet has been added to your bank.");
			received = true;
		}
		if (!received) {
			ItemAssistant.addItemReward(null, player.getPlayerName(), item, 1, false, -1);
			received = true;
		}
		if (received) {
			if (from.equals("BOSS")) {
				RareDropLog.appendRareDrop(player, NpcDefinition.getDefinitions()[npcType].name + ": " + ItemAssistant.getItemName(item));
			} else {
				RareDropLog.appendRareDrop(player, from + ": " + ItemAssistant.getItemName(item));
			}
			if (from.equals("BOSS")) {
				Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " received one " + ItemAssistant.getItemName(item) + " from " + NpcDefinition
						                                                                                                                                                     .getDefinitions()[npcType].name
				                      + "!");
			} else {
				boolean skipAnnouncement = from.toLowerCase().contains("pet mystery box") && player.profilePrivacyOn;
				if (!skipAnnouncement) {
					Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " received one " + ItemAssistant.getItemName(item) + " from " + from + "!");
				}
			}
			player.getPA().sendScreenshot(ItemAssistant.getItemName(item), 2);
			player.singularUntradeableItemsOwned.add(Integer.toString(originalPetInventory));
		}
	}

}
