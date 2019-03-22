package tools.discord.content;

import sx.blah.discord.handle.impl.events.ReadyEvent;
import tools.discord.api.DiscordBot;
import utility.Misc;

public class DiscordBotOnline {

	public static void botFullyOnline(ReadyEvent event) {
		DiscordUserJoined.updateNameTotalCount();
		Misc.print("Bot is online: " + DiscordBot.BOT_TYPE);
	}

}
