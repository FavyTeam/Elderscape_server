package game.content.skilling.summoning.pet.impl;

import java.util.ArrayList;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.skilling.summoning.pet.PetDialogue;
import game.npc.data.NpcDefinition;

/**
 * Handles baby basilisk
 * 
 * @author user
 *
 */
public class BabyBasilisk implements PetDialogue {

	/**
	 * The npc id
	 */
	private static final int BABY_BASILISK = 0;

	@Override
	public ArrayList<DialogueChain> getDialogue() {
		/*
		 * The npc definition
		 */
		NpcDefinition npc = NpcDefinition.DEFINITIONS[BABY_BASILISK];
		/*
		 * The chain
		 */
		ArrayList<DialogueChain> chain = new ArrayList<>();
		/*
		 * The dialogues
		 */
		chain.add(new DialogueChain().player(FacialAnimation.CALM_1, "Hey there.")
				.npc(npc, FacialAnimation.CALM_1, "Why do my eyes sting?")
				.player(FacialAnimation.CALM_1,
						"Ah, you're only little, but someone should probably tell you...")
				.npc(npc, FacialAnimation.CALM_1, "Tell me what?")
				.player(FacialAnimation.CALM_1, "Well, when you grow up, your eyes will have ",
						"the power to weaken and kill people.")
				.npc(npc, FacialAnimation.CALM_1, "...").player(FacialAnimation.CALM_1, "...")
				.npc(npc, FacialAnimation.CALM_1, "Cool!"));
		chain.add(new DialogueChain().npc(npc, FacialAnimation.CALM_1, "So, how did I come to be?")
				.player(FacialAnimation.CALM_1, "Is this the birds and the bees question?")
				.npc(npc, FacialAnimation.CALM_1, "I have no idea. Yes?")
				.player(FacialAnimation.CALM_1, "Well, you began life as a serpent egg",
						"that got hatched by a chicken.")
				.npc(npc, FacialAnimation.CALM_1, "But which came first, the chicken or the egg?")
				.player(FacialAnimation.CALM_1, "Good question. I'll get back to you."));
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
