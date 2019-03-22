package game.position.distance.impl;

import game.position.distance.DistanceAlgorithm;

/**
 * Created by Jason MK on 2018-09-26 at 10:59 AM
 */
public class ManhattanDistanceAlgorithm implements DistanceAlgorithm {

    @Override
    public int distance(int startX, int startY, int endX, int endY) {
        return Math.abs(startX - endX) + Math.abs(startY - endY);
    }

}
