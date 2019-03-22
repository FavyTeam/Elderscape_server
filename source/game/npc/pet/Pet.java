package game.npc.pet;

import core.GameType;
import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.Wolpertinger;
import game.content.skilling.summoning.Summoning;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.object.clip.Region;
import game.player.Player;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author Jordon
 * @author Animeking1120
 * @author Erick
 * @author MGT Madness, started editing at 11-12-2013.
 * <p>
 * Pet system.
 */
public class Pet {
	public static ArrayList<String> petsToClaim = new ArrayList<String>();

	public static void loadPetsToClaim() {
		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/petsclaim.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				petsToClaim.add(line);
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void claimPet(Player player) {
		for (int index = 0; index < petsToClaim.size(); index++) {
			String parse[] = petsToClaim.get(index).split(":");
			if (!player.getPlayerName().equalsIgnoreCase(parse[0])) {
				continue;
			}
			String data = parse[1];
			String secondParse[] = data.split(" ");
			if (secondParse.length > ItemAssistant.getFreeInventorySlots(player)) {
				player.getPA()
				      .sendMessage(ServerConstants.RED_COL + "Bank your inventory and relog to receive your pets!");
				player.getPA()
				      .sendMessage(ServerConstants.RED_COL + "Bank your inventory and relog to receive your pets!");
				player.getPA()
				      .sendMessage(ServerConstants.RED_COL + "Bank your inventory and relog to receive your pets!");
				return;
			}
			for (int a = 0; a < secondParse.length; a++) {
				ItemAssistant.addItem(player, Integer.parseInt(secondParse[a]), 1);
			}
			petsToClaim.remove(index);
			return;
		}
	}

	/**
	 * Dismiss the familiar.
	 *
	 * @param player The associated player.
	 * @param dismissed True, if the player died in Wilderness.
	 */
	public static void dismissFamiliar(Player player) {
		if (!player.getPetSummoned()) {
			player.playerAssistant.sendMessage("You do not have a familiar summoned.");
			return;
		}

		for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (NpcHandler.npcs[i] == null) {
				continue;
			}
			if (NpcHandler.npcs[i].getNpcPetOwnerId() == player.getPlayerId()) {
				Pet.deletePet(NpcHandler.npcs[i]);
				if (NpcHandler.npcs[i].summoned) {
					player.getPA().sendMessage(":packet:npcpetid:-1");
				}
				break;
			}
		}
		Summoning.reset(player);
		player.setPetSummoned(false);
		player.setPetId(0);
		Wolpertinger.informClientOff(player);
		player.playerAssistant.sendMessage("You have dismissed your familiar.");
	}

