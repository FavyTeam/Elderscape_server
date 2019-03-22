#Original Release date: 9th January 2013
#Edits by Owain
#14/2/16

#from com.ownxile.rs2.Point import Position

from game.content.quest import Quest
from game.content.quest import QuestHandler
from game.content.quest import QuestReward
from game.item import ItemAssistant

monkeys_nan_id = 5270
noted_monkey_nuts = 4013
special_monkey_nuts = 7573

def configure_quest_0():
    quest_name = 'The Nut Hunt'
    quest_stages = 2
    Quest.addQuest(quest_name, quest_stages)

def quest_button_0(player):
    quest_name = 'The Nut Hunt'
    quest_stage = player.getQuest(0).getStage()
    if quest_stage == 0:
        QuestHandler.startInfo(player, quest_name, "I can start The Nut Hunt by talking to @dre@The Monkey's Nan@dbl@",  "in Edgeville.", "There are no requirements to start this quest.", "This quest takes roughly @dre@20@dbl@ minutes to complete.")
    elif quest_stage == 1:
        QuestHandler.startInfo(player, quest_name, "I need to bring @dre@1000 noted Monkey nuts@dbl@ for @dre@The Monkey's Nan@dbl@.", "", "", "")
    elif quest_stage == 2:
        QuestHandler.startInfo(player, quest_name, "I have completed @dre@The Nut Hunt@dbl@.", "", "", "")
        
def first_click_npc_5270(player):
    quest_stage = player.getQuest(0).getStage()
    if quest_stage == 0:
        player.getDH().sendDialogues(5000)
    elif quest_stage == 1 and ItemAssistant.hasItemAmountInInventory(player, noted_monkey_nuts, 1000):
        player.getDH().sendDialogues(5017)
    elif quest_stage == 1:
        player.getDH().sendNpcChat("Hurry up and get my nuts!", 595)
    elif quest_stage == 2:
        player.getDH().sendDialogues(5554)
    else:
        player.getDH().sendPlayerChat("He looks pretty grumpy,", "I better not disturb him.", 610)
    
def first_click_item_4012(player):
    handle_nut_click(player)
def first_click_item_7573(player):
    handle_nut_click(player)
    
def handle_nut_click(player):
    player.setNpcType(5270)
    player.getDH().sendNpcChat("Oi! Keep away from my nuts!", 605)
    
def item_1963_on_npc_5270(player):
    player.getDH().sendNpcChat("Just because I'm a monkey, that doesn't mean that" , "I want a banana you know. Don't be racist...", 605)
    
def chat_5000(player):
    player.getDH().sendNpcChat("Hello, do you have any monkey nuts?", 591)
    player.nextDialogue = 5001;

def chat_5001(player):
    player.getDH().sendPlayerChat("Uhhh maybe...", 591)
    player.nextDialogue = 5002;

def chat_5002(player):
    player.getDH().sendPlayerChat("...Why?", 591)
    player.nextDialogue = 5003;
    
def chat_5003(player):
    player.getDH().sendNpcChat("I need to get some for my nephews birthday.", 591)
    player.nextDialogue = 5004;
    
def chat_5004(player):
    player.getDH().sendPlayerChat("Ah I see.", 591)
    player.nextDialogue = 5900;
    
def chat_5900(player):
    player.getDH().sendPlayerChat("How many do you need?", 591)
    player.nextDialogue = 5006;
    
def chat_5006(player):
    player.getDH().sendNpcChat("Ooh I don't know.. how about...", 591)
    player.nextDialogue = 5007;
    
def chat_5007(player):
    player.getDH().sendNpcChat("OVER NINE THOUSAND!", 604)
    player.nextDialogue = 5008;
    
def chat_5008(player):
    player.getDH().sendPlayerChat("You have got to be kidding me...", 602)
    player.nextDialogue = 5009;
    
def chat_5009(player):
    player.getDH().sendNpcChat("Hahaha alright, how about 1000?", 591)
    player.nextDialogue = 5010;
    
def chat_5010(player):
    player.getDH().sendPlayerChat("I could manage that, but what's in it for me?", 591)
    player.nextDialogue = 5011;
    
def chat_5011(player):
    player.getDH().sendNpcChat("I'll happily pay you 1000 golden coins!", 591)
    player.nextDialogue = 5012;
    
def chat_5012(player):
    player.getDH().sendPlayerChat("Uhh no thanks!", 597)
    player.nextDialogue = 5013;
    
def chat_5013(player):
    player.getDH().sendNpcChat("2000?", 591)
    player.nextDialogue = 5014;
    
def chat_5014(player):
    player.getDH().sendPlayerChat("...", 597)
    player.nextDialogue = 5015;
    
def chat_5015(player):
    player.getDH().sendNpcChat("10 million?", 591)
    player.nextDialogue = 5016;
    
