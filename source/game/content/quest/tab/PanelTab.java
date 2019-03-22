package game.content.quest.tab;

import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.miscellaneous.GuideBook;
import game.content.miscellaneous.NpcDropTableInterface;
import game.content.profile.Profile;
import game.content.quicksetup.QuickSetUp;
import game.player.Player;

/**
 * Quest tab > panel tab buttons.
 *
 * @author MGT Madness, created on 28-10-2017.
 */
public class PanelTab {

	/**
	 * True if the button is a Panel tab related button.
	 */
	public static boolean isPanelTabButton(Player player, int buttonId) {
		switch (buttonId) {
			case 90072: // Eco, Quest tab, Panels tab, Achievement button
				Achievements.displayAchievementInterface(player);
				return true;

			case 90077: // Eco, Quest tab, Panels tab, Titles button
				PlayerTitle.displayInterface(player);
				return true;

			case 90082: // Eco, Quest tab, Panels tab, Profile button
				Profile.openUpProfileInterface(player);
				return true;

			case 90087: // Eco, Quest tab, Panels tab, Npc drops button
				NpcDropTableInterface.displayInterface(player, true);
				return true;

			case 90092: // Eco, Quest tab, Panels tab, Presets button
				QuickSetUp.displayInterface(player);
				return true;

			case 90097: // Eco, Quest tab, Panels tab, Pets button
				return true;

			case 90102: // Eco, Quest tab, Panels tab, Guide button
				GuideBook.displayGuideInterface(player);
				return true;
		}
		return false;
	}
}
