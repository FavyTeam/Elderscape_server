package game.content.item.impl;

import core.GameType;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.content.item.chargeable.Chargeable;
import game.player.Player;
import game.type.GameTypeIdentity;

import java.util.stream.Stream;

/**
 * Created by Jason MK on 2018-09-19 at 1:30 PM
 */
@ItemInteractionComponent(identities = @GameTypeIdentity(type = GameType.OSRS, identity = { 22542, 22545, 22552, 22555, 22547, 22550 }))
public class ChargeableItemInteraction implements ItemInteraction {

    @Override
    public boolean useItem(Player player, int id, int useWith) {
        Chargeable uncharged = Stream.of(Chargeable.values()).filter(c -> c.getUnchargedId() == id || c.getUnchargedId() == useWith).findAny().orElse(null);

        if (uncharged != null) {
            int resource = uncharged.getUnchargedId() == id ? useWith : id;

            if (uncharged.getRequiredResources().stream().anyMatch(required -> required.getId() == resource)) {
                player.getAttributes().getOrDefault(Player.CHARGEABLE_COLLECTION_KEY).charge(player, uncharged);
                return true;
            }
            return false;
        }

        Chargeable charged = Stream.of(Chargeable.values()).filter(c -> c.getChargedId() == id || c.getChargedId() == useWith).findAny().orElse(null);

        if (charged != null) {
            int resource = charged.getChargedId() == id ? useWith : id;

            if (charged.getResources().stream().anyMatch(r -> r.getId() == resource)) {
                player.getAttributes().getOrDefault(Player.CHARGEABLE_COLLECTION_KEY).addCharge(player, charged, resource);
                return true;
            }
            return false;
        }

        return false;
    }

    /**
     * Clicking item action
     *
     * @param player the player
     * @param id     the id
     * @param type   the click type
     * @return clicking action
     */
    @Override
    public boolean sendItemAction(Player player, int id, int type) {
        if (type == 2) {
            Chargeable charged = Chargeable.valueOfCharged(id);

            if (charged != null) {
                player.getAttributes().getOrDefault(Player.CHARGEABLE_COLLECTION_KEY).check(player, charged);
                return true;
            }
        }
        return false;
    }

    /**
     * Dropping the item
     *
     * @param player the player
     * @param id     the id
     * @return dropping the item
     */
    @Override
    public boolean dropItem(Player player, int id) {
        Chargeable charged = Chargeable.valueOfCharged(id);

        if (charged != null) {
            player.getAttributes().getOrDefault(Player.CHARGEABLE_COLLECTION_KEY).uncharge(player, charged);
            return true;
        }
        return false;
    }

    /**
     * Whether item can be equipped
     *
     * @param player the player
     * @param id     the id
     * @param slot   the slot
     * @return equipping item
     */
    @Override
    public boolean canEquip(Player player, int id, int slot) {
        return true;
    }
}
