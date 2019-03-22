package game.content.clanchat;

import java.util.ArrayList;

/**
 * @author Sanity
 */
public class Clan {

	public Clan(String ownerName) {
		this.ownerName = ownerName;
	}

	public int[] members = new int[100];

	public int indexOfClanChatsSavedData = -1;

	public String ownerName;

	public String clanChatTitle;

	public boolean updated;

	public boolean publicChat = true;

	public ArrayList<String> banned = new ArrayList<String>();

	public ArrayList<String> moderators = new ArrayList<String>();

	public ArrayList<String> friends;

}
