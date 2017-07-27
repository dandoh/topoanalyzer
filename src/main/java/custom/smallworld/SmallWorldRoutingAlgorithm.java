package custom.smallworld;

import routing.RoutingAlgorithm;
import routing.RoutingPath;

/**
 * Created by mta on 7/25/17.
 */
public class SmallWorldRoutingAlgorithm implements RoutingAlgorithm {

    public SmallWorldRoutingAlgorithm() {

    }

    @Override
    public int next(int source, int current, int destination) {
        return 0;
    }

    @Override
    public RoutingPath path(int source, int destination) {
        return null;
    }
}
