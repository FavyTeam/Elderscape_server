package game.content.dialogue;

import game.content.dialogue.listener.impl.ClickOptionDialogueListener;
import game.content.dialogue.listener.impl.CloseDialogueListener;
import game.content.dialogue.listener.impl.ContinueDialogueListener;
import game.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 4:07 PM
 */
public class DialogueLink {

	private final Dialogue dialogue;

	private final List<ClickOptionDialogueListener> clickOptionListeners = new ArrayList<>();

	private final List<ContinueDialogueListener> continueListeners = new ArrayList<>();

	private final List<CloseDialogueListener> closeListeners = new ArrayList<>();

	public DialogueLink(Dialogue dialogue) {
		this.dialogue = dialogue;
	}

	public void execute(Player player) {
		dialogue.send(player);
	}

	public DialogueLink addClickOptionListener(ClickOptionDialogueListener listener) {
		clickOptionListeners.add(listener);

		return this;
	}

	public DialogueLink addContinueListener(ContinueDialogueListener listener) {
		continueListeners.add(listener);

		return this;
	}

	public DialogueLink addCloseListener(CloseDialogueListener listener) {
		closeListeners.add(listener);

		return this;
	}

	public List<ClickOptionDialogueListener> getClickOptionListeners() {
		return clickOptionListeners;
	}

	public List<ContinueDialogueListener> getContinueListeners() {
		return continueListeners;
	}

	public List<CloseDialogueListener> getCloseListeners() {
		return closeListeners;
	}

	public Dialogue getDialogue() {
		return dialogue;
	}
}
