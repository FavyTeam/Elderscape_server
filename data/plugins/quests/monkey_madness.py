# Quest Name: Monkey Madness
# Written by Owain for Runefate, 11/02/18
# Quest Length: Medium
# Quest Difficulty: Medium

from game.content.quest import Quest
from game.content.quest import QuestHandler
from game.content.quest import QuestReward

king_narnode_shareen = 1423
karam = 7172
lumdo = 1453
lumo = 7112

def configure_quest_5():
    quest_name = "Monkey Madness"
    quest_stages = 7
    Quest.addQuest(quest_name, quest_stages)

def quest_button_5(player):
    quest_name = "Monkey Madness"
    stage = player.getQuest(5).getStage()
    if stage == 0:
        QuestHandler.startInfo(player, quest_name, "I can start this quest by speaking to @dre@King Narnode Shareen@dbl@.", "He is located on the ground level of the @dre@Gnome stronghold@dbl@.", "I need to be able to defeat a @dre@level 195@dbl@ Jungle demon.", "This quest takes roughly @dre@15@dbl@ minutes to complete.")
    elif stage == 1:
        QuestHandler.startInfo(player, quest_name, "@dre@King Narnode Shareen@dbl@ told me to find his spy on @dre@Ape Atoll@dbl@." , "I'll need to take use the @dre@gnome glider@dbl@ system to get there," , "then I'll need to get inside the city somehow.", "")
    elif stage == 2:
        QuestHandler.startInfo(player, quest_name, "@dre@Lumo@dbl@ has informed me that @dre@Lumdo@dbl@ can make me", "enchanted equipment to disguise myself.", "I should bring some money with me.", "")
    elif stage == 3:
        QuestHandler.startInfo(player, quest_name, "@dre@Lumdo@dbl@ has provided me with a @dre@Monkey Greegree@dbl@", "which I can use to sneak into @dre@Ape Atoll@dbl@ and find", "the kings spy.", "")
    elif stage == 4:
        QuestHandler.startInfo(player, quest_name, "@dre@Karam@dbl@ told me to enter the trapdoor in the building", "at the centre of Ape Atoll.", "I should bring combat gear with me and expect a fight.", "")
    elif stage == 5:
        QuestHandler.startInfo(player, quest_name, "I need to kill the @dre@Jungle demon@dbl@.", "", "", "")
    elif stage == 6:
        QuestHandler.startInfo(player, quest_name, "The @dre@Jungle demon@dbl@ has been defeated and the", "gnomish race has been saved. I should probably let the", "king know.", "")
    elif stage == 7:
        QuestHandler.startInfo(player, quest_name, "I have completed @dre@Monkey Madness@dbl@.", "", "", "")

def first_click_npc_1423(player):#king
    stage = player.getQuest(5).getStage()
    if stage == 0:
        player.getDH().sendDialogues(1781662388)
    elif stage == 6:
        player.getDH().sendDialogues(22433224)
    else:
        player.getPA().sendMessage("The king looks very busy.")

def first_click_npc_7172(player):#karam
    stage = player.getQuest(5).getStage()
    if stage == 3:
        player.getDH().sendDialogues(1057961997)
    else:
        player.getPA().sendMessage("It looks like he wants to remain hidden.")
    
def first_click_npc_7112(player):#lumo
    stage = player.getQuest(5).getStage()
    if stage == 1:
        player.getDH().sendDialogues(172080016)
    else:
        player.getPA().sendMessage("It doesn't look as if he wants to talk right now.")

def first_click_npc_1453(player):#lumdo
    player.getDH().sendDialogues(1756093858)

def chat_1781662388(player):
    player.getDH().sendNpcChat("Hey there young adventurer.", 591)
    player.nextDialogue = 1781662389;

def chat_1781662389(player):
    player.getDH().sendPlayerChat("Hi little guy, so you're a king?", 591)
    player.nextDialogue = 1781662390;

def chat_1781662390(player):
    player.getDH().sendNpcChat("I am not little! I am King Narnode Shareen and ", "this is my stronghold.", 591)
    player.nextDialogue = 1781662391;

def chat_1781662391(player):
    player.getDH().sendPlayerChat("Do you have any quests?", 591)
    player.nextDialogue = 1781662392;

def chat_1781662392(player):
    player.getDH().sendNpcChat("Well as a matter of fact I do. One of my" , "spies has managed to lose contact with us on Ape Atoll." , "Perhaps you could find out what's happened to him?", 591)
    player.nextDialogue = 1781662393;

