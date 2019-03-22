from game.content.donator import NameChange

def name_change_dialogues(player):
    player.getDH().sendDialogues(740)

def chat_740(player):
    player.getDH().sendDialogueQuestion("Change name to " + NameChange.name + "?", "Yes", "No")
	
def option_one_740(player):
    NameChange.nameChange(player, NameChange.name)
	
def option_two_740(player):
    player.getPA().closeInterfaces(True);