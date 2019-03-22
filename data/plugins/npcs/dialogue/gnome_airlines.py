from core import ServerConstants

def first_click_npc_1446(player):
    player.getDH().sendDialogues(1258831997)
    
def chat_1258831997(player):
    player.getDH().sendNpcChat("Greetings traveller, would you like to travel by" , "Gnome Air?", 591)
    player.nextDialogue = 1258831998;

def chat_1258831998(player):
    player.getDH().sendPlayerChat("What's Gnome Air?", 591)
    player.nextDialogue = 1258831999;

def chat_1258831999(player):
    player.getDH().sendNpcChat("Gnome Air is the finest airline in " + ServerConstants.getServerName() + ".", 591)
    player.nextDialogue = 1258832000;

def chat_1258832000(player):
    player.getDH().sendNpcChat("Well... it's the only real airline in " + ServerConstants.getServerName() + ".", 591)
    player.nextDialogue = 1258832001;

def chat_1258832001(player):
    player.getDH().sendNpcChat("Ha!", 591)
    player.nextDialogue = 1258832002;

def chat_1258832002(player):
    player.getDH().sendPlayerChat("What do you mean by real airline?", 591)
    player.nextDialogue = 1258832003;

def chat_1258832003(player):
    player.getDH().sendNpcChat("Well there's a dodgy magic carpet operating in the", "desert, I doubt it will ever take off... Ha!", 591)
    player.nextDialogue = 1258832004;

def chat_1258832004(player):
    player.getDH().sendPlayerChat("Where can you take me?", 591)
    player.nextDialogue = 1258832005;

def chat_1258832005(player):
    player.getDH().sendNpcChat("We currently fly to 5 different air bases, would" , "you like to see?", 591)
    player.nextDialogue = 1258832006;
	
def chat_1258832006(player):
    player.getDH().sendOptionDialogue("Sure, fly me away!", "No way, I'm scared of heights.", 1258832007)
	
def option_one_1258832007(player):
    player.setActionIdUsed(2650);
    player.getPA().displayInterface(802);
	
def option_two_1258832007(player):
    player.getDH().sendPlayerChat("No way, I'm scared of heights.", 591)