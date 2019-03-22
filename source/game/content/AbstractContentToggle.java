package game.content;

/**
 * Created by Jason MacKeigan on 2018-02-05 at 2:20 PM
 */
public class AbstractContentToggle implements ContentToggle {

	private ContentState state;

	public AbstractContentToggle(ContentState state) {
		this.state = state;
	}

	@Override
	public ContentState toggle() {
		return state = state == ContentState.ENABLED ? ContentState.DISABLED : ContentState.ENABLED;
	}
}
