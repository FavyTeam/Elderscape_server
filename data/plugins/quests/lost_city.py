from game.content.quest import Quest
from game.content.quest import QuestHandler
from game.content.quest import QuestReward
from game.item import ItemAssistant
from core import ServerConstants


def configure_quest_3():
    quest_name = 'Lost City'
    quest_stages = 4
    Quest.addQuest(quest_name, quest_stages)
    
def quest_button_3(player):
    quest_name = 'Lost City'
    quest_stage = player.getQuest(3).getStage()
    if quest_stage == 0: 
        QuestHandler.startInfo(player, quest_name, "I can start this quest by speaking to the @dre@Adventurers@dbl@ in",  "@dre@Lumbridge swamp@dbl@. I need @dre@31 Crafting@dbl@, @dre@36 Woodcutting@dbl@, and" , "be able to defeat a @dre@level 101@dbl@ Tree spirit.", "This quest takes roughly @dre@5@dbl@ minutes to complete.")
    elif quest_stage == 1:
        QuestHandler.startInfo(player, quest_name, "I need to find the @dre@Leprechaun@dbl@, it must know more about how" , "I can find @dre@Zanaris@dbl@.", "", "")
    elif quest_stage == 2:
        QuestHandler.startInfo(player, quest_name, "I need to chop a @dre@Dramen tree@dbl@ to obtain a staff made" , "from Dramenwood, the @dre@Leprechaun@dbl@ said I could find such wood", "in a cave on the island of @dre@Entrana@dbl@.", "")
    elif quest_stage == 3: 
        QuestHandler.startInfo(player, quest_name, "I must use the @dre@Dramenwood staff@dbl@ obtained from @dre@Entrana@dbl@" , "to enter the city of @dre@Zanaris@dbl@ via the shed" , "in @dre@Lumbridge swamp@dbl@.", "")
    elif quest_stage == 4:
        QuestHandler.startInfo(player, quest_name, "I have completed @dre@" + quest_name + "@bla@.", "", "", "")

def first_click_npc_1162(player):
    player.getDH().sendDialogues(352061)
def first_click_npc_1157(player):
    player.getDH().sendDialogues(352034)
def first_click_npc_1158(player):
    player.getDH().sendDialogues(352034)
def first_click_npc_1159(player):
    player.getDH().sendDialogues(352034)
def first_click_npc_1160(player):
    player.getDH().sendDialogues(352034)

def kill_npc_1163(player):
    quest_stage = player.getQuest(3).getStage()
    if quest_stage == 2:
        player.getQuest(3).setStage(3)
        player.getPA().sendMessage("Carve the branch into a staff, then enter the shed in Lumbridge swamp to finish.")
        
def use_item_946_on_771(player):
    handle_staff_making(player)
    
def use_item_771_on_946(player):
    handle_staff_making(player)
    
def handle_staff_making(player):
    player.getPA().sendMessage("You carve the branch into a staff.")
    ItemAssistant.deleteItemFromInventory(player, 771, 1)
    ItemAssistant.addItemToInventoryOrDrop(player, 772, 1)

    
def first_click_object_1292(player):
    quest_stage = player.getQuest(3).getStage()
    if quest_stage == 2:
        NpcHandler.spawnNpc(player, 1163, 2858, 9733, 0, True, True);
        player.getDH().sendStatement("A spirit escapes from the tree.")
    elif quest_stage < 2:
        player.getPA().sendMessage("Nothing interesting happens.")
    elif quest_stage > 2:
        PlayerMiscContent.dramenTree(player)


def chat_352034(player):
    player.getDH().sendNpcChat("Hello there traveller.", 591)
    player.nextDialogue = 352035;
    
def chat_352035(player):
    player.getDH().sendOptionDialogue("What are you camped out here for?", "Do you know any good adventures I can go on?", 352038)
    
def option_two_352038(player):
    player.getDH().sendPlayerChat("Do you know any good adventures I can go on?", 591)
    player.nextDialogue = 352039;
    
def chat_352039(player):
    player.getDH().sendNpcChat("Well we're on an adventure right now. Mind you, this", "is OUR adventure and we don't want to share it - find", "your own!", 591)
    player.nextDialogue = 352040;

def chat_352040(player):
    quest_stage = player.getQuest(3).getStage()
    requirement = player.baseSkillLevel[ServerConstants.CRAFTING] < 30 and player.baseSkillLevel[ServerConstants.WOODCUTTING] < 35
    if quest_stage == 0:
        player.getDH().sendOptionDialogue("Please tell me.", "I don't think you've found a good adventure at all!", 352045)
    else:
        player.getDH().sendPlayerChat("Oh, okay then.", 591)

def option_one_352045(player):
    player.getDH().sendPlayerChat("Please tell me.", 591)
    player.nextDialogue = 352042;
    
def chat_352042(player):
    player.getDH().sendNpcChat("You aren't getting any information out of me!", 591)

def option_two_352045(player):
    player.getDH().sendPlayerChat("I don't think you've found a good adventure at all!", 591)
    player.nextDialogue = 352046;

def chat_352046(player):
    player.getDH().sendNpcChat("Hah! Adventurers of our calibre don't just hang around", "in forests for fun, whelp!", 591)
    player.nextDialogue = 352047;

