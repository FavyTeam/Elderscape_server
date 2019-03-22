#Written by Owain using the proper OSRS quest dialogues
#13/08/18

from game.content.quest import Quest
from game.content.quest import QuestHandler
from game.content.quest import QuestReward
from game.item import ItemAssistant
from game.content.dialogueold import DialogueHandler
from game.content.skilling import Skilling
from game.content.miscellaneous import PlayerMiscContent
from core import GameType

stake = 1549
garlic = 1550
hammer = 2347
beer = 1917

def configure_quest_7():
    quest_name = 'Vampire Slayer'
    quest_stages = 5
    Quest.addQuest(quest_name, quest_stages)

def quest_button_7(player):
    quest_name = 'Vampire Slayer'
    quest_stage = player.getQuest(7).getStage()
    if quest_stage == 0:
        QuestHandler.startInfo(player, quest_name, "I can start this quest by speaking to @dre@Morgan@dbl@ who is in",  "@dre@Draynor Village@dbl@.", "Must be able to kill a level 34 @dre@Vampire@dbl@", "This quest takes roughly @dre@10@dbl@ minutes to complete.")
    elif quest_stage == 1 or quest_stage == 2 or quest_stage == 3:
        QuestHandler.startInfo(player, quest_name, "I should travel to the @dre@Blue Moon Inn@dbl@ in @dre@Varrock@dbl@ and", "speak to @dre@Dr Harlow@dbl@.", "", "")
    elif quest_stage == 4:
        QuestHandler.startInfo(player, quest_name, "I need to go to the basement in @dre@Draynor Manor@dbl@ and " , "kill the @dre@Vampire@dbl@!", "", "")
    elif quest_stage == 5:
        QuestHandler.startInfo(player, quest_name, "I have completed @dre@Vampire Slayer@dbl@.", "", "", "")

    #Morgan
def first_click_npc_3479(player):
	if GameType.isOsrsPvp() == False:
		quest_stage = player.getQuest(7).getStage()
		if quest_stage == 0:
			player.getDH().sendDialogues(3500)
		elif quest_stage == 5:
			player.getDH().sendDialogues(3033)
		else:
			player.getDH().sendDialogues(3030)
        
    #Dr Harlow
def first_click_npc_3480(player):
    quest_stage = player.getQuest(7).getStage()
    if quest_stage == 1 or quest_stage == 2:
        player.getDH().sendDialogues(3008)
    elif quest_stage == 3:
        player.getDH().sendDialogues(3019)
    else:
        player.getDH().sendPlayerChat("He looks rather drunk, I'd probably be best to leave" , "him alone.", 610)
        
    #Draynor mansion stairs
def first_click_object_2616(player):
    player.getPA().movePlayer(3077, 9771, 0);
def first_click_object_2617(player):
    player.getPA().movePlayer(3115, 3356, 0);

    #Morgan's house stairs
def first_click_object_15645(player):
    player.getPA().movePlayer(3102, 3267, 1);
def first_click_object_15648(player):
    player.getPA().movePlayer(3098, 3267, 0);

    #Taking garlic in Morgan's house
def first_click_object_2612(player):
    PlayerMiscContent.takeGarlic(player)
            
    #Summoning the vampire
def first_click_object_2614(player):
    quest_stage = player.getQuest(7).getStage()
    if quest_stage == 4:
        if ItemAssistant.hasItemInInventory(player, garlic) and ItemAssistant.hasItemInInventory(player, stake) and ItemAssistant.hasItemInInventory(player, hammer):
            NpcHandler.spawnNpc(player, 3481, 3078, 9774, 0, True, True);
        else:
            player.getPA().sendMessage("You should get a garlic clove, a stake and a hammer before doing this.")
    else:
        player.getPA().sendMessage("Nothing interesting happens.")
        
def kill_npc_3481(player):
    player.getQuest(7).setStage(5)
    QuestHandler.updateAllQuestTab(player);
    amount = 4825 * Skilling.getBaseExperience(player, ServerConstants.ATTACK)
    reward = QuestReward("3 Quest Points", ""+ str(amount) +" Attack XP")
    player.completeQuest("Vampire Slayer", reward, stake)
    Skilling.addSkillExperience(player, 4825, ServerConstants.ATTACK, False)
        
    
    #Start of main dialogue
def chat_3500(player):
    player.getDH().sendNpcChat("Please please help us, bold adventurer!", 596)
    player.nextDialogue = 3001;

def chat_3001(player):
    player.getDH().sendPlayerChat("What's the problem?", 591)
    player.nextDialogue = 3002;
    
def chat_3002(player):
    player.getDH().sendNpcChat("Our little village has been dreadfully ravaged by an evil" , "vampire! He lives in the basement of the manor to the" , "north, we need someone to get rid of him once and for" , "all!", 610)
    player.nextDialogue = 3003;
    
def chat_3003(player):
    player.getDH().sendOptionDialogue("No, vampires are scary!", "Ok, I'm up for an adventure.", 3004)
    
def option_one_3004(player):
    player.getDH().sendPlayerChat("No, vampires are scary!", 591)

def option_two_3004(player):
    player.getDH().sendPlayerChat("Ok, I'm up for an adventure.", 591)
    player.nextDialogue = 3005;
    
def chat_3005(player):
    player.getDH().sendNpcChat("I think first you should seek help. I have a friend who", "is a retired vampire hunter, his name is Dr. Harlow. He " , "may be able to give you some tips. He can normally be" , "found in the Blue Moon Inn in Varrock, he's a bit of", 591)
    player.nextDialogue = 3006;
    
def chat_3006(player):
    player.getDH().sendNpcChat("an old soak these days. Mention his old friend Morgan, " , "I'm sure he wouldn't want me killed by a vampire.", 591)
    player.nextDialogue = 3007;
    
