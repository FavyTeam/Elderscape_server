package game.content;

/**
 * Created by Jason MacKeigan on 2018-02-05 at 2:18 PM
 */
public interface ContentToggle {

	/**
	 * Should effectively return the toggle, potentially changing the
	 * toggle if the underlying implementation allows for it.
	 *
	 * @return the state of the content.
	 */
	ContentState toggle();

}
