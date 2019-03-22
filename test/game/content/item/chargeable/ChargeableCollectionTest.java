package game.content.item.chargeable;

import game.item.GameItem;
//import org.junit.Test;

import java.util.*;

/**
 * Created by Jason MK on 2018-09-20 at 12:16 PM
 */
public class ChargeableCollectionTest {

 //   @Test
    public void assertDecrement() {
        Map<Integer, Collection<GameItem>> map = new HashMap<>();

        map.put(Chargeable.CRAWS_BOW.getId(), new ArrayList<>(Chargeable.CRAWS_BOW.getRequiredResources()));

        ChargeableCollection collection = new ChargeableCollection(map);

        GameItem item = Chargeable.CRAWS_BOW.getRequiredResources().stream().findAny().orElse(null);

        if (item == null) {
            throw new RuntimeException("Item is null.");
        }
        collection.decrease(Chargeable.CRAWS_BOW, new GameItem(item.getId(), 5));

        Collection<GameItem> resources = collection.getCharges(Chargeable.CRAWS_BOW);

        GameItem result = resources.stream().filter(i -> i.getId() == item.getId()).findAny().orElse(null);

        if (result == null) {
            throw new RuntimeException("Item result is null.");
        }
        assert result.getAmount() == item.getAmount() - 5;
    }

}
