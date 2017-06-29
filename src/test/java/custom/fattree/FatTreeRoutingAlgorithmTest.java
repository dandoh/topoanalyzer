package custom.fattree;

import network.Network;
import org.junit.Test;
import routing.RoutingAlgorithm;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Dandoh on 6/29/17.
 */
public class FatTreeRoutingAlgorithmTest {
    @Test
    public void next() throws Exception {
        FatTreeGraph fatTreeGraph = new FatTreeGraph(4);
        FatTreeRoutingAlgorithm ra = new FatTreeRoutingAlgorithm(fatTreeGraph, false);

        assertEquals(ra.next(2, 2, 17), 5);
        assertEquals(ra.next(2, 5, 17), 6);
        assertEquals(ra.next(2, 6, 17), 33);
        assertEquals(ra.next(2, 33, 17), 22);
        assertEquals(ra.next(2, 22, 17), 20);
        assertEquals(ra.next(2, 20, 17), 17);
    }

    @Test
    public void path() throws Exception {
        FatTreeGraph fatTreeGraph = new FatTreeGraph(4);
        RoutingAlgorithm ra = new FatTreeRoutingAlgorithm(fatTreeGraph, false);

        List<Integer> trace = ra.path(2, 17).path;
        Integer[] traceResult = trace.toArray(new Integer[0]);

        assertArrayEquals(traceResult, new Integer[]{5, 6, 33, 22, 20});
    }

}