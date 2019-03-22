package game.content.skilling.summoning.pet.impl;

import java.util.ArrayList;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.skilling.summoning.pet.PetDialogue;
import game.npc.data.NpcDefinition;
import game.player.Player;

/**
 * Handles pet phoenix
 * 
 * @author 2012
 *
 */
public class PhoenixEggling implements PetDialogue {

	/**
	 * The npc id
	 */
	private static final int MEAN_PHOENIX = 0;

	private static final int PHOENIX_HATCHLING = 0;

	public static void hatchEgg(Player player) {
		/*
		 * The npc definition
		 */
		NpcDefinition SPARKLES = NpcDefinition.DEFINITIONS[PHOENIX_HATCHLING];

		player.setDialogueChain(new DialogueChain()
				.npc(SPARKLES, FacialAnimation.CALM_1, "Ssssssalutations Player, how can I help?")
				.option((p, option) -> {
					if (option == 1) {
						player.setDialogueChain(new DialogueChain()
								.player(FacialAnimation.CALM_1, "Where did you get your hat?")
								.npc(SPARKLES, FacialAnimation.CALM_1, "Sssstole it from an old man.")
								.player(FacialAnimation.CALM_1, "You stole from Santa!")
								.npc(SPARKLES, FacialAnimation.CALM_1, "Who's Sssssssssanta?")
								.player(FacialAnimation.CALM_1,
										"Santa is a kind old man who gives presents",
										"to everyone all over the world.")
								.npc(SPARKLES, FacialAnimation.CALM_1, "He ssssssssssounds nice.")
								.player(FacialAnimation.CALM_1,
										"Santa is a kind old man who gives presents",
										"He is, and you stole from him.")
								.npc(SPARKLES, FacialAnimation.CALM_1, "Thissssss can be my pressssssent!")
								.player(FacialAnimation.CALM_1, "It doesn't work that way, you don't get",
										"to choose your present.")
								.npc(SPARKLES, FacialAnimation.CALM_1,
										"Sssssssso, I ssssssshould give this hat back?"))
								.start(player);
					}


				}, "Select an Option", "Where did you get your hat?", "What do you eat?", "Hey snakey!",
						"Do you want to play a game?", "Nothing"))
				.start(player);
	}

	@Override
	public ArrayList<DialogueChain> getDialogue() {
		/*
		 * The npc definition
		 */
		NpcDefinition npc = NpcDefinition.DEFINITIONS[MEAN_PHOENIX];
		/*
		 * The chain
		 */
		ArrayList<DialogueChain> chain = new ArrayList<>();
		/*
		 * The dialogues
		 */
		chain.add(new DialogueChain().player(FacialAnimation.CALM_1, "What are you, exactly?")
				.npc(npc, FacialAnimation.CALM_1, "I minitrice.")
				.player(FacialAnimation.CALM_1, "And what's a minitrice?")
				.npc(npc, FacialAnimation.CALM_1, "Fearsome creature that kill with single glance!")
				.player(FacialAnimation.CALM_1,
						"Is that so? Then why do you look like a feathered frog?")
				.npc(npc, FacialAnimation.CALM_1, "..."));
		return chain;
	}

}
