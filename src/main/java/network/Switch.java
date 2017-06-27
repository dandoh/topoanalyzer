package network;

import networkexp.DiscreteEventSimulator;

import java.util.List;
import java.util.Map;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Switch extends Endpoint{

    // map from index to link
    private Map<Integer, Link> links;

    public Switch(Map<Integer, Link> links) {
        this.links = links;
    }

    public void forward(Packet packet, DiscreteEventSimulator desim) {

    }
}
