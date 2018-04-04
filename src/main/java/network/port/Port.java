package network.port;

import common.Queue;
import network.Node;
import network.Packet;

public class Port {
    protected Node node;
    protected Queue<Packet> buffer;
    protected double packetSize;

    public Port(Node node) {
        this.buffer = new Queue<>();
        this.node = node;
    }

    public boolean addPacketToBuffer(Packet p) {
        if (!canReceive(p)) {
            return false;
        }
        buffer.enqueue(p);
        return true;
    }

    public Queue<Packet> getBuffer() {
        return buffer;
    }

    public Packet getTopPacket() {
        return buffer.peek();
    }

    public Packet removeTopPacket() {
        return buffer.dequeue();
    }

    public boolean canReceive(Packet p) {
        return buffer.size() == 0;
    }

    public boolean isBufferEmpty() {
        return buffer.isEmpty();
    }

    public Node getNode() {
        return node;
    }
}
