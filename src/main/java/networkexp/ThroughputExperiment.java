package networkexp;

import common.Knuth;
import common.StdOut;
import config.Constant;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;
import network.Network;
import network.Packet;
import routing.RoutingAlgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dandoh on 6/27/17.
 */
public class ThroughputExperiment {

    private Network network;

    public ThroughputExperiment(Network network) {
        this.network = network;
    }

    public boolean measureThroughput(Map<Integer, Integer> trafficPattern, int k) {
        DiscreteEventSimulator sim = new DiscreteEventSimulator(Constant.MAX_TIME);
//        network.clear(); // clear all the data, queue, ... in switches, hosts

        for (Integer source : trafficPattern.keySet()) {
            Integer destination = trafficPattern.get(source);
            // send with the speed of 1e9 bit per 1s (1e9 time unit)
            // => each 12000 time unit send 12000 bit (1500 byte = MTU)
            for (int i = 0; i < k; i++) {
                long time = i * Constant.PACKET_INTERVAL;
                final Packet packet = new Packet(source, destination);
                sim.addEvent(new Event(time) {
                    @Override
                    public void execute() {
                        network.getHostById(source).process(packet, sim);
                    }
                });
            }
        }

//        System.out.println("Done set up");
        sim.process();
//        System.out.println("Num packets sent: " + sim.numSent);
//        System.out.println("Num packets received: " + sim.numReceived);

        return sim.numSent <= sim.numReceived;
    }

    public long evaluateThroughput(Map<Integer, Integer> trafficPattern)  {
        int maxK = Constant.MAX_TIME / Constant.PACKET_INTERVAL;

        int first = 0;
        int last = maxK;
//        StdOut.println(measureThroughput(trafficPattern, 50));
        while (first + 1 < last) {
            int mid = (first + last) / 2;
            StdOut.printf("Measure Throughput with k = %d\n", mid);

            if (measureThroughput(trafficPattern, mid)) {
                first = mid;
            } else {
                last = mid;
            }
        }

        StdOut.printf("\n\nMaximum k = %d\n", first);
        long nPacket = trafficPattern.size() * first;
        long throughput = nPacket * Constant.PACKET_SIZE;
        return throughput;
    }

    public static void main(String[] args) {
        FatTreeGraph G = new FatTreeGraph(4);
        RoutingAlgorithm ra = new FatTreeRoutingAlgorithm(G, false);
        Network network = new Network(G, ra);

        ThroughputExperiment experiment = new ThroughputExperiment(network);
        Integer[] sources = G.hosts().toArray(new Integer[0]);
        Integer[] destination = G.hosts().toArray(new Integer[0]);
        Knuth.shuffle(destination);

        // TODO: New traffic pattern
        Map<Integer, Integer> traffic = new HashMap<>();
        for (int i = 0; i < sources.length; i++) {
            traffic.put(sources[i], destination[i]);
        }
//        traffic.put(2, 17);
//        traffic.put(3, 17);

//        StdOut.println(G.hosts().size());
        long throughput = experiment.evaluateThroughput(traffic);
        StdOut.printf("\nThrough put of network %dGb/s\n", throughput / 1000000);
    }
}
