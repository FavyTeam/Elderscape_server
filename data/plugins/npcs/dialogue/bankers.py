#A plugin to handle banker dialogues
#Written by Owain
#26/10/17

from game.content.bank import Bank

def first_click_npc_394(player):
    player.getDH().sendDialogues(8000)
def first_click_npc_395(player):
    player.getDH().sendDialogues(8000)
def first_click_npc_396(player):
    player.getDH().sendDialogues(8000)
def first_click_npc_397(player):
    player.getDH().sendDialogues(8000)
def first_click_npc_2897(player):
    player.getDH().sendDialogues(8000)
def first_click_npc_2898(player):
    player.getDH().sendDialogues(8000)
	
def second_click_npc_394(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
def second_click_npc_395(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
def second_click_npc_396(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
def second_click_npc_397(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
	
def second_click_npc_2897(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
def second_click_npc_2898(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
	
	
def chat_8000(player):
    player.getDH().sendNpcChat("Good day, how may I help you?", 591)
    player.nextDialogue = 8001;
	
def chat_8001(player):
    player.getDH().sendOptionDialogue("I'd like to access my bank account, please.", "I'd like to check my PIN settings.", "What is this place?", 8002)
	
def option_one_8002(player):
    player.setUsingBankSearch(False)
    Bank.openUpBank(player, player.getLastBankTabOpened(), True, True)
	
def option_two_8002(player):
    player.getDH().sendDialogues(116)

	
def option_three_8002(player):
    player.getDH().sendPlayerChat("What is this place?", 591)
    player.nextDialogue = 8003;
	
def chat_8003(player):
    player.getDH().sendNpcChat("This is a branch of the Bank of Runefate. We have" , "branches in many towns.", 591)