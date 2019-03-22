# Dawntained h'ween event 2017
# Written by Owain
# 07/10/17

from game.content.miscellaneous import PlayerMiscContent
from game.content.miscellaneous import Teleport
from game.content.quest import Quest
from game.content.quest import QuestHandler
from game.content.quest import QuestReward
from game.item import ItemAssistant
from game.npc import NpcHandler
from game.object import ObjectEvent
from game.player import Area
from utility import Misc


soul_rune = 566
pot = 4045
fire = 1393
water = 1395
air = 1397
earth = 1399
mould = 1597

bones = 16095

mask = 20773
top = 20775
robe = 20777
knife = 20779
bracelet = 16096

black_ween = 11847

def configure_quest_1():
    quest_name = 'Haunted Happenings'
    quest_stages = 14
    Quest.addQuest(quest_name, quest_stages)

def first_click_npc_7632(player):  # main npc, shady figure
    quest_stage = player.getQuest(1).getStage()
    if quest_stage == 0:
        player.getDH().sendDialogues(5999)
    elif quest_stage == 1:
        player.getDH().sendDialogues(6030)    
    elif quest_stage == 2:
        player.getDH().sendDialogues(6037)         
    elif quest_stage == 3:
        player.getDH().sendDialogues(6038)
    elif quest_stage == 4:
        player.getDH().sendDialogues(6040)
    elif quest_stage == 5:
        player.getDH().sendDialogues(6055)
    elif quest_stage == 6:
        player.getDH().sendDialogues(6161)
    elif quest_stage == 13:
        player.getDH().sendDialogues(6210)
    elif quest_stage <= 12:
        Teleport.startTeleport(player, 3543, 3466, 0, "ARCEUUS")
        player.getDH().sendStatement("You are teleported to the Haunted woods.")
    else:
        player.getDH().sendPlayerChat("He looks pretty grumpy,", "I better not disturb him.", 610)

def first_click_npc_7425(player):  # zammy monk
    quest_stage = player.getQuest(1).getStage()
    if quest_stage == 4:
        player.getDH().sendDialogues(6046)
    elif quest_stage == 5:
        player.getPA().talkToHalloweenZamorak(player)
    else:
        player.getDH().sendNpcChat("Nice weather isn't it.", 588)
        
def first_click_npc_7638(player):  # shady figure v2
    quest_stage = player.getQuest(1).getStage()
    if quest_stage == 6:
        player.getDH().sendDialogues(6172)
    elif quest_stage == 7:
        player.getDH().sendDialogues(6063)
    elif quest_stage == 8:
        player.getDH().sendDialogues(6181)
    elif quest_stage == 9:
        player.getDH().sendDialogues(6186)
    elif quest_stage == 10:
        if (Area.inHweenArea(player)):
            player.getDH().sendDialogues(6189)
        else:
            player.getDH().sendNpcChat("Enter the trapdoor, we'll meet you down there.", 591)
    elif quest_stage == 11:
        player.getDH().sendDialogues(6199)
    elif quest_stage == 12:
        player.getDH().sendDialogues(6207)
    elif quest_stage == 13:
        player.getDH().sendDialogues(6207)
        
    else:
        player.getDH().sendStatement("He doesn't look interested in making conversation right now.")
        
def first_click_object_5169(player):  # grave
    quest_stage = player.getQuest(1).getStage()
    if quest_stage == 7:
        player.getDH().sendDialogues(6175)
    else:
        player.getPA().sendMessage("Nothing interesting happens.")
        
def second_click_object_5169(player):  # grave
    quest_stage = player.getQuest(1).getStage()
    if quest_stage == 8:
        if (player.getX() == 3541) and (player.getY() == 3470):
            PlayerMiscContent.HalloweenDig(player)
        else:
            player.getDH().sendStatement("You need to dig closer to the grave.")
    else:
        player.getPA().sendMessage("Nothing interesting happens.")
        
