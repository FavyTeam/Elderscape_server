package game.content.quest;

public final class QuestReward {

	public String[] rewards = new String[6];

	public QuestReward(String reward, String reward1) {
		rewards[0] = reward;
		rewards[1] = reward1;
	}

	public QuestReward(String reward, String reward1, String reward2) {
		rewards[0] = reward;
		rewards[1] = reward1;
		rewards[2] = reward2;
	}

	public QuestReward(String reward, String reward1, String reward2, String reward3) {
		rewards[0] = reward;
		rewards[1] = reward1;
		rewards[2] = reward2;
		rewards[3] = reward3;
	}

	public QuestReward(String reward, String reward1, String reward2, String reward3, String reward4) {
		rewards[0] = reward;
		rewards[1] = reward1;
		rewards[2] = reward2;
		rewards[3] = reward3;
		rewards[4] = reward4;
	}

	public QuestReward(String reward, String reward1, String reward2, String reward3, String reward4, String reward5) {
		rewards[0] = reward;
		rewards[1] = reward1;
		rewards[2] = reward2;
		rewards[3] = reward3;
		rewards[4] = reward4;
		rewards[5] = reward5;
	}
}

