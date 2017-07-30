package custom.smallworld;

import org.junit.Test;

import static org.junit.Assert.*;

public class GridGraphTest {
    @Test
    public void checkContruction() throws Exception {
        GridGraph graph = new GridGraph(4, 4, "torus");
        assertEquals(graph.V(), 4 * 4 * 2);
        for (int i : graph.switches())
            assertEquals(graph.adj(i).size(), 4 + 1);
        for (int i : graph.hosts())
            assertEquals(graph.adj(i).size(), 1);

        assertTrue(graph.hasEdge(0, 1));
        assertTrue(graph.hasEdge(0, 3));
        assertTrue(graph.hasEdge(0, 4));
        assertTrue(graph.hasEdge(0, 12));
        assertTrue(graph.hasEdge(0, 16));

        graph = new GridGraph(4, 4, "grid");
        assertEquals(graph.V(), 4 * 4 * 2);
        for (int i : graph.switches())
            assertTrue(graph.adj(i).size() >= 3 && graph.adj(i).size() <= 5);
    }

    @Test
    public void distance() throws Exception {
    }

}