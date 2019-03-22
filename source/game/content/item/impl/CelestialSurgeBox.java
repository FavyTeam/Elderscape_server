package game.content.item.impl;

import core.GameType;
import game.content.dialogue.DialogueChain;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.PermanentAttributeKey;
import game.entity.attributes.PermanentAttributeKeyComponent;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.player.Player;
import game.type.GameTypeIdentity;

/**
 * Handles the celestial surge box
 * 
 * @author 2012
 *
 */


@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = 19_889),})
public class CelestialSurgeBox implements ItemInteraction {

	/**
	 * The celestial surge box
	 */
	public static final int CELESTIAL_SURGE_BOX = 19_889;

	/**
	 * The air rune
	 */
	private static final int AIR_RUNE = 556;

	/**
	 * The blood rune
	 */
	private static final int BLOOD_RUNE = 565;

	/**
	 * The death rune
	 */
	private static final int DEATH_RUNE = 560;

	@PermanentAttributeKeyComponent
	public static final AttributeKey<String> CELESTIAL_MODE =
			new PermanentAttributeKey<String>("wave", "c-wave");

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Integer> CELESTIAL_AIR_RUNE =
			new PermanentAttributeKey<Integer>(0, "c-air-rune");

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Integer> CELESTIAL_BLOOD_RUNE =
			new PermanentAttributeKey<Integer>(0, "c-blood-rune");

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Integer> CELESTIAL_DEATH_RUNE =
			new PermanentAttributeKey<Integer>(0, "c-death-rune");

	/**
	 * Checking the charges
	 * 
	 * @param player the player
	 */
	private static void check(Player player) {
		/*
		 * The air runes
		 */
		int air = player.getAttributes().getOrDefault(CELESTIAL_AIR_RUNE);
		/*
		 * The blood runes
		 */
		int blood = player.getAttributes().getOrDefault(CELESTIAL_BLOOD_RUNE);
		/*
		 * The death rune
		 */
		int death = player.getAttributes().getOrDefault(CELESTIAL_DEATH_RUNE);
		player.getPA().sendMessage("Air: " + air + ", blood: " + blood + ", death: " + death);
	}

	/**
	 * Emptying the surgebox
	 * 
	 * @param player the player
	 */
	private static void empty(Player player) {
		/*
		 * The air runes
		 */
		int air = player.getAttributes().getOrDefault(CELESTIAL_AIR_RUNE);
		/*
		 * The blood runes
		 */
		int blood = player.getAttributes().getOrDefault(CELESTIAL_BLOOD_RUNE);
		/*
		 * The death rune
		 */
		int death = player.getAttributes().getOrDefault(CELESTIAL_DEATH_RUNE);
		/*
		 * The air runes
		 */
		if (air > 0 && ItemAssistant.canAdd(player, AIR_RUNE, air)) {
			ItemAssistant.addItem(player, AIR_RUNE, air);
			player.getAttributes().put(CELESTIAL_AIR_RUNE, 0);
		}
		/*
		 * The blood runes
		 */
		if (blood > 0 && ItemAssistant.canAdd(player, BLOOD_RUNE, blood)) {
			ItemAssistant.addItem(player, BLOOD_RUNE, blood);
			player.getAttributes().put(CELESTIAL_BLOOD_RUNE, 0);
		}
		/*
		 * The death runes
		 */
		if (death > 0 && ItemAssistant.canAdd(player, DEATH_RUNE, death)) {
			ItemAssistant.addItem(player, DEATH_RUNE, death);
			player.getAttributes().put(CELESTIAL_DEATH_RUNE, 0);
		}
	}

