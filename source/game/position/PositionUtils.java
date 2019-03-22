package game.position;

import game.npc.Npc;
import game.npc.data.NpcDefinition;
import game.position.Position;
import game.position.distance.DistanceAlgorithm;
import game.position.distance.DistanceAlgorithms;

import java.util.*;

/**
 * Created by Jason MK on 2018-06-28 at 4:43 PM
 */
public final class PositionUtils {

    private PositionUtils() {
        throw new AssertionError("Cannot create static-factory method class.");
    }

    public static Position toLocal(Position position) {
        return new Position(position.getLocalX(), position.getLocalY());
    }

    public static Position toAbsolute(int regionX, int regionY, Position local) {
        return new Position(regionX * 8 + local.getX(), regionY * 8 + local.getY(), local.getZ());
    }

    public static Set<Position> rectangle(int x, int y, int z, int width, int height) {
        Set<Position> positions = new HashSet<>();

        for (int xOffset = x; xOffset < x + width; xOffset++) {
            for (int yOffset = y; yOffset < y + height; yOffset++) {
                positions.add(new Position(xOffset, yOffset, z));
            }
        }

        return positions;
    }

    public static Position randomOrNull(Collection<Position> positions) {
        return randomOr(positions, null);
    }

    public static Position randomOr(Collection<Position> positions, Position defaultValue) {
        List<Position> list = new ArrayList<>(positions);

        Collections.shuffle(list);

        return positions.stream().findAny().orElse(defaultValue);
    }

    public static boolean withinDistance(int firstX, int firstY, int secondX, int secondY, int width, int height, int distance, DistanceAlgorithm algorithm) {
        for (int x = firstX; x <= firstX + width; x++) {
            for (int y = firstY; y <= firstY + height; y++) {
                if (algorithm.distance(x, y, secondX, secondY) <= distance) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean withinDistance(Position first, Position second, int width, int height, int distance, DistanceAlgorithm algorithm) {
        return withinDistance(first.getX(), first.getY(), second.getX(), second.getY(), width, height, distance, algorithm);
    }

    public static boolean withinDistance(Npc npc, Position position, int distance, DistanceAlgorithm distanceAlgorithm) {
        NpcDefinition definition = npc.getDefinition();

        if (definition == null) {
            throw new IllegalStateException("Definition of npc is null, cannot check if within distance.");
        }
        return withinDistance(new Position(npc), position, definition.size, definition.size, distance, distanceAlgorithm);
    }

}