def chat_352047(player):
    player.getDH().sendPlayerChat("Oh really?", 591)
    player.nextDialogue = 352038;
    
def option_one_352038(player):
    player.getDH().sendPlayerChat("What are you camped out here for?", 591)
    player.nextDialogue = 352049;
    
def chat_352038(player):
    player.getDH().sendPlayerChat("What are you camped out here for?", 591)
    player.nextDialogue = 352049;
    
def chat_352049(player):
    player.getDH().sendNpcChat("We're looking for Zanaris...GAH! I mean we're not", "here for any particular reason at all.", 591)
    player.nextDialogue = 352050;

def chat_352050(player):
    player.getDH().sendPlayerChat("Who's Zanaris?", 591)
    player.nextDialogue = 352051;
    
def chat_352051(player):
    player.getDH().sendNpcChat("Ahahaha! Zanaris isn't a person! It's a magical hidden", "city filled with treasures and rich... uh, nothing. It's", "nothing.", 591)
    player.nextDialogue = 352052;
    
def chat_352052(player):
    player.getDH().sendPlayerChat("If it's hidden, how are you planning to find it?", 591)
    player.nextDialogue = 352053;

def chat_352053(player):
    player.getDH().sendNpcChat("Well, we don't want to tell anyone else about that,", "because we don't want anyone else sharing in all that", "glory and treasure.", 591)
    player.nextDialogue = 352054;
    
def chat_352054(player):
    player.getDH().sendPlayerChat("Well it looks to me like YOU don't know EITHER", "seeing as you're all just sat around here.", 591)
    player.nextDialogue = 352055;
    
def chat_352055(player):
    player.getDH().sendNpcChat("Of course we know!", "We just haven't found which tree", "the stupid leprechaun's hiding in yet!", 591)
    player.nextDialogue = 352056;
    
def chat_352056(player):
    player.getDH().sendNpcChat("GAH! I didn't mean to tell you that! Look, just forget I", "said anything in okay?", 591)
    player.nextDialogue = 352057;
    
def chat_352057(player):
    player.getDH().sendPlayerChat("So a leprechaun knows where Zanaris is eh?", 591)
    player.nextDialogue = 352058;

def chat_352058(player):
    player.getDH().sendNpcChat("Ye.. uh, no. No, not at all. And even if he did - which", "he doesn't - he DEFINITELY ISN'T hiding in some", "trees around here. Nope, definitely. Honestly.", 591)
    player.nextDialogue = 352059;
    player.getQuest(3).setStage(1)
    QuestHandler.updateAllQuestTab(player);

def chat_352059(player):
    player.getDH().sendPlayerChat("Thanks for the help!", 591)
    player.nextDialogue = 352060;

def chat_352060(player):
    player.getDH().sendNpcChat("Help? What help? I didn't help! Please don't say I did,", "I'll get in trouble!", 591)
    
def chat_352061(player):
    player.getDH().sendNpcChat("Ay yer big elephant! Yer've caught me, to be sure!", "What would an elephant like yer be wanting wid o'l", "Shamus then?", 591)
    player.nextDialogue = 352062;

def chat_352062(player):
    quest_stage = player.getQuest(3).getStage()
    if quest_stage == 1: 
        player.getDH().sendPlayerChat("I want to find Zanaris.", 591)
        player.nextDialogue = 352063;
    else:
        player.getDH().sendPlayerChat("Nothing you little green bellend.", 591)

def chat_352063(player):
    player.getDH().sendNpcChat("Zanaris is it now? Well well well... Yer'll be needing to", "be going to that funny little out there in the", "swamp, so you will.", 591)
    player.nextDialogue = 352064;
    
def chat_352064(player):
    player.getDH().sendPlayerChat("...but... I thought... Zanaris was a city...?", 591)
    player.nextDialogue = 352065;
    
def chat_352065(player):
    player.getDH().sendNpcChat("Aye that it is!", 591)
    player.nextDialogue = 352066;
    
def chat_352066(player):
    player.getDH().sendPlayerChat("I've been in that shed, I didn't see a city.", 591)
    player.nextDialogue = 352067;

def chat_352067(player):
    player.getDH().sendNpcChat("Oh, was I fergetting to say? Yer need to be carrying a", "Dramenwood staff to be getting there! Otherwise Yer'll", "just be ending up in the shed.", 591)
    player.nextDialogue = 352068;
    
def chat_352068(player):
    player.getDH().sendNpcChat("Dramenwood staffs are crafted from branches of the", "Dramen tree, so they are. I hear there's a Dramen", "tree over on the island of Entrana in a cave...", 591)
    player.nextDialogue = 352069;
    
def chat_352069(player):
    player.getDH().sendNpcChat("...or some such. There would probably be a good place", "for an elephant like yer to be starting looking I reckon.", 591)
    player.getQuest(3).setStage(2)
    QuestHandler.updateAllQuestTab(player);
    
def finish_quest(player):
    player.getQuest(3).setStage(4)
    quest_name = "Lost City"
    QuestHandler.updateAllQuestTab(player);
    reward = QuestReward("3 Quest Points", "Access to Zanaris", "Ability to wield some Dragon weapons")
    player.completeQuest("" + quest_name + "", reward, 772)
    player.qp += 3