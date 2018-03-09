package network;

import config.Constant;
import simulatedexperiment.DiscreteEventSimulator;
import simulatedexperiment.Event;

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
        double currentSimTime = sim.getTime();

        if (id == p.getDestination()) {
            sim.log(String.format("Host #%d received packet", id));
            sim.numReceived++;
            p.setEndTime(currentSimTime);
            sim.totalPacketTime += p.timeTravel();
            return;
        }

        sim.numSent++;
        sim.log(String.format("Host #%d sending a packet to Host #%d",
                id, p.getDestination()));

        Host thisHost = this;

        double timeSent = currentSimTime + Constant.HOST_DELAY;
        sim.getEventList().add(new Event(sim, timeSent) {
            @Override
            public void actions() {
                link.handle(p, thisHost, sim);
            }
        });

        double timeCheck = timeSent + Constant.DEFAULT_TIME_OUT;
        sim.getEventList().add(new Event(sim, timeCheck) {
            @Override
            public void actions() {
                if (!p.isTransmitted()) {
                    sim.log(String.format("Host #%d resending packet to Host #%d",
                            thisHost.id, p.getDestination()));
                    thisHost.process(new Packet(p, sim.getTime()), sim);
                }
            }
        });
    }

    @Override
    public void clear() {
        link.clear();
    }
}
