package routing;

import common.StdOut;
import graph.Graph;
import network.RoutingTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dandoh on 6/27/17.
 */
public class ShortestPathRoutingAlgorithm implements RoutingAlgorithm {

    private Graph G;
    private Map<Integer, Map<Integer, Integer>> paths;

    public ShortestPathRoutingAlgorithm(Graph G) {
        this.G = G;

        paths = new HashMap<>();
        for (int u : G.switches()) {
            paths.put(u, new HashMap<>());
        }
    }

    @Override
    public int next(int source, int current, int destination) {
        if (G.isHostVertex(current)) {
            return G.adj(current).get(0);
        } if (G.adj(current).contains(destination)) {
            return destination;
        }
        int desSwitch = G.isHostVertex(destination) ? G.adj(destination).get(0) : destination;

        if (paths.get(current).containsKey(desSwitch))
            return paths.get(current).get(desSwitch);

        List<Integer> path = G.shortestPath(current, desSwitch);
        for (int i = 0; i < path.size() - 1; i++) {
            paths.get(path.get(i)).put(desSwitch, path.get(i + 1));
        }
        return path.get(1);
    }

    @Override
    public RoutingPath path(int source, int destination) {
        RoutingPath routingPath = new RoutingPath();

        int current = source;
        while (current != destination) {
            routingPath.path.add(source);
            current = this.next(source, current, destination);
            if (current == -1)
                return null;
        }
        routingPath.path.add(destination);

        return routingPath;
    }
}
