package network;

import networkexp.DiscreteEventSimulator;

/**
 * Created by Dandoh on 6/27/17.
 */
public abstract class Node {
    public int id;

    public Node(int id) {
        this.id = id;
    }

    public abstract void process(Packet p, DiscreteEventSimulator sim);
}