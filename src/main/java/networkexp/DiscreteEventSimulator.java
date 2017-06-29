package networkexp;

import java.util.PriorityQueue;

/**
 * Created by Dandoh on 6/27/17.
 */
public class DiscreteEventSimulator {
    private int stime = 0; // system time
    private PriorityQueue<Event> pq;
    private int timeLimit;

    public DiscreteEventSimulator(int timeLimit) {
        this.timeLimit = timeLimit;
        pq = new PriorityQueue<>((e1, e2) -> e1.time - e2.time);
    }

    public void process() {
        while (!pq.isEmpty() && stime < timeLimit) {
            Event top = pq.poll();
            stime = top.time;
            top.execute();
        }
    }

    public void addEvent(Event event) {
        pq.add(event);
    }

    public int getTime() {
        return stime;
    }
}
