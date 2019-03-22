package game.bot;

import game.content.combat.Combat;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Bot to player and player to bot communication.
 *
 * @author MGT Madness, created on 21-04-2016.
 */
public class BotCommunication {

	//@formatter:off
	private final static String[] GOOD_LUCK = {"Good luck", "Gl", "Greetings.", "Gl bro", "Gl m8", "Gl no safe", "Gl bro", "Lets go"};

	private final static String[] GG_KILL = {"Gf", "Gg", "Gg wp", "Lol", "Xd", "L0l", "Gf!", "Good game", "Gf lol", "Gg lol", "Lel", "Rm?"};

	private final static String[] SAFE = {"You safe too much", "Stop safe", "Safer", "Nty safer", "Safer nty", "Stop safing noob!", "Safe 1 more time and i run", "Saaaafe",
			"Safe.."
	};

	private final static String[] QUESTION_MARK = {"?", "??", "Why", "Why?", "What are you doing??", "Stop!", "Nty", "Off", "Get off", "Cya", "Bb", "Bye", "Cu", "Sayonara", "Baka"
	};

	private final static String[] GG_DEATH = {"Gf", "Gg", "Gg wp", "Lol", "Xd", "L0l", "Rip", "Nice", "Nice 1", "Rly", "Fk", "Xd xp", "Haha", "Oh", "Loool", "L000l gg", "Lol gg",
			"Lel", "Dafuq", "****"
	};

	private final static String[] OK = {"Ok", "Ye", "Yh", "Ya", "Yes", "K", "Kk", "Sure", "Gl bro", "Gl!", "Ok bro"};

	private final static String[] SKULL = {"Skull first", "Skull?", "Ok but skull", "Skull", "K skull", "Nah, skull first", "Skull pl0x", "kk skull plz"};

	private final static String[] BOT = {"Yes.", "I sure am", "Yes, please don't abuse me", "Wag1", "1v1 me", "Ye, fight me?", "Mgt Madness created me",
			"Yes bro, can we fight tho?", "Bots are more intellectual than humans", "Bots are better pkers than humans."
	};

	private final static String[] DEFENCE_NOOB = {"Defence noob", "Tank noob!", "Can't hit gg", "Nice tank", "Def nub", "Defence nub", "Tank nub", "Def nub gg", "Def noob cya",
			"Def noob bye", "Nty def noob"
	};

	private final static String[] TANKED = {"You're all fucking retards", "Lo000000000o0ol so bad", "L0000000000000000000000000000000000l uninstall plz",
			"So baaaaaaaaaad rofl easy", "Easy as fuck", "Get the fuck outta here", "Looool l2pk", "Shit brids lmfao"
	};

	private final static String[] TANK_DEAD = {"Shitbrids", "Noo", "Omg", "Omfg i quit", "Omfg", ":("};

	private final static String[] LURED = {"L0000000000000000000l", "L000l", "Trade for food", "@@@@@@@@@@@@@@", "Thx 4 bank", "Fucking retard", "L00000000l gl tanking",
			"L0000000l easy", "You're fucked l000000000000l", "Gl tanking 5min", "Lured retard"
	};

	private final static String[] BM_KILL = {"#Unique", "#Lemon", "#wolves", "#Brap", "Thx 4 max", "L000000000l #unique", "L000l cya retard #lemon", "Free max #brap",
			"Frontline sucks #Unique", "#Unique bye", "bb #lemon", "Ty", "Ty l000000l #wolves", "k9k9k9k999 #brap"
	};

	private final static String[] BM_TANK = {"Shitbrids", "Nice try", "Retardssss", "Frontline is coming so gtfo", "Give up retards", "I'll get fucking frontline on your ass",
			"Kk im calling frontline", "Baited frontline is here", "Gtfo wannabe brids", "Lol you're so bad #frontline", "Easy tank #frontline", "#Frontline",
			"Shit brids easy 5min", "L00l l2brid", "Pm frontline for lessons"
	};
	//@formatter:on

	public static void sendBotMessage(final Player bot, final String textType, boolean longDelay) {
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				String text = "";
				switch (textType) {
					case "TANKED":
						text = TANKED[Misc.random(TANKED.length - 1)];
						break;
					case "BM KILL":
						text = BM_KILL[Misc.random(BM_KILL.length - 1)];
						break;
					case "LURED":
						text = LURED[Misc.random(LURED.length - 1)];
						break;
					case "TANK DEAD":
						text = TANK_DEAD[Misc.random(TANK_DEAD.length - 1)];
						break;
					case "BM TANK":
						text = BM_TANK[Misc.random(BM_TANK.length - 1)];
						break;
					case "GOOD LUCK":
						text = GOOD_LUCK[Misc.random(GOOD_LUCK.length - 1)];
						break;
					case "GG KILL":
						text = GG_KILL[Misc.random(GG_KILL.length - 1)];
						break;
					case "SAFE":
						text = SAFE[Misc.random(SAFE.length - 1)];
						break;
					case "?":
						text = QUESTION_MARK[Misc.random(QUESTION_MARK.length - 1)];
						break;
					case "DEFENCE NOOB":
						text = DEFENCE_NOOB[Misc.random(DEFENCE_NOOB.length - 1)];
						break;
					case "GG DEATH":
						text = GG_DEATH[Misc.random(GG_DEATH.length - 1)];
						break;
					case "OK":
						text = OK[Misc.random(OK.length - 1)];
						break;
					case "SKULL":
						text = SKULL[Misc.random(SKULL.length - 1)];
						break;
					case "BOT":
						text = BOT[Misc.random(BOT.length - 1)];
						break;
				}
				if (text.isEmpty()) {
					text = textType;
				}
				//DiscordBot.announce(bot.getPlayerName() + ": " + text);
				bot.forcedChat(text, false, false);
			}
		}, longDelay ? Misc.random(4, 9) : Misc.random(2, 4));


	}

	public static void playerToBot(final Player player, String text) {
		if (player.getPlayerIdToFollow() <= 0) {
			return;
		}
		final Player bot = PlayerHandler.players[player.getPlayerIdToFollow()];
		if (bot == null) {
			return;
		}
		if (!bot.isCombatBot()) {
			return;
		}
		if (text.contains("fight") || text.contains("fite") || text.contains("gl") || text.contains("good") && text.contains("luck")) {
			if (bot.getBotStatus().equals("LOOTING")) {
				return;
			}
			if (!player.getWhiteSkull()) {
				BotCommunication.sendBotMessage(bot, "SKULL", false);
				return;
			}
			if (Combat.inCombat(bot)) {
				return;
			}
			int combatDif1 = Combat.getCombatDifference(player.getCombatLevel(), bot.getCombatLevel());
			if (combatDif1 <= player.getWildernessLevel() || combatDif1 <= bot.getWildernessLevel()) {
				BotCommunication.sendBotMessage(bot, "OK", false);
				CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent<Object>() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						bot.setPlayerIdAttacking(player.getPlayerId());
					}
				}, Misc.random(6, 9));
			}
		} else if (text.contains("bot")) {
			BotCommunication.sendBotMessage(bot, "BOT", false);
		}
	}

}
