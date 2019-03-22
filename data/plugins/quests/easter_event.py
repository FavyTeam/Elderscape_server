# Name: 2018 Easter Event
# Written by Owain for Dawntained / Runefate, 27/03/18

from game.content.quest import Quest
from game.content.quest import QuestHandler
from game.content.quest import QuestReward
from game.item import ItemAssistant
from game.content.skilling import Skilling
from game.object import ObjectEvent
from game.content.miscellaneous import PlayerMiscContent

egg_mould = 22348
chocolate_chunks = 22345
bucket_of_chocolate = 4687
chocolate_egg = 22349

def configure_quest_6():
    quest_name = "2018 Easter Event"
    quest_stages = 3
    Quest.addQuest(quest_name, quest_stages)    
        
def first_click_object_31878(player):
    stage = player.getQuest(6).getStage()
    if stage == 0:
        player.setNpcType(7516)
        player.getDH().sendDialogues(19999)
    elif stage == 1 or stage == 2:
        player.getDH().sendDialogues(20019)
    else:
        player.getDH().sendStatement("You don't have a reason to enter the hole now.")
        
def enterHole(player):
    player.getPA().closeInterfaces(True);
    ObjectEvent.climbDownLadder(player, 2521 + Misc.random(2), 9321 + Misc.random(2), 0);
        
def item_22345_on_object_15300(player):
    PlayerMiscContent.chocolateCycle(player)
        
def first_click_object_19759(player):
    player.getPA().movePlayer(3101, 3501, 0)
def first_click_object_19760(player):
    player.getPA().movePlayer(3101, 3501, 0)     
    
def use_item_22348_on_4687(player):
    handleEggMaking(player)
    
def use_item_4687_on_22348(player):
    handleEggMaking(player)
    
def handleEggMaking(player):
    if ItemAssistant.hasItemInInventory(player, egg_mould) and ItemAssistant.hasItemInInventory(player, bucket_of_chocolate):
        ItemAssistant.deleteItemFromInventory(player, bucket_of_chocolate, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, 1925, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, chocolate_egg, 1)
        player.getDH().sendItemChat("", "You pour some chocolate into the mould. Somehow, it" , "sets straight away and you are able to make a" , "chocolate egg.", chocolate_egg, 200, 20, 0);

        
def chat_19999(player):
    player.getDH().sendNpcChat("Oi! Who said you could go down there?", 591)
    player.nextDialogue = 20000; 
       
def chat_20000(player):
    player.getDH().sendPlayerChat("Oh hey, it's the Easter Bunny!", 591)
    player.nextDialogue = 20001;
   
def chat_20001(player):
    player.getDH().sendNpcChat("Yes, that is me.", 591)
    player.nextDialogue = 20002;
 
def chat_20002(player):
    player.getDH().sendPlayerChat("Well then... you seem talkative.", 591)
    player.nextDialogue = 20003;    
   
def chat_20003(player):
    player.getDH().sendNpcChat("I'm sorry "+ str(player.playerName) +", I'm just not in the best of moods" , "at the moment.", 591)
    player.nextDialogue = 20004;
    
def chat_20004(player):
    player.getDH().sendPlayerChat("What's up doc?", 591)
    player.nextDialogue = 20005;  
 
def chat_20005(player):
    player.getDH().sendNpcChat("Hilarious. Anyway, I've come to the realisation that" , "I'm getting too old for all of this.", 591)
    player.nextDialogue = 20006;
    
def chat_20006(player):
    player.getDH().sendPlayerChat("Too old for what? Easter?", 591)
    player.nextDialogue = 20007;    
   
def chat_20007(player):
    player.getDH().sendNpcChat("Well, having to hand out Easter eggs and other " , "treats to my extended family.", 591)
    player.nextDialogue = 20008;
    
def chat_20008(player):
    player.getDH().sendNpcChat("I presume you know how quickly rabbits breed...", 591)
    player.nextDialogue = 20009;
   
def chat_20009(player):
    player.getDH().sendPlayerChat("So I've heard. Is there anything I can do to help?", 591)
    player.nextDialogue = 20010;
    
def chat_20010(player):
    player.getDH().sendNpcChat("I suppose you can, but I'm afraid it's nothing overly" , "eggciting. You could help to make some chocolate eggs, in" , "return for some rewards.", 591)
    player.nextDialogue = 20011;
    
def chat_20011(player):
    player.getDH().sendPlayerChat("Okay, that sounds simple enough. What do I have" , "to do?", 591)
    player.nextDialogue = 20012;
   
def chat_20012(player):
    player.getDH().sendNpcChat("I'll give you special access to my hidey hole, " , "down there you will find everything that you need" , "to make some eggs.", 591)
    player.nextDialogue = 20013;
    
def chat_20013(player):
    player.getDH().sendNpcChat("I need 10 eggs in total, so it won't take too long!", 591)
    player.nextDialogue = 20014;
   
def chat_20014(player):
    player.getDH().sendPlayerChat("Great. Is there anything else I need to know?", 591)
    player.nextDialogue = 200105;
    
def chat_200105(player):
    player.getDH().sendNpcChat("You'll need some buckets and this mould.", 591)
    player.nextDialogue = 200106;
    
