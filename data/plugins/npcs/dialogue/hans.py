def first_click_npc_3077(player):
    player.getDH().sendDialogues(11111)
	
def second_click_npc_3077(player):
    player.getDH().sendDialogues(245)
	
def chat_11111(player):
    player.getDH().sendNpcChat("Hello there, is there anything I can help with?", 589)
    player.nextDialogue = 11112;
	
	
def chat_11112(player):
    player.getDH().sendPlayerChat("I was wondering how old I am?", 591)
    player.nextDialogue = 11113;
	
def chat_11113(player):
    player.getDH().sendNpcChat("Ah yes, let me see...", 589)
    player.nextDialogue = 245;
