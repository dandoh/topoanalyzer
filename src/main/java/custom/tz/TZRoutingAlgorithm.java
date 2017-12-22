package custom.tz;

import common.StdOut;
import graph.Graph;
import routing.RoutingAlgorithm;
import routing.RoutingPath;

import java.util.*;

public class TZRoutingAlgorithm implements RoutingAlgorithm{
    private Graph graph;

    private double s;
    private Map<Integer, Map<Integer, List<Integer>>> shortestPaths;
    private List<Integer> A;
    private List<Integer> W;
    private List<List<Integer>> cluster;
    private List<Integer> closestLandmark;

    public TZRoutingAlgorithm(Graph graph) {
        this.graph = graph;

        initTZ();
    }

    @Override
    public int next(int source, int current, int destination) {
        return 0;
    }

    @Override
    public RoutingPath path(int source, int destination) {
        RoutingPath path = new RoutingPath();
        path.path.add(source);
        if (source == destination)
            return path;

        List<Integer> immediatePath = new ArrayList<>();
        List<Integer> immediatePath1 = new ArrayList<>();

        if (cluster.get(source).contains(destination))
            immediatePath = shortestPaths.get(source).get(destination);
        else if (A.contains(destination))
            immediatePath = shortestPaths.get(source).get(destination);
		else {
            immediatePath = shortestPaths.get(source).get(closestLandmark.get(destination));
            immediatePath1 = shortestPaths.get(closestLandmark.get(destination)).get(destination);
        }

        if (immediatePath != null && immediatePath.size() > 1) {
            for (int i = 1; i < immediatePath.size(); i++)
                path.path.add(immediatePath.get(i));
        }

        if (immediatePath != null && immediatePath1.size() > 1) {
            for (int i = 1; i < immediatePath1.size(); i++)
                path.path.add(immediatePath1.get(i));
        }

        return path;
    }

    private void addPaths(int u, int v, List<Integer> path) {
        if (!shortestPaths.containsKey(u)) {
            shortestPaths.put(u, new HashMap<>());
        }

        shortestPaths.get(u).put(v, path);
    }

    private void initTZ() {
        int nSwitch = graph.switches().size();
        this.s = Math.sqrt(nSwitch/(Math.log(nSwitch) / Math.log(2)));

        this.shortestPaths = new HashMap<>();
        this.A = new ArrayList<>();
        this.W = new ArrayList<>();
        this.cluster = new ArrayList<>();
        this.closestLandmark = new ArrayList<>();


//        Map<Integer, List<Integer>> ps = graph.shortestPaths(0);
//        StdOut.println(ps);
//        return;
        // Find all shortest paths
//        for (int u : graph.switches()) {
//            Map<Integer, List<Integer>> paths = graph.shortestPaths(u);
//            for (int v : graph.switches()) {
//                if (paths.containsKey(v)) {
////                if (shortestPaths.containsKey(u) && shortestPaths.get(u).containsKey(v)) {
////                    continue;
////                }
//                    this.addPaths(u, v, paths.get(v));
////                List<Integer> rPath = new ArrayList<>(path);
////                Collections.reverse(rPath);
////                this.addPaths(v, u, rPath);
//                }
//            }
//
//            StdOut.printf("Done for %d\n", u);
//
//        }
        shortestPaths = graph.allShortestPaths();
        StdOut.println("Find all paths");

        // Init cluster, W, closetLandmark
        for (int i = 0; i < nSwitch; i++) {
            W.add(i);
            closestLandmark.add(-1);
            cluster.add(new ArrayList<>());
        }

        while (W.size() > 0) {
            List<Integer> A1 = this.TZ_sample(W, s);
            A.addAll(A1);

            // Tinh closest Lanmarks cua 1 node v
            for (int v = 0; v < nSwitch; v++) {
                if (!A.contains(v)) {
                    closestLandmark.set(v, A.get(0));
                    for (int landmark : A)
                        if (shortestPaths.get(v).get(closestLandmark.get(v)).size() >
                                shortestPaths.get(v).get(landmark).size())
                            closestLandmark.set(v, landmark);
                }
            }

            // Tinh cluster cua node w
            for (int widx = 0; widx < nSwitch; widx++) {
                cluster.set(widx, new ArrayList<>());
                for (int v = 0; v < nSwitch; v++)
                    if (widx != v)
                        if (closestLandmark.get(v) != -1 &&
                                shortestPaths.get(widx).get(v).size() <
                                shortestPaths.get(v).get(closestLandmark.get(v)).size())
                            cluster.get(widx).add(v);
            }

            // Tinh W
            this.W = new ArrayList<>();
            for (int widx = 0; widx < nSwitch; widx++) {
                if (cluster.get(widx).size() > 4 * nSwitch / s)
                    W.add(widx);
            }
        }
    }

    private List<Integer> TZ_sample(List<Integer>W, double s) {
        int nodeW = W.size();
        if (nodeW > 0) {
            if (nodeW < s) {
                return W;
            } else {
                Random r = new Random();
                double prob_net = s / nodeW;
                List<Integer> new_W = new ArrayList<>();
                for (int node : W) {
                    double random_float = r.nextDouble();
                    if (random_float < prob_net)
                        new_W.add(node);
                }
                return new_W;
            }
        }
        return W;

    }
}
