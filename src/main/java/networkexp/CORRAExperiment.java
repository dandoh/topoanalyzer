package networkexp;

import common.Knuth;
import common.StdOut;
import custom.corra.CORRAGraph;
import custom.corra.CORRARoutingAlgorithm;
import network.Network;
import routing.RoutingAlgorithm;
import routing.ShortestPathRoutingAlgorithm;

import java.util.*;

public class CORRAExperiment {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        int delta = 5;
        int k = 0;
        int nTest = 1;

        int size = 4096;
        int xSize = 64;

        String type = "rsn";

        StdOut.printf("Experiment topo with %d nodes, delta = %d\n", size, delta);

        for (int i = 0; i < nTest; i++) {
            StdOut.printf("Test %d\n", i + 1);
            String prefix = "./data/" + type + "/" + size + "/" + (i + 1) +
                    "/random_" + size + "_nodes_" + xSize + "_xSize_r2";
            String fileEdge = prefix + ".edges";
            String fileGeo = prefix + ".geo";
            StdOut.printf("Loading network\n");
            CORRAGraph graph = new CORRAGraph(size, xSize, fileEdge, fileGeo, delta, k);
            RoutingAlgorithm ra = new CORRARoutingAlgorithm(graph);
            Network network = new Network(graph, ra);
            StdOut.println("Contructing done!");

            ThroughputExperiment experiment = new ThroughputExperiment(network);

            Integer[] hosts = graph.hosts().toArray(new Integer[0]);

            Knuth.shuffle(hosts);

            List<Integer> sources = new ArrayList<>();
            List<Integer> destination = new ArrayList<>();
            sources.addAll(Arrays.asList(hosts).subList(0, hosts.length / 4));
            destination.addAll(Arrays.asList(hosts).subList(3 * hosts.length / 4, hosts.length));

            Map<Integer, Integer> traffic = new HashMap<>();
            for (int u = 0; u < sources.size(); u++) {
                traffic.put(sources.get(u), destination.get(u));
            }

            double threshold = 1.1;
            StdOut.printf("Thresh hold = %.2f\n", threshold);
            long throughput = experiment.evaluateThroughput(traffic, threshold, false);
            //        StdOut.printf("Maximum frequency = %d\n", maxFrequency);
            StdOut.printf("\nThrough put of network %dGb/s\n", throughput / 1000000);

            // Shortest path
            StdOut.printf("\n\n\nShortest path:\n");
            ra = new ShortestPathRoutingAlgorithm(graph);
            network = new Network(graph, ra);

            experiment = new ThroughputExperiment(network);

            threshold = 1.1;
            StdOut.printf("Thresh hold = %.2f\n", threshold);
            throughput = experiment.evaluateThroughput(traffic, threshold, false);
            //        StdOut.printf("Maximum frequency = %d\n", maxFrequency);
            StdOut.printf("\nThrough put of network %dGb/s\n", throughput / 1000000);


        }
    }
}
