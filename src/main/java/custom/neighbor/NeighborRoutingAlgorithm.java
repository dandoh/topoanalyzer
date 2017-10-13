package custom.neighbor;

import common.StdOut;
import common.Tuple;
import network.RoutingTable;
import routing.RoutingAlgorithm;
import routing.RoutingPath;

import java.util.*;

public class NeighborRoutingAlgorithm implements RoutingAlgorithm {
    protected NeighborGraph graph;
    protected Map<Integer, NeighborTable> tables;

    public NeighborRoutingAlgorithm() {
    }

    public NeighborRoutingAlgorithm(NeighborGraph graph) {
        this.graph = graph;

        tables = new HashMap<>();
        for (int u : graph.switches()) {
            tables.put(u, new NeighborTable());
        }

        buildTables();
    }

    private void buildTables() {
        // Add self-info table
        for (int u : graph.switches()) {
            this.updateSelfTable(u);
        }

        // Receive bridges from neighborhood
        // Note: Must be happen after previous step
        for (int u : graph.switches()) {
            this.getBrFromNeighbor(u);
        }

        // Fill missing route to node
        int nMiss = 0;
        for (int u : graph.switches()) {
            NeighborTable table = tables.get(u);
            for (int v : graph.switches())
                if (u != v) {
                    boolean check = false;
                    for (Map.Entry<Integer, List<Integer>> entry : table.brTable.entrySet()) {
                        if (graph.isNeighbor(v, entry.getKey())) {
                            check = true;
                            break;
                        }
                    }
                    if (!check) {
                        nMiss++;

                        findShortestPath(u, v);
                    }
               }
        }
        StdOut.printf("Missing route: %d\n", nMiss);
    }

    protected void updateSelfTable(int u) {
        NeighborTable table = this.tables.get(u);
        table.setNeighborTable(this.neighborTable(u, graph.delta));

        // Find br1
        for (int w : graph.adj(u)) {
            if (graph.isSwitchVertex(w) && graph.isRandomLink(u, w)) {
                table.addBr1(w, w);
                table.addBrRoute(w, w, 1);

                // Find br2
                for (int k : graph.adj(w)) {
                    if (graph.isSwitchVertex(k) && k != u && graph.isRandomLink(w, k)) {
                        table.addBr2(k, w);
                        table.addBrRoute(k, w, 2);
                    }
                }
            }
        }
    }

    protected void getBrFromNeighbor(int u) {
        NeighborTable table = tables.get(u);
        // Receive bridges from u's neighbors
        for (Map.Entry<Integer, List<Integer>> entry : table.neighborTable.entrySet()) {
            int v = entry.getKey();
            List<Integer> info = entry.getValue();

            NeighborTable vTable = tables.get(v);
            // Receive br1 from v
            for (Map.Entry<Integer, Integer> brEntry : vTable.br1.entrySet()) {
                table.addBrRoute(brEntry.getKey(), info.get(0), info.get(1) + 1);
            }

            // Receive br2 from v
            for (Map.Entry<Integer,Integer> brEntry : vTable.br2.entrySet()) {
                table.addBrRoute(brEntry.getKey(), info.get(0), info.get(1) + 2);
            }
        }
    }

    @Override
    public int next(int source, int current, int destination) {
        if (graph.isHostVertex(current)) {
            return graph.adj(current).get(0);
        } if (graph.adj(current).contains(destination)) {
            return destination;
        }
        int desSwitch = graph.isHostVertex(destination) ? graph.adj(destination).get(0) : destination;

        NeighborTable table = tables.get(current);
        int nextNeighbor = table.getNextNeighborNode(desSwitch);
        if (nextNeighbor > -1) {
            return nextNeighbor;
        }

        nextNeighbor = this.getNextBrNode(current, desSwitch);

//        StdOut.printf("%d %d %d %d\n", source, destination, current, nextNeighbor);
        return nextNeighbor;
    }

    @Override
    public RoutingPath path(int source, int destination) {
        RoutingPath routingPath = new RoutingPath();

        int current = source;
        int count = 0;
        while (current != destination) {
            count++;
            if (count > 100) {
                NeighborTable table = tables.get(current);

                StdOut.printf("%d %d %d\n", current, source, destination);
                StdOut.println(table.getNextNeighborNode(destination));
                StdOut.println(this.getNextBrNode(current, destination));

                StdOut.println(routingPath.path);
                System.exit(1);
                break;
            }
            routingPath.path.add(source);
            current = this.next(source, current, destination);
            if (current == -1)
                return null;
        }
        routingPath.path.add(destination);

        return routingPath;
    }

    protected int getNextBrNode(int sourceSwitch, int desSwitch) {
        NeighborTable table = tables.get(sourceSwitch);
        int nextHop = -1;
        int minHop = Integer.MAX_VALUE;
        for (Map.Entry<Integer, List<Integer>> entry : table.brTable.entrySet()) {
            if (graph.isNeighbor(desSwitch, entry.getKey())) {
                if (entry.getValue().get(1) < minHop) {
                    minHop = entry.getValue().get(1);
                    nextHop = entry.getValue().get(0);
                }
            }
        }

        return nextHop;
    }

    private HashMap<Integer, List<Integer>> neighborTable(int source, int delta) {
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

    protected int findShortestPath(int source, int destination) {
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

                return path.get(1);
            }

            for (int v:graph.adj(u)) {
                if (!visited[v] && graph.isSwitchVertex(v)) {
                    visited[v] = true;
                    trace[v] = u;
                    queue.add(v);
                }
            }
        }
        return -1;
    }
}
