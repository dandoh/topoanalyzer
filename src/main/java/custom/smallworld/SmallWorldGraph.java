package custom.smallworld;

import common.Knuth;
import common.RandomSet;
import common.StdOut;
import common.Tuple;
import graph.Graph;
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;

/**
 * Created by Dandoh on 6/27/17.
 */
public class SmallWorldGraph extends GridGraph{
    private static Random random = new Random();
    private double[] alphas;
    private Map<Tuple<Integer, Integer>, Integer> randomLinks;
    private int[] totalRandomLink;

    /**
     * Constuctor of SmallWorldGraph for fixed degree
     */
    public SmallWorldGraph(int nRow, int nCol, String baseType, double[] alphas) {
        super(nRow, nCol, baseType);

        this.alphas = alphas;
        randomLinks = new HashMap<Tuple<Integer, Integer>, Integer>();
        this.totalRandomLink = new int[alphas.length];

        generateFixedDegree(alphas);
    }

    /**
     * Constuctor of SmallWorldGraph for varied degree
     */
    public SmallWorldGraph(int nRow, int nCol, String baseType, double[] alphas, boolean isBounded) {
        super(nRow, nCol, baseType);
        this.alphas = alphas;
        randomLinks = new HashMap<Tuple<Integer, Integer>, Integer>();
        this.totalRandomLink = new int[alphas.length];

        generateVariedDegree(alphas, isBounded);
    }

    /**
     * Constructor for non-random links
     */
    public SmallWorldGraph(String baseType, int nRow, int nCol) {
        super(nRow, nCol, baseType);
    }

    public void addEdge(int u, int v, int alphaIndex) {
        addEdge(u, v);

        randomLinks.put(new Tuple(u, v), alphaIndex);
        totalRandomLink[alphaIndex]++;
    }

    /**
     * @param alphas
     */
    private void generateFixedDegree(double[] alphas) {
        for (int i = 0; i < alphas.length; i++) {
            double alpha = alphas[i];
            // add random links with alpha parameter
            RandomSet<Integer> nodes = new RandomSet<>();
            nodes.addAll(this.switches());

            // StdOut.println(nodes.size());
            while (!nodes.isEmpty()) {
                // pick u randomly from nodes
                int source = nodes.pollRandom(random);
                // StdOut.printf("Poll random: Got %d, now we have %d vertices \n", source, nodes.size());

                // get the candidates that can receive a random link from this source
                List<Integer> candidates = new ArrayList<>();
                for (int node : nodes) {
                    if (!this.hasEdge(source, node)) {
                        candidates.add(node);
                    }
                }

                if (candidates.size() == 0) {
                    break;
                }
                // get the index with accumulate probability list
                List<Tuple<Integer, Double>> idWithAccProb =
                        indicesWithAccumulateProb(source, alpha, candidates);

                // pick a random destination
                int destination = pickDestination(idWithAccProb);
                nodes.remove(destination);

                // create link between source and destination
                this.addEdge(source, destination, i);
            }
        }
    }

    /**
     * @param alphas
     * @param isBounded
     * @return
     */
    private void generateVariedDegree(double[] alphas, boolean isBounded) {
        Graph baseGraph = new GridGraph(this);
        for (int i = 0; i < alphas.length; i++) {
            double alpha = alphas[i];
            // get a permutation of vertices
            Integer[] permutationOfVertices = range(0, switches().size() - 1);
            Knuth.shuffle(permutationOfVertices);

            for (int source : permutationOfVertices) {
                if (isBounded && this.degree(source) > baseGraph.degree(source) + alphas.length * 2) {
                    continue;
                }

                // System.out.println("source is " + source);
                List<Integer> candidates = new ArrayList<>();
                for (int v : switches()) {
                    if (v != source && (!isBounded || degree(v) <= baseGraph.degree(v) + (i + 1) * 2)
                            && !hasEdge(v, source)) {
                        candidates.add(v);
                    }
                }

                // get the index with accumulate probability list
                List<Tuple<Integer, Double>> idWithAccProb =
                        indicesWithAccumulateProb(source, alpha, candidates);

                // pick a random destination
                int destination = pickDestination(idWithAccProb);

                // System.out.printf("destination is %d \n", destination);
                // create link between source and destination
                this.addEdge(source, destination, i);
            }
        }
    }

