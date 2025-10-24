package org.example;

import java.util.*;

public class KruskalMST {

    public static class Result {
        public final List<Edge> mstEdges;
        public final double totalWeight;
        public final int V;
        public final int E;
        public final long comparisons;
        public final long unions;
        public final long timeMs;

        public Result(List<Edge> mstEdges, double totalWeight, int V, int E, long comparisons, long unions, long timeMs) {
            this.mstEdges = mstEdges;
            this.totalWeight = totalWeight;
            this.V = V;
            this.E = E;
            this.comparisons = comparisons;
            this.unions = unions;
            this.timeMs = timeMs;
        }
    }

    public static Result run(Graph g) {
        long t0 = System.nanoTime();
        List<Edge> edges = g.getEdgesCopy();
        Collections.sort(edges);

        DisjointSet ds = new DisjointSet();
        ds.makeSet(g.vertices);

        List<Edge> mst = new ArrayList<>();
        double total = 0.0;
        long comparisons = 0;
        long unions = 0;

        for (Edge e : edges) {
            comparisons += 2;
            String ru = ds.find(e.u);
            String rv = ds.find(e.v);
            if (ru == null || rv == null) continue;
            if (!ru.equals(rv)) {
                boolean joined = ds.union(ru, rv);
                if (joined) {
                    unions++;
                    mst.add(e);
                    total += e.w;
                }
            }
            if (mst.size() == g.vertexCount() - 1) break;
        }

        long t1 = System.nanoTime();
        return new Result(mst, total, g.vertexCount(), g.edgeCount(), comparisons, unions, (t1 - t0) / 1_000_000);
    }
}


