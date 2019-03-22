package tools.discord.api;

import sx.blah.discord.handle.obj.IMessage;

public abstract class CommandListener {

	protected DiscordBot bot;

	private String commandPrefix;

	public CommandListener(DiscordBot bot, String commandPrefix) {
		this.bot = bot;
		this.commandPrefix = commandPrefix;
	}

	public abstract void handleCommand(IMessage message, String command, String[] cmd);

	public String getCommandPrefix() {
		return commandPrefix;
	}


}
