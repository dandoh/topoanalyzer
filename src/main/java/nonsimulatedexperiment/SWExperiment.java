package nonsimulatedexperiment;

import common.StdOut;
import custom.smallworld.SmallWorldGraph;
import routing.RoutingAlgorithm;
import routing.ShortestPathRoutingAlgorithm;

public class SWExperiment {
    public static void main(String[] args) {
        String baseType = "torus";
        String randomLinkType = "fixed";
        int nRow = 32;
        int nCol = 32;
        int nAlpha = 2;

        double[] alphas = new double[nAlpha];
        for (int i = 0; i < nAlpha; i++) {
            alphas[i] = 1.6;
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

        RoutingAlgorithm ra = new ShortestPathRoutingAlgorithm(graph);
        TopologyExperiment topo = new TopologyExperiment(graph, ra);

        StdOut.printf("Diameter: %d\n", topo.diameter());
        StdOut.printf("Average routing path length: %.3f\n", topo.averagePathLength());
    }
}
