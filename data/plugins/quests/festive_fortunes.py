###################################
###       Author: Owain		    ###
###       Date: 26/11/15		###
###	Re-written for DT: 22/11/17	###
###################################

#Originally wrote this for OwnXile, however re-used bits for Dawntained.

from game.content.quest import Quest
from game.content.quest import QuestHandler
from game.content.quest import QuestReward
from game.item import ItemAssistant
from game.content.skilling import Skilling

sled = 4084
logs = 1511
plank = 960
hammer = 2347
broken_sled = 4083

def configure_quest_2():
    quest_name = "Festive Fortunes"
    quest_stages = 7
    Quest.addQuest(quest_name, quest_stages)
   
def quest_button_2(player):
    quest_stage = player.getQuest(2).getStage()
    if quest_stage == 0: 
        QuestHandler.startInfo(player, quest_name, "@blu@Festive Fortunes" ,"@bla@I can start this quest" , "during the month of December" , "and by talking to @dre@Santa in Edgeville.")
    elif quest_stage == 1:
        player.boxMessage("I should investigate Santa's storeroom in East Ardougne")
    elif quest_stage == 2:
        player.boxMessage("I should travel to West Ardougne to find @dre@Santa's @bla@stolen items")
    elif quest_stage == 3:
        player.boxMessage("I should return to @dre@Santa @bla@to claim my reward.")
    elif quest_stage == 4:
        player.boxMessage("I need to have Santa's sled in my inventory to" , "complete the quest.")
    elif quest_stage == 6:
        player.boxMessage("Merry Christmas from everyone at Dawntained." , "You have completed @blu@Festive Fortunes.")    

def first_click_npc_11112(player):#Santa
    stage = player.getQuest(2).getStage()
    if stage == 0:
        player.getDH().sendDialogues(43849100)
    elif stage == 1:
        player.getDH().sendDialogues(43849110)
    elif stage == 5:
        player.getDH().sendDialogues(82137100)
    elif stage == 6:
        player.getDH().sendDialogues(82137108)   
    elif stage == 7:
        player.getDH().sendDialogues(73736)
    else:
        player.getDH().sendNpcChat("Come on, I haven't got all day!", 591)
       
def first_click_npc_5209(player):#Charlie the tramp
    stage = player.getQuest(2).getStage()
    if stage == 1:
        player.getDH().sendDialogues(7634754)
    elif stage == 2:
        player.getDH().sendNpcChat("Search those drawers behind you to find the sled.", 591)
    elif stage == 3:
        player.getDH().sendDialogues(70000)
    elif stage == 4:
        player.getDH().sendDialogues(70000)    
    else:
        player.getDH().sendNpcChat("Can't I s*** in peace?", 591) 
        
def first_click_npc_5422(player):#Sawmill npc
    player.getDH().sendDialogues(73750)       
        
def first_click_object_7194(player):
    stage = player.getQuest(2).getStage()#If player loses sled before they finish quest they can get another.
    if stage == 2:
        player.getDH().sendDialogues(555555)
    elif stage >=3 and stage <=6 and not ItemAssistant.hasItemInInventory(player, broken_sled):
        player.getDH().sendItemChat("", "You search the drawers and find the broken sled.", broken_sled, 200, 20, 0);
        player.startAnimation(832)
        ItemAssistant.addItemToInventoryOrDrop(player, broken_sled, 1)
    else:
        player.getDH().sendStatement("The drawers are empty.")  
        
def use_item_4083_on_960(player):
    HandleSledRepair(player)
    
def use_item_960_on_4083(player):
    HandleSledRepair(player)
    
def use_item_2347_on_4083(player):
    HandleSledRepair(player)
        
def use_item_4083_on_2347(player):
    HandleSledRepair(player)
    
