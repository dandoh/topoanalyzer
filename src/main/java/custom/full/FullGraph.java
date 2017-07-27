package custom.full;


import graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mta on 7/25/17.
 */
public class FullGraph extends Graph {
    public static final int HOST_PER_SWITCH = 8;
    private List<Integer> switches;
    private List<Integer> hosts;

    private final int nHost;
    private final int nSwitch;


    public FullGraph(int switchSize) {
        this.nSwitch = switchSize;
        this.nHost = nSwitch * HOST_PER_SWITCH;

        this.V = nHost + nSwitch;
        adj = (List<Integer>[]) new List[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new ArrayList<Integer>();
        }

        // Add full edges between swiches
        for (int i = 0; i < nSwitch - 1; i++)
            for (int j = i + 1; j < nSwitch; j++) {
                addEdge(i, j);
            }

        // Add edges between hosts and switch
        int hostId = nSwitch;
        for (int i = 0; i < nSwitch; i++) {
            for (int j = 0; j < HOST_PER_SWITCH; j++) {
                addEdge(i, hostId);
                hostId++;
            }
        }
    }

    @Override
    public List<Integer> hosts() {
        if (hosts != null) return hosts;
        hosts = new ArrayList<>();
        for (int i = nSwitch; i < V; i++)
            hosts.add(i);

        return hosts;
    }

    @Override
    public List<Integer> switches() {
        if (switches != null) return switches;
        switches = new ArrayList<>();
        for (int i = 0; i < nSwitch; i++)
            switches.add(i);

        return switches;
    }

    @Override
    public boolean isHostVertex(int v) {
        return v >= nSwitch;
    }

    @Override
    public boolean isSwitchVertex(int v) {
        return v < nSwitch;
    }
}