def chat_3007(player):
    player.getDH().sendPlayerChat("I'll look him up then.", 591)
    player.getQuest(7).setStage(1)
    QuestHandler.updateAllQuestTab(player);
    
    
    #Dr Harlow dialogues
def chat_3008(player):
    player.getDH().sendNpcChat("Buy me a drrink pleassh...", 591)
    quest_stage = player.getQuest(7).getStage()
    if quest_stage == 2 and ItemAssistant.hasItemInInventory(player, 1917):
        player.nextDialogue = 3016;
    else:
        player.nextDialogue = 3009;
    
def chat_3009(player):
    player.getDH().sendOptionDialogue("No, you've had enough.", "Morgan needs your help!", 3010)
        
def option_one_3010(player):
    player.getDH().sendPlayerChat("No, you've had enough.", 591)
    
def option_two_3010(player):
    player.getDH().sendPlayerChat("Morgan needs your help!", 591)
    player.nextDialogue = 3011;
    
def chat_3011(player):
    player.getDH().sendNpcChat("Morgan you shhay..?", 591)
    player.nextDialogue = 3012;
    
def chat_3012(player):
    player.getDH().sendPlayerChat("His village is being terrorised by a vampire! He told me" , "to ask you about how I can stop it.", 597)
    player.nextDialogue = 3013;
    
def chat_3013(player):
    player.getDH().sendNpcChat("Buy me a beer... then I'll teash you what you need to" , "know...", 591)
    player.nextDialogue = 3014;
    
def chat_3014(player):
    player.getDH().sendPlayerChat("But this is your friend Morgan we're talking about!", 591)
    player.nextDialogue = 3015;
    
def chat_3015(player):
    player.getDH().sendNpcChat("Buy ush a drink anyway...", 591)
    player.getQuest(7).setStage(2)
    QuestHandler.updateAllQuestTab(player);
    
    #If a player has a beer
def chat_3016(player):
    player.getDH().sendPlayerChat("Here you go.", 591)
    player.nextDialogue = 3017;
    
def chat_3017(player):
    player.getDH().sendItemChat("", "You give a beer to Dr Harlow.", 1917, 200, 14, 0)
    ItemAssistant.deleteItemFromInventory(player, beer, 1)
    player.getQuest(7).setStage(3)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 3018;
    
def chat_3018(player):
    player.getDH().sendNpcChat("Cheersh matey...", 591)
    player.nextDialogue = 3019;
    
def chat_3019(player):
    player.getDH().sendPlayerChat("So tell me how to kill vampires then.", 591)
    player.nextDialogue = 3020;
    
def chat_3020(player):
    player.getDH().sendNpcChat("Yesh yesh vampires, I was very good at" , "killing em once...", 591)
    player.nextDialogue = 3021;
    
def chat_3021(player):
    player.getDH().sendStatement("Dr Harlow appears to sober up slightly.")
    player.nextDialogue = 3022;
    
def chat_3022(player):
    player.getDH().sendNpcChat("Well you're gonna need a stake, otherwise he'll just" , "regenerate. Yes, you must have a stake to finish it off..." , "I just happen to have one with me.", 591)
    player.nextDialogue = 3023;
    
def chat_3023(player):
    player.getDH().sendItemChat("", "Dr Harlow hands you a stake.", stake, 200, 14, 0)
    ItemAssistant.addItemToInventoryOrDrop(player, stake, 1)
    player.nextDialogue = 3024;
    
def chat_3024(player):
    player.getDH().sendNpcChat("You'll need a hammer as well, to drive it in properly," , "your everyday general store hammer will do. One last" , "thing... It's wise to carry garlic with you, vampires are" , "somewhat weakened if they can smell garlic. Morgan", 591)
    player.nextDialogue = 3025;
    
def chat_3025(player):
    player.getDH().sendNpcChat("always liked garlic, you should try his house. But" , "remember, a vampire is still a dangerous foe!", 591)
    player.nextDialogue = 3026;
    
def chat_3026(player):
    player.getDH().sendPlayerChat("Thank you very much!", 591)
    player.getQuest(7).setStage(4)
    QuestHandler.updateAllQuestTab(player);
    
    
    #Talking to Morgan again
    #Start of main dialogue
def chat_3030(player):
    player.getDH().sendNpcChat("Have you managed to speak to Dr Harlow yet?", 591)
    quest_stage = player.getQuest(7).getStage()
    if quest_stage == 1 or quest_stage == 2:
        player.nextDialogue = 3037;    
    else:
        player.nextDialogue = 3031;
    
def chat_3031(player):
    player.getDH().sendPlayerChat("Yes, I have. He's given me some advice on how" , "to take down the vampire!", 591)
    player.nextDialogue = 3032;
    
def chat_3032(player):
    player.getDH().sendNpcChat("Wonderful! I'd better let you get on with the business" , "then. Best of luck!", 591)
    
    #If quest is done
def chat_3033(player):
    player.getDH().sendNpcChat("So, is it dead?", 591)
    player.nextDialogue = 3034;
    
def chat_3034(player):
    player.getDH().sendPlayerChat("Yup. It was a piece of cake.", 591)
    player.nextDialogue = 3035;
    
def chat_3035(player):
    player.getDH().sendNpcChat("Cake? I asked about the vampire!", 591)
    player.nextDialogue = 3036;
    
def chat_3036(player):
    player.getDH().sendPlayerChat("Never mind...", 591)
    
    
def chat_3037(player):
    player.getDH().sendPlayerChat("Not yet.", 591)
    player.nextDialogue = 3038;
    
def chat_3038(player):
    player.getDH().sendNpcChat("Please hurry!", 591)