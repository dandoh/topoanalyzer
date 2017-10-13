package custom.neighbor;

import common.StdOut;
import common.Tuple;
import routing.RoutingAlgorithm;
import routing.RoutingPath;

import java.util.*;

public class BigNBRRoutingAlgorithm implements RoutingAlgorithm {
    private NeighborGraph G;
    private Map<Integer, NeighborTable> tables;

    public BigNBRRoutingAlgorithm(NeighborGraph G) {
        this.G = G;
        this.buildTables();
    }

    private void buildTables() {
        tables = new HashMap<>();

        for (int u : G.switches()) {
            NeighborTable table = new NeighborTable();
            tables.put(u, table);
        }
    }

    @Override
    public int next(int source, int current, int destination) {
        if (G.isHostVertex(current)) {
            return G.adj(current).get(0);
        } if (G.adj(current).contains(destination)) {
            return destination;
        }
        int desSwitch = G.isHostVertex(destination) ? G.adj(destination).get(0) : destination;

        NeighborTable table = tables.get(current);
        int nextNeighbor = table.getNextNeighborNode(desSwitch);
        if (nextNeighbor > -1) {
            return nextNeighbor;
        }

        int minHop = Integer.MAX_VALUE;
        for (Map.Entry<Integer, List<Integer>> entry : table.brTable.entrySet()) {
            if (G.isNeighbor(desSwitch, entry.getKey())) {
                if (entry.getValue().get(1) < minHop) {
                    minHop = entry.getValue().get(1);
                    nextNeighbor = entry.getValue().get(0);
                }
            }
        }

//        StdOut.printf("%d %d %d %d\n", source, destination, current, nextNeighbor);
        return nextNeighbor;
    }

    @Override
    public RoutingPath path(int source, int destination) {
        return null;
    }

    public static HashMap<Integer, List<Integer>> neighborTable(NeighborGraph graph, int source, int delta) {
        HashMap<Integer, List<Integer>> table = new HashMap<>();

        Queue<Tuple<Integer, Integer>> queue = new LinkedList<>();
        boolean[] visited = new boolean[graph.V()];
        int[] trace = new int[graph.V()];

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

            for (int vNode : graph.adj(uNode)) {
                if (graph.isSwitchVertex(vNode) && !visited[vNode] && !graph.isRandomLink(uNode, vNode)) {//graph.manhattanDistance(source, vNode) <= delta) {
                    visited[vNode] = true;
                    trace[vNode] = uNode;
                    queue.add(new Tuple<>(vNode, hop + 1));
                }
            }
        }
        return table;
    }

    private void findShortestPath(Map<Integer, NeighborTable> tables, NeighborGraph graph, int source, int destination) {
        Queue<Integer> queue = new LinkedList<Integer>();
        List<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[graph.V()];
        int[] trace = new int[graph.V()];
        queue.add(source);
        visited[source] = true;
        trace[source] = -1;
        while(!queue.isEmpty()) {
            int u = queue.remove();
            if (u == destination) {
                path.add(u);
                while(trace[u] != -1) {
                    u = trace[u];
                    path.add(0, u);

                    tables.get(u).addBrRoute(destination, path.get(1), path.size() - 1);
                }

                break;
            }

            for (int v:graph.adj(u)) {
                if (!visited[v] && G.isSwitchVertex(v)) {
                    visited[v] = true;
                    trace[v] = u;
                    queue.add(v);
                }
            }
        }
    }
}
