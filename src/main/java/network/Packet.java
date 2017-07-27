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
    private long startTime;
    private long endTime;

    public void setPredeterminedPath(List<Integer> predeterminedPath) {
        this.predeterminedPath = predeterminedPath;
    }

    private List<Integer> predeterminedPath;

    public Packet(int source, int destination, long startTime) {
        this.source = source;
        this.destination = destination;
        this.size = Constant.PACKET_SIZE;
        this.startTime = startTime;
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

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long timeTravel() {
        return endTime - startTime;
    }
}