def HandleSledRepair(player):
    stage = player.getQuest(2).getStage() #Added a check in for quest stage, players were getting bugged by trading plank over from main
    if stage == 4:
        if ItemAssistant.hasItemInInventory(player, hammer) and ItemAssistant.hasItemInInventory(player, plank):
            ItemAssistant.deleteItemFromInventory(player, plank, 1)
            ItemAssistant.deleteItemFromInventory(player, broken_sled, 1)
            ItemAssistant.addItemToInventoryOrDrop(player, sled, 1)
            player.getDH().sendItemChat("", "@blu@Congratulations, you just advanced a Sled fixing level!", "", "Your Sled fixing level is now 73.", "", 4084, 200, 20, 0);
            player.getPA().sendMessage("<col=0008f7>You should take the sled back to Santa.")
            player.gfx100(199)
            player.getQuest(2).setStage(5)
            QuestHandler.updateAllQuestTab(player);
        else:
            player.getPA().sendMessage("You need a hammer and a plank to fix the sled.")
    else:
        player.getPA().sendMessage("I should speak with the sawmill operator first before doing this.")
        
        
        
#Talking to Santa    
       
def chat_43849100(player):
    player.getDH().sendPlayerChat("Hey Santa!", 591)
    player.nextDialogue = 43849101;
   
def chat_43849101(player):
    player.getDH().sendNpcChat("What do you mean I only have 5% of my welfare" , "allowance left this month?!", 591)
    player.nextDialogue = 43849102;
 
def chat_43849102(player):
    player.getDH().sendPlayerChat("Wat.", 591)
    player.nextDialogue = 43849103;    
   
def chat_43849103(player):
    player.getDH().sendNpcChat("Oh, I beg your pardon "+ str(player.playerName) +", I didn't see you there.", "I'm in a spot of bother.", 591)
    player.nextDialogue = 43849105;
 
def chat_43849105(player):
    player.getDH().sendNpcChat("The local authority has seized my Sled. Apparently you" , "need something called insurance these days...", 591)
    player.nextDialogue = 43849106;  
   
def chat_43849106(player):
    player.getDH().sendNpcChat("Would you be able to help me get it back?", 591)
    player.nextDialogue = 43849107;
   
def chat_43849107(player):
    player.getDH().sendPlayerChat("Er... What's in it for me?", 591)
    player.nextDialogue = 43849108;
    
def chat_43849108(player):
    player.getDH().sendNpcChat("Ahem. Well. I suppose you might get an" , "early Christmas present...", 591)
    player.nextDialogue = 43849109;
    
def chat_43849109(player):
    player.getDH().sendPlayerChat("Sounds simple enough. What do I have to do?", 591)
    player.nextDialogue = 43849110;
   
def chat_43849110(player):
    player.getDH().sendNpcChat("Well, the last time that I saw my sled, was " , "in Varrock. I was staying at the Blue Moon Inn.", 591)
    player.nextDialogue = 43849111;
    
def chat_43849111(player):
    player.getDH().sendNpcChat("Maybe you could look around there to start.", 591)
    player.nextDialogue = 43849112;
   
def chat_43849112(player):
    player.getDH().sendPlayerChat("Oh dear, I suppose I'd better go and investigate.", 591)
    player.nextDialogue = 43849114;
   
def chat_43849114(player):
    player.getDH().sendNpcChat("Please hurry! Christmas depends on you "+ str(player.playerName) +".", 591)
    player.getQuest(2).setStage(1)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 43849115;
   
def chat_43849115(player):
    player.getDH().sendPlayerChat("Don't worry Santa, I'm on it!", 591)
   
  #Talking to Charlie the tramp!#
   
def chat_7634754(player):
    player.getDH().sendNpcChat("I didn't do it!", 591)
    player.nextDialogue = 7634755;
   
def chat_7634755(player):
    player.getDH().sendPlayerChat("Try explaining that to Santa!", 591)
    player.nextDialogue = 7634756;
   
def chat_7634756(player):
    player.getDH().sendNpcChat("Damn! I nearly got away with it as well.", 591)
    player.nextDialogue = 7634757;
   
def chat_7634757(player):
    player.getDH().sendPlayerChat("Whatever, I need you to give me what you took please.", 591)
    player.nextDialogue = 7634758;
   
def chat_7634758(player):
    player.getDH().sendNpcChat("Sorry, can't do that!", 591)
    player.nextDialogue = 7634759;
   
def chat_7634759(player):
    player.getDH().sendPlayerChat("Oh really, why's that then?", 591)
    player.nextDialogue = 7634710;
   
def chat_7634710(player):
    player.getDH().sendNpcChat("Already got a bid of $250 on eBay for that old sled!", 591)
    player.nextDialogue = 7634711;
   
def chat_7634711(player):
    player.getDH().sendPlayerChat("I don't care how many bids you have," , "it doesn't belong to you!", 591)
    player.nextDialogue = 7634712;
   
