package game.content.skilling.summoning.pet.impl;

import java.util.ArrayList;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.skilling.summoning.pet.PetDialogue;
import game.npc.data.NpcDefinition;

/**
 * Handles the abyssal minion
 * 
 * @author 2012
 *
 */
public class AbyssalMinion implements PetDialogue {

	/**
	 * The npc id
	 */
	private static final int ABYSSAL_MINION = 0;

	@Override
	public ArrayList<DialogueChain> getDialogue() {
		/*
		 * The npc definition
		 */
		NpcDefinition npc = NpcDefinition.DEFINITIONS[ABYSSAL_MINION];
		/*
		 * The chain
		 */
		ArrayList<DialogueChain> chain = new ArrayList<>();
		/*
		 * The dialogues
		 */
		chain.add(new DialogueChain()
				.npc(npc, FacialAnimation.CALM_1, "You know what I miss? I miss THE ABYSS")
				.player(FacialAnimation.CALM_1, "That rhymes, you know.")
				.npc(npc, FacialAnimation.CALM_1,
						"...the warm, slippery walls, the piercing screams...")
				.player(FacialAnimation.CALM_1, "How lovely.")
				.npc(npc, FacialAnimation.CALM_1, "...the chasing after runecrafters...")
				.player(FacialAnimation.CALM_1, "We could still chase after runecrafters, you know.")
				.npc(npc, FacialAnimation.CALM_1, "Human, nothing would make me happier."));

		chain.add(new DialogueChain()
				.npc(npc, FacialAnimation.CALM_1, "I am confused. I thought you killed me?")
				.player(FacialAnimation.CALM_1,
						"Lucky for you, I found a place to bring you back to life.")
				.npc(npc, FacialAnimation.CALM_1, "This makes me happy. Maybe we can go to THE ABYSS.")
				.player(FacialAnimation.CALM_1, "As fun as that sounds, why don't you give",
						"me an abyssal whip instead? I saved your life."));

		chain.add(new DialogueChain().player(FacialAnimation.CALM_1, "Hello there, little creature.")
				.npc(npc, FacialAnimation.CALM_1, "Mum!")
				.player(FacialAnimation.CALM_1, "Er, no, I'm not your mother.")
				.npc(npc, FacialAnimation.CALM_1, "Not mum?")
				.player(FacialAnimation.CALM_1,
						"Well, no, but I will be taking care of you... I suppose.")
				.npc(npc, FacialAnimation.CALM_1, "Mum!"));
		return chain;
	}
}
