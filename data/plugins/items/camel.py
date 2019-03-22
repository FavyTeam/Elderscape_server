from core import ServerConstants

def first_click_object_6578(player):
    if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 1712) and (player.playerEquipment[ServerConstants.CAPE_SLOT] == 10499) and (player.playerEquipment[ServerConstants.WEAPON_SLOT] == 11802):
        ItemAssistant.addItemToInventoryOrDrop(player, 7003, 1)
        player.getPA().sendMessage("You find a camel mask in the tree.")