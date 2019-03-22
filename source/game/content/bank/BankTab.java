package game.content.bank;

/**
 * Created by Jason MacKeigan on 2018-03-19 at 1:52 PM
 */
public enum BankTab {
	FIRST(5382),

	SECOND(35_000),

	THIRD(35_001),

	FOURTH(35_002),

	FIFTH(35_003),

	SIXTH(35_004),

	SEVENTH(35_005),

	EIGHTH(35_006);

	private final int componentId;

	BankTab(int componentId) {
		this.componentId = componentId;
	}

	public int getComponentId() {
		return componentId;
	}
}
