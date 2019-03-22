package game.content.item.impl;

import java.util.Arrays;
import core.ServerConstants;
import core.GameType;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.content.skilling.Skilling;
import game.npc.Npc;
import game.player.Player;
import game.type.GameTypeIdentity;

/**
 * Handles the milestone capes
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC,
		identity = {20754, 20755, 20756, 20757, 20758, 20759, 20760, 20761, 20762}),})
public class MilestoneCapes implements ItemInteraction {

	/**
	 * The milestone capes
	 */
	public enum MilestoneCape {
		MILESTONE_10(20754, 10),

		MILESTONE_20(20755, 20),

		MILESTONE_30(20756, 30),

		MILESTONE_40(20757, 40),

		MILESTONE_50(20758, 50),

		MILESTONE_60(20759, 60),

		MILESTONE_70(20760, 70),

		MILESTONE_80(20761, 80),

		MILESTONE_90(20762, 90),

		;

		/**
		 * The id
		 */
		private int id;

		/**
		 * The requirement
		 */
		private int requirement;

		/**
		 * Represenst a milestone cape
		 * 
		 * @param id the id
		 * @param requirement the requirement
		 */
		MilestoneCape(int id, int requirement) {
			this.id = id;
			this.requirement = requirement;
		}

		/**
		 * Gets the id
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the requirement
		 *
		 * @return the requirement
		 */
		public int getRequirement() {
			return requirement;
		}

		/**
		 * Gets by id
		 * 
		 * @param id the id
		 * @return the cape
		 */
		public static MilestoneCape forId(int id) {
			return Arrays.stream(values()).filter(c -> c.getId() == id).findFirst().orElse(null);
		}
	}

	/**
	 * Checking whether the cape can be equipped
	 * 
	 * @param player the player
	 * @param id the id
	 * @param slot the slot
	 * @return equip cape
	 */
	@Override
	public boolean canEquip(Player player, int id, int slot) {
		/*
		 * The cape
		 */
		MilestoneCape cape = MilestoneCape.forId(id);
		/*
		 * Invalid cape
		 */
		if (cape == null) {
			return true;
		}
		/*
		 * Checks the skills
		 */
		for (int i = 0; i < player.skillExperience.length; i++) {
			/*
			 * Below requirement
			 */
			if (Skilling.getLevelForExperience(player.skillExperience[i]) < cape.getRequirement()) {
				player.getPA().sendMessage(
						"You haven't reached the " + cape.getRequirement()
								+ " milestone in all skills. Your " + ServerConstants.SKILL_NAME[i]
								+ " is too low.");
				return false;
			}
		}
		return true;
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
		return false;
	}

	@Override
	public boolean useItemOnNpc(Player player, int id, Npc npc) {
		return false;
	}

	@Override
	public boolean dropItem(Player player, int id) {
		return false;
	}
}
