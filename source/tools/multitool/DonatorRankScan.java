package tools.multitool;

import core.ServerConstants;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DonatorRankScan {


	private static int donator = 0;

	private static int superDonator = 0;

	private static int extremeDonator = 0;

	private static int legendaryDonator = 0;

	private static int uber = 0;

	private static int immortal = 0;

	private static int ultimate = 0;

	private static int supreme = 0;

	private static int lucifer = 0;

	private static int omega = 0;

	public static void scanDonatorsAmounts(int alphabetIndex) {
		DawntainedMultiTool.scanThreadOnline = true;
		final File folder = new File(ServerConstants.getCharacterLocationWithoutLastSlash());

		int size = DawntainedMultiTool.getTotalCharacters();
		for (final File fileEntry : folder.listFiles()) {
			if (alphabetIndex == -1) {
				if (!Character.isDigit(fileEntry.getName().charAt(0))) {
					break;
				}
			} else {
				if (!fileEntry.getName().startsWith(DawntainedMultiTool.ALPHABET[alphabetIndex])) {
					continue;
				}
			}
			try {
				BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocationWithoutLastSlash() + "/" + fileEntry.getName()));
				String originalLine;
				while ((originalLine = file.readLine()) != null) {
					if (originalLine.contains("donatorTokensRankUsed")) {
						int tokens = Integer.parseInt(originalLine.substring(originalLine.lastIndexOf("=") + 2));
						if (tokens >= 150_000) {
							omega++;
						} else if (tokens >= 100_000) {
							lucifer++;
						} else if (tokens >= 50_000) {
							supreme++;
						} else if (tokens >= 25000) {
							immortal++;
						} else if (tokens >= 12000) {
							uber++;
						} else if (tokens >= 6000) {
							ultimate++;
						} else if (tokens >= 3000) {
							legendaryDonator++;
						} else if (tokens >= 1000) {
							extremeDonator++;
						} else if (tokens >= 400) {
							superDonator++;
						} else if (tokens >= 150) {
							donator++;
						}
						DawntainedMultiTool.totalScans++;
						if (DawntainedMultiTool.totalScans % 500 == 0) {
							DawntainedMultiTool.setStatusText("Accounts scanned: " + DawntainedMultiTool.totalScans + "/" + size);
						}

					}
				}
				file.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		DawntainedMultiTool.scanThreadsActive--;
	}

	public static void scanDonatorAmountsEnd() {
		DawntainedMultiTool.updateOutputText("Omega: " + omega, Color.BLACK, true);
		DawntainedMultiTool.updateOutputText("Lucifer: " + lucifer, Color.BLACK, true);
		DawntainedMultiTool.updateOutputText("Supreme: " + supreme, Color.BLACK, true);
		DawntainedMultiTool.updateOutputText("Immortal: " + immortal, Color.BLACK, true);
		DawntainedMultiTool.updateOutputText("Uber: " + uber, Color.BLACK, true);
		DawntainedMultiTool.updateOutputText("Ultimate: " + ultimate, Color.BLACK, true);
		DawntainedMultiTool.updateOutputText("Legendary: " + legendaryDonator, Color.BLACK, true);
		DawntainedMultiTool.updateOutputText("Extreme: " + extremeDonator, Color.BLACK, true);
		DawntainedMultiTool.updateOutputText("Super: " + superDonator, Color.BLACK, true);
		DawntainedMultiTool.updateOutputText("Normal: " + donator, Color.BLACK, true);
		DawntainedMultiTool.setStatusText("Completed.");
	}
}
