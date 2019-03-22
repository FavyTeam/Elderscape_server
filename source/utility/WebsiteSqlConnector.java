package utility;

import game.player.Player;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import network.connection.DonationManager;
import network.connection.VoteManager;

public class WebsiteSqlConnector implements Runnable {

	private Player player;

	private Connection connection;

	private Statement statement;

	private String actionType;

	private String databaseName;

	private String query;

	private String nothingToClaimMessage;

	private String websiteIp;

	private String sqlUsername;

	private String sqlPassword;

	public WebsiteSqlConnector(Player player, String actionType, String databaseName, String query, String nothingToClaimMessage, String websiteIp, String sqlUsername,
	                           String sqlPassword) {
		this.player = player;
		this.actionType = actionType;
		this.databaseName = databaseName;
		this.query = query;
		this.nothingToClaimMessage = nothingToClaimMessage;
		this.websiteIp = websiteIp;
		this.sqlUsername = sqlUsername;
		this.sqlPassword = sqlPassword;
	}

	@Override
	public void run() {
		ReferralWebsiteDb.delete = false; // Reset
		if (player != null) {
			if (websiteIp.isEmpty()) {
				player.getPA().sendMessage("This feature is disabled.");
				return;
			}
			long timeLastClaimed = actionType.contains("BMT") ? player.timeClaimedBmtCheck : player.sqlConnectionDelay;
			if (System.currentTimeMillis() - timeLastClaimed <= 20000) {
				player.getPA().sendMessage("Please wait at least 20 seconds before trying again.");
				return;
			}
			if (actionType.contains("BMT")) {
				player.timeClaimedBmtCheck = System.currentTimeMillis();
			} else {
				player.sqlConnectionDelay = System.currentTimeMillis();
			}
			player.getPA().sendMessage("Connecting to website...");
			player.websiteMessaged = false;
			player.totalWebsiteClaimed = 0;
		}
		if (websiteIp.isEmpty()) {
			return;
		}
		boolean continueLooping = false;
		try {
			DonationChargebackDb.preExecute(actionType);
			if (!connect(websiteIp, sqlUsername, sqlPassword, databaseName)) {
				return;
			}
			ResultSet rs = null;
			if (actionType.equals("CLAIM DONATION") || actionType.equals("CLAIM BMT DONATION")) {
				if (player == null) {
					return;
				}
				rs = query(query, new char[]
						                  {'s'}, new Object[]
								                         {player.getPlayerName()});
			} else {
				rs = executeQuery(query);
			}
			while (rs.next()) {
				switch (actionType) {
					case "REFERRAL STATISTICS":
						continueLooping = ReferralWebsiteDb.executeQuery(rs);
						break;
					case "DONATION CHARGEBACK":
						continueLooping = DonationChargebackDb.executeQuery(rs);
						break;
					case "REFERRAL DELETE":
						ReferralWebsiteDb.delete = true;
						continueLooping = ReferralWebsiteDb.executeQuery(rs);
						break;
					case "CLAIM DONATION":
						continueLooping = DonationManager.getContinueLoopingAndExecuteQueryDonation(rs, player, "Paypal");
						break;
					case "CLAIM BMT DONATION":
						continueLooping = DonationManager.getContinueLoopingAndExecuteQueryDonation(rs, player, "BMT");
						break;
					case "CLAIM VOTE":
						continueLooping = VoteManager.executeQuery(rs, player);
						break;
					case "TOPIC UPDATE":
						continueLooping = WebsiteTopicsNotification.getContinueLoopingAndExecuteQueryTopicsNotification(rs);
						break;
				}
				if (!continueLooping) {
					break;
				}
			}
			if (player != null) {
				if (actionType.contains("CLAIM") && !player.websiteMessaged) {
					player.getPA().sendMessage(nothingToClaimMessage);
					if (actionType.equals("CLAIM DONATION") || actionType.equals("CLAIM BMT DONATION")) {
						player.getPA().sendMessage("If you have waited 24 hours and still cannot receive the donation, please");
						player.getPA().sendMessage("type in ::thread 2008 and read it carefully.");
					}
				} else if (actionType.equals("CLAIM VOTE") && player.totalWebsiteClaimed > 0) {
					player.getPA().sendMessage("Thank you for voting, you have been awarded " + player.totalWebsiteClaimed + " vote tickets!");
					player.getPA().sendMessage("Talk to the Vote Manager at ::shops to claim your reward.");
				}
			}
			destroy();
			ReferralWebsiteDb.connectionFinished(actionType);
			DonationChargebackDb.connectionFinished(actionType);
			WebsiteTopicsNotification.connectionFinished(actionType);
		} catch (Exception e) {
			Misc.print("Failed1: " + actionType + ", " + databaseName + ", " + query);
			e.printStackTrace();
		}
	}


	public boolean connect(String ipAddress, String sqlUsername, String sqlPassword, String database) {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":3306/" + database, sqlUsername, sqlPassword);
		} catch (SQLException e) {
			Misc.print("Failed2: " + actionType + ", " + databaseName + ", " + query);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void destroy() {
		try {
			connection.close();
			connection = null;
			if (statement != null) {
				statement.close();
				statement = null;
			}
		} catch (Exception e) {
			Misc.print("Failed3: " + actionType + ", " + databaseName + ", " + this.query);
			e.printStackTrace();
		}
	}

	public int executeUpdate(String query) {
		try {
			statement = connection.createStatement(1005, 1008);
			int results = statement.executeUpdate(query);
			return results;
		} catch (SQLException ex) {
			Misc.print("Failed4: " + actionType + ", " + databaseName + ", " + this.query);
			ex.printStackTrace();
		}
		return -1;
	}

	public ResultSet executeQuery(String query) {
		try {
			statement = connection.createStatement(1005, 1008);
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException ex) {
			Misc.print("Failed5: " + actionType + ", " + databaseName + ", " + this.query);
			ex.printStackTrace();
		}
		return null;
	}

	public ResultSet query(String query, char[] type, Object[] value) {
		try {
			PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			if (type == null || value == null) {
				ps.executeQuery();
				return null;
			}

			for (int i = 0; i < type.length; i++) {
				if (type[i] == 's') {
					ps.setString((i + 1), value[i].toString());
				} else if (type[i] == 'i') {
					ps.setInt((i + 1), Integer.parseInt(value[i].toString()));
				} else if (type[i] == 'l') {
					ps.setLong((i + 1), Long.parseLong(value[i].toString()));
				} else if (type[i] == 'd') {
					ps.setDouble((i + 1), Double.parseDouble(value[i].toString()));
				}
			}

			if (query.toLowerCase().startsWith("select")) {
				ResultSet rs = ps.executeQuery();
				return rs;
			}
			ps.executeUpdate();
			return null;
		} catch (Exception e) {
			Misc.print("Failed6: " + actionType + ", " + databaseName + ", " + this.query);
			e.printStackTrace();
			return null;
		}

	}

}
