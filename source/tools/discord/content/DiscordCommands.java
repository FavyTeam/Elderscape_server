package tools.discord.content;

import game.content.commands.NormalCommand;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.GameTimeSpent;
import game.content.staff.StaffActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import tools.multitool.ReferralScan;
import utility.ChargebackPlayerAutoJail;
import utility.Misc;
import utility.TwilioApi;
import utility.WebsiteBackup;
import utility.WebsiteLogInDetails;

public class DiscordCommands {

	public static void discordCommand(IMessage message) {
		String currentChannelId = message.getChannel().getLongID() + "";
		IUser user = message.getAuthor();
		String command = message.getContent().substring(DiscordConstants.COMMAND_TRIGGER.length(), message.getContent().length());
		if (!command.contains("announce")) {
			command = command.toLowerCase();
		}
		if (command.equals("response") || command.equals("alive")) {
			DiscordBot.sendMessage(currentChannelId, "I'm awake and alive " + user.getName(), false);
			return;
		}
		if (botBusy) {
			DiscordBot.sendMessage(currentChannelId, "I am busy executing another command, please try again in 30 seconds.", false);
			return;
		}
		switch (DiscordBot.BOT_TYPE) {
			case DiscordConstants.SERVER_BOT:
				serverBotCommand(command, currentChannelId, user, message);
				break;
			case DiscordConstants.ANNOUNCEMENT_BOT:
				announcementBotCommand(command, currentChannelId, user, message);
				break;
			case DiscordConstants.DAWNTAINED_BOT:
				dawntainedBotCommand(command, currentChannelId, user, message);
				break;
			case DiscordConstants.EXTERNAL_BOT:
				externalBotCommand(command, currentChannelId, user, message);
				break;
		}
	}

	private static void dawntainedBotCommand(String command, String currentChannelId, IUser user, IMessage message) {
		if (DiscordBot.hasRank("Moderator", user, currentChannelId)) {
			if (!currentChannelId.equals(DiscordConstants.STAFF_CHANNEL_DAWNTAINED)) {
				DiscordBot.sendMessage(currentChannelId, "You cannot use commands outside the staff channel.", false);
				return;
			}
			if (command.equals("viewflagged")) {
				String flaggedText = "";
				for (int index = 0; index < DiscordMessageSent.flaggedText.size(); index++) {
					flaggedText = flaggedText + DiscordMessageSent.flaggedText.get(index) + "\n";
				}
				DiscordBot.sendMessage(currentChannelId, flaggedText, false);
			} else if (command.startsWith("addflagged")) {
				String[] parse = command.split(" ");
				DiscordMessageSent.flaggedText.add(parse[1]);
				DiscordBot.sendMessage(currentChannelId, "The term '" + parse[1] + "' has been flagged.", false);
				DiscordMessageSent.updateFlaggedFile();
			} else if (command.startsWith("deleteflagged")) {
				String[] parse = command.split(" ");
				for (int index = 0; index < DiscordMessageSent.flaggedText.size(); index++) {
					if (DiscordMessageSent.flaggedText.get(index).equals(parse[1])) {
						DiscordMessageSent.flaggedText.remove(index);
						DiscordBot.sendMessage(currentChannelId, "The term '" + parse[1] + "' has been removed.", false);
						DiscordMessageSent.updateFlaggedFile();
						return;
					}
				}
				DiscordBot.sendMessage(currentChannelId, "The term '" + parse[1] + "' was not found.", false);
			}
		}
	}

