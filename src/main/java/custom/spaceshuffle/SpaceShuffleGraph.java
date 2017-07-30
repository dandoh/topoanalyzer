package custom.spaceshuffle;

import common.StdOut;
import graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by Dandoh on 6/27/17.
 */
public class SpaceShuffleGraph extends Graph {
    public static final int HOST_PER_SWITCH = 1;
    private List<Integer> switches;
    private List<Integer> hosts;
    private final int nHost;
    private final int nSwitch;


    private int numDimensions;
    /**
     * standPoint[d][i] is the stand point value in [0, 1) of vertex i in dimension d
     */
    private double[][] standPoint;
    /**
     * verticesInOrder[d] is the array of vertices sorted in ascending order in dimension d
     */
    private int[][] verticesInOrder;

    public SpaceShuffleGraph(int numSwitch, int numDimensions) {
        this.numDimensions = numDimensions;

        this.nSwitch = numSwitch;
        this.nHost = this.nSwitch * HOST_PER_SWITCH;
        this.V = nHost + nSwitch;
        adj = (List<Integer>[]) new List[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new ArrayList<>();
        }

        this.standPoint = new double[numDimensions][nSwitch];
        // init vertices in order
        this.verticesInOrder = new int[numDimensions][nSwitch];
        for (int d = 0; d < numDimensions; d++) {
            for (int v = 0; v < nSwitch; v++) {
                verticesInOrder[d][v] = v;
            }
        }

        // generate random number for each vertex in each dimension
        Random random = new Random();
        for (int d = 0; d < numDimensions; d++) {
            for (int v = 0; v < nSwitch; v++) {
                standPoint[d][v] = random.nextDouble();
            }

//            StdOut.println(standPoint[d]);
        }

        // sort array of vertices base on stand point
        IntStream.range(0, numDimensions).forEach(d -> {
                    verticesInOrder[d] = Arrays.stream(verticesInOrder[d])
                            .boxed().sorted((v1, v2) -> {
                                if (standPoint[d][v1] < standPoint[d][v2]) return -1;
                                else return 1;
                            })
                            .mapToInt(i -> i)
                            .toArray();
//                    StdOut.println(verticesInOrder[d]);
                }
        );

//        StdOut.println("End sort");

        // add neighbors to each vertex using its order on each dimension
        for (int d = 0; d < numDimensions; d++) {
            for (int i = 0; i < nSwitch; i++) {
                int u = verticesInOrder[d][i];
                int[] ns = new int[]{verticesInOrder[d][(i + 1) % nSwitch], verticesInOrder[d][(i - 1 + nSwitch) % nSwitch]};

//                StdOut.println(u);
//                StdOut.println(ns);
                for (int n : ns) {
                    if (!adj[u].contains(n)) {
                        adj[u].add(n);
                    } else {
                        // MORE PRECISE BUT SLOWER
//                        int[] other = IntStream.range(0, V)
//                                .filter(v -> !adj[u].has(v) && v != u)
//                                .toArray();
//
////                        StdOut.println(other);
//                        int rv = other[random.nextInt(other.length)];
//                        StdOut.println(rv);
                        // LESS PRECISE BUT FASTER
                        int rv = 0;
                        while (true) {
                            rv = random.nextInt(nSwitch);
                            if (!adj[u].contains(rv) && rv != u) break;
                        }
                        adj[u].add(rv);
                    }
                }
            }
        }

        // Add edges between host and switch
        int hostId = nSwitch;
        for (int i = 0; i < nSwitch; i++) {
            for (int j = 0; j < HOST_PER_SWITCH; j++) {
                addEdge(i, hostId);
                hostId++;
            }
        }

    }

    public double distance(int u, int v) {
        if (isHostVertex(u)) u = adj(u).get(0);
        if (isHostVertex(v)) v = adj(v).get(0);

        double res = Double.MAX_VALUE;
        for (int d = 0; d < numDimensions; d++) {
            double absoluteDistance = Math.abs(standPoint[d][u] - standPoint[d][v]);
            double distanceOnDimension = Math.min(absoluteDistance, 1 - absoluteDistance);
            res = Math.min(distanceOnDimension, 1 - distanceOnDimension);
        }

        return res;
    }

    public int numDimensions() {
        return numDimensions;
    }


    public double standPoint(int dimension, int v) {
        return standPoint[dimension][v];
    }


    @Override
    public List<Integer> hosts() {
        if (hosts != null) return hosts;

        hosts = new ArrayList<>();
        for (int i = nSwitch; i < V; i++)
            hosts.add(i);

        return hosts;
    }

    @Override
    public List<Integer> switches() {
        if (switches != null) return switches;

        switches = new ArrayList<>();
        for (int i = 0; i < nSwitch; i++)
            switches.add(i);

        return switches;

    }

    @Override
    public boolean isHostVertex(int v) {
        return v >= nSwitch;
    }

    @Override
    public boolean isSwitchVertex(int v) {
        return v < nSwitch;
    }
}
