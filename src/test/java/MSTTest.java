import org.example.Graph;
import org.example.KruskalMST;
import org.example.PrimMST;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MSTTest {

    @Test
    void kruskalEqualsPrimForSimpleGraph() {
        Graph g = new Graph();
        g.addEdge("A", "B", 4);
        g.addEdge("A", "C", 3);
        g.addEdge("B", "C", 2);
        g.addEdge("B", "D", 5);
        g.addEdge("C", "D", 7);

        KruskalMST.Result kr = KruskalMST.run(g);
        PrimMST.Result pr = PrimMST.run(g);

        assertEquals(kr.totalWeight, pr.totalWeight, 1e-6, "MST weights must match");
        assertEquals(g.vertexCount() - 1, kr.mstEdges.size());
        assertEquals(g.vertexCount() - 1, pr.mstEdges.size());
    }

    @Test
    void handlesDisconnectedGraph() {
        Graph g = new Graph();
        g.addEdge("A", "B", 1);
        g.addVertex("C");

        KruskalMST.Result kr = KruskalMST.run(g);
        assertTrue(kr.mstEdges.size() < g.vertexCount() - 1);
    }
}