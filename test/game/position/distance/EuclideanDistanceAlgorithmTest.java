package game.position.distance;

import game.position.distance.impl.EuclideanDistanceAlgorithm;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jason MK on 2018-09-26 at 11:02 AM
 */
public class EuclideanDistanceAlgorithmTest {

    @Test
    public void assertDistanceIsEqual() {
        assert new EuclideanDistanceAlgorithm().distance(3202, 3201, 3200, 3200) == 2;
    }

}