def chat_1781662393(player):
    player.getDH().sendPlayerChat("What's Ape Atoll?", 591)
    player.nextDialogue = 1781662394;

def chat_1781662394(player):
    player.getDH().sendNpcChat("Ape Atoll is where all the stinking apes live! We gnomes", "have a history of poor relations with them. They don't", "take kindly to newcomers.", 591)
    player.nextDialogue = 1781662395;

def chat_1781662395(player):
    player.getDH().sendPlayerChat("I'm sure they'll love me though!", 591)
    player.nextDialogue = 1781662396;

def chat_1781662396(player):
    player.getDH().sendNpcChat("If there's one thing an Ape hates more than a Gnome,", "it's a human.", 591)
    player.nextDialogue = 1781662397;

def chat_1781662397(player):
    player.getDH().sendPlayerChat("Pfff, I'm not scared of stupid monkeys!", 591)
    player.nextDialogue = 1781662398;

def chat_1781662398(player):
    player.getDH().sendNpcChat("Good.", 591)
    player.nextDialogue = 1781662399;

def chat_1781662399(player):
    player.getDH().sendNpcChat("My spies name is Karam. If you find him, tell him he", "needs to report back to us immediately.", 591)
    player.nextDialogue = 1781662400;

def chat_1781662400(player):
    player.getDH().sendNpcChat("You'll need to take the gnome glider over to the island", "and if you get caught....well....", "good luck.", 591)
    player.getQuest(5).setStage(1)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 1781662401;

def chat_1781662401(player):
    player.getDH().sendPlayerChat("Um, thanks.", 591)
    
def chat_172080016(player):
    player.getDH().sendPlayerChat("Hello there, have you seen a gnome named Karam?", 591)
    player.nextDialogue = 172080017;

def chat_172080017(player):
    player.getDH().sendNpcChat("Who's asking?", 591)
    player.nextDialogue = 172080018;

def chat_172080018(player):
    player.getDH().sendPlayerChat("My name is " + str(player.playerName) + ", I was sent by King", "Narnode Shareen.", 591)
    player.nextDialogue = 172080019;

def chat_172080019(player):
    player.getDH().sendNpcChat("King Narnode eh.. yeah I've seen him.", 591)
    player.nextDialogue = 172080020;

def chat_172080020(player):
    player.getDH().sendNpcChat("He passed through here a few weeks ago, said", "he was heading up to the city of the apes.", 591)
    player.nextDialogue = 172080021;

def chat_172080021(player):
    player.getDH().sendPlayerChat("Brilliant, I'll head there right away then!", 591)
    player.nextDialogue = 172080022;

def chat_172080022(player):
    player.getDH().sendNpcChat("Hah. Very funny.", 591)
    player.nextDialogue = 172080023;

def chat_172080023(player):
    player.getDH().sendNpcChat("A human in Ape Atoll!", 591)
    player.nextDialogue = 172080024;

def chat_172080024(player):
    player.getDH().sendNpcChat("Ha ha ha...", 591)
    player.nextDialogue = 172080025;

def chat_172080025(player):
    player.getDH().sendPlayerChat("What's so funny about that?", 591)
    player.nextDialogue = 172080026;

def chat_172080026(player):
    player.getDH().sendNpcChat("You don't know what they do to humans?", 591)
    player.nextDialogue = 172080027;

def chat_172080027(player):
    player.getDH().sendNpcChat("Let's just say you're better off disguising yourself.", 591)
    player.nextDialogue = 172080028;

def chat_172080028(player):
    player.getDH().sendPlayerChat("What? As a monkey?!", 591)
    player.nextDialogue = 172080029;

def chat_172080029(player):
    player.getDH().sendNpcChat("Precisely.", 591)
    player.nextDialogue = 172080030;

def chat_172080030(player):
    player.getDH().sendPlayerChat("And how do you suggest I manage that?", 591)
    player.nextDialogue = 172080031;

def chat_172080031(player):
    player.getDH().sendNpcChat("I know a guy handy little gnome, goes by", "the name Lumdo. You can find him at the end of" , "the dungeon right here.", 591)
    player.nextDialogue = 172080032;

def chat_172080032(player):
    player.getDH().sendNpcChat("He's a specialist in enchanted equipment. The kind", "that can transform your physical appearance.", 591)
    player.nextDialogue = 172080033;

