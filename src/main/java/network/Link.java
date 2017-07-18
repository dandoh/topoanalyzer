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

    public int serialLatency(int packetSize) {
        return (int) (packetSize / this.bandwidth);
    }
    public int propagationLatency() {
        return (int) (length / Constant.PROPAGATION_VELOCITY);
    }

    public void handle(Packet packet, Node input, DiscreteEventSimulator sim) {
        // move packet from input endpoint to output endpoint
        Node output = input == u ? v : u;
        long currentTime = sim.getTime();
        if (currentTime >= nextAvailableTime) {
            System.out.println(
                    String.format("Transferring from %d to %d at %d", input.id, output.id, sim.getTime()));

            int latency = serialLatency(packet.getSize()) + propagationLatency();

            nextAvailableTime = currentTime + latency;
            sim.addEvent(new Event(currentTime + latency) {
                @Override
                public void execute() {
                    System.out.println(
                            String.format("Completed transferring from %d to %d at %d", input.id, output.id, sim.getTime()));
                    output.process(packet, sim);
                }
            });
        } else {
            System.out.println(
                    String.format("Transfer from %d to %d delayed at %d", input.id, output.id, sim.getTime()));
            sim.addEvent(new Event(nextAvailableTime) {
                @Override
                public void execute() {
                    Link.this.handle(packet, input, sim);
                }
            });
        }
    }
}
