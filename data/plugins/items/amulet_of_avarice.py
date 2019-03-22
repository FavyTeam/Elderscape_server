#A plugin to handle the dialogue for equipping an amulet of avarice
#Written by Owain
#07/09/18

def amulet_of_avarice(player):
	player.getDH().sendDialogues(10000)
	
def chat_10000(player):
    player.getDH().sendDialogueQuestion("Wearing this amulet will give you a PK skull.", "Give me a PK skull.", "Cancel.")
	
def option_one_10000(player):
    player.getDH().sendDialogues(10001)
	
def option_two_10000(player):
    player.getPA().closeInterfaces(True);
	
def chat_10001(player):
    player.getPA().sendMessage("skulled lol")