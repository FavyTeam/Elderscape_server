package game.content.dialogue.impl;

import game.content.dialogue.Dialogue;
import game.content.dialogueold.DialogueHandler;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.npc.data.NpcDefinition;
import game.player.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-25 at 6:19 PM
 */
public class NpcDialogue implements Dialogue {

	private final NpcDefinition definition;

	private final FacialAnimation animation;

	private final List<String> lines;

	public NpcDialogue(NpcDefinition definition, FacialAnimation animation, String... lines) {
		this.definition = definition;
		this.animation = animation;
		this.lines = Arrays.asList(lines);
	}

	@Override
	public void send(Player player) {
		int frame = lines.size() == 1 ? 4883 : lines.size() == 2 ? 4888 : lines.size() == 3 ? 4894 : lines.size() == 4 ? 4901 : 0;

		if (frame == 0) {
			return;
		}
		player.getPA().sendFrame200(frame, animation.getAnimationId());
		player.getPA().sendFrame126(definition.name, frame + 1);

		for (int index = 0; index < lines.size(); index++) {
			player.getPA().sendFrame126(lines.get(index), frame + 2 + index);
		}
		player.getPA().sendFrame75(definition.npcType, frame);
		player.getPA().sendFrame164(frame - 1);
	}

	public NpcDefinition getDefinition() {
		return definition;
	}

	public FacialAnimation getAnimation() {
		return animation;
	}

	public List<String> getLines() {
		return lines;
	}
}