	/**
	 * @param itemID The item identity to check if it matches any pet inventory
	 * item.
	 * @return True, if the itemID is a pet inventory item.
	 */
	public static boolean petItem(int itemID) {
		for (int j = 0; j < PetData.petData.length; j++) {
			if (itemID == PetData.petData[j][1]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find if this NPC is a pet to pickup.
	 *
	 * @param player The player interacting with the the NPC.
	 * @param npcType The NPC type being interacted with.
	 */
	public static boolean pickUpPetRequirements(Player player, int index) {
				if (NpcHandler.npcs[player.getNpcClickIndex()].getNpcPetOwnerId() == player.getPlayerId()) {
					if (ItemAssistant.getFreeInventorySlots(player) > 0) {
				pickUpPet(player, PetData.petData[index][0]);
						return true;
					} else {
						player.playerAssistant.sendMessage("Not enough space in your inventory.");
						return true;
					}
				} else {
					player.playerAssistant.sendMessage("This is not your pet.");
					return true;
		}
	}

	/**
	 * Pick up the pet and place in inventory of the player.
	 *
	 * @param player The player picking up the pet.
	 * @param pet The identity of the pet being picked up
	 */
	public static void pickUpPet(final Player player, final int pet) {
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.getPA().sendMessage("You do not have enough inventory space.");
			return;
		}
		player.startAnimation(827);
		player.playerAssistant.sendMessage("You pick up your pet.");
		for (int i = 0; i < PetData.petData.length; i++) {
			if (PetData.petData[i][0] == pet) {
				ItemAssistant.addItem(player, PetData.petData[i][1], 1);
			}
		}
		boolean secondPet = player.getSecondPetId() == pet;
		for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (NpcHandler.npcs[i] == null) {
				continue;
			}
			if (NpcHandler.npcs[i].getNpcPetOwnerId() == player.getPlayerId()) {
				if (NpcHandler.npcs[i].summoned && NpcHandler.npcs[i].npcType == pet) {
					if (secondPet) {
						player.getPA().sendMessage(":packet:npcsecondpetid:-1");
					} else {
						player.getPA().sendMessage(":packet:npcpetid:-1");
					}
					deletePet(NpcHandler.npcs[i]);
					break;
				}
			}
		}
		InterfaceAssistant.summoningOrbOff(player);
		if (secondPet) {
			player.setSecondPetSummoned(false);
			player.setSecondPetId(0);
		} else {
			player.setPetSummoned(false);
			player.setPetId(0);
		}
	}

	public static Npc summonNpcOnValidTile(Player player, int npcType, boolean secondPet) {
		int x = player.getX();
		int y = player.getY();
		if (Region.pathUnblocked(x, y, player.getHeight(), "SOUTH")) {
			y--;
		} else if (Region.pathUnblocked(x, y, player.getHeight(), "WEST")) {
			x--;
		} else if (Region.pathUnblocked(x, y, player.getHeight(), "EAST")) {
			x++;
		} else if (Region.pathUnblocked(x, y, player.getHeight(), "NORTH")) {
			y++;
		}
		int height = player.getHeight();
		return summonNpc(player, npcType, x, y, height, true, secondPet);
	}

	/**
	 * Summon the pet.
	 *
	 * @param player The player who summoned the pet.
	 * @param npcType The pet being summoned.
	 * @param x The x coord of the pet.
	 * @param y The x coord of the pet.
	 * @param heightLevel The height of the pet.
	 */
	public static Npc summonNpc(Player player, int npcType, int x, int y, int heightLevel, boolean pet,
	                            boolean secondPet) {
		int slot = -1;
		for (int i = 1; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (NpcHandler.npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return null;
		}
		Npc newNpc = new Npc(slot, npcType);
		newNpc.setX(x);
		newNpc.setY(y);
		newNpc.setSpawnPositionX(x);
		newNpc.setSpawnPositionY(y);
		newNpc.setHeight(heightLevel);
		newNpc.faceAction = "";
		if (pet) {
			if (secondPet) {
				player.getPA().sendMessage(":packet:npcsecondpetid:" + slot);
			} else {
				player.getPA().sendMessage(":packet:npcpetid:" + slot);
			}
			newNpc.setSpawnedBy(player.getPlayerId());
			boolean facePlayer = true;

			for (int index = 0; index < PetData.petData.length; index++) {
				if (PetData.petData[index].length == 4 && PetData.petData[index][0] == npcType) {
					facePlayer = false;
					break;
				}
			}

			if (facePlayer) {
				newNpc.facePlayer(player.getPlayerId());
			}
			newNpc.summoned = true;
			newNpc.setNpcPetOwnerId(player.getPlayerId());
			if (npcType == 6869) {
				newNpc.requestAnimation(8309);
			}
			if (secondPet) {
				player.setSecondPetId(npcType);
				player.setSecondPetSummoned(true);
			} else {
				player.setPetId(npcType);
				player.setPetSummoned(true);
			}
		}
		NpcHandler.npcs[slot] = newNpc;
		newNpc.onAdd();
		return newNpc;
	}

	/**
	 * Delete the current pet to summon a new one. This is for cases such as the
	 * Player is too far away from Pet, so the pet gets deleted and summoned
	 * close to player. Or if pet is picked up.
	 */
	public static void deletePet(Npc pet) {
		pet.setX(-1);
		pet.setY(-1);
		pet.faceAction = "";
		pet.setCurrentHitPoints(-1);
		pet.maximumHitPoints = -1;
		pet.needRespawn = true;
		pet.respawnTimer = 0;
		pet.setDead(true);
		pet.applyDead = true;
		pet.setNpcPetOwnerId(0);
		pet.deleteNpc = true;
		pet.onRemove();
	}

	/**
	 * Spawn the pet for the player that just logged in
	 *
	 * @param player The associated player.
	 */
	public static void ownerLoggedIn(Player player) {
		boolean firstPet = player.getPetSummoned() && player.getPetId() > 0;
		boolean secondPet = player.getSecondPetSummoned() && player.getSecondPetId() > 0;
		if (firstPet) {
			Pet.summonNpcOnValidTile(player, player.getPetId(), false);
			player.playerAssistant.sendMessage("Your loyal pet finds you!");
		}
		if (secondPet) {
			Pet.summonNpcOnValidTile(player, player.getSecondPetId(), true);
			player.playerAssistant.sendMessage("Your loyal pet finds you!");
		}
	}

	/**
	 * True to pick-up the pet.
	 *
	 * @param string FIRST CLICK/SECOND CLICK
	 */
	public static boolean pickUpPetClick(Player player, int npcId, String string) {
		if (GameType.isPreEoc()) {
			return false;
		}
		int pickUpNpcType = string.equals("FIRST CLICK") ? 1 : 2;
		for (int index = 0; index < PetData.petData.length; index++) {
			int npcIdData = PetData.petData[index][0];
			int pickUpType = PetData.petData[index][2];
			if (npcIdData == npcId && pickUpType == pickUpNpcType) {
				Pet.pickUpPetRequirements(player, index);
				return true;
			}
		}
		return false;
	}

	/**
	 * Call the familiar close to the player.
	 */
	public static void callFamiliar(Player player) {
		if (!player.getPetSummoned() && !player.getSecondPetSummoned()) {
			player.playerAssistant.sendMessage("You do not have a familiar summoned.");
		}
		if ((System.currentTimeMillis() - player.callFamiliarTimer) < 3000) {
			return;
		}
		player.forceCallFamiliar = true;
		player.callFamiliarTimer = System.currentTimeMillis();

	}

	private final static int[][] petMetamorphosisData = {
			//@formatter:on
			// All npc transformation ids and the last number means if its 2,
			// then metamorphosis is npc click 2.
			{5536, 5537, 3}, // Vet'ion Jr.
			{2130, 2131, 2132, 3}, // Snakeling
			{6637, 6638, 2}, // Kalphite princess
			{318, 8010, 3}, // Dark core
			//@formatter:off
	};

	/**
	 * Metamorphosis option on the pet.
	 */
	public static boolean petMetamorphosis(Player player, Npc npc, int clickType) {
		if (npc.getNpcPetOwnerId() != player.getPlayerId()) {
			return false;
		}
		for (int index = 0; index < petMetamorphosisData.length; index++) {
			int arrayLength = petMetamorphosisData[index].length;
			for (int i = 0; i < arrayLength - 1; i++) {
				if (petMetamorphosisData[index][i] == npc.npcType
				    && clickType == petMetamorphosisData[index][arrayLength - 1]) {
					int nextIndex = i + 1;
					if (nextIndex > arrayLength - 2) {
						nextIndex = 0;
					}
					int switchTo = petMetamorphosisData[index][nextIndex];
					boolean firstPet = player.getPetSummoned() && player.getPetId() == npc.npcType;
					boolean secondPet = player.getSecondPetSummoned() && player.getSecondPetId() == npc.npcType;
					int petId = firstPet ? player.getPetId() : player.getSecondPetId();
					if (switchTo > 0 && switchTo != (firstPet ? player.getPetId() : player.getSecondPetId())) {
						for (int b = 0; b < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; b++) {
							Npc npcLoop = NpcHandler.npcs[b];
							if (npcLoop == null) {
								continue;
							}
							if (npcLoop.getNpcPetOwnerId() == player.getPlayerId() && npcLoop.npcType == petId) {
								int oldX = npcLoop.getX();
								int oldY = npcLoop.getY();
								int oldHeight = npcLoop.getHeight();
								Pet.deletePet(npcLoop);
								Pet.summonNpc(player, switchTo, oldX, oldY, oldHeight, true, secondPet);
								break;
							}
						}
					}
					return true;
				}
			}
		}
		return false;
	}

}