def first_click_item_16099(player):  # spade
    quest_stage = player.getQuest(1).getStage()
    if quest_stage == 8:
        if (player.getX() == 3541) and (player.getY() == 3470):
            PlayerMiscContent.HalloweenDig(player)
        else:
            player.getDH().sendStatement("You need to dig closer to the grave.")
    else:
        player.getPA().sendMessage("Nothing interesting happens.")
    
        
def first_click_object_11636(player):  # trapdoor
    quest_stage = player.getQuest(1).getStage()
    if quest_stage == 9:
        ObjectEvent.climbDownLadder(player, 3788, 9226, 0);
        player.getQuest(1).setStage(10)
        QuestHandler.updateAllQuestTab(player);
    elif quest_stage == 10:
        ObjectEvent.climbDownLadder(player, 3788, 9226, 0);
    elif quest_stage == 11:
        ObjectEvent.climbDownLadder(player, 3788, 9226, 0);
        
def first_click_object_22172(player):  # trapdoor
    quest_stage = player.getQuest(1).getStage()
    if quest_stage >= 12:
        ObjectEvent.climbUpLadder(player, 3543, 3463, 0);
    else:
        player.getPA().sendMessage("You can't leave right now.")
        
def kill_npc_680(player):  # handles oversized skeleton death
    player.getQuest(1).setStage(11)
    QuestHandler.updateAllQuestTab(player);
    player.getDH().sendDialogues(6199)


def chat_5999(player):
    player.getDH().sendStatement("You approach the Shady figure. You're surprised by how small he is.")
    player.nextDialogue = 6000;
    
def chat_6000(player):
    player.getDH().sendNpcChat("It looks like you've come at the right time," , "I could do with some assistance...", 591)
    player.nextDialogue = 6001;

def chat_6001(player):
    player.getDH().sendPlayerChat("Okay, well first of all, who might you be?", 589)
    player.nextDialogue = 6002;
    
def chat_6002(player):
    player.getDH().sendNpcChat("That doesn't concern you. You do not need to know.", 591)
    player.nextDialogue = 6003;
    
def chat_6003(player):
    player.getDH().sendPlayerChat("Right...", 595)
    player.nextDialogue = 6004;
    
def chat_6004(player):
    player.getDH().sendPlayerChat("If you aren't going to speak to me nicely,", "then I might as well leave you to it.", 596)
    player.nextDialogue = 6005;
    
def chat_6005(player):
    player.getDH().sendNpcChat("That would be unwise of you " + player.getPlayerName() + ",", "very unwise indeed.", 591)
    player.nextDialogue = 6006;
    
def chat_6006(player):
    player.getDH().sendPlayerChat("Uhh, how do you know my name?", "You're starting to creep me out.", 598)
    player.nextDialogue = 6007;
    
def chat_6007(player):
    player.getDH().sendNpcChat("Silence fool!" , "You will do exactly as I say else you will face the" , "consequences of your actions.", 591)
    player.nextDialogue = 6008;
    
def chat_6008(player):
    player.getDH().sendPlayerChat("Okay okay, fine. What do you want from me?", 599)
    player.nextDialogue = 6009;
    
def chat_6009(player):
    player.getDH().sendNpcChat("As you might have noticed, it's the time of year where the" , "barrier between the living and the dead gets extremely thin.", 591)
    player.nextDialogue = 6010;
    
def chat_6010(player):
    player.getDH().sendNpcChat("I want to make good use of it and attempt" , "to summon a monster from the underworld.", 591)
    player.nextDialogue = 6015;
    
def chat_6015(player):
    player.getDH().sendNpcChat("I'm going to create a special bracelet using" , "the remains of the monster, and enchant it to provide" , "mystical properties", 605)
    player.nextDialogue = 6016;
    
def chat_6016(player):
    player.getDH().sendPlayerChat("That sounds rather complicated...", 602)
    player.nextDialogue = 6017;
    
def chat_6017(player):
    player.getDH().sendNpcChat("So, are you willing to help me?", 605)
    player.nextDialogue = 6018;
    
def chat_6018(player):
    player.getDH().sendPlayerChat("I suppose I could lend a hand.", 591)
    player.nextDialogue = 6019;
    
