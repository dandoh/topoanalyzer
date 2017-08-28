package custom.neighbor;

import custom.smallworld.SmallWorldGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NeighborGraph extends SmallWorldGraph {
    public int delta;
    public int k;

    public NeighborGraph(int nRow, int nCol, String baseType, int delta) {
        super(nRow, nCol, baseType, new double[]{0, 0});

        this.k = 0;
        this.delta = delta;
    }

    public NeighborGraph(int size, int nCol, String fileEdge, String fileGeo, int delta, int k) {
        super("grid", size / nCol, nCol);

        this.delta = delta;
        this.k = k;
        try (Stream<String> stream = Files.lines(Paths.get(fileEdge))) {
            stream.skip(1).forEach(line -> {
                if (line.split(" ").length > 2) return;

                int u = Integer.parseInt(line.split(" ")[0]);
                int v = Integer.parseInt(line.split(" ")[1]);

                if (!this.hasEdge(u, v)) {
                    this.addEdge(u, v);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRandomLink(int u, int v) {
        return !(Math.abs(u - v) == 1 ) && !(Math.abs(u -v) == nCol);
    }

    public boolean isNeighbor(int u, int v) {
        return manhattanDistance(u, v) <= delta;
    }
}
