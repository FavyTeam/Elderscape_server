package game.content.skilling.summoning.pet.impl;

import java.util.ArrayList;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.skilling.summoning.pet.PetDialogue;
import game.npc.data.NpcDefinition;

/**
 * Handles the baby kurask
 * 
 * @author 2012
 *
 */
public class BabyKurask implements PetDialogue {

	/**
	 * The npc id
	 */
	private static final int BABY_KURASK = 0;

	@Override
	public ArrayList<DialogueChain> getDialogue() {
		/*
		 * The npc definition
		 */
		NpcDefinition npc = NpcDefinition.DEFINITIONS[BABY_KURASK];
		/*
		 * The chain
		 */
		ArrayList<DialogueChain> chain = new ArrayList<>();
		/*
		 * The dialogues
		 */
		chain.add(new DialogueChain().npc(npc, FacialAnimation.CALM_1, "Ughhh.")
				.player(FacialAnimation.CALM_1,
						"What are you trying to say? Are you in pain?")
				.npc(npc, FacialAnimation.CALM_1, "Eeeurgh.")
				.player(FacialAnimation.CALM_1, "Hungry? Thirsty?")
				.npc(npc, FacialAnimation.CALM_1, "Baarughm.")
				.player(FacialAnimation.CALM_1, "Sleepy?.")
				.npc(npc, FacialAnimation.CALM_1, "Aaghhaargh.")
				.player(FacialAnimation.CALM_1, "I give up."));

		chain.add(new DialogueChain().npc(npc, FacialAnimation.CALM_1, "Uhhhhrrr.")
				.player(FacialAnimation.CALM_1, "Hey, kurask, what do you think of the",
						"current state of the RuneScape economy?")
				.npc(npc, FacialAnimation.CALM_1, "Blerughh, boring.")
				.player(FacialAnimation.CALM_1, "Wow, really? What are your thoughts",
						"on the melting ice on Ice Mountain?")
				.npc(npc, FacialAnimation.CALM_1, "Eurggh, warm.")
				.player(FacialAnimation.CALM_1, "A fascinating response. Anything else?")
				.npc(npc, FacialAnimation.CALM_1, "...")
				.player(FacialAnimation.CALM_1, "Well, that was a thrilling conversation."));

		chain.add(new DialogueChain()
				.player(FacialAnimation.CALM_1,
						"I spy with my little eye, something beginning with 'K'.")
				.npc(npc, FacialAnimation.CALM_1, "krrgghh...knave?")
				.player(FacialAnimation.CALM_1, "No.")
				.npc(npc, FacialAnimation.CALM_1, "Kuennnag...knucklehead?")
				.player(FacialAnimation.CALM_1,
						"Nope.")
				.npc(npc, FacialAnimation.CALM_1, "Knnnnnggg...kumquat?")
				.player(FacialAnimation.CALM_1, "No, it's kurask. Honestly, that wasn't difficult."));
		return chain;
	}
}
