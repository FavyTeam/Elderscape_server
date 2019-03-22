#Plugin to handle dialogues for men and women
#Written by Owain for Runefate
#31/01/18

#I want to give every spawned NPC a use, and not do what most servers have with useless non functioning NPCs.
#Even small things such as NPC dialogues can help make Runefate more rounded and enjoyable.


npc_ids = [3078, 3079]
dialogue_ids = [7502, 7504, 7506, 7508]

def first_click_npc_3078(player):
    player.getDH().sendDialogues(7500)
	
def chat_7500(player):
    player.getDH().sendPlayerChat("Hi there.", 591)
    dialogue = random.choice(dialogue_ids)
    player.nextDialogue = dialogue;
	
def chat_7502(player):
    player.getDH().sendNpcChat("Hello, lovely day today isn't it?", 589)
    
def chat_7504(player):
    player.getDH().sendNpcChat("Can't stop, I'm rather busy.", 589)
    
def chat_7506(player):
    player.getDH().sendNpcChat("Hey! How are you today?", 589)
    player.nextDialogue = 7507;
    
def chat_7507(player):
    player.getDH().sendPlayerChat("I'm great thanks.", 591)
    
def chat_7508(player):
    player.getDH().sendNpcChat("*Cough* *Splutter*", 589)
    player.nextDialogue = 7509;
    
def chat_7509(player):
    player.getDH().sendPlayerChat("Oh dear, I think that might be contagious...", 591)


