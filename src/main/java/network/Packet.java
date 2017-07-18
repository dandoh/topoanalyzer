package network;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import config.Constant;

import java.util.List;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Packet {
    private int source;
    private int destination;
    private int size;

    public void setPredeterminedPath(List<Integer> predeterminedPath) {
        this.predeterminedPath = predeterminedPath;
    }

    private List<Integer> predeterminedPath;

    public Packet(int source, int destination) {
        this.source = source;
        this.destination = destination;
        this.size = Constant.PACKET_SIZE;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    public int getSize() {
        return size;
    }
}
