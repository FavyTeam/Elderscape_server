package tools.discord.api;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import tools.discord.DiscordConstants;
import tools.discord.content.DiscordCommands;
import tools.discord.content.DiscordMessageSent;

public class MyEvents {


	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMessage().getContent().startsWith(DiscordConstants.COMMAND_TRIGGER)) {
			DiscordCommands.discordCommand(event.getMessage());
		}
		DiscordMessageSent.messageSent(event.getMessage());
	}

}
