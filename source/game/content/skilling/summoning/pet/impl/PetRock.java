package game.content.skilling.summoning.pet.impl;

import core.GameType;
import game.content.dialogue.DialogueChain;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.npc.Npc;
import game.player.Player;
import game.type.GameTypeIdentity;

@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {3695}),})
/**
 * Handles the pet rock pet
 * 
 * @author 2012
 *
 */
public class PetRock implements ItemInteraction {
	// anim 2161 - Pet Red Pet Rock
	// anim 2162 - Pet Green Pet Rock
	// anim 2206 - Pet Blue Pet Rock
	// anim 2213 - Pet Yellow Pet Rock

	// anim 2214 - Pet Rock Fetch
	// anim 2215 - Green Pet Rock Fetch
	// anim 2216 - Blue Pet Rock Fetch
	// anim 2217 - Yellow Pet Rock Fetch
	// anim 6664 - Pet﻿ rock fetch
	//
	// 1319 - stand
	// 1320 - stroke
	// 1323 - walk
	// 1324 - walkback
	// 1325 - walk right
	// 1326 - walk left
	// 1333 - stroke

	// 1331 - put down
	// 1332 - pickup


	/**
	 * Sending the interact dialogue
	 * 
	 * @param player the player
	 */
	private static void sendInteractOption(Player player) {
		/*
		 * The dialogue
		 */
		player.setDialogueChain(new DialogueChain().option((p, option) -> {
			/*
			 * Close interface
			 */
			player.getPA().closeInterfaces(true);
			if (option == 1) {
				player.getPA().sendMessage("You try and feed the rock..");
				player.getPA().sendDelayedMessage("Your rock doesn't seem hungry", 2);
			} else if (option == 2) {
				player.getPA().sendMessage("You stroke your pet rock");
				player.getPA().sendDelayedMessage("Your rock seems much happier", 2);
			}
		}, "Select an Option", "Feed", "Stroke", "Talk-to", "Fetch", "Stay")).start(player);
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		return true;
	}

	@Override
	public void operate(Player player, int id) {
		if (id == 3695) {
			sendInteractOption(player);
		}
	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		if (type == 1 && id == 3695) {
			sendInteractOption(player);
			return true;
		}
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
