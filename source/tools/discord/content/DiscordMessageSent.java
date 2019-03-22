package tools.discord.content;

import java.util.ArrayList;
import sx.blah.discord.handle.obj.IMessage;
import tools.discord.DiscordConstants;
import utility.EmailSystem;
import utility.FileUtility;

public class DiscordMessageSent {

	/**
	 * Flagged text such as sythe etc..
	 */
	public static ArrayList<String> flaggedText = new ArrayList<String>();

	public static void readFlaggedText() {
		flaggedText = FileUtility.readFile("discord_flagged.txt");
	}


	public static void updateFlaggedFile() {
		FileUtility.deleteAllLines("discord_flagged.txt");
		FileUtility.saveArrayContents("discord_flagged.txt", flaggedText);

	}

	private final static String BUG_MESSAGE_ID_LOCATION = "backup/logs/discord/bug_message_id.txt";

	public static void loadLastBugMessageId() {
		if (!FileUtility.fileExists(BUG_MESSAGE_ID_LOCATION)) {
			return;
		}
		lastBugMessageId = FileUtility.readFirstLine(BUG_MESSAGE_ID_LOCATION);
	}

	public static String lastBugMessageId = "";

	public static void messageSent(IMessage message) {
		String content = message.getContent();
		String channelId = Long.toString(message.getChannel().getLongID());
		String messageId = Long.toString(message.getLongID());
		String senderName = message.getChannel().getName();
		if (channelId.equals(DiscordConstants.DAWNTAINED_BUGS_CHANNEL)) {
			if (!lastBugMessageId.equals(messageId)) {
				EmailSystem.addPendingEmail("New bug report on discord", senderName + ": " + content, "mohamed_ffs25ffs@yahoo.com");
				lastBugMessageId = messageId;
				FileUtility.deleteAllLines(BUG_MESSAGE_ID_LOCATION);
				FileUtility.addLineOnTxt(BUG_MESSAGE_ID_LOCATION, lastBugMessageId);
			}
		}

	}
}
