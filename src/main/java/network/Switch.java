package network;

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

    // Buffer of switch
    public List<Tuple<Packet, Long>> buffer;
    public long bufferSize;

    public Switch(int id, RoutingAlgorithm ra) {
        super(id);
        this.ra = ra;

        this.bufferSize = Constant.SWITCH_BUFFER_SIZE;
        this.buffer = new ArrayList<>();
    }

    @Override
    public void process(Packet p, DiscreteEventSimulator sim) {
        long currentTime = sim.getTime();
        this.checkBuffer(currentTime);

        sim.log(String.format("Switch #%d processing a packet", id));

        if (currentBufferSize() + p.getSize() > bufferSize) {
            sim.numLoss++;
            sim.log(String.format("Switch #%d drop a packet", id));
//            StdOut.printf("At %d: Switch #%d drop a packet\n", sim.getTime(), id);
//            StdOut.println(String.format("Switch #%d drop a packet %d", id, buffer.size()));
            return;
        }

        int nextId = ra.next(p.getSource(), id, p.getDestination());
//        StdOut.printf("%d %d\n", id, nextId);

        long executeTime;
        if (buffer.isEmpty()) {
            executeTime = currentTime + Constant.SWITCH_DELAY;
        } else {
            executeTime = maxPacketTime() + Constant.SWITCH_DELAY;
        }

        buffer.add(new Tuple<Packet, Long>(p, executeTime));

        sim.addEvent(new Event(executeTime, ++sim.numEvent) {
            @Override
            public void execute() {
                links.get(nextId).handle(p, Switch.this, sim);
            }
        });
    }

    @Override
    public void clear() {
        for (Map.Entry<Integer, Link> link: this.links.entrySet()) {
            link.getValue().clear();
        }

        this.buffer = new ArrayList<>();
    }

    private void checkBuffer(long currentTime) {

//        StdOut.printf("Before %d : %d\n", currentTime, buffer.size());
        this.buffer.removeIf((Tuple<Packet, Long> t) -> t.b < currentTime);
//        for (Iterator<Tuple<Packet, Long>> iter = buffer.listIterator(); iter.hasNext();) {
//            Tuple<Packet, Long> tuple = iter.next();
//
//            if (tuple.b < currentTime) {
//                iter.remove();
//            }
//        }
//        StdOut.printf("After %d\n", buffer.size());
//        if (this.id == 555) {
//            StdOut.printf("At %d\n", currentTime);
//            StdOut.println(this.buffer.size());
//            for (Tuple<Packet, Long> t : buffer) {
//                StdOut.println(t.b);
//            }
//        }
    }

    private long maxPacketTime() {
        long maxTime = 0;
        for (Tuple<Packet, Long> t : buffer) {
            maxTime = Math.max(t.b, maxTime);
        }
        return  maxTime;
    }

    private long currentBufferSize() {
        long size = 0;
        for (Tuple<Packet, Long> t : buffer) {
            size += t.a.getSize();
        }

        return size;
    }
}
