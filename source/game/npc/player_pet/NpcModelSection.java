package game.npc.player_pet;

/**
 * Created by Jason MK on 2018-07-04 at 12:42 PM
 */
public enum NpcModelSection {
    BODY(0, 4),

    LEGS(1, 7),

    HANDS(2, 9),

    //ARMS(3, requiredItemSlot),

    FEET(4, 10),

    WEAPON(5, 3),

    HELMET(6, 0),

    SHIELD(7, 5),

    AMULET(8, 2),

    CAPE(9, 1);

    private final int slot;

    private final int requiredItemSlot;

    NpcModelSection(int slot, int requiredItemSlot) {
        this.slot = slot;
        this.requiredItemSlot = requiredItemSlot;
    }

    public int getSlot() {
        return slot;
    }

    public int getRequiredItemSlot() {
        return requiredItemSlot;
    }
}
