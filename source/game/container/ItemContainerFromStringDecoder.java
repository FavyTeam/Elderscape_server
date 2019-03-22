package game.container;

import com.google.gson.Gson;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 1:15 PM
 */
public class ItemContainerFromStringDecoder {

	public ItemContainer decode(String input) {
		return new Gson().fromJson(input, ItemContainer.class);
	}

}
