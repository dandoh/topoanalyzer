package graph;

import java.util.List;


public abstract class Graph {
    public abstract int V();

    public abstract List<Integer> hosts();

    public abstract List<Integer> switches();

    public abstract List<Integer> adj(int v);

    public abstract boolean isHostVertex(int v);

    public abstract boolean isSwitchVertex(int v);
}
