package network;


import networkexp.DiscreteEventSimulator;
import networkexp.Event;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Link {
    private Endpoint u;
    private Endpoint v;
    private int capacity; // Gbps

    public void handle(Packet packet, Endpoint input, DiscreteEventSimulator desim) {
        // move packet from input endpoint to output endpoint
    }
}
