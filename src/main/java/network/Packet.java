package network;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import config.Constant;

import java.util.List;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Packet {
    public int id;
    private int source;
    private int destination;
    private int size;

    private double startTime;
    private double endTime;

    private List<Integer> predeterminedPath;
    public void setPredeterminedPath(List<Integer> predeterminedPath) {
        this.predeterminedPath = predeterminedPath;
    }

    public Packet(int id, int source, int destination, double startTime) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.size = Constant.PACKET_SIZE;
        this.startTime = startTime;
        this.endTime = -1;
    }

    public Packet(int id, Packet p, double startTime) {
        this.id = id;
        this.source = p.getSource();
        this.destination = p.getDestination();
        this.size = p.getSize();
        this.startTime = startTime;
        this.endTime = -1;
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

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public double timeTravel() {
        return endTime - startTime;
    }

    public boolean isTransmitted() {
        return endTime > startTime;
    }
}
