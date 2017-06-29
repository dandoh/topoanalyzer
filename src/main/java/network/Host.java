package network;

import networkexp.DiscreteEventSimulator;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Host extends Node {
    // link to the ToR switch
    private Link link;

    public Host(Link link) {
        this.link = link;
    }

    @Override
    public void process(Packet p, DiscreteEventSimulator sim) {

    }
}