	/**
	 * Charging the celestial box
	 * 
	 * @param player the player
	 */
	private static void charge(Player player) {
		/*
		 * The mode
		 */
		String mode = player.getAttributes().getOrDefault(CELESTIAL_MODE);
		/*
		 * The air runes
		 */
		int air = player.getAttributes().getOrDefault(CELESTIAL_AIR_RUNE);
		/*
		 * The blood runes
		 */
		int blood = player.getAttributes().getOrDefault(CELESTIAL_BLOOD_RUNE);
		/*
		 * The death rune
		 */
		int death = player.getAttributes().getOrDefault(CELESTIAL_DEATH_RUNE);
		/*
		 * Charging wave
		 */
		if (mode.equalsIgnoreCase("wave")) {
			/*
			 * The air runes available
			 */
			int airAvailable = ItemAssistant.getItemAmount(player, AIR_RUNE) / 5;
			/*
			 * The blood runes available
			 */
			int bloodAvailable = ItemAssistant.getItemAmount(player, BLOOD_RUNE);
			/*
			 * Fixes amount
			 */
			if (airAvailable < bloodAvailable) {
				bloodAvailable = airAvailable;
			}
			/*
			 * No runes
			 */
			if (airAvailable == 0 || bloodAvailable == 0) {
				player.getPA()
						.sendMessage("You need a both air and blood runes to charge the surgebox.");
				return;
			}
			/*
			 * Deletes item
			 */
			ItemAssistant.deleteItemFromInventory(player, AIR_RUNE, airAvailable * 5);
			ItemAssistant.deleteItemFromInventory(player, BLOOD_RUNE, bloodAvailable);
			/*
			 * Notify
			 */
			player.getPA().sendMessage("Added: " + bloodAvailable + " total casts. Air: "
					+ (airAvailable * 5) + ", Blood: " + bloodAvailable);
			/*
			 * Increases casts
			 */
			player.getAttributes().put(CELESTIAL_AIR_RUNE, (air + (airAvailable * 5)));
			player.getAttributes().put(CELESTIAL_BLOOD_RUNE, (blood + bloodAvailable));
		} else {
			/*
			 * The air runes available
			 */
			int airAvailable = ItemAssistant.getItemAmount(player, AIR_RUNE) / 7;
			/*
			 * The blood runes available
			 */
			int bloodAvailable = ItemAssistant.getItemAmount(player, BLOOD_RUNE);
			/*
			 * The death runes available
			 */
			int deathAvailable = ItemAssistant.getItemAmount(player, DEATH_RUNE);
			/*
			 * Fixes amount blood
			 */
			if (airAvailable < bloodAvailable) {
				bloodAvailable = airAvailable;
			}
			/*
			 * Fixes amount death
			 */
			if (airAvailable < deathAvailable) {
				deathAvailable = airAvailable;
			}
			/*
			 * Fixes amount death to blood
			 */
			if (deathAvailable > bloodAvailable) {
				deathAvailable = bloodAvailable;
			}
			/*
			 * Fixes amount blood to death
			 */
			if (bloodAvailable > deathAvailable) {
				bloodAvailable = deathAvailable;
			}
			/*
			 * No runes
			 */
			if (airAvailable == 0 || bloodAvailable == 0 || deathAvailable == 0) {
				player.getPA().sendMessage(
						"You need a both air, blood, and death runes to charge the surgebox.");
				return;
			}
			/*
			 * Deletes item
			 */
			ItemAssistant.deleteItemFromInventory(player, AIR_RUNE, airAvailable * 7);
			ItemAssistant.deleteItemFromInventory(player, BLOOD_RUNE, bloodAvailable);
			ItemAssistant.deleteItemFromInventory(player, DEATH_RUNE, deathAvailable);
			/*
			 * Notify
			 */
			player.getPA().sendMessage("Added: " + bloodAvailable + " total casts. Air: "
					+ (airAvailable * 5) + ", Death: " + deathAvailable + ", Blood: " + bloodAvailable);
			/*
			 * Increases casts
			 */
			player.getAttributes().put(CELESTIAL_AIR_RUNE, (air + (airAvailable * 7)));
			player.getAttributes().put(CELESTIAL_BLOOD_RUNE, (blood + bloodAvailable));
			player.getAttributes().put(CELESTIAL_DEATH_RUNE, (death + deathAvailable));
		}
	}

	/**
	 * Changing mode
	 * 
	 * @param player the player
	 */
	private static void changeMode(Player player) {
		/*
		 * The mode
		 */
		String mode = player.getAttributes().getOrDefault(CELESTIAL_MODE);
		/*
		 * Switch
		 */
		player.getAttributes().put(CELESTIAL_MODE, mode.equalsIgnoreCase("wave") ? "Surge" : "Wave");
		/*
		 * Notify
		 */
		player.getPA().sendMessage("You switch the surgebox type. It is now: "
				+ player.getAttributes().getOrDefault(CELESTIAL_MODE));
	}

