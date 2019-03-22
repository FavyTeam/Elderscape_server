package game.content.item.impl;

import core.ServerConstants;
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
 * Handles the rapid renewal scroll
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {18_343}),})
public class RapidRenewalScroll implements ItemInteraction {

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Integer> SCROLL_UNLOCKED =
			new PermanentAttributeKey<Integer>(0, "rapid-renewal");

	/**
	 * Reading the scroll
	 * 
	 * @param player the player
	 */
	private static void read(Player player) {
		/*
		 * Whether unlocked
		 */
		boolean unlocked = player.getAttributes().getOrDefault(SCROLL_UNLOCKED) == 1;
		/*
		 * Already unlocked
		 */
		if (unlocked) {
			player.getPA().sendMessage("You cannot absorb anymore knowledge.");
			return;
		}
		/*
		 * Wrong prayer level
		 */
		if (player.baseSkillLevel[ServerConstants.PRAYER] < 65) {
			player.getPA().sendMessage("You need a Prayer level of 65 to read this scroll.");
			return;
		}
		/*
		 * The dialogue
		 */
		DialogueChain chain = new DialogueChain()
				.statement("Absorbing this scroll, you will learn the ability to heal",
						"yourself faster.")
				.option((p, option) -> {

					if (option == 1) {
						ItemAssistant.deleteItemFromInventory(player, 18_343, 1);
						player.getPA().sendFrame36(1073, 1, false);
						player.getAttributes().put(SCROLL_UNLOCKED, 1);
						player.getPA().sendMessage(
								"You read the scroll and unlock the ability to use the Rapid Renewal prayer.");
					}

					player.getPA().closeInterfaces(true);
				}, "Absorb the Knowledge?", "Yes", "No");

		player.setDialogueChain(chain).start(player);
	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		if (type == 1) {
			if (id == 18_343) {
				read(player);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		return false;
	}

	@Override
	public void operate(Player player, int id) {

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
