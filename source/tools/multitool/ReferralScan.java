package tools.multitool;

import core.ServerConfiguration;
import core.ServerConstants;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import tools.discord.content.DiscordCommands;
import utility.Misc;
import utility.ReferralWebsiteDb;
import utility.WebsiteLogInDetails;
import utility.WebsiteSqlConnector;

public class ReferralScan {
	private final static String IP_COLLECTION_NAME_LOCATION = "backup/logs/player base/collection ip name.txt";

	public static void ipTracker() {
		ArrayList<String> data = new ArrayList<String>();
		for (int a = 0; a < ReferralWebsiteDb.referralData.size(); a++) {
			String parse[] = ReferralWebsiteDb.referralData.get(a).split("!");
			String siteId = parse[0];
			String ip = parse[1];
			boolean match = false;
			for (int index = 0; index < data.size(); index++) {
				String parseData[] = data.get(index).split("#");
				String siteIdData = parseData[0];
				if (siteId.equals(siteIdData)) {
					String oldData = data.get(index).substring(siteIdData.length() + 1);
					data.remove(index);
					data.add(index, siteIdData + "#" + oldData + " " + ip);
					match = true;
					break;
				}

			}
			if (!match) {
				data.add(siteId + "#" + ip);
			}
		}

		ArrayList<String> collection = new ArrayList<String>();
		String location = IP_COLLECTION_NAME_LOCATION;
		try {
			BufferedReader file = new BufferedReader(new FileReader(location));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					collection.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int totalDonated = 0;
		for (int index = 0; index < data.size(); index++) {
			ArrayList<String> link = new ArrayList<String>();
			String parseData[] = data.get(index).split("#");
			String siteIdData = parseData[0];
			String oldData = data.get(index).substring(siteIdData.length() + 1);
			String parseIp[] = oldData.split(" ");
			for (int i = 0; i < parseIp.length; i++) {
				link.add(parseIp[i]);
			}
			totalDonated += results(collection, link, "ref" + siteIdData);
		}
		DiscordCommands.addOutputText("Total donated: " + totalDonated + "$");
		DiscordCommands.botBusy = false;
	}

	public static int results(ArrayList<String> collection, ArrayList<String> linkClicksIp, String string) {
		int linkClicks = linkClicksIp.size();
		int logIns = scanCollectionIps(linkClicksIp, collection, string);
		int clicks = linkClicks;
		String name = string;
		Double ratio = (double) clicks / (double) logIns;
		int averageKills = 0;
		int averageTeleports = 0;
		double paymentInUsdTotal = 0;
		if (logIns > 0) {
			averageKills = scanCollectionIpsAndGetTilesWalked(linkClicksIp, collection, "wildernessKills") / logIns;
			averageTeleports = scanCollectionIpsAndGetTilesWalked(linkClicksIp, collection, "teleportsUsed") / logIns;
			paymentInUsdTotal = scanCollectionIpsAndGetTilesWalkedDouble(linkClicksIp, collection, "totalPaymentAmount");
		}
		int incomeFromPlayers = (int) paymentInUsdTotal;
		@SuppressWarnings("unused")
		double incomeFromPlayersDouble = 0.0;
		if (incomeFromPlayers > 0 && logIns > 0) {
			incomeFromPlayersDouble = Misc.roundDoubleToNearestTwoDecimalPlaces((double) incomeFromPlayers / (double) logIns);
		}
		String extra = DiscordCommands.limitRefs ? "" : ", average kills: " + averageKills + ", average tps: " + averageTeleports;
		DiscordCommands.addOutputText(name + ": clicks: " + clicks + ", log ins: " + logIns + ", clicks per log in: " + round(ratio, 1) + extra + ", players spent: " + incomeFromPlayers + "$");
		return incomeFromPlayers;
	}

	private static double round(double value, int precision) {
		if (Double.isInfinite(value)) {
			return Double.POSITIVE_INFINITY;
		}
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}

	private static double scanCollectionIpsAndGetTilesWalkedDouble(ArrayList<String> linkClicksIp, ArrayList<String> collection, String target) {
		double count = 0;
		for (int index = 0; index < linkClicksIp.size(); index++) {
			String ip = linkClicksIp.get(index);
			for (int i = 0; i < collection.size(); i++) {
				String parse[] = collection.get(i).split("-");
				String collectionIp = parse[0];
				if (ip.equals(collectionIp)) {
					try {
						BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocationWithoutLastSlash() + "/" + parse[1] + ".txt"));
						String line;
						while ((line = file.readLine()) != null) {
							if (line.startsWith(target + " = ")) {
								String string = line.substring(line.lastIndexOf("=") + 2);
								if (string.isEmpty()) {
									continue;
								}
								double amount = Double.parseDouble(string);
								count += amount;
							}
						}
						file.close();
					} catch (Exception e) {
					}
					break;
				}
			}
		}
		return count;
	}

	private static int scanCollectionIpsAndGetTilesWalked(ArrayList<String> linkClicksIp, ArrayList<String> collection, String target) {
		int count = 0;
		for (int index = 0; index < linkClicksIp.size(); index++) {
			String ip = linkClicksIp.get(index);
			for (int i = 0; i < collection.size(); i++) {
				String parse[] = collection.get(i).split("-");
				String collectionIp = parse[0];
				if (ip.equals(collectionIp)) {
					try {
						BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocationWithoutLastSlash() + "/" + parse[1] + ".txt"));
						String line;
						while ((line = file.readLine()) != null) {
							if (line.startsWith(target + " = ")) {
								String string = line.substring(line.lastIndexOf("=") + 2);
								if (string.isEmpty()) {
									continue;
								}
								int amount = Integer.parseInt(string);
								if (target.equals("donatorTokensReceived")) {
									int limit = (DiscordCommands.referralDonatedAmountLimit * 11);
									if (amount > limit && limit > 0) {
										amount = limit;
									}
								}
								count += amount;
							}
						}
						file.close();
					} catch (Exception e) {
					}
					break;
				}
			}
		}
		return count;
	}

	private static int scanCollectionIps(ArrayList<String> linkClicksIp, ArrayList<String> collection, String string) {
		int count = 0;
		for (int index = 0; index < linkClicksIp.size(); index++) {
			String ip = linkClicksIp.get(index);
			for (int i = 0; i < collection.size(); i++) {
				String parse[] = collection.get(i).split("-");
				String collectionIp = parse[0];
				if (ip.equals(collectionIp)) {
					count++;
					break;
				}
			}
		}
		return count;
	}

	public static void initiate() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		DiscordCommands.botBusy = true;
		WebsiteLogInDetails.readLatestWebsiteLogInDetails();
		ReferralWebsiteDb.referralData.clear();
		String query = "SELECT * FROM referral.logs order by id desc;";
		if (DiscordCommands.limitRefs) {
			query = "SELECT * FROM referral.logs WHERE siteId BETWEEN 104 AND 150 order by id desc;";
		}
		new Thread(new WebsiteSqlConnector(null, "REFERRAL STATISTICS", "referral", query, "", WebsiteLogInDetails.IP_ADDRESS,
		                                   WebsiteLogInDetails.SQL_USERNAME, WebsiteLogInDetails.SQL_PASSWORD)).start();

	}

}

