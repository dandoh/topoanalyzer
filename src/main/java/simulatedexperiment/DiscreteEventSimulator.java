package simulatedexperiment;

import common.StdOut;
import umontreal.ssj.simevents.Simulator;

import java.util.PriorityQueue;

/**
 * Created by Dandoh on 6/27/17.
 */
public class DiscreteEventSimulator extends Simulator {
    private long stime = 0; // system time
    private int timeLimit;
    public int numReceived = 0;
    public int numSent = 0;
    public int numLoss = 0;
    public long totalPacketTime = 0;
    public int numEvent = 0;
    private boolean isLimit;
    private boolean verbose;

    public DiscreteEventSimulator() {
        super();
    }

    public DiscreteEventSimulator(boolean isLimit, int timeLimit, boolean verbose) {
        super();
        this.isLimit = isLimit;
        this.verbose = verbose;
    }

    public double getTime() {
        return currentTime;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void log(String message) {
        if (this.verbose) {
            StdOut.printf("At %d: %s\n", (long) this.getTime(), message);
        }
    }
}
