package game.container;

import game.item.GameItem;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 1:18 PM
 */
public class ItemContainerCodecTest {

    public static void main(String[] args) {
        ItemContainer container = new ItemContainer(20, ItemContainerStackPolicy.STACKABLE, ItemContainerNotePolicy.PERMITTED,
                new GameItem[] {
                        new GameItem(4151, 5), new GameItem(4153, 3) });

        String encoded = new ItemContainerToStringEncoder().encode(container);

        ItemContainer decoded = new ItemContainerFromStringDecoder().decode(encoded);

        System.out.println("Container: " + container);
        System.out.println("---");
        System.out.println("Encoded: " + encoded);
        System.out.println("---");
        System.out.println("Decoded: " + decoded);
    }

}
