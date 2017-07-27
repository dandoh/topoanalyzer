package custom.full;

import routing.RoutingAlgorithm;
import routing.RoutingPath;

/**
 * Created by mta on 7/25/17.
 */
public class FullRoutingAlgorithm implements RoutingAlgorithm {

    private FullGraph graph;

    public FullRoutingAlgorithm(FullGraph graph) {
        this.graph = graph;
    }

    @Override
    public int next(int source, int current, int destination) {
        if (graph.isHostVertex(current)) {
            return graph.adj(current).get(0);
        } else if (graph.adj(current).contains(destination)) {
            return destination;
        } else {
            for (int i : graph.adj(current)) {
                if (graph.isSwitchVertex(i) && graph.adj(i).contains(destination)) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public RoutingPath path(int source, int destination) {
        return null;
    }
}
