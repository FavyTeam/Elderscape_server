package game.content.dialogue.impl;

import game.content.dialogue.Dialogue;
import game.player.Player;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-25 at 1:31 PM
 */
public class OptionDialogue implements Dialogue {

	private final String title;

	private final List<String> options;

	public OptionDialogue(String title, String... options) {
		this.title = title;
		this.options = Arrays.asList(options);
	}

	@Override
	public void send(Player player) {
		if (options.size() < 2 || options.size() > 5) {
			return;
		}
		int frame = options.size() == 2 ? 2460 : options.size() == 3 ? 2470 : options.size() == 4 ? 2481 : 2493;

		int nearSwords = frame + 3 + options.size();

		int farSwords = options.size() == 2 ? frame + 8 : options.size() == 3 ? frame + 9 : options.size() == 4 ? frame + 8 : frame + 9;
		player.getPA().sendFrame126(title, frame);
		for (int index = 0; index < options.size(); index++) {
			player.getPA().sendFrame126(options.get(index), frame + 1 + index);
		}
		player.getPA().sendFrame171(title.length() > 16 ? 1 : 0, nearSwords);
		player.getPA().sendFrame171(title.length() > 16 ? 0 : 1, farSwords);

		player.getPA().sendFrame164(frame - 1);
	}

	public String getTitle() {
		return title;
	}

	public List<String> getOptions() {
		return options;
	}
}
