package nonsimulatedexperiment;

import common.StdOut;
import custom.corra.CORRAGraph;
import custom.corra.CORRARoutingAlgorithm;
import custom.tz.TZRoutingAlgorithm;

import java.util.List;
import java.util.Map;

public class TZExperiment {
    public static void main(String[] args) {
        long totalTime = System.currentTimeMillis();

        int delta = 3;
        int k = 1;
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
            StdOut.printf("Loading network...");
            CORRAGraph graph = new CORRAGraph(size, xSize, fileEdge, fileGeo, delta, k);
            StdOut.printf("Done!\n");

//            for (int u : graph.switches()) {
//                Map<Integer, List<Integer>> paths = graph.shortestPaths(u);
//                StdOut.println(paths.get(0));
//            }

            StdOut.printf("Building routing algorithm\n");
            // Choose type of routing algorithm
            TZRoutingAlgorithm ra = new TZRoutingAlgorithm(graph);

            StdOut.printf("Done!\n");

            StdOut.printf("Calculating...\n");

            // Full pair analysis
//            TopologyExperiment topo = new TopologyExperiment(graph, ra);

//            Part analysis
            int nPair = 100;
            TopologyExperiment topo = new TopologyExperiment(graph, ra, nPair);

            StdOut.printf("Done!\n");

            StdOut.printf("Diameter: %d\n", topo.diameter());
            StdOut.printf("Average routing path length: %.3f\n", topo.averagePathLength());
            StdOut.printf("Average latency: %.5f\n", topo.getAvgLatency());
        }

        totalTime = System.currentTimeMillis() - totalTime;
        StdOut.printf("\nRuntime = %.3f\n", 1.0 * totalTime / 1000);
    }
}
