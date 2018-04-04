package network;

import config.Constant;
import simulatedexperiment.DiscreteEventSimulator;

/**
 * Created by Dandoh on 6/27/17.
 */
public abstract class Node extends NetworkObject {
    public Node(int id) {
        super(id);
    }

    public abstract L2Object processPacket(Packet p, DiscreteEventSimulator sim);
}