    /**
     * @param idWithAccProb
     * @return
     */
    private static int pickDestination(List<Tuple<Integer, Double>> idWithAccProb) {
        // printIterable(idWithAccProb);
        double r = random.nextDouble();

        for (Tuple<Integer, Double> tuple : idWithAccProb) {
            if (tuple.b >= r) {
                return tuple.a;
            }
        }

        return idWithAccProb.get(idWithAccProb.size() - 1).a;
    }

    private List<Tuple<Integer, Double>> indicesWithAccumulateProb(int source,
                                                                          double alpha,
                                                                          Iterable<Integer> candidates) {
        List<Tuple<Integer, Double>> tempResult = new ArrayList<>();
        List<Tuple<Integer, Double>> result = new ArrayList<>();
        double sumProbs = 0;
        for (int candidate : candidates) {
            double smallWorldRatio = 1d / Math.pow(distance(source, candidate), alpha);
            sumProbs += smallWorldRatio;
            tempResult.add(new Tuple<>(candidate, sumProbs));
        }


        for (Tuple<Integer, Double> tuple : tempResult) {
            result.add(new Tuple<>(tuple.a, tuple.b / sumProbs));
        }

        return result;
    }


    private static Integer[] range(int begin, int end) {
        Integer[] result = new Integer[end - begin + 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = begin + i;
        }

        return result;
    }

    public List<List<Integer>> kHopNeighbor(int id, int k) {

        List<List<Integer>> neighbors = new ArrayList<List<Integer>>();
        Queue<Integer> queue = new LinkedList<>();

        boolean[] visited = new boolean[V];
        int[] trace = new int[this.V];
        queue.add(id);
        visited[id] = true;
        trace[id] = -1;
        for (int i = 0; i < k; i++) {
            int uNode = queue.remove();
            for (int vNode : adj(uNode)) {
                if (!visited[vNode] && isSwitchVertex(vNode)) {
                    visited[vNode] = true;
                    queue.add(vNode);
                    trace[vNode] = uNode;

                    int nextNode = vNode;
                    while (trace[nextNode] != id) {
                        nextNode = trace[nextNode];
                    }
                    List<Integer> info = new ArrayList<>();
                    info.add(vNode);
                    info.add(i + 1);
                    info.add(nextNode);
                    neighbors.add(info);
                }
            }
        }
        return neighbors;
    }

    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        String baseType = "torus"; //args[0];
        String randomLinkType = "fixed"; //args[1];
        int nRow = 4; //Integer.parseInt(args[2]);
        int nCol = 4; //Integer.parseInt(args[3]);
        int nAlpha = 2; //Integer.parseInt(args[4]);

        double[] alphas = new double[nAlpha];
        for (int i = 0; i < nAlpha; i++) {
            alphas[i] = 1.6; //Double.parseDouble(args[i + 5]);
        }

        boolean isBounded = false;
        if (randomLinkType.equals("varied"))
            isBounded = false; //Boolean.parseBoolean(args[nAlpha + 5]);

        SmallWorldGraph graph;
        if (randomLinkType.equals("fixed")) {
            // fixed degree
            graph = new SmallWorldGraph(nRow, nCol, baseType, alphas);
        } else {
            // varied degree
            graph = new SmallWorldGraph(nRow, nCol, baseType, alphas, isBounded);
        }

        StdOut.println(graph);

        time = System.currentTimeMillis() - time;
        StdOut.println(String.format("Runtime = %f\n", 1.0 * time / 1000));

        // modifiedGraph.writeFileEdges(fileName + ".edges");
    }

}
