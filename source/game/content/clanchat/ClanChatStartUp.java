package game.content.clanchat;

import utility.Misc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Load Clan chat data on server start-up. Later to be used for when a player creates a clan chat instance, then save this data to it.
 *
 * @author MGT Madness, created on 01-09-2016.
 */
public class ClanChatStartUp {
	public static List<ClanChatStartUp> clanChats = new ArrayList<ClanChatStartUp>();

	public static void loadClanChatStartUp() {
		File folder = new File("backup/logs/clan chat");
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				loadData(fileEntry.getName(), "backup/logs/clan chat/" + fileEntry.getName());
			}
		}
	}

	private static void loadData(String ownerName, String location) {
		ownerName = ownerName.replace(".txt", "");
		String line = "";
		boolean EndOfFile = false;
		BufferedReader fileLocation = null;
		String fileLocationText = location;
		try {
			fileLocation = new BufferedReader(new FileReader(fileLocationText));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Misc.print(fileLocationText + ": file not found.");
			return;
		}
		try {
			line = fileLocation.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			Misc.print(fileLocationText + ": error loading file.");
		}
		String title = "";
		ArrayList<String> banned = new ArrayList<String>();
		ArrayList<String> moderators = new ArrayList<String>();
		ArrayList<String> friends = new ArrayList<String>();
		boolean publicChat = true;
		while (!EndOfFile && line != null) {
			if (line.isEmpty()) {
				continue;
			}
			if (line.startsWith("Title:")) {
				title = line.substring(6);
			} else if (line.startsWith("Public:off")) {
				publicChat = false;
			} else if (line.startsWith("Mod:")) {
				moderators.add(line.substring(4));
			} else if (line.startsWith("Ban:")) {
				banned.add(line.substring(4));
			} else if (line.startsWith("Friend:")) {
				friends.add(line.substring(7));
			}

			try {
				line = fileLocation.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				EndOfFile = true;
			}
		}
		try {
			fileLocation.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClanChatStartUp item = new ClanChatStartUp(ownerName, title, publicChat, banned, moderators, false, friends);
		clanChats.add(item);


	}

	public ClanChatStartUp(String ownerName, String clanChatTitle, boolean publicChat, ArrayList<String> banned, ArrayList<String> moderators, boolean updated,
	                       ArrayList<String> friends) {
		this.ownerName = ownerName;
		this.clanChatTitle = clanChatTitle;
		this.publicChat = publicChat;
		this.banned = banned;
		this.moderators = moderators;
		this.updated = updated;
		this.friends = friends;
	}

	public String ownerName;

	public String clanChatTitle;

	public boolean publicChat;

	public boolean updated;

	public ArrayList<String> banned = new ArrayList<String>();

	public ArrayList<String> moderators = new ArrayList<String>();

	public ArrayList<String> friends = new ArrayList<String>();

}
