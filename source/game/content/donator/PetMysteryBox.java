package game.content.donator;

import game.content.interfaces.InterfaceAssistant;
import game.item.ItemAssistant;
import game.npc.pet.BossPetDrops;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.util.ArrayList;
import utility.Misc;

public class PetMysteryBox {

	public final static int[] petsMysteryBox =
	//@formatter:off
	{
			12643, // Dagannoth Supreme
			12644, // Dagannoth Prime
			12645, // Dagannoth Rex
			13178, // Callisto
			12652, // K'ril
			12651, // Commander Zilyana
			12649, // Kree'arra
			12650, // General Graardor
			11995, // Chaos Elemental
			16157, // White chaos elemental pet
			12653,// King black dragon
			13177, // Venenatis.
			16007, // Tormented demon.
			16008, // Ice Strykewyrm.
			13179, // Vet'ion Jr
			13225, // Tzrek-jad
			// Beaver (woodcutting)
			13322,
			// Heron (fishing)
			13320,
			// Rock golem (Mining)
			13321,
			// Giant Squirrel (Agility)
			20659,
			// Tangleroot (Farming)
			20661,
			// Rift guardian (Runecrafting)
			20665,
			// Rocky (thieving)
			20663,
			// Bloodhound
			19730,
			// Dharok pet
			16015,
			// Phoenix (Firemaking)
			20693,
			// Karil pet
			16123,
			// Revenant dragon pet
			16198,
			// Revenant knight pet
			16203,
			// Revenant demon pet
			16204,
			// Herbi pet (Herblore)
			21509,
			// Cave Kraken pet
			12655,
			// Giant rat pet
			16250,
			// Scorpia's offspring
			13181,
			// Spawn pet (Lizard shaman spawn pet)
			16261,
			// Gorilla pet
			16028,
			// Kalphite princess (green)
			12647,
			// Baby mole
			12646,
			// Dark core
			12816,
			// Vorki pet
			21992, 
			
			// Mystery box only pets
			// Olmlet pet
			20851,
			// Chompy chick
			13071,
			// Jal-nib-rek
			21291,
			// Pet penance queen.
			12703,
			// Lil'Zik
			22473,
			
			//43 pets total
			//@formatter:on
	};

	public static void openPetMysteryBox(Player player) {
		if (player.usingPetMysteryBoxEvent) {
			return;
		}
		if (!ItemAssistant.hasItemInInventory(player, 6199)) {
			return;
		}
		ArrayList<Integer> availablePets = new ArrayList<Integer>();
		for (int index = 0; index < petsMysteryBox.length; index++) {
			boolean hasPet = false;
			for (int a = 0; a < player.singularUntradeableItemsOwned.size(); a++) {
				if (Integer.parseInt(player.singularUntradeableItemsOwned.get(a)) == petsMysteryBox[index]) {
					hasPet = true;
					break;
				}
			}
			if (hasPet) {
				continue;
			}
			availablePets.add(petsMysteryBox[index]);
		}
		if (availablePets.isEmpty()) {
			player.getPA().sendMessage("You already own every pet!");
			return;
		}
		//delete mystery box
		InterfaceAssistant.changeInterfaceSprite(player, 29214, 752); // Reset background to non-gold version.
		ItemAssistant.deleteItemFromInventory(player, 6199, 1);
		final int petItemIdToGive = availablePets.get(Misc.random(availablePets.size() - 1));
		int count = 0;
		String data = "";
		player.getPA().sendFrame126("Which pet will you unlock?", 29213);
		player.getPA().sendMessage(":packet:petmysteryboxstart"); // Tell client to clear the previous data.
		for (int index = 0; index < availablePets.size(); index++) {
			count++;
			data = data + availablePets.get(index) + "#";
			if (count == 7) {
				player.getPA().sendMessage(":packet:petmysteryboxarray:" + data);
				count = 0;
				data = "";
			}
		}
		if (!data.isEmpty()) {
			player.getPA().sendMessage(":packet:petmysteryboxarray:" + data);
		}
		player.getPA().sendMessage(":packet:petmysteryboxend " + petItemIdToGive); // Tell client to start the interface action and give it the unlocked pet
		player.usingPetMysteryBoxEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}
	
			@Override
			public void stop() {
				player.getPA().sendFrame126("@yel@You have unlocked the " + ItemAssistant.getItemName(petItemIdToGive) + "!", 29213);
				BossPetDrops.awardBoss(player, petItemIdToGive, petItemIdToGive, 0, "the Pet Mystery Box");
				player.gfx100(199);
				player.usingPetMysteryBoxEvent = false;
			}
		}, 11);
	}

	public static void displayPetMysteryBoxInterface(Player player) {
		player.getPA().sendFrame126("Which pet will you unlock?", 29213);
		InterfaceAssistant.changeInterfaceSprite(player, 29215, 751); // Add back the question mark.
		InterfaceAssistant.changeInterfaceSprite(player, 29214, 752); // Reset background to non-gold version.
		player.getPA().sendFrame34(29216, 0, 0, 0); // Delete the item id on the pet interface.
		player.getPA().displayInterface(29211);
	}

}
