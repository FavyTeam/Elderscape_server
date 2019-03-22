package game.content.skilling.summoning.pet.impl;

import java.util.ArrayList;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.skilling.summoning.pet.PetDialogue;
import game.npc.data.NpcDefinition;

/**
 * Handles the creeping hand
 * 
 * @author 2012
 *
 */
public class CreepingHand implements PetDialogue {

	/**
	 * The creeping hand npc id
	 */
	private static final int CREEPING_HAND = 0;

	@Override
	public ArrayList<DialogueChain> getDialogue() {
		ArrayList<DialogueChain> chain = new ArrayList<>();
		NpcDefinition hand = NpcDefinition.DEFINITIONS[CREEPING_HAND];
		chain.add(new DialogueChain()
					.player(FacialAnimation.CALM_1, "I've got to hand it to you, you fought valiantly")
					.npc(hand, FacialAnimation.CALM_1,
							"My goodness, you're very witty for a cold-blooded killer.")
					.player(FacialAnimation.CALM_1, "You forgot to add 'hand-some'.")
					.npc(hand, FacialAnimation.CALM_1, "Yes, that's right. I forgot.")
					.player(FacialAnimation.CALM_1,
						"That's enough chatter for now. There's no wrist for the wicked."));

		chain.add(new DialogueChain()
					.player(FacialAnimation.CALM_1, "Greetings. I would shake your hand, but",
							"I'm not sure how that would work.")
					.npc(hand, FacialAnimation.CALM_1,
							"Every time, always with the hand jokes.")
					.player(FacialAnimation.CALM_1, "I'm sorry, I can't help myself. Gimme five!")
					.npc(hand, FacialAnimation.CALM_1, "I should slap you right here and now.")
					.player(FacialAnimation.CALM_1,
							"Okay, we shouldn't let this get out of hand.")
				.npc(hand, FacialAnimation.CALM_1, "..."));

		chain.add(new DialogueChain().player(FacialAnimation.CALM_1, "Lovely day, isn't it?")
							.npc(hand, FacialAnimation.CALM_1,
									"Sorry. Did you just say something WITHOUT a needless hand pun?")
							.player(FacialAnimation.CALM_1, "It can happen, you know.")
							.npc(hand, FacialAnimation.CALM_1, "I'm... I'm stunned.")
							.player(FacialAnimation.CALM_1, "Obviously, you can't hand-le the truth.")
				.npc(hand, FacialAnimation.CALM_1, "Ah, there it is."));
		return chain;
	}
}