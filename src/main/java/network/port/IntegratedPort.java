package network.port;

import network.Node;

public class IntegratedPort {
    private Port inPort;
    private Port outPort;

    public IntegratedPort(Node node) {
        this.inPort = new Port(node);
        this.outPort = new Port(node);
    }

    public Port getInPort() {
        return inPort;
    }

    public Port getOutPort() {
        return outPort;
    }
}
