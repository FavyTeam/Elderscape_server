package game.container;

import com.google.gson.Gson;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 1:08 PM
 */
public class ItemContainerToStringEncoder {

	public String encode(ItemContainer container) {
		return new Gson().toJson(container);
	}

}
