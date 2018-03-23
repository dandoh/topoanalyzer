package network;

import common.Queue;
import simulatedexperiment.DiscreteEventSimulator;
import sun.nio.ch.Net;

public abstract class NetworkObject {
    public int id;
    protected Queue<Packet> buffer;
    protected double currentBufferSize;
    protected double maxBufferSize = -1;

    public NetworkObject(int id) {
        this.id = id;
        this.buffer = new Queue<>();
    }

    protected void addToBuffer(Packet p) {
        buffer.enqueue(p);
        currentBufferSize += p.getSize();
    }

    protected Packet dequeueBuffer() {
        Packet p = buffer.dequeue();
        currentBufferSize -= p.getSize();
        return p;
    }

    public double getCurrentBufferSize() {
        return currentBufferSize;
    }

    public abstract void receive(Packet p, NetworkObject source, DiscreteEventSimulator sim);
    public abstract void process(DiscreteEventSimulator sim);
    public abstract void send(NetworkObject destination, DiscreteEventSimulator sim);

    public abstract boolean canReceive(Packet p, double time);

    public abstract void clear();

    public abstract double getNextAvailableTime();
}
