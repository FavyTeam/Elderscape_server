package game.content.skilling.summoning.pet;

import java.util.ArrayList;
import game.content.dialogue.DialogueChain;

/**
 * Represents a list of dialogues for a pet
 * 
 * @author 2012
 *
 */
public interface PetDialogue {

	/**
	 * Gets the dialogues
	 * 
	 * @return the dialogue
	 */
	public ArrayList<DialogueChain> getDialogue();
}
