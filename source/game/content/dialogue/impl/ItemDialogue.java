package game.content.dialogue.impl;

import game.content.dialogue.Dialogue;
import game.player.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jason MK on 2018-07-31 at 10:36 AM
 */
public class ItemDialogue implements Dialogue {

    String header;

    private final List<String> lines;

    private final int item;

    private final int zoom;

    private final int xOffset;

    private final int yOffset;

    public ItemDialogue(String header, int item, int zoom, int xOffset, int yOffset, String... lines) {
        this.header = header;
        this.item = item;
        this.zoom = zoom;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.lines = Arrays.asList(lines);
    }

    @Override
    public void send(Player player) {
        final int frame = lines.size() == 1 ? 4885 : lines.size() == 2 ? 4890 : lines.size() == 3 ? 4896 : lines.size() == 4 ? 4903 : 0;

        final int header = frame - 1;

        final int itemFrame = frame - 2;

        if (frame == 0) {
            return;
        }
        player.getPA().sendMessage(":packet:senditemchat 4901 " + xOffset + " " + yOffset);
        player.getPA().sendFrame246(itemFrame, zoom, item);
        player.getPA().sendFrame126(this.header, header);
        for (int index = 0; index < lines.size(); index++) {
            player.getPA().sendFrame126(lines.get(index), frame + index);
        }
        player.getPA().sendFrame164(frame - 3);


    }
}