def chat_172080033(player):
    player.getDH().sendPlayerChat("Sounds amazing, I can't wait to meet him.", 591)
    player.getQuest(5).setStage(2)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 172080034;

def chat_172080034(player):
    player.getDH().sendNpcChat("Be careful though, it won't be easy surviving down", "in the dungeon, all sorts of undead creatures" , "live down there.", 591)
    player.nextDialogue = 1720835;
    
def chat_1720835(player):
    player.getDH().sendNpcChat("Also, he drives a hard bargain. Be sure to bring" , "some money with you just in case.", 591)
    player.nextDialogue = 172080035;

def chat_172080035(player):
    player.getDH().sendPlayerChat("Don't worry, I've handled quite a few undeads" , "in my time. Thank you for your assistant, I'll" , "see you later.", 591)

def chat_1756093858(player):
    player.getDH().sendPlayerChat("Lumdo?", 591)
    player.nextDialogue = 1756093859;

def chat_1756093859(player):
    player.getDH().sendNpcChat("That's my name, don't wear it out.", 591)
    player.nextDialogue = 1756093860;

def chat_1756093860(player):
    player.getDH().sendPlayerChat("I was told that you're able to provide me with" , "a special item that can disguise me.", 591)
    player.nextDialogue = 1756093861;

def chat_1756093861(player):
    player.getDH().sendNpcChat("Yes I can.", 591)
    player.nextDialogue = 1756093862;

def chat_1756093862(player):
    player.getDH().sendNpcChat("What are you looking for?", 591)
    player.nextDialogue = 1756093863;

def chat_1756093863(player):
    player.getDH().sendPlayerChat("I wish to take on the physical appearance of a monkey!", 591)
    player.nextDialogue = 1756093864;

def chat_1756093864(player):
    player.getDH().sendNpcChat("Ah, you're not the first. I have a couple of greegrees that", "will do just that.", 591)
    player.nextDialogue = 1756093865;

def chat_1756093865(player):
    player.getDH().sendNpcChat("They don't come cheap mind.", 591)
    player.nextDialogue = 1756093866;

def chat_1756093866(player):
    player.getDH().sendPlayerChat("How much for your cheapest greegree?", 591)
    player.nextDialogue = 1756093867;

def chat_1756093867(player):
    player.getDH().sendNpcChat("I'd say around 500,000 coins should cover", "my production costs.", 591)
    player.nextDialogue = 1756093868;

def chat_1756093868(player):
    if ItemAssistant.hasItemAmountInInventory(player, 995, 500000):
        player.getDH().sendPlayerChat("Sounds reasonable, thanks!", 591)
        ItemAssistant.deleteItemFromInventory(player, 995, 500000)
        player.nextDialogue = 1756093869;
    else:
        player.getDH().sendPlayerChat("Ah, I didn't bring enough money with me.", 591)

def chat_1756093869(player):
    player.getDH().sendNpcChat("A pleasure doing business with you, have fun", "being an ape. Don't let the real apes catch", "you though! Ha ha ha.", 591)
    stage = player.getQuest(5).getStage()
    if stage == 2:
        player.getQuest(5).setStage(3)
        QuestHandler.updateAllQuestTab(player);
        if not ItemAssistant.hasItemAmountInInventory(player, 4024, 1):
            ItemAssistant.addItem(player, 4024, 1)

def chat_1057961997(player):
    player.getDH().sendPlayerChat("Hello? Is anybody there?", 591)
    player.nextDialogue = 1057961998;

def chat_1057961998(player):
    player.getDH().sendNpcChat("That depends on who is asking....", 591)
    player.nextDialogue = 1057961999;

def chat_1057961999(player):
    player.getDH().sendPlayerChat("I am in service of King Narnode Shareen. I have", "been sent here to retrieve information from the ", "gnome spy mission.", 591)
    player.nextDialogue = 1057962000;

def chat_1057962000(player):
    player.getDH().sendNpcChat("At last! I've been stuck here for weeks.", 591)
    player.nextDialogue = 1057962001;

def chat_1057962001(player):
    player.getDH().sendNpcChat("The monkeys are preparing the launch an attack on ", "the gnome stronghold!", 591)
    player.nextDialogue = 1057962002;

def chat_1057962002(player):
    player.getDH().sendPlayerChat("Really...? I find that hard to believe....", 591)
    player.nextDialogue = 1057962003;

