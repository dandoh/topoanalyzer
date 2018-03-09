package network;


import config.Constant;
import simulatedexperiment.DiscreteEventSimulator;
import simulatedexperiment.Event;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Link {
    private Node u;
    private Node v;
    private long bandwidth;
    private long length;

    private double nextAvailableTime = 0;

    public Link(Node u, Node v) {
        this.u = u;
        this.v = v;
        this.bandwidth = Constant.LINK_BANDWIDTH;
        this.length= Constant.LINK_LENGTH;
    }

    public long serialLatency(int packetSize) {
        return (long) (1e9 * packetSize / this.bandwidth);
    }
    public long propagationLatency() {
        return (long) (length / Constant.PROPAGATION_VELOCITY);
    }

    public void handle(Packet packet, Node input, DiscreteEventSimulator sim) {
        // move packet from input endpoint to output endpoint
        Node output = input == u ? v : u;
        double currentTime = sim.time();
        if (currentTime >= nextAvailableTime) {
            sim.log(String.format("Transferring from %d to %d", input.id, output.id));
            long latency = serialLatency(packet.getSize()) + propagationLatency();

            nextAvailableTime = currentTime + latency;
            sim.getEventList().add(new Event(sim, currentTime + latency) {
                @Override
                public void actions() {
                    sim.log(String.format("Completed transferring from %d to %d", input.id, output.id));
                    output.process(packet, sim);
                }
            });
        } else {
            sim.log(String.format("Transfer from %d to %d delayed", input.id, output.id));
            sim.getEventList().add(new Event(sim, nextAvailableTime) {
                @Override
                public void actions() {
                    Link.this.handle(packet, input, sim);
                }
            });
        }
    }

    public void clear() {
        this.nextAvailableTime = 0;
    }
}
