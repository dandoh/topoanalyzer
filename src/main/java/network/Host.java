package network;

import common.Queue;
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
    public void receive(Packet p, NetworkObject source, DiscreteEventSimulator sim) {
        this.addToBuffer(p);
        if (this.buffer.size() == 1) {
            process(sim);
        }
    }

    @Override
    public void process(DiscreteEventSimulator sim) {
        Host self = this;
        double currentTime = sim.getTime();
        Packet p = buffer.peek();

        // Packet went to its destination
        if (id == p.getDestination()) {
            sim.log(String.format("Host #%d received packet", id));
            sim.numReceived++;
            p.setEndTime(currentTime);
            sim.totalPacketTime += p.timeTravel();
            return;
        }

        double timeSent = currentTime + Constant.HOST_DELAY;
        sim.addEvent(new Event(sim, timeSent) {
            @Override
            public void actions() {
                self.send(link, sim);
            }
        });
    }

    @Override
    public void send(NetworkObject link, DiscreteEventSimulator sim) {
        Host self = this;
        Packet p = buffer.peek();
        double currentTime = sim.getTime();
        if (link.canReceive(p, currentTime)) {
            // Host send packet to link
            sim.numSent++;
            sim.log(String.format("Host #%d sending a packet to Host #%d",
                    id, p.getDestination()));

            dequeueBuffer();
            link.receive(p, self, sim);

            // Schedule for the next packet in buffer
            if (!buffer.isEmpty()) {
                double nextWakeUpTime = currentTime + 1;
                sim.addEvent(new Event(sim, nextWakeUpTime) {
                    @Override
                    public void actions() {
                        self.process(sim);
                    }
                });
            }
        } else {
            // Schedule for current packet
            double nextWakeUpTime = link.getNextAvailableTime();
            sim.addEvent(new Event(sim, nextWakeUpTime) {
                @Override
                public void actions() {
                    self.process(sim);
                }
            });
        }
    }

    @Override
    public void clear() {
        link.clear();
        this.buffer = new Queue<>();
    }
}