def chat_6019(player):
    player.getDH().sendNpcChat("Excellent news.", 591)
    player.nextDialogue = 6012;
    
def chat_6012(player):
    player.getDH().sendNpcChat("First of all I need you to bring me some ingredients." , "They will be used to summon the monster.", 591)
    player.nextDialogue = 6021;
    
def chat_6021(player):
    player.getDH().sendPlayerChat("What do you need me to get?", 591)
    player.nextDialogue = 6022;
    
def chat_6022(player):
    player.getDH().sendNpcChat("I need an explosive potion, one of each elemental ", "battlestave, 500 soul runes and a necklace mould.", 591)
    player.nextDialogue = 6023;
    
def chat_6023(player):
    player.getDH().sendNpcChat("I specify that they must be battlestaves," , "else the summon spell will not work.", 591)
    player.nextDialogue = 6024;
    
def chat_6024(player):
    player.getDH().sendNpcChat("Have you got that?" , "Don't worry if you forget the items," , "just come and speak to me again and I'll remind you.", 591)
    
def chat_6025(player):
    player.getDH().sendPlayerChat("I'll be back with those items soon.", 588)
    player.nextDialogue = 6026;
    
def chat_6026(player):
    player.getDH().sendNpcChat("Don't be too long, I don't want to come looking for you.", 594)
    player.nextDialogue = 6025;
    player.getQuest(1).setStage(1)
    QuestHandler.updateAllQuestTab(player);
    
def chat_6030(player):
    player.getDH().sendNpcChat("How are you getting on?", 591)
    player.nextDialogue = 6031;
    
def chat_6031(player):
    if ItemAssistant.hasItemAmountInInventory(player, soul_rune, 500) and ItemAssistant.hasItemInInventory(player, pot) and ItemAssistant.hasItemInInventory(player, air) and ItemAssistant.hasItemInInventory(player, water) and ItemAssistant.hasItemInInventory(player, earth) and ItemAssistant.hasItemInInventory(player, fire) and ItemAssistant.hasItemInInventory(player, mould):
        player.getDH().sendDialogues(6036)
    else:
        player.getDH().sendDialogues(6035)
        
def chat_6035(player):
    player.getDH().sendPlayerChat("I forgot, what items did I need to bring?", 598)
    player.nextDialogue = 6022;
        
def chat_6036(player):
    player.getDH().sendPlayerChat("I've got everything that you asked for!", 588)
    player.nextDialogue = 6037;
    
def chat_6037(player):
    player.getDH().sendNpcChat("Ah, very good " + player.getPlayerName() + ", very good indeed.", 591)
    player.getQuest(1).setStage(3)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 6038;
    
def chat_6038(player):
    player.getDH().sendNpcChat("We can move on to the preparation now.", 591)
    ItemAssistant.deleteItemFromInventory(player, soul_rune, 500)
    ItemAssistant.deleteItemFromInventory(player, pot, 1)
    ItemAssistant.deleteItemFromInventory(player, air, 1)
    ItemAssistant.deleteItemFromInventory(player, water, 1)
    ItemAssistant.deleteItemFromInventory(player, earth, 1)
    ItemAssistant.deleteItemFromInventory(player, fire, 1)
    ItemAssistant.deleteItemFromInventory(player, mould, 1)
    player.nextDialogue = 6039;
    
def chat_6039(player):
    player.getDH().sendPlayerChat("What might that be?", 590)
    player.nextDialogue = 6040;
    
def chat_6040(player):
    player.getDH().sendNpcChat("I need you to find a zamorakian wizard that could help" , "with summoning the monster.", 590)
    player.getQuest(1).setStage(4)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 6041;
    
def chat_6041(player):
    player.getDH().sendNpcChat("You should be able to find one at the temple" , "near to where the goblins live.", 590)
    player.nextDialogue = 6042;
    
def chat_6042(player):
    player.getDH().sendNpcChat("Say that you need him to meet us in the Haunted woods," , "east of Canafis. It needs to be the small building" , "with the gravestone to the north and the swamp to the south.", 605)
    player.nextDialogue = 6043;
    
