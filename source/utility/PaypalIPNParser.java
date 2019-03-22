package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaypalIPNParser {

	public static void parse(String location) {
		ArrayList<String> data = new ArrayList<String>();
		//@formatter:off
		String[] ignore = {"verify_sign", "payer_id", "receiver_id", "charset", "notify_version", "ipn_track_id", "protection_eligibility",
				"shipping_discount", "insurance_amount", "discount", "mc_currency", "handling_amount", "shipping_method", "shipping"
		};
		//@formatter:on
		try {
			BufferedReader file = new BufferedReader(new FileReader(location));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					if (line.contains("Verified IPN")) {
						String[] parse = line.split("&");
						for (int index = 0; index < parse.length; index++) {
							String string = parse[index];
							String split[] = string.split("=");
							boolean skip = false;
							for (int i = 0; i < ignore.length; i++) {
								if (split[0].equals(ignore[i])) {
									//skip = true;
									//break;
								}
							}
							if (skip) {
								continue;
							}
							string = string.replaceAll("%40", "@");
							if (index == 0) {
								string = string.replace(" Verified IPN: cmd=_notify-validate", "");
								string = string.replace("[", "");
								string = string.replace(" UTC]", "");
								SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm");
								try {
									Date d = f.parse(string);
									long milliseconds = d.getTime();
									// +1 hour because UTC is 1 hour behind my pc
									string = millisecondsToDate(milliseconds + 3600000);
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
							data.add(string);
						}
						data.add("");
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileUtility.saveArrayContentsSilent("backup/logs/discord/paypal_formatted.txt", data);
	}


	public static String millisecondsToDate(long milliseconds) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm: a");
		String dateFormatted = formatter.format(milliseconds);
		return dateFormatted;
	}
}
