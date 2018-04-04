package network;


import common.Queue;
import common.StdOut;
import config.Constant;
import network.port.IntegratedPort;
import network.port.Port;
import simulatedexperiment.DiscreteEventSimulator;
import simulatedexperiment.Event;

/**
 * Created by Dandoh on 6/27/17.
 */
public class L2Object extends NetworkObject {
    private Node u;
    private Node v;
    private IntegratedPort portU;
    private IntegratedPort portV;
    private long bandwidth;
    private long length;

    private double nextAvailableTime = 0;

    public L2Object(Node u, Node v) {
        super(0);
        this.u = u;
        this.v = v;
        portU = new IntegratedPort(u);
        portV = new IntegratedPort(v);

        this.bandwidth = Constant.LINK_BANDWIDTH;
        this.length = Constant.LINK_LENGTH;
    }

    public long serialLatency(int packetSize) {
        return (long) (1e9 * packetSize / this.bandwidth);
    }
    public long propagationLatency() {
        return (long) (length / Constant.PROPAGATION_VELOCITY);
    }

    public boolean isAvailableAt(double time) {
        return nextAvailableTime <= time;
    }

    public double getNextAvailableTime() {
        return nextAvailableTime;
    }

        public void receivePacket(Packet p, Node source, DiscreteEventSimulator sim) {
        sim.log(String.format(
                "Link %d-%d receive a packet", source.id, (source == u ? v : u).id));
        IntegratedPort port = source == u ? portU : portV;
        Port outPort = port.getOutPort();
        Port inPort = (port == portU ? portV : portU).getInPort();

        if (outPort.addPacketToBuffer(p)) {
            if (outPort.getBuffer().size() == 1) {
                transferPacket(outPort, inPort, sim);
            }
        } else {
            sim.numLoss++;
            sim.log(String.format("Drop a packet from %d to %d", p.getSource(), p.getDestination()));
        }
    }

    public void transferPacket(Port outPort, Port inPort, DiscreteEventSimulator sim) {
        sim.log(String.format("Link %d-%d start transfer", u.id, v.id));
        L2Object self = this;
        double currentTime = sim.time();
        Packet p = outPort.getTopPacket();

        long latency = serialLatency(p.getSize()) + propagationLatency();
        sim.getEventList().add(new Event(sim, currentTime + latency) {
            @Override
            public void actions() {
                self.transferCompleted(outPort, inPort, sim);
            }
        });
    }

    public void transferCompleted(Port outPort, Port inPort, DiscreteEventSimulator sim) {
        sim.log(String.format("Link %d-%d completed transfer", u.id, v.id));

        L2Object self = this;
        double currentTime = sim.time();
        Packet p = outPort.getTopPacket();

        outPort.removeTopPacket();

        // Schedule for next packet in out port buffer
        if (!outPort.isBufferEmpty()) {
            sim.getEventList().add(new Event(sim, currentTime + 1) {
                @Override
                public void actions() {
                    self.transferPacket(outPort, inPort, sim);
                }
            });
        }

        if (inPort.canReceive(p)) {
            inPort.addPacketToBuffer(p);
            if (inPort.getBuffer().size() == 1) {
                sendToOutPort(inPort, sim);
            }

        } else {
            sim.numLoss++;
            sim.log(String.format("Drop a packet"));
        }
    }

    public void sendToOutPort(Port inPort, DiscreteEventSimulator sim) {
        L2Object self = this;
        double currentTime = sim.time();
        Node currentNode = inPort.getNode();
        Packet p = inPort.getTopPacket();
        L2Object nextLink = currentNode.processPacket(p, sim);
        if (nextLink != null) {
            sim.getEventList().add(new Event(sim, currentTime + Constant.SWITCH_DELAY) {
                @Override
                public void actions() {
                    inPort.removeTopPacket();
                    nextLink.receivePacket(p, currentNode, sim);

                    // Schedule for next packet in in port buffer
                    if (!inPort.isBufferEmpty()) {
                        sim.getEventList().add(new Event(sim, sim.getTime() + 1) {
                            @Override
                            public void actions() {
                                self.sendToOutPort(inPort, sim);
                            }
                        });
                    }
                }
            });
        }
    }
//    public void nodeProcess(Packet p, Node node, DiscreteEventSimulator sim) {
//        L2Object nextL2 = node.processPacket(p, sim);
//        if (nextL2 != null) {
//
//        }
//    }

//    public void sendStart(Packet p, Node source, DiscreteEventSimulator sim) {
//        Node output = source == u ? v : u;
//        sim.log(String.format("Transferring from %d to %d", source.id, output.id));
//        L2Object self = this;
//        double currentTime = sim.time();
//
//
//        long latency = serialLatency(p.getSize()) + propagationLatency();
//        sim.getEventList().add(new Event(sim, currentTime + latency) {
//            @Override
//            public void actions() {
//                self.sendFinish(p, output, sim);
//            }
//        });
//    }
//
//    public void sendFinish(Packet p, Node output, DiscreteEventSimulator sim) {
//        L2Object self = this;
//        double currentTime = sim.time();
//
//        Node input = output == u ? v : u;
//        sim.log(String.format("Completed transferring from %d to %d", input.id, output.id));
//
//        Port inPort = output == u ? portU : portV;
//        L2Object outL2Object = output.processPacket(p, sim);
//        if (outL2Object != null) {
//            sim.addEvent(new Event(sim, currentTime + Constant.SWITCH_DELAY) {
//                @Override
//                public void actions() {
//                    self.forwardToPort(p, outL2Object, output, sim);
//                }
//            });
//        }
//    }
//
//    public void forwardToPort(Packet p, L2Object outL2Object, Node currentNode, DiscreteEventSimulator sim) {
//        L2Object self = this;
//        double currentTime = sim.time();
//
//        double nextSendTime = outL2Object.nextSendTime(currentNode);
//        if (nextSendTime <= currentTime) {
//
//        } else {
//
//        }
//    }

    public boolean canSend(Packet p, Node source) {
        Port outPort = (source == u ? portU : portV).getOutPort();
        return outPort.canReceive(p);
    }

    @Override
    public void clear() {
        this.nextAvailableTime = 0;
//        this.buffer = new Queue<>();
    }
}
