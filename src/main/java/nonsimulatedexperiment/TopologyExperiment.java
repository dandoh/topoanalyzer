package nonsimulatedexperiment;

import common.StdOut;
import common.Tuple;
import custom.corra.CORRARoutingAlgorithm;
import graph.Graph;
import routing.RoutingAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TopologyExperiment {
    private Graph graph;
    private RoutingAlgorithm routingAlgorithm;
    private boolean isFull;

    private int diameter;
    private double arpl;
    private double totalLatency;
    private double avgLatency;

    public TopologyExperiment(Graph graph, RoutingAlgorithm routingAlgorithm) {
        this.graph = graph;
        this.routingAlgorithm = routingAlgorithm;

        this.isFull = true;
        fullAnalysis();
    }

    public TopologyExperiment(Graph graph, RoutingAlgorithm routingAlgorithm, int nPair) {
        this.graph = graph;
        this.routingAlgorithm = routingAlgorithm;

        this.isFull = false;
        partAnalysis(nPair);
    }

    private void fullAnalysis() {
        List<Tuple<Integer, Integer>> pairs = new ArrayList<>();

        for (int u : graph.switches())
            for (int v : graph.switches())
                if (u < v) {
                    pairs.add(new Tuple<>(u, v));
                }

        analysis(pairs);
    }

    private void partAnalysis(int nPair) {
        List<Tuple<Integer, Integer>> pairs = new ArrayList<>();

        List<Integer> switches = graph.switches();
        Random rand = new Random();

        while (pairs.size() < nPair) {
            int u = switches.get(rand.nextInt(switches.size()));
            int v = switches.get(rand.nextInt(switches.size()));

            int count = 0;
            while (count < 100 && graph.hasEdge(u, v)) {
                v = switches.get(rand.nextInt(switches.size()));
                count++;
            }

            if (!pairs.contains(new Tuple<>(u, v)))
                pairs.add(new Tuple<>(u, v));
        }

        analysis(pairs);
    }

    private void analysis(List<Tuple<Integer, Integer>> pairs) {
        int diameter = 0;
        int totalPath = 0;
        double totalLatency = 0;

        for (Tuple<Integer, Integer> pair : pairs) {
//            StdOut.printf("%d %d\n", pair.a, pair.b);
            List<Integer> path = routingAlgorithm.path(pair.a, pair.b).path;
//            StdOut.println(path);

            diameter = Math.max(diameter, path.size() - 1);
            totalPath += path.size() - 1;

            double cableLength = graph.pathCableLength(path);
//            totalCableLength += cableLength;
//            maxCableLength = Math.max(maxCableLength, cableLength);

            double latency = cableLength * 5 + (path.size() - 1) * 100;
            totalLatency += latency;

        }

        this.diameter = diameter;
        this.arpl = 1.0 * totalPath / pairs.size();
        this.totalLatency = totalLatency;
        this.avgLatency = totalLatency / pairs.size();
    }

    public int diameter() {
        return this.diameter;
    }

    public double averagePathLength() {
        return this.arpl;
    }

    public double getAvgLatency() {
        return avgLatency;
    }

    public double getTotalLatency() {
        return totalLatency;
    }

    public static double getAvgRTS(CORRARoutingAlgorithm ra) {
        return ra.avgRTS();
    }
}
