package network;


import com.sun.tools.internal.jxc.ap.Const;
import config.Constant;
import networkexp.DiscreteEventSimulator;
import networkexp.Event;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Link {
    private Node u;
    private Node v;
    private int nextAvailableTime = 0;

    public Link(Node u, Node v) {
        this.u = u;
        this.v = v;
    }

    public void handle(Packet packet, Node input, DiscreteEventSimulator sim) {
        // move packet from input endpoint to output endpoint
        Node output = input == u ? v : u;
        int currentTime = sim.getTime();
        if (currentTime >= nextAvailableTime) {
            System.out.println(
                    String.format("Transferring from %d to %d at %d", input.id, output.id, sim.getTime()));
            nextAvailableTime = currentTime + Constant.LINK_DELAY;
            sim.addEvent(new Event(currentTime + Constant.LINK_DELAY) {
                @Override
                public void execute() {
                    System.out.println(
                            String.format("Complete transferring from %d to %d at %d", input.id, output.id, sim.getTime()));
                    output.process(packet, sim);
                }
            });
        } else {
            System.out.println(
                    String.format("Transfer from %d to %d delayed at %d", input.id, output.id, sim.getTime()));
            sim.addEvent(new Event(nextAvailableTime) {
                @Override
                public void execute() {
                    Link.this.handle(packet, input, sim);
                }
            });
        }
    }
}
