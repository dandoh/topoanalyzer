package custom.corra;

import common.StdOut;
import common.Tuple;

import java.util.*;

public class CORRAKGraph extends CORRAGraph {
    public CORRAKGraph(int nRow, int nCol, String baseType, int k) {
        super(nRow, nCol, baseType, 3);

        this.k = k;

        initKNeighborEdges();
    }

    private void initKNeighborEdges() {
        if (k == 1) return;

        for (int sid : this.switches()) {
            addNeighborEdges(sid);
        }
    }

    private void addNeighborEdges(int source) {
        Queue<Tuple<Integer, Integer>> queue = new LinkedList<>();
        boolean[] visited = new boolean[V];
        queue.add(new Tuple<>(source, 0));
        visited[source] = true;

        while(!queue.isEmpty()) {
            Tuple next = queue.remove();
            int u = (int) next.a;
            int hop = (int) next.b;


            if (hop > 1 && !hasEdge(source, u)) {
                addEdge(source, u);
            }

            if (hop == k) continue;

            for (int v : adj(u)) {
                if (isSwitchVertex(v) && !visited[v] && !isRandomLink(u, v)) {
                    visited[v] = true;
                    queue.add(new Tuple<>(v, hop + 1));
                }
            }
        }
    }

    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        String baseType = "grid"; //args[0];
        String randomLinkType = "fixed"; //args[1];
        int nRow = 32; //Integer.parseInt(args[2]);
        int nCol = 32; //Integer.parseInt(args[3]);
        int nAlpha = 2; //Integer.parseInt(args[4]);

        double[] alphas = new double[nAlpha];
        for (int i = 0; i < nAlpha; i++) {
            alphas[i] = 0; //Double.parseDouble(args[i + 5]);
        }

        int k = 2;
        CORRAKGraph graph = new CORRAKGraph(nRow, nCol, baseType, k);

        StdOut.println(graph);

        time = System.currentTimeMillis() - time;
        StdOut.println(String.format("Runtime = %f\n", 1.0 * time / 1000));

        String fileName = "random_" + graph.switches().size() + "_nodes_" +
                            graph.getnCol() + "_xSize_r" + nAlpha +
                            "_k" + k;
        graph.writeFileGeos(fileName + ".geos");
        graph.writeFileEdges(fileName + ".edges");

    }

}
