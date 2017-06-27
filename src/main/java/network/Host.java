package network;

import networkexp.DiscreteEventSimulator;
import networkexp.Event;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Host extends Endpoint {
    // link to the ToR switch
    private Link link;

    public Host(Link link) {
        this.link = link;
    }

    public void send(Packet packet, DiscreteEventSimulator desim) {

    }

    public void receive(Packet packet, DiscreteEventSimulator desim) {

    }
}
