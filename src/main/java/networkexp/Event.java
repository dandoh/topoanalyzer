package networkexp;

/**
 * Created by Dandoh on 6/27/17.
 */
public abstract class Event {
    public long time;
    public int id;

    public Event(long time, int id) {
        this.time = time;
        this.id = id;
    }

    public abstract void execute();
}
