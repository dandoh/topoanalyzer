package network;


import networkexp.DiscreteEventSimulator;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Link {
    private Node u;
    private Node v;
    private int capacity; // Gbps

    public void handle(Packet packet, Node input, DiscreteEventSimulator desim) {
        // move packet from input endpoint to output endpoint
    }
}
