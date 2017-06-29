package custom.spaceshuffle;

import graph.Graph;

import java.util.List;

/**
 * Created by Dandoh on 6/27/17.
 */
public class SpaceShuffleGraph extends Graph {
    @Override
    public int V() {
        return 0;
    }

    @Override
    public List<Integer> hosts() {
        return null;
    }

    @Override
    public List<Integer> switches() {
        return null;
    }

    @Override
    public List<Integer> adj(int v) {
        return null;
    }

    @Override
    public boolean isHostVertex(int v) {
        return false;
    }

    @Override
    public boolean isSwitchVertex(int v) {
        return false;
    }
}