	private static void serverBotCommand(String command, String currentChannelId, IUser user, IMessage message) {
		if (DiscordBot.hasRank("Moderator", user, currentChannelId)) {
			if (command.equals("activity")) {
				StaffActivity.printCurrentActivity();
				return;
			}
			if (command.equals("activeplayers")) {
				GameTimeSpent.printActivePlayers();
				return;
			}
			if (command.equals("dice")) {
				DiceSystem.saveDiceLogs();
				DiscordBot.sendMessage(currentChannelId, "Uploading files...", false);
				try {

					final File folder = new File("backup/logs/dice/history");
					ArrayList<String> sort = new ArrayList<String>();
					for (final File fileEntry : folder.listFiles()) {
						sort.add(fileEntry.getPath() + "#" + fileEntry.lastModified());
					}

					// Sort the order from the recently modified file to the oldest file
					sort = NormalCommand.sort(sort, "#");
					for (int index = 0; index < sort.size(); index++) {
						String[] parse = sort.get(index).split("#");
						DiscordBot.client.getChannelByID(Long.parseLong(currentChannelId)).sendFile(new File(parse[0]));
						if (index == 3) {
							break;
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (DiscordException e) {
					e.printStackTrace();
				} catch (RateLimitException e) {
					e.printStackTrace();
				} catch (MissingPermissionsException e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

	public static String queuedBotString = "";

	public static void addOutputText(String string) {
		queuedBotString = queuedBotString + string + "\n";
	}

	public static int referralDonatedAmountLimit = 0;

	public static boolean limitRefs;

	public static boolean botBusy;

	private static void externalBotCommand(String command, String currentChannelId, IUser user, IMessage message) {
		if (command.startsWith("callmohamed")) {
			try {
				String reason = command.substring(12);
				DiscordCommands.addOutputText("Calling Mohamed's phone and sending sms text:");
				DiscordCommands.addOutputText(user.getName() + " on discord: " + reason);
				DiscordBot.sendMessage(currentChannelId, DiscordCommands.queuedBotString, true);
				TwilioApi.callAdmin(user.getName() + " on discord: " + reason, "");
				DiscordBot.sendMessage(currentChannelId, DiscordCommands.queuedBotString, true);
			} catch (Exception e) {
				DiscordBot.sendMessage(currentChannelId, "Use as ::callmohamed reasonhere", false);
			}
			return;
		}
		// Owner command log channel
		if (currentChannelId.equals(DiscordConstants.GRAHAM_CHANNEL)) {

			if (command.startsWith("referral")) {
				grahamReferralCommand();
			}
		}
		// Owner command log channel
		if (currentChannelId.equals(DiscordConstants.OWNER_COMMAND_LOG_CHANNEL)) {
			if (DiscordBot.hasRank("Administrator", user, currentChannelId)) {
				if (command.startsWith("referral")) {
					limitRefs = false;
					String[] parse = command.split(" ");
					referralDonatedAmountLimit = 0;
					if (parse.length > 1) {
						referralDonatedAmountLimit = Integer.parseInt(parse[1]);
					}
					DiscordBot.sendMessage(currentChannelId, "Referral with spent limit: " + (referralDonatedAmountLimit == 0 ? "unlimited" : referralDonatedAmountLimit) + "$",
					                       false);
					ReferralScan.initiate();
				} else if (command.equals("paypal")) {
					botBusy = true;
					File file = new File("backup/logs/discord/paypal_raw.txt");
					file.delete();
					file = new File("backup/logs/discord/paypal_formatted.txt");
					file.delete();
					file = new File("backup/logs/discord/paypal_other.txt");
					file.delete();
					DiscordBot.sendMessage(currentChannelId, "Connecting to website...", false);
					WebsiteLogInDetails.readLatestWebsiteLogInDetails();
					String tempFileLocation = "backup/logs/donations/ipn/ipn_temp_" + Misc.random(10) + System.currentTimeMillis() + ".txt";
					WebsiteBackup.downloadWebsiteFile("/home/admin/public_html/store/class", "payments.txt", tempFileLocation);
					try {
						DiscordBot.sendMessage(currentChannelId, "Uploading file...", false);
						file = new File(ChargebackPlayerAutoJail.IPN_FULL_LOCATION);
						file.delete();
						file = new File(tempFileLocation);
						File dest = new File(ChargebackPlayerAutoJail.IPN_FULL_LOCATION);
						file.renameTo(dest);
						DiscordBot.client.getChannelByID(Long.parseLong(currentChannelId)).sendFile(dest);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (DiscordException e) {
						e.printStackTrace();
					} catch (RateLimitException e) {
						e.printStackTrace();
					} catch (MissingPermissionsException e) {
						e.printStackTrace();
					}
					botBusy = false;
				}
			}
		}
	}

	private static void grahamReferralCommand() {
		limitRefs = true;
		referralDonatedAmountLimit = 0;
		DiscordBot.sendMessage(DiscordConstants.GRAHAM_CHANNEL, "Referral with spent limit: " + (referralDonatedAmountLimit == 0 ? "unlimited" : referralDonatedAmountLimit) + "$", false);
		ReferralScan.initiate();
	}

	private static void announcementBotCommand(String command, String currentChannelId, IUser user, IMessage message) {
		if (!DiscordBot.hasRank("Administrator", user, currentChannelId)) {
			return;
		}
		if (command.startsWith("announce")) {
			String text = command.replace("announce ", "");
			DiscordBot.sendMessage(currentChannelId, text, false);
			for (int index = 0; index < DiscordBot.bot.getClient().getUsers().size(); index++) {
				IUser loop = DiscordBot.bot.getClient().getUsers().get(index);
				if (loop.isBot()) {
					continue;
				}
				try {
					DiscordBot.client.getOrCreatePMChannel(loop).sendMessage(text);
					Misc.print(index + "/" + DiscordBot.bot.getClient().getUsers().size() + " Sent private message to: " + loop.getName());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (MissingPermissionsException e) {
					e.printStackTrace();
				} catch (RateLimitException e) {
					e.printStackTrace();
				} catch (DiscordException e) {
					e.printStackTrace();
				}
			}
			return;
		}
	}
}


