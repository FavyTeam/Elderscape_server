from game.item import ItemAssistant

def first_click_object_3195(player):
    if player.playerRights == 1 or player.playerRights == 2:
        if (ItemAssistant.getFreeInventorySlots(player) == 0):
            player.getDH().sendStatement("You need at least 1 free inventory space to do this.")
            return
        if (System.currentTimeMillis() - player.buryDelay > 1200):
            player.buryDelay = System.currentTimeMillis();
            ItemAssistant.addItem(player, 2518, ItemAssistant.getFreeInventorySlots(player))
            player.startAnimation(832)
            player.getPA().sendMessage("You take some rotten tomatoes from the crate.")
    else:
        player.getPA().sendMessage("Nice try, but only staff may do this.")