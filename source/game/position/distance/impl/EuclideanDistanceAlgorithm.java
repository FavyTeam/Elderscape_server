package game.position.distance.impl;

import game.position.distance.DistanceAlgorithm;

/**
 * Created by Jason MK on 2018-09-26 at 11:00 AM
 */
public class EuclideanDistanceAlgorithm implements DistanceAlgorithm {

    @Override
    public int distance(int startX, int startY, int endX, int endY) {
        return (int) Math.floor(Math.sqrt(Math.pow(Math.abs(startX - endX), 2) + Math.pow(Math.abs(startY - endY), 2)));
    }

}
