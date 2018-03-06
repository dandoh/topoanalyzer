package simulatedexperiment;

/**
 * Created by Dandoh on 6/27/17.
 */
public abstract class Event extends umontreal.ssj.simevents.Event{
    public int id;

    public Event(DiscreteEventSimulator sim, double time) {
        super(sim);
        this.eventTime = time;
        this.id = ++sim.numEvent;
    }

}
