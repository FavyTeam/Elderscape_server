def first_click_object_679(player):
    player.getDH().sendDialogues(12345)
	
	
def chat_12345(player):
    player.getDH().sendDialogueQuestion("This exit leads to the Wilderness, are you sure?", "Yes", "No")
	
def option_one_12345(player):
    player.getPA().movePlayer(3206, 3681, 0);
    player.getPA().closeInterfaces(True);
	
def option_two_12345(player):
    player.getPA().closeInterfaces(True);