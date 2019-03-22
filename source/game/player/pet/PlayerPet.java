package game.player.pet;

import com.google.common.collect.ImmutableSet;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.position.Position;
import game.content.combat.Combat;
import game.content.dialogue.DialogueChain;
import game.content.skilling.Skilling;
import game.entity.EntityType;
import game.item.ItemAssistant;
import game.item.ItemSlot;
import game.object.clip.Region;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.PlayerSave;
import game.player.movement.Movement;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by Jason MK on 2018-07-05 at 2:15 PM
 */
public class PlayerPet extends Player {

    public static final Set<Integer> ANIMATIONS_CANNOT_MIMIC = ImmutableSet.of(3363);

    public static final Set<Integer> GRAPHICS_CANNOT_MIMIC = ImmutableSet.of();

    public static final boolean DELETE_ITEM_ON_EQUIP = true;

    private Player owner;

    private boolean summoned;

    private boolean mimicking;

    public PlayerPet(Player owner, String username) {
        super(null, -1, true, EntityType.PLAYER_PET);
        this.owner = owner;

        String name = username;
        String pass = "test";

        name = name.trim();
        pass = pass.toLowerCase();
        setPlayerName(name);

        int result = 2;

        if (PlayerHandler.isPlayerOn(name)) {
            result = 5;
        }

        if (result == 2) {
            int load = 13;
            if (!ServerConfiguration.STABILITY_TEST) {
                load = PlayerSave.loadGame(this, name, pass, true);
            }

            if (load == 3) // 3 = wrong password.
            {
                saveFile = false;
            } else {
                for (int i = 0; i < playerEquipment.length; i++) {
                    if (playerEquipment[i] == 0) {
                        playerEquipment[i] = -1;
                        playerEquipmentN[i] = 0;
                    }
                }
                saveFile = Server.playerHandler.newPlayerClient(this);
            }
        }

        playerPass = pass;

        botPkType = "";

        gameModeTitle = "";

        getOutStream().packetEncryption = null;

        packetType = -1;

        packetSize = 0;

        setActive(true);

        setDisplayName(String.format("Mini-%s", getOwner().getPlayerName()));

        initialize();

        loadState();

        if (!summoned) {
            summoned = true;
            saveState();
        }
        getEventHandler().singularExecution(this, () -> setAppearanceUpdateRequired(true), 5);
    }

    @Override
    public void gfx100(int gfx) {
        if (GRAPHICS_CANNOT_MIMIC.contains(gfx)) {
            return;
        }
        super.gfx100(gfx);
    }

    @Override
    public void gfx0(int gfx) {
        if (GRAPHICS_CANNOT_MIMIC.contains(gfx)) {
            return;
        }
        super.gfx0(gfx);
    }

    @Override
    public void gfx(int gfx, int height) {
        if (GRAPHICS_CANNOT_MIMIC.contains(gfx)) {
            return;
        }
        super.gfx(gfx, height);
    }

    @Override
    public void startAnimation(int animId) {
        if (ANIMATIONS_CANNOT_MIMIC.contains(animId)) {
            return;
        }
        super.startAnimation(animId);
    }

    private void initialize() {
        for (int skill = 0; skill < 7; skill++) {
            Skilling.setLevel(this, skill, 99);
        }
        ItemAssistant.deleteAllItems(this);
    }

    private void dismiss() {
        if (owner.getPlayerName() == null) {
            owner.setDialogueChain(new DialogueChain().statement("You do not have a pet to dismiss right now.")).start(owner);
            return;
        }
        if (isDisconnected()) {
            owner.setDialogueChain(new DialogueChain().statement("This pet has already been dismissed.")).start(owner);
            return;
        }
        if (ItemAssistant.getFreeInventorySlots(owner) == 0) {
            owner.setDialogueChainAndStart(new DialogueChain().statement("You need at least one free inventory space to dismiss me."));
            return;
        }
		if (!ItemAssistant.addItem(owner, 7677, 1)) {
			return;
		}
        summoned = false;
        saveState();
        setDisconnected(true, "dismissing-player-pet");
    }

