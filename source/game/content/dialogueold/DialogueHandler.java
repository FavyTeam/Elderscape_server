package game.content.dialogueold;

import core.GameType;
import core.Plugin;
import core.ServerConstants;
import game.content.achievement.PlayerTitle;
import game.content.bank.BankPin;
import game.content.donator.Yell;
import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.barrows.BarrowsRepair;
import game.content.minigame.lottery.Lottery;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.PlayerGameTime;
import game.content.quicksetup.Presets;
import game.content.skilling.Slayer;
import game.content.skilling.Slayer.Task;
import game.content.starter.NewPlayerContent;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.npc.impl.KrakenCombat;
import game.player.Player;
import utility.Misc;

public class DialogueHandler {

	/**
	 * Handles all talking
	 *
	 * @param dialogue The dialogue you want to use
	 * @param npcId The npc id that the chat will focus on during the chat
	 */

	public void sendDialogues(int dialogue) {
		player.nextDialogue = 0;
		player.setDialogueAction(dialogue);
		switch (dialogue) {
			case 4343:
				sendStatement("Resetting");
				break;

			case 0:
				player.setDialogueAction(0);
				player.getPA().closeInterfaces(true);
				break;

			case 13:
				sendOption("Cape shop #1", "Cape shop #2", "Hats, boots & gloves shop", "Robe shop", "Previous");
				break;
			case 16:
				sendOption("Armour shop #1", "Armour shop #2", "Armour shop #3", "Weapon shop", "Previous");
				break;
			case 17:
				sendOption("Runes and weapon shop", "Armour shop", "Previous");
				break;
			case 19:
				sendOption("Ammo and weapon shop", "Armour shop", "Previous");
				break;

			case 22:
				sendOption("Modern spellbook", "Ancient magicks", "Lunar spellbook");
				break;
			case 24:
				sendNpcChat("Goodness me you look awful!", "I think we should change that...", FacialAnimation.NEARLY_CRYING.getAnimationId());
				player.nextDialogue = 25;
				break;
			case 25:
				sendOption("Change appearance", "You can't talk, you're ugly!");
				break;

			case 39:
				String taskString = Slayer.getTaskString(player);
				sendStatement(taskString);
				Task b = Slayer.getTask(player.slayerTaskNpcType);
				if (b != null) {
					player.nextDialogue = 496;
				}
				break;

			case 84:
				sendStatement("@red@This will lead you to level 50 wilderness, are you sure?");
				player.nextDialogue = 85;
				break;
			case 85:
				sendOption("Yes", "No");
				break;

			case 112:
				sendOption("Recipe for disaster", "Previous");
				break;

			case 116:
				if (player.setPin) {
					sendOption("Delete bank pin", "Nothing");
					return;
				} else {
					BankPin.open(player);
				}
				break;

			/* Start of White skull feature. */

			case 136:
				sendStatement("Are you sure you want to activate a white skull?");
				player.nextDialogue = 96;
				break;

			case 96:
				sendOption("Yes", "No");
				break;

			/* End of White skull feature. */

			/* Start of Red skull feature. */

			case 137:
				sendStatement("Activate a red skull and lose all items on death. Are you sure?");
				player.nextDialogue = 138;
				break;

			case 138:
				sendOption("Yes", "No");
				break;

			/* End of Red skull feature. */

			case 1:
				sendNpcChat("Would you like to repair barrows pieces for " + BarrowsRepair.getRepairCost(player) + " each?", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 2;
				break;

			case 2:
				sendOption("Yes.", "No.");
				break;

			case 3:
				sendNpcChat("You do not have enough coins.", FacialAnimation.DEFAULT.getAnimationId());

				break;

			case 4:
				sendNpcChat("Remember that dungeoneering items have no item bonuses.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 92;
				break;

			case 5:
				sendNpcChat("Remove your equipment please.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 6:
				sendOption("Permanent mute", "IP-mute", "Timed mute");
				break;

			case 8:
				sendOption("Permanent ban", "Timed ban", "Un-ban", "Un-mute", "Un-IP-mute");
				break;

			case 9:
				sendOption("Copy worn equipment.", "Copy inventory.");
				break;

			case 10:
				sendNpcChat("'Ello, and what are you after then?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 11;
				break;

			case 11:
				sendOption("I need another assignment", "May you spare me an Enchanted gem?", "Upgrade to Slayer helmet (i)", "I need a boss assignment!", "Untradeable shop");
				break;

			case 12:
				sendNpcChat("Your new task is to kill",
				            player.slayerTaskNpcAmount + " " + NpcDefinition.getDefinitions()[player.slayerTaskNpcType].name + ". Good luck, " + Misc.capitalize(
						            player.getPlayerName()) + "!", FacialAnimation.DEFAULT.getAnimationId());
				Task t = Slayer.getTask(player.slayerTaskNpcType);
				if (t != null) {
					player.nextDialogue = 495;
				}
				break;

			case 14:
				sendNpcChat("Would you like to upgrade to",
				            "Slayer helmet (charged) for " + Misc.formatRunescapeStyle(Slayer.getSlayerHelmUpgradeCost()) + " " + ServerConstants.getMainCurrencyName()
				                                                                                                                                 .toLowerCase() + "?",
				            FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 21;
				break;

			case 20:
				sendNpcChat("You already have a Slayer helmet (charged).", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 15:
				sendNpcChat("You need to buy a Slayer helmet from my shop first.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 21:
				sendOption("Yes", "No");
				break;

			case 184:
				String taskName = "Nothing";
				if (player.slayerTaskNpcType >= 0) {
					if (NpcDefinition.getDefinitions()[player.slayerTaskNpcType] != null) {
						taskName = NpcDefinition.getDefinitions()[player.slayerTaskNpcType].name;
					}
				}
				Task task = Slayer.getTask(player.slayerTaskNpcType);
				if (task != null) {
					sendNpcChat("You already have an assignment to kill " + player.slayerTaskNpcAmount + " " + taskName + ", ", "which can be found in " + task.getLocation() + ".", "Would you like to reset this task in return", "for " + Misc.formatRunescapeStyle(Slayer.getTaskResetCost()) + " " + ServerConstants.getMainCurrencyName() + "?", FacialAnimation.DEFAULT.getAnimationId());
					player.nextDialogue = 203;
				}
				// Now it's a boss task.
				else {
					sendNpcChat("You already have an assignment to kill " + player.slayerTaskNpcAmount + " " + taskName + ", ", "Would you like to reset this task in return", "for " + Misc.formatRunescapeStyle(Slayer.getTaskResetCost()) + " " + ServerConstants.getMainCurrencyName() + "?", FacialAnimation.DEFAULT.getAnimationId());
					player.nextDialogue = 203;
				}
				break;

			case 104:
				sendNpcChat("Young one, which altar do you seek?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 105;
				break;

			case 105:
				sendOption("Air altar", "Fire altar", "Cosmic altar", "Next");
				break;

			case 106:
				sendOption("Nature altar", "Law altar", "Death altar", "Previous");
				break;

			case 142:
				sendOption("Entrana (Skilling)", "Agility", "Mining & Smithing", "Previous");
				break;

			case 143:
				sendOption("Gnome course", "Barbarian course", "Previous");
				break;

			case 144:
				sendNpcChat("You do not have 2m coins", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 159:
				sendNpcChat("Sorry, only players with Train Combat mode", "have access to player titles.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 160:
				sendNpcChat("Hello there young one.", "Where would you like to be teleported to?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 161;
				break;

			case 161:
				sendOption("Boss teleports", "Skilling teleports", "Monster teleports", "Minigame teleports", "City teleports");
				break;

			case 163:
				sendStatement("You found a hidden tunnel! Do you want to enter it?");
				player.nextDialogue = 164;
				break;

			case 164:
				sendOption("Yes I'm fearless!", "No.");
				player.setDialogueAction(164);
				break;

			case 103:
				sendOption("Dagannoth Kings", "Corporeal Beast", "TzTok-Jad", "Venenatis (28 Wilderness)", "Next");
				break;

			case 101:
				sendOption("Callisto (44 Wilderness)", "Next");
				break;

			case 69:
				sendOption("K'ril Tsutsaroth", "Commander Zilyana", "Kree'arra", "General Graardor", "Previous");
				break;

			case 82:
				sendOption("Ice Strykewyrm (44 Wilderness)", "King Black Dragon (44 Wilderness)", "Chaos Elemental (50 Wilderness)", "Mage arena (55 Wilderness)", "Next");
				break;

			case 174:
				int random = Misc.random(4);
				if (random == 0) {
					sendNpcChat("I am busy right now.", FacialAnimation.DEFAULT.getAnimationId());
				} else if (random == 1) {
					sendNpcChat("Come tomorrow.", FacialAnimation.DEFAULT.getAnimationId());
				} else if (random == 2) {
					sendNpcChat("Sorry, i am in the middle of something.", FacialAnimation.DEFAULT.getAnimationId());
				} else if (random == 3) {
					sendNpcChat("Please visit another time.", FacialAnimation.DEFAULT.getAnimationId());
				} else if (random == 4) {
					sendNpcChat("I need to concentrate.", FacialAnimation.DEFAULT.getAnimationId());
				}
				break;

			case 110:
				sendOption("Rock crabs", "Slayer tower", "Brimhaven dungeon", "Bandit camp", "Next");
				break;

			case 171:
				sendNpcChat("You have defeated TzTok-Jad. I am most impressed!", "Please accept this gift.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 172:
				sendNpcChat("You're on your own now " + player.getPlayerName() + ", prepare to fight for", "your life!", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 173:
				sendNpcChat("You may only have one Fire cape at a time.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 176:
				sendOption("Taverly dungeon", "Edgeville dungeon", "Fremennik dungeon", "Tzhaar", "Previous");
				break;

			case 180:
				sendOption("Small ninja", "Large ninja", "Monkey guard", "Next", "Nothing.");
				break;

			case 181:
				sendOption("Bearded monkey guard", "Blue face monkey guard", "Small zombie", "Next", "Previous");
				break;

			case 182:
				sendOption("Large zombie", "Karamja monkey", "Previous");
				break;

			case 183:
				sendOption("That's okay; I want a party hat!", "Stop, i want to keep my cracker");
				break;

			case 187:
				sendOption("Yes.", "No.");
				break;

			case 188:
				sendOption("Claim Member prize!", "Not now.");
				break;

			case 189:
				sendOption("I want to be a spawner now.", "Not now.");
				break;

			case 190:
				sendNpcChat("You do not have any broken chaotic equipment.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 191:
				sendNpcChat("Get back to training this instant!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 192;
				break;

			case 192:
				sendOption("Let me check your store please.", "Yes sir!");
				break;

			case 1920:
				sendPlayerChat("Yes sir!", DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
				break;

			case 1921:
				sendPlayerChat("Let me check your store please.", DialogueHandler.FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 1922;
				break;

			case 1922:
				sendNpcChat("Certainly!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 1923;
				break;

			case 1923:
				player.getShops().openShop(49);
				break;

			case 195:
				sendStatement("You are not maxed out in all skills.");
				break;

			case 196:
				sendStatement("Only 'Train Combat' accounts are eligible.");
				break;

			case 197:
				sendStatement("Not enough coins.");
				break;

			case 198:
				sendStatement("You need 2080 total level to claim the Max cape.");
				break;

			case 199:
				sendOption("Purchase Max cape for " + Misc.formatRunescapeStyle(ServerConstants.getMaxCapeCost()) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + ".",
				           "Purchase Infernal cape for " + Misc.formatRunescapeStyle(ServerConstants.getInfernalCapeCost()) + " " + ServerConstants.getMainCurrencyName()
				                                                                                                                                   .toLowerCase() + ".",
				           "Purchase Imbued saradomin cape for " + Misc.formatRunescapeStyle(ServerConstants.getImbuedCapeCost()) + " " + ServerConstants.getMainCurrencyName()
				                                                                                                                                         .toLowerCase() + ".",
				           "Purchase Imbued zamorak cape for " + Misc.formatRunescapeStyle(ServerConstants.getImbuedCapeCost()) + " " + ServerConstants.getMainCurrencyName()
				                                                                                                                                       .toLowerCase() + ".",
				           "Purchase Imbued guthix cape for " + Misc.formatRunescapeStyle(ServerConstants.getImbuedCapeCost()) + " " + ServerConstants.getMainCurrencyName()
				                                                                                                                                      .toLowerCase() + ".");
				break;

			case 203:
				sendOption("Yes.", "No, nevermind.");
				break;

			case 204:
				sendNpcChat("You do not have 20 slayer points.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 206:
				sendOption("Claim Legendary Member prize!", "Not now.");
				break;

			case 207:
				sendOption("Claim Dicer prize!", "Not now.");
				break;

			case 208:
				sendOption("How many credits do i have?", "Open credit shop 1", "Open credit shop 2", "Close");
				break;

			case 209:
				sendOption("Blood money shop", "Untradeables", "Buy-back untradeables", "Auto-buy untradeables on death");
				break;

			case 211:
				sendNpcChat("You do not have any artefacts.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 212:
				sendNpcChat("Thank you! Find as many as you can.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 214:
				sendNpcChat("You do not have any barrows equipment.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 216:
				sendOption("Melee shop", "Ranged shop", "Magic shop", "Equipment shop");
				break;

			case 217:
				String option = player.displayBots ? "off" : "on";
				sendOption("Change password", "Turn profile Pvm & rare drop announcement privacy to " + (player.profilePrivacyOn ? "off" : "on"), "Pet shops",
				           "Turn " + option + " bots", "Next");
				break;

			case 218:
				sendOption("Animal pets", "Magical pets", "Close");
				break;

			case 219:
				sendOption("Pking achievement shop", "Gloves & books");
				break;

			case 221:
				sendOption("Dark crabs (21 Wilderness)", "Revenants (27 Wilderness)", "Venenatis (28 Wilderness)", "Callisto (44 Wildeness)", "Next");
				break;

			case 220:
				sendOption("Ice Strykewyrm (44 Wilderness)", "King Black Dragon (44 Wilderness)", "Chaos Elemental (50 Wilderness)", "Mage arena (55 Wilderness)", "Previous");
				break;

			case 222:
				sendNpcChat("Would you like to be protected by my fellow wizards while", "at Wilderness course? 500k for 30 minutes is a fair deal.",
				            FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 223;
				break;

			case 223:
				player.getDH().sendOption("Yes", "No");
				break;

			case 224:
				sendNpcChat("You do not have 500k coins.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 225:
				sendOption("Equipment shop", "Merit shop (Runecrafting)");
				break;

			case 226:
				sendOption("Toggle teleport warning", "Toggle kill screenshots", GameType.isOsrsEco() ? "Change gameplay settings" : "Empty",
				           "Turn " + (player.xpLock ? "off" : "on") + " experience lock", "Previous");
				break;

			case 228:
				sendNpcChat("Hello again. How is " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + " going?", player.skillCapeMasterExpression);
				player.nextDialogue = 229;
				break;

			case 229:
				sendOption("May i buy a Skillcape of " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ", please?",
				           "May i buy the trimmed Skillcape of " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ", please?");
				break;

			case 230:
				sendNpcChat("Most certainly; it has been a pleasure to watch you", "grow. I am privileged to have been", "instrumental in your learning, but i must ask for a",
				            "donation of 99000 coins to cover the expense of the cape.", player.skillCapeMasterExpression);
				player.nextDialogue = 231;
				break;

			case 231:
				sendOption("I'm afraid that's too much money for me.", "Fair enough.");
				break;

			case 232:
				sendNpcChat("Good luck to you, " + player.getCapitalizedName() + ".", player.skillCapeMasterExpression);
				break;

			case 233:
				sendNpcChat("I'm sorry, you are not yet worthy of wearing the Skillcape.", player.skillCapeMasterExpression);
				break;

			case 234:
				sendNpcChat("You must have achieved 100 million experience in " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ".", player.skillCapeMasterExpression);
				break;

			case 235:
				sendNpcChat("I am honoured to have raised a student who surpassed me.", "Use this legendary Skillcape with your head held high.",
				            "A donation of 2 million coins will be just fine", "to cover the cost of this rare cape.", player.skillCapeMasterExpression);
				player.nextDialogue = 236;
				break;

			case 236:
				sendOption("I'm afraid that's too much money for me.", "Fair enough.");
				break;

			case 237:
				sendNpcChat("You do not seem to have the correct amount of coins.", player.skillCapeMasterExpression);
				break;

			case 238:
				sendStatement("It looks as if you can climb the cart. Would you like to try?");
				player.nextDialogue = 239;
				break;

			case 239:
				sendOption("Yes, i am very nimble and agile!", "No, i am happy where i am thanks!");
				break;

			case 240:
				sendNpcChat("You have not achieved 200 million experience in " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ".", player.skillCapeMasterExpression);
				break;

			case 241:
				sendNpcChat("Your title has been set, do guide and help others in " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ".",
				            player.skillCapeMasterExpression);
				PlayerTitle.setTitle(player, ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + " Legend", false, false);
				break;

			case 242:
				sendStatement("Your Pvm privacy has been turned " + (player.profilePrivacyOn ? "on" : "off"));
				break;

			case 245:
				sendNpcChat("You have played for " + PlayerGameTime.getHoursOnline(player) + " hours, since",
				            "arriving " + PlayerGameTime.getDaysSinceAccountCreated(player) + " days ago on the " + player.accountDateCreated + ".",
				            "Your active playtime is " + GameTimeSpent.getActivePlayTimeHoursInLastWeek(player) + " hours in the last 7 days.",
				            FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 246:
				sendNpcChat("You currently have " + player.getSlayerPoints() + " slayer points.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 248:
				sendOption("Upgrade to the next Donator rank", "Keep the Donator tokens");
				break;

			case 249:
				sendOption("Gain 740k prayer xp from the lamp (70 prayer)", "Keep the Prayer lamp.");
				break;

			case 250:
				sendNpcChat("Hello, you'll need at least 100 tokens to enter.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 251:
				sendNpcChat("You are allowed to enter.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 252:
				sendOption("Yes, change game mode to " + Misc.capitalize(player.selectedGameMode), "No, i'll keep my " + Misc.capitalize(player.getGameMode()) + " game mode",
				           "Back");
				break;

			case 253:
				sendNpcChat("Would you like to have your hides tanned for 100gp each?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 254;
				break;

			case 254:
				sendOption("Yes", "No");
				break;

			case 256:
				sendOption("Donator Cosmetics shop #1", "Donator Cosmetics shop #2");
				break;
			case 257:
				sendOption("Donator shop", "Open Donator tokens trading post", "What is inside Mystery boxes?", "Donate now for alot of special rewards!", "Next");
				break;

			case 258:
				sendOption("Ironman skilling store", "Ironman equipment store", "Hats & robe sets shop", "Cape shop");
				break;
			case 259:
				sendOption("Red throne", "White throne", "Orange throne");
				break;

			case 260:
				sendOption("Claim achievement rewards", "Community event shop", "Open www.dawntained.com/event");
				break;

			case 261:
				sendOption("Supplies shop", "Skilling shop", "Hats & robe sets shop", "Cape shop");
				//sendOption3("Supplies shop", "Hats & robe sets shop", "Cape shop");
				break;

			case 262:
				sendOption("Equip " + Presets.getCurrentPresetName(player), "Update " + Presets.getCurrentPresetName(player), "Rename " + Presets.getCurrentPresetName(player));
				break;

			case 263:
				sendOption("Update the " + Presets.getCurrentPresetName(player) + " preset", "Don't you dare update!");
				break;

			case 264:
				sendOption("Toggle boss kill counts to: @blu@" + (player.bossKillCountMessage ? "off" : "on"), 
									"Change loot value notification",
				           "Toggle profile Pvm and rare drop announcement privacy to: @blu@" + (player.profilePrivacyOn ? "off" : "on"),
						Yell.getYellOptionString(player, false));
				break;

			case 265:
				sendOption("Kills left?", "Obtain a Pvp task", "Claim reward", "What are the rewards for completing a Pvp task?");
				break;

			case 266:
				sendStatement("@red@Picking it up will skull and teleblock you!");
				player.nextDialogue = 267;
				break;

			case 471:
				sendStatement("@red@Picking it up will skull and teleblock you!");
				player.nextDialogue = 472;
				break;

			case 472:
				sendOption("Pick up the blood key quick!", "Too risky..");
				break;

			case 267:
				sendOption("Pick up the blood key quick!", "Too risky..");
				break;

			case 268:
				sendOption("Tournament supplies", "Achievement items");
				break;

			case 269:
				sendOption("Achievements shop", "Skill cape shop");
				break;

			case 270:
				sendNpcChat("Welcome to " + ServerConstants.getServerName() + ", " + player.getPlayerName() + "!", "A starter kit has been added to your bank.",
				            FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 271;
				break;

			case 271:
				sendNpcChat("Would you like me to show you around?", "I recommend this if you're a new player.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 272;
				break;

			case 272:
				sendOption("Sure thing!", "No thanks, I know my way around " + ServerConstants.getServerName() + ".");
				break;

			case 273:
				sendNpcChat("This area contains some important features, such as", "altars, the Blood key chest, a Highscores statue and even",
				            "an object to claim a max cape from.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 274;
				player.getPA().movePlayer(3096, 3510, 0);
				break;

			case 274:
				sendNpcChat("Here you'll find almost anything that you need.", "Spend your Blood money at the Blood money shop,", "which sells a variety of useful items.",
				            FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 276;
				player.getPA().movePlayer(3083, 3510, 0);
				break;

			case 276:
				sendNpcChat("By using the Teleport NPC or any spell in a spellbook, ", "you can access all major teleports for " + ServerConstants.getServerName() + ".",
				            FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 277;
				player.getPA().movePlayer(3094, 3495, 0);
				break;

			case 277:
				sendNpcChat("Although " + ServerConstants.getServerName() + " primarily focuses on PvP activities, ", "Skillers and PvMers are also welcome!",
				            FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 278;
				player.getPA().movePlayer(3419, 3572, 2);
				break;

			case 278:
				sendNpcChat("Slaying bosses and revenants in the wilderness", "is one of the best money making methods!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 279;
				player.getPA().movePlayer(2978, 3735, 0);
				break;

			case 279:
				sendNpcChat("Skilling resources such as logs, fish and ore can be ", "sold to the Blood money shop for decent profit.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 281;
				player.getPA().movePlayer(2857, 3336, 0);
				break;

			case 281:
				sendNpcChat("Check out the settings tab for many different client options.", "Customise your client to how you like it!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 283;
				player.getPA().movePlayer(NewPlayerContent.END_TUTORIAL_X_COORDINATE, NewPlayerContent.END_TUTORIAL_Y_COORDINATE, 0);
				//player.getPA().sendMessage(":packet:facecompass");
				break;
			case 283:
				sendNpcChat("Have fun " + player.getPlayerName() + "!", "Remember to invite your friends or create", "your own clan to dominate and survive in the Wilderness.",
				            FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 284;
				break;
			case 284:
				NewPlayerContent.endTutorial(player);
				break;

			case 285:
				sendOption("Pick the flowers.", "Leave the flowers.");
				break;

			case 286:
				sendOption("@red@Delete my inventory", "No!");
				break;

			case 287:
				sendPlayerChat("Can i please view the player title shop?", DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 288;
				break;

			case 288:
				sendNpcChat("Ofcourse!", DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 289;
				break;
			case 289:
				PlayerTitle.displayInterface(player);
				break;

			case 290:
				if (ItemAssistant.hasEquipment(player)) {
					player.getDH().sendDialogues(5);
					return;
				}
				player.getPA().displayInterface(3559);
				player.canChangeAppearance = true;
				break;

			case 292:
				sendPlayerChat("Ok ok, I'm sorry!", DialogueHandler.FacialAnimation.ALMOST_CRYING.getAnimationId());
				for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
					Npc npc = NpcHandler.npcs[index];
					if (npc == null) {
						continue;
					}
					if (npc.name.equals("Make-over mage")) {
						npc.forceChat("lol slapped cu");
						npc.requestAnimation(7644);
						npc.gfx0(1211);
						break;
					}
				}
				break;

			case 293:
				sendNpcChat("You need " + Misc.formatRunescapeStyle(Slayer.getTaskResetCost()) + " " + ServerConstants.getMainCurrencyName() + " to reset a task.",
				            DialogueHandler.FacialAnimation.DEFAULT.getAnimationId());
				break;
			case 294:
				player.getDH().sendPlayerChat("Why the grizzly face?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 295:
				player.getDH().sendNpcChat("You're not funny...", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 296:
				player.getDH().sendPlayerChat("You should get in the.... sun more.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 297:
				player.getDH().sendNpcChat("You're really not funny...", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 298:
				player.getDH().sendPlayerChat("One second, let me take a picture of you with", "my.... kodiak camera.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 299:
				player.getDH().sendNpcChat(".....", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 300:
				player.getDH().sendPlayerChat("Feeling.... blue.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 301:
				player.getDH().sendNpcChat("If you don't stop, I'm going to leave some...", "brown... at your feet, human.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;


			case 302: //Abyssal orphan
				player.getDH().sendNpcChat("You killed my father.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 303:
				player.getDH().sendPlayerChat("Yeah, don't take it personally.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 304:
				player.getDH().sendNpcChat("In his dying moment, my father poured", "his last ounce of strength into my creation. My being is", "formed from his remains.",
				                           FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 305:
				player.getDH().sendNpcChat("When your own body is consumed to", "nourish the Nexus, and an army of scions arises from your",
				                           "corpse, I trust you will not take it personally either.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 306:
				player.getDH().sendPlayerChat("No, I am your father.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 307:
				player.getDH().sendNpcChat("No you're not.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 308: //Baby mole
				player.getDH().sendPlayerChat("Hey, Mole. How is life above ground?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 309:
				player.getDH().sendNpcChat("Well, the last time I was above ground, I was having to contend with people throwing snow at", "some weird yellow duck in my park.",
				                           FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 310:
				player.getDH().sendPlayerChat("Why were they doing that?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 311:
				player.getDH().sendNpcChat("No idea, I didn't stop to ask as an angry mob was closing in on them pretty quickly.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 312:
				player.getDH().sendPlayerChat("Sounds awful.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 313:
				player.getDH().sendNpcChat("Anyway, keep Molin'!", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 314: //Hellpuppy
				player.getDH().sendPlayerChat("Hell yeah! Such a cute puppy!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 315:
				player.getDH().sendNpcChat("Silence mortal! Or I'll eat your soul.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 316:
				player.getDH().sendPlayerChat("Would that go well with lemon?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 317:
				player.getDH().sendNpcChat("Grrr...", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;


			case 318: //Kalphite princess
				player.getDH().sendPlayerChat("What is it with your kind and potato cactus?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 319:
				player.getDH().sendNpcChat("Truthfully?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 320:
				player.getDH().sendPlayerChat("Yeah, please.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 321:
				player.getDH().sendNpcChat("Soup. We make a fine soup with it.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 322:
				player.getDH().sendPlayerChat("Kalphites can cook?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 323:
				player.getDH().sendNpcChat("Nah, we just collect it and put it there because we know fools like yourself will come",
				                           "down looking for it then inevitably be killed by my mother.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 324:
				player.getDH().sendPlayerChat("Evidently not, that's how I got you!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 325:
				player.getDH().sendNpcChat("Touche", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 326: //Olmlet
				player.getDH().sendPlayerChat("Where do creatures like you come from?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 327:
				player.getDH().sendNpcChat("From eggs, of course! You can't make an olmlet", "without breaking an egg.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 328:
				player.getDH().sendPlayerChat("That's... informative. Thank you.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 329: //Pet chaos elemental
				player.getDH().sendPlayerChat("Is it true a level 3 skiller caught one of your siblings?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 330:
				player.getDH().sendNpcChat("Yes, they killed my mummy, kidnapped my brother,", "smiled about it and went to sleep.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 331:
				player.getDH().sendPlayerChat("Aww, well you have me now! I shall call you Squishy", "and you shall be mine and you shall be my Squishy",
				                              FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 332:
				player.getDH().sendPlayerChat("Come on, Squishy come on, little Squishy!", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 333: //Pet dagannoth prime
				player.getDH().sendPlayerChat("So despite there being three kings, you're", "clearly the leader, right?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 334:
				player.getDH().sendNpcChat("Definitely.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 335:
				player.getDH().sendPlayerChat("I'm glad I got you as a pet.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 336:
				player.getDH().sendNpcChat("Ugh. Human, I'm not a pet.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 337:
				player.getDH().sendPlayerChat("Stop following me then.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 338:
				player.getDH().sendNpcChat("I can't seem to stop.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 339:
				player.getDH().sendPlayerChat("Pet.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 340: //Pet dagannoth rex
				player.getDH().sendPlayerChat("Do you have any berserker rings?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 341:
				player.getDH().sendNpcChat("Nope.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 342:
				player.getDH().sendPlayerChat("You sure?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 343:
				player.getDH().sendNpcChat("Yes.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 344:
				player.getDH().sendPlayerChat("So, if I tipped you upside down and shook you, you'd", "not drop any berserker rings?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 345:
				player.getDH().sendNpcChat("Nope.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 346:
				player.getDH().sendPlayerChat("What if I endlessly killed your father for weeks on end,", "would I get one then?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 347:
				player.getDH().sendNpcChat("Been done by someone, nope.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 348: //Pet dagannoth supreme
				player.getDH().sendPlayerChat("Hey, so err... I kind of own you now.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 349:
				player.getDH().sendNpcChat("Tsssk. Next time you enter those caves, human,", "my father will be having words.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 350:
				player.getDH().sendPlayerChat("Maybe next time I'll add your brothers to my collection.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 351: //Pet dark core
				player.getDH().sendPlayerChat("Got any sigils for me?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 352:
				player.getDH().sendNpcChat("I do not.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 353:
				player.getDH().sendPlayerChat("Damnit Core-al!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 354:
				player.getDH().sendPlayerChat("Let's bounce!", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 355: //Pet general graardor
				player.getDH().sendPlayerChat("Not sure this is going to be worth my time but... ", "how are you?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 356:
				player.getDH().sendNpcChat("SFudghoigdfpDSOPGnbSOBNfd", "bdnopbdnopbddfnopdfpofhdARRRGGGGH", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 357:
				player.getDH().sendPlayerChat("Nope. Not worth it.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;


			case 358: //Pet k'ril tsutsaroth
				player.getDH().sendPlayerChat("How's life in the light?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 359:
				player.getDH().sendNpcChat("Burns slightly.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 360:
				player.getDH().sendPlayerChat("You seem much nicer than your father. He's mean.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 361:
				player.getDH().sendNpcChat("If you were stuck in a very dark cave for centuries", "you'd be pretty annoyed too.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 362:
				player.getDH().sendPlayerChat("I guess.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 363:
				player.getDH().sendNpcChat("He's actually quite mellow really.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 364:
				player.getDH().sendPlayerChat("Uh.... Yeah.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 365: //Pet kraken
				player.getDH().sendPlayerChat("What's Kraken?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 366:
				player.getDH().sendNpcChat("Not heard that one before.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 367:
				player.getDH().sendPlayerChat("How are you actually walking on land?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 368:
				player.getDH().sendNpcChat("We have another leg, just below the center of our body", "that we use to move across solid surfaces.",
				                           FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 369:
				player.getDH().sendPlayerChat("That's.... interesting.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 370: //Pet kree'arra
				player.getDH().sendPlayerChat("Huh... that's odd... I thought that would be big news.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 371:
				player.getDH().sendNpcChat("You thought what would be big news?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 372:
				player.getDH()
				      .sendPlayerChat("Well there seems to be an absence of a certain ornithological", "piece: a headline regarding mass awareness of a certain avian variety.",
				                      FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 373:
				player.getDH().sendNpcChat("What are you talking about?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 374:
				player.getDH().sendPlayerChat("Oh have you not heard?", "It was my understanding that everyone had heard....", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 375:
				player.getDH().sendNpcChat("Heard wha...... OH NO!!!!?!?!!?!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 376:
				player.getDH().sendPlayerChat("OH WELL THE BIRD, BIRD, BIRD, BIRD BIRD IS THE WORD.", "OH WELL THE BIRD, BIRD, BIRD, BIRD BIRD IS THE WORD.",
				                              FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 377: //Pet penance queen
				player.getDH().sendPlayerChat("Of all the high gamble rewards I could have won, I won you...", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 378:
				player.getDH().sendNpcChat("Keep trying, human. You'll never win that Dragon Chainbody.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 379: //Pet smoke devil
				player.getDH().sendPlayerChat("Your kind comes in three different sizes?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 380:
				player.getDH().sendNpcChat("Four, actually.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 381:
				player.getDH()
				      .sendPlayerChat("Wow. Whoever created you wasn't very creative.", "You're just resized versions of one another!", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 382: //Pet snakeling
				player.getDH().sendPlayerChat("Hey little snake!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 383:
				player.getDH().sendNpcChat("Soon, Zulrah shall establish dominion over this plane.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 384:
				player.getDH().sendPlayerChat("Wanna play fetch?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 385:
				player.getDH().sendNpcChat("Submit to the almighty Zulrah.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 386:
				player.getDH().sendPlayerChat("Walkies? Or slidies...?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 387:
				player.getDH().sendNpcChat("Zulrah's wilderness as a God will soon be demonstrated.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 388:
				player.getDH().sendPlayerChat("I give up...", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 389: //Pet zilyana
				player.getDH().sendPlayerChat("FIND THE GODSWORD!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 390:
				player.getDH().sendNpcChat("FIND THE GODSWORD!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 391:
				player.getDH().sendPlayerChat("Is there an echo in here?", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;

			case 392: //Prince black dragon
				player.getDH().sendPlayerChat("Shouldn't a prince only have two heads?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 393:
				player.getDH().sendNpcChat("Why is that?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 394:
				player.getDH().sendPlayerChat("Well, a standard Black dragon has one, the King has", "three so inbetween must have two?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 395:
				player.getDH().sendNpcChat("You're overthinking this.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 396: //Scorpia's offspring
				player.getDH().sendPlayerChat("At night time, if I were to hold ultraviolet light over you,", "would you glow?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 397:
				player.getDH().sendNpcChat("Two things wrong there, human.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 398:
				player.getDH().sendNpcChat("One, When has it ever been night time here?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 399:
				player.getDH().sendNpcChat("Two, When have you ever seen ultraviolet light around here?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 400:
				player.getDH().sendPlayerChat("Hm...", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 401:
				player.getDH().sendNpcChat("In answer to your question though. Yes I, like every scorpion, would glow.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 402: //Tzrek-jad
				player.getDH().sendPlayerChat("Do you miss your people?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 403:
				player.getDH().sendNpcChat("Mej-TzTok-Jad Kot-Kl! (TzTok-Jad will protect us!)", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 404:
				player.getDH().sendPlayerChat("No.. I don't think so.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 405:
				player.getDH().sendNpcChat("Jal-Zek Kl? (Foreigner hurt us?)", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 406:
				player.getDH().sendPlayerChat("No, no, I wouldn't hurt you.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 407: //Venenatis spiderling
				player.getDH().sendPlayerChat("It's a damn good thing I don't have arachnophobia.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 408:
				player.getDH().sendNpcChat("We're misunderstood.", "Without us in your house, you'd be", "infested with flies and other REAL nasties.",
				                           FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 409:
				player.getDH().sendPlayerChat("Thanks for that enlightening fact.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 410:
				player.getDH().sendNpcChat("Everybody gets one.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;


			//skilling pets m8s
			case 417: //Baby chinchompa
				player.getDH().sendNpcChat("Squeak squeak!", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 418: //Beaver
				player.getDH().sendPlayerChat("How much wood could a woodchuck chuck if", "a woodchuck could chuck wood?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 419:
				player.getDH().sendNpcChat("Approximately 32,768 depending on his Woodcutting level.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 420: //Heron
				player.getDH().sendNpcChat("Hop inside my mouth if you want to live!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 421:
				player.getDH().sendPlayerChat("I'm not falling for that... I'm not a fish!", "I've got more foresight than that.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 422: //Rock golem
				player.getDH().sendPlayerChat("So you're made entirely of rocks?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 423:
				player.getDH().sendNpcChat("Not quite, my body is formed mostly of minerals.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 424:
				player.getDH().sendPlayerChat("Aren't minerals just rocks?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 425:
				player.getDH().sendNpcChat("No, rocks are rocks, minerals are minerals.", "I am formed from minerals.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 426:
				player.getDH().sendPlayerChat("But you're a Rock Golem...", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 427: //Giant squirrel
				player.getDH().sendPlayerChat("Did you ever notice how big squirrels' teeth are?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 428:
				player.getDH().sendNpcChat("No...", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 429:
				player.getDH().sendPlayerChat("You could land a gnome glider on those things!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 430:
				player.getDH().sendNpcChat("Watch it, I'll crush your nuts!", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 431: //Tangleroot
				player.getDH().sendPlayerChat("How are you doing today?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 432:
				player.getDH().sendNpcChat("I am Tangleroot!", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 433: //Rift guardian
				player.getDH().sendPlayerChat("Can you see your own rift?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 434:
				player.getDH().sendNpcChat("No. From time to time I feel it shift and", "change inside me though. It is an odd feeling.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 435: //Rocky
				player.getDH().sendPlayerChat("Is there much competition between", "you raccoons and the magpies?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 436:
				player.getDH().sendNpcChat("Magpies have nothing on us!", "They're just interested in shinies.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 437:
				player.getDH()
				      .sendNpcChat("Us raccoons have a finer taste, we can see the", "value in anything, whether it shines or not.", FacialAnimation.DEFAULT.getAnimationId());
				// This is the last line so we do not add player.nextDialogue
				break;



			case 438: // Olmlet
				player.getDH().sendNpcChat("Hee hee! What shall we talk about, human?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 439:
				player.getDH().sendPlayerChat("Where do creatures like you come from?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 440:
				player.getDH().sendNpcChat("From eggs, of course! You can't make an olmlet without", "breaking an egg.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 441:
				player.getDH().sendPlayerChat("That's... informative. Thank you.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 442:
				player.getDH().sendNpcChat("Hee hee! What's next, human?", FacialAnimation.DEFAULT.getAnimationId());
				break;

			// Cleaner.
			case 443:
				player.getDH().sendNpcChat("Give me the item you want to note.", DialogueHandler.FacialAnimation.CALM_2.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 444:
				player.getDH().sendPlayerChat("Sure thing!", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 448:
				player.getDH().sendOption("King's Throne", "Luxurious Throne");
				break;

			case 449:
				player.getDH().sendOption("Wooden Throne", "Simple Throne", "Queen's Throne", "King's Throne", "Devil's Throne");
				break;

			case 450:
				player.getDH().sendPlayerChat("Hey boy, what's up?", FacialAnimation.LAUGH_4.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;

			case 451:
				player.getDH().sendNpcChat("*Woof! Bark bark woof!* You smell funny.", DialogueHandler.FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;

			case 452:
				player.getDH().sendPlayerChat("Err... funny strange or funny ha ha?", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;

			case 453:
				player.getDH().sendNpcChat("*Bark bark woof!* You aren't funny.", DialogueHandler.FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 454:
				player.getDH().sendNpcChat("The Clue casket (1st release) has a rare chance", "to receive 3rd age wand, 3rd age bow & Ring of nature!",
				                           "Normal loot is clue scroll rewards, details at ::thread 575", DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
				break;

			case 455:
				String[] text =
						{"Patched up and ready!", "Have a good day.", "Stay safe!", "*wink*", "You are battle-ready!", "Becareful next time."};
				random = Misc.random(0, text.length - 1);
				player.getDH().sendNpcChat(text[random], DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
				break;

			case 456:
				player.getDH().sendOption("Edgeville", "Karamja", "Draynor", "Al-Kharid");
				break;

			case 457:
				player.getDH().sendOption("Teleport me to the wilderness!", "No.");
				break;

			case 458:
				player.getDH().sendNpcChat("The Clue casket (2nd release) has a rare chance", "to receive 3rd age pickaxe, 3rd age longsword &", "Ring of blood money!",
				                           "Normal loot is clue scroll rewards, details at ::thread 679", DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
				break;

			case 459:
				player.getDH().sendOption("Ask about Clue casket (1st release) loot", "Ask about Clue casket (2nd release) loot", "Ask about Clue casket (3rd release)");
				break;



			case 460: // Hellpuppy
				player.getDH().sendPlayerChat("What a cute puppy, how nice to meet you.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 461:
				player.getDH().sendNpcChat("It'd be nice to meat you too...", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 462:
				player.getDH().sendPlayerChat("Urk... nice doggy.", FacialAnimation.SAD.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 463:
				player.getDH().sendNpcChat("Grrr....", FacialAnimation.ANGER_1.getAnimationId());
				break;

			// Jad pet
			case 464:
				player.getDH().sendPlayerChat("Do you miss your people?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 465:
				player.getDH().sendNpcChat("Mej-TzTok-Jad Kot-Kl! (TzTok-Jad will protect us!)", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;

			case 466:
				player.getDH().sendPlayerChat("No.. I don't think so.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 467:
				player.getDH().sendNpcChat("Jal-Zek Kl? (Foreigner hurt us?)", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;


			case 468:
				player.getDH().sendPlayerChat("No, no, I wouldn't hurt you.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 470:
				player.getDH().sendOption("Yes send a ticket to an Admin to donate Osrs gold", "No do not send a ticket.");
				break;

			case 473:
				player.getDH().sendOption("Edit buy amount", "Edit buy price", "Cancel buy offer", "Close");
				break;

			case 474:
				player.getDH().sendOption("Edit sell amount", "Edit sell price", "Cancel sell offer", "Close");
				break;

			case 475:
				player.getDH().sendOption("Cancel offer", "Close");
				break;

			case 476:
				player.getDH().sendOption("Cancel offer", "Close");
				break;

			case 477:
				player.getDH().sendOption("Teleport to: " + player.teleToNamePermission, "Do not teleport.");
				break;

			// Vet'ion jr.
			case 478:
				player.getDH().sendPlayerChat("Who is the true lord and king of the lands?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 479:
				player.getDH().sendNpcChat("The mighty heir and lord of the Wilderness.)", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 480:
				player.getDH().sendPlayerChat("Where is he? Why hasn't he lifted your burden?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 481:
				player.getDH().sendNpcChat("I have not fulfilled my purpose.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 482:
				player.getDH().sendPlayerChat("What is your purpose?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 483:
				player.getDH()
				      .sendNpcChat("Not what is, what was. A great war tore this", "land apart and, for my failings in protecting this", " land, I carry the burden of its waste.",
				                   FacialAnimation.DEFAULT.getAnimationId());
				break;
			case 490:
				player.getDH().sendItemChat("Resetting KDR", "You will not lose your progress towards", "Infernal cape/Imbued god cape etc..",
				                            "This will cost " + Misc.formatRunescapeStyle(ServerConstants.getResetKdrCost()) + " " + ServerConstants.getMainCurrencyName()
				                                                                                                                                    .toLowerCase() + " and",
				                            "cannot be reverted.", 13316, 200, 20, -20);
				player.nextDialogue = dialogue + 1;
				break;

			case 491:
				sendOption("Yes, reset my KDR for " + Misc.formatRunescapeStyle(ServerConstants.getResetKdrCost()) + ".", "No, I'd rather not.");
				break;

			case 495:
				player.getDH().sendPlayerChat("Great, where can I find those?", FacialAnimation.CALM_1.getAnimationId()); //slayer expansion dialogues
				player.nextDialogue = dialogue + 1;
				break;

			case 496:
				Task a = Slayer.getTask(player.slayerTaskNpcType);
				if (a != null) {
					if (NpcDefinition.getDefinitions()[player.getNpcType()] == null || player.getNpcType() != 6797 && player.getNpcType() != 403) {
						player.getDH().sendStatement("Your task can be found in " + a.getLocation() + ".");
					} else {
						player.getDH().sendNpcChat("Your task can be found in " + a.getLocation() + ".", FacialAnimation.HAPPY.getAnimationId());
					}
				}
				break;

			case 500:
				if (player.getX() == 3056 && player.getY() == 9562) {
					player.getDH().sendItemChat("", "STOP! The creatures in this cave are @red@VERY @bla@dangerous.", "Are you sure you want to enter?", 6811, 200, 14, 0);
					player.nextDialogue = dialogue + 1;
				}
				break;

			case 501:
				sendOption("Yes, I'm not afraid of death!", "No thanks, I don't want to die!");
				break;

			case 550:
				sendOption("Al kharid", "Varrock west", "Lumbridge west");
				break;

			case 551:
				sendOption("Varrock anvil", "Falador furnace", "Edge furnace");
				break;
			case 552:
				sendOption("Draynor", "Fishing guild", "Entrana");
				break;
			case 553:
				sendOption("Al kharid", "Catherby", "Entrana");
				break;
			case 554:
				sendOption("Seer's village", "Woodcutting guild", "Tai bwo wannai");
				break;
			case 555:
				sendOption("Gnome course", "Barbarian course", "Wilderness course @red@(50 WILD)");
				break;
			case 556:
				sendOption("Edgeville stalls", "Ardougne", "Elves");
				break;
			case 557:
				sendOption("Implings", "Chinchompa", "Black chinchompa @red@(WILD)");
				break;

			//eco account creation dialogue
			case 560:
				sendNpcChat("Why hello there, " + player.getPlayerName() + ". Welcome to " + ServerConstants.getServerName() + "!",
				            "I haven't seen your name around these parts before.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 561;
				break;

			case 561:
				sendNpcChat("Would you like me to show you around?", "I recommend this if you're a new player.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 562;
				break;

			case 562:
				sendOption("Sure thing!", "No thanks, I know my way around " + ServerConstants.getServerName() + ".");
				break;

			case 563:
				sendNpcChat("This area contains some important features, such as", "altars, the Blood key chest, a Highscores statue and even",
				            "an object to claim a max cape from.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 564;
				player.getPA().movePlayer(3096, 3510, 0);
				break;

			case 564:
				sendNpcChat("Here you'll find basic shops.", "Along with many useful Npcs", "who provide all kinds of content!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 565;
				player.getPA().movePlayer(3083, 3510, 0);
				break;

			case 565:
				sendNpcChat("By using the Teleport NPC or any spell in a spellbook, ", "you can access all major teleports for " + ServerConstants.getServerName() + ".",
				            FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 567;
				player.getPA().movePlayer(3094, 3495, 0);
				break;

			case 567:
				sendNpcChat("Slaying bosses in safe areas and revenants in the wilderness", "is one of the best money making methods!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 569;
				player.getPA().movePlayer(2978, 3735, 0);
				break;

			case 569:
				sendNpcChat("Check out the settings tab for many different client options.", "Customise your client to how you like it!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 570;
				player.getPA().movePlayer(3094, 3496, 0);
				break;
			case 570:
				sendNpcChat("Enjoy your stay" + player.getPlayerName() + ".", "Invite your friends over for even more fun!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 571;
				break;
			case 571:
				NewPlayerContent.endTutorial(player);
				break;

			case 590:
				sendOption("Go up the stairs.", "Go down the stairs.");
				break;

			case 591: //dragon defender
				player.getDH().sendPlayerChat("I have a Rune defender here, please let me in!", FacialAnimation.CALM_1.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;

			case 592:
				sendNpcChat("Very well, I've unlocked the door but you'll still need", "100 tokens from upstairs before you can enter as well",
				            "as 10 for every minute you want to stay inside.", "Have fun in there!", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 593:
				sendNpcChat("Greetings!", "Are you looking to participate in " + ServerConstants.getServerName() + "'s lottery?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;

			case 594:
				sendPlayerChat("Durial... Haven't I seen you somewhere before?", FacialAnimation.ANNOYED.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;

			case 595:
				sendNpcChat("Wha.. ahem... you must be thinking of someone else.", "Do you need anything?", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;

			case 596:
				sendOption("How does the lottery work?", "Buy lottery tickets for " + Misc.formatNumber(Lottery.getLotteryTicketCost()) + " each", "What is the current pot at?",
				           "What is my percentage of winning?");
				break;
			case 597:
				sendNpcChat("The winner takes all the pot!", "I do however take a small " + Lottery.COMMISION_PERCENTAGE + "% commission to cover the",
				            "expenses of the bodyguards that sacrifice their life to protect", "the lottery against thieves.", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 596;
				break;
			case 598 :
				sendNpcChat("Buy as many tickets as you can so you have a greater %", "chance of winning!", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 597;
				break;

			case 600:
				sendNpcChat("Hello traveller, what brings you to these parts?", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 601;
				break;

			case 601:
				sendOption("Can you un-note my bones please?", "I'm on the hunt for some loot.");
				break;

			case 602:
				sendPlayerChat("Can you un-note my bones please?", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 603;
				break;

			case 603:
				sendNpcChat("I can indeed, just use a noted bone on me and", "I'll use my power to convert it back to its", "original item state.",
				            FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 604;
				break;

			case 604:
				sendPlayerChat("That's great, thanks.", FacialAnimation.HAPPY.getAnimationId());
				break;

			case 605:
				sendPlayerChat("I'm on the hunt for some loot.", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 606;
				break;

			case 606:
				sendNpcChat("Oh, you're that type of player I see...", FacialAnimation.ANGER_1.getAnimationId());
				player.nextDialogue = 607;
				break;

			case 607:
				sendNpcChat("We condone any violence in our parts.", "If you do feel the need to go on a rampage and", "start savaging other players, do it elsewhere please.",
				            FacialAnimation.ANGER_1.getAnimationId());
				player.nextDialogue = 608;
				break;

			case 608:
				sendPlayerChat("Make me.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 609;
				break;

			case 609:
				sendNpcChat("Do not challenge the power of the gods!", "They will strike you down!", FacialAnimation.ANGER_1.getAnimationId());
				player.nextDialogue = 610;
				break;

			case 610:
				sendPlayerChat("lol nice meme m8", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 611:
				sendOption("Mystery boxes (Partyhats, 3rd age, H'ween, Santas, Ancestral!)", "Pet Mystery boxes");
				break;

			case 615:
				sendNpcChat("'Ello chum, are ye wanting to decant yer potions?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = 616;
				break;

			case 616:
				sendOption("Yes, decant my potions please.", "Never mind.");
				break;

			case 617:
				sendNpcChat("There ye go, all done!", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = 618;
				break;

			case 618:
				sendPlayerChat("Thanks Bob.", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 620:
				sendOption("Equipment", "Skilling & Supplies", "General store", "Expensive store");
				break;

			case 621:
				sendOption("Melee shop", "Ranged shop", "Magic shop", "Misc. shop", "@dre@Back");
				break;

			case 622:
				sendOption("Supplies shop", "Skilling shop", "Hats & robe sets shop", "Cape shop", "@dre@Back");
				break;

			case 623:
				player.getDH().sendNpcChat("The Clue casket (3rd release) has a rare chance", "to receive Ankou pieces, 3rd age cloak & 3rd age axe!",
				                           "Normal loot is clue scroll rewards, details at ::thread 679", DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
				break;

			case 630:
				player.getDH().sendItemChat("", "You can make out some faded words on the ancient", "parchment. It appears to be an archiac invocation of the",
				                            "gods! Would you like to absorb its power?", 21079, 150, 10, 0);
				player.nextDialogue = dialogue + 1;
				break;

			case 631:
				sendOption("Learn Augury", "Cancel");
				break;

			case 633:
				player.getDH().sendItemChat("", "You can make out some faded words on the ancient", "parchment. It appears to be an archiac invocation of the",
				                            "gods! Would you like to absorb its power?", 21034, 150, 10, 0);
				player.nextDialogue = dialogue + 1;
				break;

			case 634:
				sendOption("Learn Rigour", "Cancel");
				break;

			case 635:
				player.getDH()
				      .sendItemChat("Smashing vials", "", "Do you want to toggle the ability to smash vials", "when you have drunk your last dose of potion?", "", 229, 200, 15, 0);
				player.nextDialogue = dialogue + 1;
				break;

			case 636:
				sendOption("Yes.", "No, I'd rather not.");
				break;

			case 637:
				player.getDH().sendItemChat("Loot key scroll", "", "Do you want to toggle the ability to receive your", "PvP loot in the form of a key?", "", 16145, 150, 10, 0);
				player.nextDialogue = dialogue + 1;
				break;

			case 638:
				sendOption("Yes", "No");
				break;

			case 640:
				sendOption("Bandit Camp", "Lava Maze", "Chaos Temple");
				break;

			// A'abla dialogue
			case 641:
				sendNpcChat("Would you like a 7 day ban from staking & dicing?", DialogueHandler.FacialAnimation.ANGER_3.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 642:
				sendOption("Ban me please, i have an uncontrollable addiction and it's haram.", "No, i can't do it");
				break;
			case 643:
				sendNpcChat("You have been banned from all gambling for 7 days!", "It is time for you to start a new leaf and venture into",
				            "other activites. Such as Pking, Pvming, Skilling & Chilling!", DialogueHandler.FacialAnimation.ANGER_3.getAnimationId());
				break;

			case 644:
				sendOption("Public room",
				           "Instanced room x" + Misc.formatNumber(KrakenCombat.getKrakenInstancedRoomCost()) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + "");
				break;
			case 645:
				player.getDH()
				      .sendItemChat("Dismantle blowpipe", "", "Are you sure you want to dismantle your blowpipe?", "You will receive 20,000 Zulrah's scales in return.", "", 12924,
				                    200, 15, 0);
				player.nextDialogue = dialogue + 1;
				break;

			case 646:
				sendDialogueQuestion("Dismantle?", "Yes.", "No, I'd rather not.");
				break;
			case 647:
				player.getDH().sendNpcChat("Meow?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 648:
				player.getDH().sendPlayerChat("Why am I trying to talk to a cat...", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 649:
				player.getDH().sendNpcChat("Good question.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 650:
				player.getDH().sendPlayerChat("What?!", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 651:
				player.getDH().sendNpcChat("Meow meowmeowmeow? Meow meow.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 652:
				player.getDH().sendPlayerChat("I must be hearing things... I should probably take a break!", FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 653:
				player.getDH().sendOption("Turn on anti-scam", "Continue with stake with anti-scam turned off");
				break;
			case 655:
				player.getDH().sendOption("Repair untradeables", "View list of breakable untradeables");
				break;

			case 656:
				player.getDH().sendOption("Ironman store", "Expensive shop", "Hats & robe sets shop", "Cape shop");
				break;

			// Scorpia's offspring dialogue
			case 657:
				player.getDH().sendPlayerChat("At night time, if I were to hold ultraviolet light over you,", "would you glow?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 658:
				player.getDH().sendNpcChat("Two things wrong there, human.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 659:
				player.getDH().sendPlayerChat("Oh?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 660:
				player.getDH().sendNpcChat("One, When has it ever been night time here?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 661:
				player.getDH().sendNpcChat("Two, When have you ever seen ultraviolet light", "around here?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 662:
				player.getDH().sendPlayerChat("Hm...", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 663:
				player.getDH().sendNpcChat("In answer to your question though. Yes I, like", "every scorpion, would glow.", FacialAnimation.DEFAULT.getAnimationId());
				break;


			// Change gameplay settings dialogue with Guide
			case 664:
				player.getDH().sendNpcChat("I can delete your account the next time you log off.", "So that you are able to log in as a brand new player and",
				                           "Choose the gameplay settings you want.", FacialAnimation.ANGER_1.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 665:
				player.getDH().sendNpcChat("Remember to transfer important wealth and items to a", "different account of yours for safe keeping.",
				                           "You can do so by opening another Client.", FacialAnimation.ANGER_1.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 666:
				player.getDH().sendOption("No! I do not want my account deleted.", "@red@Yes, please delete my account");
				break;
			case 667:
				player.getDH().sendOption("@red@Yes, delete my account, i am 100% sure", "No! I do not want to lose my account");
				break;

			case 668:
				player.getDH().sendOption("Afk Throne", "Custom Donator Pet store", "View Custom Donator pets thread");
				break;

			case 700:
				player.getDH().sendOption("Yes, dismantle it.", "No, keep the item as it is.");
				break;

			case 701:
				player.getDH().sendOption("Yes, combine them.", "No, keep the item as it is.");
				break;

			// Scorpia's offspring dialogue
			case 702:
				player.getDH().sendPlayerChat("Hey little snake!", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 703:
				player.getDH().sendNpcChat("Soon, Zulrah shall establish dominion over this plane.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 704:
				player.getDH().sendPlayerChat("Wanna play fetch?", FacialAnimation.HAPPY.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 705:
				player.getDH().sendNpcChat("Submit to the almighty Zulrah.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 706:
				player.getDH().sendPlayerChat("Walkies? Or slidies...?", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 707:
				player.getDH().sendNpcChat("Zulrah's wilderness as a God will soon be demonstrated.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;
			case 708:
				player.getDH().sendPlayerChat("I give up...", FacialAnimation.ANNOYED.getAnimationId());
				break;

			case 710:
				sendNpcChat("Welcome to the gambling zone, " + player.getPlayerName() + "!", "Please feel free to speak to me if you have any", "questions.",
				            FacialAnimation.DEFAULT.getAnimationId());
				break;

			case 715:
				player.getPA().sendMessage("You need at least 150 Wilderness kills to toggle the teleport destination to Edgeville.");
				break;

			case 716:
				player.getDH().sendItemChat("", "Seed pod destination toggle: @blu@True@bla@.", "It will now teleport you to Edgeville.", "", 19564, 200, 10, 0);
				player.toggleSeedPod = true;
				break;

			case 717:
				player.getDH().sendItemChat("", "Seed pod destination toggle: @blu@False@bla@.", "It will now teleport you to the Gnome Stronghold.", "", 19564, 200, 10, 0);
				player.toggleSeedPod = false;
				break;

			case 718:
				player.getDH().sendNpcChat("Darling, are you a robot by any chance?", "Either way, i will come to meet you and", "you will have to repeat what i say.", FacialAnimation.DEFAULT.getAnimationId());
				player.nextDialogue = dialogue + 1;
				break;

			case 719:
				player.getDH().sendOption("Okay", "I've been waiting for you");
				break;

			// In use in spellbook swap. Keep here.
			case 720:
				break;

			default:
				Plugin.execute("chat_" + dialogue, player);
				break;
		}

		player.lastDialogueAction = System.currentTimeMillis();
	}



	private Player player;

	public DialogueHandler(Player client)

	{
		this.player = client;
	}

	/**
	 * Reset dialogue variables after using a dialogue action.
	 *
	 * @param player
	 */
	public void dialogueActionReset() {
		if (System.currentTimeMillis() - player.lastDialogueAction > 10) {
			player.setDialogueAction(0);
		}
	}

	public void dialogueWalkingReset() {
		player.nextDialogue = 0;
		player.setDialogueAction(0);
	}

	/**
	 * Dialogue packet.
	 *
	 * @param player The associated player.
	 */
	public void dialoguePacketAction() {
		player.lastDialogueOptionString = "";
		if (player.nextDialogue > 0) {
			player.getDH().sendDialogues(player.nextDialogue);
		} else {
			player.setDialogueAction(0);
			if (player.getDialogueAction() > 0) {
				return;
			}
			player.getDH().sendDialogues(0);
		}

	}

	public void sendStartInfo(String title, String text, String text1, String text2, String text3) {
		player.getPA().sendFrame126(title, 6180);
		player.getPA().sendFrame126(text, 6181);
		player.getPA().sendFrame126(text1, 6182);
		player.getPA().sendFrame126(text2, 6183);
		player.getPA().sendFrame126(text3, 6184);
		player.getPA().sendFrame164(6179);
	}

	/*
	private void sendOption(String s, String s1)
	{
		player.getPA().sendFrame126("Select an Option", 2470);
		player.getPA().sendFrame126(s, 2471);
		player.getPA().sendFrame126(s1, 2472);
		player.getPA().sendFrame126("Click here to continue", 2473);
		player.getPA().sendFrame164(13758);
	}
	*/

	public void sendOption(String s, String s1) {
		if (player.hasDialogueOptionOpened && player.lastDialogueOptionString.equals(s)) {
			player.getPA().closeInterfaces(true);
			return;
		}
		player.lastDialogueOptionString = s;
		player.getPA().sendFrame126("Select an Option", 2460);
		player.getPA().sendFrame126(s, 2461);
		player.getPA().sendFrame126(s1, 2462);
		player.getPA().sendFrame171(1, 2468);
		player.getPA().sendFrame171(0, 2465);
		player.getPA().sendFrame164(2459);
		player.hasDialogueOptionOpened = true;
	}

	public void sendOption(String s, String s1, String s2) {
		if (player.getDialogueAction() != 262) {
			if (player.hasDialogueOptionOpened && player.lastDialogueOptionString.equals(s)) {
				player.getPA().closeInterfaces(true);
				return;
			}
		}
		player.lastDialogueOptionString = s;
		player.getPA().sendFrame126("Select an Option", 2470);
		player.getPA().sendFrame126(s, 2471);
		player.getPA().sendFrame126(s1, 2472);
		player.getPA().sendFrame126(s2, 2473);
		player.getPA().sendFrame171(1, 2479);
		player.getPA().sendFrame171(0, 2476);
		player.getPA().sendFrame164(2469);
		player.hasDialogueOptionOpened = true;
	}

	public void sendOption(String s, String s1, String s2, String s3) {
		if (player.hasDialogueOptionOpened && player.lastDialogueOptionString.equals(s)) {
			// Exception for trading post dialogue.
			if (player.getDialogueAction() == 473 || player.getDialogueAction() == 474) {
				InterfaceAssistant.closeDialogueOnly(player);
				return;
			}
			player.getPA().closeInterfaces(true);
			return;
		}
		player.lastDialogueOptionString = s;
		player.getPA().sendFrame126("Select an Option", 2481);
		player.getPA().sendFrame126(s, 2482);
		player.getPA().sendFrame126(s1, 2483);
		player.getPA().sendFrame126(s2, 2484);
		player.getPA().sendFrame126(s3, 2485);
		player.getPA().sendFrame171(1, 2489);
		player.getPA().sendFrame171(0, 2488);
		player.getPA().sendFrame164(2480);
		player.hasDialogueOptionOpened = true;
	}

	public void sendOption(String s, String s1, String s2, String s3, String s4) {
		if (player.hasDialogueOptionOpened && player.lastDialogueOptionString.equals(s)) {
			player.getPA().closeInterfaces(true);
			return;
		}

		player.lastDialogueOptionString = s;
		player.getPA().sendFrame126("Select an Option", 2493);
		player.getPA().sendFrame126(s, 2494);
		player.getPA().sendFrame126(s1, 2495);
		player.getPA().sendFrame126(s2, 2496);
		player.getPA().sendFrame126(s3, 2497);
		player.getPA().sendFrame126(s4, 2498);
		player.getPA().sendFrame171(1, 2502);
		player.getPA().sendFrame171(0, 2501);
		player.getPA().sendFrame164(2492);
		player.hasDialogueOptionOpened = true;
	}

	public void sendStatement(String s) { // 1 line click here to continue chat box interface
		player.getPA().sendFrame126(s, 357);
		player.getPA().sendFrame126("Click here to continue", 358);
		player.getPA().sendFrame164(356);
	}

	public void sendStatement(String s, String s1) { 
		player.getPA().sendFrame126(s, 360);
		player.getPA().sendFrame126(s1, 361);
		player.getPA().sendFrame126("Click here to continue", 362);
		player.getPA().sendFrame164(359);
	}

	public static void sendOption(Player c, String s, String s1) {
		c.getPA().sendFrame126("Select an Option", 2460);
		c.getPA().sendFrame126(s, 2461);
		c.getPA().sendFrame126(s1, 2462);
		c.getPA().sendFrame164(2459);
	}

	public static void sendOption(Player c, String s, String s1, String s2) {
		c.getPA().sendFrame126("Select an Option", 2470);
		c.getPA().sendFrame126(s, 2471);
		c.getPA().sendFrame126(s1, 2472);
		c.getPA().sendFrame126(s2, 2473);
		c.getPA().sendFrame164(2469);
	}

	public static void sendOption(Player c, String s, String s1, String s2, String s3) {
		c.getPA().sendFrame126("Select an Option", 2481);
		c.getPA().sendFrame126(s, 2482);
		c.getPA().sendFrame126(s1, 2483);
		c.getPA().sendFrame126(s2, 2484);
		c.getPA().sendFrame126(s3, 2485);
		c.getPA().sendFrame164(2480);
	}

	public static void sendOption(Player c, String s, String s1, String s2, String s3, String s4) {
		c.getPA().sendFrame126("Select an Option", 2493);
		c.getPA().sendFrame126(s, 2494);
		c.getPA().sendFrame126(s1, 2495);
		c.getPA().sendFrame126(s2, 2496);
		c.getPA().sendFrame126(s3, 2497);
		c.getPA().sendFrame126(s4, 2498);
		c.getPA().sendFrame164(2492);
	}

	public void sendDialogueQuestion(String title, String s, String s1) {
		if (player.hasDialogueOptionOpened && player.lastDialogueOptionString.equals(s)) {
			player.getPA().closeInterfaces(true);
			return;
		}
		player.lastDialogueOptionString = s;

		player.getPA().sendFrame171(title.length() >= 12 ? 1 : 0, 2465);
		player.getPA().sendFrame171(title.length() >= 12 ? 0 : 1, 2468);

		player.getPA().sendFrame126(title, 2460);
		player.getPA().sendFrame126(s, 2461);
		player.getPA().sendFrame126(s1, 2462);
		player.getPA().sendFrame164(2459);
		player.hasDialogueOptionOpened = true;
	}

	public void sendNpcChat(String s, int expression) {
		player.getPA().sendFrame200(4883, expression);
		player.getPA().sendFrame126(NpcDefinition.getDefinitions()[player.getNpcType()].name, 4884);
		player.getPA().sendFrame126(s, 4885);
		player.getPA().sendFrame75(player.getNpcType(), 4883);
		player.getPA().sendFrame164(4882);
	}

	public void sendNpcChat(String s1, String s2, int expression) {
		player.getPA().sendFrame200(4888, expression);
		player.getPA().sendFrame126(NpcDefinition.getDefinitions()[player.getNpcType()].name, 4889);
		player.getPA().sendFrame126(s1, 4890);
		player.getPA().sendFrame126(s2, 4891);
		player.getPA().sendFrame75(player.getNpcType(), 4888);
		player.getPA().sendFrame164(4887);
	}

	public void sendNpcChat(String s1, String s2, String s3, int expression) {
		InterfaceAssistant.changeInterfaceModel(player, 4894, -1, -1, -1, 40, 0, 0);
		player.getPA().sendFrame126(NpcDefinition.getDefinitions()[player.getNpcType()].name, 4895);
		player.getPA().sendFrame200(4894, expression);
		player.getPA().sendFrame126(s1, 4896);
		player.getPA().sendFrame126(s2, 4897);
		player.getPA().sendFrame126(s3, 4898);
		player.getPA().sendFrame75(player.getNpcType(), 4894);
		player.getPA().sendFrame164(4893);
	}

	public void sendNpcChat(String s1, String s2, String s3, String s4, int expression) {
		player.getPA().sendFrame200(4901, expression);
		player.getPA().sendFrame126(NpcDefinition.getDefinitions()[player.getNpcType()].name, 4902);
		player.getPA().sendFrame126(s1, 4903);
		player.getPA().sendFrame126(s2, 4904);
		player.getPA().sendFrame126(s3, 4905);
		player.getPA().sendFrame126(s4, 4906);
		player.getPA().sendFrame75(player.getNpcType(), 4901);
		player.getPA().sendFrame164(4900);
	}

	/**
	 * Player talking back
	 **/
	public void sendPlayerChat(String s, int expression) {
		player.getPA().sendFrame200(969,
		                            expression); // FacialAnimation.DEFAULT.getAnimationId() is the head animation, use 718 data http://www.rune-server.org/runescape-development/rs-503-client-server/configuration/536545-718-npc-dialogue-expressions.html
		player.getPA().sendFrame126(player.getPlayerName(), 970);
		player.getPA().sendFrame126(s, 971);
		player.getPA().sendFrame185(969);
		player.getPA().sendFrame164(968);
	}

	public void sendPlayerChat(String s, String s1, int expression) {
		player.getPA().sendFrame200(974, expression);
		player.getPA().sendFrame126(player.getPlayerName(), 975);
		player.getPA().sendFrame126(s, 976);
		player.getPA().sendFrame126(s1, 977);
		player.getPA().sendFrame185(974);
		player.getPA().sendFrame164(973);
	}

	public void sendPlayerChat(String s, String s1, String s2, int expression) {
		player.getPA().sendFrame200(980, expression);
		player.getPA().sendFrame126(player.getPlayerName(), 981);
		player.getPA().sendFrame126(s, 982);
		player.getPA().sendFrame126(s1, 983);
		player.getPA().sendFrame126(s2, 984);
		player.getPA().sendFrame185(980);
		player.getPA().sendFrame164(979);
	}

	public void sendItemChat(String header, String one, int item, int zoom, int offset1, int offset2) {
		player.getPA().sendMessage(":packet:senditemchat 307 " + offset1 + " " + offset2);
		player.getPA().sendFrame126(one, 308);
		player.getPA().sendFrame126(header, 4885);
		player.getPA().sendFrame246(307, zoom, item);
		player.getPA().sendFrame164(306);
	}

	public void sendItemChat(String header, String one, String two, int item, int zoom, int offset1, int offset2) {
		player.getPA().sendMessage(":packet:senditemchat 311 " + offset1 + " " + offset2);
		player.getPA().sendFrame126(two, 312);
		player.getPA().sendFrame126(one, 313);
		player.getPA().sendFrame126(header, 4885);
		player.getPA().sendFrame246(311, zoom, item);
		player.getPA().sendFrame164(310);
	}

	public void sendItemChat(String header, String one, String two, String three, int item, int zoom, int offset1, int offset2) {
		player.getPA().sendMessage(":packet:senditemchat 4894 " + offset1 + " " + offset2);
		player.getPA().sendFrame246(4894, zoom, item);
		player.getPA().sendFrame126(header, 4895);
		player.getPA().sendFrame126(one, 4896);
		player.getPA().sendFrame126(two, 4897);
		player.getPA().sendFrame126(three, 4898);
		player.getPA().sendFrame164(4893);
	}

	public void sendItemChat(String header, String one, String two, String three, String four, int item, int zoom, int offset1, int offset2) {
		player.getPA().sendMessage(":packet:senditemchat 4901 " + offset1 + " " + offset2);
		player.getPA().sendFrame246(4901, zoom, item);
		player.getPA().sendFrame126(header, 4902);
		player.getPA().sendFrame126(one, 4903);
		player.getPA().sendFrame126(two, 4904);
		player.getPA().sendFrame126(three, 4905);
		player.getPA().sendFrame126(four, 4906);
		player.getPA().sendFrame164(4900);
	}

	public enum FacialAnimation {


		/**
		 * Dialogue animations.
		 */
		HAPPY(588, 9847),
		// - Joyful/happy
		CALM_1(589, 9772),
		// - Speakingly calmly
		CALM_2(590, 9808),
		// - Calm talk
		DEFAULT(591, 9760),
		// - Default speech
		EVIL(592, 9836),
		// - Evil
		EVIL_CONTINUED(593, 9784),
		// - Evil continued
		DELIGHTED_EVIL(594, 9792),
		// - Delighted evil
		ANNOYED(595, 9824),
		// - Annoyed
		DISTRESSED(596, 9776),
		// - Distressed
		DISTRESSED_CONTINUED(597, 9780),
		// - Distressed continued
		ALMOST_CRYING(598, 9832),
		// - Almost crying
		BOWS_HEAD_WHILE_SAD(599, 9792),
		// - Bows head while sad
		DRUNK_TO_LEFT(600, 9812),
		// - Talks and looks sleepy/drunk
		DRUNK_TO_RIGHT(601, 9808),
		// - Talks and looks sleepy/drunk
		DISINTERESTED(602, 9804),
		// - Sleepy or disinterested
		SLEEPY(603, 9816),
		// - Tipping head as if sleepy.
		PLAIN_EVIL(604, 9800),
		// - Grits teeth and moves eyebrows
		LAUGH_1(605, 9840),
		// - Laughing or yawning
		LAUGH_2(606, 9851),
		// - Laughing or yawning for longer
		LAUGH_3(607, 9855),
		// - Laughing or yawning for longer
		LAUGH_4(608, 9859),
		// - Laughing or yawning
		EVIL_LAUGH(609, 9792),
		// - Evil laugh then plain evil
		SAD(610, 9764),
		// - Slightly sad
		MORE_SAD(611, 9768),
		// - Quite sad
		ON_ONE_HAND(612, 9828),
		// - On one hand...
		NEARLY_CRYING(613, 9820),
		// - Close to crying
		ANGER_1(614, 9784),
		// - Angry
		ANGER_2(615, 9784),
		// - Angry
		ANGER_3(616, 9792),
		// - Angry
		ANGER_4(617, 9796); // - Angry

		private int animation;

		private int preEocAnimation;

		private FacialAnimation(int animation, int preEocAnimation) {
			this.animation = animation;
			this.preEocAnimation = preEocAnimation;
		}

		/**
		 * @return the animation
		 */
		public int getAnimationId() {
			return GameType.isPreEoc() ? preEocAnimation : animation;
		}

	}

	/**
	 * Used by Python plugins only.
	 */
	public void sendOptionDialogue(String option1, String option2, int dialogueId) {
		player.nextDialogue = 0;
		player.setDialogueAction(dialogueId);
		sendOption(option1, option2);
		player.lastDialogueAction = System.currentTimeMillis();
	}

	/**
	 * Used by Python plugins only.
	 */
	public void sendOptionDialogue(String option1, String option2, String option3, int dialogueId) {
		player.nextDialogue = 0;
		player.setDialogueAction(dialogueId);
		sendOption(option1, option2, option3);
		player.lastDialogueAction = System.currentTimeMillis();
	}

	/**
	 * Used by Python plugins only.
	 */
	public void sendOptionDialogue(String option1, String option2, String option3, String option4, int dialogueId) {
		player.nextDialogue = 0;
		player.setDialogueAction(dialogueId);
		sendOption(option1, option2, option3, option4);
		player.lastDialogueAction = System.currentTimeMillis();
	}

	/**
	 * Used by Python plugins only.
	 */
	public void sendOptionDialogue(String option1, String option2, String option3, String option4, String option5, int dialogueId) {
		player.nextDialogue = 0;
		player.setDialogueAction(dialogueId);
		sendOption(option1, option2, option3, option4, option5);
		player.lastDialogueAction = System.currentTimeMillis();
	}
}
