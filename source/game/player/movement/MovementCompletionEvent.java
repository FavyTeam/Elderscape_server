package game.player.movement;

import game.player.Player;

/**
 * Created by Jason MK on 2018-07-05 at 10:27 AM
 */
public interface MovementCompletionEvent {

    void onComplete(Player player);

}