def chat_1057962003(player):
    player.getDH().sendNpcChat("I know it's difficult but they have a secret weapon! There ", "isn't much time. You must help me stop them.", 591)
    player.nextDialogue = 1057962004;

def chat_1057962004(player):
    player.getDH().sendPlayerChat("Me? I'm really only a messenger...", 591)
    player.nextDialogue = 1057962005;

def chat_1057962005(player):
    player.getDH().sendNpcChat("You must be tough if you were sent by the King! ", "Please - I need your help.", 591)
    player.nextDialogue = 1057962006;

def chat_1057962006(player):
    player.getDH().sendPlayerChat("Well, I suppose I could offer you some assistance.", "What can I do though?", 591)
    player.nextDialogue = 1057962007;

def chat_1057962007(player):
    player.getDH().sendNpcChat("There's a trapdoor to the east of here! I'll meet you" , "down there.", 591)
    player.getQuest(5).setStage(4)
    QuestHandler.updateAllQuestTab(player);
    player.nextDialogue = 1057962008;

def chat_1057962008(player):
    player.getDH().sendPlayerChat("Very well.", 591)
    
def start_mm_dialogue(player):
    stage = player.getQuest(5).getStage()
    if stage == 4 or stage == 5:
        player.getDH().sendDialogues(1635409878)

def chat_1635409878(player):
    player.setNpcType(1453)
    Movement.stopMovement(player)
    player.getDH().sendPlayerChat("Wait... so what are we doing here?", 591)
    player.nextDialogue = 1635409879;

def chat_1635409879(player):
    Movement.stopMovement(player)
    player.getDH().sendNpcChat("The demon...!!!!", 591)
    player.nextDialogue = 1635409880;

def chat_1635409880(player):
    Movement.stopMovement(player)
    player.getDH().sendNpcChat("He must be stopped, the gnomish race depends on you!", 591)
    player.nextDialogue = 1635409881;

def chat_1635409881(player):
    Movement.stopMovement(player)
    player.getDH().sendPlayerChat("A demon? You lied to me!", 591)
    player.nextDialogue = 1635409882;

def chat_1635409882(player):
    Movement.stopMovement(player)
    player.getDH().sendNpcChat("I didn't lie... I just didn't tell you the whole truth.", "Look, there isn't time, here it comes!", 591)
    player.nextDialogue = 35346346;
    #World.getNpcHandler().spawnNpc(player, 1472, 2651, 4573, 1, 0, 220, 35, 370, 370, True, True)
    
def chat_35346346(player):
    player.getQuest(5).setStage(5)
    QuestHandler.updateAllQuestTab(player);
    player.getDH().sendStatement("A jungle demon appears!");
    Movement.setNewPath(player, 2767, 9197);
    NpcHandler.spawnNpc(player, 1443, 2767, 9197, 0, True, True);

def kill_npc_1443(player):
    stage = player.getQuest(5).getStage()
    if stage == 5:
        player.getQuest(5).setStage(6)
        QuestHandler.updateAllQuestTab(player);
        player.getDH().sendPlayerChat("I'd better update the King on what's happened.", 591)
        player.getPA().sendMessage("I'd better update the King on what's happened.")

def chat_22433224(player):
    player.getDH().sendNpcChat("Ah there you are! Karam told me all about what" , "you did! You saved us all.", 591)
    player.nextDialogue = 22433225;

def chat_22433225(player):
    player.getDH().sendPlayerChat("Oh, it was nothing.", 591)
    player.nextDialogue = 22433226;

def chat_22433226(player):
    player.getDH().sendPlayerChat("It wasn't the first time I've slain a demon and it", "certainly won't be the last.", 591)
    player.nextDialogue = 22433227;

def chat_22433227(player):
    player.getDH().sendNpcChat("Bravo! The entire gnome kingdom owes you greatly,", "please accept this gift as a reward.", 591)
    player.nextDialogue = 22433228;

def chat_22433228(player):
    player.getQuest(5).setStage(7)
    QuestHandler.updateAllQuestTab(player);
    ItemAssistant.addItemToInventoryOrDrop(player, 10586, 3)
    reward = QuestReward("Ability to equip Dragon Scimitars", "Access to Ape Atoll", "1 Quest Point", "3x Combat Lamp")
    player.completeQuest("Monkey Madness", reward, 4587)