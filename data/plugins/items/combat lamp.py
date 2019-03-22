from game.item import ItemAssistant
from game.content.skilling import Skilling
	
def first_click_item_10586(player):
    player.getDH().sendOptionDialogue("Attack Experience", "Strength Experience", "Defence Experience", "Ranged Experience", "Magic Experience", 15000)
    
def add_lamp_experience(level, player):
    if ItemAssistant.hasItemInInventory(player, 10586):
        ItemAssistant.deleteItemFromInventory(player, 10586, 1)
        player.getPA().closeInterfaces(True)
        amount = (125 + Misc.random(2000)) + player.skillExperience[level] / 50
        name = ServerConstants.SKILL_NAME[level]
        Skilling.addSkillExperience(player, amount, level, True)
        player.getPA().sendMessage("You receive <col=bc0000>" + Misc.formatNumber(amount) + " <col=000000>experience in <col=bc0000>" + name + "<col=000000>!")
    
def option_one_15000(player):
    level = ServerConstants.ATTACK
    add_lamp_experience(level, player)
    
def option_two_15000(player):
    level = ServerConstants.STRENGTH
    add_lamp_experience(level, player)

def option_three_15000(player):
    level = ServerConstants.DEFENCE
    add_lamp_experience(level, player)

def option_four_15000(player):
    level = ServerConstants.RANGED
    add_lamp_experience(level, player)

def option_five_15000(player):
    level = ServerConstants.MAGIC
    add_lamp_experience(level, player)