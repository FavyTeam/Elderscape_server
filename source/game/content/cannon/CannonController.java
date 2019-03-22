package game.content.cannon;

import game.player.Player;

/**
 * Created by Jason MK on 2018-07-20 at 12:31 PM
 */
public class CannonController {

    private final Player player;

    private Cannon cannon;

    public CannonController(Player player) {
        this.player = player;
    }

    private boolean exists() {
        return cannon != null;
    }

    public void setup() {
        if (exists()) {
            player.getPA().sendMessage("You already have a cannon setup, you cannot setup more than one.");
            return;
        }

    }
}
