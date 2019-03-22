package game.content.skilling.summoning.pet;

import java.util.ArrayList;
import core.GameType;
import core.ServerConstants;
import game.content.skilling.summoning.Summoning;
import game.entity.Entity;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import utility.Misc;

/**
 * Handles summoning pets
 *
 * @author 2012
 */
public class SummoningPetManager {

	/**
	 * The growth rate
	 */
	private static final int GROWTH_RATE = 20;

	/**
	 * The growing percentage
	 */
	private static final double GROWTH_PERCENTAGE = 0.20;

	/**
	 * The rate of hunger
	 */
	private static final int HUNGER_RATE = 100;

	/**
	 * The chance of random shouts
	 */
	private static final int RANDOM_SHOUT = 10;

	/**
	 * THe percentage at which the next stage of growth happens
	 */
	private static final int NEXT_STAGE_GROWTH = 70;

	/**
	 * The dog food
	 */
	public static final int[] DOG_FOOD = {2132, 2136, 2138, 526};

	/**
	 * The raw fish
	 */
	public static final int[] RAW_FISH = {317, 321, 335, 331, 359, 377, 371,};

	/**
	 * The forced chat when dismissing a pet
	 */
	public static final String FREE_PET = "Run along; I'm setting you free.";

	/**
	 * The owned pets
	 */
	private ArrayList<GrowingPet> pets = new ArrayList<GrowingPet>();

	/**
	 * The current spawned pet
	 */
	private Npc pet;

	/**
	 * Spawning the pet
	 * 
	 * @param player the player
	 * @param id the item id
	 */
	public static boolean spawn(Player player, int id) {
		/*
		 * Wrong game type
		 */
		if (!GameType.isPreEoc()) {
			return false;
		}
		/*
		 * Already spawned
		 */
		if (player.getSummoningPet().getPet() != null) {
			return false;
		}
		/*
		 * The pet
		 */
		SummoningPet pet = SummoningPet.forItem(id);
		/*
		 * Invalid pet
		 */
		if (pet == null) {
			return false;
		}
		/*
		 * Checks the levels required
		 */
		for (int i = 0; i < pet.getSkillsRequired().length; i++) {
			/*
			 * Wrong level
			 */
			if (player.baseSkillLevel[pet.getSkillsRequired()[i][0]] < pet.getSkillsRequired()[i][1]) {
				player.getDH()
						.sendStatement("You need a "
								+ ServerConstants.SKILL_NAME[pet.getSkillsRequired()[i][0]] + " level of "
								+ pet.getSkillsRequired()[i][1] + " to spawn this pet.");
				return true;
			}
		}
		/*
		 * The npc id
		 */
		int npcId = SummoningPet.getNpc(id);
		/*
		 * Invalid npc
		 */
		if (npcId == -1) {
			return false;
		}
		/*
		 * The npc
		 */
		Npc npc = Pet.summonNpcOnValidTile(player, npcId, false);
		/*
		 * Invalid npc
		 */
		if (npc == null) {
			return false;
		}
		/*
		 * The growing pet
		 */
		GrowingPet growingPet = getPet(player, id);
		/*
		 * No pet exists
		 */
		if (growingPet == null) {
			player.getSummoningPet().getPets().add(new GrowingPet(id));
		}
		/*
		 * Sets the pet
		 */
		player.getSummoningPet().setPet(npc);
		Summoning.sendPetInterface(player, npcId);
		player.startAnimation(827);
		/*
		 * Spawns
		 */
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				/*
				 * Hunger
				 */
				if (container.getExecutions() % HUNGER_RATE == 1) {
					hunger(player, id, GROWTH_PERCENTAGE * 10);
				}
				/*
				 * Growth
				 */
				if (container.getExecutions() % GROWTH_RATE == 1) {
					grow(player, npcId, pet);
				}
				/*
				 * Random shout
				 */
				if (Misc.random(RANDOM_SHOUT) == 1) {
					npc.forceChat(Misc.random(pet.getShouts()));
				}
			}

