package game.npc.pet;

import core.GameType;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Drop the pet.
 *
 * @author MGT Madness, created on 11-12-2013.
 */
public class DropPet {

	/**
	 * Check if the player has requirements to drop the pet.
	 *
	 * @param player The player dropping the pet.
	 * @param itemId The pet item identity being dropped.
	 * @param slot The slot of the item being dropped.
	 */
	public static boolean dropPetRequirementsOsrs(Player player, int itemId, int slot) {
		if (!GameType.isOsrs()) {
			return false;
		}
		if (player.isUsingSummonPetEvent) {
			return true;
		}
		for (int i = 0; i < PetData.petData.length; i++) {
			if (PetData.petData[i][1] == itemId) {

				if (!player.getPetSummoned()) {
					dropPet(player, itemId, slot, false);
					return true;
				} else {
					if (player.isUberDonator() && !player.getSecondPetSummoned()) {
						dropPet(player, itemId, slot, true);
						return true;
					}
					player.playerAssistant.sendMessage("You already have a pet following you.");
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * Summon the pet and delete inventory pet item.
	 *
	 * @param player The player dropping the pet.
	 * @param itemId The pet identity item in inventory.
	 * @param slot The slot of the pet item in inventory.
	 */
	private static void dropPet(Player player, int itemId, int slot, boolean secondPet) {
		for (int i = 0; i < PetData.petData.length; i++) {
			if (PetData.petData[i][1] == itemId) {
				boolean skipTurn = false;
				if (PetData.petData[i].length >= 4) {
					if (PetData.petData[i][3] == 1) {
						skipTurn = true;
					}
				}
				if (!skipTurn) {
					player.turnPlayerTo(player.getX(), player.getY() - 1);
				}
				summonPetEvent(player, PetData.petData[i][0], secondPet);
			}
		}
		ItemAssistant.deleteItemFromInventory(player, itemId, slot, player.playerItemsN[slot]);
	}

	/**
	 * Start the cycle event of summoning the pet.
	 *
	 * @param player The associated player.
	 * @param petSpawnID The identity of the pet to spawn.
	 */
	private static void summonPetEvent(final Player player, final int petSpawnID, boolean secondPet) {
		if (player.isUsingSummonPetEvent) {
			return;
		}
		player.isUsingSummonPetEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				//InterfaceAssistant.summoningOrbOn(player);
				player.playerAssistant.sendMessage("You drop your pet and it starts following you.");
				Pet.summonNpcOnValidTile(player, petSpawnID, secondPet);
				container.stop();
			}

			@Override
			public void stop() {
				player.isUsingSummonPetEvent = false;
			}
		}, 1);
	}

}
