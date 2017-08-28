package network;

import common.StdOut;
import config.Constant;
import networkexp.DiscreteEventSimulator;
import networkexp.Event;
import routing.RoutingAlgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Switch extends Node {

    // map from index to link
    public Map<Integer, Link> links = new HashMap<>();
    private RoutingAlgorithm ra;

    public Switch(int id, RoutingAlgorithm ra) {
        super(id);
        this.ra = ra;
    }

    @Override
    public void process(Packet p, DiscreteEventSimulator sim) {
        if (sim.isVerbose()) {
            System.out.println(
                    String.format("Switch #%d processing a packet at %d", id, sim.getTime()));
        }
        long currentTime = sim.getTime();
        int nextId = ra.next(p.getSource(), id, p.getDestination());
//        StdOut.printf("%d %d\n", id, nextId);
        sim.addEvent(new Event(currentTime + Constant.SWITCH_DELAY, ++sim.numEvent) {
            @Override
            public void execute() {
                links.get(nextId).handle(p, Switch.this, sim);
            }
        });
    }

    @Override
    public void clear() {
        for (Map.Entry<Integer, Link> link: this.links.entrySet()) {
            link.getValue().clear();
        }
    }


}
