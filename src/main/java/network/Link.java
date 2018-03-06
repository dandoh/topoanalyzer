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

    public void handle(Packet packet, Node input, DiscreteEventSimulator simulator) {
        // move packet from input endpoint to output endpoint
        Node output = input == u ? v : u;
        double currentTime = simulator.time();
        if (currentTime >= nextAvailableTime) {
            simulator.log(String.format("Transferring from %d to %d", input.id, output.id));
            long latency = serialLatency(packet.getSize()) + propagationLatency();

            nextAvailableTime = currentTime + latency;
            simulator.getEventList().add(new Event(simulator, currentTime + latency) {
                @Override
                public void actions() {
                    simulator.log(String.format("Completed transferring from %d to %d", input.id, output.id));
                    output.process(packet, simulator);
                }
            });
        } else {
            simulator.log(String.format("Transfer from %d to %d delayed", input.id, output.id));
            simulator.getEventList().add(new Event(simulator, nextAvailableTime) {
                @Override
                public void actions() {
                    Link.this.handle(packet, input, simulator);
                }
            });
        }
    }

    public void clear() {
        this.nextAvailableTime = 0;
    }
}