			@Override
			public void stop() {

			}
		}, 5);
		return true;
	}

	/**
	 * Feeding your pet
	 * 
	 * @param player the player
	 * @param npc the npc
	 * @param food the food
	 */
	public static boolean feed(Player player, Npc npc, int food) {
		/*
		 * Wrong game type
		 */
		if (!GameType.isPreEoc()) {
			return false;
		}
		/*
		 * The pet
		 */
		SummoningPet pet = SummoningPet.forNpc(npc.npcType);
		/*
		 * Invalid pet
		 */
		if (pet == null) {
			return false;
		}
		/*
		 * Not owner
		 */
		if (npc.getNpcPetOwnerId() != player.getPlayerId()) {
			player.getPA().sendMessage("This isn't your pet.");
			npc.forceChat(pet.getRejectFood() + " " + player.getPlayerName());
			return true;
		}
		/*
		 * Message
		 */
		player.getPA().sendMessage("You attempt to feed your pet..");
		/*
		 * Wrong food
		 */
		for (int foods : pet.getFood()) {
			if (foods == food) {
				eat(player, npc.npcType, 1);
				npc.forceChat(pet.getAcceptFood());
				ItemAssistant.deleteItemFromInventory(player, food, 1);
				player.getPA().sendMessage("Your pet likes and accepts the food.");
				return true;
			}
		}
		/*
		 * Rejects other food
		 */
		npc.forceChat(pet.getRejectFood());
		player.getPA().sendMessage("Your pet dislikes and rejects the food.");
		return true;
	}

	/**
	 * The pet growing
	 * 
	 * @param player the player
	 * @param id the id
	 * @param summoningPet the summoning pet
	 */
	private static void grow(Player player, int id, SummoningPet summoningPet) {
		/*
		 * The growing pet
		 */
		GrowingPet growingPet = getPet(player, id);
		/*
		 * No pet exists
		 */
		if (growingPet == null) {
			player.getSummoningPet().getPets().add(new GrowingPet(id));
		}
		/*
		 * Grows
		 */
		grow(player, id, GROWTH_PERCENTAGE);
		/*
		 * The next stage
		 */
		int nextStage = SummoningPet.getNextStage(id);
		/*
		 * No next stage
		 */
		if (nextStage == -1) {
			return;
		}
		/*
		 * Grown into next stage
		 */
		if (growingPet.getGrowth() == NEXT_STAGE_GROWTH) {
			/*
			 * Valid existing spawned pet
			 */
			if (player.getSummoningPet().getPet() != null) {
				/*
				 * The spawned pet
				 */
				Npc npc = player.getSummoningPet().getPet();
				/*
				 * Transform pet
				 */
				npc.transform(nextStage);
				player.getPA().sendMessage("Your pet has grown!");
				growingPet.setId(nextStage);
			}
		}
	}

	/**
	 * Gets the pet
	 * 
	 * @param player the player
	 * @param id the id
	 * @return the pet
	 */
	public static GrowingPet getPet(Player player, int id) {
		return player.getSummoningPet().getPets().stream().filter(c -> c.getId() == id).findFirst()
				.orElse(null);
	}

	/**
	 * The pet growing
	 * 
	 * @param player the player
	 * @param id the id
	 * @param growth the growth
	 */
	private static void grow(Player player, int id, double growth) {
		player.getSummoningPet().getPets().stream().filter(c -> c.getId() == id)
				.forEach(pet -> pet.setGrowth(pet.getGrowth() + growth));
		Summoning.sendPetInterface(player, id);
	}

	/**
	 * The pet getting hungry
	 * 
	 * @param player the player
	 * @param id the id
	 * @param hunger the hunger
	 */
	private static void hunger(Player player, int id, double hunger) {
		player.getSummoningPet().getPets().stream().filter(c -> c.getId() == id)
				.forEach(pet -> pet.setHunger(pet.getHunger() + hunger));
		Summoning.sendPetInterface(player, id);
	}

	/**
	 * The pet getting hungry
	 * 
	 * @param player the player
	 * @param id the id
	 * @param hunger the hunger
	 */
	private static void eat(Player player, int id, double hunger) {
		player.getSummoningPet().getPets().stream().filter(c -> c.getId() == id)
				.forEach(pet -> pet.setHunger(pet.getHunger() - hunger));
		Summoning.sendPetInterface(player, id);
	}

	/**
	 * Gets the pets
	 *
	 * @return the pets
	 */
	public ArrayList<GrowingPet> getPets() {
		return pets;
	}

	/**
	 * Gets the pet
	 *
	 * @return the pet
	 */
	public Npc getPet() {
		return pet;
	}

	/**
	 * Sets the pet
	 * 
	 * @param pet the pet
	 */
	public void setPet(Npc pet) {
		this.pet = pet;
	}
}
