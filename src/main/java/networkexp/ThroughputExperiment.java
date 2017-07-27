package networkexp;

import common.Knuth;
import common.StdOut;
import config.Constant;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;
import custom.full.FullGraph;
import custom.full.FullRoutingAlgorithm;
import network.Network;
import network.Packet;
import routing.RoutingAlgorithm;

import java.util.*;

/**
 * Created by Dandoh on 6/27/17.
 */
public class ThroughputExperiment {

    private Network network;

    public ThroughputExperiment(Network network) {
        this.network = network;
    }

    public long measureThroughput(Map<Integer, Integer> trafficPattern, long frequency) {
        DiscreteEventSimulator sim = new DiscreteEventSimulator(false, Constant.MAX_TIME, false);
        network.clear(); // clear all the data, queue, ... in switches, hosts

        long timeInterval = Constant.MAX_TIME / frequency;

        for (Integer source : trafficPattern.keySet()) {
            Integer destination = trafficPattern.get(source);
            for (int i = 0; i < frequency; i++) {
                long time = i * timeInterval;
                final Packet packet = new Packet(source, destination, time);

                sim.addEvent(new Event(time, ++sim.numEvent) {
                    @Override
                    public void execute() {
                        network.getHostById(source).process(packet, sim);
                    }
                });
            }
        }

        sim.process();
//        System.out.println("Num packets sent: " + sim.numSent);
//        System.out.println("Num packets received: " + sim.numReceived);

        long averageTime = sim.totalPacketTime / sim.numSent;
        StdOut.printf("For f = %d, average packet time = %.2fms\n", frequency, averageTime / 1e6);
        return averageTime;
    }

    public long evaluateThroughput(Map<Integer, Integer> trafficPattern, double threshold)  {
//        StdOut.println(measureThroughput(trafficPattern, 300));
        long minTime = measureThroughput(trafficPattern, 1);
        StdOut.printf("Minimum average time = %.2fms\n", minTime / 1e6);

        int maxF = Constant.MAX_TIME / Constant.PACKET_INTERVAL;

        int first = 0;
        int last = maxF;

        while (first + 1 < last) {
            int mid = (first + last) / 2;
            StdOut.printf("Measure Throughput with f = %d\n", mid);

            if (measureThroughput(trafficPattern, mid) < minTime * threshold) {
                first = mid;
            } else {
                last = mid;
            }
        }

        StdOut.printf("\n\nMaximum frequency = %d\n", first);
        long nPacket = trafficPattern.size() * first * (Constant.MAX_TIME / (int) 1e9);
        long throughput = nPacket * Constant.PACKET_SIZE;
        return throughput;
    }

    public static void main(String[] args) {
        FatTreeGraph G = new FatTreeGraph(8);
        RoutingAlgorithm ra = new FatTreeRoutingAlgorithm(G, false);
//        FullGraph G = new FullGraph(8);
//        RoutingAlgorithm ra = new FullRoutingAlgorithm(G);
        Network network = new Network(G, ra);

        ThroughputExperiment experiment = new ThroughputExperiment(network);
        Integer[] hosts = G.hosts().toArray(new Integer[0]);

        Knuth.shuffle(hosts);

        List<Integer> sources = new ArrayList<>();
        List<Integer> destination = new ArrayList<>();
        sources.addAll(Arrays.asList(hosts).subList(0, hosts.length / 2));
        destination.addAll(Arrays.asList(hosts).subList(hosts.length / 2, hosts.length));

        Map<Integer, Integer> traffic = new HashMap<>();
        for (int i = 0; i < sources.size(); i++) {
            traffic.put(sources.get(i), destination.get(i));
        }
//        traffic.put(2, 17);
//        traffic.put(3, 17);

//        StdOut.println(G.hosts().size());
        double threshold = 1.3;
        StdOut.printf("Thresh hold = %.2f\n", threshold);
        long throughput = experiment.evaluateThroughput(traffic, threshold);
//        StdOut.printf("Maximum frequency = %d\n", maxFrequency);
        StdOut.printf("\nThrough put of network %dGb/s\n", throughput / 1000000);
    }
}
