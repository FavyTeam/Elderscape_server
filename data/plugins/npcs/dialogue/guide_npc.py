#Plugin to handle dialogue for the Guide of Runefate
#Written by Owain for Runefate
#31/01/18
from core import ServerConstants
from game.content.achievement import Achievements
from game.item import ItemAssistant
from game.content.miscellaneous import PlayerGameTime

#def first_click_npc_306(player):
    #Achievements.checkCompletionSingle(player, 1001);
    #player.getDH().sendDialogues(15000)
        
def chat_15000(player):
    player.getDH().sendNpcChat("Hi there, is there anything I can help you with?", 589)
    player.nextDialogue = 15001;
    
def chat_15001(player):
    player.getDH().sendOptionDialogue("View general settings", "Ask about Veteran cape", "View guides", 15002)
    
def option_one_15002(player):
    player.getDH().sendDialogues(217)
    
def option_two_15002(player):
    player.getDH().sendPlayerChat("What cape is that you're wearing?", 589)
    player.nextDialogue = 15003;
    
def option_three_15002(player):
    player.getDH().sendStatement("View player made guides on the forum via ::guides.")
    
def chat_15003(player):
    player.getDH().sendNpcChat("It's a Veteran cape. It shows that I have been a part" , "of the " + ServerConstants.getServerName() + " community for at least 200 days.", 589)
    player.nextDialogue = 15004;
    
def chat_15004(player):
    player.getDH().sendPlayerChat("Am I eligible to claim one please?", 589)
    player.nextDialogue = 15005;
    
def chat_15005(player):
    player.getDH().sendNpcChat("Let me see...", 589)
    player.nextDialogue = 15006;
    
def chat_15006(player):
    if PlayerGameTime.getDaysSinceAccountCreated(player) >= 200:
        player.getDH().sendNpcChat("Great news "+ str(player.playerName) +", you are able to claim your cape!", 589)
        player.nextDialogue = 15007;
    else:
        player.getDH().sendNpcChat("Sorry "+ str(player.playerName) +", you need to have played for at least" , "200 days to claim a Veteran cape.", 589)
    
def chat_15007(player):
    player.getDH().sendItemChat("", "You are handed a Veteran cape!", 14013, 200, 14, 0);
    ItemAssistant.addItemToInventoryOrDrop(player, 14013, 1)