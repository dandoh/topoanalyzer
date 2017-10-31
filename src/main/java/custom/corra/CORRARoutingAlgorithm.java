package custom.corra;

import common.StdOut;
import common.Tuple;
import routing.RoutingAlgorithm;
import routing.RoutingPath;

import java.util.*;

public class CORRARoutingAlgorithm implements RoutingAlgorithm {
    protected CORRAGraph graph;
    public Map<Integer, CORRATable> tables;

    public int nBr1 = 0;
    public int nBr2 = 0;
    public int nSTP = 0;
    protected int type = 0;

    public CORRARoutingAlgorithm() {
    }

    public CORRARoutingAlgorithm(CORRAGraph graph) {
        this.graph = graph;

        tables = new HashMap<>();
        for (int u : graph.switches()) {
            tables.put(u, new CORRATable());
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
            CORRATable table = tables.get(u);
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
        CORRATable table = this.tables.get(u);
        table.setNeighborTable(this.neighborTable(u));

        // Find br1
        for (int w : graph.adj(u)) {
            if (graph.isSwitchVertex(w) && graph.isRandomLink(u, w)) {
                table.addBr1(w, w);
                table.addBrRoute(w, w, 1, 1);

                // Find br2
                for (int k : graph.adj(w)) {
                    if (graph.isSwitchVertex(k) && k != u && graph.isRandomLink(w, k)) {
                        table.addBr2(k, w);
                        table.addBrRoute(k, w, 2, 2);
                    }
                }
            }
        }
    }

    protected void getBrFromNeighbor(int u) {
        CORRATable table = tables.get(u);
        // Receive bridges from u's neighbors
        for (Map.Entry<Integer, List<Integer>> entry : table.neighborTable.entrySet()) {
            int v = entry.getKey();
            List<Integer> info = entry.getValue();
            CORRATable vTable = tables.get(v);

            if (vTable.neighborTable.isEmpty()) {
                updateSelfTable(v);
            }

            // Receive br1 from v
            for (Map.Entry<Integer, Integer> brEntry : vTable.br1.entrySet()) {
                table.addBrRoute(brEntry.getKey(), info.get(0), info.get(1) + 1, 1);
            }

            // Receive br2 from v
            for (Map.Entry<Integer,Integer> brEntry : vTable.br2.entrySet()) {
                table.addBrRoute(brEntry.getKey(), info.get(0), info.get(1) + 2, 2);
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

        CORRATable table = tables.get(current);
        int nextNeighbor = table.getNextNeighborNode(desSwitch);
        if (nextNeighbor > -1) {
            return nextNeighbor;
        }

        Tuple<Integer, Integer> nextBr =  this.getNextBrNode(current, desSwitch);
        nextNeighbor = nextBr.a;
        if (this.type == 0) this.type = nextBr.b;

//        StdOut.printf("%d %d %d %d\n", source, destination, current, nextNeighbor);
        return nextNeighbor;
    }

    @Override
    public RoutingPath path(int source, int destination) {
        RoutingPath routingPath = new RoutingPath();

        type = 0;
        int current = source;
        int count = 0;
        while (current != destination) {
            count++;
            if (count > 100) {
                CORRATable table = tables.get(current);

                StdOut.printf("%d %d %d\n", current, source, destination);
                StdOut.println(table.getNextNeighborNode(destination));
                StdOut.println(this.getNextBrNode(current, destination));

                StdOut.println(routingPath.path);
                System.exit(1);
                break;
            }
            routingPath.path.add(current);
            current = this.next(source, current, destination);
            if (current == -1)
                return null;
        }
        routingPath.path.add(destination);
        updatePathByK(routingPath.path);

        switch (type) {
            case 1:
                nBr1++;
                break;
            case 2:
                nBr2++;
                break;
            case 3:
                nSTP++;
                break;
        }
        return routingPath;
    }

    protected Tuple<Integer, Integer> getNextBrNode(int sourceSwitch, int desSwitch) {
        CORRATable table = tables.get(sourceSwitch);
        int nextHop = -1;
        int minHop = Integer.MAX_VALUE;
        int type = 0;
        for (Map.Entry<Integer, List<Integer>> entry : table.brTable.entrySet()) {
            if (graph.isNeighbor(desSwitch, entry.getKey())) {
                if (entry.getValue().get(1) < minHop) {
                    minHop = entry.getValue().get(1);
                    nextHop = entry.getValue().get(0);
                    type = entry.getValue().get(2);
                }
            }
        }

        return new Tuple<>(nextHop, type);
    }

    public void updatePathByK(List<Integer> path) {
        // Update link at the begin of path
        int lastIndex = 0;
        for (int i = 1; i < path.size(); i++) {
            if (graph.isRandomLink(path.get(i - 1), path.get(i)) || graph.manhattanDistance(path.get(0), path.get(i)) > graph.k) {
                break;
            }
            lastIndex = i;
        }

        if (lastIndex > 0) {
            for (int i = lastIndex - 1; i >= 1; i--) {
                path.remove(i);
            }
        }

        int last = path.size() - 1;
        int firstIndex = last;
        for (int i = last - 1; i >= 0; i--) {
            if (graph.isRandomLink(path.get(i), path.get(i + 1)) || graph.manhattanDistance(path.get(last), path.get(i)) > graph.k) {
                break;
            }
            firstIndex = i;
        }

        if (firstIndex < last) {
            for (int i = last - 1; i >= firstIndex + 1; i--)
                path.remove(i);
        }
    }

    private HashMap<Integer, List<Integer>> neighborTable(int source) {
        return graph.neighborTable(source);
    }

    protected int findShortestPath(int source, int destination) {
        Queue<Integer> queue = new LinkedList<Integer>();
        List<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[graph.V()];
        int[] trace = new int[graph.V()];
        queue.add(source);
        visited[source] = true;
        trace[source] = -1;
        while (!queue.isEmpty()) {
            int u = queue.remove();
            if (u == destination) {
                path.add(u);
                while (trace[u] != -1) {
                    u = trace[u];
                    path.add(0, u);
                    tables.get(u).addBrRoute(destination, path.get(1), path.size() - 1, 3);
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

    public long totalRTS() {
        long total = 0;
        for (CORRATable table : this.tables.values()) {
            total += table.size();
        }
        return total;
    }

    public double avgRTS() {
        return 1.0 * this.totalRTS() / this.tables.size();
    }
}
