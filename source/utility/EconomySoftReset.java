package utility;

import game.content.commands.NormalCommand;
import tools.EconomyScan;

public class EconomySoftReset {
	public static void main(String[] args) {
		Misc.print("Eco reset running.");
		EconomyScan.loadStartupFiles(false);
		NormalCommand.fixCharacters();
		Misc.print("Eco reset completed.");
	}
}
