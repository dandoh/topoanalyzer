package network;

import common.Queue;
import common.StdOut;
import common.Tuple;
import config.Constant;
import simulatedexperiment.DiscreteEventSimulator;
import simulatedexperiment.Event;
import routing.RoutingAlgorithm;

import java.util.*;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Switch extends Node {

    private RoutingAlgorithm ra;

    // map from index to link
    public Map<Integer, Link> links = new HashMap<>();

    public Switch(int id, RoutingAlgorithm ra) {
        super(id);
        this.ra = ra;

        this.maxBufferSize= Constant.SWITCH_BUFFER_SIZE;
    }

    @Override
    public void receive(Packet p, NetworkObject source, DiscreteEventSimulator sim) {
        this.addToBuffer(p);
        if (this.buffer.size() == 1) {
            this.process(sim);
        }
    }

    @Override
    public void process(DiscreteEventSimulator sim) {
        Switch self = this;
        Packet p = buffer.peek();
        double currentTime = sim.time();

        sim.log(String.format("Switch #%d processing a packet", id));

        int nextId = ra.next(p.getSource(), id, p.getDestination());
//        StdOut.printf("%d %d\n", id, nextId);

        double executeTime = currentTime + Constant.SWITCH_DELAY;

        sim.addEvent(new Event(sim, executeTime) {
            @Override
            public void actions() {
                self.send(links.get(nextId), sim);
            }
        });
    }

    @Override
    public void send(NetworkObject link, DiscreteEventSimulator sim) {
        Switch self = this;
        double currentTime = sim.time();
        Packet p = dequeueBuffer();

        if (link.canReceive(p, currentTime)) {
            link.receive(p, this, sim);
        } else {
            addToBuffer(p);
        }

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
    }

    @Override
    public void clear() {
        for (Map.Entry<Integer, Link> link: this.links.entrySet()) {
            link.getValue().clear();
        }

        this.buffer = new Queue<>();
    }
}
