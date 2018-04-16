package simulatedexperiment;

import common.Knuth;
import common.StdOut;
import common.Tuple;
import config.Constant;
import custom.corra.CORRAKGraph;
import custom.corra.CORRARoutingAlgorithm;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;
import custom.full.FullGraph;
import custom.full.FullRoutingAlgorithm;
import custom.smallworld.SmallWorldGraph;
import custom.smallworld.SmallWorldRoutingAlgorithm;
import graph.Coordination;
import network.Host;
import network.Network;
import network.Packet;
import routing.RoutingAlgorithm;
import umontreal.ssj.charts.XYLineChart;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

            final Packet packet = new Packet(0, source, destination, simulator.getTime());

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
                final Packet packet = new Packet(0, source, destination, time);

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

    public double calThroughput(Map<Integer, Integer> trafficPattern, boolean verbose) {
        DiscreteEventSimulator simulator =
                new DiscreteEventSimulator(true, Constant.MAX_TIME * 10, verbose);
        network.clear(); // clear all the data, queue, ... in switches, hosts

        for (Integer source : trafficPattern.keySet()) {
            Integer destination = trafficPattern.get(source);
            Packet packet = new Packet(simulator.numSent++, source, destination, 0);
            network.getHostById(source).startPacket(packet, simulator);
        }

        simulator.start();

        System.out.println("Num packets sent: " + simulator.numSent);
        System.out.println("Num packets received: " + simulator.numReceived);
        System.out.println("Num packets lost: " + simulator.numLoss);
        StdOut.printf("Loss percentage = %.2f\n",
                1.0 * simulator.numLoss / (simulator.numReceived + simulator.numLoss) * 100);
        double averageTime = simulator.totalPacketTime / simulator.numReceived;
        StdOut.printf("Average packet time = %.2fms\n", averageTime / 1e6);

        Coordination C = new Coordination(network.getGraph());
        StdOut.printf("Total cable length = %.2f\n", C.totalCableLength());

        double interval = 1e7;
        int nPoint = (int) (simulator.getTimeLimit() / interval + 1);
        double[][] points = new double[2][nPoint];
        for (int i = 0; i < nPoint; i++) {
            // convert to ms
            points[0][i] = i * interval / 1e6;
            points[1][i] = 0;
        }

        for (Tuple<Double, Integer> packet: simulator.receivedPacket) {
            int i = (int) (packet.a / interval + 1);
            points[1][i] += 1;
        }

        for (int i = 0; i < 10; i++) {
            StdOut.printf("%.2f %.2f\n", points[0][i], points[1][i]);
        }

        double maxThroughput = 0;
        for (int i = 0; i < nPoint; i++) {
            points[1][i] = 100 * points[1][i] * Constant.PACKET_SIZE /
                (trafficPattern.size() * Constant.LINK_BANDWIDTH * interval / 1e9);
            maxThroughput = Math.max(maxThroughput, points[1][i]);
        }

//        for (int i = 0; i < 10; i++) {
//            StdOut.printf("%.2f %.2f\n", points[0][i], points[1][i]);
//        }
//        StdOut.println(points[1][0]);
        StdOut.println(trafficPattern.size() * Constant.LINK_BANDWIDTH * interval / 1e9
            / Constant.PACKET_SIZE);

        StdOut.printf("Maximum throughput : %.2f\n", maxThroughput);

//        XYLineChart chart = new XYLineChart(null, "Time (ms)", "Throughput (%)", points);
//        chart.setAutoRange00(true, true);      // Axes pass through (0,0)
////        chart.toLatexFile("NormalChart.tex", 12, 8);
//        chart.view(800,500);

        // Export to file
        try {
            String fileName = "./results/throughput.txt";
            File file = new File(fileName);
            // creates the file
            file.createNewFile();

            FileWriter writer = new FileWriter(file);

            // Writes the content to the file
            for (Tuple<Double, Integer> packet: simulator.receivedPacket) {
                writer.write(String.format("%.2f %d\n",packet.a, packet.b));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return simulator.receivedPacket.size();
//
//        long averageTime = simulator.totalPacketTime / simulator.numSent;
//        StdOut.printf("For f = %d, average packet time = %.2fms\n", frequency, averageTime / 1e6);
//        return averageTime;
    }

    public static void main(String[] args) {
//        FatTreeGraph G = new FatTreeGraph(10);
//        FatTreeRoutingAlgorithm ra = new FatTreeRoutingAlgorithm(G, false);
        FullGraph G = new FullGraph(256);
        RoutingAlgorithm ra = new FullRoutingAlgorithm(G);
//        SmallWorldGraph G = new SmallWorldGraph(16, 16,
//                "torus", new double[]{1.6, 1.6});
//        RoutingAlgorithm ra = new SmallWorldRoutingAlgorithm(G);
//        CORRAKGraph G = new CORRAKGraph(16, 16, "grid", 1);
//        RoutingAlgorithm ra = new CORRARoutingAlgorithm(G);

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

        StdOut.println(traffic.size());
//        double threshold = 1.1;
//        StdOut.printf("Thresh hold = %.2f\n", threshold);
//        long throughput = experiment.evaluateThroughput(traffic, threshold, false);
//        long throughput = experiment.minAveragePacketTime(traffic, true);
        StdOut.println(experiment.calThroughput(traffic, false));

//        StdOut.printf("Maximum frequency = %d\n", maxFrequency);
//        StdOut.printf("\nThrough put of network %dGb/s\n", throughput / 1000000);
    }
}
