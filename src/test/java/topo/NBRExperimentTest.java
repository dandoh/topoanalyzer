package topo;

import common.StdOut;
import custom.corra.BigCORRARoutingAlgorithm;
import custom.corra.CORRAGraph;
import custom.corra.CORRARoutingAlgorithm;
import org.junit.Test;

import static org.junit.Assert.*;

public class NBRExperimentTest {
    @Test
    public void checkBigGraph() throws Exception {
        int delta = 3;
        int k = 1;
        int nTest = 5;

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
            CORRAGraph graph = new CORRAGraph(size, xSize, fileEdge, fileGeo, delta, k);
            StdOut.printf("Done!\n");

            StdOut.printf("Build routing algorithm...");
            CORRARoutingAlgorithm ra1 = new CORRARoutingAlgorithm(graph);
            BigCORRARoutingAlgorithm ra2 = new BigCORRARoutingAlgorithm(graph);
            StdOut.printf("Done!\n");

            StdOut.printf("Calculating...");
            int nPair = 100;
            TopologyExperiment topo1 = new TopologyExperiment(graph, ra1);

            int diameter1 = topo1.diameter();
            double arpl1 = topo1.averagePathLength();

            TopologyExperiment topo2 = new TopologyExperiment(graph, ra2);

            int diameter2 = topo2.diameter();
            double arpl2 = topo2.averagePathLength();

//            StdOut.printf("%d %d\n", diameter1, diameter2);
            StdOut.printf("%.5f %.5f\n", arpl1, arpl2);
//            StdOut.println(ra1.path(0, 30).path);
//            StdOut.println(ra1.tables.get(0).brTable);
//            StdOut.println(ra2.tables.get(0).brTable);

//            assertTrue(diameter1 == diameter2);
            double arplChange = 100.0 * Math.abs(arpl1- arpl2) / Math.max(arpl1, arpl2);
            StdOut.println("Expect difference between arpl of two not too big");
            assertTrue(arplChange < 1);
        }

    }
}