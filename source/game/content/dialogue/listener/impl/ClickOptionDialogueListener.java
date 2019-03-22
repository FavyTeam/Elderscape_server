package game.content.dialogue.listener.impl;

import game.content.dialogue.listener.DialogueListener;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 10:49 PM
 */
public interface ClickOptionDialogueListener extends DialogueListener {

	void onOption(Player player, int option);

}
