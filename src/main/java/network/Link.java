package network;


import config.Constant;
import simulatedexperiment.DiscreteEventSimulator;
import simulatedexperiment.Event;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Link extends NetworkObject {
    private Node u;
    private Node v;
    private long bandwidth;
    private long length;

    private double nextAvailableTime = 0;

    public Link(Node u, Node v) {
        super(0);
        this.u = u;
        this.v = v;
        this.bandwidth = Constant.LINK_BANDWIDTH;
        this.length = Constant.LINK_LENGTH;
    }

    public long serialLatency(int packetSize) {
        return (long) (1e9 * packetSize / this.bandwidth);
    }
    public long propagationLatency() {
        return (long) (length / Constant.PROPAGATION_VELOCITY);
    }

    public boolean isAvailableAt(double time) {
        return nextAvailableTime <= time;
    }

    public double getNextAvailableTime() {
        return nextAvailableTime;
    }

    @Override
    public void receive(Packet p, NetworkObject source, DiscreteEventSimulator sim) {
        Node output = source == u ? v : u;
        sim.log(String.format("Transferring from %d to %d", source.id, output.id));
        addToBuffer(p);
        this.transfer(p, output, sim);
    }

    public void transfer(Packet p, Node output, DiscreteEventSimulator sim) {
        Link self = this;
        double currentTime = sim.time();

        long latency = serialLatency(p.getSize()) + propagationLatency();

        nextAvailableTime = currentTime + latency;
        sim.getEventList().add(new Event(sim, currentTime + latency) {
            @Override
            public void actions() {
                self.send(output, sim);
            }
        });
    }

    @Override
    public void process(DiscreteEventSimulator sim) {
    }

    @Override
    public void send(NetworkObject output, DiscreteEventSimulator sim) {
        Link self = this;
        double currentTime = sim.time();
        Packet p = buffer.peek();
        Node input = output == u ? v : u;
        sim.log(String.format("Completed transferring from %d to %d", input.id, output.id));

        if (output.canReceive(p, currentTime)) {
            dequeueBuffer();
            output.receive(p, this, sim);
        } else {
            // Schedule for next wake up time
            double nextWakeUpTime = output.getNextAvailableTime();
            sim.addEvent(new Event(sim, nextWakeUpTime) {
                @Override
                public void actions() {
                    self.process(sim);
                }
            });
        }
    }

    @Override
    public boolean canReceive(Packet p, double time) {
        return nextAvailableTime <= time;
    }

    @Override
    public void clear() {
        this.nextAvailableTime = 0;
    }
}
