package game.player.pet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Created by Jason MK on 2018-07-20 at 1:39 PM
 */
public class PlayerPetManager {

    /**
     * The prefix of each of the actual player pet account names.
     */
    private static final String IDENTIFIER_PREFIX = "jasonmgt";

    /**
     * The maximum number of available accounts that can be used as player pets. This limits the number
     * of players that can exist in the world. If {@link #unavailable} meets or exceeds this value no more
     * pets can be created.
     */
    private static final int MAXIMUM_AVAILABLE = 100;

	/**
	 * The size of the player pet.
	 */
	public static final int PET_SIZE = 90;

    /**
     * The single instance of the pet manager.
     */
    private static final PlayerPetManager SINGLETON = new PlayerPetManager();

    /**
     * The unavailable names or accounts.
     */
    private Set<String> unavailable = new HashSet<>();

    private PlayerPetManager() {

    }

	public static boolean dropPlayerPet(Player player, int itemId) {
		if (itemId != 7677) {
			return false;
		}
		if (player.getPlayerPet() != null) {
			player.getPA().sendMessage("You already have a pet summoned.");
			return true;
		}
		PlayerPet pet = PlayerPetManager.getSingleton().create(player);

		if (pet == null) {
			player.getPA().sendMessage("A pet cannot be created at this time.");
			return true;
		}
		ItemAssistant.deleteItemFromInventory(player, 7677, 1);
		player.setPlayerPet(pet);
		player.getPA().sendMessage("The Mini-" + player.getPlayerName() + " has been summoned!");
		return true;
	}

    /**
     * Creates a new player pet if possible, but will fail if no names are available.
     *
     * @param owner
     *            the owner of this pet.
     * @return the new pet, or null if one cannot be created.
     */
    public PlayerPet create(Player owner) {
        String availableName = nextAvailable();

        if (availableName == null) {
            return null;
        }
        unavailable.add(availableName);

        return new PlayerPet(owner, availableName);
    }

    /**
     * Removes the name from the unavailable list.
     *
     * @param name
     *            the name of the pet.
     */
    public void remove(String name) {
        unavailable.remove(name);
    }

    /**
     * Retrieves the first available name, or null if one cannot be provided.
     *
     * @return an available name, or null if none exists.
     */
    private String nextAvailable() {
        if (unavailable.size() > MAXIMUM_AVAILABLE) {
            return null;
        }

        for (int index = 0; index < MAXIMUM_AVAILABLE; index++) {
            String name = String.format("%s%s", IDENTIFIER_PREFIX, index);

            if (!unavailable.contains(name)) {
                return name;
            }
        }
        return null;
    }

    /**
     * On server launch, this will ensure that all accounts exists with at least some given template.
     *
     * @throws IOException
     *            the IOException is thrown if there is an issue reading/writing bot accounts.
     */
    public static void ensureAccountsExist() throws IOException {
        Path template = Paths.get("data", "player_pet_template.txt");

        if (!Files.exists(template)) {
            throw new RuntimeException("No template exists to create the bots from.");
        }
        List<String> templateLines = Files.readAllLines(template);

        int indexOfUsername = templateLines.indexOf("Username =");

        int created = 0;

        for (int index = 0; index < MAXIMUM_AVAILABLE; index++) {
            String name = String.format("%s%s.txt", IDENTIFIER_PREFIX, index);

            Path character = Paths.get("backup", "characters", "bots", name);

            if (!Files.exists(character)) {
                templateLines.set(indexOfUsername, String.format("Username = %s", name.replaceAll(".txt", "")));

                Files.write(character, templateLines, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

                created++;
            }
        }
        if (created > 0) {
			Misc.print(String.format("%s Player Pet accounts created.", created));
        }
    }

    /**
     * The single static instance of this object.
     *
     * @return the single instance.
     */
    public static PlayerPetManager getSingleton() {
        return SINGLETON;
    }
}