def chat_5016(player):
    player.getDH().sendPlayerChat("Now you're talking!", "I'll get right on it.", 605)
    player.getQuest(0).setStage(1)
    QuestHandler.updateAllQuestTab(player);
    
def chat_5017(player):
    player.getDH().sendPlayerChat("I have your nuts!", 591)
    player.nextDialogue = 5018;
    
def chat_5018(player):
    player.getDH().sendNpcChat("Excuse me?", 591)
    player.nextDialogue = 5019;
    
def chat_5019(player):
    player.getDH().sendPlayerChat("The monkey nuts.", 591)
    player.nextDialogue = 5020;
    
def chat_5020(player):
    player.getDH().sendNpcChat("Excellent, I should be able to live", "off these nuts for a few months!", 591)
    player.nextDialogue = 5021;
    
def chat_5021(player):
    player.getQuest(0).setStage(2)
    ItemAssistant.deleteItemFromInventory(player, noted_monkey_nuts, 1000)
    ItemAssistant.addItem(player, 995, 10000000)
    reward = QuestReward("10 Million coins", "Access to Nan's Nut Business", "1 Quest Point")
    player.completeQuest("The Nut Hunt", reward, 4012)

def chat_5554(player):
    player.getDH().sendOptionDialogue("Chat to her", "Ask about nuts", 5555)

def option_one_5555(player):
    player.getDH().sendNpcChat("Hello again "+ str(player.playerName) +", how are you?", 591)
    player.nextDialogue = 5023;

def chat_5023(player):
    player.getDH().sendPlayerChat("I'm great! Did those nuts last you long enough?", 591)
    player.nextDialogue = 5024;

def chat_5024(player):
    player.getDH().sendNpcChat("What nuts?", 591)
    player.nextDialogue = 5025;

def chat_5025(player):
    player.getDH().sendNpcChat("Oh, you mean...", 591)
    player.nextDialogue = 5026;

def chat_5026(player):
    player.getDH().sendNpcChat("Deez nuts! Hah, gottem! Hah!", 591)
    player.nextDialogue = 5027;

def chat_5027(player):
    player.getDH().sendPlayerChat("You really are an odd one aren't you...", 591)

def option_two_5555(player):
    if ItemAssistant.hasItemAmountInInventory(player, special_monkey_nuts, 1):
        player.getDH().sendDialogues(5060)
    else:
        player.getDH().sendDialogues(5049)

def chat_5049(player):
    player.getDH().sendPlayerChat("Do you need any more nuts?", 591)
    player.nextDialogue = 5050;

def chat_5050(player):
    player.getDH().sendNpcChat("Funny that you should say that," , "I've recently started a new business up.", 591)
    player.nextDialogue = 5051;

def chat_5051(player):
    player.getDH().sendNpcChat("Over on Ape Atoll, the monkey children are going" , "crazy for Tchiki monkey nuts.", 591)
    player.nextDialogue = 5052;

def chat_5052(player):
    player.getDH().sendPlayerChat("Really?", 591)
    player.nextDialogue = 5053;

def chat_5053(player):
    player.getDH().sendNpcChat("Indeed, I was going to ask you if you can" , "do me a favour.", 591)
    player.nextDialogue = 5054;

def chat_5054(player):
    player.getDH().sendPlayerChat("Go on...", 591)
    player.nextDialogue = 5055;

def chat_5055(player):
    player.getDH().sendNpcChat("Well, if you can collect some of those special nuts," , "I'll be happy to buy them off you for 500k each.", 591)
    player.nextDialogue = 5056;

def chat_5056(player):
    player.getDH().sendPlayerChat("You've got yourself a deal!" , "Where can I find them?", 591)
    player.nextDialogue = 5057;

def chat_5057(player):
    player.getDH().sendNpcChat("I've heard that they grow on the same bush as the" , "normal Monkey nuts, but the Tchiki variety" , "are much rarer.", 591)
    player.nextDialogue = 5058;

def chat_5058(player):
    player.getDH().sendPlayerChat("Okay, I'll go and collect some for you then!", 591)
	


def chat_5060(player):
    player.getDH().sendPlayerChat("I've got some special nuts for you!", 591)
    player.nextDialogue = 5061;
	
def chat_5061(player):
    amount = ItemAssistant.getItemAmount(player, special_monkey_nuts)
    totalprice = 500000 * amount
    if ItemAssistant.hasItemAmountInInventory(player, special_monkey_nuts, amount):
        ItemAssistant.deleteItemFromInventory(player, special_monkey_nuts, amount)
        ItemAssistant.addItem(player, 995, totalprice)
        ItemAssistant.resetItems(player, 3214) # Update inventory.
        player.getDH().sendNpcChat("Ah, wonderful, thanks for these!", 591)