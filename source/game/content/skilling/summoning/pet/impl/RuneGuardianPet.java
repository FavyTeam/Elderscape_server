package game.content.skilling.summoning.pet.impl;

import java.util.Arrays;
import core.GameType;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.pet.Pet;
import game.player.Player;
import game.type.GameTypeIdentity;

@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {15483, 15626, 15627,
				15628, 15629, 15630, 15631, 15632, 15633, 15634, 15635, 15636, 15637, 15638, 15639}),})
/**
 * Handles rune guardian pet
 * 
 * @author 2012
 *
 */
public class RuneGuardianPet implements ItemInteraction {

	/**
	 * The player replying to rune guardian chat
	 */
	public static final String PLAYER_REPLY = "I thought it was penguins that played bongos.";

	/**
	 * The rune gaurdian pet
	 */
	public enum RuneGuardian {

		GREY(0, 9436, 15483),

		AIR(14897, 9642, 15626),

		MIND(14898, 9643, 15627),

		WATER(14899, 9644, 15628),

		EARTH(14900, 9645, 1529),

		FIRE(14901, 9646, 15630),

		BODY(14902, 9647, 15631),

		COSMIC(14903, 9648, 15632),

		CHAOS(14906, 9649, 15633),

		ASTRAL(14911, 9650, 15634),

		NATURE(14905, 9651, 15635),

		LAW(14904, 9652, 15636),

		DEATH(14907, 9653, 15637),

		BLOOD(27978, 9654, 15638),

		SOUL(27980, 9655, 15639),
		;

		/**
		 * The altar
		 */
		private int altar;

		/**
		 * The npc
		 */
		private int npc;

		/**
		 * The item
		 */
		private int item;

		/**
		 * Represents a rune guardian
		 * 
		 * @param altar the altar
		 * @param npc the npc
		 * @param item the item
		 */
		RuneGuardian(int altar, int npc, int item) {
			this.altar = altar;
			this.npc = npc;
			this.item = item;
		}

		/**
		 * Gets the altar
		 *
		 * @return the altar
		 */
		public int getAltar() {
			return altar;
		}

		/**
		 * Gets the npc
		 *
		 * @return the npc
		 */
		public int getNpc() {
			return npc;
		}

		/**
		 * Gets the item
		 *
		 * @return the item
		 */
		public int getItem() {
			return item;
		}

		/**
		 * Gets the rune guardian for altar
		 * 
		 * @param altar the altar
		 * @return the rune guardian
		 */
		public static RuneGuardian forAltar(int altar) {
			return Arrays.stream(values()).filter(c -> c.getAltar() == altar).findFirst().orElse(null);
		}

		/**
		 * Gets the rune guardian for item
		 * 
		 * @param item the item
		 * @return the rune guardian
		 */
		public static RuneGuardian forItem(int item) {
			return Arrays.stream(values()).filter(c -> c.getItem() == item).findFirst().orElse(null);
		}
	}

	/**
	 * Transforming the guardian
	 * 
	 * @param player the player
	 * @param id the id
	 * @param object the object
	 */
	private boolean transformGuardian(Player player, int id, int object) {
		/*
		 * The altar
		 */
		RuneGuardian altar = RuneGuardian.forAltar(object);
		/*
		 * Invalid altar
		 */
		if (altar == null) {
			return false;
		}
		/*
		 * The item
		 */
		RuneGuardian item = RuneGuardian.forItem(id);
		/*
		 * Invalid item
		 */
		if (item == null) {
			return false;
		}
		/*
		 * Already the same
		 */
		if (altar.getItem() == item.getItem()) {
			player.getPA().sendMessage("Your Rune Guardian is already set to this altar.");
			return true;
		}
		/*
		 * Transform
		 */
		ItemAssistant.deleteItemFromInventory(player, item.getItem(), 1);
		ItemAssistant.addItem(player, altar.getItem(), 1);
		player.getPA().sendMessage(
				"You hold the rune guardian to the altar and feel the magic changing its essence");
		return true;
	}

	/**
	 * Spawning the guardian
	 * 
	 * @param player the player
	 * @param id the id
	 * @return spawning
	 */
	private boolean spawn(Player player, int id) {
		/*
		 * The guardian
		 */
		RuneGuardian guardian = RuneGuardian.forItem(id);
		/*
		 * Invalid
		 */
		if (guardian == null) {
			return false;
		}
		/*
		 * Spawn
		 */
		ItemAssistant.deleteItemFromInventory(player, id, 1);
		Pet.summonNpcOnValidTile(player, guardian.getNpc(), false);
		return true;
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		return false;
	}

	@Override
	public void operate(Player player, int id) {

	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		return false;
	}

	@Override
	public boolean useItem(Player player, int id, int useWith) {
		return false;
	}

	@Override
	public boolean useItemOnObject(Player player, int id, int object) {
		return transformGuardian(player, id, object);
	}

	@Override
	public boolean useItemOnNpc(Player player, int id, Npc npc) {
		return false;
	}

	@Override
	public boolean dropItem(Player player, int id) {
		return spawn(player, id);
	}
}