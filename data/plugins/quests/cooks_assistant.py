# Quest Name: Cooks Assistant
# Quest Authors: Cam
# Date Created: 14/09/13
# Quest Length: Short
# Heavily revised by Owain for Runefate, 03/02/18

egg = 1944
bucket_of_milk = 1927
pot_of_flour = 1933
cooking_gauntlets = 775

def configure_quest_4():
    quest_name = "Cook's Assistant"
    quest_stages = 2
    Quest.addQuest(quest_name, quest_stages)
	
def quest_button_4(player):
    quest_name = "Cook's Assistant"
    quest_stage = player.getQuest(4).getStage()
    if quest_stage == 0: 
        QuestHandler.startInfo(player, quest_name, "I can start this quest by speaking to the @dre@Cook@dbl@ in the",  "@dre@Kitchen@dbl@ on the ground floor of @dre@Lumbridge Castle@dbl@." , "This quest takes roughly @dre@2-5@dbl@ minutes to complete.", "")
    elif quest_stage == 1:
        QuestHandler.startInfo(player, quest_name, "The @dre@Cook@dbl@ needs the following ingredients:", "@dre@Egg", "@dre@Pot of flour", "@dre@Bucket of milk")
    elif quest_stage == 2:
        QuestHandler.startInfo(player, quest_name, "I have completed @dre@" + quest_name + "@dbl@.", "", "", "")

def first_click_npc_4626(player):
    quest_stage = player.getQuest(4).getStage()
    if quest_stage == 0:
        player.getDH().sendDialogues(3468202)
    elif quest_stage == 1 and ItemAssistant.hasItemInInventory(player, egg) and ItemAssistant.hasItemInInventory(player, pot_of_flour) and ItemAssistant.hasItemInInventory(player, bucket_of_milk):
        player.getDH().sendDialogues(3468212)
    elif quest_stage == 1:
        player.getDH().sendDialogues(3468209)
    elif quest_stage == 2:
        player.getDH().sendNpcChat("Thanks for helping with the dukes birthday!", 591)

def chat_3468202(player):
    player.getDH().sendPlayerChat("You look sad! What's wrong?", 591)
    player.nextDialogue = 3468203;
	
def chat_3468203(player):
    player.getDH().sendNpcChat("Oh dear, oh dear, oh dear, I'm in a terrible, terrible", "mess! It's the Duke's birthday today, and I should be", "making him a lovely big birthday cake.", 591)
    player.nextDialogue = 3468204;
	
def chat_3468204(player): 
    player.getDH().sendNpcChat("I've forgotten to buy the ingredients. I'll never get" , "them in time now. He'll sack me! What ever will I do? I have" , "four children and a goat to look after. Would you help" , "me? Please?", 591)
    player.nextDialogue = 523010;

def chat_523010(player):
    player.getDH().sendOptionDialogue("I'm always happy to help a cook in distress.", "I can't right now, maybe later.", 3468206)    

def option_one_3468206(player):
    player.getDH().sendPlayerChat("Yes I'll help you.", 591)
    player.getQuest(4).setStage(1)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 3468207;

def option_two_3468206(player):
    player.getDH().sendPlayerChat("I can't right now, maybe later.", 591)
    player.nextDialogue = 3468208;
    
def chat_3468208(player):
    player.getDH().sendNpcChat("Oh, okay then...", 591)

def chat_3468207(player):
    player.getDH().sendNpcChat("Oh thank you, thank you. I need milk, an egg and" , "flour. I'd be very grateful if you can get them for me.", 591)
    player.nextDialogue = 346828;
    
def chat_346828(player):
    player.getDH().sendPlayerChat("Okay, I'll be back soon.", 591)

def chat_3468209(player):
    player.getDH().sendNpcChat("How are you getting on with finding the ingredients?", 591)
    player.nextDialogue = 3468210;

def chat_3468210(player):
    player.getDH().sendPlayerChat("I haven't got all of them yet, I'm still looking.", 591)
    player.nextDialogue = 3468211;

def chat_3468211(player):
    player.getDH().sendNpcChat("Please get the ingredients quickly. I'm running out of time!", "The Duke will throw me onto the street!", 591)

def chat_3468212(player):
    player.getDH().sendPlayerChat("I've got the ingredients!", 591)
    player.nextDialogue = 3468213;

def chat_3468213(player):
    player.getDH().sendNpcChat("You've got everything I need! I am saved!", "Thank you!", 591)
    player.nextDialogue = 3468214;

def chat_3468214(player):
    player.getDH().sendPlayerChat("So do I get to go to the Duke's party?", 591)
    player.nextDialogue = 3468215;

def chat_3468215(player):
    player.getDH().sendNpcChat("I'm afraid not. Only the big cheeses get to dine with the" , "Duke.", 591)
    player.nextDialogue = 3468216;

def chat_3468216(player):
    player.getDH().sendPlayerChat("Well, maybe one day I'll be important enough to sit at" , "the Duke's table.", 591)
    player.nextDialogue = 3468217;

def chat_3468217(player):
    player.getDH().sendNpcChat("Maybe, but I won't be holding my breath.", 591)
    player.nextDialogue = 3468218;
	
def chat_3468218(player):
    player.getQuest(4).setStage(2)
    quest_name = "Cook's Assistant"
    ItemAssistant.deleteItemFromInventory(player, egg, 1)
    ItemAssistant.deleteItemFromInventory(player, bucket_of_milk, 1)
    ItemAssistant.deleteItemFromInventory(player, pot_of_flour, 1)
    ItemAssistant.addItemToInventoryOrDrop(player, cooking_gauntlets, 1)
    
    Skilling.addSkillExperience(player, 5000, 7, True);
    reward = QuestReward("1 Quest Point", "5000 Cooking XP", "Cooking gauntlets")
    player.completeQuest("" + quest_name + "", reward, 1891)