def chat_6043(player):
    player.getDH().sendNpcChat("There's only one building in that area," , "so he will know which one it is. Come back and" , "speak with me again once you have arranged it.", 605)
    player.nextDialogue = 6044;
    
def chat_6044(player):
    player.getDH().sendPlayerChat("Hopefully I can remember all of that." , "I'll see what I can do.", 589)
    
    
# speaking with the zamorak mage
def chat_6046(player):
    player.getDH().sendNpcChat("Why hello there " + player.getPlayerName() + ", what brings you to" , "these parts?", 588)
    player.nextDialogue = 6047;
    
def chat_6047(player):
    player.getDH().sendPlayerChat("I've been sent to look for a certain zamorak mage" , "that can help with summoning undead monsters.", 591)
    player.nextDialogue = 6048;
    
def chat_6048(player):
    player.getDH().sendNpcChat("Well, that would be me. What can I do for you?", 591)
    player.nextDialogue = 6050;
    
def chat_6050(player):
    player.getDH().sendPlayerChat("I need you to meet me in the Haunted woods" , "east of Canafis. There's a building there, but I've" , "been told that you will know which one it is.", 597)
    player.nextDialogue = 6051;
    
def chat_6051(player):
    player.getDH().sendNpcChat("Goodness me, not there I hope! I still vividly remember" , "what happened there last time...", 598)
    player.nextDialogue = 6052;
    
def chat_6052(player):
    player.getDH().sendPlayerChat("What happened? What do you mean last time?", 595)
    player.nextDialogue = 6053;
    
def chat_6053(player):
    player.getDH().sendStatement("He either doesn't hear you, or decides to ignore you.")
    player.nextDialogue = 6054;
    
def chat_6054(player):
    player.getDH().sendNpcChat("Never mind, it must be done. I had better be off " + player.getPlayerName() + "." , "Tell him that i will meet him there", "Take care out there, may Zamorak be in your stride!", 605)
    player.getQuest(1).setStage(5)
    QuestHandler.updateAllQuestTab(player);
    
    
    
def chat_6070(player):
    player.getDH().sendNpcChat("Did you find a zamorakian wizard like I asked?", 591)
    player.nextDialogue = 6071;
    
def chat_6071(player):
    player.getDH().sendPlayerChat("Not yet, I'm still looking.", 597)
    player.nextDialogue = 6072;
    
def chat_6072(player):
    player.getDH().sendNpcChat("Please get a move on then, " + player.getPlayerName() + ".", 591)
    player.nextDialogue = 6073;
    
def chat_6073(player):
    player.getDH().sendNpcChat("Meet me at the location like we discussed.", 605)
    
    
    
    # conversation with shady figure after speaking with the mage
def chat_6055(player):
    player.getDH().sendNpcChat("Ah " + player.getPlayerName() + ", good to see you again." , "I see that you completed my task.", 605)
    player.nextDialogue = 6056;
    
def chat_6056(player):
    player.getDH().sendPlayerChat("I did! What's next?", 588)
    player.nextDialogue = 6157;
    
def chat_6157(player):
    player.getDH().sendNpcChat("While you were away, I prepared the rest of the equipment" , "that I need to give to the mage in order to summon" , "the monster.", 605)
    player.nextDialogue = 6158;
    
def chat_6158(player):
    player.getDH().sendNpcChat("For this next part, it's probably best if you prepare" , "yourself for combat.", 605)
    player.nextDialogue = 6159;
    
def chat_6159(player):
    player.getDH().sendNpcChat("Once you are ready, speak to me again and I'll" , "teleport both of us to the Haunted woods.", 605)
    player.nextDialogue = 6160;
    
def chat_6160(player):
    player.getDH().sendPlayerChat("Sounds like a plan. I'll be back shortly!", 591)
    player.getQuest(1).setStage(6)
    QuestHandler.updateAllQuestTab(player);
    
def chat_6161(player):
    player.getDH().sendNpcChat("Are you ready now?", 605)
    player.nextDialogue = 6162;
    
