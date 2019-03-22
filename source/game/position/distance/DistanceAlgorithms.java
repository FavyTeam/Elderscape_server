package game.position.distance;

import game.position.distance.impl.EuclideanDistanceAlgorithm;
import game.position.distance.impl.ManhattanDistanceAlgorithm;

/**
 * Created by Jason MK on 2018-09-26 at 11:14 AM
 */
public enum DistanceAlgorithms implements DistanceAlgorithm {
    MANHATTAN(new ManhattanDistanceAlgorithm()),
    EUCLIDEAN(new EuclideanDistanceAlgorithm());

    private final DistanceAlgorithm algorithm;

    DistanceAlgorithms(DistanceAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public int distance(int startX, int startY, int endX, int endY) {
        return algorithm.distance(startX, startY, endX, endY);
    }
}