def chat_200106(player):
    player.getDH().sendItemChat("", "You are handed an egg mould.", egg_mould, 200, 20, 0);
    ItemAssistant.addItemToInventoryOrDrop(player, egg_mould, 1);
    player.nextDialogue = 200107;
    
def chat_200107(player):
    player.getDH().sendNpcChat("Oh, I almost forgot. You'll need a pickaxe to mine through" , "the chocolate.", 591)
    player.nextDialogue = 20016;
    
def chat_20016(player):
    player.getDH().sendPlayerChat("Chocolate mining? What on earth...", 591)
    player.nextDialogue = 20017;
   
def chat_20017(player):
    player.getDH().sendNpcChat("Indeed. If you need any assistance "+ str(player.playerName) +", pop back here" , "and I'll be willing to help.", 591)
    player.getQuest(6).setStage(1)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 20018;
   
def chat_20018(player):
    player.getDH().sendPlayerChat("Okay thanks, I'll see what I can do.", 591)  
    

def chat_200150(player):
    player.setNpcType(7516)
    player.getDH().sendNpcChat("How are you getting on, "+ str(player.playerName) +"?", 591)
    player.nextDialogue = 200151;
       
def chat_200151(player):
    player.getDH().sendPlayerChat("I have 10 eggs for you right here.", 591)
    player.nextDialogue = 200154;
    
def chat_200154(player):
    player.getDH().sendNpcChat("Well done, "+ str(player.playerName) +", eggcellent work!" , "These are exactly how I would make them.", 591)
    player.getQuest(6).setStage(2)
    QuestHandler.updateAllQuestTab(player);
    ItemAssistant.deleteItemFromInventory(player, chocolate_egg, 10);
    player.nextDialogue = 200155;
    
def chat_200155(player):
    player.getDH().sendPlayerChat("Please stop with the terrible puns...", 591)
    player.nextDialogue = 200156;
    
def chat_200156(player):
    player.getDH().sendNpcChat("Hah. Anyway, thanks for helping me out. " , "My nephews will be pleased.", 591)
    player.nextDialogue = 200157;
    
def chat_200157(player):
    player.getDH().sendNpcChat("I found some old stock left over from last" , "year. I'm not sure if they're still edible, but its" , "worth a try I suppose.", 591)
    player.nextDialogue = 200158;
    
def chat_200158(player):
    player.getDH().sendNpcChat("You can have those as a reward, alongside some other" , "junk I found.", 591)
    player.nextDialogue = 200159;
    
def chat_200159(player):
    player.getDH().sendPlayerChat("Right... thanks I guess.", 591)
    player.nextDialogue = 200160;
    
def chat_200160(player):
    player.getDH().sendNpcChat("Happy Easter!", 591)
    player.nextDialogue = 200161;
    
    
    
def chat_20019(player):
    player.getDH().sendOptionDialogue("Talk to the Easter Bunny.", "Continue down hole.", 20020)
    
def option_one_20020(player):
    if ItemAssistant.hasItemAmountInInventory(player, chocolate_egg, 10):
       player.getDH().sendDialogues(200150)
    else:
        player.getDH().sendDialogues(20021)
    
def option_two_20020(player):
    enterHole(player)
    
def chat_20021(player):
    player.getDH().sendPlayerChat("Um, hello? Is anyone down there?", 591)
    player.nextDialogue = 20022;
    
def chat_20022(player):
    player.setNpcType(7516)
    player.getDH().sendNpcChat("Hello "+ str(player.playerName) +", did you need some help?", 591)
    player.nextDialogue = 20023;
    
def chat_20023(player):
    player.getDH().sendPlayerChat("Yes please.", 591)
    player.nextDialogue = 20024;
    
def chat_20024(player):
    player.getDH().sendNpcChat("Okay okay, so. You need to mine some chocolate chunks." , "Then you need a bucket. Put the chocolate into the crater" , "in the cave, then you'll get a bucket of chocolate.", 591)
    player.nextDialogue = 20025;
    
def chat_20025(player):
    player.getDH().sendNpcChat("You can then put the chocolate into an egg mould." , "Don't forget I need 10 eggs!", 591)
    player.nextDialogue = 20029;
    
def chat_20029(player):
    player.getDH().sendPlayerChat("That's great, thanks.", 591)
    
def chat_200161(player):
    player.getQuest(6).setStage(3)
    QuestHandler.updateAllQuestTab(player);
    if Misc.random(300) == 1:
        ItemAssistant.addItemToInventoryOrDrop(player, 1037, 1)   
        ItemAssistant.addItemToInventoryOrDrop(player, 13663, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, 13664, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, 13182, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, 1962, 15)
        reward = QuestReward("Bunny ears", "Bunny top", "Bunny legs", "Bunny feet", "5 Easter eggs")
        player.completeQuest("the 2018 Easter Event",reward, 1037)
    else:
        ItemAssistant.addItemToInventoryOrDrop(player, 21214, 1)   
        ItemAssistant.addItemToInventoryOrDrop(player, 22351, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, 22353, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, 1962, 5)
        reward = QuestReward("Easter egg helm", "Eggshell platebody", "Eggshell platelegs", "5 Easter eggs", "")
        player.completeQuest("the 2018 Easter Event",reward, 21214)
