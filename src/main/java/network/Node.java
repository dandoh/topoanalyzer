package network;

import simulatedexperiment.DiscreteEventSimulator;

/**
 * Created by Dandoh on 6/27/17.
 */
public abstract class Node extends NetworkObject {
    public Node(int id) {
        super(id);
    }

    @Override
    public boolean canReceive(Packet p, double time) {
        if (maxBufferSize == -1) {
            return true;
        }
        return currentBufferSize + p.getSize() <= maxBufferSize;
    }

    @Override
    public double getNextAvailableTime() {
        // TODO
        return -1;
    }
}