def chat_6162(player):
    player.getDH().sendPlayerChat("Yeah! This monster won't know what's hit it.", 588)
    player.nextDialogue = 6163;
    
def chat_6163(player):
    player.getDH().sendNpcChat("A little over-enthusiastic. I'll begin the teleport now.", 605)
    player.nextDialogue = 6164;
    
def chat_6164(player):
    player.getDH().sendStatement("He mutters some words and starts waving his arms about.")
    player.nextDialogue = 6165;
    
def chat_6165(player):
    player.getDH().sendPlayerChat("What are you doing with your arms?" , "That looks ridiculous!", 588)
    player.nextDialogue = 6166;
    
def chat_6166(player):
    player.getDH().sendNpcChat("I'm sorry I have no idea what you're talking about.", 605)
    player.nextDialogue = 6167;
    
def chat_6167(player):
    player.getDH().sendStatement("He continues the strange ritual.")
    player.nextDialogue = 6168;
    
def chat_6168(player):
    player.getDH().sendNpcChat("Any second now, and we'll both be teleported." , "I'll see you there.", 605)
    player.nextDialogue = 6169;
    
def chat_6169(player):
    player.getDH().sendStatement("You start to feel a little odd.")
    player.nextDialogue = 6170;
    
def chat_6170(player):
    player.getDH().sendPlayerChat("I think it's working...", 596)
    player.nextDialogue = 6171;
    
def chat_6171(player):
    Teleport.startTeleport(player, 3543, 3466, 0, "ARCEUUS")
    player.getDH().sendStatement("You are teleported to the Haunted woods.")
    player.getQuest(1).setStage(6)
    QuestHandler.updateAllQuestTab(player);
    
    
    
    # conversation with shady figure in the haunted woods after you have been teleported by the shady figure
def chat_6172(player):
    player.getDH().sendNpcChat("So, we meet again " + player.getPlayerName() + "." , "I hope you're ready, this is the most important part.", 605)
    player.nextDialogue = 6173;
    
def chat_6173(player):
    player.getDH().sendPlayerChat("Ready as I'll ever be." , "What do you need me to do now?", 591)
    player.nextDialogue = 6057;
    
def chat_6057(player):
    player.setNpcType(7425)
    player.getDH().sendNpcChat("I think we should...", 605)
    player.nextDialogue = 6058;
    
def chat_6058(player):
    player.setNpcType(7638)
    player.getDH().sendNpcChat("Excuse me, who asked you to speak?", 592)
    player.nextDialogue = 6059;
    
def chat_6059(player):
    player.setNpcType(7425)
    player.getDH().sendNpcChat("But I assumed tha...", 598)
    player.nextDialogue = 6060;
    
def chat_6060(player):
    player.setNpcType(7638)
    player.getDH().sendNpcChat("Well, you assumed wrong. Speak when spoken to, do ", "you understand?", 593)
    player.nextDialogue = 6061;
    
def chat_6061(player):
    player.getDH().sendStatement("The mage nods, and looks rather anxious.")
    player.nextDialogue = 6062;
    
def chat_6062(player):
    player.getDH().sendNpcChat("Good. Now that we have established that, we need to" , "find a way to get into that trap door.", 591)
    player.getQuest(1).setStage(7)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 6063;
    
def chat_6063(player):
    player.getDH().sendNpcChat("There must be a key around here somewhere...", 605)
    player.nextDialogue = 6064;
    
def chat_6064(player):
    player.setNpcType(7425)
    player.getDH().sendNpcChat("Maybe we could try looking near that grave over there.", 605)
    player.nextDialogue = 6065;
    
def chat_6065(player):
    player.setNpcType(7638)
    player.getDH().sendNpcChat("Oh I'm sorry, did someone say something?", 605)
    player.nextDialogue = 6066;
    
def chat_6066(player):
    player.getDH().sendPlayerChat("Hey! Cut him some slack, he's got a point.", 595)
    player.nextDialogue = 6067;
    
def chat_6067(player):
    player.getDH().sendNpcChat("Hmmph." , "I suppose you could try it.", 605)
    player.nextDialogue = 6068;
    
