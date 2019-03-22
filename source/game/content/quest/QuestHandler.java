package game.content.quest;

import core.GameType;
import core.Plugin;
import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.player.Player;
import utility.Misc;

public class QuestHandler {

	public static int setColor(int c) {
		switch (c) {
			case 1:
				return ServerConstants.RED_HEX;
			case 2:
				return ServerConstants.YELLOW_HEX;
			case 3:
				return ServerConstants.GREEN_HEX;
		}
		return 0;
	}

	private final Player player;

	public QuestHandler(Player player) {
		this.player = player;
	}

	public void sendQuestInterface(Player client, String questName, String... lines) {
		client.getPA().sendFrame126("@dre@" + questName, 8144);

		for (int i = 0; i < lines.length; i++) {
			client.getPA().sendFrame126(lines[i], 8147 + i);
		}
		for (int i = 8147 + lines.length; i < 8195; i++) {
			client.getPA().sendFrame126("", i);
		}
		client.getPA().displayInterface(8134);
	}

	public void completeQuest(String questName, QuestReward questReward, int itemId) {
		player.qp++;
		player.getPA().sendMessage("Congratulations, you have completed " + questName + "!");
		player.getPA().sendFrame126("You have completed " + questName + "!", 12144);
		for (int i = 0; i < questReward.rewards.length; i++) {
			if (questReward.rewards[i] != null) {
				player.getPA().sendFrame126(questReward.rewards[i], 12150 + i);
			} else {
				player.getPA().sendFrame126(" ", 12150 + i);
			}
		}
		player.getPA().sendFrame126("Quest Points:", 12146);
		player.getPA().sendFrame126("" + player.qp, 12147);
		player.getPA().displayInterface(12140);
		player.gfx100(199);
		if (itemId > 0) {
			player.getPA().sendFrame246(12145, 240, itemId);
		}
		QuestHandler.updateAllQuestTab(player);
		//player.getPA().playTempSong(86);
	}

	public void ShowQuestInfo(String questName, String startInfo) {
		int frameIndex = 0;
		player.getPA().sendFrame126("" + questName + "", 14103);
		player.getPA().sendFrame126("" + startInfo + "", 14105 + frameIndex);
		frameIndex++;
		InterfaceAssistant.setFixedScrollMax(player, 14105, (int) (frameIndex * 20.5));
		InterfaceAssistant.clearFrames(player, 14105 + frameIndex, 14130);
		player.getPA().displayInterface(14202);
	}

	public static void updateQuestTabTitles(Player player, int id, String name, int questStage, int stages) {
		int c;
		if (questStage == 0) {
			c = 1;
		} else if (questStage == stages) {
			c = 3;
		} else {
			c = 2;
		}
		player.getPA().sendFrame126(name, QUEST_INTERFACE_START_FRAME + id);
		player.getPA().changeTextColour(QUEST_INTERFACE_START_FRAME + id, setColor(c));
	}

	public boolean hasCompletedAll() {
		for (int i = 0; i <= Quest.totalQuests; i++) {
			if (Quest.cachedQuestConfig[i] != null) {
				if (player.getQuest(i).getStage() != Quest.cachedQuestConfig[i].stages && i != 30 && i != 34)
					return false;
			}
		}
		return true;
	}

	private final static int QUEST_POINTS_INTERFACE_FRAME = 23303;

	private final static int QUEST_INTERFACE_START_FRAME = 23305;

	private static final int QUEST_TAB_BUTTON_START_ID = 91009;

	public static void updateAllQuestTab(Player player) {
		if (GameType.isOsrsPvp()) {
			return;
		}
		for (int i = 0; i < Quest.totalQuests; i++) {
			if (Quest.questConfigData[i] != null && i < Quest.questConfigData.length) {
				if (Quest.questConfigData[i] == null) {
					Misc.print("Nulled: " + i);
				}
				updateQuestTabTitles(player, i, Quest.questConfigData[i].name, player.getQuest(Quest.questConfigData[i].i).getStage(), Quest.questConfigData[i].stages);
			}
		}
		player.getPA().sendFrame126("Quest Points: " + player.qp, QUEST_POINTS_INTERFACE_FRAME);
	}

	public static boolean isQuestButton(Player player, int buttonId) {
		int buttonIndex = buttonId - QUEST_TAB_BUTTON_START_ID;
		if (buttonIndex >= 0 && buttonIndex <= Quest.questConfigData.length - 1) {
			return Plugin.execute("quest_button_" + Quest.questConfigData[buttonIndex].i, player);
		}
		return false;
	}

	public static void startInfo(Player player, String text1, String text2, String text3, String text4, String text5) {
		player.getPA().displayInterface(14202);
		int index = 0;

		player.getPA().sendFrame126(text1, 14104);
		index++;
		player.getPA().sendFrame126(text2, 14105 + index);
		index++;
		player.getPA().sendFrame126(text3, 14105 + index);
		index++;
		player.getPA().sendFrame126(text4, 14105 + index);
		index++;
		player.getPA().sendFrame126(text5, 14105 + index);
		index++;
	}

}
