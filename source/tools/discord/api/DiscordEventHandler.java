package tools.discord.api;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import tools.discord.content.DiscordBotOnline;
import tools.discord.content.DiscordUserJoined;

import java.time.LocalDateTime;

public class DiscordEventHandler {
	/*
	@EventSubscriber
	public void onMessageEvent(MessageReceivedEvent event)
	{
		DiscordMessageSent.messageSent(event);
	}

	*/
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		DiscordBotOnline.botFullyOnline(event);
	}

	@EventSubscriber
	public void UserJoinEvent(IGuild guild, IUser user, LocalDateTime when) {
		DiscordUserJoined.userJoined(guild, user, when);
	}
}
