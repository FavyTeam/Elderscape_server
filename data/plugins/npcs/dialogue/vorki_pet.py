#Plugin to handle dialogue for Vorki (Vorkath) pet
#Written by Owain
#15/08/18


dialogue_ids = [8501, 8502, 8503, 8504, 8503, 8504, 8505, 8506, 8507, 8508, 8509, 8510]

def first_click_npc_8025(player):
    player.getDH().sendDialogues(8500)
	
def chat_8500(player):
    player.getDH().sendPlayerChat("Hey Vorki, got any interesting dragon facts?", 591)
    dialogue = random.choice(dialogue_ids)
    player.nextDialogue = dialogue;
	
def chat_8501(player):
    player.getDH().sendNpcChat("Although they have wings, dragons rarely fly. This is because" , "the animals they prey on are all ground dwelling.", 591)
    
def chat_8502(player):
    player.getDH().sendNpcChat("Unlike their creators, dragons have the ability to reproduce." , "Like most reptiles, they are oviparous. This means that they" , "lay eggs rather than birthing live young.", 591)
    
def chat_8503(player):
    player.getDH().sendNpcChat("Dragons have a very long lifespan and can live for thousands" , "of years. With a lifespan that long, most dragons die to" , "combat instead of age.", 591)
    
def chat_8504(player):
    player.getDH().sendNpcChat("While very closely related, dragons and wyverns are" , "actually different species. You can easily tell the difference" , "between them by counting the number of legs, dragons" , "have four while wyverns have two.", 591)

def chat_8505(player):
    player.getDH().sendNpcChat("Metallic dragons were created by inserting molten metal" , "into the eggs of other dragons. Very few eggs survived" , "this process.", 591)

def chat_8506(player):
    player.getDH().sendNpcChat("The dragonkin created dragons by fusing their own lifeblood" , "with that of a lizard. The dragonkin created other species" , "in similar ways by using different types of reptile.", 591)

def chat_8507(player):
    player.getDH().sendNpcChat("Dragons have the ability to speak. However, most dragons" , "don't have the brain capacity to do it very well.", 591)

def chat_8508(player):
    player.getDH().sendNpcChat("Dragons share their name with dragon equipment, which" , "was also created by the dragonkin. This equipment is" , "fashioned out of Orikalkum.", 591)

def chat_8509(player):
    player.getDH().sendNpcChat("Although very aggressive, dragons do not typically stray from" , "their own territory. They instead make their homes in places" , "where there is plenty of prey to be found.", 591)

def chat_8510(player):
    player.getDH().sendNpcChat("Dragons have a duct in their mouth from which they" , "can expel various internally produced fluids. The most common" , "of these is a fluid which ignites when it reacts" , "with air. This is how dragons breathe fire.", 591)





