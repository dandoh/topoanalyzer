package custom.corra;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CORRATable {
    public HashMap<Integer, List<Integer>> neighborTable;
    public HashMap<Integer, Integer> br1;
    public HashMap<Integer, Integer> br2;
    public HashMap<Integer, List<Integer>> brTable;
    public boolean isReceiveBr = false;

    public CORRATable() {
        neighborTable = new HashMap<>();
        br1 = new HashMap<>();
        br2 = new HashMap<>();
        brTable = new HashMap<>();
    }

    public void setNeighborTable(HashMap<Integer, List<Integer>> neighborTable) {
        this.neighborTable = neighborTable;
    }

    public void addBr1(int destination, int nextHop) {
        br1.put(destination, nextHop);
    }

    public void addBr2(int destination, int nextHop) {
        br2.put(destination, nextHop);
    }

    /*
     * Add bridge to brTable
     * @param
     *      destination: Destination of this bridge
     *      nextHop: Next node from current node
     *      hop: Number of hop it takes to go to destination by this path
     *      typeBr: type of bridge; 1 - br1, 2 - br2, 3 - STP
     */
    public void addBrRoute(int destination, int nextHop, int hop, int typeBr) {
        if (!brTable.containsKey(destination) || brTable.get(destination).get(1) > hop) {
            brTable.put(destination, Arrays.asList(nextHop, hop, typeBr));
        }
    }

    public int getNextNeighborNode(int u) {
        return neighborTable.containsKey(u) ? neighborTable.get(u).get(0) : -1;
    }

    public int getNextNode(int u) {
        return brTable.containsKey(u) ? brTable.get(u).get(0) : -1;
    }

    public int size() {
        return this.neighborTable.size() + this.brTable.size();
    }
}
