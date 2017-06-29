package network;

import networkexp.DiscreteEventSimulator;

import java.util.Map;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Switch extends Node {

    // map from index to link
    private Map<Integer, Link> links;

    @Override
    public void process(Packet p, DiscreteEventSimulator sim) {

    }
}
