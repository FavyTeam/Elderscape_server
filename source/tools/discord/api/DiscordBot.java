package tools.discord.api;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import tools.discord.DiscordConstants;
import tools.discord.content.DiscordCommands;
import tools.discord.content.DiscordMessageSent;
import utility.Misc;

public class DiscordBot {

	public static IDiscordClient client;

	public DiscordBot(IDiscordClient client) {
		this.client = client; // Sets the client instance to the one provided
	}


	private DiscordSettings settings;

	private CommandListener commandListener;

	public static String BOT_TYPE = "";

	public static DiscordBot bot = null;

	public static void startDiscordBot(String type) {
		BOT_TYPE = type;
		if (type.equals("DAWNTAINED_BOT") && ServerConfiguration.DEBUG_MODE) {
			return;
		}
		bot = createClient(getBotKey(), true);
		client.getDispatcher().registerListener(new MyEvents());
		timer.schedule(myTask, 0, 100);
		DiscordMessageSent.loadLastBugMessageId();
	}

	public static DiscordBot createClient(String token, boolean login) { // Returns a new instance of the Discord client
		ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
		clientBuilder.withToken(token); // Adds the login info to the builder
		DiscordBot bot = null;
		try {
			if (login) {
				IDiscordClient client = clientBuilder.login();
				bot = new DiscordBot(client);
			} else {
				IDiscordClient client = clientBuilder.login();
				bot = new DiscordBot(
						client); // Creates the client instance and logs the client in// Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
			}
		} catch (DiscordException e) { // This is thrown if there was a problem building the client
			e.printStackTrace();
			return bot;
		}
		return bot;
	}

	/**
	 * Store the time the name change was failed. The discord api limits it to 1 name change per minute or something.
	 */
	public static long timeNameChanged;

	/**
	 * DAWNTAINED_BOT = the bot ran on the official Dawntained discord.
	 * <p>
	 * SERVER_BOT = the bot connected directly to the server, used for staff commands.
	 * <p>
	 * EXTERNAL_BOT = the bot that is connected to the Dawntained multi tool.
	 */
	public static boolean isDiscordBot(String type) {
		return BOT_TYPE.equals(type);
	}


	/**
	 * Get the bot key depending on bot type.
	 */
	private static String getBotKey() {
		switch (BOT_TYPE) {
			case "SERVER_BOT":
				return DiscordConstants.STAFF_BOT_SERVER_KEY;
			case "DAWNTAINED_BOT":
				return DiscordConstants.DAWNTAINED_BOT_KEY;
			case "EXTERNAL_BOT":
				return DiscordConstants.STAFF_BOT_EXTERNAL_KEY;
			case "ANNOUNCEMENT_BOT":
				return DiscordConstants.ANNOUNCEMENT_BOT_KEY;
		}
		return "";
	}

	public static void announce(String announce, boolean announceOnServer) {
		sendMessage("219411654264225792", announce, false);
		if (announceOnServer) {
			Announcement.announce(announce, ServerConstants.DARK_BLUE);
		}

	}

	public static void changeBotNameCore(String string) {
		try {
			DiscordBot.client.changeUsername(string);
		} catch (DiscordException | RateLimitException e) {
			e.printStackTrace();
		}

	}

	public static void sendMessage(String channelId, String message, boolean queued) {
		if (queued) {
			DiscordCommands.queuedBotString = "";
		}
		String splitMessage = "";
		if (message.length() > 2000) {
			splitMessage = message.substring(2000);
			message = message.substring(0, 2000);
		}
		messageQueue.add(channelId + ServerConstants.TEXT_SEPERATOR + message);
		if (!splitMessage.isEmpty()) {
			sendMessage(channelId, splitMessage, queued);
		}
	}

	public static void sendMessageDate(String channelId, String message) {
		String splitMessage = "";
		if (message.length() > 2000) {
			splitMessage = message.substring(2000);
			message = message.substring(0, 2000);
		}
		messageQueue.add(channelId + ServerConstants.TEXT_SEPERATOR + "[" + Misc.getDateAndTime() + "] " + message);
		if (!splitMessage.isEmpty()) {
			sendMessageDate(channelId, splitMessage);
		}
	}

	private static void sendMessageCore(String channelId, String message) {
		try {
			debug.add("[" + Misc.getDateAndTime() + "] Here3: " + channelId + ", " + message);
			client.getChannelByID(Long.parseLong(channelId)).sendMessage(message);
			debug.add("[" + Misc.getDateAndTime() + "] Here4: " + channelId + ", " + message);
			timeMessageSent = System.currentTimeMillis();
			messageQueue.remove(0);
		} catch (MissingPermissionsException e) {
			debug.add("[" + Misc.getDateAndTime() + "] Here5: " + channelId + ", " + message);
			e.printStackTrace();
		} catch (RateLimitException e) {
			debug.add("[" + Misc.getDateAndTime() + "] Here6: " + channelId + ", " + message);
			e.printStackTrace();
		} catch (DiscordException e) {
			debug.add("[" + Misc.getDateAndTime() + "] Here7: " + channelId + ", " + message);
			e.printStackTrace();
		}
		debug.add("[" + Misc.getDateAndTime() + "] Here8: " + channelId + ", " + message);
	}


	public static void deleteMessage(IMessage message) {
		try {
			message.delete();
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> debug = new ArrayList<String>();


	/**
	 * Store messages in here, 1 is sent every second maximum.
	 */
	public static ArrayList<String> messageQueue = new ArrayList<String>();

	public static long timeMessageSent;

	public static Timer timer = new Timer();

	public static TimerTask myTask = new TimerTask() {
		@Override
		public void run() {
			if (System.currentTimeMillis() - timeMessageSent <= DiscordConstants.MESSAGE_DELAY) {
				return;
			}

			debug.add("[" + Misc.getDateAndTime() + "] Here1: " + messageQueue.size());
			for (int index = 0; index < messageQueue.size(); index++) {
				String[] parse = messageQueue.get(index).split(ServerConstants.TEXT_SEPERATOR);
				if (client.getChannelByID(Long.parseLong(parse[0])) == null) {
					continue;
				}
				debug.add("[" + Misc.getDateAndTime() + "] Here2: " + parse[0] + ", " + parse[1]);
				sendMessageCore(parse[0], parse[1]);
				break;
			}
		}
	};

	public IDiscordClient getClient() {
		return client;
	}

	public CommandListener getCommandListener() {
		return commandListener;
	}

	public void registerCommandListener(CommandListener listener) {
		this.commandListener = listener;
	}

	public DiscordSettings getSettings() {
		return settings;
	}

	public static boolean hasRank(String rankName, MessageReceivedEvent event) {
		return false;
	}

	public static boolean hasRank(String rankName, IUser user, String currentChannelId) {
		//List<IRole> list = user.getRolesForGuild(user.getClient().getChannelByID(currentChannelId).getGuild());
		List<IRole> list = user.getRolesForGuild(user.getClient().getChannelByID(Long.parseLong(currentChannelId)).getGuild());
		for (int index = 0; index < list.size(); index++) {
			String roleId = list.get(index).toString().replace("<@&", "");
			roleId = roleId.replace(">", "");
			if (user.getClient().getRoleByID(Long.parseLong(roleId)) != null) {
				if (user.getClient().getRoleByID(Long.parseLong(roleId)).getName().contains(rankName)) {
					return true;
				}
			}
		}
		return false;
	}

}
