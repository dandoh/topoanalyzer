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
    public void process(Packet p, DiscreteEventSimulator simulator) {
        double currentSimTime = simulator.getTime();

        if (id == p.getDestination()) {
            // TODO - save information
            simulator.log(String.format("Host #%d received packet", id));
            simulator.numReceived++;
            p.setEndTime(currentSimTime);
            simulator.totalPacketTime += p.timeTravel();
            return;
        }

        simulator.numSent++;
        simulator.log(String.format("Host #%d sending a packet to Host #%d",
                id, p.getDestination()));

        Host thisHost = this;

        double timeSent = currentSimTime + Constant.HOST_DELAY;
        simulator.getEventList().add(new Event(simulator, timeSent) {
            @Override
            public void actions() {
                link.handle(p, thisHost, simulator);
            }
        });

        double timeCheck = timeSent + Constant.DEFAULT_TIME_OUT;
        simulator.getEventList().add(new Event(simulator, timeCheck) {
            @Override
            public void actions() {
                if (!p.isTransmitted()) {
                    simulator.log(String.format("Host #%d resending packet to Host #%d",
                            thisHost.id, p.getDestination()));
                    thisHost.process(p, simulator);
                }
            }
        });
    }

    @Override
    public void clear() {
        link.clear();
    }
}
