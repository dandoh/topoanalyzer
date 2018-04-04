package network;

import common.Queue;
import config.Constant;
import simulatedexperiment.DiscreteEventSimulator;
import simulatedexperiment.Event;
import routing.RoutingAlgorithm;

import java.util.*;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Switch extends Node {

    private RoutingAlgorithm ra;

    // map from index to link
    public Map<Integer, L2Object> links = new HashMap<>();

    public Switch(int id, RoutingAlgorithm ra) {
        super(id);
        this.ra = ra;
    }

    @Override
    public L2Object processPacket(Packet p, DiscreteEventSimulator sim) {
        int nextHop = ra.next(p.getSource(), id, p.getDestination());
        return links.get(nextHop);
    }

    @Override
    public void clear() {
        for (Map.Entry<Integer, L2Object> link: this.links.entrySet()) {
            link.getValue().clear();
        }

    }
}
