package custom.neighbor;

import common.StdOut;
import common.Tuple;
import routing.RoutingAlgorithm;
import routing.RoutingPath;

import java.util.*;

public class BigNBRRoutingAlgorithm extends NeighborRoutingAlgorithm {

    public BigNBRRoutingAlgorithm(NeighborGraph graph) {
        super();
        this.graph = graph;

        tables = new HashMap<>();
        for (int u : graph.switches()) {
            tables.put(u, new NeighborTable());
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
        // Initial neighbor
        if (table.neighborTable.isEmpty()) {
            this.updateSelfTable(current);
        }

        int nextNeighbor = table.getNextNeighborNode(desSwitch);
        if (nextNeighbor > -1) {
            return nextNeighbor;
        }

        // Receive bridges from neighborhood if does not
        if (table.brTable.size() == table.br1.size() + table.br2.size()) {
            // Receive Br from u's neighbors
            this.getBrFromNeighbor(current);
        }

        nextNeighbor = this.getNextBrNode(current, desSwitch);

        if (nextNeighbor > -1)
            return nextNeighbor;

        return this.findShortestPath(current, destination);
    }

    @Override
    public RoutingPath path(int source, int destination) {
        return null;
    }
}
