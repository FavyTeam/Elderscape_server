from game.object.custom import DoorEvent
from game.player.movement import Movement
from game.player.event import CycleEvent
from game.player.event import CycleEventContainer
from game.player.event import CycleEventHandler
from game.content.miscellaneous import PlayerMiscContent

def first_click_object_4787(player):
    handle_gate_chat(player)
def first_click_object_4788(player):
    handle_gate_chat(player)
	
def handle_gate_chat(player):
    if player.getY() == 2765:
        if not player.getWieldedWeapon() == 4026:
            player.setNpcType(5271)
            player.getDH().sendNpcChat("Halt! Humans are not permitted on Ape Atoll." , "Please leave at once or else we shall do very bad" , "things to you.", 591)
            player.nextDialogue = 50000;
        else:
            player.getDH().sendDialogues(50002)
    else:
        player.getDH().sendDialogues(50002)
        
def chat_50000(player):
    player.getDH().sendPlayerChat("Really? I'd like to see you try, monkey brain!", 591)
    player.nextDialogue = 50001;
    
def chat_50001(player):
    player.getDH().sendStatement("He either doesn't hear, or chooses to ignore you.")
    
def chat_50002(player):   
    player.setNpcType(5271)
    player.getDH().sendNpcChat("Open the gates! A monkey wishes to pass.", 591)
    player.nextDialogue = 50003;
    
def chat_50003(player):
    player.getPA().closeInterfaces(True)
    PlayerMiscContent.handleApeAtollDoor(player) 

	