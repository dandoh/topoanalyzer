package networkexp;

import network.Host;
import network.Network;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by Dandoh on 6/27/17.
 */
public class ThroughputExperiment {
    private final static int MAX_TIME = (int) 1e7;

    private Network network;

    public ThroughputExperiment(Network network) {
        this.network = network;
    }

    public int measureThroughput(Map<Integer, Integer> trafficPattern) {
        DiscreteEventSimulator des = new DiscreteEventSimulator(MAX_TIME);
        network.clear(); // clear all the data, queue, ... in switches, hosts

        return 0;
    }
}
