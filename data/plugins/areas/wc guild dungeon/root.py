#Plugin to handle Root shortcuts in the WC Guild dungeon
#Written by Owain
#21/10/17
from game.content.skilling.agility import AgilityShortcuts
from core import ServerConstants

def first_click_object_26721(player):		
    if player.baseSkillLevel[ServerConstants.AGILITY] < 50:
        player.playerAssistant.sendMessage("You need 50 agility to use this shortcut.")
        return 
    player.resetPlayerTurn();
    if player.getX() == 1590:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, -2, 0, 1588, 9899)
    if player.getX() == 1588:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, 2, 0, 1590, 9899)
    if player.getY() == 9880:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, 0, 2, 1551, 9882)
    if player.getY() == 9882:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, 0, -2, 1551, 9880)
	return

def first_click_object_26720(player):		
    if player.baseSkillLevel[ServerConstants.AGILITY] < 50:
        player.playerAssistant.sendMessage("You need 50 agility to use this shortcut.")
        return 
    player.resetPlayerTurn();
    if player.getX() == 1590:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, -2, 0, 1588, 9900)
    if player.getX() == 1588:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, 2, 0, 1590, 9900)
    if player.getY() == 9870:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, 0, 2, 1555, 9872)
    if player.getY() == 9872:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, 0, -2, 1555, 9870)
    if player.getY() == 9880:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, 0, 2, 1552, 9882)
    if player.getY() == 9882:
        AgilityShortcuts.performAgilityShortcut(player, 839, False, 0, -2, 1552, 9880)
    return 		