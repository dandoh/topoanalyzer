package custom.spaceshuffle;

import routing.RoutingAlgorithm;
import routing.RoutingPath;

/**
 * Created by Dandoh on 6/27/17.
 */

public class SpaceShuffleRoutingAlgorithm implements RoutingAlgorithm {

    @Override
    public int next(int source, int current, int destination) {
        return 0;
    }

    @Override
    public RoutingPath path(int source, int destination) {
        return null;
    }
}
