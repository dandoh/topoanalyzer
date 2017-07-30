package custom.smallworld;

import common.StdOut;
import network.Node;
import network.RoutingTable;
import routing.RoutingAlgorithm;
import routing.RoutingPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mta on 7/25/17.
 */
public class SmallWorldRoutingAlgorithm implements RoutingAlgorithm {
    private SmallWorldGraph G;
    private Map<Integer, RoutingTable> tables;

    public SmallWorldRoutingAlgorithm(SmallWorldGraph G) {
        this.G = G;

        buildTables();
    }

    private void buildTables() {
        tables = new HashMap<>();
        // Build routing table for switches
        for (int sid : G.switches()) {
            RoutingTable table = new RoutingTable();

            List<List<Integer>> neighbors = G.kHopNeighbor(sid, 3);

            for (int j : G.switches()) {
                if (sid != j) {
                    int min = G.switches().size() * 2;
                    int id = -1;
                    for (List<Integer> neighbor : neighbors) {
                        int distance = G.distance(neighbor.get(0), j) + neighbor.get(1);
                        if (distance < min) {
                            min = distance;
                            id = neighbor.get(2);
                        }
                    }
                    table.addRoute(j, id);
                } else {
                    table.addRoute(j, -1);
                }
            }
            for (int hid : G.getHostsOfSwitch(sid)) {
                table.addRoute(hid, hid);
            }
            tables.put(sid, table);
        }
    }

    @Override
    public int next(int source, int current, int destination) {
        if (G.isHostVertex(current)) {
            return G.adj(current).get(0);
        } if (G.adj(current).contains(destination)) {
            return destination;
        } else {
            int desSwitch = G.isHostVertex(destination) ? G.adj(destination).get(0) : destination;
            return tables.get(current).getNextNode(desSwitch);
        }
    }

    @Override
    public RoutingPath path(int source, int destination) {
        return null;
    }
}
