package org.example;

import java.util.*;

public class PrimMST {

    public static class Result {
        public final List<Edge> mstEdges;
        public final double totalWeight;
        public final int V;
        public final int E;
        public final long edgeChecks; // number of edges polled/checked
        public final long timeMs;

        public Result(List<Edge> mstEdges, double totalWeight, int V, int E, long edgeChecks, long timeMs) {
            this.mstEdges = mstEdges;
            this.totalWeight = totalWeight;
            this.V = V;
            this.E = E;
            this.edgeChecks = edgeChecks;
            this.timeMs = timeMs;
        }
    }

    public static Result run(Graph g) {
        long t0 = System.nanoTime();
        int V = g.vertexCount();
        int E = g.edgeCount();
        if (V == 0) return new Result(Collections.emptyList(), 0.0, V, E, 0, 0);

        // choose arbitrary start
        String start = g.vertices.iterator().next();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.w));

        visited.add(start);
        List<Edge> inc = g.adj.get(start);
        if (inc != null) pq.addAll(inc);

        List<Edge> mst = new ArrayList<>();
        double total = 0.0;
        long edgeChecks = 0;

        while (!pq.isEmpty() && mst.size() < V - 1) {
            Edge e = pq.poll();
            edgeChecks++;
            String u = e.u;
            String v = e.v;
            // find the vertex that is outside visited
            String next = null;
            if (visited.contains(u) && !visited.contains(v)) next = v;
            else if (visited.contains(v) && !visited.contains(u)) next = u;
            else continue; // both visited or both unvisited -> skip

            visited.add(next);
            mst.add(e);
            total += e.w;

            // add adjacent edges of newly added vertex
            List<Edge> neigh = g.adj.get(next);
            if (neigh != null) pq.addAll(neigh);
        }
        long t1 = System.nanoTime();
        return new Result(mst, total, V, E, edgeChecks, (t1 - t0) / 1_000_000);
    }
}
