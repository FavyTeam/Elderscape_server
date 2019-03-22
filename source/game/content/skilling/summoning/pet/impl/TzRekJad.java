package game.content.skilling.summoning.pet.impl;

import java.util.ArrayList;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.skilling.summoning.pet.PetDialogue;
import game.npc.data.NpcDefinition;

/**
 * Handles the jad pet
 * 
 * @author 2012
 *
 */
public class TzRekJad implements PetDialogue {
	/**
	 * The npc id
	 */
	private static final int TZREK_JAD = 0;

	@Override
	public ArrayList<DialogueChain> getDialogue() {
		/*
		 * The npc definition
		 */
		NpcDefinition npc = NpcDefinition.DEFINITIONS[TZREK_JAD];
		/*
		 * The chain
		 */
		ArrayList<DialogueChain> chain = new ArrayList<>();
		/*
		 * The dialogues
		 */
		chain.add(new DialogueChain()
				.player(FacialAnimation.CALM_1, "You're not quite as scary when you're this size.")
				.npc(npc, FacialAnimation.CALM_1, "Do not mock me, human.")
				.player(FacialAnimation.CALM_1,
						"Aww, so cute.")
				.npc(npc, FacialAnimation.CALM_1, "I will rend your flesh from your bones",
						"and feast on your suffering!")
				.player(FacialAnimation.CALM_1, "Bad Jad! Bad! In your box! No chew toy for you!")
				.npc(npc, FacialAnimation.CALM_1, "No! Tiny human, I beg you...do not take",
						"away Mr Squeekles. He needs me.")
				.player(FacialAnimation.CALM_1, "Mr Squeekles?")
				.npc(npc, FacialAnimation.CALM_1, "Do not judge me."));
		chain.add(new DialogueChain().npc(npc, FacialAnimation.CALM_1, "Cower, tiny creature.")
				.player(FacialAnimation.CALM_1, "Look who's talking.")
				.npc(npc, FacialAnimation.CALM_1, "You will mock me less when I reach my full size.")
				.player(FacialAnimation.CALM_1, "Hah, I beat you once, no reason I can't do so again.")
				.npc(npc, FacialAnimation.CALM_1,
						"But this time, I am ready for you. I have studied you.")
				.player(FacialAnimation.CALM_1, "Haha..yea...good point."));
		chain.add(new DialogueChain()
				.npc(npc, FacialAnimation.CALM_1, "Human pet, scratch my ears now; I command you!")
				.player(FacialAnimation.CALM_1, "You have ears? Also, I'm not your pet.")
				.npc(npc, FacialAnimation.CALM_1, "Hahaha... Of course you are, tiny creature.")
				.player(FacialAnimation.CALM_1, "I'm not so small compared to you either.")
				.npc(npc, FacialAnimation.CALM_1, "I'm...erm...kneeling."));
		chain.add(new DialogueChain().player(FacialAnimation.CALM_1, "Aaaargh! Jad! Aaargh!")
				.npc(npc, FacialAnimation.CALM_1, "Rrrr?")
				.player(FacialAnimation.CALM_1,
						"Yea...not quite the same when you're so small."));
		return chain;
	}
}