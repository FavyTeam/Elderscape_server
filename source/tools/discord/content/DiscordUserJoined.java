package tools.discord.content;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import tools.discord.api.DiscordBot;

import java.time.LocalDateTime;

public class DiscordUserJoined {

	public static void userJoined(IGuild guild, IUser user, LocalDateTime when) {
		DiscordUserJoined.updateNameTotalCount();
		DiscordBot.sendMessage("357660154935902209", "Welcome to Dawntained " + user.getName() + "!", false);
	}

	public static boolean updateNameTotalCount() {
		/*
		if (DiscordBot.isDiscordBot(DiscordConstants.DAWNTAINED_BOT))
		{
			String generalTextChannelId = "357660154935902209";
			if (DiscordBot.bot.getClient().getChannelByID(generalTextChannelId) == null)
			{
				return false;
			}
			DiscordBot.bot.getClient().changeStatus(Status.game(DiscordBot.bot.getClient().getChannelByID(generalTextChannelId).getGuild().getTotalMemberCount() + " users here!"));
			return true;
		}
		*/
		return false;
	}


}