def chat_7634712(player):
    player.getDH().sendNpcChat("Okay okay... Search those drawers behind you," , "I stashed it in there for safe keeping.", 591)
    player.nextDialogue = 7634713;
   
def chat_7634713(player):
    player.getDH().sendPlayerChat("You put a sled in a chest of drawers?..", 591)
    player.nextDialogue = 7634714;
   
def chat_7634714(player):
    player.getDH().sendNpcChat("Why are you surprised? This is Dawntained not" , "real life, anything is possible.", 591)
    player.nextDialogue = 7634715;
   
def chat_7634715(player):
    player.getDH().sendPlayerChat("You have a good point.", 591)
    player.getQuest(2).setStage(2)
    QuestHandler.updateAllQuestTab(player);


def chat_555555(player):
    player.getDH().sendItemChat("", "You search the drawers and find the broken sled.", broken_sled, 200, 20, 0);
    player.getQuest(2).setStage(3)
    QuestHandler.updateAllQuestTab(player);
    player.startAnimation(832)
    ItemAssistant.addItemToInventoryOrDrop(player, broken_sled, 1)
    player.nextDialogue = 70000;     
    

def chat_70000(player):
    player.getDH().sendNpcChat("Oh, just to let you know it's a bit worse for wear." , "I went sledging with it and crashed into a tree...", 591)
    player.nextDialogue = 70001;
    
def chat_70001(player):
    player.getDH().sendPlayerChat("YOU DID WHAT??", 591)
    player.nextDialogue = 70002;
    
def chat_70002(player):
    player.getDH().sendPlayerChat("Santa will go mad if he finds out that its damaged!", 591)
    player.nextDialogue = 70003;
    
def chat_70003(player):
    player.getDH().sendNpcChat("You'll probably have to fix it up somehow.", "What would be the best thing to use?", 591)
    player.nextDialogue = 70004;
    
def chat_70004(player):
    player.getDH().sendPlayerChat("Would planks work?", 591)
    player.nextDialogue = 70005;
    
def chat_70005(player):
    player.getDH().sendNpcChat("Oh yeah, good thinking! The sawmill north of Varrock" , "should be able to help you with that.", 591)
    player.nextDialogue = 70006;
    
def chat_70006(player):
    player.getDH().sendPlayerChat("I'd better head to the sawmill then.", 591)
    player.getQuest(2).setStage(4)
    QuestHandler.updateAllQuestTab(player);
    
def chat_700004(player):
    player.getDH().sendItemChat("", "@blu@Congratulations, you just advanced a Sled fixing level!", "", "Your Sled fixing level is now 73.", "", 4084, 200, 20, 0);
    player.playerAssistant.sendFilterableMessage("Congratulations! Your Sled fixing level is now 73.");
    player.gfx100(199)
    
def chat_7634733(player):
    player.getDH().sendPlayerChat("I should go back to Santa and tell him the good news.", 591)
    
  #Returning to santa# 
def chat_82137100(player):
    player.getDH().sendPlayerChat("Santa! I managed to find your sled!", 591)
    player.nextDialogue = 82137101;
   
def chat_82137101(player):
    if ItemAssistant.hasItemInInventory(player, sled):
       player.getDH().sendDialogues(82137103)
    else:
       player.getDH().sendPlayerChat("Whoops, I forgot to bring it. I'll be back soon.", 591)     
   
def chat_82137103(player):
    player.getDH().sendNpcChat("Well done, "+ str(player.playerName) +", where did you find it?", 591)
    player.getQuest(2).setStage(6)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 82137104;
   
def chat_82137104(player):
    player.getDH().sendPlayerChat("Charlie the tramp stole it from your storeroom!", 591)
    player.nextDialogue = 82137105;
   
def chat_82137105(player):
    player.getDH().sendNpcChat("I should've known... the filthy so-and-so.", 591)
    player.nextDialogue = 82137106;
    
def chat_82137106(player):
    player.getDH().sendNpcChat("Oh, maybe I should re-locate my store too." , "It's not really secret now is it?", 591)
    player.nextDialogue = 82137107;
   
def chat_82137107(player):
    player.getDH().sendPlayerChat("No, I suppose not.", 591)
    player.nextDialogue = 82137108;
   
