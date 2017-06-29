package network;

import config.Constant;
import networkexp.DiscreteEventSimulator;
import networkexp.Event;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Host extends Node {

    // link to the ToR switch
    public Link link;

    public Host(int id) {
        super(id);
    }

    @Override
    public void process(Packet p, DiscreteEventSimulator sim) {
        if (id == p.getDestination()) {
            // TODO - save information
            System.out.println(String.format("Host #%d received packet at %d", id, sim.getTime()));
            sim.numReceived++;
            return;
        }

        sim.numSent++;
        System.out.println(String.format("Host #%d sending packet at %d", id, sim.getTime()));
        long currentSimTime = sim.getTime();
        sim.addEvent(new Event(currentSimTime + Constant.HOST_DELAY) {
            @Override
            public void execute() {
                link.handle(p, Host.this, sim);
            }
        });
    }
}
