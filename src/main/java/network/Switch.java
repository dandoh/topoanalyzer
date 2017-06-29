package network;

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
        System.out.println(
                String.format("Switch #%d processing a packet at %d", id, sim.getTime()));
        int currentTime = sim.getTime();
        int nextId = ra.next(p.getSource(), id, p.getDestination());

        sim.addEvent(new Event(currentTime + Constant.SWITCH_DELAY) {
            @Override
            public void execute() {
                links.get(nextId).handle(p, Switch.this, sim);
            }
        });
    }
}
