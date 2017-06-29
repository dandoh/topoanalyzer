package custom.fattree;

import custom.fattree.Address;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dandoh on 5/24/17.
 */
public class FatTreeGraph extends Graph {
    public static final int CORE = 1;
    public static final int AGG  = 2;
    public static final int EDGE = 3;
    private final int numServers;
    private final int numPodSwitches;
    private final int numCores;
    private final int k;
    private Address[] address;

    private List<Integer> switches;
    private List<Integer> hosts;

    public FatTreeGraph(int k) {
        if (k / 2 == 1) throw new IllegalArgumentException("K must be even");
        if (k > 256) throw new IllegalArgumentException("K must less than 256");

        this.k = k;
        this.numServers = k * k * k / 4;
        this.numPodSwitches = k * k;
        this.numCores = k * k / 4;

        this.V = numServers + numPodSwitches + numCores;
        this.E = 0;
        adj = (List<Integer>[]) new List[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new ArrayList<Integer>();
        }

        // each pod has k^2/4 servers and k switches
        int numEachPod = k * k / 4 + k;
        for (int p = 0; p < k; p++) {
            int offset = numEachPod * p;

            // between server and edge
            for (int e = 0; e < k / 2; e++) {
                int edgeSwitch = offset + k * k / 4 + e;
                for (int s = 0; s < k / 2; s++) {
                    int server = offset + e * k / 2 + s;
                    addEdge(edgeSwitch, server);
                }
            }

            // between agg and edge
            for (int e = 0; e < k / 2; e++) {
                int edgeSwitch = offset + k * k / 4 + e;
                for (int a = k / 2; a < k; a++) {
                    int aggSwitch = offset + k * k / 4 + a;
                    addEdge(edgeSwitch, aggSwitch);
                }
            }

            // between agg and core
            for (int a = 0; a < k / 2; a++) {
                int aggSwitch = offset + k * k / 4 + k / 2 + a;
                for (int c = 0; c < k / 2; c++) {
                    int coreSwitch = a * k / 2 + c + numPodSwitches + numServers;
                    addEdge(aggSwitch, coreSwitch);
                }
            }

        }

        buildAddress();
    }

    private void buildAddress() {
        this.address = new Address[V];

        int numEachPod = k * k / 4 + k;

        // address for pod's switches
        for (int p = 0; p < k; p++) {
            int offset = numEachPod * p;
            for (int s = 0; s < k; s++) {
                int switchId = offset + k * k / 4 + s;
                address[switchId] = new Address(10, p, s, 1);
            }
        }

        // address for core switches
        for (int j = 1; j <= k / 2; j++) {
            for (int i = 1; i <= k / 2; i++) {
                int offset = numPodSwitches + numServers;
                int switchId = offset + (j - 1) * k / 2 + i - 1;
                address[switchId] = new Address(10, k, j, i);
            }
        }

        // address for servers
        for (int p = 0; p < k; p++) {
            int offset = numEachPod * p;
            for (int e = 0; e < k / 2; e++) {
                for (int h = 2; h <= k / 2 + 1; h++) {
                    int serverId = offset + e * k / 2 + h - 2;
                    address[serverId] = new Address(10, p, e, h);
                }
            }
        }
    }

    public int getK() {
        return k;
    }

    public Address getAddress(int u) {
        return address[u];
    }

    @Override
    public List<Integer> hosts() {
        if (hosts != null) return hosts;
        hosts = new ArrayList<>();

        int numEachPod = k * k / 4 + k;
        for (int p = 0; p < k; p++) {
            int offset = numEachPod * p;
            for (int e = 0; e < k / 2; e++) {
                for (int h = 2; h <= k / 2 + 1; h++) {
                    int serverId = offset + e * k / 2 + h - 2;
                    hosts.add(serverId);
                }
            }
        }

        return hosts;
    }

    @Override
    public List<Integer> switches() {
        if (switches != null) return switches;
        switches = new ArrayList<>();

        int numEachPod = k * k / 4 + k;
        for (int p = 0; p < k; p++) {
            int offset = numEachPod * p;
            for (int s = 0; s < k; s++) {
                int switchId = offset + k * k / 4 + s;
                switches.add(switchId);
            }
        }

        // address for core switches
        for (int j = 1; j <= k / 2; j++) {
            for (int i = 1; i <= k / 2; i++) {
                int offset = numPodSwitches + numServers;
                int switchId = offset + (j - 1) * k / 2 + i - 1;
                switches.add(switchId);
            }
        }



        return switches;
    }

    public boolean isHostVertex(int u) {
        if (u >= numServers + numPodSwitches) return false;
        int offset = u % (k * k / 4 + k);
        return offset < k * k / 4;
    }

    public boolean isSwitchVertex(int u) {
        return !isHostVertex(u);
    }

    public int switchType(int u) {
        int numEachPod = k * k / 4 + k;
        if (u >= k * numEachPod) return CORE;
        else {
            int os = u % numEachPod;
            if (os >= k * k / 4 + k / 2) return AGG;
            else return EDGE;
        }
    }
}
