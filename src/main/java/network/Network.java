package network;

import graph.Graph;
import routing.RoutingAlgorithm;

import java.util.List;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Network {
    private Graph graph;
    private List<Host> hosts;
    private List<Switch> switches;
    private RoutingAlgorithm routingAlgorithm;

    public Network(Graph graph, RoutingAlgorithm routingAlgorithm) {
        this.graph = graph;
        // construct hosts, switches and links and routing algorithm
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public List<Switch> getSwitches() {
        return switches;
    }

    public void clear() {

    }
}
