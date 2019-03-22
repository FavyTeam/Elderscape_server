package tools.discord;

import core.GameType;
import core.ServerConfiguration;
import tools.discord.api.DiscordBot;

public class DiscordLaunchExternal {

	public static void main(String args[]) {
		ServerConfiguration.DEBUG_MODE = false;
		DiscordBot.startDiscordBot(args[0]);
	}

}