def chat_82137108(player):
    player.getDH().sendNpcChat("Thanks for helping by the way. I've got some other" , "rewards for you too!", 591)
    player.nextDialogue = 82137109;
   
def chat_82137109(player):
    player.getDH().sendPlayerChat("Wow! Thanks Santa!", 591)
    player.nextDialogue = 82137110;
    
def chat_82137110(player):
    player.getDH().sendPlayerChat("Don't you need your Sled though?", 591)
    player.nextDialogue = 82137111;
   
def chat_82137111(player):
    player.getDH().sendNpcChat("Well, I wouldn't want to be handling stolen goods now," , "would I?", 591)
    player.nextDialogue = 82137112;
    
def chat_82137112(player):
    player.getDH().sendPlayerChat("Eh?", 591)
    player.nextDialogue = 82137113;
   
def chat_82137113(player):
    player.getDH().sendNpcChat("Nothing. Merry Christmas!", 591)
    player.nextDialogue = 82137114;
    
def chat_82137114(player):
    player.getQuest(2).setStage(7)
    QuestHandler.updateAllQuestTab(player);
    Skilling.addSkillExperience(player, 1500000, ServerConstants.THIEVING, True);
    ItemAssistant.addItemToInventoryOrDrop(player, 20834, 1)   
    ItemAssistant.addItemToInventoryOrDrop(player, 20836, 1)  
    reward = QuestReward("Santa's sled", "Sack of presents", "Giant present", "The ability to unlock bonus rewards", "1.5m Thieving XP")
    player.completeQuest("Festive Fortunes",reward, 4084) #Sled id
    
#Conversation with sawmill npc
def chat_73750(player):
    player.getDH().sendNpcChat("Would you like me to cut some planks for you?", 591)
    player.nextDialogue = 73751;
    
def chat_73751(player):
    player.getDH().sendOptionDialogue("Yes please.", "Not right now.", 73752)
    
def option_one_73752(player):
    player.getDH().sendPlayerChat("Yes please.", 591)
    player.nextDialogue = 73753;
    
def option_two_73752(player):
    player.getDH().sendPlayerChat("Not right now.", 591)
    
def chat_73753(player):
    if ItemAssistant.hasItemInInventory(player, logs):
        amount = ItemAssistant.getItemAmount(player, logs)
        if ItemAssistant.hasItemAmountInInventory(player, logs, amount):
            ItemAssistant.deleteItemFromInventory(player, logs, amount)
            ItemAssistant.addItem(player, plank, amount)
            player.getDH().sendNpcChat("There you go sir.", 591)
            player.getPA().sendMessage("<col=0008f7>You should use the plank on the sled to repair it.")
    else:
        player.getDH().sendNpcChat("You don't have any logs for me to cut." , "I'm not a magician!", 591)
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
#Talking with Santa after the event to learn about snow piles
def chat_73736(player):
    player.getDH().sendNpcChat("Ah, hello again "+ str(player.playerName) +". Would you like me to teach" , "you about snow piles?", 591)
    player.nextDialogue = 73737;
    
def chat_73737(player):
    player.getDH().sendOptionDialogue("Sure thing.", "Not right now.", 73738)
    
def option_one_73738(player):
    player.getDH().sendDialogues(73742)  
    
def option_two_73738(player):
    player.getDH().sendDialogues(73740)
    
def chat_73740(player):
    player.getDH().sendPlayerChat("Not right now, Santa.", 591)
    player.nextDialogue = 73741;
    
def chat_73741(player):
    player.getDH().sendNpcChat("Not a problem, speak to me at any time if you change" , "your mind.", 591)
    
def chat_73742(player):
    player.getDH().sendPlayerChat("Sure thing.", 591)
    player.nextDialogue = 73743;
    
def chat_73743(player):
    player.getDH().sendNpcChat("Snow piles form at various locations around Dawntained" , "every 4 hours or so, type in ::events to see when it starts!", 591) 
    player.nextDialogue = 73744;
    
def chat_73744(player):
    player.getDH().sendNpcChat("Players that have completed my task can search" , "through the snow to have a chance at getting some rare" , "holiday rewards!", 591) 
    player.nextDialogue = 73745;
    
def chat_73745(player):
    player.getDH().sendNpcChat("A global announcement will be broadcast when a snow" , "pile forms.", 591)        