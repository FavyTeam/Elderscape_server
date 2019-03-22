package game.player;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jason MK on 2018-07-25 at 11:55 AM
 */
public enum PlayerFaceDirection {
    NORTH(0, 0, 1),
    NORTH_WEST(14, -1, 1),
    NORTH_EAST(2, 1, 1),
    WEST(12, -1, 0),
    EAST(4, 1, 0),
    SOUTH(8, 0, -1),
    SOUTH_WEST(10, -1, -1),
    SOUTH_EAST(6, 1, -1)
    ;

    private final int value;

    private final int xOffset;

    private final int yOffset;

    PlayerFaceDirection(int value, int xOffset, int yOffset) {
        this.value = value;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public int getValue() {
        return value;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public static final Map<Integer, PlayerFaceDirection> DIRECTION_MAP = ImmutableMap.copyOf(
            Stream.of(PlayerFaceDirection.values()).collect(Collectors.toMap(PlayerFaceDirection::getValue, Functions.identity())));
}
