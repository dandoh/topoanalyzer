package topo;

import common.StdOut;
import custom.neighbor.NeighborGraph;
import custom.neighbor.NeighborRoutingAlgorithm;

public class NBRExperiment {
    public static void main(String[] args) {
        long totalTime = System.currentTimeMillis();

        int delta = 3;
        int k = 1;
        int nTest = 1;

        int size = 1024;
        int xSize = 32;

        String type = "rsn";

        StdOut.printf("Experiment topo with %d nodes, delta = %d\n", size, delta);

        for (int i = 0; i < nTest; i++) {
            StdOut.printf("Test %d\n", i + 1);
            String prefix = "./data/" + type + "/" + size + "/" + (i + 1) +
                    "/random_" + size + "_nodes_" + xSize + "_xSize_r2";
            String fileEdge = prefix + ".edges";
            String fileGeo = prefix + ".geo";
            StdOut.printf("Loading network...");
            NeighborGraph graph = new NeighborGraph(size, xSize, fileEdge, fileGeo, delta, k);
            StdOut.printf("Done!\n");

            StdOut.printf("Build routing algorithm...");
            NeighborRoutingAlgorithm ra = new NeighborRoutingAlgorithm(graph);
            StdOut.printf("Done!\n");

            StdOut.printf("Calculating...");
            TopologyExperiment topo = new TopologyExperiment(graph, ra);
            topo.fullAnalysis();
            StdOut.printf("Done!\n");

            StdOut.printf("Diameter: %d\n", topo.diameter());
            StdOut.printf("Average routing path length: %.3f\n", topo.averagePathLength());
        }

        totalTime = System.currentTimeMillis() - totalTime;
        StdOut.printf("\nRuntime = %.3f\n", 1.0 * totalTime / 1000);
    }
}
