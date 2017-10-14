package custom.neighbor;

import common.Tuple;
import custom.smallworld.SmallWorldGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class NeighborGraph extends SmallWorldGraph {
    public int delta;
    public int k;

    public NeighborGraph(int nRow, int nCol, String baseType, int delta) {
        super(nRow, nCol, baseType, new double[]{0, 0});

        this.k = 1;
        this.delta = delta;
    }

    public NeighborGraph(int size, int nCol, String fileEdge, String fileGeo, int delta, int k) {
        super("grid", size / nCol, nCol);

        this.delta = delta;
        this.k = k;
        try (Stream<String> stream = Files.lines(Paths.get(fileEdge))) {
            stream.skip(1).forEach(line -> {
                if (line.split(" ").length > 2) return;

                int u = Integer.parseInt(line.split(" ")[0]);
                int v = Integer.parseInt(line.split(" ")[1]);

                if (!this.hasEdge(u, v)) {
                    this.addEdge(u, v);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRandomLink(int u, int v) {
        if (!isSwitchVertex(u) || !isSwitchVertex(v))
            return false;

        return !(Math.abs(u - v) == 1 ) && !(Math.abs(u -v) == nCol);
    }

    public boolean isNeighbor(int u, int v) {
        if (!isSwitchVertex(u) || !isSwitchVertex(v))
            return false;

        return manhattanDistance(u, v) <= delta;
    }

    public double cableLength() {
        double totalLength = totalCableLength();
        if (k == 1)
            return totalLength;

        for (int u : switches()) {
            HashMap<Integer, List<Integer>> neighbor = neighborTable(u);
            for (int v : neighbor.keySet())
                if (!hasEdge(u, v) && manhattanDistance(u, v) <= k)
                    totalLength += euclidDistance(u, v);
        }

        return totalLength;
    }

    public HashMap<Integer, List<Integer>> neighborTable(int source) {
        HashMap<Integer, List<Integer>> table = new HashMap<>();

        Queue<Tuple<Integer, Integer>> queue = new LinkedList<>();
        boolean[] visited = new boolean[V];
        int[] trace = new int[V];

        queue.add(new Tuple<>(source, 0));
        visited[source] = true;
        trace[source] = -1;
        while(!queue.isEmpty()) {
            Tuple next = queue.remove();
            int uNode = (int) next.a;
            int hop = (int) next.b;
            if (hop > 0 && hop <= delta) {
                int v = uNode;
                while(trace[v] != source) {
                    v = trace[v];
                }
                table.put(uNode, Arrays.asList(v, hop));
            }

            if (hop == delta) continue;

            for (int vNode : adj(uNode)) {
                if (isSwitchVertex(vNode) && !visited[vNode] && !isRandomLink(uNode, vNode)) {//graph.manhattanDistance(source, vNode) <= delta) {
                    visited[vNode] = true;
                    trace[vNode] = uNode;
                    queue.add(new Tuple<>(vNode, hop + 1));
                }
            }
        }
        return table;
    }
}
