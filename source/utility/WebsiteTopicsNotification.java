package utility;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Send an email to the Administrator when an thread is created or a new post has been made on a thread in an important board of the forums.
 *
 * @author MGT Madness, created on 05-02-2018.
 */
public class WebsiteTopicsNotification {


	/*
	public long lastPost;
	
	
	public WebsiteTopicsNotification(int id, int amount) {
		//this.id = id;
		//this.amount = amount;
	}
	*/

	/*
	public static void main(String[] args) {
		WebsiteTopicsNotification.checkWebsiteForImportantTopicsUpdate();
	}
	*/

	private final static String WEBSITE_TOPICS_NOTIFICATION_FILE_LOCATION = "backup/logs/website_topics.txt";


	public static enum WebsiteTopicsNotificationData {

		// Must also add manually to the SEARCH_QUERY array.
		CUSTOM_PAYMENT(46),
		PAYMENT_REWARD_ISSUE(49),
		CUSTOM_ITEM(43),
		REPORT_A_BUG(52);

		private int boardId;


		private WebsiteTopicsNotificationData(int boardId) {
			this.boardId = boardId;
		}

		public int getBoardId() {
			return boardId;
		}
	}

	private final static String[] SEARCH_QUERY = {
			"SELECT * FROM forums_topics",
			"WHERE forum_id = 46 OR forum_id = 49 OR forum_id = 43 OR forum_id = 50 OR forum_id = 52",
			"order by tid desc LIMIT 10;"
	};

	public static String getBoardName(int boardId) {
		for (int index = 0; index < WebsiteTopicsNotificationData.values().length; index++) {
			WebsiteTopicsNotificationData instance = WebsiteTopicsNotificationData.values()[index];
			if (instance.getBoardId() == boardId) {
				return Misc.capitalize(instance.name().replaceAll("_", " "));
			}
		}
		return "";
	}

	public static ArrayList<Long> lastPostIdList = new ArrayList<Long>();

	public static void checkWebsiteForImportantTopicsUpdate() {
		Misc.print("Checking website topic updates.");
		String query = "";
		for (int index = 0; index < SEARCH_QUERY.length; index++) {
			query = query + SEARCH_QUERY[index] + " ";
		}
		WebsiteLogInDetails.readLatestWebsiteLogInDetails();
		new Thread(new WebsiteSqlConnector(null, "TOPIC UPDATE", "forum_7720654948", query, "", WebsiteLogInDetails.IP_ADDRESS, WebsiteLogInDetails.SQL_USERNAME,
		                                   WebsiteLogInDetails.SQL_PASSWORD)).start();
	}

	public static void loadWebsiteTopicsNotificationHistory() {
		ArrayList<String> arraylist = FileUtility.readFile(WEBSITE_TOPICS_NOTIFICATION_FILE_LOCATION);
		for (int index = 0; index < arraylist.size(); index++) {
			lastPostIdList.add(Long.parseLong(arraylist.get(index)));
		}
	}

	public static boolean getContinueLoopingAndExecuteQueryTopicsNotification(ResultSet rs) {
		try {
			String topicTitle = rs.getString("title");
			int forumId = rs.getInt("forum_id");
			long lastPostId = rs.getLong("last_post");
			String lastPosterName = rs.getString("last_poster_name");

			if (lastPosterName.equalsIgnoreCase("mgt madness")) {
				return true;
			}

			for (int index = 0; index < lastPostIdList.size(); index++) {
				if (lastPostId == lastPostIdList.get(index)) {
					return true;
				}
			}
			lastPostIdList.add(lastPostId);
			ArrayList<String> content = new ArrayList<String>();
			content.add("Topic type: " + getBoardName(forumId));
			content.add("Title: " + topicTitle);
			content.add("By: " + lastPosterName);
			EmailSystem.addPendingEmail("Topic " + getBoardName(forumId) + " from " + lastPosterName + " about " + topicTitle, content, "mgtdt@yahoo.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void connectionFinished(String actionType) {
		if (!actionType.equals("TOPIC UPDATE")) {
			return;
		}
		FileUtility.deleteAllLines(WEBSITE_TOPICS_NOTIFICATION_FILE_LOCATION);
		FileUtility.saveArrayContentsSilent(WEBSITE_TOPICS_NOTIFICATION_FILE_LOCATION, lastPostIdList);

	}

}
