#Plugin to handle dialogue for bartenders
#Written by Owain for Runefate
#14/08/18

def first_click_npc_1312(player):
    player.getDH().sendDialogues(7700)
	
def chat_7700(player):
    player.getDH().sendNpcChat("What can I do yer for?", 589)
    player.nextDialogue = 7701;
	
def chat_7701(player):
    player.getDH().sendOptionDialogue("A glass of your finest ale please.", "Never mind.", 7701)
    
def option_two_7701(player):
    player.getPA().closeInterfaces(True);
    
def option_one_7701(player):
    player.getDH().sendPlayerChat("A glass of your finest ale please.", 589)
    player.nextDialogue = 7702;
    
def chat_7702(player):
    player.getDH().sendNpcChat("No problemo. That'll be 2 coins.", 589)
    player.nextDialogue = 7703;
    
def chat_7703(player):
    if ItemAssistant.hasItemAmountInInventory(player, 995, 2):
        player.getPA().closeInterfaces(True);
        player.getPA().sendMessage("You buy a pint of beer.")
        ItemAssistant.deleteItemFromInventory(player, 995, 2)
        ItemAssistant.addItemToInventoryOrDrop(player, 1917, 1)
    else:
        player.getDH().sendNpcChat("Sorry pal, you don't have enough gold.", 589)