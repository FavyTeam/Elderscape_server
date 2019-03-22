package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import utility.Misc;

public class UpdateServerTool {

	private static Connection connection;

	private static Statement statement;

	public static void main(String[] args) {
		Misc.print("Started");
		boolean continueLooping = false;
		try {
			if (!connect("94.23.249.25", "", "", "vote")) {
				return;
			}
			ResultSet rs = null;
			long time = 0;
			while (true) {
				if (System.currentTimeMillis() - time <= 5000) {
					continue;
				}
				Misc.print("Query started.");
				executeQueryCode(rs, "SELECT * FROM vote.fx_votes order by id desc LIMIT 0, 10;");
				// UPDATE `vote`.`fx_votes` SET `site_id`='3' WHERE `id`='1510424';
				// DELETE FROM `vote`.`fx_votes` WHERE `id`='1510435';
				// UPDATE `vote`.`fx_votes` SET `id`='1510440', `callback_date`='2018-05-10 09:53:08', `site_id`='3', `uid`='305691401' WHERE `id`='1510439';

				time = System.currentTimeMillis();
			}
			// destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void executeQueryCode(ResultSet rs, String query) {
		try {
			rs = executeQuery(query);
			long loops = 0;
			while (rs.next()) {
				String text;
				text = rs.getString("username");
				Misc.print(text);
				loops++;
			}
			Misc.print("Ended: " + loops);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static boolean connect(String ipAddress, String sqlUsername, String sqlPassword, String database) {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":3306/" + database, sqlUsername, sqlPassword);
		} catch (SQLException e) {
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
			e.printStackTrace();
		}
	}

	public int executeUpdate(String query) {
		try {
			statement = connection.createStatement(1005, 1008);
			int results = statement.executeUpdate(query);
			return results;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	public static ResultSet executeQuery(String query) {
		try {
			statement = connection.createStatement(1005, 1008);
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static ResultSet query(String query, char[] type, Object[] value) {
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
			e.printStackTrace();
			return null;
		}

	}
}
