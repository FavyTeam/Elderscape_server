package game.content.quest;

public class QuestConfigData implements Comparable<QuestConfigData> {

	public int i, stages;

	public String name;

	public QuestConfigData(int i, String name, int stages) {
		this.i = i;
		this.name = name;
		this.stages = stages;
	}

	@Override
	public int compareTo(QuestConfigData other) {
		if (name != null || other.name != null) {
			return name.compareTo(other.name);
		}
		return 0;
	}
}
