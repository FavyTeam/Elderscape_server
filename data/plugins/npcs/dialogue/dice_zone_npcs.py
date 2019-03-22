#A plugin to handle dilaogues for NPCs at the gamble area
#Written by Owain
#06/03/18

from game.content.bank import Bank
from game.content.clanchat import ClanChatHandler


#Bankers
def first_click_npc_6939(player):
    player.getDH().sendDialogues(80000)
def first_click_npc_6940(player):
    player.getDH().sendDialogues(80000)
def first_click_npc_6941(player):
    player.getDH().sendDialogues(80000)
def first_click_npc_6942(player):
    player.getDH().sendDialogues(80000)
	
#Party pete
def first_click_npc_11201(player):
    player.getDH().sendDialogues(454545)

#Bankers
def second_click_npc_6939(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
def second_click_npc_6940(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
def second_click_npc_6941(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
def second_click_npc_6942(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
	
	
def chat_80000(player):
    player.getDH().sendNpcChat("Wow this music is amazing!", 591)
    player.nextDialogue = 80001;
	
def chat_80001(player):
    player.getDH().sendOptionDialogue("What music?", "It is, isn't it!", 80002)
	
def option_one_80002(player):
    player.getDH().sendPlayerChat("What music? I can't hear anything...", 591)
    player.nextDialogue = 80004;
	
def option_two_80002(player):
    player.getDH().sendPlayerChat("It is, isn't it!", 591)
    player.nextDialogue = 80003;
	
def chat_80003(player):
    player.getDH().sendNpcChat("I'm glad you agree. Now, if you will excuse me...", 591)
	
def chat_80004(player):
    player.getDH().sendNpcChat("Very funny.", 591)
    
def chat_454545(player):
    player.getDH().sendNpcChat("Hi there, can I help you with anything?", 591)
    player.nextDialogue = 454546;
    
def chat_454546(player):
    player.getDH().sendOptionDialogue("View gambling rules.", "What is this place?", "Nothing.", 454547)
    
def option_one_454547(player):
    ClanChatHandler.displayDiceRulesInterface(player)
    
def option_two_454547(player):
    player.getDH().sendPlayerChat("What is this place?", 591)
    player.nextDialogue = 454548; 
    
def chat_454548(player):
    player.getDH().sendNpcChat("This is " + ServerConstants.getServerName() + "'s gambling area. Players may host" , "dice and flower based games here.", 591)
    player.nextDialogue = 454546;    
    
    
    
def option_three_454547(player):
    player.getPA().closeInterfaces(True);
    