package graph;

import java.util.List;


public abstract class Graph {
    protected int V;
    protected int E;
    protected List<Integer>[] adj;

    public void addEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        E++;
        adj[v].add(w);
        adj[w].add(v);
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    public boolean hasEdge(int u, int v) {
        return adj[u].contains(v);
    }

    public int V() { return V; }

    public List<Integer> adj(int v) { return adj[v]; }

    public abstract List<Integer> hosts();

    public abstract List<Integer> switches();

    public abstract boolean isHostVertex(int v);

    public abstract boolean isSwitchVertex(int v);
}
