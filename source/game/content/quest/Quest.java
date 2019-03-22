package game.content.quest;

import core.Plugin;
import core.ServerConstants;
import game.player.Player;

import java.io.File;

public class Quest {

	private Player player;

	private int questId;

	public static int totalQuests;

	public static QuestConfigData[] cachedQuestConfig;

	public static QuestConfigData[] questConfigData = null;

	public Quest(int i, Player player) {
		questId = i;
		this.player = player;
	}

	public int getId() {
		return questId;
	}

	public void setId(int id) {
		questId = id;
	}

	public int getStage() {
		return player.questStages[questId];
	}

	public void setStage(int stage) {
		player.questStages[questId] = stage;
	}

	public static void addQuest(String name, int stages) {
		int id = currentLoadedQuests;
		Quest.questConfigData[id] = new QuestConfigData(id, name, stages);
		Quest.cachedQuestConfig[id] = new QuestConfigData(id, name, stages);
		currentLoadedQuests++;
	}

	private static int currentLoadedQuests;

	public static void loadQuests() {
		File folder = new File(ServerConstants.QUEST_DATA_DIR);
		Quest.totalQuests = folder.list().length;
		Quest.questConfigData = new QuestConfigData[Quest.totalQuests];
		Quest.cachedQuestConfig = new QuestConfigData[Quest.totalQuests];

		for (int index = 0; index < Quest.totalQuests; index++) {
			Plugin.execute("configure_quest_" + index);
		}
		//Arrays.sort(questConfigData);
	}

}
