package tools.discord.api;

import sx.blah.discord.api.internal.json.event.GuildMemberAddEventResponse;
import sx.blah.discord.api.internal.json.objects.UserObject;

public class DiscordJoinsGuild extends GuildMemberAddEventResponse {
	public UserObject user;
}