    public DialogueChain talk() {
        return new DialogueChain().option((p, option) -> {
            if (option == 1) {
                owner.setDialogueChainAndStart(createRemoveItemDialogueChain(
                        ServerConstants.HEAD_SLOT,
                        ServerConstants.CAPE_SLOT,
                        ServerConstants.AMULET_SLOT,
                        ServerConstants.WEAPON_SLOT,
                        createRemoveItemDialogueChain(
                                ServerConstants.BODY_SLOT,
                                ServerConstants.SHIELD_SLOT,
                                ServerConstants.LEG_SLOT,
                                ServerConstants.HAND_SLOT,
                                createRemoveItemDialogueChain(
                                        ServerConstants.FEET_SLOT,
                                        ServerConstants.RING_SLOT,
                                        -1,
                                        -1,
                                        talk(),
                                        "Feet", "Ring", "", "", "Nevermind"),
                                "Body", "Shield", "Legs", "Hands", "Next"),
                        "Head", "Cape", "Amulet", "Weapon", "Next"));
            } else if (option == 2) {
                if (DELETE_ITEM_ON_EQUIP) {
                    p.setDialogueChainAndStart(new DialogueChain().statement(
                            "@red@You will not receive any of these items.",
                            "@red@All of the items will be lost.").addCloseListener((p1, d) -> p.setDialogueChainAndStart(createStripItemsDialogueChain())));
                } else {
                    p.setDialogueChainAndStart(createStripItemsDialogueChain());
                }
            } else if (option == 3) {
                p.setDialogueChain(new DialogueChain().option((p1, op2) -> {
                    if (op2 == 1) {
                        if (Arrays.equals(p1.playerAppearance, playerAppearance)) {
                            p1.setDialogueChain(new DialogueChain().statement("Your appearance is exactly the same!")).start(p1);
                        } else {
                            System.arraycopy(owner.playerAppearance, 0, playerAppearance, 0, owner.playerAppearance.length);
                            appearanceUpdateRequired = true;
                            saveState();
                            p1.setDialogueChain(new DialogueChain().statement("Our appearance is the same now!")).start(p1);
                        }
                    } else {
                        p1.getPA().closeInterfaces(true);
                    }
                }, "Are you sure you want to mirror appearance?", "Yes", "No")).start(p);
            } else if (option == 4) {
                p.setDialogueChain(new DialogueChain().option((f, s) -> {
                    if (s == 1) {
                        mimicking = !mimicking;
                        if (mimicking) {
                            Movement.stopMovement(this);
                            startAnimation(2107);
                            if (!Combat.inCombat(owner)) {
                                Movement.stopMovement(owner);
                                owner.startAnimation(2107);
                            }
                        }
                        saveState();
                        owner.setDialogueChainAndStart(new DialogueChain().statement(String.format("Mimicking animations and graphics has been %s.",
                                mimicking ? "enabled" : "disabled")));
                    } else if (s == 2) {
                        dismiss();
                        owner.getPA().closeInterfaces(true);
                    } else if (s == 3) {
                        owner.setDialogueChain(talk()).start(f);
                    } else {
                        owner.getPA().closeInterfaces(true);
                    }
                }, getDisplayName(), String.format("%s mimicking animations and graphics",
                        mimicking ? "@red@Disable" : "@gre@Enable"), "Dismiss", "Previous", "Nevermind")).start(p);
            } else if (option == 5) {
                p.getPA().closeInterfaces(true);
            }
        }, getDisplayName(), "Remove item from slot.", "Remove all items", "Mirror my appearance", "Next", "Nevermind");
    }

    private DialogueChain createRemoveItemDialogueChain(int first, int second, int third, int fourth, DialogueChain onFifth, String... options) {
        return new DialogueChain().option((p1, op1) -> {
            if (op1 == 1 && first != -1) {
                confirmDelete(first);
            } else if (op1 == 2 && second != -1) {
                confirmDelete(second);
            } else if (op1 == 3 && third != -1) {
                confirmDelete(third);
            } else if (op1 == 4 && fourth != -1) {
                confirmDelete(fourth);
            } else {
                owner.setDialogueChainAndStart(onFifth);
            }
        }, "What slot do you want to delete?", options);
    }

    private DialogueChain createStripItemsDialogueChain() {
        return new DialogueChain().option((p1, op2) -> {
            if (op2 == 1) {
                if (ItemAssistant.hasEquipment(this)) {
                    if (!DELETE_ITEM_ON_EQUIP && ItemAssistant.getEquipmentSlotsOccupied(this) > ItemAssistant.getFreeInventorySlots(owner)) {
                        p1.setDialogueChain(new DialogueChain().statement("You don't have enough free slots!",
                                "Make more room in your inventory.").addCloseListener((pl2, d) -> pl2.setDialogueChain(talk()).start(pl2))).start(p1);
                        return;
                    }
                    ItemAssistant.deleteInventory(this);
                    for (int index = 0; index < playerEquipment.length; index++) {
                        if (playerEquipment[index] > 0) {
                            ItemAssistant.removeItem(this, playerEquipment[index], index, true);
                        }
                    }
                    if (!DELETE_ITEM_ON_EQUIP) {
                        for (int index = 0; index < playerItems.length; index++) {
                            if (playerItems[index] > 0) {
                                ItemAssistant.addItem(owner, playerItems[index] - 1, playerItemsN[index]);
                            }
                        }
                    }
                    saveState();
                    p1.setDialogueChain(new DialogueChain().statement("I have removed all of my items!")).start(p1);
                } else {
                    p1.setDialogueChain(new DialogueChain().statement("I don't have any items to strip!")).start(p1);
                }
            } else {
                p1.getPA().closeInterfaces(true);
            }
        }, "Are you sure you want to strip all items?", "Yes", "No");
    }

