package network;

import java.util.List;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Packet {
    private int source;
    private int destination;

    public void setPredeterminedPath(List<Integer> predeterminedPath) {
        this.predeterminedPath = predeterminedPath;
    }

    private List<Integer> predeterminedPath;

    public Packet(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }
}
