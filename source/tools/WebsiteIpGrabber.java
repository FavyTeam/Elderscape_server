package tools;

import java.util.ArrayList;
import utility.FileUtility;

public class WebsiteIpGrabber {

	public static void main(String[] args) {

		ArrayList<String> arraylist = FileUtility.readFile(System.getProperty("user.home") + "/Desktop/ips to flag.txt");
		ArrayList<String> ips = new ArrayList<String>();
		ArrayList<String> output = new ArrayList<String>();
		for (int index = 0; index < arraylist.size(); index++) {
			String line = arraylist.get(index);
			if (line.contains("Output.php on line 538")) {
				String first = "[error] [client ";
				String second = "] PHP Fatal";
				line = line.substring(line.indexOf(first) + first.length(), line.indexOf(second));
				if (!ips.contains(line)) {
					ips.add(line);
					output.add("Deny from " + line);
				}
			}
		}

		//
		FileUtility.saveArrayContentsSilent(System.getProperty("user.home") + "/Desktop/output.txt", output);
	}

}
