package network;


import common.StdOut;
import config.Constant;
import networkexp.DiscreteEventSimulator;
import networkexp.Event;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Link {
    private Node u;
    private Node v;
    private long bandwidth;
    private long length;

    private long nextAvailableTime = 0;

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
        long currentTime = sim.getTime();
        if (currentTime >= nextAvailableTime) {
            if (sim.isVerbose()) {
                System.out.println(
                        String.format("Transferring from %d to %d at %d", input.id, output.id, sim.getTime()));
            }
            long latency = serialLatency(packet.getSize()) + propagationLatency();

            nextAvailableTime = currentTime + latency;
            sim.addEvent(new Event(currentTime + latency, ++sim.numEvent) {
                @Override
                public void execute() {
                    if (sim.isVerbose()) {
                        System.out.println(
                                String.format("Completed transferring from %d to %d at %d", input.id, output.id, sim.getTime()));
                    }
                    output.process(packet, sim);
                }
            });
        } else {
            if (sim.isVerbose()) {
                System.out.println(
                        String.format("Transfer from %d to %d delayed at %d", input.id, output.id, sim.getTime()));
            }
            sim.addEvent(new Event(nextAvailableTime, ++sim.numEvent) {
                @Override
                public void execute() {
                    Link.this.handle(packet, input, sim);
                }
            });
        }
    }

    public void clear() {
        this.nextAvailableTime = 0;
    }
}
