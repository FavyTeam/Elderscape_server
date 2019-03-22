package game.content.skilling.summoning.pet.impl;

import java.util.ArrayList;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.skilling.summoning.pet.PetDialogue;
import game.npc.data.NpcDefinition;

/**
 * Handles the minitrice
 * 
 * @author 2012
 *
 */
public class Minitrice implements PetDialogue {

	/**
	 * The npc id
	 */
	private static final int MINITRICE = 0;

	@Override
	public ArrayList<DialogueChain> getDialogue() {
		/*
		 * The npc definition
		 */
		NpcDefinition npc = NpcDefinition.DEFINITIONS[MINITRICE];
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
		chain.add(new DialogueChain().npc(npc, FacialAnimation.CALM_1, "You deaded me!")
				.player(FacialAnimation.CALM_1, "Okay, okay, let's leave all that in the past.")
				.npc(npc, FacialAnimation.CALM_1, "But you deaded me!")
				.player(FacialAnimation.CALM_1, "It was an accident.")
				.npc(npc, FacialAnimation.CALM_1, "No accident; you deaded me on purpose!")
				.player(FacialAnimation.CALM_1, "Uh-huh. What can I do to make up for it?")
				.npc(npc, FacialAnimation.CALM_1, "Nothing. I get payback, I'll dead you!")
				.player(FacialAnimation.CALM_1, "Riiight. You're just a baby. Give it your best shot.")
				.npc(npc, FacialAnimation.CALM_1, "**Stares hard**")
				.player(FacialAnimation.CALM_1, "...").npc(npc, FacialAnimation.CALM_1, "...")
				.player(FacialAnimation.CALM_1, "Oh, no! My eyes are burning!")
				.npc(npc, FacialAnimation.CALM_1, "Mwa-ha-ha! Me told you I dead you!")
				.player(FacialAnimation.CALM_1, "And I told you that you're just a baby.",
						"I can't believe you fell for that!"));
		chain.add(new DialogueChain().npc(npc, FacialAnimation.CALM_1, "Me kill you.")
				.player(FacialAnimation.CALM_1, "Yes, yes, I know.")
				.npc(npc, FacialAnimation.CALM_1,
						"Maybe not today, maybe not tomorrow, but me kill you.")
				.player(FacialAnimation.CALM_1, "I await the day.")
				.npc(npc, FacialAnimation.CALM_1,
						"We understand each other, hooman. Now, when's dinner time?")
				.player(FacialAnimation.CALM_1, "You're very hard to love, you know that?"));
		return chain;
	}
}
