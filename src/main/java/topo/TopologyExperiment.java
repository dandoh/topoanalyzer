package topo;

import common.Tuple;
import graph.Graph;
import routing.RoutingAlgorithm;
import routing.RoutingPath;

import java.util.ArrayList;
import java.util.List;

public class TopologyExperiment {
    private Graph graph;
    private RoutingAlgorithm routingAlgorithm;
    private int diameter;
    private double arpl;

    public TopologyExperiment(Graph graph, RoutingAlgorithm routingAlgorithm) {
        this.graph = graph;
        this.routingAlgorithm = routingAlgorithm;
    }

    public void fullAnalysis() {
        List<Tuple<Integer, Integer>> pairs = new ArrayList<>();

        for (int u = 0; u < graph.V() - 1; u++)
            for (int v = u + 1; v < graph.V(); v++)
                pairs.add(new Tuple<>(u, v));

        int diameter = 0;
        int totalPath = 0;

        for (Tuple<Integer, Integer> pair : pairs) {
            List<Integer> path = routingAlgorithm.path(pair.a, pair.b).path;

            diameter = Math.max(diameter, path.size() - 1);
            totalPath += path.size() - 1;

//            double cableLength = graph.pathCableLength(packetTrace.trace);
//            totalCableLength += cableLength;
//            maxCableLength = Math.max(maxCableLength, cableLength);

//            double latency = cableLength * 5 + (packetTrace.trace.size() - 1) * 100;
//            totalLatency += latency;
//            maxLatency = Math.max(maxLatency, latency);

        }

        this.diameter = diameter;
        this.arpl = 1.0 * totalPath / pairs.size();
    }

    public int diameter() {
        return this.diameter;
    }

    public double averagePathLength() {
        return this.arpl;
    }
}
