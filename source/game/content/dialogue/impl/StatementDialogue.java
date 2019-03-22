package game.content.dialogue.impl;

import game.content.dialogue.Dialogue;
import game.content.dialogueold.DialogueHandler;
import game.player.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-06-18 at 10:10 AM
 */
public class StatementDialogue implements Dialogue {

	private final List<String> messages;

	public StatementDialogue(String... messages) {
		this.messages = Arrays.asList(messages);
	}

	public List<String> getMessages() {
		return messages;
	}

	@Override
	public void send(Player player) {
		int frame = messages.size() == 1 ? 357 : messages.size() == 2 ? 360 : messages.size() == 3 ? 364 :
				messages.size() == 4 ? 369 : messages.size() == 5 ? 375 : 0;

		if (frame == 0) {
			return;
		}
		for (int index = 0; index < messages.size(); index++) {
			player.getPA().sendFrame126(messages.get(index), frame + index);
		}
		player.getPA().sendFrame164(frame - 1);
	}
}
