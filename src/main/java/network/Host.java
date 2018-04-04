package network;

import common.Queue;
import common.StdOut;
import config.Constant;
import simulatedexperiment.DiscreteEventSimulator;
import simulatedexperiment.Event;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Host extends Node {

    private Queue<Packet> buffer;
    // link to the ToR switch
    public L2Object link;

    public Host(int id) {
        super(id);
        this.buffer = new Queue<>();
    }

    public void startPacket(Packet p, DiscreteEventSimulator sim) {
        buffer.enqueue(p);
        if (this.buffer.size() == 1) {
            processPacket(p, sim);
        }
    }

    @Override
    public L2Object processPacket(Packet p, DiscreteEventSimulator sim) {
        Host self = this;
        double currentTime = sim.getTime();

        // Packet went to its destination
        if (id == p.getDestination()) {
            sim.log(String.format("Host #%d received packet", id));
            sim.numReceived++;
            p.setEndTime(currentTime);
            sim.totalPacketTime += p.timeTravel();
            return null;
        }

        double timeSent = currentTime + Constant.HOST_DELAY;
        sim.addEvent(new Event(sim, timeSent) {
            @Override
            public void actions() {
                self.send(link, sim);
            }
        });
        return null;
    }

    public void send(L2Object link, DiscreteEventSimulator sim) {
//        sim.log(String.format("Try to sending from host #%d", id));
        Host self = this;
        double currentTime = sim.getTime();
        Packet p = buffer.peek();

        if (link.canSend(p, this)) {
            // Host send packet to link
            sim.numSent++;
            sim.log(String.format("Host #%d sending a packet to Host #%d",
                    id, p.getDestination()));

            this.buffer.dequeue();
            link.receivePacket(p, self, sim);

            // Schedule for the next packet in buffer
            if (!buffer.isEmpty()) {
                double nextWakeUpTime = currentTime + 1;
                sim.addEvent(new Event(sim, nextWakeUpTime) {
                    @Override
                    public void actions() {
                        self.processPacket(buffer.peek(), sim);
                    }
                });
            }
        } else {
            // Schedule for current packet
            double nextTryTime = currentTime + Constant.RETRY_TIME;
            sim.addEvent(new Event(sim, nextTryTime) {
                @Override
                public void actions() {
                    self.send(link, sim);
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
