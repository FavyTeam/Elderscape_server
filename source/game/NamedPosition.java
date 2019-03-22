package game;

import game.npc.Npc;
import game.player.Player;
import game.position.Position;

/**
 * Created by Jason MK on 2018-07-19 at 10:00 AM
 */
public class NamedPosition extends Position {

    private final String name;

    /**
     * Creates a new position from the given coordinates.
     *  @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @param name the name of this identifiable position.
     */
    public NamedPosition(int x, int y, int z, String name) {
        super(x, y, z);
        this.name = name;
    }

    /**
     * Creates a new position from the given position.
     *
     * @param position the position we're copying.
     * @param name the name of this identifiable position.
     */
    public NamedPosition(Position position, String name) {
        super(position);
        this.name = name;
    }

    /**
     * Creates a new position from the given coordinates.
     *  @param x the x coordinate.
     * @param y the y coordinate.
     * @param name the name of this identifiable position.
     */
    public NamedPosition(int x, int y, String name) {
        super(x, y);
        this.name = name;
    }

    /**
     * Gets the position for player
     *
     * @param player the player
     * @param name the name of this identifiable position.
     */
    public NamedPosition(Player player, String name) {
        super(player);
        this.name = name;
    }

    /**
     * Gets the position for npc
     *
     * @param npc the npc
     * @param name the name of this identifiable position.
     */
    public NamedPosition(Npc npc, String name) {
        super(npc);
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("name=%s: %s", name, super.toString());
    }
}
