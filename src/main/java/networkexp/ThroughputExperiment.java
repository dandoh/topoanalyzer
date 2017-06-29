package networkexp;

import common.Knuth;
import config.Constant;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;
import graph.Graph;
import network.Host;
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

    public int measureThroughput(Map<Integer, Integer> trafficPattern) {
        final DiscreteEventSimulator sim = new DiscreteEventSimulator(Constant.MAX_TIME);
//        network.clear(); // clear all the data, queue, ... in switches, hosts
        for (Integer source : trafficPattern.keySet()) {
            Integer destination = trafficPattern.get(source);
            // send with the speed of 1e9 bit per 1s (1e9 time unit)
            // => each 12000 time unit send 12000 bit (1500 byte = MTU)
            for (int t = 0; t < Constant.MAX_TIME; t += Constant.PACKET_INTERVAL) {
                final Packet packet = new Packet(source, destination);
                sim.addEvent(new Event(t) {
                    @Override
                    public void execute() {
                        network.getHostById(source).process(packet, sim);
                    }
                });
            }
        }

        System.out.println("Done set up");
        sim.process();

        return 0;
    }

    public static void main(String[] args) {
        FatTreeGraph G = new FatTreeGraph(4);
        RoutingAlgorithm ra = new FatTreeRoutingAlgorithm(G, false);
        Network network = new Network(G, ra);

        ThroughputExperiment experiment = new ThroughputExperiment(network);
        Integer[] sources = G.hosts().toArray(new Integer[0]);
        Integer[] destination = G.hosts().toArray(new Integer[0]);
        Knuth.shuffle(destination);

        Map<Integer, Integer> traffic = new HashMap<>();
        for (int i = 0; i < sources.length; i++) {
            traffic.put(sources[i], destination[i]);
        }


        experiment.measureThroughput(traffic);
    }
}
