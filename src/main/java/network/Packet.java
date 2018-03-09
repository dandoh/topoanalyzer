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
    private double timeOut;

    private double startTime;
    private double endTime;

    private List<Integer> predeterminedPath;
    public void setPredeterminedPath(List<Integer> predeterminedPath) {
        this.predeterminedPath = predeterminedPath;
    }

    public Packet(int source, int destination, double startTime) {
        this.source = source;
        this.destination = destination;
        this.size = Constant.PACKET_SIZE;
        this.startTime = startTime;
        this.endTime = -1;
        this.timeOut = -1;
    }

    public Packet(Packet p, double startTime) {
        this.source = p.getSource();
        this.destination = p.getDestination();
        this.size = p.getSize();
        this.startTime = startTime;
        this.endTime = -1;
        this.timeOut = -1;
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

    public double getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(double timeOut) {
        this.timeOut = timeOut;
    }
}
