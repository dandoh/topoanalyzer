package networkexp;

import common.Knuth;
import common.StdOut;
import custom.smallworld.SmallWorldGraph;
import custom.smallworld.SmallWorldRoutingAlgorithm;
import network.Network;
import routing.RoutingAlgorithm;

import java.util.*;

public class SmallWorldExperiment {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        String baseType = "torus"; //args[0];
        String randomLinkType = "fixed"; //args[1];
        int nRow = 32; //Integer.parseInt(args[2]);
        int nCol = 32; //Integer.parseInt(args[3]);
        int nAlpha = 2; //Integer.parseInt(args[4]);

        double[] alphas = new double[nAlpha];
        for (int i = 0; i < nAlpha; i++) {
            alphas[i] = 1.6; //Double.parseDouble(args[i + 5]);
        }

        boolean isBounded = false;
        if (randomLinkType.equals("varied"))
            isBounded = false; //Boolean.parseBoolean(args[nAlpha + 5]);

        SmallWorldGraph graph;
        if (randomLinkType.equals("fixed")) {
            // fixed degree
            graph = new SmallWorldGraph(nRow, nCol, baseType, alphas);
        } else {
            // varied degree
            graph = new SmallWorldGraph(nRow, nCol, baseType, alphas, isBounded);
        }

        RoutingAlgorithm ra = new SmallWorldRoutingAlgorithm(graph);
        Network network = new Network(graph, ra);

        ThroughputExperiment experiment = new ThroughputExperiment(network);

        Integer[] hosts = graph.hosts().toArray(new Integer[0]);

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
