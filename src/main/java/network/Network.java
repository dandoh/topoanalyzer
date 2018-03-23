package network;

import graph.Graph;
import routing.RoutingAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Network {
    private Graph graph;
    private List<Host> hosts;
    private List<Switch> switches;
    private Map<Integer, Host> hostById;
    private Map<Integer, Switch> switchById;

    public Network(Graph graph, RoutingAlgorithm routingAlgorithm) {
        this.graph = graph;
        // construct hosts, switches and links and routing algorithm
        hosts = new ArrayList<>();
        switches = new ArrayList<>();
        hostById = new HashMap<>();
        switchById = new HashMap<>();

        for (int hid : graph.hosts()) {
            Host host = new Host(hid);
            hosts.add(host);
            hostById.put(hid, host);
        }

        for (int sid : graph.switches()) {
            Switch sw = new Switch(sid, routingAlgorithm);
            switches.add(sw);
            switchById.put(sid, sw);
        }

        // link from switch to host
        for (Host host : hosts) {
            // get corra switch
            int nsid = graph.adj(host.id)
                    .get(0);
            Switch csw = switchById.get(nsid);

            // add to both
            host.link = new L2Object(host, csw);
            ;
            if (!csw.links.containsKey(host.id)) {
                csw.links.put(host.id, new L2Object(host, csw));
            }
        }

        // link from switch to switch
        for (Switch sw : switches) {
            int swid = sw.id;
            for (int nsid : graph.adj(swid)) {
                if (graph.isSwitchVertex(nsid)) {
                    Switch other = switchById.get(nsid);
                    // create new link
                    // add to both
                    if (!other.links.containsKey(swid)) {
                        other.links.put(swid, new L2Object(sw, other));
                    }
                    if (!sw.links.containsKey(nsid)) {
                        sw.links.put(nsid, new L2Object(sw, other));
                    }
                }
            }
        }
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public List<Switch> getSwitches() {
        return switches;
    }

    public Host getHostById(int id) {
        return hostById.get(id);
    }

    public void clear() {
        for (Host host : hosts) {
            host.clear();
        }

        for (Switch sw: switches) {
            sw.clear();
        }
    }
}
