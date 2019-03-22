#Handles the holiday tool to spawn items
#Written by Owain for Dawntained
#15/10/17

from game.content.miscellaneous import PlayerMiscContent
from game.player import Player
from java.lang import System
from game.object.clip import Region
from core import Server
from utility import Misc
import random

easter_egg = 1961

holiday_items = [11019, 11020, 11021, 11022, 13663, 13664, 13665, 13182, 1037] #change this to include any items that will be picked at random
#Current items in the list: Xmas hats/scarfs, star baubles
#There is a 1/250 chance that an inverted santa hat is spawned, and a 1/10 chance that an item other than snowballs are spawned.

def first_click_item_11996(player):
    uses_left = player.toolUses
    if (System.currentTimeMillis() - player.buryDelay > 1200):
        player.buryDelay = System.currentTimeMillis();
        if uses_left <= 0:
	        player.getPA().sendMessage("<col=bc0000>You don't have any charges remaining. Ask MGT nicely and you might get some more...")
        else:
            HandleHolidayTool(player)
			
def second_click_item_11996(player):
    CheckUsesLeft(player)
	
def CheckUsesLeft(player):
    uses_left = player.toolUses
    if uses_left == 1:
        player.getPA().sendMessage("<col=af00bc>You have " + str(uses_left) + " charge remaining.")
    elif uses_left == 0:
        player.getPA().sendMessage("<col=af00bc>You don't have any charges remaining. Ask MGT nicely and you might get some more...")
    else:
        player.getPA().sendMessage("<col=af00bc>You have " + str(uses_left) + " charges remaining.")
			
def HandleUsesLeft(player):
    uses_left = player.toolUses
    player.toolUses -= 1
    if uses_left == 2:
        player.getPA().sendMessage("<col=af00bc>You can use this tool " + str(uses_left - 1) + " more time.")
    elif uses_left == 1:
        player.getPA().sendMessage("<col=af00bc>You've used up the last charge on the tool.")
    else:
        player.getPA().sendMessage("<col=af00bc>You can use this tool " + str(uses_left - 1) + " more times.")
		
def HandleHolidayTool(player):
    HandleUsesLeft(player)
    x = player.getX();
    y = player.getY();
    a = 0
    while a < 5:
        i = 0
        while i < 5:
            if (Region.getClipping(x + i, y + a, player.getHeight()) == 0):
                player.getPA().createPlayersStillGfx(144, x + i, y + a, 0, 0)
                if Misc.hasPercentageChance(5):
                    item = random.choice(holiday_items)
                    Server.itemHandler.createGroundItem(player, item, x + i, y + a, player.getHeight(), 1, 1, 0, 0, "", "", "", "", "HolidayTool")
                else:
                    Server.itemHandler.createGroundItem(player, easter_egg, x + i, y + a, player.getHeight(), 1, 1, 0, 0, "", "", "", "", "HolidayTool")
            i += 1
        a += 1
		