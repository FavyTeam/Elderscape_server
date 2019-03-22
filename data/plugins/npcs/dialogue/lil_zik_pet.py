#Plugin to handle dialogue for Lil' zik pet (raids 2)
#Written by Owain
#12/09/18

dialogue_list = [10500, 10506, 10510, 10515]

def first_click_npc_8336(player):
    dialogueId = random.choice(dialogue_list)
    player.getDH().sendDialogues(dialogueId)
	
def chat_10500(player):
    player.getDH().sendPlayerChat("Hey Lil' Zik.", 591)
    player.nextDialogue = 10501;
    
def chat_10501(player):
    player.getDH().sendNpcChat("Stop.", 591)
    player.nextDialogue = 10502;
    
def chat_10502(player):
    player.getDH().sendNpcChat("Calling.", 591)
    player.nextDialogue = 10503;
    
def chat_10503(player):
    player.getDH().sendNpcChat("Me.", 591)
    player.nextDialogue = 10504;
    
def chat_10504(player):
    player.getDH().sendNpcChat("Little!", 591)
    player.nextDialogue = 10505;
    
def chat_10505(player):
    player.getDH().sendPlayerChat("Never!", 591)
    
def chat_10506(player):
    player.getDH().sendPlayerChat("You know... you're not like other spiders.", 591)
    player.nextDialogue = 10507;
    
def chat_10507(player):
    player.getDH().sendNpcChat("You know I hate it when you say that... please" , "leave me alone.", 591)
    player.nextDialogue = 10508;
    
def chat_10508(player):
    player.getDH().sendPlayerChat("But I earned you fair and square at the Theatre" , "of Blood! You're mine to keep.", 591)
    player.nextDialogue = 10509;
    
def chat_10509(player):
    player.getDH().sendNpcChat("...", 591)
    

def chat_10510(player):
    player.getDH().sendPlayerChat("Incy wincy Verzik climbed up the water spout...", 591)
    player.nextDialogue = 10511;
    
def chat_10511(player):
    player.getDH().sendPlayerChat("Down came the rain and washed poor Verzik out...", 591)
    player.nextDialogue = 10512;
    
def chat_10512(player):
    player.getDH().sendNpcChat("Out came the Vampyre to put an end to this at" , "once. Humans deserve only one fate!", 591)
    player.nextDialogue = 10513;
    
def chat_10513(player):
    player.getDH().sendPlayerChat("Wow, calm down. It's just a nursery rhyme.", 591)
    player.nextDialogue = 10514;
    
def chat_10514(player):
    player.getDH().sendNpcChat("I'm not in the mood.", 591)
    

def chat_10515(player):
    player.getDH().sendPlayerChat("Hi, I'm here for my reward!", 591)
    player.nextDialogue = 10516;
    
def chat_10516(player):
    player.getDH().sendNpcChat("Not again...", 591)
