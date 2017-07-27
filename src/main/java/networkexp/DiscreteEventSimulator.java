package networkexp;

import common.StdOut;

import java.util.PriorityQueue;

/**
 * Created by Dandoh on 6/27/17.
 */
public class DiscreteEventSimulator {
    private long stime = 0; // system time
    private PriorityQueue<Event> pq;
    private int timeLimit;
    public int numReceived = 0;
    public int numSent = 0;
    public long totalPacketTime = 0;
    private boolean isLimit;
    private boolean verbose;

    public DiscreteEventSimulator(int timeLimit) {
        this.timeLimit = timeLimit;
        this.isLimit = true;
        pq = new PriorityQueue<>((e1, e2) -> {
            if (e1.time < e2.time) return -1;
            else if (e1.time > e2.time) return 1;
            else return 0;
        });
    }

    public DiscreteEventSimulator(boolean isLimit, int timeLimit, boolean verbose) {
        this(timeLimit);
        this.isLimit = isLimit;
        this.verbose = verbose;
    }

    public void process() {
        while (!pq.isEmpty()) {
            if (isLimit) {
                if (stime < timeLimit) break;
            }
            Event top = pq.poll();
            stime = top.time;
            top.execute();
        }
    }

    public void addEvent(Event event) {
        pq.add(event);
    }

    public long getTime() {
        return stime;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
