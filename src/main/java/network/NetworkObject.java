package network;

import common.Queue;
import simulatedexperiment.DiscreteEventSimulator;
import sun.nio.ch.Net;

public abstract class NetworkObject {
    public int id;

    public NetworkObject(int id) {
        this.id = id;
    }

    public abstract void clear();
}
