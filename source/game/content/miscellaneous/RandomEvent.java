package game.content.miscellaneous;

import java.util.ArrayList;
import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.commands.AdministratorCommand;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.punishment.Blacklist;
import utility.Misc;

/**
 * Random events.
 * 
 * @author MGT Madness, created on 09-05-2018.
 */
public class RandomEvent {

	// Make the npc perform an emote and the player has to copy.
	private static String[] art = {"the", "my", "your", "our", "that", "this", "every", "one", "the only", "his", "her"};
	public static String[] adj = {"glorious", "hairy", "happy", "rotating", "red", "fast", "elastic", "smily", "unbelievable", "infinte", "surprising", "mysterious", "glowing", "green", "blue", "tired", "hard", "soft", "transparent", "long", "short", "excellent", "noisy", "silent", "rare", "normal", "typical", "living", "clean", "glamorous", "fancy", "handsome", "lazy", "scary", "helpless", "skinny", "melodic", "silly", "kind", "brave", "nice", "ancient", "modern", "young", "sweet", "wet", "cold", "dry", "heavy", "industrial", "complex", "accurate", "awesome", "shiny", "cool", "glittering", "fake", "unreal", "naked", "intelligent", "smart", "curious", "strange", "unique", "empty", "gray", "saturated", "blurry"};
	private static String[] nou = {"bush", "computer program", "grandma", "school", "bed", "mouse", "keyboard", "bicycle", "spaghetti", "drink", "cat", "t-shirt", "carpet", "wall", "poster", "airport", "bridge", "road", "river", "beach", "sculpture", "piano", "guitar", "fruit", "banana", "apple", "strawberry", "rubber band", "saxophone", "window", "linux computer", "skate board", "piece of paper", "photograph", "painting", "hat", "space", "fork", "mission", "goal", "project", "tax", "wind mill", "light bulb", "microphone", "cpu", "hard drive", "screwdriver"};
	private static String[] pre = {"under", "in front of", "above", "behind", "near", "following", "inside", "besides", "unlike", "like", "beneath", "against", "into", "beyond", "considering", "without", "with", "towards"};
	private static String[] ver = {"sings", "dances", "was dancing", "runs", "will run", "walks", "flies", "moves", "moved", "will move", "glows", "glowed", "spins", "promised", "hugs", "cheated", "waits", "is waiting", "is studying", "swims", "travels", "traveled", "plays", "played", "enjoys", "will enjoy", "illuminates", "arises", "eats", "drinks", "calculates", "kissed", "faded", "listens", "navigated", "responds", "smiles", "will smile", "will succeed", "is wondering", "is thinking", "is", "was", "will be", "might be", "was never"};


	private static String getRandomWord(String[] words) {
		return words[Misc.random(words.length - 1)];
	}

	public static void main (String[] args)
	{
		Misc.print(getRandomWord(art));
		Misc.print(getRandomWord(adj));
		Misc.print(getRandomWord(nou));

		/*
		Misc.print(getRandomWord(ver));
		Misc.print(getRandomWord(pre));
		
		Misc.print(getRandomWord(art));
		Misc.print(getRandomWord(adj));
		Misc.print(getRandomWord(nou));
		*/
	}

	public static String getRandomEventSentence() {
		return getRandomWord(art) + " " + getRandomWord(adj) + " " + getRandomWord(nou);
	}

	private final static int[] RANDOM_EVENT_NPCS = {
			// @formatter:off
			3015, // Woman
			3083, // Woman
			3084, // Woman
			3085, // Woman
			1219, // Lady Servil
			5510, // Sandwich lady
			// @formatter:on
	};

	private static void openRandomEventNpcInterface(Player player) {
		player.randomEventNpcType = RANDOM_EVENT_NPCS[Misc.random(RANDOM_EVENT_NPCS.length - 1)];
		player.setRandomEvent("NPC_TALK_DIALOGUE");
		showRandomEventNpcDialogue(player);
	}

	public static boolean showRandomEventNpcDialogue(Player player) {
		if (player.isInRandomEventType("NPC_TALK_DIALOGUE")) {
			player.setNpcType(player.randomEventNpcType);
			player.getDH().sendDialogues(718);
			return true;
		}
		return false;
	}


