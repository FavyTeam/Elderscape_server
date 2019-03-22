def first_click_npc_7941(player):
    player.getDH().sendDialogues(15000)
	
def chat_15000(player):
    player.getDH().sendNpcChat("What are you doing out here "+ str(player.playerName) +"? It's" , "extremely dangerous you know!", 589)
    player.nextDialogue = 15001;
	
def chat_15001(player):
    player.getDH().sendPlayerChat("I was wondering if you could help me convert some" , "emblems that I might have...", 589)
    player.nextDialogue = 15002;
	
def chat_15002(player):
    player.getDH().sendNpcChat("I can indeed, please use the type of emblem that" , "you want converting on me and I'll see what" , "I can do.", 589)