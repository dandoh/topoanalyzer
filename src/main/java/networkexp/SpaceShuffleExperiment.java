package networkexp;

import common.Knuth;
import common.StdOut;
import custom.spaceshuffle.SpaceShuffleGraph;
import custom.spaceshuffle.SpaceShuffleRoutingAlgorithm;
import network.Network;

import java.util.*;

public class SpaceShuffleExperiment {
    public static void main(String[] args) {
        int V = 200;
        int d = 6;
        SpaceShuffleGraph G = new SpaceShuffleGraph(V, d);
        SpaceShuffleRoutingAlgorithm ra = new SpaceShuffleRoutingAlgorithm(G);
        Network network = new Network(G, ra);

        ThroughputExperiment experiment = new ThroughputExperiment(network);

        Integer[] hosts = G.hosts().toArray(new Integer[0]);

        Knuth.shuffle(hosts);

        List<Integer> sources = new ArrayList<>();
        List<Integer> destination = new ArrayList<>();
        sources.addAll(Arrays.asList(hosts).subList(0, hosts.length / 2));
        destination.addAll(Arrays.asList(hosts).subList(hosts.length / 2, hosts.length));

        Map<Integer, Integer> traffic = new HashMap<>();
        for (int i = 0; i < sources.size(); i++) {
            traffic.put(sources.get(i), destination.get(i));
        }
//        traffic.put(2, 17);
//        traffic.put(3, 17);

//        StdOut.println(G.hosts().size());
        double threshold = 1.1;
        StdOut.printf("Thresh hold = %.2f\n", threshold);
        long throughput = experiment.evaluateThroughput(traffic, threshold, false);
//        StdOut.printf("Maximum frequency = %d\n", maxFrequency);
        StdOut.printf("\nThrough put of network %dGb/s\n", throughput / 1000000);

    }
}