	public static void randomEventNpcDialogueOptionClicked(Player player) {
		player.setRandomEvent("NPC_TALK_SPAWNED");
		summonRandomNpc(player);
		player.getPA().closeInterfaces(true);
	}

	private final static String[] FAREWELLS = {"See you next time darling.", "Bye honey!", "*blows kiss*", "Take care darling.", "*wink*", "Bai"};


	public static void summonRandomNpc(Player player) {
		int[] teleportCoords = new int[2];
		teleportCoords = NpcHandler.teleportPlayerNextToNpc(0, 0, false, player.getX(), player.getY(), player.getHeight(), 1, 1);
		Npc npc = NpcHandler.spawnNpc(null, player.randomEventNpcType, teleportCoords[0], teleportCoords[1], player.getHeight(), false, false);
		npc.setAttackable(false);
		player.randomEventNpcTextToRepeat = getRandomEventSentence();
		String npcText = player.getPlayerName() + ", please repeat this \"" + player.randomEventNpcTextToRepeat + "\"";
		int fareWellIndex = Misc.random(FAREWELLS.length - 1);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (container.getExecutions() == 3 || container.getExecutions() == 6 || container.getExecutions() == 9) {
					npc.forceChat(npcText);
				}
				else if (container.getExecutions() == 13) {
					npc.setNeverRandomWalks(true);
					NpcHandler.facePlayer(player, npc);
					npc.forceChat(FAREWELLS[fareWellIndex]);
				}
				else if (container.getExecutions() == 14) {
					// Blow kiss
					if (fareWellIndex == 2) {
						npc.requestAnimation(0x558);
						npc.gfx0(574);
					}
					// Wave
					else {
						npc.requestAnimation(863);
					}
				} else if (container.getExecutions() == 18) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				npc.deleteNpc = true;
			}
		}, 1);
	}

	public static void randomEvent(Player player) {
		if (player.isInRandomEvent()) {
			return;
		}
		RandomEvent.randomEventLog.add(Misc.getDateAndTimeLog() + player.getPlayerName() + " given event.");
		if (!AdministratorCommand.enableNewRandomEventNpc) {
			int skill = Misc.random(20);
			player.randomEventSkillIndex = skill;
			player.setRandomEvent("SELECT_SKILL");
			summonRandomEventClickSkillInterface(player);
		}
		else {
			openRandomEventNpcInterface(player);
		}
		Skilling.stopAllSkilling(player);
	
		RandomEvent.pendingRandomEvent.add("ip#&#" + System.currentTimeMillis() + "#&#" + player.addressIp);
		if (Blacklist.useAbleData(player.addressUid)) {
			RandomEvent.pendingRandomEvent.add("uid#&#" + System.currentTimeMillis() + "#&#" + player.addressUid);
		}
	}
	public static void randomEventAnswer(Player player, int button, int index) {
		int selectedAnswer = XpLamp.skillOrder[index];
	
		// Correct answer
		if (selectedAnswer == player.randomEventSkillIndex) {
			player.getPA().closeInterfaces(true);
			randomEventCompletedReward(player);
		}
	
		// Incorrect answer
		else {
			incorrectAnswerGiven(player, "SKILL");
		}

	}

	private static void incorrectAnswerGiven(Player player, String type) {
		player.randomEventIncorrectTries++;
		RandomEvent.randomEventLog.add(Misc.getDateAndTimeLog() + player.getPlayerName() + " has chosen incorrect answer, " + player.randomEventIncorrectTries);
		String string = "once";
		if (player.randomEventIncorrectTries == 2) {
			string = "twice";
		} else if (player.randomEventIncorrectTries == 3) {
			string = "You have been ipbanned from skilling for 12 hours.";
			if (type.equals("SKILL")) {
				player.getPA().sendFrame126(string, 2810);
			}
			else {
				player.getPA().sendMessage(ServerConstants.RED_COL + string);
			}
			XpLamp.skillingBans.add("ip#&#" + System.currentTimeMillis() + "#&#" + player.addressIp);
			if (Blacklist.useAbleData(player.addressUid)) {
				XpLamp.skillingBans.add("uid#&#" + System.currentTimeMillis() + "#&#" + player.addressUid);
			}
			player.setRandomEvent("");
			player.randomEventIncorrectTries = 0;
			RandomEvent.deletePendingRandomEvent(player);
			return;
		}
		if (type.equals("SKILL")) {
			player.getPA().sendFrame126("Choose the " + ServerConstants.SKILL_NAME[player.randomEventSkillIndex] + " icon: Wrong " + string, 2810);
		}
		else {
			player.getPA().sendMessage(ServerConstants.RED_COL + "Wrong " + string);
		}
	}

	public static void randomEventCompletedReward(Player player) {
		player.randomEventIncorrectTries = 0;
		RandomEvent.randomEventLog.add(Misc.getDateAndTimeLog() + player.getPlayerName() + " has solved answer correctly.");
		player.setRandomEvent("");
		int amount = GameType.isOsrsEco() ? Misc.random(750000, 1500000) : Misc.random(1000, 2000);
		if (!ItemAssistant.addItem(player, ServerConstants.getMainCurrencyId(), amount)) {
			Bank.addItemToBank(player, ServerConstants.getMainCurrencyId(), amount, false);
			player.getPA().sendMessage(Misc.formatNumber(amount) + " " + ServerConstants.getMainCurrencyName() + " have been added to your bank.");
		} else {
			player.getPA().sendMessage("You have been awarded with " + Misc.formatRunescapeStyle(amount) + " " + ServerConstants.getMainCurrencyName() + " for completing the anti-bot test.");
		}
		RandomEvent.deletePendingRandomEvent(player);
	}

	public static void summonRandomEventClickSkillInterface(Player player) {
		player.getPA().sendFrame126("Choose the " + ServerConstants.SKILL_NAME[player.randomEventSkillIndex] + " icon", 2810);
		player.getPA().displayInterface(2808);
	}

	public static void randomEvent(Player player, double chance) {
		if (!Misc.hasOneOutOf(chance)) {
			return;
		}
		if (System.currentTimeMillis() - player.lastRandomEvent <= Misc.getMinutesToMilliseconds(30)) {
			return;
		}
		player.lastRandomEvent = System.currentTimeMillis();
		RandomEvent.randomEvent(player);
	}

	public static boolean isBannedFromSkilling(Player player) {
		for (int index = 0; index < XpLamp.skillingBans.size(); index++) {
			String parse[] = XpLamp.skillingBans.get(index).split("#&#");
			if (parse[0].equals("ip")) {
				if (player.addressIp.equals(parse[2]) && System.currentTimeMillis() - Long.parseLong(parse[1]) < 43200000) {
					Skilling.bannedMessage(player, Long.parseLong(parse[1]));
					return true;
				}
			}
			if (parse[0].equals("uid")) {
				if (Misc.uidMatches(player.addressUid, parse[2]) && System.currentTimeMillis() - Long.parseLong(parse[1]) < 43200000) {
					Skilling.bannedMessage(player, Long.parseLong(parse[1]));
					return true;
				}
			}
		}
		if (player.isInRandomEvent()) {
			return true;
		}
		return false;
	}

	public static void deletePendingRandomEvent(Player player) {
		for (int a = 0; a < RandomEvent.pendingRandomEvent.size(); a++) {
			if (RandomEvent.pendingRandomEvent.get(a).equals(player.addressIp) || Misc.uidMatches(player.addressUid, RandomEvent.pendingRandomEvent.get(a))) {
				RandomEvent.pendingRandomEvent.remove(a);
				a--;
			}
		}
	}

	public static ArrayList<String> pendingRandomEvent = new ArrayList<String>();

	public static void randomEventLogInUpdate(Player player) {
		for (int a = 0; a < pendingRandomEvent.size(); a++) {
			if (pendingRandomEvent.get(a).equals(player.addressIp)) {
				randomEvent(player);
			}
			if (Misc.uidMatches(player.addressUid, pendingRandomEvent.get(a))) {
				randomEvent(player);
			}
		}
	}

	public static ArrayList<String> randomEventLog = new ArrayList<String>();


	public static void isNpcRandomEventChatPacketSent(Player player, String text) {
		if (player.isInRandomEventType("NPC_TALK_SPAWNED")) {
			if (text.equals(player.randomEventNpcTextToRepeat)) {
				RandomEvent.randomEventCompletedReward(player);
			}
			else
			{
				incorrectAnswerGiven(player, "NPC_TALK");
			}
		}
	}

}
