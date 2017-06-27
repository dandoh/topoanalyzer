package routing;


public interface RoutingAlgorithm {

    int next(int current, int destination);

    RoutingPath path(int source, int destination);
}