def chat_6068(player):
    player.getDH().sendNpcChat("" + player.getPlayerName() + ", go and look at that gravestone and see if you can" , "find anything.", 605)
    player.nextDialogue = 6069;
    
def chat_6069(player):
    player.getDH().sendPlayerChat("Okay, I'll have a look.", 591)
    
def chat_6175(player):
    player.getDH().sendStatement("You attempt to read the description on the gravestone...")
    player.nextDialogue = 6176;
    
def chat_6176(player):
    if Misc.random(3) == 1:
        player.getDH().sendStatement("The grave reads 'Here lies Scamdi, may he never be forgotten'.")
        player.nextDialogue = 6178;
    else:
        player.getDH().sendDialogues(6177)
        
def chat_6177(player):
    PlayerMiscContent.HalloweenInterface(player)
    
def chat_6178(player):
    player.setNpcType(7638)
    player.getDH().sendNpcChat("Did you find anything useful?", 605)
    player.nextDialogue = 6179;
    
def chat_6179(player):
    player.getDH().sendPlayerChat("Nope, sadly it's just a regular gravestone.", 597)
    player.nextDialogue = 6180;
    
def chat_6180(player):
    player.getDH().sendNpcChat("Blast! Try something else.", 605)
    player.nextDialogue = 6181;
    
def chat_6181(player):
    player.getDH().sendNpcChat("Here, take this spade and dig near to the grave." , "The key might be burried.", 605)
    player.nextDialogue = 6182;
    
def chat_6182(player):
    player.getDH().sendItemChat("", "He hands you a spade.", 16099, 200, 14, 0);
    player.getQuest(1).setStage(8)
    QuestHandler.updateAllQuestTab(player);
    ItemAssistant.addItemToInventoryOrDrop(player, 16099, 1)
    
    # if the player gets spooked by the ghost interface lul
def chat_6183(player):
    player.setNpcType(7638)
    player.getDH().sendNpcChat("What happened " + player.getPlayerName() + "? Did you find anything.", 605)
    player.nextDialogue = 6184;
    
def chat_6184(player):
    player.getDH().sendPlayerChat("Find anything? I got spooked!" , "Was that set up?..", 595)
    player.nextDialogue = 6185;
    
def chat_6185(player):
    player.getDH().sendNpcChat("I have no idea what you're talking about fool.", 605)
    player.nextDialogue = 6181;
    
    
    # after the player finds the key
def chat_6186(player):
    player.getDH().sendPlayerChat("I found the key!", 588)
    player.nextDialogue = 6187;
    
def chat_6187(player):
    player.getDH().sendNpcChat("Very good. Now we can begin the final stage.", 605)
    player.nextDialogue = 6188;
    
def chat_6188(player):
    player.getDH().sendNpcChat("Come, lets enter that trapdoor." , "We'll follow you.", 605)
    
    
    
    # once player has climbed down the trapdoor
def chat_6189(player):
    player.getDH().sendNpcChat("Ah yes, here we are. The site of a mass burial.", 605)
    player.nextDialogue = 6190;
    
def chat_6190(player):
    player.getDH().sendPlayerChat("Sounds spooky... What now?", 591)
    player.nextDialogue = 6191;
    
def chat_6191(player):
    player.getDH().sendNpcChat("Now it's over to our friend here to summon the monster.", 605)
    player.nextDialogue = 6192;
    
def chat_6192(player):
    player.setNpcType(7425)
    player.getDH().sendNpcChat("About time!", 588)
    player.nextDialogue = 6193;
    
def chat_6193(player):
    player.setNpcType(7638)
    player.getDH().sendNpcChat("As soon as it spawns I need you to kill it as quickly" , "as you can. Understand?", 605)
    player.nextDialogue = 6194;
    
def chat_6194(player):
    player.getDH().sendPlayerChat("Okay, I'll try my best.", 588)
    player.nextDialogue = 6195;
    
def chat_6195(player):
    player.setNpcType(7425)
    player.getDH().sendNpcChat("Lets begin. Prepare yourself for combat!", 605)
    player.nextDialogue = 6196;
    