	/**
	 * Gets the cast left
	 * 
	 * @param player the player
	 * @return the casts
	 */
	public static int getCastLeft(Player player) {
		/*
		 * The mode
		 */
		String mode = player.getAttributes().getOrDefault(CELESTIAL_MODE);
		/*
		 * Wave mode
		 */
		if (mode.equalsIgnoreCase("wave")) {
			/*
			 * The air runes available
			 */
			int airAvailable = player.getAttributes().getOrDefault(CELESTIAL_AIR_RUNE) / 5;
			/*
			 * The blood runes available
			 */
			int bloodAvailable = player.getAttributes().getOrDefault(CELESTIAL_BLOOD_RUNE);
			/*
			 * Fixes amount
			 */
			if (airAvailable < bloodAvailable) {
				bloodAvailable = airAvailable;
			}
			/*
			 * No runes
			 */
			if (airAvailable == 0 || bloodAvailable == 0) {
				return 0;
			}
			return bloodAvailable;
		} else {
			/*
			 * The air runes available
			 */
			int airAvailable = player.getAttributes().getOrDefault(CELESTIAL_AIR_RUNE) / 7;
			/*
			 * The blood runes available
			 */
			int bloodAvailable = player.getAttributes().getOrDefault(CELESTIAL_BLOOD_RUNE);
			/*
			 * The death runes available
			 */
			int deathAvailable = player.getAttributes().getOrDefault(CELESTIAL_DEATH_RUNE);
			/*
			 * Fixes amount blood
			 */
			if (airAvailable < bloodAvailable) {
				bloodAvailable = airAvailable;
			}
			/*
			 * Fixes amount death
			 */
			if (airAvailable < deathAvailable) {
				deathAvailable = airAvailable;
			}
			/*
			 * Fixes amount death to blood
			 */
			if (deathAvailable > bloodAvailable) {
				deathAvailable = bloodAvailable;
			}
			/*
			 * Fixes amount blood to death
			 */
			if (bloodAvailable > deathAvailable) {
				bloodAvailable = deathAvailable;
			}
			/*
			 * No runes
			 */
			if (airAvailable == 0 || bloodAvailable == 0 || deathAvailable == 0) {
				return 0;
			}
			return bloodAvailable;
		}
	}

	/**
	 * Draining the box
	 * 
	 * @param player the player
	 */
	public static void drain(Player player) {
		/*
		 * The mode
		 */
		String mode = player.getAttributes().getOrDefault(CELESTIAL_MODE);
		/*
		 * The air runes
		 */
		int air = player.getAttributes().getOrDefault(CELESTIAL_AIR_RUNE);
		/*
		 * The blood runes
		 */
		int blood = player.getAttributes().getOrDefault(CELESTIAL_BLOOD_RUNE);
		/*
		 * Wave mode
		 */
		if (mode.equalsIgnoreCase("wave")) {
			/*
			 * Deleterunes
			 */
			player.getAttributes().put(CELESTIAL_AIR_RUNE, (air - 5));
			player.getAttributes().put(CELESTIAL_BLOOD_RUNE, (blood - 1));
		} else {
			/*
			 * The death rune
			 */
			int death = player.getAttributes().getOrDefault(CELESTIAL_DEATH_RUNE);
			/*
			 * Delete runes
			 */
			player.getAttributes().put(CELESTIAL_AIR_RUNE, (air - 7));
			player.getAttributes().put(CELESTIAL_BLOOD_RUNE, (blood - 1));
			player.getAttributes().put(CELESTIAL_DEATH_RUNE, (death - 1));
		}
	}

	/**
	 * Sending the dialogue
	 * 
	 * @param player the player
	 */
	private static void sendCheckDialogue(Player player) {
		DialogueChain chain = new DialogueChain().option((p, option) -> {
			if (option == 1) {
				check(player);
			} else if (option == 2) {
				empty(player);
			}
			player.getPA().closeInterfaces(true);
		}, "Select an option", "Check", "Empty");

		player.setDialogueChain(chain).start(player);
	}

	/**
	 * Sending the dialogue
	 * 
	 * @param player the player
	 */
	private static void sendChargeDialogue(Player player) {
		DialogueChain chain = new DialogueChain().option((p, option) -> {
			if (option == 1) {
				charge(player);
			} else if (option == 2) {
				changeMode(player);
			}
			player.getPA().closeInterfaces(true);
		}, "Select an option", "Charge", "Change mode");

		player.setDialogueChain(chain).start(player);
	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		if (id == 19_889) {
			if (type == 2) {
				sendChargeDialogue(player);
				return true;
			} else if (type == 3) {
				sendCheckDialogue(player);
				return true;
			}
		}
		return false;
	}

	@Override
	public void operate(Player player, int id) {
		check(player);
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
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
