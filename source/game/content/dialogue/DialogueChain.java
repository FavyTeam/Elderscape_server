package game.content.dialogue;

import game.content.dialogue.impl.*;
import game.content.dialogue.listener.DialogueListener;
import game.content.dialogue.listener.impl.ClickOptionDialogueListener;
import game.content.dialogue.listener.impl.CloseDialogueListener;
import game.content.dialogue.listener.impl.ContinueDialogueListener;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.npc.data.NpcDefinition;
import game.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 4:02 PM
 */
public class DialogueChain {

	private final List<DialogueLink> links = new ArrayList<>();

	private int indexOfLink;

	public void start(Player player) {
		indexOfLink = 0;
		show(player);
	}

	public void next(Player player) {
		DialogueLink link = links.get(indexOfLink);

		if (link != null) {
			link.getContinueListeners().forEach(listener -> listener.onContinue(player, link.getDialogue()));
		}
		indexOfLink++;
		show(player);
	}

	public boolean isOnLastLink() {
		return indexOfLink == links.size() - 1;
	}

	public void end(Player player) {
		DialogueLink link = links.get(indexOfLink);

		if (link != null) {
			link.getCloseListeners().forEach(listener -> listener.onClose(player, link.getDialogue()));
		}
		indexOfLink = 0;
	}

	public void previous(Player player) {
		indexOfLink--;

		if (indexOfLink < 0) {
			indexOfLink = 0;
		}
		show(player);
	}

	public void show(Player player) {
		DialogueLink link = links.get(indexOfLink);

		if (link == null) {
			return;
		}
		link.execute(player);
	}

	public DialogueChain link(DialogueLink link) {
		links.add(link);

		return this;
	}

	public DialogueLink getCurrentLink() {
		return links.get(indexOfLink);
	}

	public DialogueChain addContinueListener(ContinueDialogueListener listener) {
		if (links.isEmpty()) {
			throw new IllegalStateException("Cannot add a listener to last link, no last link.");
		}
		links.get(links.size() - 1).addContinueListener(listener);

		return this;
	}

	public DialogueChain addCloseListener(CloseDialogueListener listener) {
		if (links.isEmpty()) {
			throw new IllegalStateException("Cannot add a listener to last link, no last link.");
		}
		links.get(links.size() - 1).addCloseListener(listener);

		return this;
	}

	public DialogueChain option(ClickOptionDialogueListener optionListener, String title, String... options) {
		if (options == null || options.length < 2 || options.length > 5) {
			throw new IllegalArgumentException("Length of options must be between 2 and 5 inclusive and cannot be null.");
		}
		if (optionListener == null) {
			throw new IllegalArgumentException("Option listener cannot be null.");
		}
		links.add(new DialogueLink(new OptionDialogue(title, options)).addClickOptionListener(optionListener));

		return this;
	}

	public DialogueChain player(FacialAnimation animation, String... message) {
		if (animation == null) {
			throw new IllegalArgumentException("Animation for players face cannot be null.");
		}
		if (message == null || message.length == 0) {
			throw new IllegalArgumentException("Messages length cannot be null and must be greater than zero.");
		}
		links.add(new DialogueLink(new PlayerDialogue(animation, message)));

		return this;
	}

	public DialogueChain npc(NpcDefinition definition, FacialAnimation animation, String... message) {
		if (definition == null || definition.name == null) {
			throw new IllegalArgumentException("Definition or definition name is null, not permitted.");
		}
		if (animation == null) {
			throw new IllegalArgumentException("Animation for npc cannot be null.");
		}
		if (message == null || message.length < 1 || message.length > 4) {
			throw new IllegalArgumentException("Amount of lines must be between 1 and 4 inclusive. ");
		}
		links.add(new DialogueLink(new NpcDialogue(definition, animation, message)));

		return this;
	}

	public DialogueChain statement(String... messages) {
		if (messages == null || messages.length == 0 || messages.length > 5) {
			throw new IllegalArgumentException("Messages cannot be null and must contain between 1 and 5 elements.");
		}
		links.add(new DialogueLink(new StatementDialogue(messages)));

		return this;
	}

	public DialogueChain item(String header, int item, int zoom, int xOffset, int yOffset, String... messages) {
		if (messages == null || messages.length == 0 || messages.length > 4) {
			throw new IllegalArgumentException("Messages cannot be null and must contain between 1 and 4 elements.");
		}
		links.add(new DialogueLink(new ItemDialogue(header, item, zoom, xOffset, yOffset, messages)));

		return this;
	}

}
