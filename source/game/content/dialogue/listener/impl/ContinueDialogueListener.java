package game.content.dialogue.listener.impl;

import game.content.dialogue.Dialogue;
import game.content.dialogue.listener.DialogueListener;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 10:48 PM
 */
public interface ContinueDialogueListener extends DialogueListener {

	void onContinue(Player player, Dialogue dialogue);
}
