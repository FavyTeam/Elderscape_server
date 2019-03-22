package game.content.dialogue.impl;

import game.content.dialogue.Dialogue;
import game.content.dialogueold.DialogueHandler;
import game.player.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 4:18 PM
 */
public class PlayerDialogue implements Dialogue {

	private final List<String> messages;

	private final DialogueHandler.FacialAnimation facialAnimation;

	public PlayerDialogue(DialogueHandler.FacialAnimation facialAnimation, String... messages) {
		this.messages = Arrays.asList(messages);
		this.facialAnimation = facialAnimation;
	}

	public List<String> getMessages() {
		return messages;
	}

	public DialogueHandler.FacialAnimation getFacialAnimation() {
		return facialAnimation;
	}

	@Override
	public void send(Player player) {
		int frame = messages.size() == 1 ? 969 : messages.size() == 2 ? 974 : messages.size() == 3 ? 980 : 0;

		if (frame == 0) {
			return;
		}
		player.getPA().sendFrame200(frame, facialAnimation.getAnimationId());
		player.getPA().sendFrame126(player.getPlayerName(), frame + 1);

		for (int index = 0; index < messages.size(); index++) {
			player.getPA().sendFrame126(messages.get(index), frame + 2 + index);
		}
		player.getPA().sendFrame185(frame);
		player.getPA().sendFrame164(frame - 1);
	}
}
