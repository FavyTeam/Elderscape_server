package game.content.dialogue.listener.impl;

import game.content.dialogue.Dialogue;
import game.content.dialogue.listener.DialogueListener;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 11:26 PM
 */
public interface CloseDialogueListener extends DialogueListener {

	void onClose(Player player, Dialogue dialogue);

}
