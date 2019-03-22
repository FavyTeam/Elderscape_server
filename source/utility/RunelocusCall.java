package utility;

import core.ServerConfiguration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Read a Runelocus page for a specific text, which means the advertisment is ready to be purchased.
 * @author MGT Madness, createed on 22-08-2018.
 */
public class RunelocusCall {

	public static Timer timer = new Timer();

	public static TimerTask myTask = new TimerTask() {
		@Override
		public void run() {
			loops(argsSaved);
		}
	};

	public static String[] argsSaved = null;

	private static long timeCalled;
	private static void loops(String arguments[]) {
		Misc.print("[" + Misc.getDateAndTime() + "] Scan started.");
		try {
			URL url;
			URLConnection uc;
			String urlString = "https://www.runelocus.com/advertising/";
			url = new URL(urlString);
			uc = url.openConnection();
			uc.connect();
			uc = url.openConnection();
			uc.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
			uc.setRequestProperty("Cache-Control", "max-age=0");
			uc.setRequestProperty("Connection", "keep-alive");
			uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			uc.setRequestProperty("Host", "www.google.com");
			uc.setRequestProperty("Origin", "www.google.com");
			uc.setRequestProperty("Referer", "https://www.google.co.uk");
			uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
			uc.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				for (int index = 0; index < arguments.length; index++) {
					String text = arguments[index];
					if (inputLine.contains(text)) {
						if (Misc.timeElapsed(timeCalled, Misc.getMinutesToMilliseconds(30))) {
							WebsiteLogInDetails.readLatestWebsiteLogInDetails();
							TwilioApi.callAdmin("Runelocus advertisment available", "");
							timeCalled = System.currentTimeMillis();
						}
					}
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		argsSaved = args;
		for (int index = 0; index < argsSaved.length; index++) {
			String text = argsSaved[index];
			Misc.print("Arguments: " + text);
		}
		ServerConfiguration.DEBUG_MODE = false;
		timer.schedule(myTask, 0, Misc.getMinutesToMilliseconds(1));
	}
}
