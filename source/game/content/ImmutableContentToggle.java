package game.content;

/**
 * Created by Jason MacKeigan on 2018-02-05 at 2:23 PM
 */
public class ImmutableContentToggle implements ContentToggle {

	private final ContentState state;

	public ImmutableContentToggle(ContentState state) {
		this.state = state;
	}

	@Override
	public ContentState toggle() {
		return state;
	}
}
