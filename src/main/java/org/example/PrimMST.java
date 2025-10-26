package org.example;

import java.util.*;

public class PrimMST {

    public static class Result {
        public final List<Edge> mstEdges;
        public final double totalWeight;
        public final int V;
        public final int E;
        public final long edgeChecks;
        public final double timeMs;

        public Result(List<Edge> mstEdges, double totalWeight, int V, int E,
                      long edgeChecks, double timeMs) {
            this.mstEdges = mstEdges;
            this.totalWeight = totalWeight;
            this.V = V;
            this.E = E;
            this.edgeChecks = edgeChecks;
            this.timeMs = timeMs;
        }
    }

    public static Result run(Graph g) {
        long start = System.nanoTime();

        int V = g.vertexCount();
        int E = g.edgeCount();
        if (V == 0)
            return new Result(Collections.emptyList(), 0.0, V, E, 0, 0.0);

        String startVertex = g.vertices.iterator().next();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.w));

        visited.add(startVertex);
        List<Edge> initialEdges = g.adj.get(startVertex);
        if (initialEdges != null) pq.addAll(initialEdges);

        List<Edge> mst = new ArrayList<>();
        double total = 0.0;
        long edgeChecks = 0;

        while (!pq.isEmpty() && mst.size() < V - 1) {
            Edge e = pq.poll();
            edgeChecks++;

            String u = e.u;
            String v = e.v;
            String next = null;

            if (visited.contains(u) && !visited.contains(v)) next = v;
            else if (visited.contains(v) && !visited.contains(u)) next = u;
            else continue;

            visited.add(next);
            mst.add(e);
            total += e.w;

            List<Edge> neighbors = g.adj.get(next);
            if (neighbors != null) pq.addAll(neighbors);
        }

        long end = System.nanoTime();
        double timeMs = (end - start) / 1_000_000.0; // точное время

        return new Result(mst, total, V, E, edgeChecks, timeMs);
    }
}
