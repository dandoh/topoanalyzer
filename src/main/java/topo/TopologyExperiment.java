package topo;

import graph.Graph;
import routing.RoutingAlgorithm;

public abstract class TopologyExperiment {
    private Graph graph;
    private RoutingAlgorithm routingAlgorithm;

    public TopologyExperiment(Graph graph, RoutingAlgorithm routingAlgorithm) {
        this.graph = graph;
        this.routingAlgorithm = routingAlgorithm;
    }

    public int diameter() {
        return 0;
    }

    public double averagePathLength() {
        return 0;
    }
}
