package nonsimulatedexperiment;

import common.StdOut;
import custom.corra.CORRAGraph;

public class NRBCableLength {
    public static void main(String[] args) {
        int delta = 3;
        int k = 3;
        int nTest = 5;

        int size = 8192;
        int xSize = 128;

        String type = "rsn";

        for (int i = 0; i < nTest; i++) {
            String prefix = "./data/" + type + "/" + size + "/" + (i + 1) +
                    "/random_" + size + "_nodes_" + xSize + "_xSize_r2";
            String fileEdge = prefix + ".edges";
            String fileGeo = prefix + ".geo";

            CORRAGraph graph = new CORRAGraph(size, xSize, fileEdge, fileGeo, delta, k);

            StdOut.println(graph.cableLength());
        }

    }
}