package custom.fattree;

import graph.Graph;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dandoh on 5/24/17.
 */
public class FatTreeGraphTest {
    @Test
    public void isHostVertex() throws Exception {

    }

    @Test
    public void switchType() throws Exception {
        FatTreeGraph graph = new FatTreeGraph(4);
        assertEquals(graph.switchType(4), FatTreeGraph.EDGE);
        assertEquals(graph.switchType(14), FatTreeGraph.AGG);
        assertEquals(graph.switchType(33), FatTreeGraph.CORE);
    }

    @Test
    public void getAddress() throws Exception {
        FatTreeGraph fatGraph = new FatTreeGraph(4);
        assertEquals(fatGraph.getAddress(17), new Address(10, 2, 0, 3));
        assertEquals(fatGraph.getAddress(5),  new Address(10, 0, 1, 1));
        assertEquals(fatGraph.getAddress(32), new Address(10, 4, 1, 1));
        assertEquals(fatGraph.getAddress(35), new Address(10, 4, 2, 2));
    }

    @Test
    public void isSwitchVertex() throws Exception {
        FatTreeGraph fatGraph = new FatTreeGraph(4);
        assertFalse(fatGraph.isSwitchVertex(2));
        assertFalse(fatGraph.isSwitchVertex(16));
        assertFalse(fatGraph.isSwitchVertex(19));
        assertTrue(fatGraph.isSwitchVertex(32));
    }

    @Test
    public void isSwitchVertex1() throws Exception {
        FatTreeGraph fatGraph = new FatTreeGraph(6);
        assertFalse(fatGraph.isSwitchVertex(37));
        assertFalse(fatGraph.isSwitchVertex(20));
        assertFalse(fatGraph.isSwitchVertex(4));
        assertTrue(fatGraph.isSwitchVertex(90));
        assertTrue(fatGraph.isSwitchVertex(1000));
        assertTrue(fatGraph.isSwitchVertex(44));
        assertTrue(fatGraph.isSwitchVertex(27));
    }


    @Test
    public void testFatTreeConstruction1() throws Exception {
        Graph fatGraph = new FatTreeGraph(4);
        assertTrue(fatGraph.hasEdge(0, 4));
        assertTrue(fatGraph.hasEdge(1, 4));
        assertTrue(fatGraph.hasEdge(8, 12));
        assertTrue(fatGraph.hasEdge(11, 13));
        assertTrue(fatGraph.hasEdge(5, 6));
        assertTrue(fatGraph.hasEdge(6, 33));
        assertTrue(fatGraph.hasEdge(30, 32));
        assertTrue(fatGraph.hasEdge(31, 35));
        assertTrue(fatGraph.hasEdge(28, 31));

    }

    @Test
    public void testFatTreeConstruction2() throws Exception {
        Graph fatGraph = new FatTreeGraph(6);
        assertTrue(fatGraph.hasEdge(0, 9));
        assertTrue(fatGraph.hasEdge(1, 9));
        assertTrue(fatGraph.hasEdge(2, 9));
        assertTrue(fatGraph.hasEdge(24, 29));
        assertTrue(fatGraph.hasEdge(33, 40));
        assertTrue(fatGraph.hasEdge(40, 44));
        assertTrue(fatGraph.hasEdge(12, 90));
        assertTrue(fatGraph.hasEdge(28, 93));
        assertTrue(fatGraph.hasEdge(28, 95));


    }

}