def chat_6196(player):
    player.getDH().sendStatement("The mage begins reciting an ancient ritual...")
    player.nextDialogue = 6197;
    
def chat_6197(player):
    player.getDH().sendNpcChat("Look out, here it comes!", 605)
    player.nextDialogue = 6198;

def chat_6198(player):  # spawns skeleton
    player.getPA().closeInterfaces(True);
    NpcHandler.spawnNpc(player, 680, 3786, 9225, 0, True, True);    
    
def chat_6199(player):
    player.setNpcType(7638)
    player.getDH().sendNpcChat("Well well " + player.getPlayerName() + ", you actually did it.", 605)
    player.nextDialogue = 6200;
    
def chat_6200(player):
    if ItemAssistant.hasItemInInventory(player, bones):
        player.getDH().sendDialogues(6202)
    else:
        player.getDH().sendDialogues(6201)
    
def chat_6201(player):
    player.getDH().sendNpcChat("Pick up its bones and bring them here.", 588)
    player.nextDialogue = 6213;
    
def chat_6213(player):
    player.getDH().sendOptionDialogue("Will do.", "But i haven't killed it yet!", 10005)
    player.nextDialogue = 6214;
    
def option_one_10005(player):
    player.getPA().closeInterfaces(True);
    
def option_two_10005(player):
    player.getDH().sendDialogues(6195)
    
def chat_6202(player):
    player.getDH().sendNpcChat("I'm somewhat impressed, you made it look easy!", 588)
    player.nextDialogue = 6203;
    
def chat_6203(player):
    player.getDH().sendPlayerChat("Thanks!", 588)
    player.nextDialogue = 6204;
    
def chat_6204(player):
    player.getDH().sendNpcChat("Now, if you pass me the bones, I can start work" , "on the bracelet.", 591)
    player.nextDialogue = 6205;
    
def chat_6205(player):
    player.getDH().sendPlayerChat("Here you go.", 591)
    player.getQuest(1).setStage(12)
    QuestHandler.updateAllQuestTab(player);
    ItemAssistant.deleteItemFromInventory(player, bones, 1)
    player.nextDialogue = 6207;
    
def chat_6207(player):
    player.getDH().sendNpcChat("Excellent. Meet me back in Edgeville " + player.getPlayerName() + "," , "I have a little reward for you.", 605)
    player.nextDialogue = 6208;
    
def chat_6208(player):
    player.getDH().sendPlayerChat("Sure thing, glad I could be of assistance.", 588)
    player.nextDialogue = 6209;
    
def chat_6209(player):
    player.setNpcType(7425)
    player.getDH().sendNpcChat("I'd better be heading off myself. It's almost time" , "for my prayers. Safe travels!", 588)
    player.getQuest(1).setStage(13)
    QuestHandler.updateAllQuestTab(player);
    
    
    # final dialogue
def chat_6210(player):
    player.getDH().sendNpcChat("Thanks for helping, " + player.getPlayerName() + ".", 605)
    player.nextDialogue = 6211;
    
def chat_6211(player):
    player.getDH().sendNpcChat("I've got some nice rewards for you as a thank you." , "Happy Halloween!", 588)
    player.nextDialogue = 6212;
    
    
    
def chat_6212(player):
    player.getQuest(1).setStage(14)
    QuestHandler.updateAllQuestTab(player);
    if Misc.random(300) == 1:
        ItemAssistant.addItemToInventoryOrDrop(player, mask, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, top, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, robe, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, knife, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, black_ween, 1)   
        reward = QuestReward("Banshee outfit", "Hunting knife", "Black h'ween mask", "")
        player.completeQuest("Dawntained's Halloween Event 2017", reward, mask)
    else:
        ItemAssistant.addItemToInventoryOrDrop(player, mask, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, top, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, robe, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, knife, 1)
        ItemAssistant.addItemToInventoryOrDrop(player, bracelet, 1)
        reward = QuestReward("Banshee outfit", "Hunting knife", "Bone bracelet", "")
        player.completeQuest("Dawntained's Halloween Event 2017", reward, mask)