    public void confirmDelete(int slot) {
        DialogueChain last = owner.getDialogueChain();

        if (playerEquipment[slot] == -1 || playerEquipmentN[slot] < 1) {
            owner.setDialogueChainAndStart(new DialogueChain().statement("There is no item in this slot.").addCloseListener((p, d)
                    -> p.setDialogueChainAndStart(last)));
            return;
        }
        owner.setDialogueChainAndStart(new DialogueChain().item("Are you sure you want to remove this item?",
                playerEquipment[slot], 250, 0, 0, DELETE_ITEM_ON_EQUIP ? "@red@You will not receive this item back." : "You will receive this item back.").
                option((player, option) -> {
                    if (option == 1) {
                        if (playerEquipment[slot] == -1 || playerEquipmentN[slot] < 1) {
                            owner.setDialogueChainAndStart(new DialogueChain().statement("There is no item in this slot.").addCloseListener((p, d)
                                    -> p.setDialogueChainAndStart(last)));
                            return;
                        }
                        if (!DELETE_ITEM_ON_EQUIP && ItemAssistant.getFreeInventorySlots(owner) == 0) {
                            player.setDialogueChain(new DialogueChain().statement("You don't have enough free slots!",
                                    "Make more room in your inventory.").addCloseListener((pl2, d) -> pl2.setDialogueChain(talk()).start(pl2))).start(player);
                            return;
                        }
                        ItemAssistant.deleteInventory(this);
                        ItemAssistant.removeItem(this, playerEquipment[slot], slot, true);

                        if (!DELETE_ITEM_ON_EQUIP) {
                            for (int index = 0; index < playerItems.length; index++) {
                                if (playerItems[index] > -1 && playerItemsN[index] > 0) {
                                    ItemAssistant.addItemToInventoryOrDrop(owner, playerItems[index] - 1, playerItemsN[index]);
                                }
                            }
                        }
                    }
                    player.setDialogueChainAndStart(talk());
                }, "Are you sure?", "Yes", "No"));
    }

    public void equip(int item, int amount, int fromSlot) {
        int equipmentSlot = ItemSlot.getItemEquipmentSlot(ItemAssistant.getItemName(item).toLowerCase(), item);

        if (equipmentSlot == -1) {
            owner.setDialogueChainAndStart(new DialogueChain().statement("This is not an item that can be equipped."));
            return;
        }
        if (equipmentSlot == ServerConstants.ARROW_SLOT) {
            owner.setDialogueChainAndStart(new DialogueChain().statement("You cannot add any items to the arrow-slot."));
            return;
        }
        ItemAssistant.deleteInventory(this);
        ItemAssistant.addItem(this, item, amount);

        int inventorySlot = ItemAssistant.getItemSlot(this, item);

        if (inventorySlot == -1) {
            return;
        }
        if (item == playerEquipment[equipmentSlot]) {
            owner.setDialogueChainAndStart(new DialogueChain().statement("The pet is already wearing this item."));
            return;
        }

        if (ItemAssistant.wearItem(this, item, inventorySlot)) {
            owner.turnPlayerTo(getX(), getY());
            owner.startAnimation(827);
            ItemAssistant.deleteItemFromInventory(owner, item, fromSlot, amount);

            if (!DELETE_ITEM_ON_EQUIP) {
                for (int index = 0; index < playerItems.length; index++) {
                    if (playerItems[index] > -1 && playerItemsN[index] > 0) {
                        ItemAssistant.addItemToInventoryOrDrop(owner, playerItems[index] - 1, playerItemsN[index]);
                    }
                }
            }
        }
        appearanceUpdateRequired = true;
        saveState();
    }

    private void saveState() {
        if (owner == null) {
            throw new IllegalStateException("Owner is null, cannot save state.");
        }
        owner.setPlayerPetState(new PlayerPetState.Builder().
                setEquipment(playerEquipment).
                setEquipmentAmount(playerEquipmentN).
                setAppearance(playerAppearance).
                setSummoned(summoned).
                setMimicking(mimicking).build());
    }

    private void loadState() {
        PlayerPetState state = owner.getPlayerPetState();

        if (state == null) {
            return;
        }
        playerEquipment = state.getEquipment();
        playerEquipmentN = state.getEquipmentAmount();
        playerAppearance = state.getAppearance();
        summoned = state.isSummoned();
    }

    public Player getOwner() {
        return owner;
    }

    /**
     * Called every 600ms.
     */
    @Override
    public void onSequence() {
        if (owner == null || owner.isDisconnected()) {
            setDisconnected(true, "player-pet-owner-disconnected");
            return;
        }

        if (!owner.getLocalPlayers().contains(this)) {
            Position nextOpen = Region.nextOpenTileOrNull(owner.getX(), owner.getY(), owner.getHeight());

            if (nextOpen == null) {
                nextOpen = new Position(owner);
            }
			move(nextOpen);
        } else {
            setPlayerIdToFollow(owner.getPlayerId());
        }
        super.onSequence();
    }

    @Override
    public void onRemove() {
        super.onRemove();

        if (owner != null) {
            owner.setPlayerPet(null);
        }
        PlayerPetManager.getSingleton().remove(getPlayerName());
    }

    public boolean isSummoned() {
        return summoned;
    }

    public boolean isMimicking() {
        return mimicking;
    }
}
