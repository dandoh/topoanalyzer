package networkexp;

/**
 * Created by Dandoh on 6/27/17.
 */
public abstract class Event {
    public long time;

    public Event(long time) {
        this.time = time;
    }

    public abstract void execute();
}
