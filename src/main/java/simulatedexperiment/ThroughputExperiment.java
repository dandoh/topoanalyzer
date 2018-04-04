package simulatedexperiment;

import common.Knuth;
import common.StdOut;
import config.Constant;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;
import custom.full.FullGraph;
import custom.full.FullRoutingAlgorithm;
import network.Host;
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

    public long minAveragePacketTime(Map<Integer, Integer> trafficPattern, boolean verbose) {
        DiscreteEventSimulator simulator = new DiscreteEventSimulator(false, Constant.MAX_TIME, verbose);
        network.clear();

        for (Integer source : trafficPattern.keySet()) {
            Integer destination = trafficPattern.get(source);

            final Packet packet = new Packet(source, destination, simulator.getTime());

            simulator.getEventList().add(new Event(simulator, simulator.getTime()) {
                @Override
                public void actions() {
                    network.getHostById(source).startPacket(packet, simulator);
                }
            });

            simulator.start();
        }

        long averageTime = simulator.totalPacketTime / simulator.numSent;
        StdOut.printf("Loss percentage = %.2f\n", 1.0 * simulator.numLoss / simulator.numSent * 100);
        StdOut.printf("Average packet time = %.2fms\n", averageTime / 1e6);
        return averageTime;
    }

    public long measureThroughput(Map<Integer, Integer> trafficPattern, long frequency, boolean verbose) {
        DiscreteEventSimulator simulator = new DiscreteEventSimulator(false, Constant.MAX_TIME, verbose);
        network.clear(); // clear all the data, queue, ... in switches, hosts

        long timeInterval = Constant.MAX_TIME / frequency;

        for (Integer source : trafficPattern.keySet()) {
            Integer destination = trafficPattern.get(source);
            for (int i = 0; i < frequency; i++) {
                long time = i * timeInterval;
                final Packet packet = new Packet(source, destination, time);

                simulator.getEventList().add(new Event(simulator, time) {
                    @Override
                    public void actions() {
                        network.getHostById(source).startPacket(packet, simulator);
                    }
                });
            }
        }

        simulator.start();
//        System.out.println("Num packets sent: " + sim.numSent);
//        System.out.println("Num packets received: " + sim.numReceived);

        long averageTime = simulator.totalPacketTime / simulator.numSent;
        StdOut.printf("For f = %d, average packet time = %.2fms\n", frequency, averageTime / 1e6);
        StdOut.printf("Loss percentage = %.2f\n", 1.0 * simulator.numLoss / simulator.numSent * 100);
        return averageTime;
    }

    public long evaluateThroughput(Map<Integer, Integer> trafficPattern, double threshold, boolean verbose)  {
//        StdOut.println(measureThroughput(trafficPattern, false));
        long minTime = minAveragePacketTime(trafficPattern, verbose);
        StdOut.printf("Minimum average time = %.2fms\n\n", minTime / 1e6);

        int maxF = 1000; //Constant.MAX_TIME / Constant.PACKET_INTERVAL;

        int first = 0;
        int last = maxF;

        while (first + 1 < last) {
            int mid = (first + last) / 2;
            StdOut.printf("Measure Throughput with f = %d\n", mid);

            if (measureThroughput(trafficPattern, mid, verbose) < minTime * threshold) {
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

//        StdOut.println(G.hosts().size());
        double threshold = 1.1;
        StdOut.printf("Thresh hold = %.2f\n", threshold);
//        long throughput = experiment.evaluateThroughput(traffic, threshold, false);
//        long throughput = experiment.minAveragePacketTime(traffic, true);
        StdOut.println(experiment.measureThroughput(traffic, 100, true));

//        StdOut.printf("Maximum frequency = %d\n", maxFrequency);
//        StdOut.printf("\nThrough put of network %dGb/s\n", throughput / 1000000);
    }
}
